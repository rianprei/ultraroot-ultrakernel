# Critérios de "UltraRoot" — o tier máximo (maestro, 2026-10-11)

UltraRoot = o nível mais elevado do SuperRoot: root presente em **userspace + kernel + boot**, gerenciado por **1 app completo** com o superset de features de Magisk+KernelSU+APatch+forks. SuperRoot (TIER 0–3 em `SUPERROOT_CRITERIA.md`) é a **fundação**; UltraRoot é o **topo** (TIER U) que se empilha em cima — não substitui, estende.

## Tradução HONESTA dos adjetivos do dono (alvo real × teto)
| Adjetivo pedido | Alvo de engenharia REAL | Teto honesto (não negociável) |
|---|---|---|
| "root em tudo (userspace+kernel+boot)" | Cobertura das 3 camadas: KSU in-tree (kernel), daemon+manager (userspace), boot via nosso pipeline gated | REAL e alcançável |
| "todas as features" | Superset da matriz + forks (Fase 3), absorvido **incremental** | Alcançável ao longo do tempo, não no dia 1 |
| "manager mais completo" | 1 APK unificado, todas as features de gestão | REAL |
| "indetectável" | **Reduz** superfície de detecção (hide/denylist/sulist/susfs) — passa checagens locais comuns | **NÃO existe root indetectável.** Atestação remota com raiz de hardware (Play Integrity STRONG, TEE) lê o bootloader destravado por baixo de qualquer root. Fora de alcance — declarado. |
| "indestrutível / inquebrável / invencível" | Robusto + recuperável: inbrickável (risco→0 via backup+guard), auto-recuperação, sobrevive a OTA/A-B | Nenhum software é literalmente indestrutível; meta = recuperação garantida, não imunidade |
| "imutável" | Integridade verificada por gates (nada entra sem passar) | Não "impossível de alterar" — isso contradiz um root, que por definição altera |
| "onipotente" | Capacidade máxima DENTRO do device (kernel+userspace) | Para no que hardware/bootloader-locked permite |
| "onisciente" | Observabilidade máxima do estado do sistema (logs, mounts, processos) | Não literal |
| "o root mais perfeito absoluto" | Medido pelos critérios abaixo com prova | Adjetivo não é critério; a checklist é |

## TIER U — critérios UltraRoot (empilha sobre SuperRoot T0–T3, todos com verificação observável)
- [ ] **U1 Cobertura de 3 camadas**: root ativo e cooperante em kernel (KSU in-tree), userspace (daemon/manager) e boot (sobrevive ao ciclo de boot). Verif: `su` funciona + módulo de kernel ativo + daemon vivo pós-boot, os três ao mesmo tempo.
- [ ] **U2 Superset de features 100%**: toda feature PORTÁVEL da matriz (Fase 1) + toda feature NOVA no-alvo dos forks (Fase 3: SUSFS, .rc por módulo, scripts globais, install em lote) ou está absorvida, ou tem decisão explícita "fora" com razão. Verif: checklist feature-a-feature, cada uma [x] com teste ou [fora] com motivo.
- [ ] **U3 Manager completo (1 APK)**: gerencia grants, módulos (formatos Magisk+KSU), perfis por app, ocultação por UID, WebUI, e update do próprio engine. Verif: cada função exercida no app.
- [ ] **U4 Ocultação (bounded)**: minimiza superfície de detecção local (denylist/sulist/susfs integrado). Verif: app-alvo comum não detecta root via checagens locais. **DECLARADO: não cobre atestação remota hardware-backed.**
- [ ] **U5 Auto-recuperação**: se um módulo/engine falhar no boot, o device ainda sobe (safe-mode/disable automático) sem brick. Verif: plantar módulo quebrado → device boota e desabilita sozinho.
- [ ] **U6 Persistência multi-vetor**: sobrevive a reboot (sempre), a OTA/A-B (re-aplica) e a update do SuperRoot (gated). Verif: cada cenário observado.

## O que NÃO é critério (impossível ou fora de escopo — não perseguir)
- Invisibilidade a atestação remota com raiz de hardware (Play Integrity STRONG / TEE) — impossível com bootloader destravado.
- Indestrutibilidade/onipotência literais — além do físico.
- Qualquer coisa que exija travar o bootloader (quebraria o root) ou tocar preloader/TEE (fora, risco de brick irreversível — ver [[project-lake-kernel-state]]).

## Definição de "UltraRoot alcançado"
SuperRoot T0+T1 (real) + T2 (supremo) + T3 (robusto) **e** TIER U (U1–U6) — todos [x] com prova observável. Device só sob backup+fastboot_guard+slot B (nunca A)+"sim" do dono. Os adjetivos absolutos NÃO entram: o que conta é a checklist provada.
