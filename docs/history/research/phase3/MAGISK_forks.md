# MAGISK forks — Fase 3 (refeita)

Base do compare: `topjohnwu/Magisk` branch `master` (default), push `2026-10-09T16:40:31Z`, 63195 estrelas, 18834 forks. Lista: `GET repos/topjohnwu/Magisk/forks` com `sort=stargazers`, `per_page=100`, página 1. O 40º lugar empata em 4 estrelas, então a comparação cobre os 47 forks com 4 ou mais estrelas dessa página. Em cada um, `GET repos/topjohnwu/Magisk/compare/master...USER:BRANCH` para o branch default, `master` e `main`. Scratch: `[artefato local, fora do repo]/grok/magisk-forks/`. Sem clone. Sem workflow.

O relatório anterior olhou o top 10 e tratou WeaveMask como 1 commit de ícone e Magisk-Monet como 1 commit de README. Nesta medição o WeaveMask está 312 à frente e o Monet 28. `pixincreate/Magisk`, listado antes como espelho, está 7 à frente.

`KitsuneMagisk/Magisk` e `HuskyDG/Magisk` aparecem na lista de forks e o `GET` do repositório devolve 404. O contrato manda ignorar Kitsune e `HuskyDG/magisk-files`. Esse segundo nem entrou no corte de estrelas.

Matriz de referência: `docs/history/research/phase1/GROK_matriz_features.md`, 30 linhas. NOVA = não é uma dessas linhas.

## Tabela

| fork | estrelas | ahead | feature | evidência | classe |
|---|---:|---|---|---|---|
| vvb2060/Magisk | 1294 | 0 no `master` (behind 81). Branch `download` 8, `fix` 7 | No `master`, nenhuma. Nos branches, extrator de payload OTA | `master` = pin Alpha `96221b69`. O `download` adiciona `Payload.kt`. O master atual já tem `OtaPayloadExtractor.kt:17` | na-matriz linha 30 |
| KitsuneMagisk/Magisk | 343 | ERR | repo 404 | `GET repos/KitsuneMagisk/Magisk` HTTP 404 | ignorado |
| Seyud/WeaveMask | 195 | 312 (behind 326) | WebUI de módulo no manager | `app/apk/.../weave/ui/webui/WebUIActivity.kt:29` `class WebUIActivity`. Busca `WebUIActivity` no master oficial: 0 | na-matriz linha 15 |
| LSPosed/Metagisk | 173 | 0 (behind 796) | nenhuma | compare `master` | espelho |
| pranshoe/Magisk-Monet | 146 | 28 (behind 1688) | tema e README | subjects e paths em `app/` | sem feature nova |
| pixincreate/Magisk | 100 | 7 (behind 4) | spawn do GrapheneOS no Zygisk | `native/src/core/zygisk/exec_spawn_replay.hpp:12` `is_grapheneos_exec_spawn_replay_contract`. `module.hpp:214` `is_grapheneos_exec_spawn_replay`. Busca `exec_spawn` no master: 0 | NOVA |
| RikkaApps/Magisk | 77 | 3 (behind 4560) | `sepolicy_custom` em 2019 | subjects `dbd3ea6dad0a` e `49ff61b388d1`, paths `native/jni/init/` | sem feature nova |
| fei-ke/Magisk | 64 | 0 (behind 1277) | nenhuma | compare `master` | espelho |
| QOS3/Magisk | 54 | 0 (behind 218) | nenhuma | compare `master` | espelho |
| RikkaW/Magisk | 49 | 0 (behind 4231) | nenhuma | compare `master` | espelho |
| zcg9783/Magisk-On-System | 47 | 11 (behind 208) | instala o Magisk na partição system | `scripts/app_functions.sh:83` `mount_system`. `:134` grava `/system/etc/setup-magisk.sh`. `:136` comentário `Magisk On System`. Commit `6e4421482aab` | NOVA |
| Anto426/Magisk-but-expressive | 43 | 108 (behind 208) | UI Compose | 293 arquivos em `app/`, datas 2026-03 a 2026-09 | sem feature nova |
| solohsu/Magisk | 43 | 3 (behind 4727) | regras sepolicy de 2019 | subjects, sem corpo aberto | sem feature nova |
| ianmacd/Magisk | 40 | 43 (behind 3251) | tema, 2020 | subjects de UI | sem feature nova |
| ayasa520/Magisk | 32 | 16 no `direct-system-install` (behind 208) | instala na partição system. Commit à parte comenta contexto SELinux | `scripts/app_functions.sh:61` `install_addond`, `:82` ramo `SYSTEM_INSTALL=true` copia para `/system/etc/init/magisk`, `:99` `direct_install`. Commit `cd3650a7afd7` altera `native/src/base/files.rs` | NOVA a instalação. O patch de contexto é compat |
| HuskyDG/Magisk | 29 | ERR | repo 404 | `GET repos/HuskyDG/Magisk` HTTP 404 | ignorado |
| badabing2005/Magisk | 26 | 3 no `Custom_30600` (behind 363). `master` 0 | o commit de desligar módulos foi desfeito no tip | subjects: disable de módulos, depois `Undo the last change`. Diff líquido de `module.rs` não aberto | [não-verificado] |
| isjerryxiao/Magisk | 20 | 0 no `master` (behind 7141). Branch `out` sem ancestral | nenhuma no `master`. Repo arquivado, push 2018 | compare `out`: `No common ancestor` | espelho no `master`. `out` [não-verificado] |
| tombriden/Magisk | 20 | 0 (behind 7837) | nenhuma | compare `master`. Push 2016 | espelho |
| canyie/Magisk | 19 | 0 (behind 1053) | nenhuma | compare `master` | espelho |
| tiann/Magisk | 17 | 1 (behind 1203) | `resetprop` como binário solto | subject do único commit | na-matriz linha 29 |
| 5ec1cff/Magisk | 16 | 0 (behind 500) | nenhuma | compare `master` | espelho |
| Yanndroid/Magisk-OneUI | 16 | 4 (behind 1913) | tema One UI, até 2023 | subjects `ui:` | sem feature nova |
| MT-Taybii/Magisk | 14 | 0 (behind 75) | nenhuma | compare `master` | espelho |
| EdgeAtZero/Magisk-MD3 | 13 | 2 no `feature/MD3` (behind 571). `master` 0 | UI MD3. Há arquivos `ui/web/` no compare | subjects. O corpo do web não foi aberto | [não-verificado] o web |
| d4rken/Magisk | 13 | 0 (behind 3288) | nenhuma | compare `master` | espelho |
| mistrmochov/MagiskForWaydroid | 11 | 21 (behind 411) | compat Waydroid e ReZygisk embutido no daemon | `README.MD:9` declara o fork para Waydroid. Commit `f070be0298df` tira `get_secontext`/`set_secontext` de `native/src/base/files.rs`. `rezygisk.rs:8` inclui `rezygisk.zip`. `rezygisk.rs:19` `install_rezygisk`. `bootstages.rs:140` chama essa instalação. `bootstages.rs:203` chama `hide_rezygisk` | NOVA |
| Magisk-Modules-Repo-CN/Magisk | 10 | 2 (behind 5693) | locale e ponteiro de repo, 2018 | subjects | sem feature nova |
| SunRayEx/Magisk-Metro | 7 | 47 (behind 79) | interceptor seccomp no su. Também há tela de WebUI | `native/src/core/su/ghost.hpp:4` descreve o interceptor. `:12` `ghost_install_filter`. `:15` `ghost_install`. WebUI em `WebUiFragment.kt` | NOVA o interceptor. WebUI na-matriz linha 15 |
| aviraxp/Magisk | 7 | 0 no `master` (behind 614) | nenhuma no `master` | O branch default listado não tem ancestral comum com o master | espelho no `master`. Outro branch [não-verificado] |
| kokoromagic/KitsuneMask | 7 | 2 (behind 1206) | workflow de CI | subjects só de `.github/workflows` | sem feature nova |
| bmax121/Magisk | 6 | 4 (behind 1119) | corte de sepolicy | subjects `adapt to apatch` e `sepolicy:` | sem feature nova |
| chaosmaster/Magisk | 6 | 0 (behind 1515) | nenhuma | compare `master` | espelho |
| tsynik/Magisk | 6 | 13 (behind 1723) | layouts e bump de submodule | subjects e paths de UI | sem feature nova |
| Genymobile/Magisk | 5 | 0 (behind 321) | nenhuma | compare `master` | espelho |
| phhusson/Magisk | 5 | 0 (behind 7320) | nenhuma | compare `master` | espelho |
| raana-inf/Magisk_domain_randomize | 5 | 3 (behind 208) | nome de domínio SELinux gerado em runtime | `native/src/include/consts.rs:43` `generate_and_save_domain`. `:75` `get_sepol_proc_domain`. Também há gerador de tipo de arquivo (`:92`) e de log (`:140`) | na-matriz linha 27 |
| twoyi/Magisk | 5 | 0 (behind 2430) | nenhuma | compare `master` | espelho |
| Dees-Troy/Magisk | 4 | 2 (behind 5419) | build do magiskboot na árvore Android, 2019 | subjects | sem feature nova |
| EkinStrop/Magisk_odm | 4 | 5 (behind 72) | prefixo `/odm` no mount de módulo | `native/src/core/module.rs:23` inclui `/odm/`. `:60` `find_attr_source`. O `module.rs` do master oficial não tem essas duas strings | na-matriz linha 16 |
| Fox2Code/Magisk | 4 | 1 (behind 2278) | tema AMOLED | subject | sem feature nova |
| Namelesswonder/Magisk | 4 | 4 no `32bit` (behind 1607) | magisk32 quando o zygote é 64-bit | `native/src/init/rootdir.cpp:68` reescreve `ro.vendor.product.cpu.abilist` para incluir `armeabi-v7a`. `:201` trata `magisk32`. Commits `5b0405ef6a21` e `a321cdb0a679` (2023). Busca `Zygote64` no master: 0 | NOVA |
| ajayyy/Magisk | 4 | 0 (behind 4193) | nenhuma | compare `master` | espelho |
| arter97/Magisk | 4 | 7 no `dev` (behind 4695). `master` 1 | commits de 2019 no `dev` (socket e sepolicy). O 1 commit do `master` não foi aberto | subjects do `dev` | sem feature nova no `dev`. `master` [não-verificado] |
| kimocoder/Magisk | 4 | 0 (behind 377) | nenhuma | compare `master` | espelho |
| nilz3000/Magisk | 4 | 9 no `vendor_boot_v4_updates` (behind 1181) | editor de tabela vendor ramdisk | `native/src/boot/ramdisk_table.hpp:7` `class ramdisk_table`. `:24` `ramdisk_table_commands`. O master oficial já tem `boot_img_hdr_v4` em `bootimg.hpp:319` e `vendor_ramdisk_table_entry_v4` em `:326`. `cli.rs:219` só cita `vendor_boot` como estado | na-matriz linha 30 |
| wzsx150/Magisk | 4 | 0 (behind 5672) | nenhuma | compare `master` | espelho |

## TOP features novas

Três, nesta ordem. O alvo C75 é um aparelho arm64 com boot image, não é GrapheneOS nem Waydroid.

1. Instalação na partição system. `ayasa520` (`install_addond` em `scripts/app_functions.sh:82`) e `zcg9783` (`mount_system` em `scripts/app_functions.sh:83`, script em `/system/etc/setup-magisk.sh`). A linha 30 da matriz é o patcher de `boot.img`. A linha 6 é o `magiskinit` no ramdisk. As duas células do Magisk oficial continuam sem este modo.
2. Spawn do GrapheneOS no Zygisk (`pixincreate`, `exec_spawn_replay.hpp:12`). Entra se um alvo usar esse zygote.
3. Compat para Waydroid (`mistrmochov`, `README.MD:9` e o commit `f070be0298df`).

Ficam de fora desta lista, de propósito: o interceptor de `ghost.hpp`, o domínio aleatório de `consts.rs:43`, e a chamada `hide_rezygisk` em `bootstages.rs:203`. Estão na tabela. O porte `magisk32` do Namelesswonder é de 2023 e só importa com zygote 32-bit.

O WebUI do WeaveMask e o prefixo `/odm` do EkinStrop já têm linha na matriz (15 e 16). O formato `vendor_boot` v4 já está no `bootimg.hpp` do master.

## Veredito

Vivo. Em 2026 ainda há fork com feature que o master oficial não tem: instalação em `/system`, spawn do GrapheneOS, e o par Waydroid mais ReZygisk. O cenário do relatório anterior, oito de dez espelhos e o resto trivial, não se sustenta.

## Aberto

- `badabing2005/Magisk`: o tip desfaz o commit de bootloop. O diff líquido não foi aberto.
- `EdgeAtZero/Magisk-MD3`: arquivos `ui/web/` no compare, corpo não lido.
- `arter97/Magisk` branch `master`: 1 commit, não aberto. O `dev` foi o que entrou na tabela.
- `isjerryxiao/Magisk` branch `out` e o branch default de `aviraxp/Magisk`: sem ancestral comum com o master.
- `KitsuneMagisk/Magisk` e `HuskyDG/Magisk`: 404.
- Forks abaixo de 4 estrelas não foram comparados.
