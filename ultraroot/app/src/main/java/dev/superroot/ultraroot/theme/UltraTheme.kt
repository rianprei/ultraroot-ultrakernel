package dev.superroot.ultraroot.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Esquema escuro com todos os papéis de cor usados pelo app. Papéis de erro (error,
// errorContainer, onError, onErrorContainer) ficam com os valores padrão do M3.
private val UltraDark = darkColorScheme(
    primary = UltraPalette.Brass,
    onPrimary = UltraPalette.BrassInk,
    primaryContainer = UltraPalette.BrassContainer,
    onPrimaryContainer = UltraPalette.BrassOnContainer,
    secondary = UltraPalette.Steel,
    onSecondary = UltraPalette.Ink,
    secondaryContainer = UltraPalette.SteelContainer,
    onSecondaryContainer = UltraPalette.SteelOnContainer,
    tertiary = UltraPalette.Azure,
    onTertiary = UltraPalette.AzureInk,
    tertiaryContainer = UltraPalette.AzureContainer,
    onTertiaryContainer = UltraPalette.AzureOnContainer,
    background = UltraPalette.Ink,
    onBackground = UltraPalette.Paper,
    surface = UltraPalette.Panel,
    onSurface = UltraPalette.Paper,
    surfaceVariant = UltraPalette.Variant,
    onSurfaceVariant = UltraPalette.Steel,
    surfaceDim = UltraPalette.Ink,
    surfaceBright = UltraPalette.Variant,
    surfaceContainerLowest = UltraPalette.Lowest,
    surfaceContainerLow = UltraPalette.Low,
    surfaceContainer = UltraPalette.Panel,
    surfaceContainerHigh = UltraPalette.PanelHigh,
    surfaceContainerHighest = UltraPalette.PanelHighest,
    error = UltraPalette.Error,
    onError = UltraPalette.OnError,
    errorContainer = UltraPalette.ErrorContainer,
    onErrorContainer = UltraPalette.OnErrorContainer,
    outline = UltraPalette.Outline,
    outlineVariant = UltraPalette.OutlineVariant,
    inverseSurface = UltraPalette.Paper,
    inverseOnSurface = UltraPalette.Ink,
    inversePrimary = UltraPalette.BrassContainer,
)

@Composable
fun UltraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UltraDark,
        typography = UltraTypography,
        shapes = UltraShapes,
        content = content,
    )
}
