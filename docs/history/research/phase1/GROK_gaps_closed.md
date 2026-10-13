# GROK — lacunas fechadas (rodada 2)

Data: 2026-10-10. Matriz atualizada: `GROK_matriz_features.md`. Pins iguais aos da rodada 1. Nenhuma célula já decidida foi reaberta. Denominador: 53 células que estavam `NÃO-VERIFICÁVEL` na grade. Resultado: 53/53 fechadas. A grade não tem mais essa tag em célula.

## Alpha

O APK `[artefato local, fora do repo]/grok/alpha/app-release.apk` abriu no REA.

- Evidência: `ev_f2b95681edc8305aa24c64ff0590948df99394994a65317986482b5e8b81f2f4`
- JSON: `[artefato local, fora do repo]/grok/rea/alpha-pkg.json`
- Pacote `io.github.vvb2060.magisk`, `version_name` `96221b69-alpha`, `version_code` `31000`, minSdk 23, targetSdk 37
- ABIs no zip: `arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`
- `lib/x86_64/libmagiskboot.so` 1046880 bytes: `ANDROID!` e `truncate` presentes, `/data/magiskinit` ausente
- `lib/x86_64/libmagisk.so` 434976 bytes: `zygisk`, `zygisk_companion_entry`, `denylist` presentes
- Ghidra não foi reexecutado nesse ELF

Células Alpha que estavam abertas:

| Feature | Era | Ficou | Evidência |
|---|---|---|---|
| 8 redirect de `su` | NÃO-VERIFICÁVEL | ACOPLADA | `MagiskAlpha/native/src/core/su/su.cpp:210` `connect_daemon(RequestCode::SUPERUSER)` |
| 15 `webroot` + `action.sh` | NÃO-VERIFICÁVEL | PORTÁVEL | `ActionViewModel.kt:46` e `LocalModule.kt:39` executam `action.sh`. A string `webroot` não aparece em `*.{kt,rs,cpp,h,sh,java}` do tree Alpha |
| 30 patcher de boot.img | NÃO-VERIFICÁVEL | PORTÁVEL | `.so` acima. Confiança Média, mesma ressalva do `libmagiskboot.so` v30.7 |

A feature 15 do Magisk master fecha junto: `scripts/app_functions.sh:183`, `app/core/.../LocalModule.kt:39`, `app/apk/.../ActionViewModel.kt:46`. Busca de `webroot` no tree Magisk, mesmos globs: zero.

## Células fechadas

Caminhos relativos a `[artefato local, fora do repo]/grok/src/`.

### 3 — LSM `task_fix_setuid`

- bsxx: ACOPLADA. O setuid está no kernel, por hijack, sem chamada a `security_add_hooks`. `lsm_hooks_list.c:308` `LSM_HACK_INIT(task_fix_setuid, ksu_task_fix_setuid)`. `lsm_hooks_ultralegacy.c:345-346` grava `ops->task_fix_setuid`. `lsm_hooks_static.c:115-116` faz `arm64_bl_patch` de `security_task_fix_setuid`. Corpo manual em `lsm_hooks_manual.c:21`.

### 4 — LKM + late load

- Next: ACOPLADA. `kernel/core/init.c:116` `ksu_late_loaded = (current->pid != 1)`. `module_init` `:237` e `:239`. `MODULE_LICENSE("GPL")` `:243`.
- Suki: ACOPLADA. `init.c:117`, `module_init` `:237`/`:239`, licença `:243`.
- bsxx: ACOPLADA. `kernel/ksu.c:298` `kernelsu_lkm_init`. `:306-309` retorna 0 se `pid == 1`; senão o caminho “pid not 1, late load”. `module_init` `:346`. `MODULE_LICENSE("GPL")` `:350`. O built-in separado é `device_initcall` `:284` sob `#if !defined(MODULE)`.
- rs: AUSENTE. `kernel/ksu.c:135` `device_initcall(kernelsu_init)`. `MODULE_LICENSE` está dentro do comentário `:137-141`. Sem `module_init` e sem `ksu_late_loaded`.
- MKSU: ACOPLADA. `init.c:105`, `module_init` `:220`/`:222`, licença `:226`.

### 7 — `ksuinit` + `kernelsu.ko` no cpio

- Next: ACOPLADA. `boot_patch.rs` asset `ksuinit` `:708`/`:712`, `cpio.exists("kernelsu.ko")` `:731`, `cpio.add` `:738`.
- Suki: ACOPLADA. `:701`/`:705`, exists `:724`, add `:731`.
- rs: ACOPLADA. O installer fala com magiskboot: `exists kernelsu.ko` `:374`, `copy_assets_to_file("ksuinit"` `:650`, `add 0755 kernelsu.ko kernelsu.ko` `:702`.
- MKSU: ACOPLADA. `:688`/`:692`, exists `:711`, add `:718`.

bsxx continua ACOPLADA pela identidade de arquivo com o KSU oficial (linhas 746/750/776). Não reaberto.

### 8 — redirect de `/system/bin/su`

- Next: ACOPLADA. `sucompat.c:31` `SU_PATH "/system/bin/su"`, `:209` `filp_open(KSUD_PATH, O_PATH, 0)`, `:227` `AT_EMPTY_PATH`.
- bsxx: ACOPLADA. `sucompat.c:7` `SU_PATH`, `:237` `kern_path("/data/adb/ksud")`, `:242` `*filename_user = ksud_user_path()`. Sem `AT_EMPTY_PATH` nesse trecho.
- rs: ACOPLADA. `sucompat.c:1` `SU_PATH`, `:93` `kern_path(ksud_path)`, `:98` rewrite para `ksud_user_path()`.
- MKSU: ACOPLADA. `sucompat.c:30` `SU_PATH`, `:205` `filp_open`, `:223` `AT_EMPTY_PATH`.
- Alpha: ACOPLADA. Ver tabela Alpha.

### 9 — allowlist de uid no kernel

- Next: ACOPLADA. `sucompat.c:97`, `:133`, `:183` `ksu_is_allow_uid_for_current`.
- bsxx: ACOPLADA. `is_su_allowed` em `sucompat.c:84`. Uid 0 exige `is_ksu_domain` (`:108-113`). Default: só uid 2000 cai em `__ksu_is_allow_uid_copy` (`:134-142`). Variantes `CONFIG_KSU_ENABLE_FULL_UID_CHECKS` e `CONFIG_KSU_SHELL_HAS_SU_ALWAYS`.
- MKSU: ACOPLADA. `sucompat.c:96`, `:131`, `:179`.

### 12 — `reboot(magic)` + ioctl `'K'`

- Next: ACOPLADA. `uapi/supercall.h:16-17` `0xDEADBEEF` / `0xCAFEBABE`. `kernel/supercall/supercall.c:132`. `ksucalls.rs:127`.
- Suki: ACOPLADA. header `:16-17`, `supercall.c:123`, `ksucalls.rs:128`.
- rs: ACOPLADA. `kernel/include/uapi/supercall.h:16-17`, `supercall.c:83`, `ksucalls.rs:37`.
- MKSU: ACOPLADA. `uapi/supercall.h:13-14`, `supercall.c:86`, `ksucalls.rs:39`.

### 14, 15, 17 — contrato de módulo

- rs: PORTÁVEL nas três. `userspace/ksud/src/defs.rs:23` `MODULE_DIR` `/data/adb/modules/`, `:27-28` `webroot` e `action.sh`, `:29-31` `disable` / `update` / `remove`, `:39` `metamount.sh`. `metamodule.rs:244` `exec_mount_script`.
- MKSU: PORTÁVEL nas três. `defs.rs:22` `MODULE_DIR`, `:32-36` `webroot`, `action.sh`, `disable`, `update`, `remove`, `:45` `metamount.sh`. `metamodule.rs:255`.
- Magisk e Alpha, feature 15: PORTÁVEL só para `action.sh`. `webroot` ausente. Ver tabela Alpha.

### 16 e 18 — magic mount e overlayfs no APatch

- 16 KP: AUSENTE. `APatch/apd/src/module.rs` tem 799 linhas e zero ocorrências de `bind_mount`, `tmpfs`, `overlay`, `replace`, `metamount`.
- 18 KP: AUSENTE como implementação. O mount do APatch é delegação: `apd/src/metamodule.rs:260` `exec_mount_script`.

### 21 — umount por uid

- Next: ACOPLADA. `kernel/feature/kernel_umount.c:75` `ksu_handle_umount`. `:97-98` retorna 0 quando `!ksu_uid_should_umount(new_uid) && !is_isolated_process(new_uid)`.
- bsxx: ACOPLADA. `kernel_umount.c:76` e a mesma saída em `:98-99`.

### 27 — `selinux_hide`

- Suki: ACOPLADA. `selinux_hide.c:472-474` `.name = "selinux_hide"`. `ksu_selinux_hide_init` `:520`.
- bsxx: ACOPLADA. `:356` o mesmo nome. `ksu_selinux_hide_init` `:361`.
- MKSU: ACOPLADA. `:452`. init `:498`.

### 28 — assinatura do manager no kernel

- Next: ACOPLADA. `apk_sign.c:331-333` `check_v2_signature(path, EXPECTED_MANAGER_SIZE, EXPECTED_MANAGER_HASH)`. `Kbuild:140` size `0x3e6`, `:144` hash `79e590113c4c4c0c222978e413a5faa801666957b1212a328e46c00c69821bf7`, flags `:155-156`.
- Suki: ACOPLADA. `apk_sign.c:339`, check `:353`, segundo cert opcional `:356-357`. `Kbuild:136` size `0x35c`, `:140` hash `947ae944f3de4ed4c21a7e4f7953ecf351bfa2b36239da37a34111ad29993eef`.
- bsxx: ACOPLADA. `apk_sign.c:323` size `0x363` hash `4359c171f32543394cbc23ef908c4bb94cad7c8087002ba164c8230948c21549`; `:327` `EXPECTED_SIZE`/`EXPECTED_HASH`; `:331` size `0x375` hash `484fcba6e6c43b1fb09700633bf2fb4758f13cb0b2f4457b80d075084b26c588`. `kernel/Makefile:17` pacote `me.weishu.kernelsu`, `:18-19` size `0x033b` e o hash oficial `c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6`.
- rs: ACOPLADA. `apk_sign.c:164` `check_v2_signature(char *path)` abre o arquivo e caminha o EOCD. `is_manager_apk` `:340-355` pode comparar `KSU_MANAGER_PACKAGE` e chama `check_v2_signature(path)`.
- MKSU: ACOPLADA. `apk_sign.c:352`, check `:366`, segundo cert `:369-370`. `Kbuild:131` size `0x033b`, `:135` hash oficial.
- KP: AUSENTE nesta linha. `kernel/patch/android/userd.c:1151-1157` `is_trusted_manager_uid_android` compara `uid == trusted_manager_uid` e devolve 0 se o uid confiável é inválido. Chamado em `supercall.c:413`. Declaração em `userd.h:19`. É confiança por uid. O superkey continua na linha 13, já ACOPLADA.

### 29 — resetprop

- rs: PORTÁVEL. `resetprop.rs:26-31` nome `"resetprop"`, “Magisk-compatible”. `resetprop_main` `:97`.
- MKSU: PORTÁVEL. `:26-31` e `resetprop_main` `:105`.
- KP: PORTÁVEL. `APatch/apd/src/resetprop.rs:26-31` e `:105`.

### 30 — patcher de boot.img como ferramenta

- KSU, Next, Suki, bsxx, rs, MKSU: ACOPLADA. O corpo é o installer da linha 7. O rs usa o CLI do magiskboot e mesmo assim grava `ksuinit` e `kernelsu.ko`. Não há API de unpack separada desse installer.
- Alpha: PORTÁVEL, confiança Média. Ver tabela Alpha.
- Magisk master permanece PORTÁVEL (já decidido; Ghidra do v30.7 estourou 300 s).

## susfs

SukiSU-Ultra e KernelSU-Next integram só o cliente userspace. Next manda `syscall(SYS_reboot, KSU_INSTALL_MAGIC1, 0xFAFAFAFA, CMD, …)` em `susfsd.rs:44`, `:65` e `:86`, e em `:111` diz para ligar a operação no kernel. Suki despacha o mesmo protocolo em `cli.rs:1173-1194`: versão/features, `set_uname`, log, spoof de AVC, cmdline/bootconfig, esconder mounts para processos sem su, redirect de open, e marca de path/map/kstat. Nenhum checkout tem arquivo `*susfs*` dentro de `kernel/` (contagem 0, inclusive KernelPatch). A dependência externa está nomeada pela própria SukiSU em `docs/README.md:90`: `https://gitlab.com/simonpunk/susfs4ksu`, descrita como addon de patches de kernel e módulo userspace para esconder root. A integração que a documentação manda rodar é um branch externo (`susfs-main`), em `website/docs/en/guide/installation.md:76` e `website/docs/zh/guide/how-to-integrate.md:92`. O tree do KernelSU-Next não contém essa URL. O repositório do susfs não foi clonado, nem um kernel GKI. O cliente é fonte copiável. O efeito depende do patch de kernel externo. Levar o CLI para o core unificado não entrega a feature. Não é portável como feature funcionando.

## O que permanece impreciso

Nenhuma célula da grade ficou aberta. Quatro ressalvas:

- rsuntk: o literal do digest dentro de `check_v2_signature` depois da linha 193 não foi extraído. O veredito ACOPLADA apoia-se no parser in-kernel, não num hash copiado.
- Magisk e Alpha, feature 15: `action.sh` está no código. A string `webroot` não está nesses trees.
- KernelPatch, feature 28: a checagem lida é igualdade de uid, não assinatura de APK.
- backslashxx, feature 3: o hook de `task_fix_setuid` existe e está acoplado ao kernel. A API com o nome `security_add_hooks` não é a que esse tree chama.

Corpos de método Java (`isSuEnabled`, `Natives.su`, `SuPolicy`) continuam indisponíveis desde a rodada 1. Não eram células desta grade.

## Scratch

`[artefato local, fora do repo]/grok` ficou em 321 MB. `tmp/` e `decompile/` já estavam vazios (0 bytes); não havia lixo de Ghidra para apagar. Ficam `src/` (155 MB), `bin/` (115 MB, APKs e nativos citados), `tools/jadx-headless-mcp-0.7.1-all.jar` (41 MB), `alpha/` (11 MB), `rea/` e `notes/`. Nada foi escrito em `/tmp`.
