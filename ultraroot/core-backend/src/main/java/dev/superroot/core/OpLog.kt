package dev.superroot.core

import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * UR-LOG: uma linha por operação, sem segredo.
 * Fonte: docs/ultraroot/TODOs.md, seção UR-LOG (nunca gravar SuperKey, senha de keystore ou bytes de chave
 * privada; teto em bytes UNKNOWN até medir).
 * Material de chave privada recusa a linha inteira; senha em "chave=valor" ou em flag de linha de comando
 * tem o valor trocado por REDACTED.
 */
data class OpLogEntry(val timeUtc: String, val operation: String, val result: String, val detail: String)

sealed interface OpLogLine {
    data class Line(val text: String) : OpLogLine
    data class Refused(val reason: String) : OpLogLine
}

object OpLog {
    /** Teto em bytes: UNKNOWN até medir no aparelho. Sem valor inventado. */
    val MAX_BYTES: Long? = null
    const val REDACTED = "[REDIGIDO]"

    private val PRIVATE_KEY = Regex("-----BEGIN [A-Z0-9 ]*PRIVATE KEY-----")
    private val KEY_VALUE = Regex(
        "(?i)\\b(password|passwd|senha|storepass|keypass|superkey|secret|token|pass)(\\s*[=:]\\s*)\\S+",
    )
    private val CLI_FLAG = Regex("(?i)(--?(?:storepass|keypass|ks-pass|key-pass|password))(\\s+)\\S+")

    private fun redact(s: String): String =
        CLI_FLAG.replace(KEY_VALUE.replace(s) { "${it.groupValues[1]}${it.groupValues[2]}$REDACTED" }) {
            "${it.groupValues[1]}${it.groupValues[2]}$REDACTED"
        }

    fun format(entry: OpLogEntry): OpLogLine {
        val fields = listOf(entry.timeUtc, entry.operation, entry.result, entry.detail)
        if (fields.any { PRIVATE_KEY.containsMatchIn(it) }) return OpLogLine.Refused("PRIVATE_KEY_MATERIAL")
        if (fields.any { it.contains('\n') || it.contains('\r') }) return OpLogLine.Refused("MULTILINE")
        if (fields.any { it.contains('\t') }) return OpLogLine.Refused("TAB")
        try {
            Instant.parse(entry.timeUtc)
        } catch (_: DateTimeParseException) {
            return OpLogLine.Refused("BAD_TIME")
        }
        if (entry.operation.isBlank()) return OpLogLine.Refused("EMPTY_OPERATION")
        return OpLogLine.Line(listOf(entry.timeUtc, redact(entry.operation), redact(entry.result), redact(entry.detail)).joinToString("\t"))
    }

    /** Acrescenta uma linha; entrada recusada não muda o log. Retenção: até o usuário limpar (sem corte aqui). */
    fun append(log: List<String>, entry: OpLogEntry): List<String> =
        when (val l = format(entry)) {
            is OpLogLine.Line -> log + l.text
            is OpLogLine.Refused -> log
        }
}
