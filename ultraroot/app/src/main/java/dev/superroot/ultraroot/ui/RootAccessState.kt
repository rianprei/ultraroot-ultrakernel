package dev.superroot.ultraroot.ui

import dev.superroot.core.AllowEntry
import dev.superroot.core.KsuRead

const val UNKNOWN_PACKAGE = "—"

data class RootRow(
    val uid: Int,
    val packageName: String,
    val allowSu: Boolean?,
)

enum class RootFilter { ALL, GRANTED, NOT_GRANTED }

sealed class RootAccessUi {
    data class Unavailable(val message: String) : RootAccessUi()
    data object Empty : RootAccessUi()
    // grantedTotal conta a allowlist inteira, não só as linhas que passaram no filtro.
    data class Listed(val rows: List<RootRow>, val grantedTotal: Int) : RootAccessUi()
    data class NoMatch(val query: String, val filter: RootFilter) : RootAccessUi()
}

fun presentRootAccess(
    read: KsuRead<List<AllowEntry>>,
    query: String = "",
    filter: RootFilter = RootFilter.ALL,
): RootAccessUi {
    return when (read) {
        is KsuRead.Unavailable -> RootAccessUi.Unavailable(read.reason)
        is KsuRead.Ok -> {
            if (read.value.isEmpty()) {
                RootAccessUi.Empty
            } else {
                val all = read.value.map { entry ->
                    when (entry) {
                        is AllowEntry.Unavailable -> RootRow(
                            uid = entry.uid,
                            packageName = UNKNOWN_PACKAGE,
                            allowSu = null,
                        )
                        is AllowEntry.Confirmed -> RootRow(
                            uid = entry.grant.uid,
                            packageName = entry.grant.name.ifEmpty { UNKNOWN_PACKAGE },
                            allowSu = entry.grant.allowSu,
                        )
                    }
                }
                val needle = query.trim()
                val rows = all.filter { it.inFilter(filter) && it.matches(needle) }
                if (rows.isEmpty()) {
                    RootAccessUi.NoMatch(query = needle, filter = filter)
                } else {
                    RootAccessUi.Listed(rows, grantedTotal = all.count { it.allowSu == true })
                }
            }
        }
    }
}

// Concedido = allowSu verdadeiro. Perfil indisponível (null) só aparece no filtro ALL.
private fun RootRow.inFilter(filter: RootFilter): Boolean = when (filter) {
    RootFilter.ALL -> true
    RootFilter.GRANTED -> allowSu == true
    RootFilter.NOT_GRANTED -> allowSu == false
}

// Busca sem diferenciar maiúsculas no nome do pacote; o UID casa por substring dos dígitos.
private fun RootRow.matches(needle: String): Boolean =
    needle.isEmpty() ||
        packageName.contains(needle, ignoreCase = true) ||
        uid.toString().contains(needle)
