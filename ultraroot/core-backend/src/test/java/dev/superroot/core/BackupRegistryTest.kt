package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Fixture sintética: bytes gerados no teste, nenhum backup real.
class BackupRegistryTest {
    private val img = "ANDROID!".toByteArray() + ByteArray(4088) { (it % 251).toByte() }
    private val hash = BackupRegistry.sha256Hex(img)
    private val rec = BackupRecord("acervo/boot_b.img", hash, Slot.B, "2026-10-05T12:00:00Z")

    @Test fun sha256OfEmptyIsKnownVector() =
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", BackupRegistry.sha256Hex(ByteArray(0)))

    @Test fun formatParseRoundTrip() {
        val line = BackupRegistry.format(rec)
        assertEquals("acervo/boot_b.img\t$hash\tb\t2026-10-05T12:00:00Z", line)
        assertEquals(rec, BackupRegistry.parse(line))
    }

    @Test fun slotParse() {
        assertEquals(Slot.A, Slot.parse("_a")); assertEquals(Slot.B, Slot.parse("B")); assertNull(Slot.parse("c"))
    }

    @Test fun parseRejectsMalformed() {
        assertNull(BackupRegistry.parse("acervo/boot_b.img\t$hash\tb"))                       // campo faltando
        assertNull(BackupRegistry.parse("acervo/boot_b.img\t${hash.dropLast(1)}\tb\t2026-10-05T12:00:00Z")) // hash curto
        assertNull(BackupRegistry.parse("acervo/boot_b.img\t${hash.uppercase()}\tb\t2026-10-05T12:00:00Z")) // hash fora do formato
        assertNull(BackupRegistry.parse("acervo/boot_b.img\t$hash\tc\t2026-10-05T12:00:00Z"))  // slot inválido
        assertNull(BackupRegistry.parse("acervo/boot_b.img\t$hash\tb\tontem"))                 // data inválida
        assertNull(BackupRegistry.parse("\t$hash\tb\t2026-10-05T12:00:00Z"))                   // caminho vazio
    }

    @Test fun parseAllRefusesWholeFileWithOneBadLine() {
        val good = BackupRegistry.format(rec)
        assertEquals(listOf(rec, rec), BackupRegistry.parseAll("$good\n$good\n"))
        assertNull(BackupRegistry.parseAll("$good\nlixo\n"))
    }

    @Test fun validateOk() = assertEquals(BackupCheck.Ok, BackupRegistry.validateBeforeRisk(rec, hash, "lake", Slot.B))

    @Test fun swappedHashIsRefused() {
        val swapped = BackupRegistry.sha256Hex(img.copyOf().also { it[100] = (it[100] + 1).toByte() })
        assertEquals(BackupCheck.Refused(BackupRefusal.HASH_MISMATCH), BackupRegistry.validateBeforeRisk(rec, swapped, "lake", Slot.B))
    }

    @Test fun missingFileIsRefused() =
        assertEquals(BackupCheck.Refused(BackupRefusal.FILE_MISSING), BackupRegistry.validateBeforeRisk(rec, null, "lake", Slot.B))

    @Test fun slotAOnLakeIsRefusedEvenWithGoodHash() {
        val recA = rec.copy(sourceSlot = Slot.A)
        assertEquals(BackupCheck.Refused(BackupRefusal.SLOT_A_FORBIDDEN_ON_LAKE), BackupRegistry.validateBeforeRisk(recA, hash, "lake", Slot.A))
    }

    @Test fun slotMismatchIsRefused() =
        assertEquals(BackupCheck.Refused(BackupRefusal.SLOT_MISMATCH), BackupRegistry.validateBeforeRisk(rec, hash, "outro", Slot.A))

    @Test fun dryRunMatchesHashAndWritesNothing() {
        val reads = mutableListOf<String>()
        val r = BackupRegistry.dryRunRestore(rec, { reads += it; img }, "lake", Slot.B)
        assertEquals(RestoreDryRun(BackupCheck.Ok, "boot_b"), r)
        assertEquals(listOf("acervo/boot_b.img"), reads)
    }

    @Test fun dryRunWithSwappedBytesIsRefused() {
        val bad = img.copyOf().also { it[0] = 'X'.code.toByte() }
        assertEquals(RestoreDryRun(BackupCheck.Refused(BackupRefusal.HASH_MISMATCH), null), BackupRegistry.dryRunRestore(rec, { bad }, "lake", Slot.B))
    }

    @Test fun dryRunMissingFileIsRefused() =
        assertEquals(RestoreDryRun(BackupCheck.Refused(BackupRefusal.FILE_MISSING), null), BackupRegistry.dryRunRestore(rec, { null }, "lake", Slot.B))
}
