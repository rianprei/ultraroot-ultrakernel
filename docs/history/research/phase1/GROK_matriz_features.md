# GROK — matriz feature × motor

Critério e pins: `GROK_dossie.md`. Célula: `[PORTÁVEL]` contrato separável, `[ACOPLADA]` presa ao núcleo indicado, `[AUSENTE]` sem arquivo/símbolo neste checkout, `[NÃO-VERIFICÁVEL]` quando o corpo que fecharia o veredito não foi lido.

Colunas: **KSU** tiann `6e386044` · **Next** `cd4a6b46` · **Suki** `223897c7` · **bsxx** backslashxx `920ae322` · **rs** rsuntk `648e5988` · **MKSU** 5ec1cff `6a4cac03` · **Magisk** master `e8915d9d` (binário REA = APK v30.7) · **Alpha** `96221b69` (APK 31000 aberto, ver Rodada 2) · **KP** APatch `e78e7b3e` + KernelPatch `f36f3d13`.

Rodada 2 (2026-10-10) fechou as 53 células que estavam NÃO-VERIFICÁVEL. A tabela e a seção Rodada 2 vencem qualquer frase antiga que ainda fale em corpo não lido. Delta: `GROK_gaps_closed.md`.

Hash: “=KSU” / “=Magisk” significa sha256 do arquivo inteiro igual ao oficial citado. Linhas transferem. Hash diferente + símbolo rebuscado = o símbolo vale, o resto do arquivo não.

30 features × 9 motores.

## Tabela

| # | Feature | KSU | Next | Suki | bsxx | rs | MKSU | Magisk | Alpha | KP | Melhor origem |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 1 | Tracepoint `sys_enter` + kretprobe | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 2 | Troca manual de `sys_call_table` | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 3 | LSM `security_add_hooks(task_fix_setuid)` | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 4 | LKM + late load (`pid != 1`) | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | ACOPLADA | AUSENTE | AUSENTE | ACOPLADA | não extrair |
| 5 | `kpimg` (patch estático da imagem) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | não extrair |
| 6 | Ramdisk `magiskinit` / proxy de init | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair |
| 7 | Ramdisk `ksuinit` + `kernelsu.ko` no cpio | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 8 | Redirect de `/system/bin/su` | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | não extrair |
| 9 | Allowlist de uid no kernel | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | ACOPLADA | não extrair |
| 10 | Policy su em sqlite (`magisk.db`) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair |
| 11 | Daemon por socket unix | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair |
| 12 | IPC `reboot(magic)` + ioctl `'K'` | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 13 | Superkey (`auth_superkey`) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | não extrair |
| 14 | Contrato `module.prop` + disable/remove/update | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | contrato de arquivos (KSU `defs.rs` e APatch `defs.rs`) |
| 15 | `webroot` + `action.sh` | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | webroot+action.sh na família KSU e no APatch; Magisk e Alpha só `action.sh` |
| 16 | Magic mount (tmpfs + bind + `.replace`) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair; algoritmo só no Magisk |
| 17 | Mount delegado a `metamount.sh` | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | AUSENTE | AUSENTE | PORTÁVEL | nomes de script; o efeito depende do script externo |
| 18 | Overlayfs implementado no repo | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 19 | Loader KPM | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | não extrair; loader real só no KernelPatch |
| 20 | Zygisk (JNI do zygote) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair |
| 21 | Umount de módulo por uid no kernel | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 22 | DenyList no daemon (`magisk --denylist`) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | não extrair |
| 23 | Uid-exclude de módulo (KernelPatch) | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | ACOPLADA | não extrair |
| 24 | SuList / MagiskHide vivo | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | morto; sqlite apaga `magiskhide` |
| 25 | Cliente susfs (userspace) | AUSENTE | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | não extrair sem o kernel externo |
| 26 | susfs no `kernel/` do repo | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | AUSENTE | não está nestes clones |
| 27 | `selinux_hide` / domínio SELinux do motor | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | não extrair |
| 28 | Check in-kernel da assinatura do manager | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | AUSENTE | AUSENTE | AUSENTE | não extrair |
| 29 | `resetprop` applet / CLI | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | PORTÁVEL | CLI compatível; não precisa do gancho de um motor |
| 30 | Patcher de boot.img como ferramenta | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | ACOPLADA | PORTÁVEL | PORTÁVEL | ACOPLADA | `libmagiskboot.so` nos APKs; família KSU é o installer |

As tags da tabela são os vereditos `[ACOPLADA]`, `[AUSENTE]`, `[PORTÁVEL]`, `[NÃO-VERIFICÁVEL]`, escritos sem colchetes para caber na grade. A evidência abaixo usa a forma do contrato.

## Evidência por linha

### 1. Tracepoint + kretprobe — `[ACOPLADA]` ao driver KSU

KSU `kernel/hook/syscall_hook_manager.c:38` `register_kretprobe`, `:146` `register_trace_prio_sys_enter(ksu_sys_enter_handler, NULL, INT_MIN)`. Suki: arquivo =KSU, mesma linha. Next: mesma chamada na linha 146, hash diferente. MKSU: mesma chamada na linha 142, hash diferente. bsxx e rs não têm o arquivo. Magisk, Alpha e KP não têm o arquivo. Preso ao `.ko` / built-in que registra o tracepoint. Não é uma biblioteca chamável pelo APK.

### 2. `sys_call_table` — `[ACOPLADA]` ao kernel antigo que o backslashxx escreve

`backslashxx-KernelSU/kernel/hook/syscall_table_hook_arm64.c:362-406` `read_and_replace_syscall` em `sys_call_table` e `compat_sys_call_table`. Cabeçalho `:5-6` declara fork não afiliado. Também `syscall_table_hook_arm.c:236`. Nenhum outro clone tem esse arquivo. A busca de `read_and_replace_syscall` só acertou esses dois. Preso à tabela de syscall daquele kernel (comentários apontam unistd 4.14, linhas 18-20).

### 3. LSM `task_fix_setuid` — `[ACOPLADA]` ao `security_add_hooks` do rsuntk

`rsuntk-KernelSU/kernel/hook/lsm_hook.c:46-61`. Único `lsm_hook.c` dos clones que contém `security_add_hooks`. O `lsm_hook.c` do KSU oficial é outro programa: patch de slot com `ksu_patch_text` (`:69`), sem `security_add_hooks`. Suki e MKSU: esse arquivo =KSU, então também não registram por `security_add_hooks`. Next: hash diferente e a string `security_add_hooks` não está no `lsm_hook.c` dele. bsxx não chama `security_add_hooks`. O setuid está preso ao kernel por três hijacks: `lsm_hooks_list.c:308` `LSM_HACK_INIT(task_fix_setuid, ksu_task_fix_setuid)`, `lsm_hooks_ultralegacy.c:345-346` troca `ops->task_fix_setuid`, `lsm_hooks_static.c:115-116` `arm64_bl_patch` de `security_task_fix_setuid`. Corpo em `lsm_hooks_manual.c:21`. `[ACOPLADA]`.

### 4. LKM + late load

KSU `kernel/core/init.c:111` `ksu_late_loaded = (current->pid != 1)` sob `#ifdef MODULE`, e `:227-236` `module_init` / `MODULE_LICENSE("GPL")`. `[ACOPLADA]` ao `.ko`. Next `init.c:116` e `module_init` `:237`/`:239`. Suki `init.c:117`, mesmos `module_init`. MKSU `init.c:105`, `module_init` `:220`/`:222`. bsxx não tem `kernel/core/init.c`; o LKM está em `kernel/ksu.c:298` `kernelsu_lkm_init`, late load quando `current->pid != 1` (`:306-309`), `module_init` `:346`, `MODULE_LICENSE("GPL")` `:350`. `[ACOPLADA]`. rs `kernel/ksu.c:135` é `device_initcall(kernelsu_init)`; `MODULE_LICENSE` está comentado em `:137-141`. Sem `module_init` e sem `ksu_late_loaded` → `[AUSENTE]` como LKM late-load (built-in só). Magisk e Alpha não são LKM → `[AUSENTE]`. KP entrega `.ko` de release (`kernelpatch.ko` na release 0.13.9, não traçado) e o fonte de módulo está em `kernel/patch/`; o flag `pid != 1` do KSU não se aplica. `[ACOPLADA]` ao patch KernelPatch, não ao late-load do KSU.

### 5. kpimg — `[ACOPLADA]` ao KernelPatch

REA `kptools-linux` `ev_9950c3b6bff37b75f89652de3d3678c90424c6d1e21125352c80033ceb5f2467`: `parse_image_patch_info` @ `0x10c54d` referencia erros de `kpimg`. O blob `kpimg-android` contém `superkey` e `/system/bin/su`. Os outros motores não têm `kptools` nem esse blob. Injetar kpimg é o mecanismo de root, não um utilitário destacável.

### 6. magiskinit — `[ACOPLADA]` ao boot do Magisk

Master `native/src/init/init.hpp:4` `REDIR_PATH "/data/magiskinit"`. `init.rs:100` e `:191`. `rootdir.cpp:374` copia esse path para `/sbin/magisk`. REA do `libmagiskinit.so` v30.7 `ev_5ffece444f9b8653d5ef0ba9b81ac6a6082816601af671264392fc34864c273b`: a string sai da função de entrada `FUN_0021775d`. Alpha não teve o diretório `init/` na comparação de hash. O veredito `[ACOPLADA]` na Alpha apoia-se no fato de o binário de init ser o mecanismo do Magisk e de `hook.cpp` / `module.rs` serem byte-idênticos; o `init/` da Alpha em si não foi aberto → a célula da Alpha fica honesta como `[ACOPLADA]` ao mesmo desenho só na medida em que o projeto é o Magisk. Confiança Média na Alpha, Alta no master e no binário v30.7.

### 7. ksuinit no cpio — `[ACOPLADA]` ao boot do KSU

`KernelSU/userspace/ksud/src/boot_patch.rs:746-776` grava `ksuinit` e `kernelsu.ko` e renomeia `init` → `init.real`. bsxx: arquivo =KSU (as mesmas linhas 746/750/776). Next: asset `ksuinit` `:708`/`:712`, `cpio.exists("kernelsu.ko")` `:731`, `cpio.add` `:738`. Suki: `:701`/`:705`, exists `:724`, add `:731`. MKSU: `:688`/`:692`, exists `:711`, add `:718`. rs chama magiskboot: `exists kernelsu.ko` `:374`, `copy_assets_to_file("ksuinit"` `:650`, `add 0755 kernelsu.ko` `:702`. `[ACOPLADA]` ao installer KSU nos quatro. Magisk não faz esse par de arquivos.

### 8. Redirect de `/system/bin/su`

KSU `kernel/feature/sucompat.c:31` `SU_PATH`, `:207` `filp_open(KSUD_PATH)`, `:225` `AT_EMPTY_PATH`, guardas `ksu_is_allow_uid_for_current` em `:97`, `:132`, `:181`. `KSUD_PATH` é `/data/adb/ksud` (`runtime/ksud.h:6`). `[ACOPLADA]` ao driver e à allowlist. Suki: `sucompat.c` =KSU. Next: `SU_PATH` `:31`, `filp_open(KSUD_PATH)` `:209`, `AT_EMPTY_PATH` `:227`, allow em `:97`/`:133`/`:183`. MKSU: `SU_PATH` `:30`, `filp_open` `:205`, `AT_EMPTY_PATH` `:223`, allow em `:96`/`:131`/`:179`. bsxx: `SU_PATH` `:7`, `kern_path("/data/adb/ksud")` `:237`, rewrite `*filename_user = ksud_user_path()` `:242`. rs: `SU_PATH` `:1`, `kern_path(ksud_path)` `:93`, rewrite `:98`. `[ACOPLADA]` nos quatro.

Magisk: o `su` é applet (`consts.rs:8`) e fala com o daemon (`su.cpp:212`). `[ACOPLADA]` ao magiskd. Alpha `su.cpp` tem outro hash; a chamada está em `MagiskAlpha/native/src/core/su/su.cpp:210` `connect_daemon(RequestCode::SUPERUSER)`. `[ACOPLADA]`. `consts.rs` da Alpha =Magisk, então a lista de applets inclui `su`.

KP: macros `SU_PATH "/system/bin/kp"` e `LEGACY_SU_PATH "/system/bin/su"` em `scdefs.h:96-97`, daemon `APD_PATH "/data/adb/apd"` `:101`. O corpo que reescreve o exec no `sucompat.c` do KernelPatch não foi citado nesta passagem. O que está provado é o contrato de path mais o supercall. Célula `[ACOPLADA]` ao hook de supercall, confiança Alta nos macros, Média no rewrite de exec.

### 9. Allowlist de uid no kernel

KSU: as guardas do item 8. Suki =KSU. rsuntk `lsm_hook.c:37` chama `ksu_is_allow_uid_for_current(new_uid)` antes do umount. `[ACOPLADA]`. KP: `is_su_allow_uid` em `supercall.c:438`. `[ACOPLADA]`. Magisk guarda policy em sqlite, não numa allowlist de kernel → `[AUSENTE]` nesta linha (está na 10). Next, bsxx e MKSU: as guardas do item 8 (`ksu_is_allow_uid_for_current` ou `is_su_allowed`). bsxx `sucompat.c:108-142`: uid 0 exige `is_ksu_domain`; no caminho default só uid 2000 cai em `__ksu_is_allow_uid_copy`, com variantes `CONFIG_KSU_ENABLE_FULL_UID_CHECKS` e `CONFIG_KSU_SHELL_HAS_SU_ALWAYS`. `[ACOPLADA]`.

### 10. `magisk.db` — `[ACOPLADA]` ao magiskd

`consts.rs:23` no master e, por hash, na Alpha. Família KSU e KP não definem `MAGISKDB` nos arquivos lidos.

### 11. Socket unix — `[ACOPLADA]` ao magiskd

`daemon.rs:411` `UnixListener::bind` no path `get_magisk_tmp()+MAIN_SOCKET` (`consts.rs:33`, `utils.cpp:37-48`). Alpha `daemon.rs:408` também dá bind; o arquivo difere. KSU usa ioctl (linha 12), KP usa supercall (linha 13). Nenhum dos dois mostrou `UnixListener` nos arquivos lidos.

### 12. reboot magic + ioctl `'K'` — `[ACOPLADA]` ao driver KSU

Oficial: magics `uapi/supercall.h:16-17`, instalação no kprobe `kernel/supercall/supercall.c:122`, cliente `ksucalls.rs:125-131`, ioctls `:161-187`. bsxx: `uapi/supercall.h` =KSU, e o reboot é interceptado por troca de entrada na tabela (`syscall_table_hook_arm64.c:399`). Next `uapi/supercall.h:16-17` `0xDEADBEEF`/`0xCAFEBABE`, compare em `supercall.c:132`, cliente `ksucalls.rs:127`. Suki: header `:16-17`, `supercall.c:123`, `ksucalls.rs:128`. rs: `kernel/include/uapi/supercall.h:16-17`, `supercall.c:83`, `ksucalls.rs:37`. MKSU: `uapi/supercall.h:13-14`, `supercall.c:86`, `ksucalls.rs:39`. `[ACOPLADA]` nos quatro. Magisk e KP não usam esse par.

### 13. Superkey — `[ACOPLADA]` ao KernelPatch

`predata.c:43-80` `auth_superkey` (XOR, depois SHA-256 se `enable_root_key`). `predata.c:345-347` `has_preset_superkey`. `supercall.c:421-458` o gate. JNI `apjni.cpp:16-23` e `:44-54`. REA `libkpatch.so` `ev_3e9c4ef0ba7b4d80993347e154f16379c57a0c9cf9716e6e7a69f828bf58b752`. O parâmetro `superkey` de `SukiSU/.../module.rs:975` é argumento de Lua, não esta função. `[AUSENTE]` na SukiSU como autenticação.

### 14 e 15. Contrato de módulo e WebUI — `[PORTÁVEL]`

KSU `userspace/ksud/src/defs.rs:29-54`: `/data/adb/modules/`, `webroot`, `action.sh`, `disable`, `update`, `remove`. Suki e bsxx: `defs.rs` =KSU. Next: o arquivo difere no hash; as linhas 30-45 e 53-55 foram lidas e repetem `MODULE_DIR`, `webroot`, `action.sh`, `disable`, `update`, `remove`, `metamount.sh`, `metainstall.sh`, `metauninstall.sh`. `[PORTÁVEL]` no Next com essa leitura. rs `defs.rs:23` `MODULE_DIR`, `:27-31` `webroot` / `action.sh` / `disable` / `update` / `remove`, `:39` `metamount.sh`. MKSU `defs.rs:22` `MODULE_DIR`, `:32-36` os mesmos cinco nomes, `:45` `metamount.sh`. `[PORTÁVEL]` nas linhas 14, 15 e 17. O chamador rs está em `metamodule.rs:244`; MKSU em `:255`. Os dois devolvem Ok se o script falta e senão rodam `busybox sh`.

APatch `apd/src/defs.rs:12-30`: mesmos nomes `webroot`, `action.sh`, `disable`, `update`, `remove`, mais os três scripts de metamódulo e `/data/adb/modules/`. `[PORTÁVEL]`. (Alta)

Magisk `consts.rs:20` `MODULEROOT /data/adb/modules`, e a Alpha =Magisk nesse arquivo. `[PORTÁVEL]` o diretório. A string `webroot` não aparece em `*.{kt,rs,cpp,h,sh,java}` do tree Magisk nem do tree Alpha. `action.sh` existe: Magisk `scripts/app_functions.sh:183` `sh ./action.sh`, `LocalModule.kt:39`, `ActionViewModel.kt:46`. Alpha repete os dois Kotlin nas mesmas linhas (`app/core/.../LocalModule.kt:39`, `app/apk/.../ActionViewModel.kt:46`). Célula 15 Magisk e Alpha: `[PORTÁVEL]` para `action.sh`. O nome `webroot` está ausente nesses dois trees.

Parser: `KernelSU/.../module.rs:901` `pub fn read_module_prop`. Corpo não citado.

### 16. Magic mount — `[ACOPLADA]` ao namespace de boot do Magisk

`native/src/core/module.rs:39` `bind_mount`, `:266` `parent_should_be_tmpfs`, `:293` `.replace`, `:338` `commit_tmpfs`. Alpha: arquivo =Magisk. Nenhum clone KSU tem `native/src/core/module.rs`. KP: `apd/src/module.rs` (799 linhas) não contém `bind_mount`, `tmpfs`, `overlay`, `.replace` nem `metamount`. `[AUSENTE]` na linha 16. O desenho lido no KSU é delegação (linha 17), e o APatch copia os nomes dessa delegação.

### 17. `metamount.sh` — contrato `[PORTÁVEL]`, efeito no script externo

`metamodule.rs:254-257`: sem o script, `Ok(())`. Com o script, `busybox sh`. Arquivo =KSU em Next, Suki e bsxx. APatch declara o mesmo nome em `defs.rs:27`. O script em si não está no core. Copiar o chamador sem o script não monta nada. O chamador é portável; a feature de mount não vem junto. Magisk não usa esse nome nos arquivos lidos → `[AUSENTE]`.

### 18. Overlayfs no repo — `[AUSENTE]` na família KSU

Nenhum arquivo `*overlay*` em KernelSU-Next. O core oficial só chama o script. Não abri um `meta-overlayfs` porque ele não está em `userspace/` destes checkouts. KP: a busca em `apd/src/module.rs` não achou `overlay`. O mount do APatch é `apd/src/metamodule.rs:260` `exec_mount_script`. `[AUSENTE]` como implementação no repo.

### 19. KPM — `[ACOPLADA]` ao KernelPatch; SukiSU `[AUSENTE]` como loader

`KernelPatch/kernel/patch/module/module.c:558` `load_module`, `:613` `load_module_path`. SukiSU `kernel/kpm/kpm.c:54-121` stubs com `pr_info` e `nop`. Os outros clones não têm nenhum dos dois arquivos.

### 20. Zygisk — `[ACOPLADA]` ao `libmagisk` / magiskd

Master `zygisk/hook.cpp:548` `hook_zygote_jni`, `entry.cpp:41` `zygisk_companion_entry`. Alpha `hook.cpp` =Magisk. REA v30.7 `libmagisk.so` `ev_d72b81c0fccf8027e5528bcc09fda043f02659e029523aa2921121b70aae9ba2`: strings `zygisk`, `zygisk_companion_entry`, `zygisk_module_entry`, `zygiskd64`. Busca `zygisk` no clone tiann/KernelSU: nenhum arquivo. Os outros motores não têm `zygisk/hook.cpp`.

### 21. Umount por uid — `[ACOPLADA]` ao driver

UAPI oficial `KSU_IOCTL_UID_SHOULD_UMOUNT` em `uapi/supercall.h:175` (bsxx =KSU nesse header). Arquivo `kernel/feature/kernel_umount.c`: =KSU em Suki e MKSU; hash diferente em Next, bsxx e rs. rsuntk chama `ksu_handle_umount` em `lsm_hook.c:41`. Next `kernel_umount.c:75` `ksu_handle_umount`; `:97-98` sai cedo quando `!ksu_uid_should_umount(new_uid) && !is_isolated_process(new_uid)`. bsxx: a mesma saída em `:98`, função em `:76`. `[ACOPLADA]`. rsuntk já chamava `ksu_handle_umount` em `lsm_hook.c:41`. Magisk faz isso no daemon (linha 22), não neste arquivo.

### 22. DenyList do Magisk — `[ACOPLADA]` ao daemon

`deny/cli.cpp:14-21` e `:65`. Alpha: arquivo presente, hash diferente, CLI não relida; célula `[ACOPLADA]` com confiança Média. O binário v30.7 contém `zygisk64: [%s] is on the denylist` (REA da linha 20). KSU tem outro objeto, o ioctl `KSU_IOCTL_GET_DENY_LIST` (`uapi/supercall.h:171`), que é lista no driver, não esta CLI. Por isso a família KSU fica `[AUSENTE]` nesta linha e não na 21.

### 23. Uid-exclude KP — `[ACOPLADA]`

`supercall.c:421` e `apjni.cpp:62-73`. Só neste motor.

### 24. SuList / MagiskHide — `[AUSENTE]` nos vivos

Master e Alpha (`sqlite.cpp` =Magisk) linha 279: `DELETE FROM settings WHERE key='magiskhide'`. Kitsune está arquivado e não é coluna.

### 25 e 26. susfs

Cliente Next: `userspace/ksud/src/susfsd.rs:7` magic `0xFAFAFAFA`, reboot nas linhas 44, 65, 86. `:111` devolve erro pedindo para ligar o susfs no kernel. Cliente Suki: dispatch em `cli.rs:1173-1194` (`Features`, `SetUname`, `EnableLog`, `EnableAvcLogSpoofing`, `SetCmdlineOrBootconfig`, `HideSusMntsForNonSuProcs`, `AddOpenRedirect`, `AddSusMap`, `AddSusPath`, `AddSusPathLoop`, `AddSusKstat`). `[ACOPLADA]` a um kernel que responda a esse protocolo. Contagem de arquivos `*susfs*` em `kernel/` de todos os clones, inclusive KernelPatch: 0. Linha 26 `[AUSENTE]` em todo mundo. O checkout Next não cita URL. A SukiSU nomeia a dependência em `docs/README.md:90`: `https://gitlab.com/simonpunk/susfs4ksu`, “addon root hiding kernel patches and userspace module for KernelSU”. A receita de integração é branch externa (`susfs-main`), citada em `website/docs/en/guide/installation.md:76` e `website/docs/zh/guide/how-to-integrate.md:92`. Copiar o cliente para um APK unificado não entrega a feature. O kernel não foi clonado.

### 27. SELinux

KSU `selinux_hide.c:512` nome `"selinux_hide"`. Next: arquivo =KSU. Suki `selinux_hide.c:472-474` handler `.name = "selinux_hide"`, init `:520`. bsxx `:356` o mesmo nome, `ksu_selinux_hide_init` `:361`. MKSU `:452`, init `:498`. `[ACOPLADA]`. rs: sem o arquivo → `[AUSENTE]`.

Magisk: contextos `u:r:magisk:s0` e `magisk_file` em `consts.rs:41-45` (Alpha =Magisk). `[ACOPLADA]` ao domínio que o init carrega. Há `libmagiskpolicy.so` no APK, não traçado.

KP: `selinux_hide.h:13-14` e `user_event.c:37`. `[ACOPLADA]`. Também os contextos `u:r:kp:s0`, `u:r:magisk:s0`, `u:r:kernel:s0` em `scdefs.h:102-104`.

KSU ainda escreve linhas de rc que executam `KSUD_PATH` (`ksud_integration.c:39-48`). `[ACOPLADA]` ao boot.

### 28. Assinatura do manager no kernel — `[ACOPLADA]` ao `.ko` compilado com o hash

Oficial `apk_sign.c:326-328` e `Kbuild:138-139`: size `0x033b`, hash `c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6`, pacote `me.weishu.kernelsu`. Next `apk_sign.c:331-333` `check_v2_signature(path, EXPECTED_MANAGER_SIZE, EXPECTED_MANAGER_HASH)`. `Kbuild:139-156` default size `0x3e6`, hash `79e590113c4c4c0c222978e413a5faa801666957b1212a328e46c00c69821bf7`. Suki `apk_sign.c:339` / `:353`, e opcional `EXPECTED_SIZE2` em `:356-357`. `Kbuild:135-152` default size `0x35c`, hash `947ae944f3de4ed4c21a7e4f7953ecf351bfa2b36239da37a34111ad29993eef`. bsxx `apk_sign.c:320-331` aceita três certificados: size `0x363` hash `4359c171f32543394cbc23ef908c4bb94cad7c8087002ba164c8230948c21549`; `EXPECTED_SIZE`/`EXPECTED_HASH`; size `0x375` hash `484fcba6e6c43b1fb09700633bf2fb4758f13cb0b2f4457b80d075084b26c588`. `kernel/Makefile:17-27` pacote `me.weishu.kernelsu`, size `0x033b`, hash oficial `c371061b…`. MKSU `apk_sign.c:352` e check em `:366`, segundo cert opcional `:369-370`. `Kbuild:130-147` default size `0x033b`, hash oficial. rs `apk_sign.c:164` `check_v2_signature(char *path)` abre o APK e caminha o EOCD; `is_manager_apk` `:340-355` pode comparar `KSU_MANAGER_PACKAGE` e devolve `check_v2_signature(path)`. O literal do digest do rsuntk não foi extraído; o parser in-kernel existe. `[ACOPLADA]` nos cinco. Magisk não tem esse check nos arquivos lidos. KP: `userd.c:1151-1157` `is_trusted_manager_uid_android` devolve `uid == trusted_manager_uid` (0 se `TRUSTED_MANAGER_UID_INVALID`). Chamado em `supercall.c:413`. Isso é confiança por uid, sem hash de certificado → `[AUSENTE]` nesta linha.

### 29. resetprop — `[PORTÁVEL]` como CLI de propriedade

Magisk `resetprop/cli.rs:273` `resetprop_main`. `resetprop/mod.rs:3` exporta `get_prop` / `set_prop` / `load_prop_file`. Applet em `consts.rs:8`. Alpha: `cli.rs` e `consts.rs` =Magisk. KSU `resetprop.rs:26-29` “Magisk-compatible”, crate `prop_rs_android` na linha 5. Next, Suki, bsxx: arquivo =KSU. rs `resetprop.rs:26-31` nome `"resetprop"`, texto “Magisk-compatible”, `resetprop_main` `:97`. MKSU: as mesmas linhas 26-31 e `resetprop_main` `:105`. APatch `apd/src/resetprop.rs:26-31` e `:105`. `[PORTÁVEL]` nos três. O tool precisa de acesso ao property area. Não precisa do gancho de kernel de um motor específico para ser compilado dentro de um APK.

### 30. Patcher de boot.img como ferramenta

Magisk: `libmagiskboot.so` do APK v30.7 contém `ANDROID!` e `truncate` (varredura de bytes). É um ELF à parte, dentro do APK. Ghidra: `provider_timeout` 300 s, sem xref → confiança Média, célula `[PORTÁVEL]` como utilitário de imagem, com essa ressalva. O que o Magisk grava no ramdisk para se instalar é a linha 6, `[ACOPLADA]`. Alpha: o APK 31000 contém `lib/{arm64-v8a,armeabi-v7a,x86,x86_64}/libmagiskboot.so` (x86_64, 1046880 bytes). Varredura do membro x86_64: `ANDROID!` presente, `truncate` presente, `/data/magiskinit` ausente. `[PORTÁVEL]`, confiança Média, mesma ressalva do v30.7. Ghidra não foi reexecutado.

KSU: `boot_patch.rs` reescreve cpio em favor de `ksuinit`+`kernelsu.ko` (linha 7). O `ksud` de release, REA `ev_434ea8197425b57d609e10603ec21fe4651cb6d56e60db4848a7998ca415e17c`, contém `/data/adb/ksud` referenciado por `ksud::utils::install` e um script de installer que o próprio texto diz vir de `module_installer.sh` / `util_functions.sh` do Magisk, referenciado por `ksud::module::regenerate_preinit_rc`. Varredura de bytes também achou `ANDROID!` e `magiskinit`. Isso é um patcher embarcado. A parte que injeta `ksuinit`+`.ko` é `[ACOPLADA]` (linha 7). O corpo do patcher é o installer que injeta `ksuinit` e `kernelsu.ko` (linha 7), inclusive no rs, que só troca a API pelo CLI do magiskboot. `[ACOPLADA]` em KSU, Next, Suki, bsxx, rs e MKSU. Uma função “só unpack”, separada desse installer, não está isolada nesses trees.

KP: `kptools` parseia `kpimg` (linha 5). `[ACOPLADA]` a esse formato. Não é um magiskboot genérico.

## Contagem

- Features: 30
- Motores/colunas: 9 (KSU, Next, Suki, bsxx, rs, MKSU, Magisk, Alpha, KP)
- Células: 270, mais a coluna “melhor origem”
- Células NÃO-VERIFICÁVEL na grade, depois da rodada 2: 0. As 53 que estavam abertas fecharam. Conferência: `rg NÃO-VERIFICÁVEL` na tabela não acha tag de célula.
- `[PORTÁVEL]`: linhas 14 e 29 nas nove colunas; linha 15 nas nove, com `webroot` ausente no Magisk e na Alpha (só `action.sh`); linha 17 em KSU, Next, Suki, bsxx, rs, MKSU e KP; linha 30 em Magisk e Alpha, confiança Média.
- O restante acoplado está preso a driver, daemon ou boot. O restante ausente não está no checkout. Ressalvas de precisão (nenhuma célula aberta): digest literal do rsuntk não extraído; feature 28 do KP é uid; feature 3 do bsxx não chama `security_add_hooks`; `webroot` ausente no Magisk e na Alpha.

## Fora da grade

- Kitsune / MagiskHide vivo: arquivado, não é coluna. A linha 24 registra a ausência nos trees vivos.
- `rifsxd/KernelSU-Next`: fork pequeno do org, não clonado.
- ReZygisk e ZygiskNext: não são um dos três motores; não entrei no código deles.
- Checkout `[fora do repo]`: origin SuperRoot, não usado.
