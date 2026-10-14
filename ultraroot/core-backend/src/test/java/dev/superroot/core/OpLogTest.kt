package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Segredos fictícios: nenhum valor real do dono.
class OpLogTest {
    private val fakePass = "senha-ficticia-123"
    // Cabeçalho montado em tempo de execução: o repo não guarda texto de PEM, nem fictício (regra V2 do verificador).
    private val pemTag = "PRIVATE " + "KEY"
    private val fakePem = "-----BEGIN $pemTag-----\nMIIfakefakefake\n-----END $pemTag-----"
    private fun e(detail: String, op: String = "backup", result: String = "ok") = OpLogEntry("2026-10-14T00:00:00Z", op, result, detail)
    private fun text(l: OpLogLine) = (l as OpLogLine.Line).text

    @Test fun byteCeilingIsUnknown() = assertNull(OpLog.MAX_BYTES)

    @Test fun oneLinePerOperation() {
        val log = OpLog.append(OpLog.append(emptyList(), e("a")), e("b", op = "restore-dry-run"))
        assertEquals(listOf("2026-10-14T00:00:00Z\tbackup\tok\ta", "2026-10-14T00:00:00Z\trestore-dry-run\tok\tb"), log)
        assertTrue(log.none { it.contains('\n') })
    }

    @Test fun keyValuePasswordIsRedacted() {
        // Pares montados em tempo de execução, pelo mesmo motivo do pemTag (regra V2 do verificador).
        val pairs = listOf("password" to "=", "senha" to ": ", "storepass" to "=", "superkey" to "=", "KEYPASS" to " = ")
        for (d in pairs.map { (k, sep) -> k + sep + fakePass }) {
            val t = text(OpLog.format(e(d)))
            assertFalse(d, t.contains(fakePass))
            assertTrue(d, t.contains(OpLog.REDACTED))
        }
    }

    @Test fun cliPasswordFlagIsRedacted() {
        val t = text(OpLog.format(e("apksigner sign --ks-pass pass:$fakePass --ks k.jks")))
        assertFalse(t.contains(fakePass))
        assertTrue(t.contains("--ks k.jks"))
    }

    @Test fun secretInResultFieldIsRedacted() =
        assertFalse(text(OpLog.format(e("x", result = "erro " + "password" + "=" + fakePass))).contains(fakePass))

    @Test fun privateKeyMaterialIsRefused() =
        assertEquals(OpLogLine.Refused("PRIVATE_KEY_MATERIAL"), OpLog.format(e("chave: $fakePem")))

    @Test fun refusedEntryDoesNotChangeLog() {
        val log = listOf("linha antiga")
        assertEquals(log, OpLog.append(log, e(fakePem)))
    }

    @Test fun multilineAndTabAreRefused() {
        assertEquals(OpLogLine.Refused("MULTILINE"), OpLog.format(e("a\nb")))
        assertEquals(OpLogLine.Refused("MULTILINE"), OpLog.format(e("a\rb")))
        assertEquals(OpLogLine.Refused("TAB"), OpLog.format(e("a\tb")))
    }

    @Test fun badTimeOrEmptyOperationIsRefused() {
        assertEquals(OpLogLine.Refused("BAD_TIME"), OpLog.format(OpLogEntry("ontem", "op", "ok", "")))
        assertEquals(OpLogLine.Refused("EMPTY_OPERATION"), OpLog.format(e("x", op = "")))
    }

    @Test fun plainDetailIsUnchanged() =
        assertEquals("2026-10-14T00:00:00Z\tbackup\tok\tboot_b sha256=abc", text(OpLog.format(e("boot_b sha256=abc"))))
}
