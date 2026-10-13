package dev.superroot.ultraroot.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

// Movimento com os valores oficiais do M3 (MotionTokens do androidx.compose.material3).
// Durações em ms: short 50-200, medium 250-400. Usadas na troca de estado da tela.
internal object UltraMotion {
    const val ShortMs = 200          // DurationShort4
    const val MediumMs = 300         // DurationMedium2
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)           // EasingStandard
    val StandardDecelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f)   // EasingStandardDecelerate
    val StandardAccelerate: Easing = CubicBezierEasing(0.3f, 0f, 1f, 1f) // EasingStandardAccelerate
}
