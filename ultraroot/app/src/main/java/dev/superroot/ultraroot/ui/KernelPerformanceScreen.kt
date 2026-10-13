package dev.superroot.ultraroot.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.superroot.core.KsuBackend
import dev.superroot.core.KsuCapability
import dev.superroot.core.KsuRead
import dev.superroot.ultraroot.R
import dev.superroot.ultraroot.theme.UltraMotion
import dev.superroot.ultraroot.ui.components.KeyValueRow
import dev.superroot.ultraroot.ui.components.LoadingState
import dev.superroot.ultraroot.ui.components.RetryButton
import dev.superroot.ultraroot.ui.components.StatusCard
import dev.superroot.ultraroot.ui.components.StatusTone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun KernelPerformanceScreen() {
    var read by remember { mutableStateOf<KsuRead<KsuCapability>?>(null) }
    var attempt by remember { mutableStateOf(0) }
    LaunchedEffect(attempt) {
        read = null
        read = withContext(Dispatchers.IO) { KsuBackend.readCapability() }
    }
    Crossfade(
        targetState = read,
        animationSpec = tween(
            durationMillis = UltraMotion.MediumMs,
            easing = UltraMotion.StandardDecelerate,
        ),
        label = "kernel-performance",
    ) { value ->
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (value) {
                null -> LoadingState(stringResource(R.string.kp_waiting))
                is KsuRead.Unavailable -> StatusCard(
                    tone = StatusTone.Error,
                    title = stringResource(R.string.kp_unavailable_title),
                    body = value.reason,
                    hint = stringResource(R.string.kp_unavailable_hint),
                    action = { RetryButton(onRetry = { attempt += 1 }) },
                )
                is KsuRead.Ok -> {
                    val cap = value.value
                    val cs = MaterialTheme.colorScheme
                    // Ponto focal: a versão do root no kernel, em destaque.
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = cap.kernelVersion.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            color = cs.primary,
                        )
                        Text(
                            text = stringResource(R.string.kp_summary_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = cs.onSurfaceVariant,
                        )
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = cs.surfaceContainer,
                            contentColor = cs.onSurface,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            KeyValueRow(
                                label = stringResource(R.string.kp_label_kernel_uapi),
                                value = cap.kernelUapi.toString(),
                                technical = true,
                            )
                            KeyValueRow(
                                label = stringResource(R.string.kp_label_app_uapi),
                                value = cap.managerUapi.toString(),
                                technical = true,
                            )
                        }
                    }
                }
            }
        }
    }
}
