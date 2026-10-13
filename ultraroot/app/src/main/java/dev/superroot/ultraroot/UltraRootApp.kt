package dev.superroot.ultraroot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.superroot.ultraroot.theme.UltraTheme
import dev.superroot.ultraroot.ui.KernelPerformanceScreen
import dev.superroot.ultraroot.ui.RootAccessScreen
import dev.superroot.ultraroot.ui.SectionPlaceholder

// Navegação inferior, na área acima dos botões do Android (WindowInsets tratados pelo NavigationBar).
// Quatro seções primárias e "Mais" para as demais: 5 destinos, dentro do limite do M3 para barra inferior.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltraRootApp() {
    UltraTheme {
        var currentName by rememberSaveable { mutableStateOf(Section.Root.name) }
        var moreOpen by rememberSaveable { mutableStateOf(false) }
        val current = Section.valueOf(currentName)
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(current.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Section.entries.filter { it.primary }.forEach { section ->
                        NavigationBarItem(
                            selected = section == current,
                            onClick = { currentName = section.name },
                            icon = { Icon(section.icon, contentDescription = null) },
                            label = { Text(section.short) },
                        )
                    }
                    NavigationBarItem(
                        selected = !current.primary,
                        onClick = { moreOpen = true },
                        icon = { Icon(Icons.Filled.MoreVert, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_more)) },
                    )
                }
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                when (current) {
                    Section.Kernel -> KernelPerformanceScreen()
                    Section.Root -> RootAccessScreen()
                    else -> SectionPlaceholder(current)
                }
            }
        }
        if (moreOpen) {
            ModalBottomSheet(onDismissRequest = { moreOpen = false }) {
                Text(
                    text = stringResource(R.string.nav_more_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
                Section.entries.filter { !it.primary }.forEach { section ->
                    ListItem(
                        headlineContent = { Text(section.title) },
                        leadingContent = { Icon(section.icon, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentName = section.name
                                moreOpen = false
                            },
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
