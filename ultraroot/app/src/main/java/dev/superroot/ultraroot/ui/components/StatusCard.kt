package dev.superroot.ultraroot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Estado real exibido com cor, ícone E texto. A cor nunca é o único indicador.
enum class StatusTone { Info, Attention, Error, Success }

@Composable
fun StatusCard(
    tone: StatusTone,
    title: String,
    body: String,
    hint: String? = null,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val (container, content, icon) = when (tone) {
        StatusTone.Info -> Triple(cs.tertiaryContainer, cs.onTertiaryContainer, Icons.Filled.Info)
        StatusTone.Attention -> Triple(cs.primaryContainer, cs.onPrimaryContainer, Icons.Filled.Warning)
        StatusTone.Error -> Triple(cs.errorContainer, cs.onErrorContainer, Icons.Filled.Close)
        StatusTone.Success -> Triple(cs.secondaryContainer, cs.onSecondaryContainer, Icons.Filled.Check)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = content)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = content)
                Spacer(Modifier.height(8.dp))
                Text(text = body, style = MaterialTheme.typography.bodyMedium, color = content)
                if (hint != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = hint, style = MaterialTheme.typography.labelMedium, color = content)
                }
                if (action != null) {
                    Spacer(Modifier.height(16.dp))
                    action()
                }
            }
        }
    }
}
