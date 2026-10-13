package dev.superroot.ultraroot.ui.components

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.superroot.ultraroot.R

// Ação de recuperação de estado de erro: repete a leitura do backend (Nielsen #9).
@Composable
fun RetryButton(onRetry: () -> Unit) {
    FilledTonalButton(onClick = onRetry) {
        Text(stringResource(R.string.action_retry))
    }
}
