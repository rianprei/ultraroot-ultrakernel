package dev.superroot.ultraroot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.superroot.ultraroot.R
import dev.superroot.ultraroot.Section
import dev.superroot.ultraroot.ui.components.StatusCard
import dev.superroot.ultraroot.ui.components.StatusTone

// Seção ainda não implementada: diz isso com um cartão de status, sem texto de desenvolvedor.
@Composable
fun SectionPlaceholder(section: Section) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = section.index,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        StatusCard(
            tone = StatusTone.Info,
            title = stringResource(R.string.placeholder_title),
            body = section.body,
        )
    }
}
