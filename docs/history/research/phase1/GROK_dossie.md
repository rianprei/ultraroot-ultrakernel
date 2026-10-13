# GROK — dossiê Fase 1 (arquitetura)

Data da leitura: 2026-10-10. Fontes: clones `--depth 1` em `[artefato local, fora do repo]/grok/src/` e binários de release em `scratch/grok/bin/`. Pins em `scratch/grok/notes/pins.txt`. O checkout local `[fora do repo]` (origin `rianprei/SuperRoot`) não entra como evidência de upstream.

## Critério

`[PORTÁVEL]`: o contrato da feature cabe num manager único sem o ABI de kernel, o daemon ou a cadeia de boot daquele motor. Embarcar o `.ko` ou o `kpimg` inteiro não conta como extrair a feature para o APK.

`[ACOPLADA]`: o comportamento correto depende do patch de kernel, do daemon ou do boot daquele motor.

`[AUSENTE]`: o arquivo ou o símbolo não está neste checkout.

`[NÃO-VERIFICÁVEL]`: o arquivo existe, ou o binário tem a string, e o corpo que decidiria o veredito não foi lido.

Confiança: **Alta** = corpo lido, ou sha256 do arquivo inteiro igual a um arquivo cujo corpo foi lido. **Média** = símbolo, string, xref ou arquivo presente, corpo não lido. **Baixa** = não usei; o que não fecha vai para `[NÃO-VERIFICÁVEL]`.

Transferência por hash: sha256 de arquivo igual ao oficial autoriza reutilizar a citação de linha. Onde o hash difere, o veredito de mecanismo só entra se o símbolo foi buscado de novo naquele arquivo.

## Pins (HEAD, 2026-10-10)

| Árvore | SHA | Data do commit | Origin |
|---|---|---|---|
| KernelSU | `6e3860443f8fea7c5fc226277e53888033796488` | 2026-10-10 +0800 | tiann/KernelSU |
| KernelSU-Next | `cd4a6b468653fb9640caaf203c05e07609ba5da1` | 2026-10-10 -0400 | KernelSU-Next/KernelSU-Next |
| SukiSU-Ultra | `223897c72cc5c23b6fea84d6542aa8ef24459998` | 2026-10-10 +0800 | SukiSU-Ultra/SukiSU-Ultra |
| backslashxx | `920ae322dd3d9f7b7e8e79ea7826da444a4b18ed` | 2026-10-10 +0800 | backslashxx/KernelSU |
| rsuntk | `648e5988cf421172769f80ce07f86331b548c053` | 2026-05-29 +0700 | rsuntk/KernelSU |
| MKSU | `6a4cac03f35751b066fcbb08e24fb8198ca354e1` | 2026-09-01 +0800 | 5ec1cff/KernelSU |
| Magisk (master) | `e8915d9db15f5aae93973ffe65068e34df375a6a` | 2026-10-09 -0700 | topjohnwu/Magisk |
| Magisk Alpha | `96221b69fae9910b1c0c75c2f92a4ebb2c2dc698` | 2026-09-04 -0700 | vvb2060/Magisk |
| APatch | `e78e7b3e611458b2ef54ad0619af6b374517b890` | 2026-10-10 +0800 | bmax121/APatch |
| KernelPatch | `f36f3d1377004871f8c964f7cd3276712eeba943` | 2026-10-10 +0800 | bmax121/KernelPatch |

O master do Magisk (`e8915d9d`, assunto `su: remove obsolete access and reauth settings`) é mais novo que o APK de release usado no REA (tag v30.7, versionCode 30700). Citação de `.rs`/`.cpp` é do master. Citação de binário é do APK v30.7. Os dois não são o mesmo commit. O APK da Alpha (tag 31000) não foi baixado.

## REA

CLI `rea-agents` 6.3.0, provider Ghidra 12.1.2 em `/opt/ghidra`, JADX `jadx-headless-mcp` 0.7.1. `TMPDIR` em `scratch/grok/tmp`. Heap JADX e Ghidra em 1024 MiB. Trace com `--format json` sem `--token-limit` (a combinação devolve `UNSUPPORTED_OUTPUT_COMBINATION`).

Managers (inspect de pacote, JADX, assinatura `not_performed`, APK não executado):

| APK | evidence_id | identidade |
|---|---|---|
| `bin/ksu/KernelSU_v3.3.0_32601-release.apk` | `ev_2ec51a9519c3826d17441bb8cf821890e7fe5389280f15b5aba61de653bee20c` | `me.weishu.kernelsu` v3.3.0 / 32601, minSdk 31, target 37, 4823 classes |
| `bin/magisk/Magisk-v30.7.apk` | `ev_2d43cf68d7610a52821ad0f55faed7221ef038d70de0387c4dce9689c5e96d0f` | `com.topjohnwu.magisk` 30.7 / 30700, minSdk 23, target 36, 3408 classes |
| `bin/apatch/APatch_11224_9a63e0f_HEAD-release-signed.apk` | `ev_9f3753d9be7615dc683d6e706fb8a3e7ffc229b7f8387e420b6d5318bb745c4c` | `me.bmax.apatch` versionName `9a63e0f` / 11224, minSdk 26, target 36, 4033 classes |

Método: `me.weishu.kernelsu.Natives.isSuEnabled` → `ev_56aca618fb147576f2bff138aeb1090df85d832b25d3dd682afe8d68cf597458`, `body_status: not_available` (JNI, sem corpo Java). `me.bmax.apatch.Natives.su` não existe com esse nome. `com.topjohnwu.magisk.core.model.su.SuPolicy` não existe com esse nome. Não há, neste dossiê, um método Java com corpo descompilado. (Média para “o manager KSU chama nativo”; o corpo Kotlin de outras funções não foi pedido ao JADX.)

Nativos (`rea trace --provider ghidra`). O JSON traz string e xref, sem pseudocódigo. Limitação registrada pelo REA: “Derived by an REA composed workflow.”

| Binário | query | evidence_id | o que a xref mostra |
|---|---|---|---|
| `bin/native/libkpatch.so` (aarch64, do APK APatch) | `superkey` | `ev_3e9c4ef0ba7b4d80993347e154f16379c57a0c9cf9716e6e7a69f828bf58b752` | 7 strings, entre elas `SUPERKEY` @ `0x101a2f` (xref em `FUN_00105170`), `invalid superkey`, `no new superkey`, `superkey too long`, ajuda de `key` / `su` / `kpm` / `sumgr` @ `0x1025a1` e `0x102e79` (xref em `FUN_00105558`) |
| `bin/native/libmagiskinit.so` (x86_64, extraído do APK v30.7) | `/data/magiskinit` | `ev_5ffece444f9b8653d5ef0ba9b81ac6a6082816601af671264392fc34864c273b` | a string aparece em `0x2018b4` e `0x204448`. Xref desde `FUN_0021775d` @ `0x21775d` (44958 bytes, `contains_entry: true`) |
| `bin/native/kptools-linux` (x86_64, release KernelPatch, não stripped) | `kpimg` | `ev_9950c3b6bff37b75f89652de3d3678c90424c6d1e21125352c80033ceb5f2467` | 27 strings, 42 xrefs. `print_usage` @ `0x10787b`, `print_preset_info` @ `0x10c19a`, `parse_image_patch_info` @ `0x10c54d` referencia mensagens `kpimg length mismatch`, `empty kpimg`, `x86 kpimg` |
| `bin/native/libmagisk.so` (x86_64, APK v30.7) | `zygisk` | `ev_d72b81c0fccf8027e5528bcc09fda043f02659e029523aa2921121b70aae9ba2` | strings `zygisk` @ `0x107cfc`, `zygisk64: [%s] is on the denylist` @ `0x107d21`, `zygisk_companion_entry` @ `0x107e67`, `zygisk_module_entry`, `zygiskd64`, falhas de `plt_hook` e `dlopen` de módulo zygisk |

`libmagiskboot.so`: `rea trace` query `ANDROID!` terminou `provider_timeout` aos 300000 ms em `search_strings` (`rea/native/magiskboot-trace.json`). Varredura de bytes neste arquivo achou `ANDROID!` e `truncate`. Sem xref. (Média para “o binário conhece imagem Android”; o fluxo de unpack não foi descompilado.)

`bin/native/ksud` (aarch64, release oficial, não stripped), query `/data/adb/ksud`: `ev_434ea8197425b57d609e10603ec21fe4651cb6d56e60db4848a7998ca415e17c`. A string `/data/adb/ksud` @ `0x3775c8` é referenciada por `ksud::utils::install` @ `0x447f30`. Outra string, @ `0x378203`, começa com o texto do installer embutido (“KernelSU installer script”, “mostly from module_installer.sh and util_functions.sh in Magisk”) e é referenciada por `ksud::module::regenerate_preinit_rc` @ `0x44a1cc`. Varredura de bytes do mesmo arquivo também achou `ANDROID!`, `magiskinit` e `truncate`. (Alta para essas strings e para os nomes de função; o corpo descompilado não foi pedido.)

`kpimg-android` é `data`, não ELF. Varredura achou `superkey`, `/system/bin/su`, `truncate`. Não houve trace Ghidra desse blob.

## Motores que contam

Três motores, não nove produtos:

1. Família KernelSU. O gancho oficial é tracepoint. Dois forks trocam o gancho: backslashxx (tabela de syscall) e rsuntk (LSM `task_fix_setuid`). Next, SukiSU e MKSU mantêm `register_trace_prio_sys_enter` e acrescentam ou cortam peças em volta.
2. Magisk. Alpha é o mesmo motor com três arquivos nativos de hash diferente. Não é um terceiro mecanismo de root.
3. APatch + KernelPatch. Um motor: o APK/JNI/`apd` falam com o núcleo que o `kpimg` / `.ko` instala. A autenticação é superkey.

Fora da matriz, como motores mortos ou redundantes neste recorte: `HuskyDG/magisk-files` respondeu HTTP 404; `1q23lyc45/KitsuneMagisk` está arquivado. SuList / MagiskHide não têm implementação viva no master nem na Alpha (os dois apagam a chave `magiskhide`). `rifsxd/KernelSU-Next` é fork de uma estrela do org e não foi clonado.

Licenças vistas, sem abrir todos os `LICENSE`: KernelSU `MODULE_LICENSE("GPL")` em `kernel/core/init.c:234`. backslashxx declara GPL-2.0 no cabeçalho de `syscall_table_hook_arm64.c:8-10`. KernelPatch `kernel/include/kpscauth.h:1` é `GPL-2.0-or-later`. Juntar esses núcleos num único objeto de kernel é um problema de licença, separado do veredito de cada feature.

## Eixo A — mecanismo de root

### KernelSU oficial

O driver entra como módulo. `kernel/core/init.c:111` seta `ksu_late_loaded = (current->pid != 1)` quando compilado com `MODULE`. `init.c:227-236`: `module_init(kernelsu_init_early)` se `NEED_OWN_STACKPROTECTOR`, senão `module_init(kernelsu_init)`; autor `weishu`; descrição `Android KernelSU`. (Alta)

O gancho de syscall está em `kernel/hook/syscall_hook_manager.c`. `register_kretprobe` na linha 38, em cima de `syscall_regfunc` / `syscall_unregfunc` (handlers nas linhas 59 e 75). `register_trace_prio_sys_enter(ksu_sys_enter_handler, NULL, INT_MIN)` na linha 146. (Alta)

`kernel/hook/lsm_hook.c` deste commit é um patcher de slot (`ksu_patch_text` na linha 69, tabela `ksu_lsm_hook_entries`). Não chama `security_add_hooks`. A busca de `security_add_hooks` em todo `**/lsm_hook.c` dos clones só acertou rsuntk. (Alta)

Não há `kernel/hook/syscall_table_hook_arm64.c` nesta árvore. (Alta, ausência de arquivo)

### KernelSU-Next, SukiSU, MKSU

Os três têm `syscall_hook_manager.c` e a mesma chamada:

- Next, linha 146, arquivo com hash diferente do oficial.
- SukiSU, linha 146, sha256 do arquivo igual ao oficial.
- MKSU, linha 142, hash diferente.

(Alta para a chamada; Média para o resto do arquivo quando o hash difere.)

SukiSU acrescenta `kernel/kpm/kpm.c`. As exportações lidas são stubs: `sukisu_kpm_load_module_path` (linha 54) e `sukisu_kpm_unload_module`, `sukisu_kpm_num`, `sukisu_kpm_info`, `sukisu_kpm_list`, `sukisu_kpm_control`, `sukisu_kpm_version` só fazem `pr_info("kpm: Stub function called...")` e `nop` (linhas 58-121). (Alta: neste commit não há loader KPM.)

### backslashxx

Cabeçalho de `kernel/hook/syscall_table_hook_arm64.c:1-15`: extensão downstream, “NOT affiliated” com o KernelSU oficial, GPL-2.0, só ARM64. Números de syscall fixos no estilo 4.14 (`__AARCH64_execve 221`, linha 23; `__AARCH64_reboot 142`, linha 22). A partir da linha 359, `syscall_table_sucompat_enable` chama `read_and_replace_syscall` sobre `sys_call_table` e, com `CONFIG_COMPAT`, sobre `compat_sys_call_table`, para execve, execveat, faccessat, newfstatat, reboot, read e os equivalentes armeabi (linhas 362-406). Existe também `syscall_table_hook_arm.c` com o mesmo padrão (linha 236 em diante). Não existe `syscall_hook_manager.c`. (Alta)

Não existe `kernel/core/init.c` nem `kernel/Kbuild` neste layout. Se o entregável é LKM ou built-in, `[NÃO-VERIFICÁVEL]` neste dossiê.

Arquivos `lsm_hooks_*.c` existem no diretório de hook (vistos na listagem da sessão anterior e não relidos aqui). O corpo deles é `[NÃO-VERIFICÁVEL]`.

### rsuntk

Não existe `syscall_hook_manager.c`. O gancho lido é `kernel/hook/lsm_hook.c:46-61`: `ksu_hooks[]` com `LSM_HOOK_INIT(task_fix_setuid, ksu_task_fix_setuid)` e, em kernels antigos ou `CONFIG_KSU_ALLOWLIST_WORKAROUND`, `key_permission`. `ksu_lsm_hook_init` chama `security_add_hooks(..., "ksu")` em `LINUX_VERSION_CODE >= 4.11` (linha 57). O handler de setuid chama `ksu_handle_umount(old_uid, new_uid)` na linha 41. (Alta)

Commit de 2026-05-29. API do GitHub ainda marcava push em 2026-07-22 na triagem. Árvore viva e atrasada, mecanismo diferente do oficial.

### Magisk e Alpha

Não há patch de kernel no código lido. O boot é userspace. `native/src/init/init.hpp:4` define `REDIR_PATH "/data/magiskinit"`. `init.rs:100` lê `backup_init()`; `init.rs:191` chama `magisk_proxy_main`. A implementação está em `init/rootdir.cpp:374-377` (`cp_afc(REDIR_PATH, "/sbin/magisk")` e `magisk_proxy_main`). (Alta para essas linhas no master.)

O binário `libmagiskinit.so` do APK v30.7 referencia `/data/magiskinit` a partir da função de entrada (REA acima). (Alta para a string no binário v30.7; a igualdade linha a linha com o master não foi provada.)

`get_magisk_tmp` em `native/src/core/utils.cpp:37-48`: se `/debug_ramdisk/.magisk` existe, devolve `/debug_ramdisk`; senão se `/sbin/.magisk` existe, devolve `/sbin`; senão string vazia. (Alta, master.)

Alpha: `zygisk/hook.cpp`, `module.rs`, `sqlite.cpp`, `resetprop/cli.rs` e `consts.rs` têm o mesmo sha256 do master. `daemon.rs`, `deny/cli.cpp` e `su/su.cpp` diferem. O delta desses três arquivos não foi auditado linha a linha. (Alta para o hash; Média para “a arquitetura de boot é a mesma”, porque `daemon.rs` mudou e o `init/` não entrou na comparação.)

### APatch + KernelPatch

O README do KernelPatch (lido na sessão anterior; não reaberto aqui) descreve patch de imagem de kernel stripped, arm64, kallsyms. O que esta passagem prova no fonte:

- `kernel/patch/include/uapi/scdefs.h:114` `#define SUPERCMD "/system/bin/truncate"`.
- `supercall.c:461-469`: `supercall_install` chama `hook_syscalln_override(__NR_supercall, 6, before, ...)`. O comentário nas linhas 465-468 diz que o supercall curto-circuita o syscall real e, quando o dispatcher global está em `invoke_syscall`, o syscall 45 usa o mesmo trampolim. (Alta para a instalação do hook; Média para o número 45, que está no comentário.)
- `kptools-linux` parseia `kpimg` (REA). `kpimg-android` contém `superkey` e `/system/bin/su` (varredura). (Alta para as strings; o layout do blob não foi descompilado.)

Não há, nos clones KSU, arquivo de kernel com `susfs` no nome (contagem 0 em `kernel/` de KSU, Next, Suki, backslashxx, rsuntk, MKSU e KernelPatch).

## Eixo B — su e daemon

### KernelSU

`kernel/feature/sucompat.c:31` `#define SU_PATH "/system/bin/su"`. O caminho do daemon é `kernel/runtime/ksud.h:6` `#define KSUD_PATH "/data/adb/ksud"`. O fluxo lido exige `ksu_is_allow_uid_for_current` (linhas 97, 132, 181), abre `KSUD_PATH` com `filp_open` (linha 207) e grava `AT_EMPTY_PATH` no registrador do execveat (linha 225). (Alta: o redirect do `su` aponta para o ksud e só segue se o uid está na allowlist do kernel.)

SukiSU: `sucompat.c` tem o mesmo sha256. As mesmas linhas valem. (Alta)

IPC do userspace, `userspace/ksud/src/ksucalls.rs:120-157`: se não há fd herdado, `syscall(SYS_reboot, KSU_INSTALL_MAGIC1, KSU_INSTALL_MAGIC2, 0, &mut fd)` e depois `ioctl` nesse fd. Os magics estão em `uapi/supercall.h:16-17`: `0xDEADBEEF` e `0xCAFEBABE`. O lado kernel, `kernel/supercall/supercall.c:116-136`, é um `kprobe` pre-handler de reboot que, com esses dois magics, agenda `ksu_install_fd` via `task_work`. (Alta)

Os ioctls usam tipo `'K'`. Exemplos em `uapi/supercall.h:161-187`: `KSU_IOCTL_GRANT_ROOT` (nr 1), `KSU_IOCTL_GET_INFO` (nr 2), `KSU_IOCTL_GET_DENY_LIST` (nr 7), `KSU_IOCTL_UID_SHOULD_UMOUNT` (nr 9), `KSU_IOCTL_NUKE_EXT4_SYSFS` (nr 17). (Alta para o oficial.)

backslashxx: `uapi/supercall.h` tem o mesmo sha256 do oficial, então a tabela de ioctl é a mesma. O gancho que observa o reboot é outro: `read_and_replace_syscall` de `__AARCH64_reboot` na linha 399 do hook de tabela. (Alta)

Next, SukiSU, MKSU e rsuntk: `uapi/supercall.h` existe e o hash difere. O par de magics e os números de ioctl desses quatro são `[NÃO-VERIFICÁVEL]` nesta passagem. O `ksucalls.rs` deles também difere.

Allowlist é estado de kernel (`ksu_is_allow_uid_for_current` no sucompat). Não há `magisk.db` nesta família.

### Magisk

`native/src/include/consts.rs:8`: `APPLET_NAMES = ["su", "resetprop"]`. O `su` é applet do binário magisk, não um binário de kernel.

Daemon: `native/src/core/daemon.rs:406-422` faz `UnixListener::bind` em `get_magisk_tmp()` + `MAIN_SOCKET`. `consts.rs:33` define `MAIN_SOCKET` como `.magisk/device/socket`. Contexto SELinux do socket: `MAGISK_FILE_CON` (`u:object_r:magisk_file:s0`, `consts.rs:42-45`). Domínio de processo `u:r:magisk:s0` (`consts.rs:41-42`). (Alta, master.)

Cliente su: `native/src/core/su/su.cpp:212` `connect_daemon(RequestCode::SUPERUSER)`. (Alta para a chamada no master. O arquivo `su.cpp` da Alpha tem outro hash; a linha 212 da Alpha não foi relida.)

Policy de su do Magisk fica em sqlite: `consts.rs:23` `MAGISKDB = /data/adb/magisk.db`. (Alta para o path. O esquema das tabelas de policy não foi aberto.)

Alpha: `daemon.rs:408` também faz `UnixListener::bind(&sock_path)`. O arquivo difere do master. (Alta para o bind existir; Média para o restante do daemon.)

### APatch + KernelPatch

Paths Android em `scdefs.h:94-114` (sob `#ifdef ANDROID`):

- `SU_PATH "/system/bin/kp"`
- `LEGACY_SU_PATH "/system/bin/su"`
- `APD_PATH "/data/adb/apd"`
- `ALL_ALLOW_SCONTEXT "u:r:kp:s0"` (também há constantes `u:r:magisk:s0` e `u:r:kernel:s0`)
- `SUPERCMD "/system/bin/truncate"`

(Alta)

Quem autentica é o hook `before` em `kernel/patch/common/supercall.c:418-458`:

1. `get_ap_mod_exclude(uid)` retorna na hora (linha 421). O uid excluído não chega no supercall.
2. `is_authed` começa em 0.
3. Se `has_preset_superkey()` (`predata.c:345-347`: `superkey_is_user_set || root_superkey_is_set`), copia a key do userspace e faz `is_authed = !auth_superkey(key)`. Falha de cópia (`len <= 0`) retorna sem chamar o supercall. `is_trusted_caller = is_authed`.
4. Se `is_trusted_manager_uid(uid)`, força `is_trusted_caller = 1` e `is_authed = 1` mesmo sem a key ter passado.
5. Senão, se `is_su_allow_uid(uid)`, marca `is_trusted_caller` e deixa `is_authed` como estava.
6. Sem `is_trusted_caller`, retorna.
7. O comando é `ver_xx_cmd & 0xFFFF`, precisa cair entre `SUPERCALL_HELLO` e `SUPERCALL_MAX`, e `args->ret = supercall(is_authed, ...)`.

`supercall` na linha 297: `if (!is_authed && !kp_supercall_allowed_for_su(cmd)) return -EPERM`. A lista curta, `kpscauth.h:6-20`, é `SUPERCALL_HELLO`, versões, `SUPERCALL_SU`, `SUPERCALL_SU_PROFILE` (o dispatcher ainda restringe ao uid do caller, linha 298), `SUPERCALL_SU_GET_PATH`, `SUPERCALL_SU_GET_SAFEMODE`.

Leitura direta: sem superkey preset, um uid da allowlist de su entra no dispatcher com `is_authed == 0` e só alcança essa lista curta. Um uid de manager trusted fica `is_authed == 1` sem apresentar a key. Isto não é “ausência de key libera todos os comandos”. (Alta)

`auth_superkey` em `predata.c:43-80`: compara tamanho e XOR byte a byte com `superkey`. Se falha e `enable_root_key` está ligado, compara SHA-256 da key com `root_superkey` via `kp_secret_equal`. No primeiro acerto, `reset_superkey(key)` e `enable_root_key = false`. Retorno 0 aceita. (Alta)

`SUPERCALL_KEY_MAX_LEN` é `0x40` (`scdefs.h:84`). Comandos de su a partir de `SUPERCALL_SU_GRANT_UID 0x1100` (`scdefs.h:118`).

O manager, `APatch/app/src/main/cpp/apjni.cpp:16-73`: `nativeReady` chama `sc_ready(super_key)` ou `sc_ready("su")` se a key vier vazia. `nativeSu` preenche `su_profile` (uid atual, `to_uid`, `scontext`) e chama `sc_su`. `nativeSetUidExclude` / `nativeGetUidExclude` chamam `sc_set_ap_mod_exclude` / `sc_get_ap_mod_exclude`. (Alta) O corpo Rust de `apd/src/supercall.rs` não foi lido.

SukiSU `userspace/ksud/src/module.rs:975` tem um parâmetro chamado `superkey` em `exec_stage_lua`. É argumento de script Lua de módulo. Não é o `auth_superkey` do KernelPatch. (Alta para o que a função é; o corpo de `run_lua` não foi lido.)

## Eixo C — módulos

Contrato de diretório, KernelSU `userspace/ksud/src/defs.rs` (bloco `android`, linhas 12-54):

- daemon `/data/adb/ksud`
- módulos `/data/adb/modules/` e `modules_update/`
- metamódulo `/data/adb/metamodule/`
- nomes `webroot`, `action.sh`, `disable`, `update`, `remove`
- scripts `metamount.sh`, `metainstall.sh`, `metauninstall.sh`

APatch `apd/src/defs.rs:1-30` repete o mesmo contrato de módulo, com daemon `/data/adb/apd`, workdir `/data/adb/ap/`, e os mesmos três nomes de script. (Alta)

Magisk `consts.rs:19-23`: `SECURE_DIR /data/adb`, `MODULEROOT /data/adb/modules`, `MAGISKDB /data/adb/magisk.db`. Não usa `metamount.sh` nesse arquivo. (Alta)

O parser `pub fn read_module_prop` existe em `KernelSU/userspace/ksud/src/module.rs:901`. O corpo não foi citado. (Média para o formato `id=`, `name=` etc.)

Mount do KSU oficial: `metamodule.rs:254-276`, `exec_mount_script`. Se `metamount.sh` não está no metamódulo, retorna `Ok(())` (linhas 255-257). Se está, roda `busybox sh` com `MODULE_DIR` no ambiente. Quem chama: `init_event.rs:114` e `late_load.rs:122`. (Alta: o core não implementa overlayfs; delega a um script externo.)

`metamodule.rs` tem o mesmo sha256 em Next, SukiSU e backslashxx. A delegação é a mesma. (Alta)

Não há, neste clone do KernelSU, uma árvore `userspace/meta-overlayfs` que eu tenha aberto. Busca por nome `*overlay*` / `*magic*` em KernelSU-Next não achou arquivo. A implementação de overlayfs que a documentação pública aponta é `[NÃO-VERIFICÁVEL]` dentro destes checkouts. (Alta para a delegação; o script de terceiros não está aqui.)

Magic mount do Magisk: `native/src/core/module.rs`. `bind_mount` na linha 39. `parent_should_be_tmpfs` na linha 266. `.replace` na linha 293. `commit_tmpfs` na linha 338, que trata `.replace` na linha 350. (Alta, master. O `module.rs` da Alpha tem o mesmo sha256, então as mesmas linhas.) Isto é código do daemon, executado no namespace que o init preparou. `[ACOPLADA]` ao boot do Magisk. É a única implementação de magic mount lida. KSU não tem este arquivo.

`apd/src/module.rs` existe. O corpo do mount não foi lido. Pelo `exec_mount_script` do KSU e pelos mesmos nomes em `defs.rs` do APatch, o desenho esperado é delegação a metamódulo. O veredito “APatch não faz bind/tmpfs dentro do apd” fica `[NÃO-VERIFICÁVEL]` até o corpo ser lido. (Baixa se eu afirmasse ausência.)

KPM real: `KernelPatch/kernel/patch/module/module.c`. `load_module` na linha 558 chama `load_module_ex` (452). `load_module_path` na 613. `set_kpm_load_result` na 65. (Alta: o loader está no KernelPatch.) SukiSU, neste commit, não o tem (stubs acima). Nenhum outro clone tem `kernel/kpm/kpm.c` nem `kernel/patch/module/module.c`.

Boot patch do KSU: `userspace/ksud/src/boot_patch.rs:573` `pub fn patch`. O trecho 746-776 carrega o asset `ksuinit`, renomeia `init` para `init.real` se o cpio ainda não tem `kernelsu.ko`, e adiciona `init` + `kernelsu.ko` ao cpio. Comentário na linha 386 cita a ordem de lookup do magiskboot (`init_boot.cpio` antes de `ramdisk.cpio`). (Alta: o patch de ramdisk do ksud injeta o init e o `.ko` do KernelSU.) O arquivo é byte-idêntico em backslashxx. Next, SukiSU, MKSU e rsuntk diferem; o corpo deles não foi relido.

O `ksud` de release referencia `/data/adb/ksud` a partir de `ksud::utils::install` e embute o texto de um installer que cita `module_installer.sh` e `util_functions.sh` do Magisk (`ev_434ea8197425b57d609e10603ec21fe4651cb6d56e60db4848a7998ca415e17c`). A varredura de bytes também achou `ANDROID!` e `magiskinit`. (Alta para as strings e os símbolos; o script embutido não foi comparado linha a linha com o do Magisk.)

## Eixo D — injeção de código (zygote)

Magisk master: `native/src/core/zygisk/hook.cpp:548` `HookContext::hook_zygote_jni`. O comentário nas linhas 86 e 115 descreve a troca das funções de especialização de processo registradas no JNI do zygote. `entry.cpp:41` faz `dlsym(..., "zygisk_companion_entry")`. (Alta, master. Alpha `hook.cpp` tem o mesmo sha256.)

O APK v30.7 não traz um `libzygisk.so` separado na extração feita. O `libmagisk.so` x86_64 contém `zygisk`, `zygisk_companion_entry`, `zygisk_module_entry`, `zygiskd64` e a frase de denylist (REA `ev_d72b81c0…`). (Alta: zygisk deste release mora dentro do `libmagisk.so`.)

Busca `zygisk` nos arquivos do clone `tiann/KernelSU` nesta passagem não retornou arquivo. KernelSU, Next, SukiSU, backslashxx, rsuntk, MKSU, APatch e KernelPatch não têm `native/src/core/zygisk/hook.cpp`. (Alta para a ausência desse arquivo.)

`KernelSU/userspace/ksud/src/magica.rs` existe no oficial, na SukiSU, no backslashxx, no rsuntk e no MKSU. Não existe no Next. Não foi relido nesta passagem. A sessão anterior o classificou como ADB-root por TCP. Sem releitura do corpo, o conteúdo fica `[NÃO-VERIFICÁVEL]` aqui. Não é zygisk: o nome do arquivo e a ausência de hits `zygisk` no clone oficial são o que esta passagem prova.

## Eixo E — esconder

### Deny / umount / exclude

Magisk: `native/src/core/deny/cli.cpp:12-21` documenta `magisk --denylist` com `enable`, `disable`, `add`, `rm`, `ls`. `denylist_cli` na linha 65. `denylist_handler` na linha 29. (Alta, master.) Alpha tem o arquivo, com outro hash. A CLI existe nos dois; o delta da Alpha é `[NÃO-VERIFICÁVEL]`. O binário v30.7 diz `zygisk64: [%s] is on the denylist` dentro do `libmagisk.so` (REA). (Alta para o v30.7.)

KSU oficial: o UAPI tem `KSU_IOCTL_GET_DENY_LIST` e `KSU_IOCTL_UID_SHOULD_UMOUNT` (`uapi/supercall.h:171` e `:175`). O arquivo `kernel/feature/kernel_umount.c` existe. O corpo da decisão de umount não foi citado nesta passagem. SukiSU e MKSU têm esse `.c` com o mesmo sha256 do oficial. Next, backslashxx e rsuntk têm o arquivo com hash diferente. rsuntk chama `ksu_handle_umount` a partir do LSM de setuid (linha 41, Alta). (Alta para a existência do ioctl oficial e da chamada no rsuntk; Média para a política exata dentro de `kernel_umount.c`.)

Isto não é o DenyList do Magisk. São listas no driver, acopladas ao gancho de kernel.

APatch: exclude por uid no kernel, consultado antes do supercall (`supercall.c:421`) e exposto no JNI (`apjni.cpp:62-73`). (Alta) `[ACOPLADA]` ao KernelPatch.

### SuList e MagiskHide

`Magisk/native/src/core/sqlite.cpp:279` executa `DELETE FROM settings WHERE key='magiskhide'`. Alpha `sqlite.cpp` tem o mesmo sha256, mesma linha. (Alta: os dois motores vivos apagam a chave.) Não achei implementação de SuList nestes dois trees. Kitsune, que historicamente carregava SuList, está arquivado e fora da matriz. `[AUSENTE]` nos motores vivos.

### susfs

Next: `userspace/ksud/src/susfsd.rs:7` `SUSFS_MAGIC = 0xFAFAFAFA`. Linhas 44, 65 e 86 fazem `syscall(SYS_reboot, KSU_INSTALL_MAGIC1, SUSFS_MAGIC, CMD_SUSFS_SHOW_*)`. Comandos `0x555e1`, `0x555e2`, `0x555e3` (linhas 9-11). (Alta: cliente userspace.) Zero arquivos de kernel com `susfs` no nome. O efeito depende de um patch de kernel que este repo não contém. `[NÃO-VERIFICÁVEL]` o lado kernel. O cliente sozinho não produz o efeito.

SukiSU: `userspace/ksud/src/cli.rs:184-185` subcomando `Susfs`. O dispatch nas linhas 1159-1195 chama `susfs::set_uname`, `add_sus_path`, `add_sus_map`, `add_sus_kstat`, e há `SusfsModuleCmd::Install` na 1220. (Alta: CLI.) Também zero arquivo de kernel `*susfs*`. Mesma ressalva.

KSU oficial, backslashxx, rsuntk, MKSU, Magisk, Alpha, APatch, KernelPatch: sem `susfsd.rs` e sem arquivo de kernel `*susfs*`. `[AUSENTE]` nestes checkouts.

### SELinux

KSU: `kernel/feature/selinux_hide.c:510-515` registra feature de nome `"selinux_hide"`. (Alta para o registro. O corpo do hide não foi citado.) Next tem o mesmo sha256 desse arquivo. SukiSU, backslashxx e MKSU têm o arquivo com hash diferente. rsuntk não tem `selinux_hide.c`.

O boot do KSU também injeta `exec u:r:` + domínio + `KSUD_PATH` em texto de rc, `kernel/runtime/ksud_integration.c:39-48`. (Alta: há uma linha de rc que dispara o ksud num domínio. O nome do domínio está no macro, não foi aberto.)

KernelPatch: `kernel/patch/include/selinux_hide.h:13-14` diz que a feature liga por supercall `SUPERCALL_CONTROL_FEATURE` com nome `"selinux_hide"` ou pelo arquivo `/data/adb/ap/selinux_hide`. `user_event.c:37` chama `selinux_hide_post_fs_data`. O `.c` existe em `kernel/patch/common/selinux_hide.c`. O corpo do hide não foi citado. (Alta para o ponto de entrada; Média para o que ele altera na policy.)

Magisk: domínio `u:r:magisk:s0` e tipo de arquivo `magisk_file` em `consts.rs:41-45`. Há `libmagiskpolicy.so` no APK (extraído, não traçado). O que a policy injeta, além desses contextos, não foi lido. (Alta para os contextos; Média para o restante da sepolicy.)

## Eixo F — manager ↔ núcleo

### KernelSU: certificado no kernel

`kernel/manager/apk_sign.c:326-328`:

```c
bool is_manager_apk(char *path)
{
    return check_v2_signature(path, EXPECTED_SIZE, EXPECTED_HASH);
}
```

Os macros saem do `kernel/Kbuild:132-148`. Default: pacote `me.weishu.kernelsu` (ou `me.weishu.kernelsu.pr` se `KSU_IS_PR_BUILD`), `KSU_EXPECTED_SIZE ?= 0x033b`, `KSU_EXPECTED_HASH ?= c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6`. Viram `-DEXPECTED_SIZE` e `-DEXPECTED_HASH` no `ccflags`. (Alta)

O manager de release inspecionado pelo REA é esse package name, versionName `v3.3.0`. Não conferi se o certificado desse APK bate com o hash default. (O hash do APK de release e o `EXPECTED_HASH` são coisas diferentes: um é sha256 do arquivo, o outro é o hash de assinatura v2 compilado no `.ko`.)

Next, SukiSU, MKSU e rsuntk têm `apk_sign.c` com outro hash. backslashxx também. O valor `EXPECTED_*` desses forks é `[NÃO-VERIFICÁVEL]` aqui. O mecanismo (checar assinatura v2 dentro do kernel) está no arquivo, então a feature continua `[ACOPLADA]` ao `.ko` que foi compilado com esses flags. (Média nos forks, Alta no oficial.)

O método Java `Natives.isSuEnabled` do APK v3.3.0 não tem corpo: a decisão de su não está no Java. (Alta para o stub JNI.)

### Magisk

O APK fala com o daemon pelo socket unix (eixo B). Não há superkey e não há `is_manager_apk` no fonte do Magisk. A identidade do pacote no master é a string `com.topjohnwu.magisk` em `consts.rs:14`, e o REA viu esse package no APK v30.7. (Alta para os dois fatos; eles não provam um check de assinatura dentro do daemon.)

### APatch

Toda chamada JNI lida recebe `super_key` (`apjni.cpp`). `nativeReady` aceita key vazia e cai em `sc_ready("su")` (linhas 20-22). Isso combina com o ramo de `supercall.c` em que, sem preset, o uid de manager trusted fica autenticado. Não li `is_trusted_manager_uid_android`, então a ligação “APK oficial == uid trusted” é `[NÃO-VERIFICÁVEL]`. (Alta para o JNI passar a key; Média para quem o kernel trata como manager.)

O REA em `libkpatch.so` achou o texto “KernelPatch SuperKey manager” com subcomandos `get`, `set <SUPERKEY>`, `rootkey [enable|disable]`. Isso é a superfície de userspace da mesma key, no `.so` do APK 11224. (Alta)

## O que um APK único consegue carregar

Com confiança Alta, e sem levar o núcleo junto:

- O contrato de arquivos de módulo: `module.prop` no diretório `/data/adb/modules/<id>/`, mais `webroot`, `action.sh`, `disable`, `update`, `remove`. KSU e APatch ainda compartilham os nomes `metamount.sh`, `metainstall.sh`, `metauninstall.sh`. Um manager lê e grava isso sem o driver. O efeito de mount continua acoplado a quem roda no boot (script externo no KSU/APatch, magic mount no Magisk).
- `resetprop` como applet. Magisk `consts.rs:8` lista o applet. `native/src/core/resetprop/cli.rs:273` é `pub fn resetprop_main`. `mod.rs:3` reexporta `get_prop`, `set_prop`, `load_prop_file`. KSU `userspace/ksud/src/resetprop.rs:26-29` se descreve como “Magisk-compatible” e usa o crate `prop_rs_android` (linha 5). Next, SukiSU e backslashxx têm esse `.rs` com o mesmo sha256 do KSU oficial. O tool precisa de um processo com acesso ao property area. Não precisa do socket do magiskd nem do ioctl do KSU para existir como código. (Alta para a existência do CLI; Média para “nenhum branch chama o daemon”, porque o corpo inteiro do `cli.rs` não foi citado.)

Com confiança Média:

- O binário `libmagiskboot.so` já vai dentro do APK v30.7, é ELF estático, e contém `ANDROID!`. O trace Ghidra estourou o tempo. Serve como candidato a utilitário de boot image. O que ele escreve no ramdisk para instalar o Magisk é acoplado. O patcher do ksud (`boot_patch.rs`) também abre boot image, e o trecho lido grava `ksuinit` e `kernelsu.ko`: essa parte é `[ACOPLADA]`.

O resto das linhas da matriz (gancho, allowlist, su redirect, socket do magiskd, magic mount, zygisk, denylist, KPM, kpimg, superkey, assinatura do manager, susfs, selinux_hide, umount de kernel) fica no motor de origem. Detalhe célula a célula em `GROK_matriz_features.md`.

## Limites desta passagem

- Pseudocódigo Ghidra não foi pedido (`rea decompile` de função nomeada). As evidências nativas são string + xref.
- `libmagiskboot.so`: timeout.
- O trace do `ksud` fechou com `ev_434ea8197425b57d609e10603ec21fe4651cb6d56e60db4848a7998ca415e17c` (string + xref, sem pseudocódigo).
- Nenhum método Java com corpo.
- Diff hunk a hunk de Alpha (`daemon.rs`, `deny/cli.cpp`, `su.cpp`) não feito. `diff -rq` da árvore inteira quebrou porque o master não tem o submodule `native/src/base/include/rust/cxx.h`.
- Corpo de `kernel_umount.c`, de `apd/src/module.rs`, de `apd/src/supercall.rs`, dos `lsm_hooks_*.c` do backslashxx, e do hide dentro de `selinux_hide.c` (KSU e KernelPatch) não citado.
- APK da Alpha não passou pelo REA.
- Overlayfs “meta-overlayfs” não está nestes checkouts.
- susfs de kernel não está em nenhum `kernel/` clonado.
