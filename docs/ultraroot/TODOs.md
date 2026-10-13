# TODOs — UltraRoot: features obrigatórias

> Fonte: spec rev.7 `[fora do repo]/docs/superpowers/specs/2026-10-11-ultraroot-design.md` (status APROVADO pelo dono em 2026-10-11; gate §13 cumprido), catálogo `docs/ultraroot/CAPABILITIES.md` (44 OBRIG, mais 8 OBRIG-PROPOSTO da seção 6) e inventário §1–§8 do mesmo spec.
> Data-base: 2026-10-11; atualização de estado: 2026-10-13. Os registros antigos abaixo são históricos; aparelho hoje: NÃO VERIFICADO / NÃO TESTADO, sem consulta. Estados fora das provas de H4: NÃO VERIFICADOS nesta rodada. Regra §0 rev.7: toda capacidade REAL é obrigatória. EXCLUDED, NÃO-REAL e FORA ficam na seção 10, com motivo.
> Revisão de cobertura: ACEITO pelo Codex #4 na rodada 4 (517/517 = 100%, zero violações). Relatório: `docs/ultraroot/REVIEW_TODOs_rodada4_codex4.md`. Histórico: 95,48% (rodada 1), 99,61% (rodada 2), 100% com violação (e) (rodada 3).

**Legenda**
- `[ ]` NOT_STARTED · `[~]` IN_PROGRESS, ou IMPLEMENTED sem validação · `[x]` validada (HOST_TESTED ou DEVICE_TESTED) com evidência citada · `[!]` bloqueado por gate do dono ou por dependência
- Enum de implementação: `NOT_STARTED | IN_PROGRESS | IMPLEMENTED | INTEGRATED | EXTERNAL | EXCLUDED`
- Enum de validação: `NOT_TESTED | HOST_TESTED | DEVICE_TESTED | FAILED | UNKNOWN`
- `[DEVICE]` exige "sim, <passo>" presencial. Sem tag: executável no host.
- IMPL e VALID são independentes. Compilar não é validar. Aceitar ZIP não é compat. Sem evidência, não marcar `[x]`.

---

## 0. Gates do dono
- [x] Aprovar o rev.7 do spec (§13): aprovado pelo dono em 2026-10-11 (mensagem "sim pros 3"). Plano novo de Manager, adapter e performance liberado a partir daí.
- [!] Nunca destruir dados do usuário. Backup e guard antes de qualquer gravação.
- [!] Toda operação no aparelho: "sim, <passo>". Sem isso não há instalação, grant de root, gravação de partição nem flash. Sem unlock, wipe nem flash de teste sem "sim".

---

## 1. SP1 — núcleo root + manager

### 1.1 Feito (operações e evidências)
- [x] Par C0.3 no host: imagem `2977e1dc…`, cert `efb8d49f…`, gate 557 módulos com 0 mismatch. Evidência: `docs/history/research/phase4/C03_stablekey_result.md`.
- [x] Artefato C0.1 no host: `[artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img`, sha256 `74090569d7fc4883159ea1fe14a484c4e3a3b14458aa84605aa3f520d78a1120`, header v4, 67108864 bytes, `kernel_size` 14634767, data 2026-10-11. Obsoleto como alvo operacional, segundo o próprio arquivo. Evidência: `docs/history/research/phase4/C01_device_plan.md` (spec §2).
- [x] [DEVICE] Histórico da base 2026-10-11: boot C0.1 no aparelho, recorte: 62 linhas `KernelSU:`, `dmesg_new_errors.sh` exit 0 nesse arquivo. Evidência: `docs/history/research/phase4/c01_dmesg.txt`. Continua UNKNOWN: 429 módulos e "boot inteiro sem erro". Aparelho em 2026-10-13: NÃO VERIFICADO.
- [x] Histórico da base 2026-10-11: APK UltraRoot vc40000 verificado no host: V2-only, cert `efb8d49f…`, pacote `me.weishu.kernelsu`, sha256 `7a3ae0b1…`. Cross-review Codex #4: ACEITO em `docs/ultraroot/REVIEW_vc40000_codex4.md`.
- [x] Backup do reskin antes da troca: `[fora do repo]/ultraroot/reskin-inc2b-vc32673.apk`, sha256 `3429e23a…`.
- [x] [DEVICE] Histórico 2026-10-11: APK vc40000 instalado no aparelho por `adb install -r` (Success; `dumpsys` = versionCode 40000). Autorizado por "sim, pode fazer os 2". Evidência: `docs/history/evidence/2026-10-11_install_vc40000.txt`. Operação, não capacidade: validação funcional segue em aberto. Estado mais recente documentado: ui8 (vc40008), 2026-10-12; sem medida hoje, NÃO VERIFICADO em 2026-10-13.
- [~] [DEVICE] C0.3 no aparelho: o handoff v2 afirma flash e boot. Sem log de boot citado em `research/`, não vira `[x]`. Estado atual: NÃO VERIFICADO (sem nova medida em 2026-10-13).

### Estado provado no host em 2026-10-13
- Último estado documentado de aparelho: ui8 (vc40008), 2026-10-12, em `plan.md:40-46`; NÃO foi medido hoje. Aparelho em 2026-10-13: NÃO VERIFICADO.
- [x] ui10 no host: código `versionCode = 40010`, `versionName = "0.2.0-ui10"` (`app/build.gradle.kts:21-22`); APK alinhado e não assinado `out/UltraRoot-own-ui10-aligned.apk`, SHA-256 `cc7212b68e7c5bcefbe3b38fa730af1362854e4ac2ecbc9588e2460493f55894`. Assinatura pendente do dono; ui10 não instalado, aparelho NÃO TESTADO. Evidência: `docs/history/reports/h3_build_ui10_2026-10-13.txt:39-76`.
- [x] Estado honesto: `classifyBackend` em `core-backend/src/main/java/dev/superroot/core/BackendState.kt:9-23`, 7 testes de host em `BackendStateTest.kt`. Só host; aparelho NÃO VERIFICADO; ligação à tela pendente (H7).
- [x] H1/H1F: flags GET_INFO (LKM, MANAGER, LATE_LOAD), `decodeKsuMode` e `decodeKernelMode` em `core-backend/src/main/java/dev/superroot/core/KsuMode.kt:18-29`, `readMode` em `KsuBackend.kt:70-76`; 10 testes de host em `KsuModeTest.kt`. JNI real no aparelho: NÃO VERIFICADO, depende de D2.
- [x] Busca e filtro: `presentRootAccess(query, filter)` em `app/src/main/java/dev/superroot/ultraroot/ui/RootAccessState.kt:24-72`; 17 testes de filtro + 5 de presenter em host. A tela chama `presentRootAccess(value)` sem esses parâmetros (`RootAccessScreen.kt:57`): integração pendente (H7); aparelho NÃO TESTADO.
- [x] H2/H2F/H2R1: grant/revoke por `RootGrantPort` (interface sem implementação nativa) e `RootGrantFlow` (confirmação e resultado por app), 15 testes de host com fakes. Sem JNI de escrita, sem ligação à tela; aparelho NÃO TESTADO. Fontes: `core-backend/src/main/java/dev/superroot/core/RootGrantPort.kt:3-6`; `app/src/main/java/dev/superroot/ultraroot/ui/RootGrantFlow.kt:27-57`.
- [!] Origem do fd `[ksu_driver]`: NÃO MEDIDA; diagnóstico D2 e decisão E2 pendentes (`plan.md:46,93,105`). CONFIG_KSU do kernel do aparelho lake: NÃO VERIFICADO; nenhuma modalidade de integração é afirmada.

### 1.2 Em andamento e pendente
- [x] Três ajustes da auditoria FINAL_AUDIT, host-only (Codex #4; evidência `docs/ultraroot/SP1_AUDIT_FIXES_codex4.md`): (a) `guard_hashes` e plano apontam para C0.3, não C0.1; (b) rollback reativa o slot b se B ficar não bootável; (c) `accept_ksu_root.sh` distingue KSU de Magisk.
- [ ] Revisão cruzada dos ajustes (a), (b) e (c) antes de qualquer flash. Autor atual: Codex #4; revisor: grok (pendente, quota).
- [!] [DEVICE] manager-grant: conceder root a um app de teste e rodar `accept_ksu_root.sh`; aceite `uid=0` + evidência KSU + contexto sem magisk. Histórico 2026-10-12: manager uid 10296 e allowlist lida, vazia (`plan.md:40-44`); NÃO medido hoje. Atualização 2026-10-13: H2 só host com fakes, sem tela/JNI de escrita; grant no aparelho NÃO TESTADO. Gate: "sim, abrir + grant teste".
- [!] [DEVICE] Coroação: KSU vira dono do `su`. Desligar o Magisk só depois de migrar os 8 módulos (1.3).

### 1.3 Migração dos 8 módulos Magisk (handoff v2)
- [ ] [DEVICE] `hosts`: nativo KSU
- [ ] [DEVICE] `yt-noads`: nativo KSU
- [ ] [DEVICE] `poco_perfgov`: nativo KSU
- [ ] [DEVICE] `termuxadb_rootport`: nativo KSU
- [!] `zygisk_vector`: exige Zygisk (SP2)
- [!] `bc-poc`: exige Zygisk (SP2)
- [!] `always-trust-user-certs`: exige adapter (SP3)
- [!] `tabs-ca-inject`: exige adapter (SP3)

### 1.4 Paridade modular por etapas (§11)
Cada item validado separadamente. ZIP aceito não prova nenhum deles.
- [ ] Scripts (`customize.sh`, `post-fs-data.sh`, `service.sh`, `uninstall.sh`, `action.sh`) rodam no estágio correto
- [ ] `system.prop`: aplicado e removido junto com o módulo
- [ ] `sepolicy.rule`: regra carregada, sem allow global
- [ ] Mount systemless e `.replace`: arquivo aparece sem escrever na partição original
- [ ] Estágios de init: post-fs-data, post-mount, service, boot-completed, skip_mount
- [ ] Magic mount (Magisk) e overlayfs nunca ativos juntos
- [ ] Cada feature declarada como nativa, dependente de metamódulo ou exigindo adapter. Sem "compat universal com ZIP Magisk"

---

## 2. Magisk: MagiskAdapter e runtime Magisk (`MG-*`, 20 OBRIG)

### MG-SU-GRANT: conceder, revogar e listar root por app e por shell/ADB
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/details.md:67`, `native/src/include/consts.hpp:7` · Aceite: policy de um uid de teste muda e volta
- [ ] [DEVICE] Conceder root a app autorizado
- [ ] Solicitar autorização quando um app chama `su`
- [ ] [DEVICE] Revogar root
- [ ] Gerenciar acesso root por app
- [ ] [DEVICE] Controlar root oferecido ao shell e ao ADB
- [ ] Listar aplicativos autorizados (tela e CLI)
- [ ] Registrar e administrar estados de autorização

### MG-SU-EXEC: `su` com shell, UID/GID/grupos, contexto SELinux, ambiente e capabilities
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `native/src/core/su/su.cpp`, `docs/tools.md` · Aceite: `su -c id` com e sem os limites
- [ ] Executar comandos como root
- [ ] Disponibilizar o executável `su`
- [ ] Selecionar shell da sessão
- [ ] Selecionar UID de destino e grupos
- [ ] Contexto SELinux da sessão `su`
- [ ] Preservar o ambiente quando solicitado
- [ ] Restringir capabilities Linux, inclusive em UID 0

### MG-SU-NS: namespace de montagem a partir de PID
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md:305` (`--target PID`) · Aceite: o processo alvo vê o namespace pedido
- [ ] Selecionar o namespace de montagem a partir de um PID

### MG-SU-PTY: PTY e shell interativo
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md:299` · Aceite: sessão interativa aloca pty
- [ ] Shell interativo com alocação de pseudoterminal

### MG-SU-DAEMON: daemon, versões e estado de autorização
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md`, `docs/details.md` · Aceite: versão lida bate com o binário instalado
- [ ] Manter serviço e daemon próprios
- [ ] Consultar versão do binário e do daemon
- [ ] Iniciar e parar o daemon pelas interfaces suportadas

### MG-MOD-LIFE: ciclo de módulos ZIP (sem repositório central)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md` · Aceite: fixture instala e remove
- [ ] Instalar módulo a partir de ZIP
- [ ] Desinstalar módulo
- [ ] Ativar e desativar módulo
- [ ] Executar `action.sh` pela interface
- [ ] Ler metadados: nome, ID, versão, autor
- [ ] Consultar `updateJson` quando declarado
- [ ] Instalar atualização fornecida pelo próprio módulo
- [ ] Aceitar ZIP com formatos de compressão compatíveis
- [ ] Ciclo inteiro validado com fixture (ZIP aceito não prova o ciclo)

### MG-MOD-SCRIPT: scripts de instalação, boot e callbacks
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md` · Aceite: cada script roda no estágio dele
- [ ] `customize.sh` na instalação
- [ ] `post-fs-data.sh` no estágio correto
- [ ] `service.sh` no boot de serviços
- [ ] `uninstall.sh` na remoção
- [ ] Callbacks de estágio: post-fs-data, service, boot-complete, zygote-restart

### MG-MOD-PROP: `system.prop` de módulos
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md` · Aceite: prop aparece com o módulo e some quando ele sai
- [ ] Aplicar `system.prop` do módulo

### MG-MOD-SEP: `sepolicy.rule`
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md:137` · Aceite: regra do fixture é carregada e não abre allow global
- [ ] Aplicar regras de `sepolicy.rule`

### MG-MOD-MOUNT: magic mount, `.replace` e systemless (algoritmo do Magisk, não do KSU)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md` · Aceite: arquivo do módulo aparece sem escrever a partição
- [ ] Montagem systemless por scripts e configuração
- [ ] Substituir arquivo do sistema sem alterar a partição original, quando permitido
- [ ] Suportar o mecanismo `.replace`
- [ ] Alterar diretórios do sistema por montagem

### MG-MOD-OVLAYD: `overlay.d` no ramdisk
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/guides.md:275` · Aceite: rc do overlay entra no boot de teste
- [ ] Integrar `overlay.d` para personalizações compatíveis no boot

### MG-ZYG: Zygisk nativo do Magisk
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ZygiskAdapter sobre Magisk · Device-gate: sim · **Gate: SP2** · Origem: Magisk `e8915d9d` `native/src/core/zygisk/module.cpp` · Aceite: módulo vazio de Zygisk carrega num processo de teste
- [ ] Injetar componentes compatíveis no Zygote
- [ ] Infraestrutura para módulos Zygisk
- [ ] Módulos com bibliotecas nativas por ABI
- [ ] API de desenvolvimento Zygisk
- [ ] Gerenciar o estado do Zygisk
- [ ] Suportar APIs Zygisk por versão
- [ ] Hooking de processos Android nos processos compatíveis

### MG-DENY: DenyList
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md:272` · Aceite: o alvo deixa de ver o mount do módulo. Não é garantia de ocultação do root
- [ ] Manter a lista de pacotes e processos
- [ ] Ativar e desativar a aplicação da lista
- [ ] Adicionar alvos
- [ ] Remover alvos
- [ ] Listar alvos configurados
- [ ] Aplicar mudanças de namespace de montagem nos alvos
- [ ] Desmontar alterações do Magisk nos alvos
- [ ] Consultar o estado de aplicação
- [ ] Gerenciar pela CLI

### MG-BOOT: magiskboot
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md` (magiskboot) · Aceite: unpack e repack de uma imagem de fixture fecham o roundtrip
- [ ] Desempacotar imagem de boot
- [ ] Reempacotar imagem de boot
- [ ] Interpretar cabeçalhos de imagem
- [ ] Extrair kernel, ramdisk e DTB
- [ ] CPIO: listar, extrair, adicionar, remover, substituir, mover e criar links
- [ ] Verificar estado do ramdisk e aplicar patch nele
- [ ] Backup e restauração do ramdisk, quando aplicável
- [ ] Inspecionar e manipular componentes DTB
- [ ] Patch hexadecimal em binários
- [ ] Comprimir e descomprimir: gzip, LZ4, LZMA, XZ, BZip2
- [ ] Formatos específicos de boot e firmware
- [ ] Estruturas AVB nas operações documentadas
- [ ] Preservar os componentes originais necessários ao repack

### MG-INIT: magiskinit
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md:162` · Aceite: boot de fixture chega ao daemon
- [ ] Substituir o init do ramdisk modificado
- [ ] Configuração inicial do ambiente Magisk
- [ ] Operações prévias à inicialização normal do Android
- [ ] Suportar layouts de init suportados
- [ ] Suportar system-as-root
- [ ] Participar da injeção e configuração de componentes
- [ ] Aplicar as adaptações de boot suportadas pela versão

### MG-POLICY: magiskpolicy e supolicy
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md` (magiskpolicy) · Aceite: regra de teste aplica e não abre allow global
- [ ] Carregar políticas SELinux
- [ ] Inspecionar e salvar políticas
- [ ] Aplicar modificações à política em execução, quando suportado
- [ ] Aplicar regras de arquivo e contextos de arquivo
- [ ] Regras allow, deny, auditallow e dontaudit
- [ ] Manipular tipos e atributos; regras estendidas
- [ ] Sintaxe da ferramenta e alias `supolicy`

### MG-PROP: resetprop
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md:344` · Aceite: get, set e wait em prop de teste
- [ ] Consultar propriedades Android
- [ ] Definir propriedades
- [ ] Modificar `ro.*` em condições suportadas
- [ ] Manipular propriedades persistentes
- [ ] Operar sem depender exclusivamente do property_service
- [ ] Aguardar propriedade atingir condição (`wait`)
- [ ] `--compact` nas versões compatíveis

### MG-CLI: CLI `magisk`
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/tools.md`, `native/src/core/applets.cpp` · Aceite: `magisk -v` e um applet
- [ ] Executar comandos `magisk`; listar applets
- [ ] `su` e `resetprop` como applets
- [ ] Instalar e remover módulos pela CLI, inclusive remover todos
- [ ] Administrar a DenyList pela CLI
- [ ] Restaurar contextos SELinux de arquivos
- [ ] Clonar atributos: permissões, dono, contexto
- [ ] Consultar caminhos temporários do Magisk
- [ ] Consultar e operar dados internos pelas interfaces documentadas
- [ ] Ferramentas auxiliares próprias do projeto

### MG-APP-PATCH: patch de boot pelo app
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `docs/install.md:74`, `docs/tools.md:77` · Aceite: dry-run numa imagem local, sem flash
- [ ] Selecionar e corrigir imagem de boot (dry-run antes)
- [ ] [DEVICE] Instalar a imagem corrigida pelo procedimento aplicável
- [ ] Fluxo Samsung AP/TAR: fora do alvo do lake até ser suportado
- [ ] Extrair imagem de boot de URL OTA nas versões que suportam
- [ ] [DEVICE] Gravar partição só com confirmação explícita ("sim")

### MG-APP-TERM: terminal embutido com TTY e ANSI
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate: sim · Origem: Magisk `e8915d9d` `app/apk/.../ui/terminal/TerminalScreen.kt:36` · Aceite: um comando roda no terminal embutido
- [ ] Terminal com TTY e suporte ANSI

---

## 3. KernelSU: KsuBackend (`KSU-*`, 11 OBRIG) e seeds

### Seed KSU-core: KernelSU no kernel (fora dos 44); modalidade no lake NÃO VERIFICADA
`IMPL: IMPLEMENTED · VALID: DEVICE_TESTED (recorte histórico da base 2026-10-11); aparelho hoje NÃO VERIFICADO` · Origem: KernelSU `6e386044`, GKI 6.6.89 · Evidência: `docs/history/research/phase4/c01_dmesg.txt`, sha256 `0aad0b4d…`
- [x] [DEVICE] Histórico da base 2026-10-11: KSU no boot C0.1 (recorte de 17,0 s a 88,5 s, 62 linhas `KernelSU:`). Evidência: `docs/history/research/phase4/c01_dmesg.txt` Aparelho hoje NÃO VERIFICADO.
- [ ] UNKNOWN: contagem de 429 módulos
- [ ] UNKNOWN: boot inteiro sem erro (recorte não começa em t=0)
- [ ] Variante GKI e não-GKI, conforme kernel e procedimento (escopo atual: lake, GKI 6.6.89)

### Seed manager-grant: manager autorizado pelo kernel (fora dos 44)
`IMPL: IMPLEMENTED (host) · VALID: NOT_TESTED` · Aceite: `docs/ultraroot/accept_ksu_root.sh`
- [x] Par host C0.3 e cert (seção 1.1)
- [!] [DEVICE] Grant no aparelho: NÃO TESTADO; H2 cobre só a lógica de host com fakes (seção 1.2).

### KSU-ROOT: root pelo kernel, allowlist, `su`, versão e UAPI
IMPL IN_PROGRESS · VALID HOST_TESTED (parcial: classificador, flags e fluxo com fakes); aparelho NÃO TESTADO · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `uapi/supercall.h` (allow list), `manager/.../ksu.h` · Aceite: `su` atendido pelo KSU, uid=0, sem magisk. Atualização 2026-10-13; histórico 2026-10-11: VALID NOT_TESTED.
- [~] Leitura versão/UAPI em `ultraroot-app/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:62-68`; classificador puro HOST_TESTED (7 testes); JNI real no host e aparelho NÃO VERIFICADO. Atualização 2026-10-13; histórico 2026-10-11: cópia `ultraroot-mgr/`.
- [ ] [DEVICE] Conceder root pelo manager (allowlist). Aparelho NÃO TESTADO; H2 só host.
- [ ] [DEVICE] Revogar root pelo manager. Aparelho NÃO TESTADO; H2 só host.
- [ ] Disponibilizar `su` e expor interfaces KSU às ferramentas compatíveis
- [ ] Aplicar políticas de acesso com suporte do kernel
- [ ] Hook interno de `su` (tracepoint `sys_enter` e kretprobe) como mecanismo do root KSU, não como API
- [ ] Ioctl SuperCall com reboot magic (F12)
- [x] Detectar backend: classificador puro `BackendState.kt:9-23`, 7 testes, HOST_TESTED em 2026-10-13; integração na tela pendente; aparelho NÃO VERIFICADO.
- [~] Listagem lida pela tela (`RootAccessScreen.kt:44,57`); busca/filtro só no presenter, HOST_TESTED (17 + 5). Atualização 2026-10-13; validação no aparelho NÃO TESTADO.
- [ ] Interfaces de desenvolvimento para recursos avançados dependentes do kernel (KSU)

### KSU-PROFILE: app profile (UID, GID, grupos, caps, SELinux, NO_NEW_PRIVS)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `uapi/app_profile.h:12` · Aceite: profile de um uid restringe o `su`
- [ ] Controlar o UID usado pelo `su` por app
- [ ] Controlar GID e grupos suplementares
- [ ] Configurar capabilities Linux
- [ ] Configurar contexto SELinux do `su` e regras específicas
- [ ] Restringir privilégios a privilégios parciais, não root irrestrito
- [ ] Opção NO_NEW_PRIVS em configurações suportadas
- [ ] Aplicar a política de perfil pelo mecanismo do kernel
- [ ] Opções específicas por app, inclusive comportamento do sistema de módulos para o app
- Nota: perfil não substitui as permissões Android que o app já possui. Não é a DenyList.

### KSU-UMOUNT: desmontar módulos por app (não é hide universal)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `userspace/ksud/src/cli.rs` (umount) · Aceite: app marcado não vê o mount do módulo
- [ ] Controlar a exposição de módulos por app
- [ ] Desmontar alterações de módulos em processos selecionados
- [ ] Configuração padrão de desmontagem
- [ ] Configuração por app em cenários suportados
- [ ] Listas de inclusão e exclusão
- [ ] Condicionar às interfaces e à versão do kernel

### KSU-MOD: sistema de módulos em `/data/adb/modules`
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `website/docs/guide/module.md:8` · Aceite: fixture executa script e prop sem metamódulo
- [ ] Instalar ZIP; gerenciar; ativar, desativar e remover
- [ ] Layout `/data/adb/modules` e `module.prop`
- [ ] `customize.sh`, `post-fs-data.sh`, `service.sh`
- [ ] `system.prop` e `sepolicy.rule`
- [ ] Diretórios de arquivos do sistema (sem mount de `/system`, ver KSU-META)
- [ ] Atualizações e metadados
- [ ] Identificar que o módulo roda no KernelSU
- [ ] Adaptar comportamento ao backend

### KSU-META: metamódulos (um ativo por vez)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `website/docs/guide/metamodule.md` · Aceite: dois metamódulos não ficam ativos juntos
- [ ] Delegar montagem a metamódulo
- [ ] Mecanismo extensível de montagem, com estratégias com e sem montagem
- [ ] Implementações customizadas de montagem
- [ ] Executar scripts especiais do metamódulo antes dos normais
- [ ] Hooks de instalação, montagem e limpeza
- [ ] Exibir o metamódulo ativo no manager
- [ ] Evitar conflito entre metamódulos
- [ ] Trocar a implementação por procedimento apropriado
- [!] Mount de `/system` depende de artefato externo medido. `meta-overlayfs` não está no pin `6e386044`

### KSU-WEBUI: WebUI de módulos
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `website/docs/guide/module-webui.md:7` · Aceite: página do fixture abre
- [ ] Renderizar `webroot` com `index.html` no manager
- [ ] Permitir interação do usuário com o módulo
- [ ] APIs de shell condicionadas à política de confiança
- [ ] Painéis customizados por módulo

### KSU-CFG: configuração key-value de módulos
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `userspace/ksud/src/cli.rs` (`module_config`) · Aceite: valor temporário mascara o persistente e some no boot
- [ ] Armazenar configuração persistente
- [ ] Armazenar configuração temporária
- [ ] Persistente sobrevive a reboot
- [ ] Temporária é limpa no estágio previsto do boot
- [ ] Temporária tem prioridade sobre persistente com a mesma chave
- [ ] Interface para módulos e ferramentas compatíveis

### KSU-RESCUE: bootloop e restauração
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `website/docs/guide/rescue-from-bootloop.md` · Aceite: módulo ruim desliga no próximo boot de teste
- [ ] Documentar procedimento para falha causada por módulo
- [ ] Ajudar a diagnosticar falha de inicialização
- [ ] Procedimentos com ferramentas externas
- [ ] Desativar ou remover módulo problemático
- [ ] Restaurar imagem de boot (ver UR-BACKUP)
- [ ] Explicar cenários de incompatibilidade e retorno à imagem stock
- Nota: sem garantia de recuperação automática. O guia não garante safe mode.

### KSU-KSURC: `.ksurc` do shell
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `userspace/ksud/src/su.rs:321`, `defs.rs:23` · Aceite: shell lê o arquivo de teste
- [ ] Ler `/data/adb/ksu/.ksurc` quando a ENV não está definida
- [ ] Ajustar inicialização de shell compatível
- [ ] Integrar com o ambiente do módulo e das ferramentas autorizadas

### KSU-BB: BusyBox
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Device-gate: sim · Origem: KernelSU `6e386044` `website/docs/guide/module.md:34`, `userspace/ksud/src/assets.rs:12` · Aceite: `busybox` em `/data/adb/ksu/bin` responde
- [ ] Disponibilizar `/data/adb/ksu/bin/busybox`
- Nota: procedência Magisk, não implementação própria do KSU.

### KSU-RESETPROP: resetprop do ksud (symlink)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `userspace/ksud/src/assets.rs:26-29` · Aceite: set e get pelo binário do ksud
- [ ] Set e get de propriedades pelo resetprop do ksud

---

## 4. APatch e KernelPatch (`AP-*`, 7 OBRIG)

### AP-ROOT: root por SuperKey e SuperCall
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · Device-gate: sim · Origem: KernelPatch `f36f3d13` `doc/en/super-syscall.md` · Aceite: chamada com chave errada não eleva
- [ ] [DEVICE] Conceder e revogar root a apps autorizados
- [ ] Controlar quais apps têm root
- [ ] Administrar concessões e revogações
- [ ] Gerar e configurar SuperKey (não registrar em log)
- [ ] SuperKey fora de logs e de interfaces públicas (UI, CLI, WebUI, código público)
- [ ] SuperCall para componentes autorizados
- [ ] Verificar credencial e rejeitar chave errada
- [ ] Interfaces para operações privilegiadas e módulos
- [ ] Controlar o comportamento de root pelos componentes APatch e KernelPatch

### AP-BOOT: patch de `boot.img` (sem presumir `init_boot` nem `vendor_boot`)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · Device-gate: sim · Origem: KernelPatch `f36f3d13` `doc/en/guide.md` (kpimg) · Aceite: dry-run em boot.img de fixture
- [ ] Selecionar `boot.img`
- [ ] Patch automático pelo manager
- [ ] Configurar a SuperKey durante o patch da imagem, no fluxo automático e no manual, sem registrar a chave em log
- [ ] Patch manual quando necessário
- [ ] Gerar imagem corrigida para device compatível
- [ ] Atualizar componentes KernelPatch
- [ ] Procedimento de atualização documentado para componentes KernelPatch: versão, passos e retorno
- [ ] Detectar requisitos de config do kernel e verificar suporte antes de iniciar
- [ ] Restaurar a partir do backup original (UR-BACKUP)
- [ ] Verificar pré-requisitos do bootloader e do kernel antes de instalar
- [ ] Verificar que o kernel atende aos requisitos de patch e aos símbolos necessários antes de gerar a imagem

### AP-APM: módulos APM (`ApmRuntime`, separado de KSU e Magisk)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Device-gate: sim · Origem: APatch `e78e7b3e` `apd/src/event.rs:209`, `apd/src/module.rs:407` · Aceite: cada estágio do fixture, separado
- [ ] Instalar, gerenciar, ativar, desativar e remover módulos
- [ ] `module.prop` e diretório `system`
- [ ] `skip_mount`; arquivos `disable` e `remove`
- [ ] `post-fs-data.sh`, `post-mount.sh`, `service.sh`, `boot-completed.sh`
- [ ] `uninstall.sh`, `action.sh` e scripts de instalação
- [ ] `system.prop` via resetprop; `sepolicy.rule`
- [ ] Contrato APM por módulo (ZIP aceito não prova os estágios)

### AP-KPM: KernelPatch modules
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KpmRuntime · Device-gate: sim · Origem: KernelPatch `f36f3d13` `kpms/demo-inlinehook`, `kpms/demo-syscallhook`, `doc/en/module.md` · Aceite: demo de hook carrega e descarrega
- [ ] Carregar KPM pelos métodos suportados
- [ ] Inline-hook, quando implementado e compatível
- [ ] Syscall-table-hook, quando implementado e compatível
- [ ] Interfaces de gerenciamento de KPM
- [ ] Incorporar KPM ao kernel corrigido, pelo procedimento da versão
- [ ] Listar operações que o KPM faz e o APM não faz, com evidência por versão
- [ ] Código no kernelspace isolado em teste, por alto risco
- [!] Install persistente de KPM só se o guia da versão tiver. Verificar antes de afirmar

### AP-WEBUI: WebUI de módulos APM
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Device-gate: sim · Origem: APatch `e78e7b3e` `app/src/main/java/me/bmax/apatch/ui/WebUIActivity.kt` · Aceite: página do fixture abre
- [ ] Exibir WebUI de módulos APM e KPM compatíveis
- [ ] Reutilizar o modelo WebUI do KernelSU quando o módulo declarar
- [ ] Interação com módulos e gerenciamento de módulos pela UI

### AP-BB: BusyBox ash standalone
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Device-gate: sim · Origem: APatch `e78e7b3e` `docs/cn/ap_module.md`, `apd/` · Aceite: script do fixture roda em ash standalone
- [ ] Fornecer applets BusyBox para scripts de módulos
- [ ] Executar scripts no ash standalone
- [ ] Suporte relacionado a SELinux nas ferramentas fornecidas
- [ ] Integrar ao sistema de scripts e módulos
- [ ] Usar as ferramentas auxiliares do ambiente APatch
- [ ] Permitir configuração do ambiente de execução dos scripts (variáveis, PATH, ash)

### AP-DIAG: estado e diagnóstico
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · Device-gate: sim · Origem: APatch `e78e7b3e` manager · Aceite: versão lida bate com o binário
- [ ] Consultar estado do APatch e versões de APatch e KernelPatch
- [ ] Diagnosticar carregamento de módulos
- [ ] Diagnosticar erros de atualização e de permissões
- [ ] Mostrar informações úteis durante o boot
- [ ] Procedimentos de recuperação de bootloop do APatch
- Texto de diagnóstico vai para UR-LOG, sem log paralelo

---

## 5. UltraRoot unificado (`UR-*`, 6 OBRIG)

### UR-PERF-READ: leitura de CPU, GPU, frequência, governor e thermal
IMPL IN_PROGRESS · VALID NOT_TESTED · Adapter: KsuBackend no lake; leitura de sysfs · Device-gate: sim · Origem: requisito do spec §6 e `research/phase4/HW_PERF_MATRIX_lake.md` · Aceite: valor exibido bate com o node lido; 0 não vira frequência
- [~] Atualização 2026-10-13: `KernelPerformanceScreen.kt:41,61-93` exibe versão/UAPI brutas, inclusive zero; não usa `classifyBackend` nem `readMode`, não lê sysfs de CPU/GPU. H7 pendente; aparelho NÃO VERIFICADO. Histórico 2026-10-11: tela do incremento 1 mostrava versão/UAPI.
- [ ] Informações da CPU
- [ ] Informações da GPU
- [ ] Memória e armazenamento
- [ ] Frequências e governors, quando expostos
- [ ] Devfreq, CPUFreq e parâmetros compatíveis
- [ ] Temperatura e throttling
- [ ] Monitoramento de potência, quando disponível
- [ ] Investigar a cadeia: userspace → driver → kernel → device tree → reguladores → clock HW → tabelas de tensão → firmware → limites físicos
- [ ] Ler frequência, tensão, OPP e limites mínimo e máximo quando expostos (somente leitura)
- Nota: não escreve OPP.

### UR-PERF-WRITE: perfis, aplicar e reverter (Performance Engine)
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: Performance Engine · Device-gate: sim · Origem: requisito do spec §5 e §6 · Aceite: antes×depois contra baseline gravado
- [ ] Perfis de desempenho e de economia
- [ ] Aplicar e reverter só controles classificados como suportados
- [ ] Persistir só com autorização; reverter; recuperar após falha
- [ ] Controles de memória, IO, rede, sched, zRAM, swap e cgroups, quando suportados
- [ ] Integração com gerenciadores de kernel
- [ ] Benchmark antes×depois contra baseline gravado
- [ ] Achar e documentar o máximo sustentável demonstrável contra baseline: ganho, temperatura, throttle, potência, estabilidade
- [ ] Rollback de configuração (não apaga o log)
- [ ] Classificar cada ajuste nas 6 classes do §6 (`HW_PERF_MATRIX_lake.md`)
  - (1) inspeção read-only; (2) controle runtime suportado; (3) não suportado ou inefetivo; (4) exige trabalho em kernel, DT ou driver; (5) exige HW ou firmware indisponível; (6) experimental não validado
- [ ] Testes de estabilidade, latência, throughput e sustentado
- [!] Overclock e undervolt só com margem medida. DVFS do lake bloqueia no driver; margem UNKNOWN
- [ ] Guardrails: sem inventar freq ou volt; sem remover proteção térmica ou elétrica; disabled-by-default

### UR-BACKUP: acervo único de backup de imagem
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: guard e acervo local · Device-gate: sim · Origem: requisito do spec, contrato de dados rev.6 · Aceite: backup restaura em dry-run de hash
- [ ] Guardar path, hash, slot de origem e data
- [ ] Validar backup antes de operação de alto risco
- [ ] Dry-run de restauração conferindo hash
- [ ] Alvo de rollback: `boot_b` no lake; em outros devices, o slot detectado
- [ ] Detectar slot ativo antes de operar; bloquear o slot errado
- [ ] Lake: nunca slot A (exceção documentada do POCO C75)
- [ ] Um único acervo: sem segundo repositório de imagem

### UR-LOG: acervo único de log
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: control plane · Device-gate: não · Origem: requisito do spec, contrato de dados rev.6 · Aceite: uma operação gera uma linha; as outras telas não criam arquivo
- [ ] Uma linha por operação; as telas mostram recortes
- [ ] Nunca gravar SuperKey, senha de keystore ou bytes de chave privada
- [ ] Segredos fora de interfaces públicas (UI, CLI, WebUI)
- [ ] Retenção até o usuário limpar; teto em bytes = UNKNOWN até medir
- [ ] Rollback de configuração não apaga o log

### UR-SELFUPDATE: atualização do APK do UltraRoot
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: manager UltraRoot · Device-gate: sim · Origem: spec §4.0 rev.6 · Aceite: APK com outro cert é recusado e o app antigo continua
- [ ] Procurar APK mais novo em origem configurada pelo usuário
- [ ] Aceitar só `me.weishu.kernelsu` com cert sha256 `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`
- [ ] Cert divergente ou falha de instalação: o APK anterior permanece instalado
- [ ] Não apagar a cópia do último APK aceito antes da confirmação
- [ ] Update inválido não conta como sucesso

### UR-EXT-SDK: SDK de adapters e módulos
IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: SDK · Device-gate: sim · Origem: spec §3 · Aceite: adapter de teste registra e sai sem deixar policy
- [ ] API para adapters; APIs por backend
- [ ] API para ferramentas externas (contrato, versão, permissões)
- [ ] API de descoberta de capacidade
- [ ] Registro de versões, dependências declaradas e permissões explícitas
- [ ] Ciclo de vida e reversão de adapter
- [ ] Contratos de compatibilidade e testes de conformidade para adapters
- [ ] Proveniência e licença de cada integração
- [ ] APIs e convenções para desenvolvimento de módulos
- [ ] Recursos de baixo nível para desenvolvedores (KPM)

---

## 6. Seções de UI (IDs OBRIG-PROPOSTO em CAPABILITIES.md)

Cada capacidade precisa de ID antes de implementar (spec §4). Os IDs abaixo foram adicionados ao `CAPABILITIES.md` com conjunto `OBRIG-PROPOSTO`; viram definitivos com a aprovação do dono (gate §13).

### 6.1 Dashboard (`UR-DASH`)
- [~] Estado do backend: lógica pura HOST_TESTED (7 testes de `BackendStateTest.kt`), sem integração ao Dashboard; demais campos continuam pendentes. Atualização 2026-10-13; aparelho NÃO VERIFICADO.
- [ ] Estado da comunicação manager e kernel
- [ ] Info do device: Android, kernel, arquitetura e ABI, estado conhecido do bootloader
- [ ] Estado das capacidades e dos módulos
- [ ] Avisos de incompatibilidade e alertas de segurança
- [ ] Atalhos para operações comuns; histórico recente de ações privilegiadas; diagnóstico resumido

### 6.2 Root Access (cobertura pelos IDs existentes e um proposto)
- [ ] Histórico de solicitações de root (UR-LOG)
- [ ] Limites e políticas de privilégio (KSU-PROFILE, MG-SU-GRANT)
- [ ] Detecção do backend de autorização (UR-EXT-SDK, descoberta)
- [ ] Controle explícito de ADB e shell (MG-SU-GRANT)
- [ ] Sessões root e diagnóstico (MG-SU-DAEMON)
- [ ] Interfaces CLI de administração (`UR-CLI`)

### 6.3 Module Center (`UR-MOD`)
- [ ] Listagem unificada de módulos de Magisk, KSU e APM
- [ ] Instalação de pacotes locais; importação offline
- [ ] Ativar, desativar, remover, atualizar; rollback quando tecnicamente possível
- [ ] Metadados, dependências e conflitos
- [ ] Classificação por backend e formato; requisitos de kernel, Android e ABI
- [ ] Indicador de compatibilidade verificada
- [ ] Executar a ação do módulo; abrir a WebUI
- [ ] Proteção contra archive malformado; verificação de hash; assinatura criptográfica quando houver
- [ ] Repositórios externos configuráveis; política de confiança por repositório; atualização de metadados
- [ ] Avisos para funções que dependem de scripts, mounts ou hooks exclusivos
- [ ] Logs da instalação de módulo: UR-LOG
- [ ] Metamódulos KSU (KSU-META), APM (AP-APM) e Magisk (MG-MOD-*): IDs existentes, ainda NOT_STARTED

### 6.4 Boot Image Center (`UR-BOOT`)
- [ ] Inspecionar imagens locais; identificar o tipo; hash e tamanho
- [ ] Mostrar compatibilidade por device
- [ ] Dry-run e diagnóstico antes de operação destrutiva
- [ ] Confirmação explícita antes de gravar partição ("sim")
- [ ] Registrar o resultado de cada operação em UR-LOG
- [ ] Documentar o procedimento de restauração
- [ ] Operações exclusivas por backend; detectar fluxos APatch que exigem `boot.img`
- [ ] Validar a origem da imagem; gerenciar backups e metadados (UR-BACKUP)

### 6.5 System Toolkit (`UR-SYS`)
- [ ] Arquivos e diretórios; processos; serviços; montagens
- [ ] Propriedades Android (MG-PROP); permissões; SELinux (MG-POLICY)
- [ ] Shell; scripts; pacotes instalados; apps e dados quando permitido
- [ ] Backup e restauração de arquivo do usuário, fora de imagem de boot (imagem fica em UR-BACKUP)
- [ ] Firewall por integração compatível
- [ ] Monitoramento de serviços; inspeção de kernel; ferramentas do sistema
- [ ] Nunca apresentar como nativa uma ferramenta de módulo externo ou não instalada

### 6.6 Recovery Center (`UR-REC`)
- [ ] Backups validados (UR-BACKUP); diagnóstico de falha de boot
- [ ] Desativar módulos problemáticos (KSU-RESCUE)
- [ ] Rollback de configuração (UR-PERF-WRITE)
- [ ] Histórico de operações (UR-LOG)
- [ ] Identificar o backend e o procedimento de recuperação correto
- [ ] Orientações de ADB e fastboot compatíveis com o device
- [ ] Estado conhecido da recuperação

### 6.7 Extensions Hub (`UR-EXT`, além de UR-EXT-SDK)
- [ ] Integração de WebUI; registro de versões; contratos de compatibilidade
- [ ] Mecanismo de atualização controlado (UR-SELFUPDATE)
- [ ] Logs e diagnóstico (UR-LOG)

### 6.8 Performance e hardware
Coberto por UR-PERF-READ e UR-PERF-WRITE (seção 5).

### 6.9 Gerenciamento do app Magisk (§1.5, `MG-APP-MGMT`)
- [ ] Exibir o estado conhecido da instalação do Magisk
- [ ] Instalação direta e atualização pelo próprio app
- [ ] Administrar o app e os componentes instalados
- [ ] Interface para permissões, módulos e configurações
- [ ] Detectar situações inválidas da instalação e mostrar avisos
- [ ] Consultar logs e informações de diagnóstico (UR-LOG)

---

## 7. Cobertura de recuperação (§9): cenários obrigatórios
- [ ] [DEVICE] Flash falho
- [ ] [DEVICE] Módulo defeituoso no boot
- [ ] [DEVICE] Interrupção durante a operação
- [ ] [DEVICE] Boot-loop com guard e combinação de teclas
- [ ] [DEVICE] Configuração de performance ruim, com rollback
- [ ] [DEVICE] Matriz de recuperação por device e por domínio
- Regra: rollback de `boot_b` vale só para o lake (POCO C75). Outros devices: detectar partições e o slot ativo, bloquear o slot errado, e só então usar o procedimento validado daquele device.

## 8. Processo e critérios de conclusão

### 8.1 Por incremento (§10)
- [ ] AUDIT → RESEARCH → GAP MATRIX → ARCHITECTURE → IMPLEMENT → INTEGRATE → TEST → REVIEW → DOCUMENT → BENCHMARK
- [ ] Por item: localizar a implementação original; confirmar a licença; identificar o backend; mapear dependências e limites; implementar ou integrar; conectar à UI real; testar; registrar evidência; declarar incompatibilidade
- [ ] Cross-review com autor ≠ revisor em cada incremento
- [ ] Verificar toda alegação, inclusive as próprias, contra evidência (path, hash, comando, versão, data e resultado observado); sem resultado observado = UNKNOWN

### 8.2 Artefatos obrigatórios
- [~] `CAPABILITIES.md`: existe, com os IDs da seção 6 (OBRIG-PROPOSTO). Falta revisar a cobertura contra o spec
- [~] `COMPAT_MATRIX.md`: existe. Preencher célula por célula: capacidade × backend × versão Android × kernel × ABI × device × deps
- [~] `RECOVERY_SUITE.md`: existe. Falta cobrir os cenários da seção 7
- [~] `MODULE_PARITY.md`: existe. Falta a paridade por estágio da seção 1.4
- [ ] Cada célula do COMPAT_MATRIX com um de: VALIDATED, IMPLEMENTED_UNVALIDATED, ARCHITECTED, INCOMPATIBLE, UNKNOWN
- [ ] Inventário de versão e procedência de cada componente herdado: KernelPatch, MagiskBoot, resetprop, magiskpolicy, BusyBox, APM, SuperCall, WebUI
- [ ] Paridade funcional por backend: testes que provam o que funciona, com limitações e dependências. Sem teste, UNKNOWN
- [ ] Corpus de pesquisa e metodologia reprodutível: domínios root/privilégio; módulos; injeção (Zygisk, LSPosed, hooking); admin Android; boot e recovery; performance e hardware; monitoramento e benchmark; ferramentas. Inviáveis, redundantes, experimentais e externos ficam no catálogo
- [ ] Diagrama de arquitetura: manager e control plane, adapters e runtimes, engines e core do backend (§3)

### 8.3 Regras de operação (§5 do inventário)
- [ ] Detectar o backend ativo antes de qualquer operação
- [ ] Cada recurso indica quais backends o fornecem
- [ ] Não presumir coexistência de mecanismos de root
- [ ] Operação de boot sem procedimento validado fica bloqueada
- [ ] ZIP aceito não é compatibilidade funcional
- [ ] Testar separadamente scripts, montagem, SELinux, propriedades, estágios, WebUI e dependências
- [ ] Registrar o motivo quando o backend não suporta a operação
- [ ] Não simular sucesso quando o comando falha
- [ ] Distinguir detectado, implementado, integrado e validado
- [ ] Não afirmar compatibilidade com todos os dispositivos Android
- [ ] Preservar licenças e avisos dos projetos integrados
- [ ] Para recurso exclusivo, chamar o adapter específico; para recurso comum, implementar interface comum
- [ ] Não considerar concluído por existir tela, botão, APK compilado, ZIP aceito, interface do sistema presente, recurso existente em outro projeto ou operação que funcionou em outro backend
- [ ] Boot safety: detectar partições e slot ativo antes de qualquer operação; nunca presumir qual slot é o seguro
- [ ] APatch sem Zygisk nativo: frameworks dependentes de Zygisk exigem integração adicional; não presumir
- [ ] Hooking não presumido disponível só porque o app APatch está instalado; verificar backend, versão e kernel
- [ ] Limite declarado só depois de investigar alternativa plausível; identificar o bloqueio exato (HW, kernel, driver ou interface)
- [ ] Nenhuma ocultação indetectável nem bypass de atestação; apenas capacidade legítima
- [ ] Home, Settings, log e WebUI são suporte e não formam 7ª seção de UI

### 8.4 Conclusão global (§12)
- [ ] Cobertura do inventário medida
- [ ] Cada item em implementado, integrado, excluído com justificativa ou desconhecido
- [ ] Paridade funcional por backend, com testes que provam
- [ ] Cobertura por versão, arquitetura, kernel e device
- [ ] Recovery testada
- [ ] Benchmark e estabilidade da Performance Engine
- [ ] Bloqueados listados com motivo

### 8.5 Requisitos de todo subsistema (§3)
- [ ] API definida; capability discovery; dependências explícitas
- [ ] Validação e tratamento de falha; testes independentes; logs e diagnóstico; reversão; documentação
- [ ] Adapters isolados; detectar conflito entre backends e escolher configuração suportada explicitamente
- [ ] SDK: novos backends, módulos, interfaces de hardware e extensões sem reescrever o core; preservar APIs específicas de backend
- [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e proveniência, IMPL, VALID, deps, suporte, riscos, teste e evidência, lacunas

---

## 9. Decomposição (§11)
- [~] **SP1 núcleo root e manager**: seções 1 e 3, e os itens de `UR-*` que o SP1 cobre
- [ ] **SP2 Zygisk**: MG-ZYG; módulos `zygisk_vector` e `bc-poc`
- [ ] **SP3 adapters**: MagiskAdapter, ApatchAdapter, ApmRuntime, SDK, features de fork (ver F02), WebUI, rebrand, e módulos `always-trust-user-certs` e `tabs-ca-inject`
- [ ] **SP4 Performance Engine**: research → matriz de HW → controles seguros → overclock só após margem medida. Disabled-by-default, com estabilidade, thermal e rollback
- [ ] **SP5 recovery completa**: OTA e A/B, self-update, benchmarking

---

## 10. Fora do conjunto obrigatório (os itens restantes, com motivo)

| Item | Classe | Motivo |
|---|---|---|
| KSU-HWBP | NÃO-REAL | Hardware breakpoint: ausente no pin `6e386044` |
| KSU-PHYSMEM | NÃO-REAL + FORA | Memória física "invisibly": ausente no pin; ocultação fora do produto |
| KSU-SYSCALL-API | NÃO-REAL | API genérica de interceptar syscall não existe. O hook de `su` fica em KSU-ROOT |
| KSU-META-OVFS | NÃO-REAL | meta-overlayfs ausente em `6e386044` (F18) |
| KSU-DETECT-SURFACE | FORA | Motivo "reduces detection surface" (`module.md:179`) |
| AP-KPM-PART | NÃO-REAL | Proteção de partição via KPM sem fonte no pin |
| AP-SEL-BYPASS | FORA | Bypass de AVC com aparência de "não muda o contexto" |
| MG-APP-STUB | EXCL | Reempacota a identidade do app; quebra o cert C0.3 `efb8d49f` |
| F24 SuList/MagiskHide | fora do inventário | Morto; não entrou no inventário |
| F25 e F26 susfs | fora do inventário | Não viram obrigatório |
| F02 troca de `sys_call_table` | fora do pin | Do fork backslashxx, não de `6e386044`. Avaliar em SP3 |
| F27 `selinux_hide` | sem ID | Ocultação: provavelmente FORA. Decisão do dono |
| F04 LKM late load, F07 `kernelsu.ko` | sem ID | Atualização 2026-10-13: modalidade do core no lake NÃO VERIFICADA; flags só HOST_TESTED, aparelho aguarda D2. Histórico 2026-10-11: presumia core integrado, sem prova da configuração |
| F23 uid-exclude do KernelPatch | sem ID | Verificar em `f36f3d13` e criar ID, se real |

---

## 11. Matriz da fase 1 (F01–F30) para IDs

| F | Feature | ID no mapa | Situação |
|---|---|---|---|
| F01 | tracepoint `sys_enter` | KSU-ROOT | mecanismo interno de `su` |
| F02 | troca de `sys_call_table` | fora do pin | SP3 (fork) |
| F03 | LSM `task_fix_setuid` | KSU-ROOT | verificar no pin |
| F04 | LKM late load | sem ID | decidir |
| F05 | kpimg | AP-BOOT | |
| F06 | magiskinit | MG-INIT | |
| F07 | ksuinit + `kernelsu.ko` | KSU-core | Atualização 2026-10-13: aplicabilidade de `.ko` no lake NÃO VERIFICADA; histórico 2026-10-11: considerado integrado |
| F08 | redirect de `su` | KSU-ROOT | |
| F09 | allowlist de UID | KSU-ROOT | |
| F10 | `magisk.db` | MG-SU-GRANT | |
| F11 | daemon socket | MG-SU-DAEMON | |
| F12 | reboot magic + ioctl | KSU-ROOT | |
| F13 | superkey | AP-ROOT | |
| F14 | `module.prop` | KSU-MOD, AP-APM, MG-MOD-LIFE | |
| F15 | webroot + `action.sh` | KSU-WEBUI, AP-APM | |
| F16 | magic mount | MG-MOD-MOUNT | |
| F17 | `metamount.sh` | KSU-META | |
| F18 | overlayfs no repo | NÃO-REAL (KSU-META-OVFS) | |
| F19 | loader KPM | AP-KPM | |
| F20 | Zygisk | MG-ZYG | SP2 |
| F21 | umount por UID | KSU-UMOUNT | |
| F22 | DenyList | MG-DENY | |
| F23 | uid-exclude KP | sem ID | verificar |
| F24 | SuList / MagiskHide | fora do inventário | |
| F25 | cliente susfs | fora do inventário | |
| F26 | susfs no kernel | fora do inventário | |
| F27 | `selinux_hide` | sem ID | provável FORA |
| F28 | assinatura do manager no kernel | manager-grant (seed) | C0.3 no host |
| F29 | resetprop | MG-PROP, KSU-RESETPROP | |
| F30 | patcher de `boot.img` | MG-APP-PATCH, AP-BOOT | |
