package dev.superroot.ultraroot.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

// Verifica os contrastes WCAG 2.x das combinações usadas pelo tema.
// Texto: mínimo 4,5:1. Contorno de componente e ícone de estado: mínimo 3:1.
class ContrastTest {

    private fun ratio(a: Color, b: Color): Double {
        val la = a.luminance().toDouble()
        val lb = b.luminance().toDouble()
        val hi = maxOf(la, lb)
        val lo = minOf(la, lb)
        return (hi + 0.05) / (lo + 0.05)
    }

    private val textPairs = listOf(
        "onBackground/background" to (UltraPalette.Paper to UltraPalette.Ink),
        "onSurface/surface" to (UltraPalette.Paper to UltraPalette.Panel),
        "onSurface/surfaceContainerHighest" to (UltraPalette.Paper to UltraPalette.PanelHighest),
        "onSurfaceVariant/surface" to (UltraPalette.Steel to UltraPalette.Panel),
        "onSurfaceVariant/surfaceContainerHigh" to (UltraPalette.Steel to UltraPalette.PanelHigh),
        "onSurfaceVariant/surfaceVariant" to (UltraPalette.Steel to UltraPalette.Variant),
        "onPrimary/primary" to (UltraPalette.BrassInk to UltraPalette.Brass),
        "primary/surface" to (UltraPalette.Brass to UltraPalette.Panel),
        "onPrimaryContainer/primaryContainer" to (UltraPalette.BrassOnContainer to UltraPalette.BrassContainer),
        "onSecondaryContainer/secondaryContainer" to (UltraPalette.SteelOnContainer to UltraPalette.SteelContainer),
        "onSurfaceVariant/background" to (UltraPalette.Steel to UltraPalette.Ink),
        "primary/background" to (UltraPalette.Brass to UltraPalette.Ink),
        "onErrorContainer/errorContainer" to (UltraPalette.OnErrorContainer to UltraPalette.ErrorContainer),
        "onTertiary/tertiary" to (UltraPalette.AzureInk to UltraPalette.Azure),
        "onTertiaryContainer/tertiaryContainer" to (UltraPalette.AzureOnContainer to UltraPalette.AzureContainer),
    )

    @Test
    fun everyTextPairReachesAA() {
        textPairs.forEach { (name, pair) ->
            val r = ratio(pair.first, pair.second)
            assertTrue("$name = %.2f (mínimo 4,5)".format(r), r >= 4.5)
        }
    }

    @Test
    fun componentOutlineReachesThreeToOne() {
        val r = ratio(UltraPalette.Outline, UltraPalette.Panel)
        assertTrue("outline/surface = %.2f (mínimo 3,0)".format(r), r >= 3.0)
    }
}
