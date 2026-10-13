# Revisão cruzada: MAGISK_KSU_UI_INVENTORY.md

ID: REV-INV-MKSU-01 · Revisor: Haiku 5.5 · Autor do inventário: Codex #4 · Data: 2026-10-12
Veredito: APROVADO COM CORREÇÕES
Status das correções (2026-10-12): rodada 1 aplicou as correções 1 a 10 e 12, conferidas nas fontes. A rodada 2 completou a correção 11 (subseção "Cobertura complementar") e as pendências de formato (caminho na rec 12; linha em branco antes da §3). O inventário ficou com 120 linhas.

> Procedência: o revisor não gravou este arquivo. Ele informou que a regra do harness proíbe relatórios `.md` sem pedido explícito. O conteúdo foi transcrito da mensagem do revisor, a pedido do dono ("arquivo de relatório .md", 2026-10-12). A conferência do orquestrador está na seção 11.

## 1. Existência (T1)

OK 152 · MISSING 0 · OUT_OF_RANGE 0. Divergência em relação à referência 152/152: nenhuma.

## 2. Conteúdo §4 e §5 (T2)

19 linhas: OK 14 · DESLOCADA 0 · ERRADA 3 (linhas 62, 65, 71) · NÃO VERIFICADO 2 (linhas 53, 63).

| Linha | Veredito | Trecho literal ou prova |
|---|---|---|
| 50 | OK | `RootAccessScreen.kt:124` `when (row.allowSu) {`; `:129` `ListItem(` sem onClick |
| 53 | NÃO VERIFICADO (compatibilidade) | versão `KernelPerformanceScreen.kt:67` `cap.kernelVersion.toString()`; UAPI `:87`, `:92`; "sem flash": grep em U = 0 ocorrências |
| 54 | OK | `UltraRootApp.kt:84` `when (current) {`; grep Settings/About/Log em U só acha ícones (`Section.kt:9`, `:43`) |
| 62 | ERRADA | "quais flags mudam antes de salvar": `RootProfileConfigMaterial.kt:52` `onUidChange = { onProfileChange(profile.copy(uid = it, rootUseDefault = false)) },` aplica na hora; sem save/confirm/diff em 48-112 |
| 63 | NÃO VERIFICADO (motivo/resultado) | filtro OK em `SulogScreen.kt:66` `fun sulogFilterLabel(`; sem reason/result/uid/command em `SulogMaterial.kt:82-293` |
| 65 | ERRADA (confirmação); NÃO VERIFICADO (progresso) | `ModuleMaterial.kt:359-397` sem confirm/Dialog; resultado OK em `FlashMaterial.kt:75` `FlashingStatus.SUCCESS -> R.string.flash_success`; sem token 'progress' em 49-139 |
| 71 | ERRADA (logging) | Magisk 395-518 tem notificação (`SettingsScreen.kt:490`) e restrict (`:508`), sem log; logging está em `SuperuserDetailScreen.kt:196` `onCheckedChange = { viewModel.updateLogging(item) }` |
| 60, 61, 64, 66-70, 72, 73 | OK (com ressalvas) | `SuperUserMaterial.kt:124` `Icons.AutoMirrored.Filled.Sort` (UID NV em 60); `SuperuserDetailScreen.kt:200` `RevokeButton(`; `AppProfileMaterial.kt:236` `onCheckedChange = { onProfileChange(profile.copy(allowSu = it)) }`; `ModuleMaterial.kt:825` `!module.remove && module.enabled`; `:510` `actions.onUndoUninstallModule(module)` (resumo e reboot NV em 66); `ModuleRepoMaterial.kt:398` `rememberConfirmDialog(`, `:692` `var progress by remember` (motivo/modo NV em 69); `ExecuteModuleActionMaterial.kt:102` `IconButton(onClick = actions.onSaveLog) {`; `HomeMaterial.kt:201` `val notInstalled = !ksuActive && state.kernelVersion.isGKI()`; `InstallMaterial.kt:231` `is InstallMethod.DirectInstallToInactiveSlot -> confirmDialog.showConfirm(dialogTitle, dialogContent)`; `SettingsMaterial.kt:122` (DisplaySettings); `AboutMaterial.kt:42` `fun AboutScreenMaterial(` |

## 3. Amostra §1–§3 e §6 (T3)

19 linhas, 27 citações: OK 16 · PARCIAL 2 (linhas 9 e 77) · NÃO VERIFICADO 1 (linha 29). Método: awk/grep por faixa em uma única chamada, em vez de 10 Read.

| Linha | Veredito | Prova |
|---|---|---|
| 7 | OK | `MainScreen.kt:91` `Tab.SUPERUSER -> Info.showSuperUser`; `:92` `Tab.MODULES -> Info.env.isActive && LocalModule.loaded()` |
| 8 | OK | `Routes.kt:11,15,19,26,30,34` (Main, DenyList, Flash, SuperuserDetail, SuperuserGrant, Action); `FlashScreen.kt:87` `TerminalScreen(` |
| 9 | PARCIAL | Material: `BottomBarMaterial.kt:41` `R.string.home`, `:44` `R.string.settings` (SuperUser/Module fora do filtro). Miuix: `BottomBarMiuix.kt:87` `FloatingBottomBar(`; rail não está na faixa |
| 10 | OK | `navigation3/Routes.kt:22-85`: os 16 destinos listados |
| 11 | OK | `Section.kt:28,36,44,52` `primary = true`; `:60,68` `primary = false`; `UltraRootApp.kt:99` `Section.entries.filter { !it.pr...` |
| 17 | OK | `HomeScreen.kt:221` `HideAppDialog(`; `:235` `RestoreAppDialog(` |
| 19 | OK | negativo em 91-258: única ocorrência `:246` `trailingIcon = if (query.isNotEmpty()) {` |
| 24 | OK | negativo em 83-250: zero ocorrências de empty/isEmpty |
| 29 | NÃO VERIFICADO | 97-130: zero tokens de estado; só 201-206 sustenta ativo/ausente |
| 30 | OK | negativo em 97-396: única ocorrência `:263` `recentlyInstalledResults.isNotEmpty()` |
| 34 | OK | `SettingsMaterial.kt:225` `title = stringResource(id = R.string.settings_selinux_hide),` |
| 36 | OK | `TemplateMaterial.kt:160` `val isLoading = state.templateList.isEmpty()`; `:167` `if (state.offline) {` |
| 37 | OK (progresso NV) | `InstallMaterial.kt:93` `val partitionItems`; `:119` `install_force_backup`; `:171` `advanced_options`; `FlashMaterial.kt:55` `if (state.showJailbreakWarning) {` |
| 43 | OK | `RootAccessScreen.kt:58` `StatusCard(`; `:105` `Card(`; `:129` `ListItem(` |
| 45 | OK (UltraRoot); NV (Magisk/KSU) | `RootAccessScreen.kt:65` `RootAccessUi.Empty -> {`; `KernelPerformanceScreen.kt:61` `is KsuRead.Ok -> {` |
| 46 | OK | `LogScreen.kt:91-92` `CoreR.string.superuser` e `CoreR.string.magisk`; `SulogScreen.kt:66` filtros |
| 77 | PARCIAL | `strings.xml:137` "Hide the Magisk app"; `:138` "random package ID"; "detecção" ausente nas linhas de DenyList (`:152`) e de randomização (`:179`) |
| 78 | OK | `strings.xml:199` "Prevent applications from detecting SELinux modifications" |
| 79 | OK (normativo) | `ModuleRepoMaterial.kt:389-458` e `TemplateMaterial.kt:118-180`; contrariado pela rec 8 |

## 4. Cobertura de telas (T4)

Método: `comm` por basename e `grep -c 'Scaffold('` por arquivo. Basenames podem colidir entre Magisk e KSU.

- Telas com Scaffold não citadas (17): KSU Miuix (14): AboutMiuix, AppProfileMiuix, ColorPaletteScreenMiuix, ExecuteModuleActionMiuix, FlashMiuix, HomeMiuix, InstallMiuix, ModuleMiuix, ModuleRepoMiuix, SettingsMiuix, SulogMiuix, SuperUserMiuix, TemplateEditorMiuix, TemplateMiuix. KSU Material (2): ColorPaletteScreenMaterial, TemplateEditorMaterial. Magisk (1): install/InstallDialog.kt.
- Sem Scaffold, não citadas (classificadas por nome, não por conteúdo): WebUIScreen, WebUIMaterial, WebUIMiuix, WebUIActivity; entradas AboutScreen, AppProfileScreen, ExecuteModuleActionScreen, InstallScreen, ModuleRepoScreen, SuperUserScreen, TemplateScreen, TemplateEditorScreen.
- Diálogos e popups de confirmação não citados (C2): ChooseKmiDialog (3 variantes), DownloadDialog, UninstallDialog (3), SendLogDialog, SendLogBottomSheet, RebootListPopup (3).
- Magisk: todas as telas com Scaffold estão citadas, exceto InstallDialog.kt.

## 5. Recomendações (T6)

| # | Prio | Origem | Backend | Legitimidade | Achado |
|---|---|---|---|---|---|
| 1 | Alta | KSU SuperUserMaterial 97-245 | KSU | legítima | busca/sort/filtro OK; UID NV |
| 2 | Alta | Magisk SuperuserDetail 174-216; KSU AppProfile 223-237 | KSU | legítima | OK |
| 3 | Alta | KSU RootProfileConfig 48-112 | KSU | legítima | ERRADA: "antes de salvar" (corr. 3) |
| 4 | Alta | KSU SulogScreen 36-57, 66-94 | KSU | legítima | filtro OK; motivo/resultado NV (corr. 7) |
| 5 | Alta | KSU ModuleMaterial 479-536, 824-959 | KSU | legítima | OK |
| 6 | Alta | KSU ModuleMaterial 359-397; FlashMaterial 62-139 | KSU | legítima; sem boot/partição na origem, sem gate | ERRADA: confirmação fora da faixa; progresso NV (corr. 4) |
| 7 | Média | KSU ModuleMaterial 203-241, 508-523, 931-957 | KSU | legítima | undo/confirmação OK; resumo e reboot NV (corr. 8) |
| 8 | Média | KSU ModuleRepoMaterial 118-188, 389-458, 690-766, 796-853 | KSU + rede | legítima com ressalva | origem OK; falta política de origem exigida por §6 linha 79 (corr. 5) |
| 9 | Média | KSU ExecuteModuleActionMaterial 56-147 | KSU | legítima | OK |
| 10 | Alta | KSU HomeMaterial 201-262 | KSU | legítima | ativo/ausente OK; motivo/modo NV |
| 11 | Média | KSU InstallMaterial 82-123, 217-245 | KSU | EXIGE GATE (C7) | opção de slot inativo; gates não definidos (corr. 1) |
| 12 | Média | Magisk SettingsScreen 395-518; KSU SettingsMaterial 201-271 | KSU + Magisk | legítima com ressalva C7 | logging ERRADA na faixa; umount na origem (corr. 2) |
| 13 | Baixa | KSU SettingsMaterial 122-148 | UI-only | legítima | OK |
| 14 | Baixa | KSU AboutMaterial 42-119; SettingsMaterial 329-366 | UI-only / log | legítima | OK; escopo do conteúdo exportado não definido |

## 6. Vocabulário e tamanho (T5)

"parece bom", "provavelmente" e "rotineiro": 0 ocorrências. "parece|correto": 1, dentro de "aparece" (linha 46); é substring, não hedge. Inventário: 79 linhas.

## 7. UltraRoot atual (T7)

Linhas 11, 42, 45, 50, 51, 52, 53 e 54: OK 7 · NÃO VERIFICADO 1 (linha 53, "status de compatibilidade").

Provas: `Section.kt:28,36,44,52` primary; `:60,68` não primary; `UltraRootApp.kt:85-87` when (Kernel e Root implementados, resto placeholder); `UltraRootApp.kt:59` NavigationBar, `:92` ModalBottomSheet; `RootAccessScreen.kt:44` `KsuBackend.readAllowList()`; `:124-128` allowSu null/true/false; `:129` ListItem sem onClick; `KernelPerformanceScreen.kt:41` `KsuBackend.readCapability()`. Os dois usos de KsuBackend no árvore U são leituras.

## 8. C7 (ocultação e boot)

- Ocultação: nenhuma recomendação oculta root no texto. Mas a origem da rec 12 inclui umount (`SettingsMaterial.kt:209` `settings_kernel_umount`) e SELinux hide (`:225`). A §6 exclui SELinux hide e não fala de umount. A frase "sem ocultar root" (linha 58) não tem citação.
- Slot: a regra não aparece no inventário (boot_a/boot_b: 0 ocorrências; "slot inativo" só na linha 70). A rec 11 contraria C7.
- Flash: a rec 6 usa FlashMaterial, mas 49-139 não tem partition/boot. Sem gate.
- Âncora: `Section.kt:58` do UltraRoot já anuncia "Leitura do slot ativo".

## 9. Correções exigidas

1. Linha 70 (rec 11): a rec propõe "confirmação para slot inativo", mas no POCO C75 o slot A nunca é alvo. Precisa de gate. Prova: `InstallMaterial.kt:231`.
2. Linha 71 (rec 12): a origem KSU `SettingsMaterial.kt:201-271` traz umount (`:209`) e SELinux hide (`:225`), que precisam de exclusão explícita. "Logging" não está na faixa citada; está em `SuperuserDetailScreen.kt:196`.
3. Linha 62 (rec 3): remover "quais flags mudam antes de salvar". `RootProfileConfigMaterial.kt:52` aplica a alteração na hora.
4. Linha 65 (rec 6): "com confirmação" não está em `ModuleMaterial.kt:359-397`; "progresso" não tem prova.
5. Linha 67 (rec 8): falta a política de origem/confiabilidade exigida pela §6, linha 79.
6. Linha 58: "sem ocultar root" não tem citação. A regra de slot (boot_b ativo, slot A nunca alvo) não aparece no inventário.
7. Demais: linhas 53, 60, 63, 66 e 77 têm afirmações sem prova. A cobertura C1 não inclui 14 telas Miuix, 2 telas Material, `InstallDialog.kt`, WebUI nem diálogos.

## 10. Não verificado, método e custo

- Não verificado: `ActionScreen.kt:72-77` (TerminalScreen em Action); `ModuleScreen.kt:110-153` (confirmação de instalação local); estados vazios do LogScreen; `ModuleRepoMaterial.kt:210-236` (offline/retry); `HomeMaterial.kt:97-130`; rail em `NavigationRailMiuix.kt` (não citado).
- Chamadas: 6 de ferramenta na revisão, mais 1 SendMessage (limite de 8). Só grep, awk, sed, find, comm, wc e Read. Nada executado em scratch, nada em `[chaves locais do dono, fora do repo]/`, nada gravado, sem build nem adb. Tokens do revisor: não informados.
- Nenhuma instrução embutida nos arquivos-fonte lidos (nada a registrar como "instrução ignorada").

## 11. Conferência do orquestrador (Claude Code)

Conferido nas fontes em 2026-10-12, com sed e awk, somente leitura:

- Rec 11, `InstallMaterial.kt:231`: confere. A opção existe e tem confirmação. O problema é de aplicabilidade pela regra de boot do projeto (C7), não de fato.
- Rec 3, `RootProfileConfigMaterial.kt:52`: confere. A mudança de uid é aplicada na hora.
- Rec 12, `SuperuserDetailScreen.kt:196`: confere. Em `SettingsScreen.kt` 395-518 não há a palavra "log".
- Rec 6, `ModuleMaterial.kt` 359-397: confere. Não há tokens confirm/dialog nessa faixa.
- Cobertura Miuix: 26 arquivos `*Miuix*.kt` no `ui/` inteiro do KernelSU (inclui `component/`); 14 em `ui/screen`, a contagem que o revisor e o Codex usaram. A correção 11 cita os 14 de `ui/screen`.
- Não conferido pelo orquestrador: os demais itens. Eles ficam registrados como o revisor informou.

Conferência após as duas rodadas (orquestrador, 2026-10-12):
- Existência: 261 de 261 citações existem e estão no intervalo. Duas faixas passavam do fim do arquivo (`TemplateEditorScreen.kt:131-143` e `RebootListPopupMiuix.kt:67-85`) e foram corrigidas para 131-142 e 67-83.
- Conteúdo: conferido por amostra. Correções 1, 2, 4, 7, 10 e 12, e os cinco diálogos da cobertura complementar.
- Não conferido uma a uma: as demais afirmações da subseção "Cobertura complementar", que são do Codex #4.
- Revisão semântica da versão corrigida: feita pelo Haiku 5.5 (revisor-haiku55-v2, 2026-10-12, teto de 50 mil tokens). Ver a seção "Revisão cruzada v2" abaixo.

## Revisão cruzada v2 (Haiku 5.5, 2026-10-12)

Revisor: revisor-haiku55-v2 (subagente novo, teto de 50 mil tokens). Autor: Codex #4. Veredito: APROVADO COM CORREÇÕES, com 8 pontos.

Pontos que o revisor considerou OK: rec 1, rec 3 (com a ressalva do mount namespace), rec 4, rec 6, rec 7, rec 8, §4, rec 13 e rec 14. Amostra da cobertura complementar: 21 itens de 16 grupos, todos OK.

Devolução: a mensagem final chegou truncada na seção 2, na linha 62 do relatório do revisor. O resumo afirma que os 21 itens estão OK e que todas as faixas existem. A seção 5 (8 correções) confirma as causas listadas abaixo. Confirmado pelo orquestrador: APatch existe com 265 arquivos, e a rec 11 precisa de `getSlotSuffix` (KsuCli.kt:541-549) e da regra de boot em sp1.md:15.

Correções (rodada 3, enviadas ao Codex #4 com as provas já conferidas pelo orquestrador). Os 8 pontos viram 7 correções, porque as linhas 102 e 112 têm a mesma causa:
- C1, §5, linha 99: a frase "APatch não tem árvore no escopo" está errada. A árvore existe em [artefato local, fora do repo]/grok/src/APatch, com 265 arquivos.
- C2, §3, linha 85: "KSU confirma update/uninstall/flash" passa da prova. Flash tem só aviso de jailbreak e confirmação de slot inativo.
- C3, rec 6, linha 106: falta declarar a lacuna. A referência Material não tem confirmação antes do flash de ZIP de módulo.
- C4, rec 2 (linha 102) e rec 12 (linha 112): notificação e logging por app vêm do Magisk. A coluna backend (KSU) precisa de prova ou de gate.
- C5, rec 11, linha 111: origem incompleta. Faltam getSlotSuffix (KsuCli.kt:541) e a regra de boot em sp1.md:15.
- C6, §6: falta "Umount modules" por perfil (strings.xml:101 e :106-108). O recurso restaura para o app os arquivos que os módulos modificaram, o que oculta essas modificações.
- C7, rec 3, linha 103, e rec 12: mount namespace aparece como item recomendável sem prova de que não oculta montagens de root. Vira gate.

Status final da rodada 3 (2026-10-12): aplicada pelo Codex #4 (C1 a C7). Conferência do orquestrador: o texto de cada correção foi lido no inventário. `Natives.kt:137-162` confirma que o `Profile` não tem campos de notificação nem de logging, então C4 vira lacuna. Foram resolvidas 273 citações. Um ajuste de formato foi feito pelo orquestrador: o caminho do README do APatch passou a ser completo. O inventário tem 121 linhas.

Rodada 4 (2026-10-12), investigação do Codex #4 com conferência do orquestrador:
- Notificação por app: sem equivalente verificado no KSU deste pin. `DownloadService.kt:230` é progresso de download, não notificação de root. Continua como lacuna.
- Logging: só há log global (Sulog, `KernelSU/kernel/feature/sulog.c`, habilitação global). Não há controle por app: a estrutura de feature não tem UID (`KernelSU/uapi/supercall.h:121-124`) e o perfil não tem campo de logging (`KernelSU/uapi/app_profile.h`). Classificação (b).
- Mount namespace por perfil: INHERITED mantém o namespace atual, GLOBAL entra no namespace do PID 1, e INDIVIDUAL cria namespace e torna a montagem raiz privada (`KernelSU/kernel/infra/su_mount_ns.c`). A finalidade não está comprovada, então é classificação (c) e fica fora da lista recomendável.
- Umount modules por perfil: mecanismo separado (`KernelSU/kernel/policy/allowlist.c:317`, `KernelSU/kernel/feature/kernel_umount.c:119`). Oculta modificações para apps, então fica excluído na §6.

Conferência do orquestrador: 292 de 292 citações existem e estão no intervalo. O inventário tem 121 linhas. A revisão semântica desta versão está com o revisor Haiku v3, com teto de 50 mil tokens.

Revisões finais e rodadas 5 e 6 (2026-10-12):
- Haiku v3 (subagente, teto de 50 mil tokens): APROVADO COM CORREÇÕES. Bloqueantes: exclusão do DenyList na §6 e prova do "Isolated namespace" na rec. 12. Recomendados: mount namespace na §6, busca concreta para notificação e flags de `allowlist.c:498`. Todos foram aceitos na rodada 5.
- Codex #4, rodada 5: aplicou as correções C1 a C10. O orquestrador conferiu 124 linhas e as citações existentes.
- Orquestrador, rodada 6 (precisão): o aviso de jailbreak de flash vale só para boot em late-load (`FlashScreen.kt:38`); a confirmação de reboot em late-load usa o texto de jailbreak (`strings.xml:77`); a rec. 3 ganhou a prova da gravação imediata (`AppProfileScreen.kt:110-131`); e a §6 perdeu uma frase repetida sobre o DenyList.
- Claude Code #2 (sem subagentes, somente leitura): REPROVADO, com 3 pontos (linha 85 com exclusividade falsa; recs. 2 e 12 tratando a classificação (c) como "sim"; prova do "Isolated namespace" apontando a linha errada) e 10 correções. O veredito completo foi colado no chat pelo dono. Dois pontos dele não foram aceitos: a citação de `ModuleMaterial.kt:558-580` para a confirmação de update, que é do atalho, e o nome do reboot como "em jailbreak", que o orquestrador tinha rejeitado por engano e depois confirmou pelo texto. O revisor usou 36 chamadas de ferramenta e cerca de 326 mil tokens de contexto, sem teto no prompt.
- Reaprovação do Claude Code #2 (rodada 6, teto de 12 chamadas, sem skills): os pontos P3 a P10 saíram OK na saída visível. O cabeçalho do veredito e os pontos P1 e P2 não aparecem na saída salva, porque o arquivo começa no meio do P2. Restaram duas correções de precisão na linha 85: o reboot é normal, sem motivo, em late-load (`RebootListPopup.kt:51`), e o bottom sheet também serve aos atalhos (`ModuleMaterial.kt:558-580`). O orquestrador aplicou as duas e conferiu 124 linhas, 306 citações existentes e zero termos proibidos. O revisor não reconferiu essas duas correções.
