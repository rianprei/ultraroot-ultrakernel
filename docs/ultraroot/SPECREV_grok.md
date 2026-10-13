# SPECREV — inventário UltraRoot (spec linhas 64–1001)

Read-only. Pins confrontados: KernelSU tiann `6e386044`, Magisk `e8915d9d`, APatch `e78e7b3e`, KernelPatch `f36f3d13`. Árvores em `[artefato local, fora do repo]/grok/src/`. O inventário copia as docs que já estão nesses commits (seção 7 do spec). Onde a doc e o código do mesmo commit divergem, vale o código.

Veredito: **CORRIGIR**. O miolo (su, módulos, Zygisk só no Magisk, SuperKey/SuperCall, KPM inline-hook e syscall-table-hook, WebUI no KSU e no APatch, um metamódulo por vez) existe nos pins. Oito achados abaixo impedem usar o catálogo como spec funcional sem correção.

## O que o pin confirma (não é achado)

- Magisk `e8915d9d`: su com PTY (`docs/tools.md`), DenyList como desmonte e não como hide (`spec` 180–181 bate com o projeto), stub de ocultar o app (`app/stub`, `docs/faq.md`), `resetprop --compact` (`docs/tools.md:344`), extração de boot por URL de OTA (`docs/changes.md` e `app/core/.../AppTest.kt`), fluxo Samsung AP/TAR (`docs/install.md`), terminal com TTY (`app/apk/.../ui/terminal/TerminalScreen.kt`).
- KernelSU `6e386044`: `FLAG_KSU_NO_NEW_PRIVS` em `uapi/app_profile.h:12`, `.ksurc` lido em `userspace/ksud/src/su.rs:321-323`, `sepolicy.rule` e `system.prop` em `module.rs` / `init_event.rs`, WebUI `webroot`, config key-value em `module_config`.
- APatch/KernelPatch: `post-mount.sh`, `boot-completed.sh`, `skip_mount`, `webroot` em `apd/`; demos `kpms/demo-inlinehook` e `kpms/demo-syscallhook`; SuperCall em `lkm/supercall/`. Zygisk não aparece no Rust do `apd` (só num diagrama do guia chinês `docs/cn/ap_module.md`). O inventário acerta ao marcar Zygisk nativo como não.

## Achados

### 1. Hardware breakpoint não existe neste KernelSU

Spec 433–434 lista “hardware breakpoints de processos” como funcionalidade KernelSU. A frase vem de `website/docs/guide/what-is-kernelsu.md:7` (“it’s possible to add…”). Busca em `kernel/` e `userspace/` por `hw_breakpoint`, `perf_event`, `process_vm` e `phys_to_virt`: zero. Não há implementação nem ioctl disso no pin `6e386044`. É exemplo de marketing, não função.

### 2. “Memória invisível” não existe no código, e o inventário apagou o advérbio

A mesma linha da doc (`what-is-kernelsu.md:7`) diz “access the physical memory of any process **invisibly**”. O spec 436 reescreve como “Acesso a memória de processos através de interfaces do kernel”. Não há essa interface no pin. Duas falhas: overclaim, e a ocultação (“invisibly”) saiu do texto. Marcar **FORA-do-produto**. Não implementar.

### 3. Interceptar “qualquer syscall” não é feature do manager

Spec 437. O pin oficial intercepta o próprio `su` por tracepoint `sys_enter` + kretprobe (`kernel/hook/syscall_hook_manager.c`, matriz feature 1, ACOPLADA). Troca de `sys_call_table` é do fork backslashxx, não deste pin (matriz feature 2). A doc fala em possibilidade. O inventário coloca isso na lista de funções do KernelSU, sem dizer que não há API exposta ao app.

### 4. “Proteção de imagens de partição” via KPM não está no pin

Spec 525–526. Busca por partition / verity / protect em `KernelPatch/doc/en` e `APatch/docs/pt_br`: nada. Inline-hook e syscall-table-hook existem (demos e `doc/en/module.md`). Esse exemplo de “proteção de imagens” não.

### 5. OverlayFS / meta-overlayfs não está no pin `6e386044`

Spec 368–370: “Utilizar implementações baseadas em OverlayFS” e “Integrar meta-overlayfs”. `userspace/meta-overlayfs` não existe nesta árvore. `ksud` não contém `overlayfs` nem `meta-overlay`. A doc aponta `github.com/tiann/KernelSU/tree/main/userspace/meta-overlayfs`, que é outro tree, não este commit. Matriz feature 18: overlayfs AUSENTE nos nove motores. O spec 381–382 avisa que sem metamódulo não há mount, e não avisa que o artefato oficial citado está fora do pin. Dependência externa não sinalizada.

No mesmo parágrafo a doc diz que a abordagem “reduces detection surface” (`website/docs/guide/module.md:179`). O inventário lista o mecanismo e omite esse motivo. Marcar o motivo **FORA-do-produto**. O mount systemless em si pode permanecer, com a frase de detecção cortada e a dependência externa explícita.

### 6. Magic mount compatível não é função do KernelSU deste pin

Spec 370: “Permitir implementações de montagem compatíveis com magic mount”, na seção KernelSU. O algoritmo (tmpfs + bind + `.replace`) está no Magisk e está AUSENTE no KSU (matriz feature 16). `metamodule.md` só usa `meta-magicmount` como exemplo de nome. Backend errado se lido como capacidade do pin.

### 7. “Não modificar o contexto SELinux” esconde bypass por hook

Spec 461–462. A fonte no mesmo pin é `APatch/docs/pt_br/faq_pt_br.md:42`: “KernelPatch não modifica o contexto do SELinux **e ignora o SELinux via hook**”, para rootear a thread dentro do contexto do app. O código faz isso: `kernel/patch/patch.c:87` chama `bypass_selinux()`; `kernel/patch/common/accctl.c:239-258` aplica hook em `avc_denied` e `slow_avc_audit`; `avc_denied_replace` (`accctl.c:194-209`) põe `allowed = 0xffffffff`, zera `auditallow`/`auditdeny` e retorna 0 quando `sel_allow` ou `priv_sel_allow`. O inventário ficou só com a metade cosmética (“sem modificar o contexto”). Isso é ocultação do domínio de root por bypass de AVC. Marcar **FORA-do-produto**. `selinux_hide_init` está comentado (`patch.c:61`); o bypass de AVC não está.

### 8. BusyBox do KernelSU é o binário do Magisk, e a matriz não diz

Spec 817, coluna KernelSU = SIM. `website/docs/guide/module.md:33`: “using the binary file compiled directly from the Magisk project”. O ksud embute o asset e extrai em `/data/adb/ksu/bin/busybox` (`userspace/ksud/src/assets.rs:12-23`). A seção 2 do inventário não lista essa procedência. A seção 3.8 sinaliza as deps do APatch; a do KSU ficou de fora. `resetprop` do KSU é outra coisa: symlink para o próprio `ksud` (`assets.rs:26-29`), não o binário Magisk.

## Fora da lista de disfarce

- Stub “Hide the Magisk app” (spec 191–192) é ocultação de identidade do aplicativo, e o próprio inventário diz que não esconde o root. Existe no pin. Não está disfarçado. Decisão de produto se entra no UltraRoot; não é overclaim.
- DenyList (spec 175–181) e umount por app do KSU (spec 330–339) já dizem que não são hide universal. O umount por uid está no kernel do pin (matriz feature 21). Não marcar como disfarce.

## Veredito

**CORRIGIR** antes de tratar o inventário como spec funcional. Cortar os itens 1–4 (não existem no pin). No item 5, declarar meta-overlayfs como artefato externo ausente em `6e386044` e cortar “reduces detection surface”. No item 6, tirar magic mount da coluna KernelSU. No item 7, não vender bypass de AVC como “não mexe no contexto”. No item 8, escrever a procedência Magisk do BusyBox do KSU.
