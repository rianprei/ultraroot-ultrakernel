package dev.superroot.ultraroot.theme

import androidx.compose.ui.graphics.Color

// Paleta da marca. Cada cor tem um papel M3 nomeado em UltraTheme.
// Contrastes verificados em ContrastTest: texto >= 4,5:1; contorno de componente >= 3:1.
internal object UltraPalette {
    // Superfícies (escala de containers do M3, do mais fundo ao mais alto).
    val Ink = Color(0xFF0B0C0E)            // background / surfaceDim
    val Lowest = Color(0xFF08090B)         // surfaceContainerLowest
    val Low = Color(0xFF101216)            // surfaceContainerLow
    val Panel = Color(0xFF14161A)          // surface / surfaceContainer
    val PanelHigh = Color(0xFF1C1F24)      // surfaceContainerHigh
    val PanelHighest = Color(0xFF24282E)   // surfaceContainerHighest
    val Variant = Color(0xFF2A2E35)        // surfaceVariant / surfaceBright

    // Primária: latão (ação principal e destaque de seleção).
    val Brass = Color(0xFFD6B25E)          // primary
    val BrassInk = Color(0xFF1A1406)       // onPrimary
    val BrassContainer = Color(0xFF3A2F12) // primaryContainer
    val BrassOnContainer = Color(0xFFF1DFA8)

    // Secundária: aço (texto de apoio e metadados).
    val Steel = Color(0xFF8AA4B5)          // secondary / onSurfaceVariant
    val SteelContainer = Color(0xFF1F2A33) // secondaryContainer
    val SteelOnContainer = Color(0xFFC9DCE6)

    // Terciária: azul-informação (estados informativos).
    val Azure = Color(0xFF9FC3D6)          // tertiary
    val AzureInk = Color(0xFF0B1820)       // onTertiary
    val AzureContainer = Color(0xFF1B2D3A) // tertiaryContainer
    val AzureOnContainer = Color(0xFFD4E8F2)

    // Texto e contornos.
    val Paper = Color(0xFFE8E6E3)          // onSurface / onBackground
    val Outline = Color(0xFF5C6670)        // outline (contorno de componente, 3:1)
    val OutlineVariant = Color(0xFF3A3F46) // outlineVariant (divisória decorativa)

    // Erro: valores do baseline M3 dark (tons 80, 20, 30 e 90), declarados aqui para teste.
    val Error = Color(0xFFFFB4AB)
    val OnError = Color(0xFF690005)
    val ErrorContainer = Color(0xFF93000A)
    val OnErrorContainer = Color(0xFFFFDAD6)
}
