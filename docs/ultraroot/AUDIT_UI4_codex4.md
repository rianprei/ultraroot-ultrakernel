## UI ui4 - nota 2/4
- Justificativa: faixa 2/4 (“needs work”): shell e hierarquia são coerentes, mas quatro rotas placeholder e a falta de recuperação do erro pedem acabamento de conteúdo (§2/4 em review-rubric.md:13,23; UltraRootApp.kt:84-88; RootAccessScreen.kt:53-58).
- Escopo visual: APK confirma versionCode 40004/versionName 0.2.0-ui4; render de ui4 = UNKNOWN porque a captura disponível é da ui3 (screen_ui3_root.png), e o APK não foi executado.

### 1. Quatro destinos sem conteúdo
- Antes: Modules, Toolkit, Boot e Extensions caem na mesma tela “Em construção” (Section.kt:30,38,54,62; UltraRootApp.kt:84-88; SectionPlaceholder.kt:24-28).
- Depois: tire esses itens da navegação até existir uma tela útil para cada um.
- Por quê: Nielsen heuristic #8 - Aesthetic and minimalist design; destinos vazios competem com as duas telas implementadas.

### 2. Erro sem ação de recuperação
- Antes: a leitura dispara uma vez em LaunchedEffect e Unavailable só mostra texto/hint; não há ação para repetir a leitura (RootAccessScreen.kt:38-40,53-58; KernelPerformanceScreen.kt:37-39,51-56; strings.xml:25).
- Depois: adicione “Tentar novamente” e faça o botão disparar a leitura outra vez.
- Por quê: Nielsen heuristic #9 - Help users recognize, diagnose, and recover from errors.

### 3. Idioma inconsistente
- Antes: o TopAppBar recebe títulos ingleses de Section.title enquanto os atalhos e as mensagens estão em português (UltraRootApp.kt:50-51; Section.kt:23-24,31-32; strings.xml:7-10).
- Depois: mova os títulos de Section.kt para strings.xml e use o mesmo idioma nos títulos, navegação e conteúdo.
- Por quê: Nielsen heuristic #4 - Consistency and standards.

### 4. Título promete dados que não aparecem
- Antes: “Kernel & Performance” nomeia a seção; a tela exibe versão do root e duas UAPIs, sem métrica de desempenho (Section.kt:47; KernelPerformanceScreen.kt:61-70,82-90).
- Depois: renomeie para “Kernel” até a tela apresentar dados de desempenho.
- Por quê: Nielsen heuristic #2 - Match between system and the real world.

### 5. Navegação não se adapta à largura
- Antes: Scaffold usa sempre NavigationBar no bottomBar, sem seleção por tamanho da janela (UltraRootApp.kt:47-59).
- Depois: use NavigationSuiteScaffold/WindowSizeClass para trocar por NavigationRail em larguras médias.
- Por quê: Material Design 3 - Adaptive navigation.

### 6. Lista de acessos repete cartões
- Antes: cada item da allowlist vira um Card full-width dentro da coluna (RootAccessScreen.kt:66-69,108-120).
- Depois: use ListItem com divisores e reserve Card para resumir ou agrupar dados.
- Por quê: Material Design 3 - List.

**Conformidade M3**: navegação inferior PASS no compacto, 5 itens (UltraRootApp.kt:58-74; Section.kt:22-53); tokens de cor PASS, papéis semânticos (UltraTheme.kt:9-40); tipografia PASS, escala base (Type.kt:11-26); formas PASS, escala 4/8/12/16/28 dp (Shape.kt:8-14); movimento PASS, tempos/easings M3 (Motion.kt:8-13; RootAccessScreen.kt:41-46), redução de movimento UNKNOWN sem execução; espaçamento em 8 dp PASS (UltraRootApp.kt:82,97,111; RootAccessScreen.kt:49,113; KernelPerformanceScreen.kt:48,81; SectionPlaceholder.kt:18; StatusCard.kt:49,53,56,59; LoadingState.kt:25,30; EmptyState.kt:17,23; KeyValueRow.kt:22; strokeWidth 3 dp não é espaçamento, LoadingState.kt:29).
**Conformidade M3** (continuação): estados PASS — carregando, indisponível, vazio e dados (RootAccessScreen.kt:51,53,59,66-69; RootAccessState.kt:24,26-27,29-42; KernelPerformanceScreen.kt:50-55,57-94); contraste PASS, pares usados ≥6,95:1 pela paleta (Color.kt:24,35-43; UltraTheme.kt:22-40); alvos 48 dp UNKNOWN quanto à medida renderizada, componentes M3 usados (UltraRootApp.kt:61-73,100-108).
- Adaptabilidade da navegação: FALHA conforme item 5 (UltraRootApp.kt:47-59).
- Verificação do APK: aapt2 dump badging → package me.weishu.kernelsu, versionCode 40004, versionName 0.2.0-ui4; sha256sum → 8da52a3a71fddaed3f330559948294f684f3fe5971be110808c24c733da485be.

**Fazer primeiro:** adicione uma ação real de “Tentar novamente” nos estados de backend indisponível (RootAccessScreen.kt:53-58; KernelPerformanceScreen.kt:51-56).
