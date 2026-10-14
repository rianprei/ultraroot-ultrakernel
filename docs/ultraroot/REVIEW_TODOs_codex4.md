# REVIEW TODOs — Codex #4

**VEREDITO: REJEITADO.** A cobertura medida é inferior a 99% e há violações nas categorias (b), (c) e (e).

## Método e denominador

- Inventário consolidado (§§1–4): 440 bullets encontrados entre spec:151–853; 7 linhas excluídas por classificação explícita; 433 obrigatórios (spec:244, 426–428, 492–494).
- CAPABILITIES: 44 linhas OBRIG, spec independente do inventário; cada ID é unidade própria.
- §§0–13: 32 unidades: cada parágrafo normativo, bullet ou linha de tabela é uma unidade; diagrama §3 é um entregável. Uma frase composta conta como uma unidade e fica PARCIAL se faltar qualquer componente material.
- Resultado: 433 + 44 + 32 = 509 itens. Duplicatas entre as três fontes são contadas separadamente, conforme a definição da tarefa.
- Só COBERTO entra no numerador; PARCIAL e AUSENTE ficam de fora.

## Cobertura

| Fonte | Coberto | Parcial | Ausente | Total |
|---|---:|---:|---:|---:|
| Bullets inventário §§1–4 | 424 | 7 | 2 | 433 |
| IDs CAPABILITIES OBRIG | 43 | 1 | 0 | 44 |
| Requisitos §§0–13 | 19 | 13 | 0 | 32 |
| **Total** | **486** | **21** | **2** | **509** |

**COBERTURA: 486 / 509 = 95.48%**

## Requisitos §0–§13

| Origem | Requisito (trecho) | TODO que cobre | Classe | Falta |
|---|---|---|---|---|
| spec:42 Ecossistema integrado de capacidades de root E control… | Definição do produto: capacidade real máxima, cobertura mensurável, limites e integração sem fundir runtimes. | TODO:3 > Fonte: spec rev.7 `[fora do repo]/docs/superpowers/specs/2026-10-11-ultraro…; TODO:4 > Data: 2026-10-11. Regra §0 rev.7: toda capacidade REAL é obrigatória. EX…; TODO:566 - [ ] Por item: localizar a implementação original; confirmar a licença; i…; TODO:592 - [ ] Não considerar concluído por existir tela, botão, APK compilado, ZIP…; TODO:597 - [ ] Cobertura do inventário medida | PARCIAL | Falta exigir investigação de alternativa plausível ao bloqueio; TODO 566 mapeia limites e incompatibilidade, sem alternativa. |
| spec:44 **Regra do dono (rev.7):** toda capacidade REAL do inv… | Toda capacidade real é obrigatória, implementada/integrada/adapter/extensão, com ID e rastreio até conclusão. | TODO:4 > Data: 2026-10-11. Regra §0 rev.7: toda capacidade REAL é obrigatória. EX…; TODO:598 - [ ] Cada item em implementado, integrado, excluído com justificativa ou …; TODO:610 - [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e… | COBERTO | — |
| spec:46 **Exceção:** EXCLUDED com justificativa neste arquivo … | EXCLUDED justificado é exceção; MG-APP-STUB fica fora por quebrar autorização C0.3. | TODO:4 > Data: 2026-10-11. Regra §0 rev.7: toda capacidade REAL é obrigatória. EX…; TODO:634 \| MG-APP-STUB \| EXCL \| Reempacota a identidade do app; quebra o cert C0… | COBERTO | — |
| spec:48 Os seis IDs `UR-*` não estão num pin upstream. São req… | Os seis UR-* são obrigatórios; NÃO-REAL/FORA ficam no catálogo fora do obrigatório. | TODO:4 > Data: 2026-10-11. Regra §0 rev.7: toda capacidade REAL é obrigatória. EX…; TODO:405 ## 5. UltraRoot unificado (`UR-*`, 6 OBRIG); TODO:623 ## 10. Fora do conjunto obrigatório (os itens restantes, com motivo); TODO:634 \| MG-APP-STUB \| EXCL \| Reempacota a identidade do app; quebra o cert C0… | COBERTO | — |
| spec:51 - Nenhuma arquitetura garante TODA feature em TODO And… | Não prometer toda feature em todo Android. | TODO:589 - [ ] Não afirmar compatibilidade com todos os dispositivos Android | COBERTO | — |
| spec:52 - **Validação real = POCO C75 / lake / GKI 6.6.89 (1 d… | Lake/POCO C75/GKI 6.6.89 é o único alvo validado; demais UNKNOWN/sem alegação universal. | TODO:232 - [ ] Variante GKI e não-GKI, conforme kernel e procedimento (escopo atual…; TODO:408 IMPL IN_PROGRESS · VALID NOT_TESTED · Adapter: KsuBackend no lake; leitura…; TODO:575 - [ ] Cada célula do COMPAT_MATRIX com um de: VALIDATED, IMPLEMENTED_UNVAL…; TODO:589 - [ ] Não afirmar compatibilidade com todos os dispositivos Android | COBERTO | — |
| spec:53 - Overclock: por EVIDÊNCIA do device. Node sysfs gravá… | Overclock exige evidência real, guardrails, disabled-by-default, reversão, falha/recovery e proteção boot-fail. | TODO:417 - [ ] Investigar a cadeia: userspace → driver → kernel → device tree → reg…; TODO:429 - [ ] Classificar cada ajuste nas 6 classes do §6 (`HW_PERF_MATRIX_lake.md…; TODO:430  - (1) inspeção read-only; (2) controle runtime suportado; (3) não suporta…; TODO:431 - [ ] Testes de estabilidade, latência, throughput e sustentado; TODO:432 - [!] Overclock e undervolt só com margem medida. DVFS do lake bloqueia no…; TODO:433 - [ ] Guardrails: sem inventar freq ou volt; sem remover proteção térmica … | COBERTO | — |
| spec:54 - Legítimo: **sem** ocultação indetectável / bypass de… | Sem ocultação indetectável ou bypass de atestação. | TODO:142 IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · Device-gate…; TODO:262 ### KSU-UMOUNT: desmontar módulos por app (não é hide universal); TODO:628 \| KSU-PHYSMEM \| NÃO-REAL + FORA \| Memória física "invisibly": ausente n…; TODO:633 \| AP-SEL-BYPASS \| FORA \| Bypass de AVC com aparência de "não muda o con… | PARCIAL | Há proibições pontuais de hide/bypass, mas nenhuma regra geral para bypass de atestação. |
| spec:55 - Device safety: build/teste no HOST; sem flash/unlock… | Build/teste host; operação no device com sim; backup+guard; nunca destruir dados. | TODO:15 - [!] Toda operação no aparelho: "sim, <passo>". Sem isso não há instalaçã…; TODO:436 IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: guard e acervo local · Devi…; TODO:438 - [ ] Validar backup antes de operação de alto risco; TODO:443 - [ ] Um único acervo: sem segundo repositório de imagem | PARCIAL | Gate e backup aparecem; falta a regra explícita de preservação/não destruição de dados. |
| spec:56 - **Boot safety (regra de PRODUTO):** detectar a confi… | Boot safety: detectar partições/slot ativo, bloquear slot inseguro, validar backup/recovery e exceção lake separada. | TODO:438 - [ ] Validar backup antes de operação de alto risco; TODO:440 - [ ] Alvo de rollback: `boot_b` no lake; em outros devices, o slot detect…; TODO:442 - [ ] Lake: nunca slot A (exceção documentada do POCO C75); TODO:560 - Regra: rollback de `boot_b` vale só para o lake (POCO C75). Outros devic…; TODO:593 - [ ] Boot safety: detectar partições e slot ativo antes de qualquer opera… | COBERTO | — |
| spec:57 - **Verificar TODA alegação (inclusive as nossas) cont… | Auditar alegações/proveniência, UNKNOWN sem evidência, sem stub/falso sucesso, pesquisa reprodutível. | TODO:566 - [ ] Por item: localizar a implementação original; confirmar a licença; i…; TODO:568 - [ ] Verificar toda alegação, inclusive as próprias, contra evidência (pa…; TODO:587 - [ ] Não simular sucesso quando o comando falha; TODO:610 - [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e… | PARCIAL | Não há corpus nem metodologia reprodutível de descoberta/pesquisa. |
| spec:65 \| Imagem C0.1 existe no host \| IMPLEMENTED \| HOST_T… | Artefato C0.1 host: path/hash/comando/versão/data/resultado. | TODO:23 - [x] Boot C0.1 no aparelho, recorte: 62 linhas `KernelSU:`, `dmesg_new_er… | PARCIAL | TODO 23 cobre log/boot, não a imagem C0.1 host e seus dados de hash/header. |
| spec:66 \| Boot C0.1 no aparelho \| IMPLEMENTED \| DEVICE_TEST… | Evidência C0.1 device no recorte de log, incluindo limites/UNKNOWN. | TODO:23 - [x] Boot C0.1 no aparelho, recorte: 62 linhas `KernelSU:`, `dmesg_new_er… | COBERTO | — |
| spec:67 \| Par C0.3 no host (cert, gate, imagem) \| IMPLEMENTE… | Par C0.3 host: cert/gate/imagem/APK e evidência reproduzível. | TODO:22 - [x] Par C0.3 no host: imagem `2977e1dc…`, cert `efb8d49f…`, gate 557 mód…; TODO:24 - [x] APK UltraRoot vc40000 verificado no host: V2-only, cert `efb8d49f…`,… | COBERTO | — |
| spec:68 \| manager-grant (o kernel autoriza o APK e o `su` dei… | Manager-grant: status NOT_TESTED, sem prova de uid=0/contexto não-Magisk. | TODO:32 - [!] [DEVICE] manager-grant: abrir o app, conceder root a um app de teste…; TODO:236 - [x] Par host C0.3 e cert (seção 1.1) | COBERTO | — |
| spec:70 Auditabilidade: alegação sem path, hash quando couber,… | Alegação sem path/hash/comando/versão/data/resultado é UNKNOWN. | TODO:568 - [ ] Verificar toda alegação, inclusive as próprias, contra evidência (pa… | PARCIAL | TODO 568 não inclui explicitamente resultado observado como campo da regra. |
| spec:74 ┌──────────────── UltraRoot Manager (1 APK) UI/WebUI ─… | Diagrama da arquitetura: manager/control plane, adapters/runtimes, engines e core do backend. | TODO:19 ## 1. SP1 — núcleo root + manager; TODO:57 ## 2. Magisk: MagiskAdapter e runtime Magisk (`MG-*`, 20 OBRIG); TODO:225 ## 3. KernelSU: KsuBackend (`KSU-*`, 11 OBRIG) e seeds; TODO:337 ## 4. APatch e KernelPatch (`AP-*`, 7 OBRIG); TODO:405 ## 5. UltraRoot unificado (`UR-*`, 6 OBRIG); TODO:615 - [~] **SP1 núcleo root e manager**: seções 1 e 3, e os itens de `UR-*` qu…; TODO:617 - [ ] **SP3 adapters**: MagiskAdapter, ApatchAdapter, ApmRuntime, SDK, fea…; TODO:618 - [ ] **SP4 Performance Engine**: research → matriz de HW → controles segu… | PARCIAL | As tarefas listam partes, mas não exigem artefato de arquitetura/topologia integrada. |
| spec:85 As seis seções de UI são: Root Access, Module Center, … | Seis seções de UI, Home/Settings/log/WebUI como suporte e ApmRuntime separado dos runtimes KSU/Magisk. | TODO:405 ## 5. UltraRoot unificado (`UR-*`, 6 OBRIG); TODO:473 ## 6. Seções de UI sem ID no CAPABILITIES (GAP); TODO:485 ### 6.2 Root Access (cobertura pelos IDs existentes e um proposto); TODO:517 ### 6.5 System Toolkit (proposto `UR-SYS-*`); TODO:526 ### 6.6 Recovery Center (proposto `UR-REC-*`); TODO:535 ### 6.7 Extensions Hub (proposto `UR-EXT-*`, além de UR-EXT-SDK); TODO:361 ### AP-APM: módulos APM (`ApmRuntime`, separado de KSU e Magisk); TODO:369 - [ ] Contrato APM por módulo (ZIP aceito não prova os estágios); TODO:445 ### UR-LOG: acervo único de log | PARCIAL | As seis áreas e separação APM aparecem; não está registrada a regra de Home/Settings/log/WebUI não virarem 7ª seção. |
| spec:87 Cada subsistema: API definida · capability discovery ·… | Cada subsistema tem API/discovery/deps/validação/falha/testes/log/reversão/docs; adapters isolados, conflitos e SDK extensível. | TODO:606 - [ ] API definida; capability discovery; dependências explícitas; TODO:607 - [ ] Validação e tratamento de falha; testes independentes; logs e diagnó…; TODO:608 - [ ] Adapters isolados; detectar conflito entre backends e escolher confi…; TODO:609 - [ ] SDK: novos backends, módulos, interfaces de hardware e extensões sem… | COBERTO | — |
| spec:90 Catálogo versionado. Cada capacidade, campos INDEPENDE… | Campos independentes por capacidade em CAPABILITIES. | TODO:610 - [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e… | COBERTO | — |
| spec:91 - Implementação: `NOT_STARTED \| IN_PROGRESS \| IMPLEM… | Enum de implementação: NOT_STARTED, IN_PROGRESS, IMPLEMENTED, INTEGRATED, EXTERNAL, EXCLUDED. | TODO:7 - `[ ]` NOT_STARTED · `[~]` IN_PROGRESS, ou IMPLEMENTED sem validação · `[…; TODO:598 - [ ] Cada item em implementado, integrado, excluído com justificativa ou …; TODO:610 - [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e… | PARCIAL | Legenda não enumera o vocabulário completo, em especial EXTERNAL. |
| spec:92 - Validação: `NOT_TESTED \| HOST_TESTED \| DEVICE_TEST… | Enum de validação: NOT_TESTED, HOST_TESTED, DEVICE_TESTED, FAILED, UNKNOWN. | TODO:7 - `[ ]` NOT_STARTED · `[~]` IN_PROGRESS, ou IMPLEMENTED sem validação · `[…; TODO:23 - [x] Boot C0.1 no aparelho, recorte: 62 linhas `KernelSU:`, `dmesg_new_er…; TODO:27 - [~] C0.3 no aparelho: o handoff v2 afirma flash e boot. Sem log de boot … | PARCIAL | Legenda descreve HOST/DEVICE; os valores FAILED e UNKNOWN não são declarados como enum completo. |
| spec:93 Princípio: **componente que compila ≠ funcional; inter… | Princípios de status honesto, domínios de inventário, retenção de inviáveis/externos e corpus/metodologia. | TODO:9 - IMPL e VALID são independentes. Compilar não é validar. Aceitar ZIP não …; TODO:584 - [ ] ZIP aceito não é compatibilidade funcional; TODO:592 - [ ] Não considerar concluído por existir tela, botão, APK compilado, ZIP…; TODO:566 - [ ] Por item: localizar a implementação original; confirmar a licença; i…; TODO:610 - [ ] Campos independentes por capacidade no CAPABILITIES.md: ID, origem e… | PARCIAL | Falta o corpus/metodologia reprodutível e a lista de domínios não aparece como deliverable único. |
| spec:96 Subsistema integrado: descoberta de HW/drivers/control… | Performance Engine: descoberta/inspeção, controles, medições, reversão e baseline; investigar a cadeia HW. | TODO:410 - [ ] Informações da CPU; TODO:417 - [ ] Investigar a cadeia: userspace → driver → kernel → device tree → reg…; TODO:422 - [ ] Perfis de desempenho e de economia; TODO:427 - [ ] Benchmark antes×depois contra baseline gravado; TODO:429 - [ ] Classificar cada ajuste nas 6 classes do §6 (`HW_PERF_MATRIX_lake.md…; TODO:431 - [ ] Testes de estabilidade, latência, throughput e sustentado; TODO:433 - [ ] Guardrails: sem inventar freq ou volt; sem remover proteção térmica … | PARCIAL | O TODO não exige leitura de volt/OPP/limites; só diz que não escreve OPP (TODO 418). |
| spec:99 Alvo inicial: lake, com [fora do repo] kernel/source/DT/OPP/drivers r… | Alvo lake, taxonomia de seis classes, máximo sustentável demonstrável e medidas contra baseline. | TODO:417 - [ ] Investigar a cadeia: userspace → driver → kernel → device tree → reg…; TODO:429 - [ ] Classificar cada ajuste nas 6 classes do §6 (`HW_PERF_MATRIX_lake.md…; TODO:431 - [ ] Testes de estabilidade, latência, throughput e sustentado; TODO:432 - [!] Overclock e undervolt só com margem medida. DVFS do lake bloqueia no… | PARCIAL | Classes e métricas aparecem, mas não há tarefa explícita para achar/documentar o máximo sustentável demonstrável. |
| spec:102 `capacidade × backend × versão Android × kernel × ABI … | Matriz por recurso e eixos, enum de estados, teste de comportamento real e gate device. | TODO:15 - [!] Toda operação no aparelho: "sim, <passo>". Sem isso não há instalaçã…; TODO:572 - [~] `COMPAT_MATRIX.md`: existe. Preencher célula por célula: capacidade …; TODO:575 - [ ] Cada célula do COMPAT_MATRIX com um de: VALIDATED, IMPLEMENTED_UNVAL…; TODO:585 - [ ] Testar separadamente scripts, montagem, SELinux, propriedades, estág… | COBERTO | — |
| spec:104 ## 8. Paridade funcional — testes que provam, por back… | Paridade funcional testada por backend, limitações/deps; sem teste=UNKNOWN. | TODO:577 - [ ] Paridade funcional por backend: testes que provam o que funciona, co… | COBERTO | — |
| spec:107 Cenários+evidência: flash falho, módulo defeituoso no … | Suite de recuperação com seis cenários e regra slot lake/genérica. | TODO:554 - [ ] Flash falho; TODO:555 - [ ] Módulo defeituoso no boot; TODO:556 - [ ] Interrupção durante a operação; TODO:557 - [ ] Boot-loop com guard e combinação de teclas; TODO:558 - [ ] Configuração de performance ruim, com rollback; TODO:559 - [ ] Matriz de recuperação por device e por domínio; TODO:560 - Regra: rollback de `boot_b` vale só para o lake (POCO C75). Outros devic… | COBERTO | — |
| spec:109 ## 10. Método: `AUDIT→RESEARCH→GAP MATRIX→ARCHITECTURE… | Pipeline AUDIT→...→BENCHMARK, auditoria do repo, upstream legal/adequado e cross-review. | TODO:565 - [ ] AUDIT → RESEARCH → GAP MATRIX → ARCHITECTURE → IMPLEMENT → INTEGRATE…; TODO:566 - [ ] Por item: localizar a implementação original; confirmar a licença; i…; TODO:567 - [ ] Cross-review com autor ≠ revisor em cada incremento; TODO:568 - [ ] Verificar toda alegação, inclusive as próprias, contra evidência (pa…; TODO:590 - [ ] Preservar licenças e avisos dos projetos integrados | COBERTO | — |
| spec:112 SP1 núcleo root+manager (KSU concede root; 3 fixes aud… | Escopo SP1–SP5, paridade modular, gate universal ZIP e mounts incompatíveis. | TODO:47 - [ ] Scripts (`customize.sh`, `post-fs-data.sh`, `service.sh`, `uninstall…; TODO:48 - [ ] `system.prop`: aplicado e removido junto com o módulo; TODO:49 - [ ] `sepolicy.rule`: regra carregada, sem allow global; TODO:50 - [ ] Mount systemless e `.replace`: arquivo aparece sem escrever na parti…; TODO:51 - [ ] Estágios de init: post-fs-data, post-mount, service, boot-completed,…; TODO:52 - [ ] Magic mount (Magisk) e overlayfs nunca ativos juntos; TODO:53 - [ ] Cada feature declarada como nativa, dependente de metamódulo ou exig…; TODO:615 - [~] **SP1 núcleo root e manager**: seções 1 e 3, e os itens de `UR-*` qu…; TODO:616 - [ ] **SP2 Zygisk**: MG-ZYG; módulos `zygisk_vector` e `bc-poc`; TODO:617 - [ ] **SP3 adapters**: MagiskAdapter, ApatchAdapter, ApmRuntime, SDK, fea…; TODO:618 - [ ] **SP4 Performance Engine**: research → matriz de HW → controles segu…; TODO:619 - [ ] **SP5 recovery completa**: OTA e A/B, self-update, benchmarking | COBERTO | — |
| spec:114 ## 12. Conclusão global (medida): (1)cobertura do inve… | Sete métricas de conclusão e encerramento por critérios/lacunas. | TODO:597 - [ ] Cobertura do inventário medida; TODO:598 - [ ] Cada item em implementado, integrado, excluído com justificativa ou …; TODO:599 - [ ] Paridade funcional por backend, com testes que provam; TODO:600 - [ ] Cobertura por versão, arquitetura, kernel e device; TODO:601 - [ ] Recovery testada; TODO:602 - [ ] Benchmark e estabilidade da Performance Engine; TODO:603 - [ ] Bloqueados listados com motivo | COBERTO | — |
| spec:116 ## 13. Gate: dono aprova **rev.7** → writing-plans do … | Gate de aprovação rev.7, planos sobre IDs, não iniciar Manager/adapter/perf antes; OC atrás de evidência. | TODO:14 - [!] Aprovar o rev.7 do spec (§13). Plano novo de Manager, adapter e perf…; TODO:432 - [!] Overclock e undervolt só com margem medida. DVFS do lake bloqueia no…; TODO:475 Cada capacidade precisa de ID antes de implementar (spec §4). Os IDs abaix…; TODO:615 - [~] **SP1 núcleo root e manager**: seções 1 e 3, e os itens de `UR-*` qu…; TODO:618 - [ ] **SP4 Performance Engine**: research → matriz de HW → controles segu… | COBERTO | — |

## IDs OBRIG do CAPABILITIES.md

| Catálogo | ID e descrição | TODO | Status | Falta |
|---|---|---|---|---|
| CAPABILITIES:33 | MG-SU-GRANT: Conceder, revogar e listar root por app e por shell/ADB | TODO:59 ### MG-SU-GRANT: conceder, revogar e listar root por app e por shell/ADB; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | PARCIAL | a descrição exige listar apps autorizados, mas os checkboxes não pedem a lista/tela; TODO 64 só diz gerenciar acesso por app. |
| CAPABILITIES:34 | MG-SU-EXEC: `su`: shell, UID/GID/grupos, contexto SELinux da sessão, ambiente preservado, caps inclus… | TODO:68 ### MG-SU-EXEC: `su` com shell, UID/GID/grupos, contexto SELinux, ambiente…; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:35 | MG-SU-NS: Namespace de montagem a partir de PID | TODO:78 ### MG-SU-NS: namespace de montagem a partir de PID; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:36 | MG-SU-PTY: PTY e shell interativo | TODO:82 ### MG-SU-PTY: PTY e shell interativo; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:37 | MG-SU-DAEMON: Versão do binário e do daemon, estado de autorização | TODO:86 ### MG-SU-DAEMON: daemon, versões e estado de autorização; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:38 | MG-MOD-LIFE: ZIP: instalar, tirar, ligar, desligar, action, updateJson, metadados. Sem repositório cen… | TODO:92 ### MG-MOD-LIFE: ciclo de módulos ZIP (sem repositório central); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · … | COBERTO | — |
| CAPABILITIES:39 | MG-MOD-SCRIPT: customize.sh, post-fs-data.sh, service.sh, uninstall.sh | TODO:104 ### MG-MOD-SCRIPT: scripts de instalação, boot e callbacks; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · … | COBERTO | — |
| CAPABILITIES:40 | MG-MOD-PROP: system.prop | TODO:112 ### MG-MOD-PROP: `system.prop` de módulos; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · … | COBERTO | — |
| CAPABILITIES:41 | MG-MOD-SEP: sepolicy.rule | TODO:116 ### MG-MOD-SEP: `sepolicy.rule`; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · … | COBERTO | — |
| CAPABILITIES:42 | MG-MOD-MOUNT: Magic mount, `.replace`, systemless. Algoritmo do Magisk, não do KSU | TODO:120 ### MG-MOD-MOUNT: magic mount, `.replace` e systemless (algoritmo do Magis…; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime Magisk · … | COBERTO | — |
| CAPABILITIES:43 | MG-MOD-OVLAYD: overlay.d no ramdisk | TODO:127 ### MG-MOD-OVLAYD: `overlay.d` no ramdisk; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:44 | MG-ZYG: Zygisk nativo do Magisk. Não é nativo no KSU nem no APatch deste pin | TODO:131 ### MG-ZYG: Zygisk nativo do Magisk; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ZygiskAdapter sob… | COBERTO | — |
| CAPABILITIES:45 | MG-DENY: DenyList: lista, liga/desliga, desmonta alteração do Magisk no alvo. Não é garantia de hi… | TODO:141 ### MG-DENY: DenyList; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:46 | MG-BOOT: magiskboot: unpack/repack, cpio, dtb, compressão, hex patch, AVB documentado | TODO:153 ### MG-BOOT: magiskboot; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:47 | MG-INIT: magiskinit no ramdisk | TODO:169 ### MG-INIT: magiskinit; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:48 | MG-POLICY: magiskpolicy / supolicy: load, allow/deny/audit, tipos | TODO:179 ### MG-POLICY: magiskpolicy e supolicy; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:49 | MG-PROP: resetprop, inclusive ro.*, persistente, wait, `--compact` | TODO:189 ### MG-PROP: resetprop; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:50 | MG-CLI: CLI magisk: applets, módulos, denylist, restorecon, caminhos | TODO:199 ### MG-CLI: CLI `magisk`; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:51 | MG-APP-PATCH: Patch de boot pelo app, fluxo Samsung AP/TAR, extração de boot por URL de OTA nas versões… | TODO:211 ### MG-APP-PATCH: patch de boot pelo app; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:52 | MG-APP-TERM: Terminal do app com TTY/ANSI | TODO:219 ### MG-APP-TERM: terminal embutido com TTY e ANSI; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: MagiskAdapter · D… | COBERTO | — |
| CAPABILITIES:54 | KSU-ROOT: Root pelo kernel, allowlist, `su`, versão e UAPI | TODO:239 ### KSU-ROOT: root pelo kernel, allowlist, `su`, versão e UAPI; IMPL IN_PROGRESS · VALID NOT_TESTED · Adapter: KsuBackend · Devi… | COBERTO | — |
| CAPABILITIES:55 | KSU-PROFILE: App profile: UID, GID, grupos, caps, contexto SELinux, NO_NEW_PRIVS | TODO:250 ### KSU-PROFILE: app profile (UID, GID, grupos, caps, SELinux, NO_NEW_PRIV…; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Devi… | COBERTO | — |
| CAPABILITIES:56 | KSU-UMOUNT: Desmontar módulos por app. Não é hide universal | TODO:262 ### KSU-UMOUNT: desmontar módulos por app (não é hide universal); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Devi… | COBERTO | — |
| CAPABILITIES:57 | KSU-MOD: module.prop, customize, post-fs-data, service, system.prop, sepolicy.rule. Sem mount de `… | TODO:271 ### KSU-MOD: sistema de módulos em `/data/adb/modules`; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:58 | KSU-META: Interface de metamódulo: um ativo, hooks de install/mount/limpeza, troca por procedimento | TODO:282 ### KSU-META: metamódulos (um ativo por vez); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:59 | KSU-WEBUI: WebUI: webroot, index.html | TODO:294 ### KSU-WEBUI: WebUI de módulos; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:60 | KSU-CFG: Config key-value persistente e temporária; temporária ganha na leitura | TODO:301 ### KSU-CFG: configuração key-value de módulos; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:61 | KSU-RESCUE: Procedimento documentado de bootloop por módulo e restauração de imagem. Sem garantia aut… | TODO:310 ### KSU-RESCUE: bootloop e restauração; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:62 | KSU-KSURC: `/data/adb/ksu/.ksurc` quando ENV não está setado | TODO:320 ### KSU-KSURC: `.ksurc` do shell; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Devi… | COBERTO | — |
| CAPABILITIES:63 | KSU-BB: BusyBox em `/data/adb/ksu/bin/busybox`, binário vindo do Magisk | TODO:326 ### KSU-BB: BusyBox; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: runtime KSU · Dev… | COBERTO | — |
| CAPABILITIES:64 | KSU-RESETPROP: resetprop do ksud (symlink para o ksud) | TODO:331 ### KSU-RESETPROP: resetprop do ksud (symlink); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KsuBackend · Devi… | COBERTO | — |
| CAPABILITIES:70 | AP-ROOT: Root APatch, SuperKey, SuperCall, rejeitar chave errada | TODO:339 ### AP-ROOT: root por SuperKey e SuperCall; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · D… | COBERTO | — |
| CAPABILITIES:71 | AP-BOOT: Patch de boot.img. Não presumir init_boot nem vendor_boot | TODO:350 ### AP-BOOT: patch de `boot.img` (sem presumir `init_boot` nem `vendor_boo…; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · D… | COBERTO | — |
| CAPABILITIES:72 | AP-APM: Módulos APM: prop, sepolicy, skip_mount, post-fs-data, post-mount, service, boot-complete… | TODO:361 ### AP-APM: módulos APM (`ApmRuntime`, separado de KSU e Magisk); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Devi… | COBERTO | — |
| CAPABILITIES:73 | AP-KPM: KPM: carregar, inline-hook, syscall-table-hook. Install persistente só se a versão do gui… | TODO:371 ### AP-KPM: KernelPatch modules; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: KpmRuntime · Devi… | COBERTO | — |
| CAPABILITIES:76 | AP-WEBUI: WebUI APM no webroot, modelo compatível com o do KSU quando o módulo declara | TODO:380 ### AP-WEBUI: WebUI de módulos APM; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Devi… | COBERTO | — |
| CAPABILITIES:77 | AP-BB: BusyBox ash standalone do APatch | TODO:386 ### AP-BB: BusyBox ash standalone; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApmRuntime · Devi… | COBERTO | — |
| CAPABILITIES:78 | AP-DIAG: Estado, versões APatch/KernelPatch, diagnóstico de módulo. O texto vai para `UR-LOG` | TODO:394 ### AP-DIAG: estado e diagnóstico; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: ApatchAdapter · D… | COBERTO | — |
| CAPABILITIES:79 | UR-PERF-READ: Leitura de CPU, GPU, freq, governor, thermal, quando o node existir. Sem escrever OPP | TODO:407 ### UR-PERF-READ: leitura de CPU, GPU, frequência, governor e thermal; IMPL IN_PROGRESS · VALID NOT_TESTED · Adapter: KsuBackend no lak… | COBERTO | — |
| CAPABILITIES:80 | UR-PERF-WRITE: Perfil, aplicar e reverter controle que a matriz classificar como runtime suportado. Over… | TODO:420 ### UR-PERF-WRITE: perfis, aplicar e reverter (Performance Engine); IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: Performance Engin… | COBERTO | — |
| CAPABILITIES:81 | UR-BACKUP: Único acervo de backup de imagem: path, hash, slot, data | TODO:435 ### UR-BACKUP: acervo único de backup de imagem; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: guard e acervo lo… | COBERTO | — |
| CAPABILITIES:82 | UR-LOG: Único acervo de log. Sem SuperKey, senha ou chave privada. Retenção até o usuário limpar | TODO:445 ### UR-LOG: acervo único de log; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: control plane · D… | COBERTO | — |
| CAPABILITIES:83 | UR-SELFUPDATE: Update do APK só com o cert `efb8d49f` e pacote `me.weishu.kernelsu`. Falha conserva o AP… | TODO:452 ### UR-SELFUPDATE: atualização do APK do UltraRoot; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: manager UltraRoot… | COBERTO | — |
| CAPABILITIES:84 | UR-EXT-SDK: SDK de adapter: descoberta, versão, permissão, reversão, licença | TODO:460 ### UR-EXT-SDK: SDK de adapters e módulos; IMPL NOT_STARTED · VALID NOT_TESTED · Adapter: SDK · Device-gate… | COBERTO | — |

## Inventário consolidado — 433 itens

| Spec | Item (trecho) | TODO correspondente (trecho) | Classe | Motivo quando não completo |
|---|---|---|---|---|
| spec:156 | Conceder privilégios root a aplicativos autorizados. | TODO:61 - [ ] Conceder root a app autorizado | COBERTO | — |
| spec:157 | Solicitar autorização root quando um aplicativo tenta utilizar su. | TODO:62 - [ ] Solicitar autorização quando um app chama `su` | COBERTO | — |
| spec:158 | Permitir e revogar permissões de root. | TODO:63 - [ ] Revogar root | COBERTO | — |
| spec:159 | Gerenciar o acesso root por aplicativo. | TODO:64 - [ ] Gerenciar acesso root por app | COBERTO | — |
| spec:160 | Controlar o acesso root oferecido aos aplicativos e ao shell/ADB. | TODO:65 - [ ] Controlar root oferecido ao shell e ao ADB | COBERTO | — |
| spec:161 | Executar comandos como root. | TODO:70 - [ ] Executar comandos como root | COBERTO | — |
| spec:162 | Disponibilizar o executável su. | TODO:71 - [ ] Disponibilizar o executável `su` | COBERTO | — |
| spec:163 | Selecionar shell para a sessão privilegiada. | TODO:72 - [ ] Selecionar shell da sessão | COBERTO | — |
| spec:164 | Suportar shells interativos e alocação de pseudoterminal. | TODO:84 - [ ] Shell interativo com alocação de pseudoterminal | COBERTO | — |
| spec:165 | Selecionar UID de destino e grupos de usuário. | TODO:73 - [ ] Selecionar UID de destino e grupos | COBERTO | — |
| spec:166 | Configurar contexto SELinux para sessões su. | TODO:74 - [ ] Contexto SELinux da sessão `su` | COBERTO | — |
| spec:167 | Selecionar namespace de montagem a partir de um processo/PID. | TODO:80 - [ ] Selecionar o namespace de montagem a partir de um PID | COBERTO | — |
| spec:168 | Preservar o ambiente da sessão quando solicitado. | TODO:75 - [ ] Preservar o ambiente quando solicitado | COBERTO | — |
| spec:169 | Restringir capacidades Linux, inclusive em processos UID 0. | TODO:76 - [ ] Restringir capabilities Linux, inclusive em UID 0 | COBERTO | — |
| spec:170 | Manter serviços e daemon próprios. | TODO:88 - [ ] Manter serviço e daemon próprios | COBERTO | — |
| spec:171 | Consultar versão do binário e versão do daemon. | TODO:89 - [ ] Consultar versão do binário e do daemon | COBERTO | — |
| spec:172 | Registrar e administrar estados de autorização. | TODO:66 - [ ] Registrar e administrar estados de autorização | COBERTO | — |
| spec:176 | Instalar módulos a partir de arquivos ZIP. | TODO:94 - [ ] Instalar módulo a partir de ZIP | COBERTO | — |
| spec:177 | Desinstalar módulos. | TODO:95 - [ ] Desinstalar módulo | COBERTO | — |
| spec:178 | Ativar e desativar módulos. | TODO:144 - [ ] Ativar e desativar a aplicação da lista | COBERTO | — |
| spec:179 | Remover todos os módulos pela ferramenta CLI. | TODO:203 - [ ] Instalar e remover módulos pela CLI, inclusive remover todos | COBERTO | — |
| spec:180 | Executar ações definidas pelo módulo. | TODO:97 - [ ] Executar `action.sh` pela interface | COBERTO | — |
| spec:181 | Apresentar metadados, nome, ID, versão e autor. | TODO:98 - [ ] Ler metadados: nome, ID, versão, autor | COBERTO | — |
| spec:182 | Consultar atualizações usando updateJson quando declarado. | TODO:99 - [ ] Consultar `updateJson` quando declarado | COBERTO | — |
| spec:183 | Instalar atualizações fornecidas pelo próprio módulo. | TODO:100 - [ ] Instalar atualização fornecida pelo próprio módulo | COBERTO | — |
| spec:184 | Suportar pacotes ZIP com formatos de compressão compatíveis. | TODO:101 - [ ] Aceitar ZIP com formatos de compressão compatíveis | COBERTO | — |
| spec:185 | Executar scripts de instalação e personalização. | TODO:104 ### MG-MOD-SCRIPT: scripts de instalação, boot e callbacks | COBERTO | — |
| spec:186 | Suportar customize.sh durante a instalação. | TODO:106 - [ ] `customize.sh` na instalação | COBERTO | — |
| spec:187 | Executar post-fs-data.sh no estágio correspondente. | TODO:107 - [ ] `post-fs-data.sh` no estágio correto | COBERTO | — |
| spec:188 | Executar service.sh durante a inicialização dos serviços. | TODO:108 - [ ] `service.sh` no boot de serviços | COBERTO | — |
| spec:189 | Executar uninstall.sh na remoção quando fornecido. | TODO:109 - [ ] `uninstall.sh` na remoção | COBERTO | — |
| spec:190 | Executar action.sh a partir da interface quando disponível. | TODO:97 - [ ] Executar `action.sh` pela interface | COBERTO | — |
| spec:191 | Aplicar system.prop para propriedades do sistema. | TODO:114 - [ ] Aplicar `system.prop` do módulo | COBERTO | — |
| spec:192 | Aplicar regras adicionais definidas em sepolicy.rule. | TODO:118 - [ ] Aplicar regras de `sepolicy.rule` | COBERTO | — |
| spec:193 | Utilizar scripts e configurações de montagem systemless. | TODO:122 - [ ] Montagem systemless por scripts e configuração | COBERTO | — |
| spec:194 | Substituir arquivos do sistema sem modificar diretamente as partições originais quando a operação suportada permit… | TODO:123 - [ ] Substituir arquivo do sistema sem alterar a partição original, quando permitido | COBERTO | — |
| spec:196 | Suportar o mecanismo de arquivos .replace do formato de módulo. | TODO:124 - [ ] Suportar o mecanismo `.replace` | COBERTO | — |
| spec:197 | Suportar alterações em diretórios do sistema por montagem. | TODO:125 - [ ] Alterar diretórios do sistema por montagem | COBERTO | — |
| spec:198 | Integrar overlay.d para personalizações compatíveis no boot. | TODO:129 - [ ] Integrar `overlay.d` para personalizações compatíveis no boot | COBERTO | — |
| spec:199 | Permitir implementação de módulos por terceiros. | TODO:468 - [ ] APIs e convenções para desenvolvimento de módulos | COBERTO | — |
| spec:200 | Fornecer APIs e convenções para desenvolvimento de módulos. | TODO:468 - [ ] APIs e convenções para desenvolvimento de módulos | COBERTO | — |
| spec:209 | Injetar componentes compatíveis no processo Zygote. | TODO:133 - [ ] Injetar componentes compatíveis no Zygote | COBERTO | — |
| spec:210 | Oferecer infraestrutura para módulos Zygisk. | TODO:134 - [ ] Infraestrutura para módulos Zygisk | COBERTO | — |
| spec:211 | Suportar módulos com bibliotecas nativas por ABI. | TODO:135 - [ ] Módulos com bibliotecas nativas por ABI | COBERTO | — |
| spec:212 | Disponibilizar API de desenvolvimento para módulos Zygisk. | TODO:136 - [ ] API de desenvolvimento Zygisk | COBERTO | — |
| spec:213 | Permitir que módulos atuem nos processos compatíveis. | TODO:139 - [ ] Hooking de processos Android nos processos compatíveis | COBERTO | — |
| spec:214 | Gerenciar o estado da funcionalidade Zygisk. | TODO:137 - [ ] Gerenciar o estado do Zygisk | COBERTO | — |
| spec:215 | Suportar APIs de Zygisk nas versões correspondentes. | TODO:138 - [ ] Suportar APIs Zygisk por versão | COBERTO | — |
| spec:216 | Integrar funcionalidades de hooking de processos Android. | TODO:139 - [ ] Hooking de processos Android nos processos compatíveis | COBERTO | — |
| spec:223 | Manter uma lista de pacotes/processos selecionados. | TODO:143 - [ ] Manter a lista de pacotes e processos | COBERTO | — |
| spec:224 | Ativar ou desativar a aplicação da DenyList. | TODO:144 - [ ] Ativar e desativar a aplicação da lista | COBERTO | — |
| spec:225 | Adicionar alvos. | TODO:145 - [ ] Adicionar alvos | COBERTO | — |
| spec:226 | Remover alvos. | TODO:146 - [ ] Remover alvos | COBERTO | — |
| spec:227 | Listar os alvos configurados. | TODO:147 - [ ] Listar alvos configurados | COBERTO | — |
| spec:228 | Aplicar mudanças de namespace de montagem nos processos selecionados. | TODO:148 - [ ] Aplicar mudanças de namespace de montagem nos alvos | COBERTO | — |
| spec:229 | Desmontar determinadas alterações do Magisk nos processos-alvo. | TODO:149 - [ ] Desmontar alterações do Magisk nos alvos | COBERTO | — |
| spec:230 | Consultar o estado de aplicação da lista. | TODO:150 - [ ] Consultar o estado de aplicação | COBERTO | — |
| spec:231 | Gerenciar a DenyList pela CLI. | TODO:204 - [ ] Administrar a DenyList pela CLI | COBERTO | — |
| spec:238 | Exibir o estado conhecido da instalação do Magisk. | TODO:544 - [ ] Exibir o estado conhecido da instalação do Magisk | COBERTO | — |
| spec:239 | Permitir instalação direta/atualização pelo aplicativo. | TODO:545 - [ ] Instalação direta e atualização pelo próprio app | COBERTO | — |
| spec:240 | Selecionar e corrigir uma imagem de boot. | TODO:213 - [ ] Selecionar e corrigir imagem de boot (dry-run antes) | COBERTO | — |
| spec:241 | Instalar a imagem corrigida usando o procedimento aplicável. | TODO:214 - [ ] Instalar a imagem corrigida pelo procedimento aplicável | COBERTO | — |
| spec:242 | Administrar o próprio aplicativo e os componentes instalados. | TODO:546 - [ ] Administrar o app e os componentes instalados | COBERTO | — |
| spec:243 | Oferecer interface para permissões, módulos e configurações. | TODO:547 - [ ] Interface para permissões, módulos e configurações | COBERTO | — |
| spec:245 | Detectar situações inválidas da instalação e mostrar avisos. | TODO:548 - [ ] Detectar situações inválidas da instalação e mostrar avisos | COBERTO | — |
| spec:246 | Consultar logs e informações para diagnóstico. | TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:247 | Instalar através do procedimento de patch de imagem compatível com o dispositivo. | TODO:213 - [ ] Selecionar e corrigir imagem de boot (dry-run antes); TODO:214 - [ ] Instalar a imagem corrigida pelo procedimento aplicável | COBERTO | — |
| spec:249 | Suportar o fluxo específico de arquivos de firmware Samsung AP/TAR. | TODO:215 - [ ] Fluxo Samsung AP/TAR: fora do alvo do lake até ser suportado | COBERTO | — |
| spec:250 | Extrair imagens de boot de URLs OTA remotas nas versões que suportam esse fluxo. | TODO:216 - [ ] Extrair imagem de boot de URL OTA nas versões que suportam | COBERTO | — |
| spec:252 | Disponibilizar terminal integrado com TTY e suporte ANSI nas versões que contêm essa função. | TODO:221 - [ ] Terminal com TTY e suporte ANSI | COBERTO | — |
| spec:259 | Desempacotar imagens de boot. | TODO:155 - [ ] Desempacotar imagem de boot | COBERTO | — |
| spec:260 | Reempacotar imagens usando os componentes extraídos. | TODO:156 - [ ] Reempacotar imagem de boot | COBERTO | — |
| spec:261 | Interpretar cabeçalhos de imagens. | TODO:157 - [ ] Interpretar cabeçalhos de imagem | COBERTO | — |
| spec:262 | Extrair kernel, ramdisk, DTB e componentes compatíveis. | TODO:158 - [ ] Extrair kernel, ramdisk e DTB | COBERTO | — |
| spec:263 | Manipular arquivos CPIO do ramdisk. | TODO:159 - [ ] CPIO: listar, extrair, adicionar, remover, substituir, mover e criar links | COBERTO | — |
| spec:264 | Listar conteúdo de CPIO. | TODO:159 - [ ] CPIO: listar, extrair, adicionar, remover, substituir, mover e criar links | COBERTO | — |
| spec:265 | Extrair arquivos do CPIO. | TODO:159 - [ ] CPIO: listar, extrair, adicionar, remover, substituir, mover e criar links | COBERTO | — |
| spec:266 | Adicionar, remover, substituir, mover ou criar links de entradas. | TODO:159 - [ ] CPIO: listar, extrair, adicionar, remover, substituir, mover e criar links | COBERTO | — |
| spec:267 | Verificar o estado de um ramdisk. | TODO:160 - [ ] Verificar estado do ramdisk e aplicar patch nele | COBERTO | — |
| spec:268 | Aplicar patches ao ramdisk. | TODO:160 - [ ] Verificar estado do ramdisk e aplicar patch nele | COBERTO | — |
| spec:269 | Fazer backup e restauração do ramdisk quando aplicável. | TODO:161 - [ ] Backup e restauração do ramdisk, quando aplicável | COBERTO | — |
| spec:270 | Inspecionar e manipular componentes DTB. | TODO:162 - [ ] Inspecionar e manipular componentes DTB | COBERTO | — |
| spec:271 | Executar operações de patch hexadecimal em arquivos binários. | TODO:163 - [ ] Patch hexadecimal em binários | COBERTO | — |
| spec:272 | Comprimir e descomprimir arquivos. | TODO:164 - [ ] Comprimir e descomprimir: gzip, LZ4, LZMA, XZ, BZip2 | COBERTO | — |
| spec:273 | Suportar formatos como gzip, LZ4, LZMA, XZ e BZip2. | TODO:164 - [ ] Comprimir e descomprimir: gzip, LZ4, LZMA, XZ, BZip2 | COBERTO | — |
| spec:274 | Processar certos formatos de boot e firmware específicos. | TODO:165 - [ ] Formatos específicos de boot e firmware | COBERTO | — |
| spec:275 | Trabalhar com estruturas AVB nas operações documentadas. | TODO:166 - [ ] Estruturas AVB nas operações documentadas | COBERTO | — |
| spec:276 | Preservar os componentes originais necessários ao reempacotamento. | TODO:167 - [ ] Preservar os componentes originais necessários ao repack | COBERTO | — |
| spec:283 | Substituir o init do ramdisk modificado no processo de inicialização. | TODO:171 - [ ] Substituir o init do ramdisk modificado | COBERTO | — |
| spec:284 | Participar da configuração inicial do ambiente Magisk. | TODO:172 - [ ] Configuração inicial do ambiente Magisk | COBERTO | — |
| spec:285 | Realizar operações prévias à inicialização normal do Android. | TODO:173 - [ ] Operações prévias à inicialização normal do Android | COBERTO | — |
| spec:286 | Dar suporte a diferentes layouts de inicialização Android. | TODO:174 - [ ] Suportar layouts de init suportados | COBERTO | — |
| spec:287 | Apoiar o funcionamento em dispositivos system-as-root. | TODO:175 - [ ] Suportar system-as-root | COBERTO | — |
| spec:288 | Participar da injeção e da configuração de componentes do Magisk. | TODO:176 - [ ] Participar da injeção e configuração de componentes | COBERTO | — |
| spec:289 | Aplicar as adaptações de boot suportadas pela versão do programa. | TODO:177 - [ ] Aplicar as adaptações de boot suportadas pela versão | COBERTO | — |
| spec:293 | Carregar políticas SELinux. | TODO:181 - [ ] Carregar políticas SELinux | COBERTO | — |
| spec:294 | Inspecionar e salvar políticas. | TODO:182 - [ ] Inspecionar e salvar políticas | COBERTO | — |
| spec:295 | Aplicar modificações à política em execução quando suportado. | TODO:183 - [ ] Aplicar modificações à política em execução, quando suportado | COBERTO | — |
| spec:296 | Aplicar regras adicionais provenientes de arquivos. | TODO:184 - [ ] Aplicar regras de arquivo e contextos de arquivo | COBERTO | — |
| spec:297 | Permitir regras allow, deny, auditallow e dontaudit. | TODO:185 - [ ] Regras allow, deny, auditallow e dontaudit | COBERTO | — |
| spec:298 | Manipular certas declarações de tipos e atributos. | TODO:186 - [ ] Manipular tipos e atributos; regras estendidas | COBERTO | — |
| spec:299 | Aplicar regras extendidas para permissões compatíveis. | TODO:186 - [ ] Manipular tipos e atributos; regras estendidas | COBERTO | — |
| spec:300 | Aplicar regras relacionadas a contextos de arquivos. | TODO:184 - [ ] Aplicar regras de arquivo e contextos de arquivo | COBERTO | — |
| spec:301 | Suportar sintaxe de política disponibilizada pela ferramenta. | TODO:187 - [ ] Sintaxe da ferramenta e alias `supolicy` | COBERTO | — |
| spec:302 | Fornecer o alias supolicy para compatibilidade. | TODO:187 - [ ] Sintaxe da ferramenta e alias `supolicy` | COBERTO | — |
| spec:309 | Consultar propriedades Android. | TODO:191 - [ ] Consultar propriedades Android | COBERTO | — |
| spec:310 | Definir propriedades do sistema. | TODO:192 - [ ] Definir propriedades | COBERTO | — |
| spec:311 | Modificar propriedades ro.* em condições suportadas. | TODO:193 - [ ] Modificar `ro.*` em condições suportadas | COBERTO | — |
| spec:312 | Manipular propriedades persistentes. | TODO:194 - [ ] Manipular propriedades persistentes | COBERTO | — |
| spec:313 | Operar sem depender exclusivamente do property_service convencional. | TODO:195 - [ ] Operar sem depender exclusivamente do property_service | COBERTO | — |
| spec:314 | Aguardar uma propriedade atingir determinada condição, conforme a funcionalidade disponibilizada pela versão. | TODO:196 - [ ] Aguardar propriedade atingir condição (`wait`) | COBERTO | — |
| spec:316 | Compactar a representação de propriedades nas versões compatíveis. | TODO:197 - [ ] `--compact` nas versões compatíveis | COBERTO | — |
| spec:317 | Integrar arquivos system.prop dos módulos. | TODO:112 ### MG-MOD-PROP: `system.prop` de módulos | COBERTO | — |
| spec:321 | Executar comandos magisk. | TODO:201 - [ ] Executar comandos `magisk`; listar applets | COBERTO | — |
| spec:322 | Executar su e resetprop como applets. | TODO:202 - [ ] `su` e `resetprop` como applets | COBERTO | — |
| spec:323 | Consultar versão e estado do daemon. | TODO:89 - [ ] Consultar versão do binário e do daemon | COBERTO | — |
| spec:324 | Listar applets disponíveis. | TODO:201 - [ ] Executar comandos `magisk`; listar applets | COBERTO | — |
| spec:325 | Iniciar e parar o daemon pelas interfaces suportadas. | TODO:90 - [ ] Iniciar e parar o daemon pelas interfaces suportadas | COBERTO | — |
| spec:326 | Executar callbacks em estágios de inicialização: post-fs-data, service, boot-complete e zygote-restart. | TODO:110 - [ ] Callbacks de estágio: post-fs-data, service, boot-complete, zygote-restart | COBERTO | — |
| spec:328 | Instalar módulos pela CLI. | TODO:203 - [ ] Instalar e remover módulos pela CLI, inclusive remover todos | COBERTO | — |
| spec:329 | Remover módulos pela CLI. | TODO:203 - [ ] Instalar e remover módulos pela CLI, inclusive remover todos | COBERTO | — |
| spec:330 | Administrar a DenyList pela CLI. | TODO:204 - [ ] Administrar a DenyList pela CLI | COBERTO | — |
| spec:331 | Restaurar contextos SELinux de arquivos compatíveis. | TODO:205 - [ ] Restaurar contextos SELinux de arquivos | COBERTO | — |
| spec:332 | Clonar atributos como permissões, proprietário e contexto. | TODO:206 - [ ] Clonar atributos: permissões, dono, contexto | COBERTO | — |
| spec:333 | Consultar caminhos temporários do Magisk. | TODO:207 - [ ] Consultar caminhos temporários do Magisk | COBERTO | — |
| spec:334 | Consultar e operar dados internos através das interfaces documentadas. | TODO:208 - [ ] Consultar e operar dados internos pelas interfaces documentadas | COBERTO | — |
| spec:335 | Disponibilizar ferramentas auxiliares próprias do projeto. | TODO:209 - [ ] Ferramentas auxiliares próprias do projeto | COBERTO | — |
| spec:343 | Conceder acesso root diretamente através do mecanismo integrado ao kernel. | TODO:242 - [ ] Conceder root pelo manager (allowlist) | COBERTO | — |
| spec:345 | Controlar as autorizações de aplicativos através do KernelSU Manager. | TODO:242 - [ ] Conceder root pelo manager (allowlist); TODO:243 - [ ] Revogar root pelo manager | COBERTO | — |
| spec:346 | Disponibilizar o comando su. | TODO:244 - [ ] Disponibilizar `su` e expor interfaces KSU às ferramentas compatíveis | COBERTO | — |
| spec:347 | Aplicar políticas de acesso com suporte no kernel. | TODO:245 - [ ] Aplicar políticas de acesso com suporte do kernel | COBERTO | — |
| spec:348 | Expor interfaces específicas do KernelSU a ferramentas compatíveis. | TODO:244 - [ ] Disponibilizar `su` e expor interfaces KSU às ferramentas compatíveis | COBERTO | — |
| spec:349 | Detectar o estado conhecido do backend e sua versão. | TODO:248 - [ ] Detectar backend e versão do KSU | COBERTO | — |
| spec:350 | Funcionar mediante integração apropriada em kernels suportados. | TODO:229 - [x] KSU no boot C0.1 (recorte de 17,0 s a 88,5 s, 62 linhas `KernelSU:`); TODO:232 - [ ] Variante GKI e não-GKI, conforme kernel e procedimento (escopo atual: lake, GKI 6… | COBERTO | — |
| spec:351 | Suportar dispositivos GKI e integrações não-GKI conforme a variante, o kernel e o procedimento de instalação. | TODO:232 - [ ] Variante GKI e não-GKI, conforme kernel e procedimento (escopo atual: lake, GKI 6… | COBERTO | — |
| spec:353 | Fornecer interfaces de desenvolvimento para recursos avançados dependentes do kernel. | TODO:468 - [ ] APIs e convenções para desenvolvimento de módulos | PARCIAL | faltam as interfaces de desenvolvimento específicas de recursos KSU dependentes do kernel; o match do TODO é o SDK/APM genérico (TODO 468). |
| spec:361 | Configurar perfis de privilégios por aplicativo. | TODO:252 - [ ] Controlar o UID usado pelo `su` por app | COBERTO | — |
| spec:362 | Controlar o UID usado pelo processo su. | TODO:252 - [ ] Controlar o UID usado pelo `su` por app | COBERTO | — |
| spec:363 | Controlar o GID. | TODO:253 - [ ] Controlar GID e grupos suplementares | COBERTO | — |
| spec:364 | Controlar grupos suplementares. | TODO:253 - [ ] Controlar GID e grupos suplementares | COBERTO | — |
| spec:365 | Configurar capacidades Linux. | TODO:254 - [ ] Configurar capabilities Linux | COBERTO | — |
| spec:366 | Configurar o contexto SELinux aplicado ao processo su. | TODO:255 - [ ] Configurar contexto SELinux do `su` e regras específicas | COBERTO | — |
| spec:367 | Definir regras SELinux específicas para o contexto configurado. | TODO:255 - [ ] Configurar contexto SELinux do `su` e regras específicas | COBERTO | — |
| spec:368 | Restringir privilégios concedidos ao aplicativo autorizado. | TODO:256 - [ ] Restringir privilégios a privilégios parciais, não root irrestrito | COBERTO | — |
| spec:369 | Criar configurações semelhantes a privilégios parciais de root. | TODO:256 - [ ] Restringir privilégios a privilégios parciais, não root irrestrito | COBERTO | — |
| spec:370 | Aplicar a política de perfil através do mecanismo do kernel. | TODO:258 - [ ] Aplicar a política de perfil pelo mecanismo do kernel | COBERTO | — |
| spec:371 | Disponibilizar a opção NO_NEW_PRIVS em configurações suportadas. | TODO:257 - [ ] Opção NO_NEW_PRIVS em configurações suportadas | COBERTO | — |
| spec:372 | Limitar privilégios em casos que não exigem root irrestrito. | TODO:256 - [ ] Restringir privilégios a privilégios parciais, não root irrestrito | COBERTO | — |
| spec:373 | Gerenciar opções específicas por aplicativo. | TODO:259 - [ ] Opções específicas por app, inclusive comportamento do sistema de módulos para o … | COBERTO | — |
| spec:374 | Controlar o comportamento do sistema de módulos para determinados aplicativos. | TODO:259 - [ ] Opções específicas por app, inclusive comportamento do sistema de módulos para o … | COBERTO | — |
| spec:382 | Controlar a exposição de modificações de módulos por aplicativo. | TODO:264 - [ ] Controlar a exposição de módulos por app | COBERTO | — |
| spec:383 | Desmontar alterações de módulos em processos selecionados. | TODO:265 - [ ] Desmontar alterações de módulos em processos selecionados | COBERTO | — |
| spec:384 | Oferecer uma configuração padrão de desmontagem de módulos. | TODO:266 - [ ] Configuração padrão de desmontagem | COBERTO | — |
| spec:385 | Permitir configuração por aplicativo em cenários suportados. | TODO:267 - [ ] Configuração por app em cenários suportados | COBERTO | — |
| spec:386 | Utilizar listas de inclusão ou exclusão conforme a estratégia escolhida. | TODO:268 - [ ] Listas de inclusão e exclusão | COBERTO | — |
| spec:387 | Condicionar essas operações à versão do kernel e às interfaces presentes. | TODO:269 - [ ] Condicionar às interfaces e à versão do kernel | COBERTO | — |
| spec:388 | Permitir integração com módulos que usem esse comportamento. | TODO:259 - [ ] Opções específicas por app, inclusive comportamento do sistema de módulos para o … | COBERTO | — |
| spec:395 | Instalar módulos a partir de ZIP. | TODO:273 - [ ] Instalar ZIP; gerenciar; ativar, desativar e remover | COBERTO | — |
| spec:396 | Gerenciar módulos instalados. | TODO:273 - [ ] Instalar ZIP; gerenciar; ativar, desativar e remover | COBERTO | — |
| spec:397 | Ativar, desativar e remover módulos. | TODO:273 - [ ] Instalar ZIP; gerenciar; ativar, desativar e remover | COBERTO | — |
| spec:398 | Utilizar estrutura semelhante à dos módulos Magisk. | TODO:273 - [ ] Instalar ZIP; gerenciar; ativar, desativar e remover; TODO:274 - [ ] Layout `/data/adb/modules` e `module.prop` | COBERTO | — |
| spec:399 | Armazenar módulos em /data/adb/modules no layout documentado. | TODO:274 - [ ] Layout `/data/adb/modules` e `module.prop` | COBERTO | — |
| spec:400 | Interpretar module.prop. | TODO:274 - [ ] Layout `/data/adb/modules` e `module.prop` | COBERTO | — |
| spec:401 | Suportar scripts customize.sh, post-fs-data.sh e service.sh. | TODO:275 - [ ] `customize.sh`, `post-fs-data.sh`, `service.sh` | COBERTO | — |
| spec:402 | Suportar system.prop. | TODO:276 - [ ] `system.prop` e `sepolicy.rule` | COBERTO | — |
| spec:403 | Suportar sepolicy.rule. | TODO:276 - [ ] `system.prop` e `sepolicy.rule` | COBERTO | — |
| spec:404 | Suportar diretórios de arquivos do sistema. | TODO:277 - [ ] Diretórios de arquivos do sistema (sem mount de `/system`, ver KSU-META) | COBERTO | — |
| spec:405 | Suportar scripts de inicialização e personalizações compatíveis. | TODO:275 - [ ] `customize.sh`, `post-fs-data.sh`, `service.sh`; TODO:278 - [ ] Atualizações e metadados | COBERTO | — |
| spec:406 | Permitir atualizações e metadados de módulos conforme a implementação. | TODO:278 - [ ] Atualizações e metadados | COBERTO | — |
| spec:407 | Identificar que um módulo está sendo executado no KernelSU através dos mecanismos documentados. | TODO:279 - [ ] Identificar que o módulo roda no KernelSU | COBERTO | — |
| spec:409 | Suportar módulos que adaptam seu comportamento ao backend. | TODO:280 - [ ] Adaptar comportamento ao backend | COBERTO | — |
| spec:410 | Disponibilizar interfaces para desenvolvimento de módulos. | TODO:244 - [ ] Disponibilizar `su` e expor interfaces KSU às ferramentas compatíveis | COBERTO | — |
| spec:419 | Delegar a infraestrutura de montagem a módulos especializados. | TODO:284 - [ ] Delegar montagem a metamódulo | COBERTO | — |
| spec:420 | Oferecer um mecanismo extensível de montagem dos módulos. | TODO:285 - [ ] Mecanismo extensível de montagem, com estratégias com e sem montagem | COBERTO | — |
| spec:421 | Permitir estratégias diferentes de montagem. | TODO:285 - [ ] Mecanismo extensível de montagem, com estratégias com e sem montagem | COBERTO | — |
| spec:422 | Permitir estratégias sem montagem para módulos que não necessitam dela. | TODO:285 - [ ] Mecanismo extensível de montagem, com estratégias com e sem montagem | COBERTO | — |
| spec:431 | Possibilitar implementações customizadas de montagem. | TODO:286 - [ ] Implementações customizadas de montagem | COBERTO | — |
| spec:432 | Executar scripts especiais do metamódulo antes dos scripts normais. | TODO:287 - [ ] Executar scripts especiais do metamódulo antes dos normais | COBERTO | — |
| spec:433 | Definir os hooks de instalação, montagem e limpeza previstos. | TODO:288 - [ ] Hooks de instalação, montagem e limpeza | COBERTO | — |
| spec:434 | Exibir o metamódulo ativo no gerenciador. | TODO:289 - [ ] Exibir o metamódulo ativo no manager | COBERTO | — |
| spec:435 | Evitar conflitos entre múltiplos metamódulos. | TODO:290 - [ ] Evitar conflito entre metamódulos | COBERTO | — |
| spec:436 | Permitir a troca de implementação por procedimento apropriado. | TODO:291 - [ ] Trocar a implementação por procedimento apropriado | COBERTO | — |
| spec:445 | Exibir interfaces personalizadas fornecidas pelos módulos. | TODO:296 - [ ] Renderizar `webroot` com `index.html` no manager; TODO:297 - [ ] Permitir interação do usuário com o módulo | COBERTO | — |
| spec:446 | Utilizar HTML, CSS e JavaScript para criar essas interfaces. | TODO:296 - [ ] Renderizar `webroot` com `index.html` no manager | COBERTO | — |
| spec:447 | Renderizar páginas WebUI no gerenciador. | TODO:296 - [ ] Renderizar `webroot` com `index.html` no manager | COBERTO | — |
| spec:448 | Utilizar o diretório webroot do módulo. | TODO:296 - [ ] Renderizar `webroot` com `index.html` no manager | COBERTO | — |
| spec:449 | Usar index.html como entrada da página. | TODO:296 - [ ] Renderizar `webroot` com `index.html` no manager | COBERTO | — |
| spec:450 | Permitir interação do usuário com o módulo. | TODO:297 - [ ] Permitir interação do usuário com o módulo | COBERTO | — |
| spec:451 | Disponibilizar APIs para interação com o sistema, inclusive comandos shell nas condições da implementação. | TODO:298 - [ ] APIs de shell condicionadas à política de confiança | COBERTO | — |
| spec:453 | Permitir ferramentas e painéis customizados por módulo. | TODO:299 - [ ] Painéis customizados por módulo | COBERTO | — |
| spec:460 | Armazenar configurações key-value de módulos. | TODO:301 ### KSU-CFG: configuração key-value de módulos | COBERTO | — |
| spec:461 | Suportar configuração persistente. | TODO:303 - [ ] Armazenar configuração persistente | COBERTO | — |
| spec:462 | Suportar configuração temporária. | TODO:304 - [ ] Armazenar configuração temporária | COBERTO | — |
| spec:463 | Manter valores persistentes através de reinicializações. | TODO:305 - [ ] Persistente sobrevive a reboot | COBERTO | — |
| spec:464 | Limpar valores temporários no estágio previsto do boot. | TODO:306 - [ ] Temporária é limpa no estágio previsto do boot | COBERTO | — |
| spec:465 | Priorizar valores temporários sobre persistentes para chaves iguais. | TODO:307 - [ ] Temporária tem prioridade sobre persistente com a mesma chave | COBERTO | — |
| spec:466 | Oferecer interface utilizável por módulos e ferramentas compatíveis. | TODO:308 - [ ] Interface para módulos e ferramentas compatíveis | COBERTO | — |
| spec:470 | Documentar procedimentos para recuperar falhas provocadas por módulos. | TODO:312 - [ ] Documentar procedimento para falha causada por módulo; TODO:313 - [ ] Ajudar a diagnosticar falha de inicialização | COBERTO | — |
| spec:471 | Ajudar a diagnosticar falhas de inicialização. | TODO:313 - [ ] Ajudar a diagnosticar falha de inicialização | COBERTO | — |
| spec:472 | Oferecer procedimentos de recuperação com ferramentas externas. | TODO:314 - [ ] Procedimentos com ferramentas externas | COBERTO | — |
| spec:473 | Permitir desativar ou remover módulos problemáticos conforme o método. | TODO:315 - [ ] Desativar ou remover módulo problemático | COBERTO | — |
| spec:474 | Documentar restauração de imagens de boot. | TODO:316 - [ ] Restaurar imagem de boot (ver UR-BACKUP); TODO:317 - [ ] Explicar cenários de incompatibilidade e retorno à imagem stock | COBERTO | — |
| spec:475 | Explicar cenários de incompatibilidade e procedimentos de retorno à imagem stock. | TODO:317 - [ ] Explicar cenários de incompatibilidade e retorno à imagem stock | COBERTO | — |
| spec:483 | Permitir um arquivo .ksurc para configuração personalizada do shell su. | TODO:322 - [ ] Ler `/data/adb/ksu/.ksurc` quando a ENV não está definida; TODO:323 - [ ] Ajustar inicialização de shell compatível; TODO:324 - [ ] Integrar com o ambiente do módulo e das ferramentas autorizadas | COBERTO | — |
| spec:484 | Ajustar inicialização de shell compatível com a implementação. | TODO:323 - [ ] Ajustar inicialização de shell compatível | COBERTO | — |
| spec:485 | Integrar esse recurso com o ambiente do módulo e as ferramentas autorizadas. | TODO:324 - [ ] Integrar com o ambiente do módulo e das ferramentas autorizadas | COBERTO | — |
| spec:504 | Corrigir a imagem boot.img com o mecanismo APatch/KernelPatch. | TODO:352 - [ ] Selecionar `boot.img`; TODO:355 - [ ] Gerar imagem corrigida para device compatível | COBERTO | — |
| spec:505 | Aplicar patch no kernel conforme os requisitos suportados. | TODO:359 - [ ] Verificar pré-requisitos do bootloader e do kernel antes de instalar | COBERTO | — |
| spec:506 | Conceder privilégios root a aplicativos autorizados. | TODO:341 - [ ] Conceder e revogar root a apps autorizados | COBERTO | — |
| spec:507 | Controlar os aplicativos com acesso root. | TODO:348 - [ ] Controlar o comportamento de root pelos componentes APatch e KernelPatch | COBERTO | — |
| spec:508 | Administrar concessões e revogações de privilégios. | TODO:343 - [ ] Administrar concessões e revogações | COBERTO | — |
| spec:509 | Utilizar um mecanismo de autorização baseado em SuperKey. | TODO:344 - [ ] Gerar e configurar SuperKey (não registrar em log); TODO:346 - [ ] Verificar credencial e rejeitar chave errada | COBERTO | — |
| spec:510 | Utilizar SuperCall como interface de chamadas privilegiadas. | TODO:345 - [ ] SuperCall para componentes autorizados | COBERTO | — |
| spec:511 | Disponibilizar um backend de root no espaço do kernel. | TODO:347 - [ ] Interfaces para operações privilegiadas e módulos; TODO:348 - [ ] Controlar o comportamento de root pelos componentes APatch e KernelPatch | COBERTO | — |
| spec:512 | Controlar o comportamento de root através dos componentes APatch e KernelPatch. | TODO:348 - [ ] Controlar o comportamento de root pelos componentes APatch e KernelPatch | COBERTO | — |
| spec:514 | Fornecer interfaces para operações privilegiadas e módulos. | TODO:347 - [ ] Interfaces para operações privilegiadas e módulos | COBERTO | — |
| spec:523 | Selecionar uma imagem boot.img. | TODO:352 - [ ] Selecionar `boot.img` | COBERTO | — |
| spec:524 | Aplicar patch automático através do gerenciador. | TODO:353 - [ ] Patch automático pelo manager | COBERTO | — |
| spec:525 | Configurar SuperKey durante o processo de patch. | TODO:344 - [ ] Gerar e configurar SuperKey (não registrar em log) | COBERTO | — |
| spec:526 | Gerar imagem corrigida para o dispositivo compatível. | TODO:355 - [ ] Gerar imagem corrigida para device compatível | COBERTO | — |
| spec:527 | Permitir uso de fluxo de patch manual quando necessário. | TODO:354 - [ ] Patch manual quando necessário | COBERTO | — |
| spec:528 | Atualizar componentes KernelPatch conforme a versão. | TODO:356 - [ ] Atualizar componentes KernelPatch | COBERTO | — |
| spec:529 | Oferecer procedimentos de atualização documentados. | TODO:356 - [ ] Atualizar componentes KernelPatch | PARCIAL | há atualização de componentes (TODO 356), mas não um procedimento de atualização documentado. |
| spec:530 | Detectar requisitos de configuração do kernel. | TODO:357 - [ ] Detectar requisitos de config do kernel e verificar suporte antes de iniciar | COBERTO | — |
| spec:531 | Verificar condições de suporte antes de iniciar o processo. | TODO:357 - [ ] Detectar requisitos de config do kernel e verificar suporte antes de iniciar | COBERTO | — |
| spec:532 | Permitir restauração utilizando backup da imagem original. | TODO:358 - [ ] Restaurar a partir do backup original (UR-BACKUP) | COBERTO | — |
| spec:541 | Instalar módulos AndroidPatch. | TODO:363 - [ ] Instalar, gerenciar, ativar, desativar e remover módulos | COBERTO | — |
| spec:542 | Gerenciar módulos instalados. | TODO:363 - [ ] Instalar, gerenciar, ativar, desativar e remover módulos | COBERTO | — |
| spec:543 | Ativar e desativar módulos. | TODO:363 - [ ] Instalar, gerenciar, ativar, desativar e remover módulos | COBERTO | — |
| spec:544 | Remover módulos. | TODO:363 - [ ] Instalar, gerenciar, ativar, desativar e remover módulos | COBERTO | — |
| spec:545 | Utilizar formato estrutural semelhante ao Magisk. | TODO:363 - [ ] Instalar, gerenciar, ativar, desativar e remover módulos; TODO:364 - [ ] `module.prop` e diretório `system` | COBERTO | — |
| spec:546 | Utilizar module.prop. | TODO:364 - [ ] `module.prop` e diretório `system` | COBERTO | — |
| spec:547 | Utilizar diretório system nos módulos. | TODO:364 - [ ] `module.prop` e diretório `system` | COBERTO | — |
| spec:548 | Suportar skip_mount. | TODO:365 - [ ] `skip_mount`; arquivos `disable` e `remove` | COBERTO | — |
| spec:549 | Suportar arquivos disable e remove. | TODO:365 - [ ] `skip_mount`; arquivos `disable` e `remove` | COBERTO | — |
| spec:550 | Suportar scripts post-fs-data.sh. | TODO:366 - [ ] `post-fs-data.sh`, `post-mount.sh`, `service.sh`, `boot-completed.sh` | COBERTO | — |
| spec:551 | Suportar scripts post-mount.sh. | TODO:366 - [ ] `post-fs-data.sh`, `post-mount.sh`, `service.sh`, `boot-completed.sh` | COBERTO | — |
| spec:552 | Suportar scripts service.sh. | TODO:366 - [ ] `post-fs-data.sh`, `post-mount.sh`, `service.sh`, `boot-completed.sh` | COBERTO | — |
| spec:553 | Suportar scripts boot-completed.sh. | TODO:366 - [ ] `post-fs-data.sh`, `post-mount.sh`, `service.sh`, `boot-completed.sh` | COBERTO | — |
| spec:554 | Suportar uninstall.sh. | TODO:367 - [ ] `uninstall.sh`, `action.sh` e scripts de instalação | COBERTO | — |
| spec:555 | Suportar action.sh. | TODO:367 - [ ] `uninstall.sh`, `action.sh` e scripts de instalação | COBERTO | — |
| spec:556 | Carregar propriedades de system.prop via resetprop. | TODO:368 - [ ] `system.prop` via resetprop; `sepolicy.rule` | COBERTO | — |
| spec:557 | Aplicar regras customizadas de sepolicy.rule. | TODO:368 - [ ] `system.prop` via resetprop; `sepolicy.rule` | COBERTO | — |
| spec:558 | Executar scripts de instalação e personalização. | TODO:389 - [ ] Executar scripts no ash standalone | COBERTO | — |
| spec:559 | Suportar BusyBox com ASH Standalone Mode. | TODO:386 ### AP-BB: BusyBox ash standalone | COBERTO | — |
| spec:560 | Utilizar ferramentas auxiliares do ambiente APatch. | TODO:392 - [ ] Usar as ferramentas auxiliares do ambiente APatch | COBERTO | — |
| spec:561 | Oferecer compatibilidade para módulos que respeitem o contrato APM. | TODO:369 - [ ] Contrato APM por módulo (ZIP aceito não prova os estágios) | COBERTO | — |
| spec:569 | Executar código no espaço do kernel. | TODO:377 - [ ] Código no kernelspace isolado em teste, por alto risco | COBERTO | — |
| spec:570 | Integrar módulos de kernel do formato KPM. | TODO:391 - [ ] Integrar ao sistema de scripts e módulos | COBERTO | — |
| spec:571 | Incorporar KPM ao kernel corrigido. | TODO:378 - [!] Install persistente de KPM só se o guia da versão tiver. Verificar antes de afirm… | PARCIAL | o TODO condiciona install persistente (TODO 378), mas não planeja incorporar KPM ao kernel corrigido. |
| spec:572 | Carregar KPM por meio dos métodos suportados. | TODO:373 - [ ] Carregar KPM pelos métodos suportados | COBERTO | — |
| spec:573 | Utilizar capacidades de hooking no kernel. | TODO:374 - [ ] Inline-hook, quando implementado e compatível; TODO:375 - [ ] Syscall-table-hook, quando implementado e compatível | COBERTO | — |
| spec:574 | Suportar inline-hook quando implementado e compatível. | TODO:374 - [ ] Inline-hook, quando implementado e compatível | COBERTO | — |
| spec:575 | Suportar syscall-table-hook quando implementado e compatível. | TODO:375 - [ ] Syscall-table-hook, quando implementado e compatível | COBERTO | — |
| spec:576 | Permitir operações que não são possíveis somente com APM. | TODO:469 - [ ] Recursos de baixo nível para desenvolvedores (KPM) | PARCIAL | há item de recursos KPM de baixo nível (TODO 469), sem critério explícito de operação que APM não consegue executar. |
| spec:577 | Disponibilizar recursos de baixo nível para desenvolvedores. | TODO:469 - [ ] Recursos de baixo nível para desenvolvedores (KPM) | COBERTO | — |
| spec:578 | Expor interfaces de gerenciamento de KPM. | TODO:376 - [ ] Interfaces de gerenciamento de KPM | COBERTO | — |
| spec:593 | Gerar/configurar uma SuperKey. | TODO:344 - [ ] Gerar e configurar SuperKey (não registrar em log) | COBERTO | — |
| spec:594 | Exigir credencial para determinadas chamadas privilegiadas. | TODO:346 - [ ] Verificar credencial e rejeitar chave errada | COBERTO | — |
| spec:595 | Disponibilizar SuperCall a componentes autorizados. | TODO:345 - [ ] SuperCall para componentes autorizados | COBERTO | — |
| spec:596 | Verificar a credencial recebida. | TODO:346 - [ ] Verificar credencial e rejeitar chave errada | COBERTO | — |
| spec:597 | Rejeitar chamadas com credencial incorreta conforme o mecanismo. | TODO:346 - [ ] Verificar credencial e rejeitar chave errada | COBERTO | — |
| spec:598 | Integrar componentes de userspace com operações do KernelPatch. | TODO:347 - [ ] Interfaces para operações privilegiadas e módulos | COBERTO | — |
| spec:599 | Manter os segredos fora de logs e interfaces públicas. | TODO:448 - [ ] Nunca gravar SuperKey, senha de keystore ou bytes de chave privada | PARCIAL | TODO 448 proíbe gravar segredo em log, mas não o exclui de interfaces públicas. |
| spec:603 | Exibir interfaces WebUI fornecidas pelos módulos APM/KPM compatíveis. | TODO:382 - [ ] Exibir WebUI de módulos APM e KPM compatíveis | COBERTO | — |
| spec:604 | Integrar páginas de módulos no gerenciador. | TODO:391 - [ ] Integrar ao sistema de scripts e módulos | COBERTO | — |
| spec:605 | Reutilizar o modelo WebUI do KernelSU nos casos declarados compatíveis. | TODO:383 - [ ] Reutilizar o modelo WebUI do KernelSU quando o módulo declarar | COBERTO | — |
| spec:606 | Suportar interfaces baseadas em HTML, CSS e JavaScript. | TODO:382 - [ ] Exibir WebUI de módulos APM e KPM compatíveis; TODO:384 - [ ] Interação com módulos e gerenciamento de módulos pela UI | COBERTO | — |
| spec:607 | Permitir interação com módulos. | TODO:384 - [ ] Interação com módulos e gerenciamento de módulos pela UI | COBERTO | — |
| spec:608 | Expor recursos de gerenciamento de módulos a partir da interface. | TODO:384 - [ ] Interação com módulos e gerenciamento de módulos pela UI | COBERTO | — |
| spec:615 | Fornecer BusyBox para scripts de módulos. | TODO:388 - [ ] Fornecer applets BusyBox para scripts de módulos | COBERTO | — |
| spec:616 | Disponibilizar applets BusyBox. | TODO:388 - [ ] Fornecer applets BusyBox para scripts de módulos | COBERTO | — |
| spec:617 | Incluir suporte relacionado ao SELinux nas ferramentas fornecidas. | TODO:390 - [ ] Suporte relacionado a SELinux nas ferramentas fornecidas | COBERTO | — |
| spec:618 | Executar scripts no shell ash. | TODO:389 - [ ] Executar scripts no ash standalone | COBERTO | — |
| spec:619 | Ativar ASH Standalone Mode. | TODO:389 - [ ] Executar scripts no ash standalone | COBERTO | — |
| spec:620 | Executar scripts usando applets internos para reduzir dependência de comandos externos variáveis. | TODO:388 - [ ] Fornecer applets BusyBox para scripts de módulos; TODO:389 - [ ] Executar scripts no ash standalone | COBERTO | — |
| spec:622 | Permitir configuração do ambiente de execução. | nenhum TODO correspondente | AUSENTE | nenhuma tarefa configura o ambiente de execução; TODO 392 só menciona ferramentas auxiliares. |
| spec:623 | Integrar o BusyBox ao sistema de scripts e módulos. | TODO:391 - [ ] Integrar ao sistema de scripts e módulos | COBERTO | — |
| spec:630 | KernelPatch. | TODO:396 - [ ] Consultar estado do APatch e versões de APatch e KernelPatch | COBERTO | — |
| spec:631 | MagiskBoot. | TODO:576 - [ ] Inventário de versão e procedência de cada componente herdado: KernelPatch, Magis… | COBERTO | — |
| spec:632 | resetprop. | TODO:368 - [ ] `system.prop` via resetprop; `sepolicy.rule` | COBERTO | — |
| spec:633 | magiskpolicy e regras SELinux adicionais. | TODO:368 - [ ] `system.prop` via resetprop; `sepolicy.rule`; TODO:390 - [ ] Suporte relacionado a SELinux nas ferramentas fornecidas; TODO:576 - [ ] Inventário de versão e procedência de cada componente herdado: KernelPatch, Magis… | COBERTO | — |
| spec:634 | BusyBox. | TODO:388 - [ ] Fornecer applets BusyBox para scripts de módulos | COBERTO | — |
| spec:635 | Sistema de módulos APM. | TODO:391 - [ ] Integrar ao sistema de scripts e módulos | COBERTO | — |
| spec:636 | SuperCall e SuperKey. | TODO:339 ### AP-ROOT: root por SuperKey e SuperCall | COBERTO | — |
| spec:637 | WebUI de módulos. | TODO:382 - [ ] Exibir WebUI de módulos APM e KPM compatíveis | COBERTO | — |
| spec:644 | Consultar informações de estado do sistema APatch. | TODO:396 - [ ] Consultar estado do APatch e versões de APatch e KernelPatch | COBERTO | — |
| spec:645 | Inspecionar logs disponíveis. | TODO:397 - [ ] Diagnosticar carregamento de módulos; TODO:398 - [ ] Diagnosticar erros de atualização e de permissões; TODO:399 - [ ] Mostrar informações úteis durante o boot; TODO:401 - Texto de diagnóstico vai para UR-LOG, sem log paralelo; TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:646 | Consultar informações úteis durante a inicialização. | TODO:399 - [ ] Mostrar informações úteis durante o boot | COBERTO | — |
| spec:647 | Diagnosticar problemas no carregamento de módulos. | TODO:397 - [ ] Diagnosticar carregamento de módulos | COBERTO | — |
| spec:648 | Diagnosticar erros de atualização e permissões. | TODO:398 - [ ] Diagnosticar erros de atualização e de permissões | COBERTO | — |
| spec:649 | Inspecionar versões de APatch e KernelPatch. | TODO:396 - [ ] Consultar estado do APatch e versões de APatch e KernelPatch | COBERTO | — |
| spec:650 | Usar procedimentos de recuperação documentados para bootloop. | TODO:400 - [ ] Procedimentos de recuperação de bootloop do APatch | COBERTO | — |
| spec:651 | Atualizar componentes conforme a versão suportada. | TODO:356 - [ ] Atualizar componentes KernelPatch | COBERTO | — |
| spec:655 | A instalação exige condições apropriadas do bootloader. | TODO:359 - [ ] Verificar pré-requisitos do bootloader e do kernel antes de instalar | COBERTO | — |
| spec:656 | O kernel precisa atender aos requisitos de patch e símbolos necessários. | TODO:357 - [ ] Detectar requisitos de config do kernel e verificar suporte antes de iniciar; TODO:359 - [ ] Verificar pré-requisitos do bootloader e do kernel antes de instalar | COBERTO | — |
| spec:657 | A compatibilidade depende da arquitetura e versão do kernel. | TODO:498 - [ ] Classificação por backend e formato; requisitos de kernel, Android e ABI; TODO:600 - [ ] Cobertura por versão, arquitetura, kernel e device | COBERTO | — |
| spec:658 | KPM e APM não são equivalentes. | TODO:369 - [ ] Contrato APM por módulo (ZIP aceito não prova os estágios) | COBERTO | — |
| spec:659 | APatch não oferece Zygisk nativamente em todas as instalações. | TODO:594 - [ ] APatch sem Zygisk nativo: frameworks dependentes de Zygisk exigem integração adic… | COBERTO | — |
| spec:660 | Frameworks que dependem de Zygisk exigem integração adicional. | TODO:594 - [ ] APatch sem Zygisk nativo: frameworks dependentes de Zygisk exigem integração adic… | COBERTO | — |
| spec:661 | Uma implementação de hooking não deve ser presumida disponível apenas porque o aplicativo APatch está instalado. | nenhum TODO correspondente | AUSENTE | nenhuma tarefa proíbe presumir hooking pelo simples fato de o app APatch estar instalado. |
| spec:679 | Backup de imagem de boot: só Boot & Recovery. Guarda path, hash, slot de origem e data. System Toolkit e a tela de… | TODO:437 - [ ] Guardar path, hash, slot de origem e data; TODO:443 - [ ] Um único acervo: sem segundo repositório de imagem | COBERTO | — |
| spec:680 | Rollback de imagem: o mesmo dono. No lake, o alvo de rollback documentado é `boot_b`. Nos outros devices, o slot s… | TODO:440 - [ ] Alvo de rollback: `boot_b` no lake; em outros devices, o slot detectado | COBERTO | — |
| spec:681 | Log: um acervo só, dono System Toolkit (ID `UR-LOG`). Home, Root Access, Recovery e Extensions mostram recortes. N… | TODO:449 - [ ] Retenção até o usuário limpar; teto em bytes = UNKNOWN até medir; TODO:450 - [ ] Rollback de configuração não apaga o log | COBERTO | — |
| spec:682 | Segredo: o log não recebe SuperKey, senha de keystore, nem bytes da chave privada. A regra que estava só no parágr… | TODO:448 - [ ] Nunca gravar SuperKey, senha de keystore ou bytes de chave privada | COBERTO | — |
| spec:688 | Procurar um APK mais novo numa origem configurada pelo usuário. | TODO:454 - [ ] Procurar APK mais novo em origem configurada pelo usuário | COBERTO | — |
| spec:689 | Aceitar só pacote `me.weishu.kernelsu` cujo certificado tenha sha256 `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c38784… | TODO:455 - [ ] Aceitar só `me.weishu.kernelsu` com cert sha256 `efb8d49f0bb8022e55e5ec89db26c513… | COBERTO | — |
| spec:690 | Se o certificado divergir, ou se a instalação falhar, o APK anterior permanece o instalado. Não apagar a cópia do … | TODO:457 - [ ] Não apagar a cópia do último APK aceito antes da confirmação | COBERTO | — |
| spec:691 | Update inválido não é sucesso. IMPL `NOT_STARTED`, VALID `NOT_TESTED`. | TODO:458 - [ ] Update inválido não conta como sucesso | COBERTO | — |
| spec:695 | Estado do root. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:696 | Backend ativo. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:697 | Versão do backend. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:698 | Versão do manager. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:699 | Estado da comunicação manager/kernel. | TODO:479 - [ ] Estado da comunicação manager e kernel | COBERTO | — |
| spec:700 | Informações do dispositivo. | TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:701 | Versão do Android. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:702 | Versão do kernel. | TODO:478 - [ ] Estado do root, backend ativo, versão do backend e versão do manager | COBERTO | — |
| spec:703 | Arquitetura e ABI. | TODO:480 - [ ] Info do device: Android, kernel, arquitetura e ABI, estado conhecido do bootloader | COBERTO | — |
| spec:704 | Estado conhecido do bootloader. | TODO:480 - [ ] Info do device: Android, kernel, arquitetura e ABI, estado conhecido do bootloader | COBERTO | — |
| spec:705 | Estado das capacidades disponíveis. | TODO:481 - [ ] Estado das capacidades e dos módulos | COBERTO | — |
| spec:706 | Estado dos módulos. | TODO:481 - [ ] Estado das capacidades e dos módulos | COBERTO | — |
| spec:707 | Avisos de incompatibilidade. | TODO:482 - [ ] Avisos de incompatibilidade e alertas de segurança | COBERTO | — |
| spec:708 | Alertas de segurança. | TODO:482 - [ ] Avisos de incompatibilidade e alertas de segurança | COBERTO | — |
| spec:709 | Atalhos para operações comuns. | TODO:483 - [ ] Atalhos para operações comuns; histórico recente de ações privilegiadas; diagnóst… | COBERTO | — |
| spec:710 | Histórico recente de ações privilegiadas. | TODO:483 - [ ] Atalhos para operações comuns; histórico recente de ações privilegiadas; diagnóst… | COBERTO | — |
| spec:711 | Diagnóstico resumido. | TODO:483 - [ ] Atalhos para operações comuns; histórico recente de ações privilegiadas; diagnóst… | COBERTO | — |
| spec:715 | Lista de aplicativos autorizados. | TODO:64 - [ ] Gerenciar acesso root por app | PARCIAL | TODO 64 gerencia root por app, mas não pede lista/tela dos aplicativos autorizados. |
| spec:716 | Conceder/revogar root. | TODO:341 - [ ] Conceder e revogar root a apps autorizados; TODO:342 - [ ] Controlar quais apps têm root; TODO:343 - [ ] Administrar concessões e revogações | COBERTO | — |
| spec:717 | Histórico de solicitações. | TODO:486 - [ ] Histórico de solicitações de root (UR-LOG) | COBERTO | — |
| spec:718 | Perfis de acesso por app. | TODO:487 - [ ] Limites e políticas de privilégio (KSU-PROFILE, MG-SU-GRANT) | COBERTO | — |
| spec:719 | UID/GID/grupos, quando o backend permitir. | TODO:252 - [ ] Controlar o UID usado pelo `su` por app; TODO:253 - [ ] Controlar GID e grupos suplementares | COBERTO | — |
| spec:720 | Capacidades Linux, quando suportado. | TODO:254 - [ ] Configurar capabilities Linux | COBERTO | — |
| spec:721 | Contextos SELinux, quando suportado. | TODO:255 - [ ] Configurar contexto SELinux do `su` e regras específicas | COBERTO | — |
| spec:722 | Limites e políticas de privilégios. | TODO:487 - [ ] Limites e políticas de privilégio (KSU-PROFILE, MG-SU-GRANT) | COBERTO | — |
| spec:723 | Detecção do backend de autorização. | TODO:488 - [ ] Detecção do backend de autorização (UR-EXT-SDK, descoberta) | COBERTO | — |
| spec:724 | Controle explícito de permissões de ADB/shell quando suportado. | TODO:489 - [ ] Controle explícito de ADB e shell (MG-SU-GRANT) | COBERTO | — |
| spec:725 | Sessões root e diagnóstico. | TODO:490 - [ ] Sessões root e diagnóstico (MG-SU-DAEMON) | COBERTO | — |
| spec:726 | Logs de operações: evento no acervo `UR-LOG`, sem arquivo próprio. | TODO:447 - [ ] Uma linha por operação; as telas mostram recortes; TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:727 | Interfaces CLI de administração. | TODO:491 - [ ] Interfaces CLI de administração (proposto `UR-CLI`, ou MG-CLI) | COBERTO | — |
| spec:731 | Listagem unificada de módulos. | TODO:494 - [ ] Listagem unificada de módulos de Magisk, KSU e APM | COBERTO | — |
| spec:732 | Instalação de pacotes locais. | TODO:495 - [ ] Instalação de pacotes locais; importação offline | COBERTO | — |
| spec:733 | Ativação/desativação. | TODO:496 - [ ] Ativar, desativar, remover, atualizar; rollback quando tecnicamente possível | COBERTO | — |
| spec:734 | Remoção. | TODO:496 - [ ] Ativar, desativar, remover, atualizar; rollback quando tecnicamente possível | COBERTO | — |
| spec:735 | Atualização. | TODO:545 - [ ] Instalação direta e atualização pelo próprio app | COBERTO | — |
| spec:736 | Rollback quando tecnicamente possível. | TODO:496 - [ ] Ativar, desativar, remover, atualizar; rollback quando tecnicamente possível | COBERTO | — |
| spec:737 | Consulta de metadados. | TODO:515 - [ ] Validar a origem da imagem; gerenciar backups e metadados (UR-BACKUP) | COBERTO | — |
| spec:738 | Detecção de dependências. | TODO:488 - [ ] Detecção do backend de autorização (UR-EXT-SDK, descoberta) | COBERTO | — |
| spec:739 | Detecção de conflitos. | TODO:497 - [ ] Metadados, dependências e conflitos | COBERTO | — |
| spec:740 | Classificação por backend e formato. | TODO:498 - [ ] Classificação por backend e formato; requisitos de kernel, Android e ABI | COBERTO | — |
| spec:741 | Requisitos de [fora do repo] kernel/Android/ABI. | TODO:498 - [ ] Classificação por backend e formato; requisitos de kernel, Android e ABI | COBERTO | — |
| spec:742 | Indicador de compatibilidade verificada. | TODO:499 - [ ] Indicador de compatibilidade verificada | COBERTO | — |
| spec:743 | Execução da ação declarada pelo módulo. | TODO:500 - [ ] Executar a ação do módulo; abrir a WebUI | COBERTO | — |
| spec:744 | Abertura da WebUI. | TODO:536 - [ ] Integração de WebUI; registro de versões; contratos de compatibilidade | COBERTO | — |
| spec:745 | Logs da instalação: evento no acervo `UR-LOG`, sem arquivo próprio. | TODO:504 - [ ] Logs da instalação de módulo: UR-LOG | COBERTO | — |
| spec:746 | Proteção contra archives malformados. | TODO:501 - [ ] Proteção contra archive malformado; verificação de hash; assinatura criptográfica… | COBERTO | — |
| spec:747 | Verificação de hashes. | TODO:501 - [ ] Proteção contra archive malformado; verificação de hash; assinatura criptográfica… | COBERTO | — |
| spec:748 | Verificação criptográfica de assinaturas quando disponíveis. | TODO:501 - [ ] Proteção contra archive malformado; verificação de hash; assinatura criptográfica… | COBERTO | — |
| spec:749 | Importação de módulos offline. | TODO:495 - [ ] Instalação de pacotes locais; importação offline | COBERTO | — |
| spec:750 | Repositórios externos configuráveis. | TODO:502 - [ ] Repositórios externos configuráveis; política de confiança por repositório; atual… | COBERTO | — |
| spec:751 | Políticas de confiança por repositório. | TODO:502 - [ ] Repositórios externos configuráveis; política de confiança por repositório; atual… | COBERTO | — |
| spec:752 | Atualização de metadados e versões. | TODO:502 - [ ] Repositórios externos configuráveis; política de confiança por repositório; atual… | COBERTO | — |
| spec:753 | Gerenciamento de metamódulos KernelSU. Dono: runtime KSU. Um metamódulo ativo. Mount de `/system` depende de artef… | TODO:284 - [ ] Delegar montagem a metamódulo; TODO:290 - [ ] Evitar conflito entre metamódulos; TODO:292 - [!] Mount de `/system` depende de artefato externo medido. `meta-overlayfs` não está …; TODO:505 - [~] Metamódulos KSU (KSU-META), APM (AP-APM) e Magisk (MG-MOD-*): IDs existentes, ain… | COBERTO | — |
| spec:754 | Gerenciamento APM/APatch. Dono: `ApmRuntime`, separado do runtime KSU e do Magisk. Cobre `post-mount.sh`, `boot-co… | TODO:51 - [ ] Estágios de init: post-fs-data, post-mount, service, boot-completed, skip_mount; TODO:369 - [ ] Contrato APM por módulo (ZIP aceito não prova os estágios) | COBERTO | — |
| spec:755 | Gerenciamento de módulos Magisk. Dono: runtime Magisk, inclusive magic mount. | TODO:122 - [ ] Montagem systemless por scripts e configuração; TODO:125 - [ ] Alterar diretórios do sistema por montagem | COBERTO | — |
| spec:756 | Integração de módulos Zygisk por adapter. | TODO:133 - [ ] Injetar componentes compatíveis no Zygote; TODO:616 - [ ] **SP2 Zygisk**: MG-ZYG; módulos `zygisk_vector` e `bc-poc` | COBERTO | — |
| spec:757 | Avisos para funções que dependem de scripts, mounts ou hooks exclusivos. | TODO:503 - [ ] Avisos para funções que dependem de scripts, mounts ou hooks exclusivos | COBERTO | — |
| spec:761 | Inspecionar imagens locais. | TODO:508 - [ ] Inspecionar imagens locais; identificar o tipo; hash e tamanho | COBERTO | — |
| spec:762 | Identificar tipo de imagem quando possível. | TODO:508 - [ ] Inspecionar imagens locais; identificar o tipo; hash e tamanho | COBERTO | — |
| spec:763 | Consultar hash e tamanho. | TODO:508 - [ ] Inspecionar imagens locais; identificar o tipo; hash e tamanho | COBERTO | — |
| spec:764 | Integrar MagiskBoot nas operações compatíveis. | TODO:153 ### MG-BOOT: magiskboot; TODO:576 - [ ] Inventário de versão e procedência de cada componente herdado: KernelPatch, Magis… | COBERTO | — |
| spec:765 | Detectar fluxos APatch que exigem boot.img. | TODO:514 - [ ] Operações exclusivas por backend; detectar fluxos APatch que exigem `boot.img` | COBERTO | — |
| spec:766 | Identificar operações exclusivas por backend. | TODO:514 - [ ] Operações exclusivas por backend; detectar fluxos APatch que exigem `boot.img` | COBERTO | — |
| spec:767 | Gerenciar backups e seus metadados. | TODO:515 - [ ] Validar a origem da imagem; gerenciar backups e metadados (UR-BACKUP) | COBERTO | — |
| spec:768 | Validar a origem da imagem. | TODO:515 - [ ] Validar a origem da imagem; gerenciar backups e metadados (UR-BACKUP) | COBERTO | — |
| spec:769 | Mostrar compatibilidade por dispositivo. | TODO:509 - [ ] Mostrar compatibilidade por device | COBERTO | — |
| spec:770 | Oferecer dry-run e diagnóstico antes de operações destrutivas. | TODO:510 - [ ] Dry-run e diagnóstico antes de operação destrutiva | COBERTO | — |
| spec:771 | Exigir confirmação explícita antes de gravar partições. | TODO:511 - [ ] Confirmação explícita antes de gravar partição ("sim") | COBERTO | — |
| spec:772 | Registrar o resultado de cada operação. | TODO:512 - [ ] Registrar o resultado de cada operação em UR-LOG | COBERTO | — |
| spec:773 | Documentar os procedimentos de restauração. | TODO:513 - [ ] Documentar o procedimento de restauração | COBERTO | — |
| spec:779 | Arquivos e diretórios. | TODO:518 - [ ] Arquivos e diretórios; processos; serviços; montagens | COBERTO | — |
| spec:780 | Processos. | TODO:518 - [ ] Arquivos e diretórios; processos; serviços; montagens | COBERTO | — |
| spec:781 | Serviços. | TODO:523 - [ ] Monitoramento de serviços; inspeção de kernel; ferramentas do sistema | COBERTO | — |
| spec:782 | Propriedades Android. | TODO:519 - [ ] Propriedades Android (MG-PROP); permissões; SELinux (MG-POLICY) | COBERTO | — |
| spec:783 | Montagens. | TODO:518 - [ ] Arquivos e diretórios; processos; serviços; montagens | COBERTO | — |
| spec:784 | Informações de permissões. | TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:785 | SELinux. | TODO:519 - [ ] Propriedades Android (MG-PROP); permissões; SELinux (MG-POLICY) | COBERTO | — |
| spec:786 | Logs: leitura do acervo único `UR-LOG`. Esta seção não abre um segundo arquivo de log. | TODO:445 ### UR-LOG: acervo único de log; TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:787 | Shell. | TODO:520 - [ ] Shell; scripts; pacotes instalados; apps e dados quando permitido | COBERTO | — |
| spec:788 | Scripts. | TODO:520 - [ ] Shell; scripts; pacotes instalados; apps e dados quando permitido | COBERTO | — |
| spec:789 | Pacotes instalados. | TODO:520 - [ ] Shell; scripts; pacotes instalados; apps e dados quando permitido | COBERTO | — |
| spec:790 | Aplicativos e dados quando permitido. | TODO:520 - [ ] Shell; scripts; pacotes instalados; apps e dados quando permitido | COBERTO | — |
| spec:791 | Backup e restauração de arquivo do usuário só quando a operação não for imagem de boot. Imagem de boot pertence a … | TODO:521 - [ ] Backup e restauração de arquivo do usuário, fora de imagem de boot (imagem fica e… | COBERTO | — |
| spec:792 | Configuração de firewall por integração compatível. | TODO:522 - [ ] Firewall por integração compatível | COBERTO | — |
| spec:793 | Monitoramento de serviços. | TODO:523 - [ ] Monitoramento de serviços; inspeção de kernel; ferramentas do sistema | COBERTO | — |
| spec:794 | Inspeção de kernel. | TODO:523 - [ ] Monitoramento de serviços; inspeção de kernel; ferramentas do sistema | COBERTO | — |
| spec:795 | Ferramentas do sistema. | TODO:523 - [ ] Monitoramento de serviços; inspeção de kernel; ferramentas do sistema | COBERTO | — |
| spec:805 | Leitura de informações da CPU. | TODO:407 ### UR-PERF-READ: leitura de CPU, GPU, frequência, governor e thermal | COBERTO | — |
| spec:806 | Leitura de informações da GPU. | TODO:407 ### UR-PERF-READ: leitura de CPU, GPU, frequência, governor e thermal | COBERTO | — |
| spec:807 | Memória e armazenamento. | TODO:412 - [ ] Memória e armazenamento | COBERTO | — |
| spec:808 | Frequências, quando expostas. | TODO:413 - [ ] Frequências e governors, quando expostos | COBERTO | — |
| spec:809 | Governors, quando expostos. | TODO:413 - [ ] Frequências e governors, quando expostos | COBERTO | — |
| spec:810 | Devfreq, CPUFreq e parâmetros compatíveis. | TODO:414 - [ ] Devfreq, CPUFreq e parâmetros compatíveis | COBERTO | — |
| spec:811 | Temperatura e throttling. | TODO:415 - [ ] Temperatura e throttling | COBERTO | — |
| spec:812 | Monitoramento de potência quando disponível. | TODO:416 - [ ] Monitoramento de potência, quando disponível | COBERTO | — |
| spec:813 | Perfis de desempenho e economia. | TODO:422 - [ ] Perfis de desempenho e de economia | COBERTO | — |
| spec:814 | Integração com gerenciadores de kernel. | TODO:426 - [ ] Integração com gerenciadores de kernel | COBERTO | — |
| spec:815 | Benchmark e comparação antes/depois. | TODO:427 - [ ] Benchmark antes×depois contra baseline gravado | COBERTO | — |
| spec:816 | Overclock/undervolting somente quando suportados e validados. | TODO:432 - [!] Overclock e undervolt só com margem medida. DVFS do lake bloqueia no driver; marg… | COBERTO | — |
| spec:817 | Aplicação e reversão controladas de configurações. | TODO:423 - [ ] Aplicar e reverter só controles classificados como suportados; TODO:424 - [ ] Persistir só com autorização; reverter; recuperar após falha | COBERTO | — |
| spec:827 | Backups validados de imagens relevantes, lidos do acervo de Boot & Recovery. | TODO:527 - [ ] Backups validados (UR-BACKUP); diagnóstico de falha de boot | COBERTO | — |
| spec:828 | Diagnóstico de falha de boot. | TODO:527 - [ ] Backups validados (UR-BACKUP); diagnóstico de falha de boot | COBERTO | — |
| spec:829 | Desativação de módulos problemáticos quando suportada. | TODO:528 - [ ] Desativar módulos problemáticos (KSU-RESCUE) | COBERTO | — |
| spec:830 | Rollback de configuração. | TODO:558 - [ ] Configuração de performance ruim, com rollback | COBERTO | — |
| spec:831 | Histórico de operações. | TODO:530 - [ ] Histórico de operações (UR-LOG) | COBERTO | — |
| spec:832 | Identificação do backend e procedimento de recuperação correto. | TODO:531 - [ ] Identificar o backend e o procedimento de recuperação correto | COBERTO | — |
| spec:833 | Orientações de ADB/fastboot compatíveis com o dispositivo. | TODO:532 - [ ] Orientações de ADB e fastboot compatíveis com o device | COBERTO | — |
| spec:834 | Estado conhecido da recuperação. | TODO:533 - [ ] Estado conhecido da recuperação | COBERTO | — |
| spec:835 | Validação de backups antes de operações de alto risco. | TODO:438 - [ ] Validar backup antes de operação de alto risco | COBERTO | — |
| spec:839 | API para adapters. | TODO:462 - [ ] API para adapters; APIs por backend | COBERTO | — |
| spec:840 | APIs por backend. | TODO:462 - [ ] API para adapters; APIs por backend | COBERTO | — |
| spec:841 | API para ferramentas externas. | TODO:462 - [ ] API para adapters; APIs por backend | PARCIAL | TODO 462 cobre APIs de adapter/backend; API para ferramentas externas não está especificada. |
| spec:842 | SDK de extensões. | TODO:535 ### 6.7 Extensions Hub (proposto `UR-EXT-*`, além de UR-EXT-SDK) | COBERTO | — |
| spec:843 | API de descoberta de capacidade. | TODO:463 - [ ] API de descoberta de capacidade | COBERTO | — |
| spec:844 | Integração de WebUI. | TODO:536 - [ ] Integração de WebUI; registro de versões; contratos de compatibilidade | COBERTO | — |
| spec:845 | Registro de versões. | TODO:536 - [ ] Integração de WebUI; registro de versões; contratos de compatibilidade | COBERTO | — |
| spec:846 | Dependências declaradas. | TODO:464 - [ ] Registro de versões, dependências declaradas e permissões explícitas | COBERTO | — |
| spec:847 | Permissões explícitas. | TODO:464 - [ ] Registro de versões, dependências declaradas e permissões explícitas | COBERTO | — |
| spec:848 | Ciclo de vida e reversão. | TODO:465 - [ ] Ciclo de vida e reversão de adapter | COBERTO | — |
| spec:849 | Logs e diagnóstico: leitura do acervo `UR-LOG`. | TODO:549 - [ ] Consultar logs e informações de diagnóstico (UR-LOG) | COBERTO | — |
| spec:850 | Contratos de compatibilidade. | TODO:536 - [ ] Integração de WebUI; registro de versões; contratos de compatibilidade | COBERTO | — |
| spec:851 | Testes de conformidade para adapters. | TODO:466 - [ ] Contratos de compatibilidade e testes de conformidade para adapters | COBERTO | — |
| spec:852 | Mecanismo de atualização controlado. | TODO:537 - [ ] Mecanismo de atualização controlado (UR-SELFUPDATE) | COBERTO | — |
| spec:853 | Proveniência e licença das integrações. | TODO:467 - [ ] Proveniência e licença de cada integração | COBERTO | — |

## Ausentes e parciais

**AUSENTES:**

- spec:622 `- Permitir configuração do ambiente de execução.` — nenhuma tarefa configura o ambiente de execução; TODO 392 só menciona ferramentas auxiliares.
- spec:661 `- Uma implementação de hooking não deve ser presumida disponível` — nenhuma tarefa proíbe presumir hooking pelo simples fato de o app APatch estar instalado.

**PARCIAIS:**

- spec:715 `- Lista de aplicativos autorizados.` — TODO 64 gerencia root por app, mas não pede lista/tela dos aplicativos autorizados.
- spec:353 `- Fornecer interfaces de desenvolvimento para recursos avançados` — faltam as interfaces de desenvolvimento específicas de recursos KSU dependentes do kernel; o match do TODO é o SDK/APM genérico (TODO 468).
- spec:529 `- Oferecer procedimentos de atualização documentados.` — há atualização de componentes (TODO 356), mas não um procedimento de atualização documentado.
- spec:571 `- Incorporar KPM ao kernel corrigido.` — o TODO condiciona install persistente (TODO 378), mas não planeja incorporar KPM ao kernel corrigido.
- spec:576 `- Permitir operações que não são possíveis somente com APM.` — há item de recursos KPM de baixo nível (TODO 469), sem critério explícito de operação que APM não consegue executar.
- spec:599 `- Manter os segredos fora de logs e interfaces públicas.` — TODO 448 proíbe gravar segredo em log, mas não o exclui de interfaces públicas.
- spec:841 `- API para ferramentas externas.` — TODO 462 cobre APIs de adapter/backend; API para ferramentas externas não está especificada.
- CAPABILITIES:33 `MG-SU-GRANT: Conceder, revogar e listar root por app e por shell/ADB` — a descrição exige listar apps autorizados, mas os checkboxes não pedem a lista/tela; TODO 64 só diz gerenciar acesso por app.
- spec:42 `Definição do produto: capacidade real máxima, cobertura mensurável, limites e integração sem fundir runt…` — Falta exigir investigação de alternativa plausível ao bloqueio; TODO 566 mapeia limites e incompatibilidade, sem alternativa.
- spec:54 `Sem ocultação indetectável ou bypass de atestação.` — Há proibições pontuais de hide/bypass, mas nenhuma regra geral para bypass de atestação.
- spec:55 `Build/teste host; operação no device com sim; backup+guard; nunca destruir dados.` — Gate e backup aparecem; falta a regra explícita de preservação/não destruição de dados.
- spec:57 `Auditar alegações/proveniência, UNKNOWN sem evidência, sem stub/falso sucesso, pesquisa reprodutível.` — Não há corpus nem metodologia reprodutível de descoberta/pesquisa.
- spec:65 `Artefato C0.1 host: path/hash/comando/versão/data/resultado.` — TODO 23 cobre log/boot, não a imagem C0.1 host e seus dados de hash/header.
- spec:70 `Alegação sem path/hash/comando/versão/data/resultado é UNKNOWN.` — TODO 568 não inclui explicitamente resultado observado como campo da regra.
- spec:74 `Diagrama da arquitetura: manager/control plane, adapters/runtimes, engines e core do backend.` — As tarefas listam partes, mas não exigem artefato de arquitetura/topologia integrada.
- spec:85 `Seis seções de UI, Home/Settings/log/WebUI como suporte e ApmRuntime separado dos runtimes KSU/Magisk.` — As seis áreas e separação APM aparecem; não está registrada a regra de Home/Settings/log/WebUI não virarem 7ª seção.
- spec:91 `Enum de implementação: NOT_STARTED, IN_PROGRESS, IMPLEMENTED, INTEGRATED, EXTERNAL, EXCLUDED.` — Legenda não enumera o vocabulário completo, em especial EXTERNAL.
- spec:92 `Enum de validação: NOT_TESTED, HOST_TESTED, DEVICE_TESTED, FAILED, UNKNOWN.` — Legenda descreve HOST/DEVICE; os valores FAILED e UNKNOWN não são declarados como enum completo.
- spec:93 `Princípios de status honesto, domínios de inventário, retenção de inviáveis/externos e corpus/metodologi…` — Falta o corpus/metodologia reprodutível e a lista de domínios não aparece como deliverable único.
- spec:96 `Performance Engine: descoberta/inspeção, controles, medições, reversão e baseline; investigar a cadeia H…` — O TODO não exige leitura de volt/OPP/limites; só diz que não escreve OPP (TODO 418).
- spec:99 `Alvo lake, taxonomia de seis classes, máximo sustentável demonstrável e medidas contra baseline.` — Classes e métricas aparecem, mas não há tarefa explícita para achar/documentar o máximo sustentável demonstrável.

## Violações

- **(a) Requisito NÃO-REAL/FORA/EXCLUDED tratado como obrigatório: nenhuma encontrada.** TODO:4 diz que esses itens ficam na seção 10; TODO:627–634 lista as exceções fora do conjunto obrigatório.
- **(b) `[x]` sem evidência citada:** TODO:26 registra `adb install -r`/Success/dumpsys sem path ou artefato de evidência; TODO:229 marca KSU no boot com números, mas sem citar o arquivo do recorte no próprio item. TODO:23 é a referência do log C0.1, não citada por TODO:229.
- **(c) ID citado no TODOs ausente do CAPABILITIES.md:**
  - TODO:477 `UR-DASH` — ### 6.1 Dashboard (proposto `UR-DASH`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:491 `UR-CLI` — - [ ] Interfaces CLI de administração (proposto `UR-CLI`, ou MG-CLI); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:493 `UR-MOD-*` — ### 6.3 Module Center (proposto `UR-MOD-*`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:507 `UR-BOOT-*` — ### 6.4 Boot Image Center (proposto `UR-BOOT-*`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:517 `UR-SYS-*` — ### 6.5 System Toolkit (proposto `UR-SYS-*`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:526 `UR-REC-*` — ### 6.6 Recovery Center (proposto `UR-REC-*`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:535 `UR-EXT-*` — ### 6.7 Extensions Hub (proposto `UR-EXT-*`, além de UR-EXT-SDK); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
  - TODO:543 `MG-APP-MGMT` — ### 6.9 Gerenciamento do app Magisk (§1.5, proposto `MG-APP-MGMT`); CAPABILITIES.md não contém esse ID. TODO:475 reconhece que os IDs da seção 6 são propostos e ausentes.
- **(d) IMPL/VALID divergente:** nenhuma. Comparação das 44 linhas CAPABILITIES:33–84 com cabeçalhos/status TODO:60–461: 0 divergências.
- **(e) tarefa de device sem `[DEVICE]`:** TODO:26 descreve instalação no aparelho sem tag; TODO:214/217 descrevem instalar imagem/escrever partição sem tag (TODO:15 exige autorização); TODO:554–559 são cenários de recuperação por aparelho, flash/bootloop/performance e matriz por device, mas nenhum checkbox tem `[DEVICE]` (Spec:107 exige recuperação verificável).

## Não obrigatórios retirados do denominador

- spec:244 `MG-APP-STUB` — EXCLUDED; reempacotar identidade quebra autorização C0.3; spec:244; TODO:634
- spec:426 `OverlayFS no ksud deste pin` — NÃO-REAL; spec:424-426; TODO:630
- spec:427 `meta-overlayfs` — NÃO-REAL; não está no commit pinado; spec:424-427; TODO:630
- spec:428 `magic mount como função KSU` — NÃO-REAL nesta origem; é requisito Magisk MG-MOD-MOUNT; spec:424-428; TODO:42/630
- spec:492 `KSU-HWBP` — NÃO-REAL/UNKNOWN; spec:488-492; TODO:627
- spec:493 `KSU-PHYSMEM` — NÃO-REAL + FORA; espec de ocultação exposto; spec:488-493; TODO:628
- spec:494 `KSU-SYSCALL-API` — NÃO-REAL como API geral; hook su fica em KSU-ROOT; spec:488-494; TODO:629
- spec:430 `KSU-DETECT-SURFACE` — FORA; motivo de reduzir detection surface; spec:430; TODO:631
- spec:516 `AP-SEL-BYPASS` — FORA; bypass de AVC exposto; spec:516; TODO:633
- spec:580 `AP-KPM-PART` — NÃO-REAL; proteção de imagem de partição sem fonte; spec:580; TODO:632

## Sabotagem

Comandos executados: `cp docs/ultraroot/TODOs.md [fora do repo] docs/history/reports/todos-sabotado.md`, remoção da linha exata pela rotina abaixo, e execução do checker nos dois arquivos.

```sh
python3 - <<'PY'
from pathlib import Path
p=Path('[fora do repo] docs/history/reports/todos-sabotado.md')
lines=p.read_text().splitlines(keepends=True)
needle='- [ ] Revogar root pelo manager'
found=[i for i,line in enumerate(lines) if line.rstrip('\n')==needle]
if len(found)!=1: raise SystemExit(f'expected one exact line; found {len(found)}')
lines.pop(found[0])
p.write_text(''.join(lines))
print(f'removed={needle!r}; matches={len(found)}; copy={p}')
PY
python3 [fora do repo] docs/history/reports/sabotage_check.py docs/ultraroot/TODOs.md
python3 [fora do repo] docs/history/reports/sabotage_check.py [fora do repo] docs/history/reports/todos-sabotado.md
```
Saída literal:
```text
removed='- [ ] Revogar root pelo manager'; matches=1; copy=[fora do repo] docs/history/reports/todos-sabotado.md
docs/ultraroot/TODOs.md: KSU-ROOT revogação pelo manager = COBERTO (linha 243: - [ ] Revogar root pelo manager)
[fora do repo] docs/history/reports/todos-sabotado.md: KSU-ROOT revogação pelo manager = AUSENTE
```


Comando para confirmar que só a linha indicada foi removida: `diff -u docs/ultraroot/TODOs.md [fora do repo] docs/history/reports/todos-sabotado.md`.

Saída literal do diff:
```diff
--- docs/ultraroot/TODOs.md	2026-10-11 21:50:42.355370308 -0300
+++ [fora do repo] docs/history/reports/todos-sabotado.md	2026-10-11 22:04:00.699396291 -0300
@@ -240,7 +240,6 @@
 IMPL IN_PROGRESS · VALID NOT_TESTED · Adapter: KsuBackend · Device-gate: sim · Origem: KernelSU `6e386044` `uapi/supercall.h` (allow list), `manager/.../ksu.h` · Aceite: `su` atendido pelo KSU, uid=0, sem magisk
 - [~] Ler versão e UAPI na cópia `[artefato local, fora do repo]/grok/ultraroot-mgr/` (host)
 - [ ] Conceder root pelo manager (allowlist)
-- [ ] Revogar root pelo manager
 - [ ] Disponibilizar `su` e expor interfaces KSU às ferramentas compatíveis
 - [ ] Aplicar políticas de acesso com suporte do kernel
 - [ ] Hook interno de `su` (tracepoint `sys_enter` e kretprobe) como mecanismo do root KSU, não como API
```

## Auto-revisão

- Recontagem assertiva do gerador: 440 bullets brutos − 7 excluídos = 433 obrigatórios; 44 IDs; 32 unidades §0–§13; total 509.
- Soma por classe: 486 COBERTO + 21 PARCIAL + 2 AUSENTE = 509.
- A cópia sabotada está em `[fora do repo] docs/history/reports/todos-sabotado.md`; a checagem detectou `AUSENTE` após remover uma única linha.

**RELATÓRIO:** `docs/ultraroot/REVIEW_TODOs_codex4.md`
