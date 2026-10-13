package dev.superroot.ultraroot

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Seções (nomes da especificação). As primárias ficam na barra inferior (M3: 3 a 5 destinos
// em tela compacta); as demais ficam em "Mais". Seções sem implementação mostram "Em construção".
enum class Section(
    val title: String,
    val short: String,
    val index: String,
    val body: String,
    val icon: ImageVector,
    val primary: Boolean,
) {
    Root(
        title = "Root Access",
        short = "Root",
        index = "01",
        body = "Allowlist de root concedido pelo kernel. Conceder e revogar acesso chega num incremento futuro.",
        icon = Icons.Filled.Lock,
        primary = true,
    ),
    Modules(
        title = "Module Center",
        short = "Módulos",
        index = "02",
        body = "Módulos de root instalados e seus estados. Disponível em um incremento futuro.",
        icon = Icons.Filled.Build,
        primary = true,
    ),
    Toolkit(
        title = "System Toolkit",
        short = "Sistema",
        index = "03",
        body = "Ferramentas de sistema: processos, propriedades e SELinux. Disponível em um incremento futuro.",
        icon = Icons.Filled.Settings,
        primary = true,
    ),
    Kernel(
        title = "Kernel & Performance",
        short = "Kernel",
        index = "04",
        body = "",
        icon = Icons.Filled.Refresh,
        primary = true,
    ),
    Boot(
        title = "Boot & Recovery",
        short = "Boot",
        index = "05",
        body = "Leitura do slot ativo e das imagens de boot. Disponível em um incremento futuro.",
        icon = Icons.Filled.Home,
        primary = false,
    ),
    Extensions(
        title = "Extensions Hub",
        short = "Extensões",
        index = "06",
        body = "Extensões e integrações com os backends. Disponível em um incremento futuro.",
        icon = Icons.Filled.Add,
        primary = false,
    ),
}
