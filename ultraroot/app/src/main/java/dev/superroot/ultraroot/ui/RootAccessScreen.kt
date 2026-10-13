package dev.superroot.ultraroot.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.superroot.core.AllowEntry
import dev.superroot.core.KsuBackend
import dev.superroot.core.KsuRead
import dev.superroot.ultraroot.R
import dev.superroot.ultraroot.theme.UltraMotion
import dev.superroot.ultraroot.ui.components.EmptyState
import dev.superroot.ultraroot.ui.components.LoadingState
import dev.superroot.ultraroot.ui.components.RetryButton
import dev.superroot.ultraroot.ui.components.StatusCard
import dev.superroot.ultraroot.ui.components.StatusTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RootAccessScreen() {
    var read by remember { mutableStateOf<KsuRead<List<AllowEntry>>?>(null) }
    var attempt by remember { mutableStateOf(0) }
    LaunchedEffect(attempt) {
        read = null
        read = withContext(Dispatchers.IO) { KsuBackend.readAllowList() }
    }
    Crossfade(
        targetState = read,
        animationSpec = tween(
            durationMillis = UltraMotion.MediumMs,
            easing = UltraMotion.StandardDecelerate,
        ),
        label = "root-access",
    ) { value ->
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (value) {
                null -> LoadingState(stringResource(R.string.root_loading))
                else -> when (val ui = presentRootAccess(value)) {
                    is RootAccessUi.Unavailable -> StatusCard(
                        tone = StatusTone.Error,
                        title = stringResource(R.string.root_unavailable_title),
                        body = ui.message,
                        hint = stringResource(R.string.root_unavailable_hint),
                        action = { RetryButton(onRetry = { attempt += 1 }) },
                    )
                    RootAccessUi.Empty -> {
                        AccessSummary(granted = 0)
                        EmptyState(
                            title = stringResource(R.string.root_empty_title),
                            body = stringResource(R.string.root_empty_body),
                        )
                    }
                    is RootAccessUi.Listed -> {
                        AccessSummary(granted = ui.grantedTotal)
                        AccessList(ui.rows)
                    }
                    is RootAccessUi.NoMatch -> EmptyState(
                        title = stringResource(R.string.root_nomatch_title),
                        body = stringResource(R.string.root_nomatch_body),
                    )
                }
            }
        }
    }
}

// Ponto focal da tela: quantos apps têm root concedido de fato (allowSu verdadeiro).
// Perfil indisponível não conta como concedido.
@Composable
private fun AccessSummary(granted: Int) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = granted.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = cs.onSurface,
        )
        Text(
            text = stringResource(R.string.root_summary_label),
            style = MaterialTheme.typography.labelLarge,
            color = cs.onSurfaceVariant,
        )
    }
}

// Lista agrupada num único cartão, com divisores (M3 - List). Cartão só agrupa; a linha é ListItem.
@Composable
private fun AccessList(rows: List<RootRow>) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer, contentColor = cs.onSurface),
    ) {
        rows.forEachIndexed { index, row ->
            AccessRow(row)
            if (index < rows.lastIndex) HorizontalDivider(color = cs.outlineVariant)
        }
    }
}

@Composable
private fun AccessRow(row: RootRow) {
    val name = if (row.packageName == UNKNOWN_PACKAGE) {
        stringResource(R.string.root_unknown_package)
    } else {
        row.packageName
    }
    val grant = when (row.allowSu) {
        null -> stringResource(R.string.root_grant_profile_unavailable)
        true -> stringResource(R.string.root_grant_allowed)
        false -> stringResource(R.string.root_grant_denied)
    }
    ListItem(
        headlineContent = { Text(name) },
        supportingContent = { Text("${stringResource(R.string.root_label_uid)} ${row.uid} · $grant") },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
