# Forks do APatch e do KernelPatch

Data: 2026-10-10. Enumeração por `gh api` em `repos/bmax121/APatch/forks` (794) e `repos/bmax121/KernelPatch/forks` (383), ordenados por estrelas. Compare contra `main` do upstream nos top 40 de cada um, mais quatro nomes de KernelPatch fora desse corte (`KernelPatch-KPM`, `KernelPatchnext`, `KPNext`, `NextGenSU`). Clone shallow só onde o subject apontava capacidade nova. Scratch: `[artefato local, fora do repo]/grok/apatch-forks/`.

Upstream no dia: APatch `main` 8029 estrelas, push `2026-10-10T14:34:38Z`, size 24523. KernelPatch `main` 1561 estrelas, push `2026-10-10T13:16:57Z`, size 2868.

Matriz de referência: `research/phase1/GROK_matriz_features.md`, 30 linhas, coluna APatch = nono motor. Pin local já lido: APatch `e78e7b3e`, KernelPatch `f36f3d13`.

Dos 84 compares: APatch 18 ahead, 20 em 0, 1 sem ancestral. KernelPatch 18 ahead, 27 em 0. Ahead 0 não entra na tabela.

## Forks com commit próprio

`ahead` é `ahead_by` de `bmax121/<repo>/compare/main...owner:branch`. Evidência de clone é o HEAD shallow. Evidência de compare é o subject, sem clone.

| Fork | Estrelas | Ahead | Feature | Evidência | Classe |
|---|---:|---:|---|---|---|
| Prslc/APatch (`miuix`) | 24 | 309 | Manager em Miuix, tema e intent pendente | `MainActivity.kt:65` importa `top.yukonga.miuix.kmp.basic.Scaffold`; `:79-90` guarda `pendingIntent` e chama `onNewIntent`. Ponta `1acd4305a9a6` é só `kp: bump to 0.13.3`. | NOVA (UI; a matriz não tem linha de toolkit) |
| niqiuqiux/KernelPatchUltra (`ultra`) | 18 | 32 | Offset de membro via BTF para o kpimg | `kernel/patch/ksyms/struct_hash.c:471` `btf_get_member_offset`; export `:487`. Uso em `task_cred.c:155`. HEAD `13c0f1b4e2256344f9a13d106224d8cf1288995e`. O pin local do KernelPatch não tem esse símbolo. | NOVA (alimenta a linha 5, não é a linha 5) |
| ColdAsSunny/APatch | 13 | 4 | Troca de package, tira checagem de assinatura, `/bin/sh` → `/system/bin/sh` | Subjects do compare. Sem clone. | sem feature nova |
| FrostRime/APatch | 10 | 178 | Troca de namespace / `applicationId`, desliga checagem de assinatura | Subjects do compare (`com.frost.apatch`, depois `com.kqengine.meet`). Sem clone. | sem feature nova |
| JingMatrix/APatch | 10 | 2 | Mostra o contexto SELinux default como `untrusted_app` | Subject do compare. Sem clone. | sem feature nova |
| HmnDev-Tech/BPatch | 10 | sem ancestral | README repete root / APM / KPM do APatch. Descrição do repo diz Jetpack Compose. | `gh api repos/HmnDev-Tech/BPatch` e o README. Compare: HTTP 404, sem ancestral comum com `main`. | [não-verificado] |
| hasandramali/APatch-hidden | 5 | 1 | Nome sugere hide. O único commit à frente se chama `update` e mexe em `app/`. | Compare, 1 commit. Diff não aberto. | [não-verificado] |
| xm999n/APatch | 4 | 39 | Lista de umount por path em `post-fs-data` / `service` | `apd/src/event.rs:506-535` `run_custom_umount`, arquivo `WORKING_DIR/umount`. HEAD `cf9896ee53404e508c93e1322d2d0e2dcbbb88d8`. O pin local do APatch não tem `run_custom_umount`. `resetprop` e `magiskpolicy` neste fork repetem o que o pin já tem (`apd/src/resetprop.rs`, `apd/src/sepolicy.rs`). | NOVA a lista de paths. resetprop = linha 29, na-matriz. magiskpolicy não é linha própria; o applet já está no pin. |
| Admirepowered/APatch | 4 | 2 | `update to latest kp` | Subject do compare. | sem feature nova |
| DjihadsTesting/FreakPatch | 3 | 19 | Subjects `freaky` e README | Compare. | sem feature nova |
| CaptainThrowback/APatch | 3 | 4 | CI e `magiskboot` na pasta `bin` | Subjects do compare. | na-matriz (linha 30, patcher de boot) |
| y0geshx/APatch | 3 | 1 | README | Subject. | sem feature nova |
| symbuzzer/fork-APatch | 3 | 1 | `docs/faq_tr.md` | Subject. | sem feature nova |
| matcxz/MPatch | 2 | 10 | Manager `armeabi-v7a` | `README.md:13` e `:23`. `app/build.gradle.kts:113` filtra `arm64-v8a` e `armeabi-v7a`. HEAD `738499f54ecb45f65bb8b0c3ecc9f22c7c92f85f`. | NOVA |
| ouchao6122/APatch | 2 | 77 | strings, manifest, gradle | Subjects do compare. | sem feature nova |
| Hotsteel2901/APatch-Win98-Style | 2 | 9 | Tema Win98 e remoção de checagem de assinatura | Subjects do compare. | sem feature nova |
| gitclone-url/APatch | 2 | 3 | Tela About | Subject. | sem feature nova |
| Tools-cx-app/APatch | 2 | 1 | `AP_MODULE` em scripts de módulo | Subject `apd: fix some module scripts won't add AP_MODULE environment var`. | na-matriz (linha 14, ajuste do contrato) |
| AlexLiuDev233/APatch | 2 | 1 | Texto em DPI alto | Subject. | sem feature nova |
| ThePedroo/APatch | 1 | 1 | Acompanha `kstorage` read/write do KP | Subject. O pin local já chama `sc_kstorage_write` em `apd/src/supercall.rs:74`. | na-matriz |
| alwayslovestudy/KernelPatch | 2 | 24 | KPM `kpm-detector-bypass`: redirect de path e filtro de binder | `kpms/detector-bypass/main.c:18` `KPM_NAME`. `redirect.h:5-6`. `binder_filter.h:3-4`. HEAD `2fd0015fcfe2f207d18e34ca454e66ede863868a`. | NOVA |
| matcxz/MKernelPatch | 2 | 13 | kpimg e hook ARMv7 | `README.md:18`. `kernel/base/hook_arm.c:3-7` sob `#ifndef __aarch64__`. HEAD `bba0cdd54439b04fc8dfbfa1d0bd6166bd0218b1`. O pin local não tem `kernel/base/arm`. | NOVA |
| xiameng666/KernelPatch | 1 | 22 | KPMs `wxjump`, `wxshadow`, `kdbg`, `kdbg-probe`, `xiam-hide`, `xiam-tools` | `kpms/wxjump/wxjump.c:3` e `:38`. `kpms/wxshadow/wxshadow.c:3` e `:40`. `kpms/kdbg/kdbg.c:3` e `:45`. `xiam_hide.c:23`. HEAD `34cd202d2e8c7e2c4b1b76c1fead7bcd01458d7c`. | NOVA |
| Kahllo/KernelPatch | 1 | 12 | Só subjects `Update syscallhook.c` | Compare, arquivos em `kpms/`. Diff não aberto. | [não-verificado] |
| enrikenavarrete1986-cmyk/KernelPatch | 1 | 12 | `build.yml` | Subjects. | sem feature nova |
| AarifZ/KernelPatch | 1 | 11 | Rename citado para `kpm-hide-lineage.c` | Subjects do compare. Corpo não lido. | [não-verificado] |
| lukasz114/KernelPatch | 1 | 3 | Templates de issue e CI | Subjects. | sem feature nova |
| ThePedroo/KernelPatch | 1 | 2 | uid 0 não passa pela auth da superkey | Subject `allow uid 0 bypass key auth`, arquivo `kernel`. | na-matriz (linha 13, relaxa `auth_superkey`; não é feature nova) |
| SharkFall/KernelPatch | 1 | 2 | Corrige compile de `is_trusted_manager_uid_android` | Subject. | sem feature nova |
| RyzinRiley/KernelPatch | 1 | 2 | README e `gen.sh` | Subjects. `gen.sh` não aberto. | [não-verificado] |
| gitayane/KernelPatch | 1 | 2 | CI | Subjects. | sem feature nova |
| ColdWindScholar/KernelPatch | 1 | 2 | Documentação | Subjects. | sem feature nova |
| NepXIN/KernelPatch | 4 | 1 | `disabled simd.` | Subject, arquivo `kernel`. | sem feature nova |
| x22616546/KernelPatchnext | 0 | 1 | `Update userd.c` | Subject. Diff não aberto. | [não-verificado] |
| nzcv/KPNext | 0 | 1 | `[TEST1]disable perpare` | Subject. | sem feature nova |
| strongoak/KernelPatch | 1 | 1 | `Create main.yml` | Subject. | sem feature nova |
| ahahaber/KernelPatch | 1 | 1 | `build.yml` | Subject. | sem feature nova |

`5ec1cff/KernelPatch` (6 estrelas), `killvxk/KernelPatch` (5), `ShirkNeko/KernelPatch` (2), `yzy16384/KernelPatch-KPM` (0) e `NextGenSU/KernelPatch` (0) estão behind, `ahead_by` 0.

## TOP para absorver

Três, nesta ordem. O alvo C75 é arm64; o porte v7 não destrava esse aparelho.

1. Offset de struct por BTF (`KernelPatchUltra`, `btf_get_member_offset`). O kpimg deixa de depender de offset chutado quando o kernel tem BTF.
2. Umount por lista de paths (`xm999n`, `run_custom_umount`). É userspace, um arquivo de `stage:path`. A linha 21 da matriz é umount por uid no kernel, e no APatch essa célula é AUSENTE. Não é a mesma coisa.
3. ARMv7 no kpimg (`MKernelPatch`) junto com o manager `armeabi-v7a` (`MPatch`), se um dia houver alvo 32-bit.

Fora dessa lista, de propósito: os KPMs `wxjump`, `wxshadow`, `kdbg`, `xiam-hide` e `kpm-detector-bypass`. Estão mapeados na tabela. São módulos de hook, debug e filtro, não contrato de manager.

O manager Miuix (`Prslc`) é UI. Não entra no núcleo.

## Aberto

- `HmnDev-Tech/BPatch`: sem ancestral comum. O README não mostra feature além do texto público do APatch.
- `hasandramali/APatch-hidden`, `Kahllo/KernelPatch`, `AarifZ/KernelPatch`, `x22616546/KernelPatchnext`, `RyzinRiley/KernelPatch`: ahead pequeno e o diff não foi aberto.
- Forks com 0 estrelas fora dos quatro nomes extras não foram comparados. São centenas de clones.
