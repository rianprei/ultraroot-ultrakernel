# Auditoria do manager KernelSU — onde entra o UltraRoot

Data: 2026-10-11. Leitura do checkout `[artefato local, fora do repo]/grok/manager/KernelSU`, commit `6e3860443f8fea7c5fc226277e53888033796488`. Sem build e sem device. Nada deste arquivo foi implementado.

As 6 seções do contrato: Root Access, Module Center, System Toolkit, Kernel & Performance, Boot & Recovery, Extensions Hub. Settings, Home, log e WebUI já existem e ficam de suporte; não são a sexta seção.

## Estrutura real

Um módulo Gradle: `include(":app")` em `manager/settings.gradle.kts:22`. Namespace e pacote padrão `me.weishu.kernelsu` em `manager/app/build.gradle.kts:26` e `:64`. `IS_PR_BUILD` troca o pacote para `me.weishu.kernelsu.pr` (`build.gradle.kts:25-28`). `KSU_PACKAGE_NAME` e `KSU_NAME` sobrescrevem pacote e nome visível (`:28-29`). `applicationId = managerPackageName` (`:149`).

UI é Compose. As quatro abas do pager estão fixas:

- `MainPagerConfig.PAGE_COUNT = 4` em `ui/viewmodel/MainActivityViewModel.kt:91-93`.
- `MainActivity.kt:401-405` desenha a página: 0 `HomePager`, 1 `SuperUserPager`, 2 `ModulePager`, 3 `SettingPager`.
- As rotas `Route.Home`, `Route.SuperUser`, `Route.Module` e `Route.Settings` (`ui/navigation3/Routes.kt:26-38`) caem todas em `mainScreenEntry()` (`MainActivity.kt:238-241`). A rota só escolhe a aba.

Telas que não são aba, registradas no mesmo `NavDisplay` (`MainActivity.kt:214-237`): About, Sulog, ColorPalette, AppProfileTemplate, TemplateEditor, AppProfile, ModuleRepo, ModuleRepoDetail, Install, Flash, ExecuteModuleAction.

WebUI não é rota. É `WebUIActivity` (`AndroidManifest.xml:67`, `ui/webui/WebUIActivity.kt:36` e `:113`). `ModuleScreen.kt:150` abre essa activity. O estado está em `ui/webui/WebUIState.kt:15-26`.

ViewModels em `ui/viewmodel/`: `HomeViewModel` (`:29`), `SuperUserViewModel` (`:80`, `loadAppList` `:398`), `ModuleViewModel` (`:50`, `fetchModuleList` `:255`), `SettingsViewModel` (`:22`), `MainActivityViewModel` (`:16`), mais Template, Sulog e ModuleRepo.

## Como fala com o kernel

Dois canais, os dois atrás de root:

1. JNI `libkernelsu`. `Natives.kt:34-36` faz `System.loadLibrary("kernelsu")`. O ioctl sai de `ksu.cc:23-76`: procura o fd `[ksu_driver]` e chama `ioctl`. `get_info()` (`ksu.cc:80-87`) usa `KSU_IOCTL_GET_INFO`. A versão de UAPI que o manager declara é `KERNEL_SU_UAPI_VERSION = 5` em `uapi/supercall.h:13`, devolvida por `get_manager_uapi_version()` (`ksu.cc:95-97`). O legado `prctl(0xDEADBEEF, 2, ...)` continua em `ksu.h:58-63`. O piso de kernel no app é `MINIMAL_SUPPORTED_KERNEL = 32513` (`Natives.kt:27`). Grants passam por `getAppProfile` / `setAppProfile` (`Natives.kt:66-67`). O domínio esperado do manager é `u:r:ksu:s0` (`Natives.kt:29`).

2. `libksud.so` via shell root. `KsuCli.kt:40` monta o path da lib. `execKsud` (`:102-109`) roda `libksud.so <args>`. `listModules()` (`:139-144`) é `ksud module list`. `getFeatureStatus` / `getFeatureValue` (`:112-122`) são `ksud feature check|get`. `install()` (`:132-136`) instala o daemon com `libadbroot.so`.

A lista de módulos da UI não lê `/data/adb/modules` direto. `ModuleRepositoryImpl.kt:21-24` chama `listModules()` e parseia o JSON do ksud.

## Onde está cada coisa que já existe

| Peça | Onde |
|---|---|
| Root Access (grants) | Página 1, `ui/screen/superuser/SuperUserScreen.kt:22`. Dados em `SuperUserViewModel.loadAppList` (`SuperUserViewModel.kt:398`) e `Natives.getAppProfile`. |
| Module Center | Página 2, `ui/screen/module/ModuleScreen.kt:39`. Repositório em `ModuleRepositoryImpl.kt:21`. Repo externo é `Route.ModuleRepo` (`Routes.kt:78`). |
| Settings | Página 3, `ui/screen/settings/SettingsScreen.kt:21`. |
| Log | `Route.Sulog` → `SulogScreen` (`MainActivity.kt:216`). |
| WebUI | `WebUIActivity`, aberta pelo módulo (`ModuleScreen.kt:150`). |
| Home | Página 0, `ui/screen/home/HomeScreen.kt:34`. Status do kernel via `Natives`. |

## Onde as 6 seções e o adapter entram

Não reescrever `Natives` nem `KsuCli`. Eles são o núcleo que o kernel reconhece.

Camada de adapter, pacote novo `me.weishu.kernelsu.core.backend` (não existe hoje):

- `RootBackend` com as operações que a UI já usa: versão, UAPI, `isManager`, profile get/set, lista de módulos, `feature check`.
- `KsuBackend` delega para `Natives` e `KsuCli`. É o único backend neste incremento.
- `BackendDetector` lê `Natives.isManager` (`Natives.kt:53-54`) e `get_kernel_uapi_version` (`ksu.cc:90-92`). Não escolhe Magisk/APatch ainda: o spec pede detectar, e este app só tem o canal KSU.

Seções:

| Seção | Encaixe |
|---|---|
| Root Access | Permanece a página 1. A tela passa a pedir grants pelo `RootBackend`, e a implementação de hoje é `KsuBackend` → `Natives.setAppProfile`. |
| Module Center | Permanece a página 2. Continua em `ksud module list`. O adapter não reimplementa o parser de `ModuleRepositoryImpl.kt:28-45`. |
| System Toolkit | Página nova. `PAGE_COUNT` sobe e o `when (page)` em `MainActivity.kt:401` ganha um ramo. Primeiro conteúdo: leitura de props e de `ksud feature check`, sem escrita. |
| Kernel & Performance | Página nova, ou um bloco dentro da Home (`HomeScreen.kt:34`) até o pager crescer. Lê `Natives.version` e `getFeatureValue` (`KsuCli.kt:122`). Não escreve governor nem OPP. |
| Boot & Recovery | Não é aba de uso diário. Encaixa ao lado de `Route.Install` e `Route.Flash` (`Routes.kt:68-81`, `MainActivity.kt:223-231`). Mostra o plano de slot e o hash; não chama fastboot. |
| Extensions Hub | Ao lado de `Route.ModuleRepo` (`Routes.kt:78`). Lista extensões conhecidas (ZygiskNext, WebUI) com estado lido. Não baixa nada. |

Settings e Sulog continuam onde estão.

## O que reusar e o que criar

Reusar: pager, `NavDisplay`, `Natives`, `KsuCli`, `ModuleRepositoryImpl`, `SuperUserViewModel`, WebUI, Flash/Install. O nome visível já tem gancho: `KSU_NAME` em `build.gradle.kts:29`.

Criar: o pacote `core.backend`, duas rotas novas no `Route` (`Routes.kt:19`), dois ramos no pager, uma tela de capability que só lê.

Não mexer: `applicationId`, o cert de release, `KERNEL_SU_DOMAIN`, o magic `0xDEADBEEF`, o UAPI 5. `IS_PR_BUILD` muda o pacote para `.pr` (`build.gradle.kts:26`). Isso quebra o reconhecimento do manager pelo kernel. O pacote que o C0.3 autoriza é `me.weishu.kernelsu`.

## Primeiro incremento

A menor fatia que lê dado real:

1. Nome visível UltraRoot via `KSU_NAME`, pacote intacto.
2. `KsuBackend` + `BackendDetector` em volta de `Natives.version`, `get_manager_uapi_version` e `KsuCli.getFeatureStatus`.
3. Uma seção stub, Kernel & Performance, na Home ou como quinta página, que mostra versão do kernel, UAPI do manager (5) e UAPI que o ioctl devolver. Sem botão de aplicar.

Isso não concede root, não lista módulo novo e não flasha.

## Aberto

- Não abri o APK instalado no aparelho. A auditoria é o source do pin.
- Não medi, neste arquivo, se `Natives.isManager` está true no lake depois do flash do C0.3.
- Magisk e APatch não têm classe neste source. O detector fica com um backend só até o SP3.
