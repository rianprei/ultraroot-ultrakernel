package dev.superroot.core

import java.security.MessageDigest
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * UR-BACKUP: registro do acervo de backup (caminho, sha256, slot de origem, data), validação antes de
 * operação de risco e dry-run de restauração por hash. Nada aqui grava partição.
 * Fonte: docs/ultraroot/TODOs.md, seção UR-BACKUP; regra do lake "nunca slot A" (AGENTS.md).
 */
enum class Slot {
    A, B;

    companion object {
        fun parse(raw: String): Slot? = when (raw.trim().removePrefix("_").lowercase()) {
            "a" -> A
            "b" -> B
            else -> null
        }
    }
}

data class BackupRecord(val path: String, val sha256: String, val sourceSlot: Slot, val createdAt: String)

enum class BackupRefusal { FILE_MISSING, HASH_MISMATCH, SLOT_MISMATCH, SLOT_A_FORBIDDEN_ON_LAKE }

sealed interface BackupCheck {
    data object Ok : BackupCheck
    data class Refused(val reason: BackupRefusal) : BackupCheck
}

/** wouldWritePartition: só o nome da partição que seria restaurada; nada é escrito. */
data class RestoreDryRun(val check: BackupCheck, val wouldWritePartition: String?)

object BackupRegistry {
    private val HEX64 = Regex("^[0-9a-f]{64}$")

    /** Uma linha: caminho, sha256, slot e data ISO-8601 (UTC), separados por tab. */
    fun format(record: BackupRecord): String =
        listOf(record.path, record.sha256, record.sourceSlot.name.lowercase(), record.createdAt).joinToString("\t")

    fun parse(line: String): BackupRecord? {
        val f = line.split('\t')
        if (f.size != 4) return null
        val (path, sha, slotRaw, date) = f
        if (path.isEmpty() || path.any { it == '\n' || it == '\r' }) return null
        if (!HEX64.matches(sha)) return null
        val slot = Slot.parse(slotRaw) ?: return null
        try {
            Instant.parse(date)
        } catch (_: DateTimeParseException) {
            return null
        }
        return BackupRecord(path, sha, slot, date)
    }

    /** null se qualquer linha não vazia estiver malformada: o acervo inteiro é recusado. */
    fun parseAll(text: String): List<BackupRecord>? =
        text.lines().filter { it.isNotEmpty() }.map { parse(it) ?: return null }

    fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    fun validateBeforeRisk(record: BackupRecord, actualSha256: String?, device: String, targetSlot: Slot): BackupCheck {
        if (device == "lake" && targetSlot == Slot.A) return BackupCheck.Refused(BackupRefusal.SLOT_A_FORBIDDEN_ON_LAKE)
        if (record.sourceSlot != targetSlot) return BackupCheck.Refused(BackupRefusal.SLOT_MISMATCH)
        if (actualSha256 == null) return BackupCheck.Refused(BackupRefusal.FILE_MISSING)
        if (actualSha256.lowercase() != record.sha256) return BackupCheck.Refused(BackupRefusal.HASH_MISMATCH)
        return BackupCheck.Ok
    }

    /** Lê o arquivo do backup por readFile (sem escrita), confere o hash e diz qual partição seria restaurada. */
    fun dryRunRestore(record: BackupRecord, readFile: (String) -> ByteArray?, device: String, targetSlot: Slot): RestoreDryRun {
        val actual = readFile(record.path)?.let { sha256Hex(it) }
        val check = validateBeforeRisk(record, actual, device, targetSlot)
        return RestoreDryRun(check, if (check == BackupCheck.Ok) "boot_${targetSlot.name.lowercase()}" else null)
    }
}
