# G1a — viabilidade estática do KernelSU contra o kernel local

Data: 2026-10-10. Read-only. Sem build, sem `insmod`, sem device.

Commit do alvo: tiann/KernelSU `6e3860443f8fea7c5fc226277e53888033796488` (`6e38604`, “feat: support forcing feature values via ksu_config”). Checkout: `kernel/upstream/KernelSU`.

Artefatos do build, o mesmo trio:

- `Module.symvers` e `System.map`: `~/lake-build/src/out/bazel/output_user_root/b816944b4a8db05c78f8f829a3c594da/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64/`
- `.config`: `.../bin/common/kernel_aarch64_config/out_dir/.config`
- `UTS_RELEASE` do KernelBuild 981: `include/generated/utsrelease.h` → `6.6.89-android15-8-4k`

Veredito: **BLOQUEADO**. O source deste commit não fecha o link contra este `Module.symvers`. Config e nomes de kprobe estão presentes. O vermagic que este build gravaria não é o da referência.

## Tabela

| Item | Resultado | Evidência |
|---|---|---|
| 1. Símbolos que o source chama e o módulo precisa resolver | FAIL | Seis referências vivas não estão exportadas. Ver abaixo. |
| 2. Alvos de hook por nome | PASS | `syscall_regfunc`, `syscall_unregfunc`, `input_event`, `__arm64_sys_reboot` estão no `System.map`. |
| 3. Config do caminho real deste commit | PASS | `KPROBES`, `KRETPROBES`, `HAVE_SYSCALL_TRACEPOINTS`, `TRACEPOINTS`, `EXT4_FS`, `MODULES`, `OVERLAY_FS` = y. |
| 4. Vermagic | FAIL | Prefixo local `6.6.89-android15-8-4k`. Referência `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`. |
| 4b. Assinatura / insmod | restrição registrada | `MODULE_SIG_FORCE` desligado. `MODULE_SIG_PROTECT=y`. Unsigned carrega, e só pode usar a lista unprotected. `kernel_read` / `kernel_write` estão fora dela. |

## 1. Símbolos — FAIL

O que está exportado, com CRC, e na lista unprotected (`gki_module_unprotected.h` do KernelBuild 981), e que o source chama:

| Símbolo | CRC | Export | Onde o KSU chama |
|---|---|---|---|
| `register_kprobe` | `0x0472cf3b` | `EXPORT_SYMBOL_GPL` | `supercall/supercall.c:153`, `runtime/ksud_integration.c:660` |
| `unregister_kprobe` | `0xeb78b1ed` | `EXPORT_SYMBOL_GPL` | `supercall.c:163`, `ksud_integration.c:631` |
| `register_kretprobe` | `0xa608af5e` | `EXPORT_SYMBOL_GPL` | `hook/syscall_hook_manager.c:38` |
| `unregister_kretprobe` | `0x5ff55291` | `EXPORT_SYMBOL_GPL` | `syscall_hook_manager.c:53` |
| `tracepoint_probe_register` | `0x805e2dd2` | `EXPORT_SYMBOL_GPL` | via inline, `include/linux/tracepoint.h:270` |
| `tracepoint_probe_register_prio` | `0xf85689ef` | `EXPORT_SYMBOL_GPL` | via inline, `tracepoint.h:277` |
| `tracepoint_probe_unregister` | `0x7aba1ef6` | `EXPORT_SYMBOL_GPL` | via inline, `tracepoint.h:283` |
| `input_event` | `0xb1b7d5c2` | `EXPORT_SYMBOL` | nome de kprobe, não import de link; também exportado |

`register_trace_prio_sys_enter` não é símbolo. É inline em `tracepoint.h:274-278` e referencia `__tracepoint_sys_enter`.

Faltam na exportação (estão no `System.map`, tipo `T` ou `D`, e o `.c` do KSU referencia):

| Símbolo | System.map | Chamada viva |
|---|---|---|
| `kallsyms_lookup_name` | sim | `infra/symbol_resolver.c:69`. No 6.6 o fallback compila: `HAVE_ON_EACH_MATCH_SYMBOL` é 1 a partir de 6.1 (`:35-36`), e a chamada fica fora do `#if` (`:69`). |
| `kallsyms_lookup` | sim | `symbol_resolver.c:71`, no mesmo fallback |
| `kallsyms_lookup_size_offset` | sim | `hook/lsm_hook.c:151` |
| `filp_open` | sim | `feature/sucompat.c:207`, `manager/throne_tracker.c:235`, `runtime/ksud_integration.c:200` e `:205`, `policy/allowlist.c:420` e `:521` |
| `commit_creds` | sim | `policy/app_profile.c:205`, `selinux/selinux.c:221`, `feature/selinux_hide.c:301` |
| `__tracepoint_sys_enter` | sim, tipo `D` | `syscall_hook_manager.c:146` sob `#ifdef CONFIG_HAVE_SYSCALL_TRACEPOINTS` (`:145`). O inline toma o endereço de `__tracepoint_sys_enter` (`tracepoint.h:254` e `:277`). |

`__tracepoint_sys_exit` está exportado (`0x066a3687`, `EXPORT_SYMBOL_GPL`). `__tracepoint_sys_enter` não. O par não é simétrico neste symvers.

`tracepoint_synchronize_unregister` não é símbolo. É inline em `tracepoint.h:91`. O KSU chama em `syscall_hook_manager.c:166`. Não entra na lista de faltantes.

Um varrimento mecânico de chamadas do `kernel/` do KSU contra o `System.map` achou 71 nomes presentes no mapa e ausentes do symvers (`[artefato local, fora do repo]/grok/notes/g1a-symbols.txt`). Vários são inline ou macro (`list_add`, `rcu_read_unlock`, `cpu_relax`). Não entram no FAIL. O FAIL são as seis referências lidas acima: o modpost deste symvers não as resolve.

Dois exports existem e estão fora da lista unprotected:

| Símbolo | CRC | Namespace | Chamada |
|---|---|---|---|
| `kernel_read` | `0xefbeb173` | `VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver` | `manager/apk_sign.c:81`, `throne_tracker.c:249`, `ksud_integration.c:257`, `allowlist.c:528` |
| `kernel_write` | `0x1d4a3081` | o mesmo namespace | `policy/allowlist.c:427` |

O `init.c:238` do KSU declara `MODULE_IMPORT_NS` desse namespace. O namespace fecha. A lista GKI, não.

## 2. Alvos de hook por nome — PASS

Nomes que o source passa a kprobe/kretprobe, conferidos no `System.map` do mesmo build:

| Nome | Onde o KSU passa | System.map |
|---|---|---|
| `syscall_regfunc` | `syscall_hook_manager.c:134`, sob `CONFIG_KRETPROBES` (`:26`) | `T`. Definição em `kernel/tracepoint.c:758`, compilada sob `CONFIG_HAVE_SYSCALL_TRACEPOINTS` (`:753`) |
| `syscall_unregfunc` | `syscall_hook_manager.c:135` | `T`. `tracepoint.c:774` |
| `input_event` | `ksud_integration.c:625` | `T`, e também exportado |
| `__arm64_sys_reboot` | `include/arch.h:22` `REBOOT_SYMBOL`, usado em `supercall.c:143` | `T` |

`kallsyms_on_each_match_symbol`, string em `symbol_resolver.c:184`, também está no `System.map` (`T`). Não é kretprobe. O lookup que a acharia depende de `kallsyms_lookup_name`, que não é exportado. O nome existe; o resolvedor que o source usa para achá-lo não linka.

Não há símbolo `sys_enter` no mapa. O objeto do tracepoint é `__tracepoint_sys_enter`, presente e não exportado. Isso é o FAIL do item 1, não um nome de kprobe ausente.

## 3. Config — PASS

Valores em `.config` do `kernel_aarch64_config`:

| Chave | Valor | Linha | Por que entra |
|---|---|---|---|
| `CONFIG_KPROBES` | y | 681 | `KernelSU/kernel/Kconfig:5` `depends on KPROBES && EXT4_FS` |
| `CONFIG_KRETPROBES` | y | 686 | ifdef do kretprobe, `syscall_hook_manager.c:26` |
| `CONFIG_HAVE_KRETPROBES` | y | 689 | suporte da arch |
| `CONFIG_HAVE_KPROBES` | y | 688 | suporte da arch |
| `CONFIG_KPROBE_EVENTS` | y | 7686 | presente; o Kconfig do KSU não exige |
| `CONFIG_TRACEPOINTS` | y | 282 | infra de tracepoint |
| `CONFIG_HAVE_SYSCALL_TRACEPOINTS` | y | 7661 | ifdef real do `sys_enter`, `syscall_hook_manager.c:145` |
| `CONFIG_FTRACE` | y | 7669 | ftrace ligado |
| `CONFIG_FTRACE_SYSCALLS` | não definido | 7680 | este commit não usa esse ifdef. `kernel/trace/Makefile:79` só compila `trace_syscalls.o` com essa chave. O `syscall_regfunc` deste kernel vem de `tracepoint.c`, não desse arquivo |
| `CONFIG_EXT4_FS` | y | 6708 | `Kconfig:5` |
| `CONFIG_MODULES` | y | 810 | LKM |
| `CONFIG_OVERLAY_FS` | y | 6765 | pedido do contrato; o Kconfig do KSU não depende |
| `CONFIG_MODVERSIONS` | y | 816 | CRC entra no vermagic e no modpost |
| `CONFIG_TRIM_UNUSED_KSYMS` | y | 836 | export que ninguém do KMI referencia some do symvers e continua no `System.map`. É o formato dos seis faltantes |

Nenhuma chave que o `#ifdef` deste commit testa está apagada.

## 4. Vermagic — FAIL

`include/linux/vermagic.h:42-46` monta `VERMAGIC_STRING` com `UTS_RELEASE` na frente, depois SMP, preempt, `mod_unload`, `modversions`.

O `utsrelease.h` do KernelBuild 981 define `UTS_RELEASE` como `6.6.89-android15-8-4k`. Com este `.config` (`CONFIG_SMP=y` linha 312, `CONFIG_PREEMPT_BUILD=y` linha 106, `CONFIG_MODULE_UNLOAD=y` linha 813, `CONFIG_MODVERSIONS=y`) o prefixo do módulo seria:

`6.6.89-android15-8-4k SMP preempt mod_unload modversions `

A referência pedida é `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`. O release já diverge antes do sufixo de arch. Não há `.ko` compilado; o sufixo `MODULE_ARCH_VERMAGIC` não foi lido de um módulo. O prefixo basta para FAIL.

## 4b. Assinatura — o que o insmod faria

| Chave | Valor | Linha |
|---|---|---|
| `CONFIG_MODULE_SIG` | y | 820 |
| `CONFIG_MODULE_SIG_ALL` | y | 823 |
| `CONFIG_MODULE_SIG_FORCE` | não definido | 821 |
| `CONFIG_MODULE_SIG_PROTECT` | y | 822 |
| `CONFIG_MODULE_SIG_KEY` | `certs/signing_key.pem` | 7243 |
| `CONFIG_SYSTEM_TRUSTED_KEYS` | `google_gki_ab13771415_modsign_cert.pem` | 7247 |

`kernel/module/signing.c:33-34`: com `MODULE_SIG_PROTECT`, `sig_enforce` é falso. `signing.c:126-138`: módulo sem assinatura não é rejeitado por falta de chave. `main.c:1165-1172`: módulo com `sig_ok == false` só resolve símbolo que outro módulo unsigned exporta ou que está em `gki_is_module_unprotected_symbol`. `register_kprobe`, `register_kretprobe` e `tracepoint_probe_register_prio` estão nessa lista. `kernel_read` e `kernel_write` não estão.

Um `.ko` assinado pela chave Google desse keyring passaria `sig_ok` e poderia usar `kernel_read`. Um `.ko` unsigned, ou assinado por outra chave, toma `-EACCES` ao resolver `kernel_read` / `kernel_write`. Isso só aparece depois do link. O link já quebra nos seis símbolos do item 1.

## Veredito

**BLOQUEADO** para o G1b contra este symvers e este source.

O que falta para um build ter chance de passar o modpost:

- exportar, ou deixar de chamar, `kallsyms_lookup_name`, `kallsyms_lookup`, `kallsyms_lookup_size_offset`, `filp_open`, `commit_creds` e `__tracepoint_sys_enter`
- decidir `kernel_read` e `kernel_write`: ou entram na lista unprotected, ou o `.ko` é assinado por uma chave que este vmlinux aceita
- o vermagic deste build não é o da referência; um `.ko` produzido aqui não casa com `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`

Config e nomes de kprobe não são o bloqueio.
