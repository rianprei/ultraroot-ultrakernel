# Dossiê de contexto UltraRoot — Codex #4
Data da coleta: 2026-10-12. Somente leitura; nenhum comando de device, build, instalação ou grant foi executado.
Citações abreviadas: H3/H2 = `docs/history/handoffs/HANDOFF-ultraroot-2026-10-12-v3.md` / `docs/history/handoffs/HANDOFF-ultraroot-2026-10-11-v2.md`; T/C/CM = `docs/ultraroot/TODOs.md` / `CAPABILITIES.md` / `COMPAT_MATRIX.md`; MP = `docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`; M = `research/MASTER_PLAN.md`; S = `docs/superpowers/specs/2026-10-11-ultraroot-design.md`. Abreviações sempre referem-se a `[fora do repo]/`, salvo quando o caminho completo aparece.

DIAG_BACKEND_INDISPONIVEL.md = docs/ultraroot/; os cabeçalhos e relatórios abreviados nas seções abaixo usam os diretórios definidos acima.
Caminhos curtos usados abaixo: evidence/... = docs/history/evidence/...; app/build.gradle.kts = ultraroot/app/build.gradle.kts; fastboot_guard.sh e selftest_fastboot_guard.sh = kernel/lake/tools/; accept_ksu_root.sh e guard_hashes_sp1.txt = docs/ultraroot/; C03_stablekey_result.md = docs/history/research/phase4/; LOOP_AUDITORIA_ui5_ui6.md = docs/ultraroot/; SUPERROOT_CRITERIA.md = docs/history/research/; repositórios Git: lake=kernel/lake, KernelSU=kernel/upstream/KernelSU, Magisk=[artefato local, fora do repo]/grok/src/Magisk.
## 1. Aparelho

- H3 (2026-10-12:6-10) documenta POCO C75 2410FPCC5G/lake, kernel 6.6.89, UltraRoot ui8/vc40008 instalado via `pm install -r` de `/data/local/tmp`, pasta F2FS. Não consultei o aparelho.
- KSU reconheceu/coroou o manager como UID 10296; a tela leu a allowlist vazia (“Nenhum app com root”). Isso valida leitura e reconhecimento, não uma concessão de root (H3:7-8; `evidence/2026-10-12_kernel_manager_found.txt:1-2`).
- Grant continua NÃO testado; `accept_ksu_root.sh` reprovou contexto Magisk: `u:r:magisk:s0` (H3:8-9; `evidence/2026-10-12_accept_after_manager_found.txt:1`). “Manager coroado” significa reconhecido pelo kernel; KSU ainda não assumiu o `su` (H3:7-9; T:37-38).
- H2 afirma C0.3 flashado e bootado (H2:15); TODO atual mantém C0.3 no aparelho como IN_PROGRESS por falta de log de boot atribuído à imagem (T:32; MP:7). Tratar boot específico de C0.3 como UNKNOWN até reconciliar a evidência.

## 2. Código do UltraRoot

- Fonte atual: `app/build.gradle.kts:21-22` declara versionCode 40009 / versionName `0.2.0-ui9`.
- `sha256sum` executado: ui8 `5861465c14a99e4bade3b6efa5885bc31eae7904b694c993bef0ac19ca7a1529`; ui9 `671d0c9ce4d14bfc910870ff5273a3a65990d179294e0f864f4ac041fc97f48a`. Ambos batem com H3:13-14. H3:14 chama ui9 de último build e diz que não foi instalado; data do handoff: 2026-10-12 (H3:1).
- O artifacto ui9 está em `ultraroot/out/UltraRoot-own-ui9-vc40009.apk`; ui8 é a versão documentada como instalada (H3:6,13-14). Testes/build log específicos de ui9: UNKNOWN; em `out/` não há log ui9 na listagem, só `gradle-own-inc1.log` e `gradle-own-inc2.log`.
- Git, consulta de leitura em 2026-10-12: `ultraroot-app` e `[fora do repo]` não são repositórios Git (fatal: not a git repository). Kernel lake: `main...origin/main [ahead 1]`, modificados `tools/fastboot_guard.sh` e `tools/selftest_fastboot_guard.sh`; HEAD `c8e78121d602a1b4bf8b00318d2d3acec78c965a`, 2026-10-11 (git status/log). KernelSU: limpo em `main`, HEAD `6e3860443f8fea7c5fc226277e53888033796488`, 2026-10-10. Magisk: limpo em `master`, HEAD `e8915d9db15f5aae93973ffe65068e34df375a6a`, 2026-10-09.
- UI: a última auditoria com captura registrada é ui6, nota 3/4; ajustes de texto e cor ficaram para ciclo posterior (LOOP_AUDITORIA_ui5_ui6.md:26-42). Ui9 precisa de instalação autorizada e nova captura/revisão (H3:28,49); renderização atual de ui9: UNKNOWN.

## 3. Kernel e manager

- Decisão de arquitetura: um core KernelSU integrado, com adapters isolados; não fundir engines incompatíveis nem anunciar compatibilidade sem implementação e validação (SUPERROOT_CRITERIA.md:6; S:42-54,72-88).
- Pin KernelSU: `6e386044`; checkout limpo conforme `git status`. C0.3 em `scratch/grok/c01-build/C03_ksu_boot_b.img`; hash medido agora: `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe` (MP:7,44-46; M:50; sha256sum).
- Cert público esperado: `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`; C0.3 documenta EXPECTED_HASH igual ao certificado, UAPI 5 e imagem de 67.108.864 bytes (C03_stablekey_result.md:17,43-45,78-88; M:50).
- Gate host: C03_stablekey_result.md:64-70 registra 557 arquivos de módulos / 370 únicos, 2309 símbolos comparados, 0 mismatches, 0 exports ausentes e 0 CRCs conflitantes. Isso não prova o boot inteiro, 429 módulos carregados, grant, nem recovery no device (S:65-66; T:32; C:9-10).
- Device: manager reconhecido e allowlist lida vazia; `su` ainda passa pelo Magisk e o aceite falha (DIAG_BACKEND_INDISPONIVEL.md:35-39; evidências citadas na seção 1). Host: par C0.3/cert e gate acima estão documentados como verificados; a validação da imagem C0.3 no device segue parcial no TODO (T:32; MP:7).

## 4. Três ajustes da auditoria para flash

- (a) **Feito no host:** allowlist contém backup e C0.3, não C0.1; plano fixa C0.3 como alvo (guard_hashes_sp1.txt:1-2; MP:5,31,37,44-49; M:63). Hash C0.3 confirmado por sha256sum nesta coleta.
- (b) **Feito no código/selftest, validação somente host documentada:** rollback prepara flash de `boot_b`, reativa só `b` após flash bem-sucedido, e valida slot atual; alvo `a` não é enviado (fastboot_guard.sh:108-127,174-193). O selftest afirma exigir `set_active b` e ausência de `set_active a` (selftest_fastboot_guard.sh:427-442); M:63 registra 44/44. Não rodei o selftest. O checkout lake está modificado e a revisão cruzada segue pendente (T:35-36).
- (c) **Feito no script, aceite no device aberto:** script exige uid=0, evidência KSU (ksud ou /data/adb/ksu), UAPI 5 e contexto sem Magisk; sem fixture/opt-in sai 2. RC de `id` e do probe são checados antes do conteúdo (accept_ksu_root.sh:4-8,14-25,31-59,63-98). Evidência host antiga cobre stub KSU/Magisk/sem opt-in (evidence/2026-10-11_manager_grant_teste.txt:22-26); evidência device atual reprova Magisk (evidence/2026-10-12_accept_after_manager_found.txt:1). T marca os três fixes feitos e a revisão cruzada pendente (T:35-36).

## 5. TODOs: contagem e dez itens abertos prioritários

- Contagem reproduzível em CAPABILITIES.md: `grep -cF '| OBRIG |'` = 44; `grep -cF '| OBRIG-PROPOSTO |'` = 8. Os 44 OBRIG estão abertos: 42 NOT_STARTED + 2 IN_PROGRESS e 44 NOT_TESTED; os 8 propostos estão NOT_STARTED/NOT_TESTED (C:3-4,54,79,85-92; contagem por grep em 2026-10-12).
- TODOs, contagem por `grep -cF` de marcador: 379 `[ ]`, 10 `[x]`, 8 `[~]`, 12 `[!]`; não li TODOs integralmente, apenas contagens, cabeçalhos e trechos necessários.
- Dez prioridades abertas, em ordem prática:
  1. Revisão cruzada dos fixes (a-c) antes de flash; revisor Grok pendente (T:36).
  2. Fechar prova de que C0.3 é o kernel bootado; estado atual parcial por falta de log (T:32; H2:15).
  3. Implementar UI inc3 de concessão/revogação por app (H3:8,51; T:37; T:245-257).
  4. Executar grant de teste e obter aceite KSU sem contexto Magisk; hoje não passa (T:37; evidence/2026-10-12_accept_after_manager_found.txt:1).
  5. Migrar/testar no aparelho os quatro módulos nativos KSU (T:41-44; CM:20,24).
  6. Implementar SP2/Zygisk e migrar `zygisk_vector`, `bc-poc` (T:45-46,641).
  7. Implementar SP3 adapters e migrar `always-trust-user-certs`, `tabs-ca-inject` (T:47-48,642).
  8. Não desligar Magisk até migração e validação dos oito módulos (T:38; H3:31; CM:20).
  9. Instalar ui9 sem incremental, capturar e revisar; depende de “sim, instalar ui9” (H3:28,49).
  10. Validar cenários de recovery no device e matriz por device/domínio (T:572-578,624-627).

## 6. Oito módulos Magisk antes da coroação

Classificação abaixo é plano de migração; a execução sob KSU continua UNKNOWN (CM:20,24-26; T:41-48; H2:16).

| Módulo | Tipo/dependência | Status |
|---|---|---|
| `hosts` | Nativo KSU | Device pendente |
| `yt-noads` | Nativo KSU | Device pendente |
| `poco_perfgov` | Nativo KSU | Device pendente |
| `termuxadb_rootport` | Nativo KSU | Device pendente |
| `zygisk_vector` | Zygisk, SP2 | Bloqueado por SP2 |
| `bc-poc` | Zygisk, SP2 | Bloqueado por SP2 |
| `always-trust-user-certs` | Adapter, SP3 | Bloqueado por SP3 |
| `tabs-ca-inject` | Adapter, SP3 | Bloqueado por SP3 |

## 7. Riscos e bloqueios por impacto, com gate

1. **Aceite KSU versus Magisk:** o teste pede contexto sem Magisk, mas o `su` atual é do Magisk; o desligamento dele está bloqueado até os oito módulos migrarem. Definir ordem/prova de aceite antes de qualquer desativação (H3:9,30-31; T:37-38).
2. **C0.3 no device:** handoff v2 afirma flash/boot, TODO e plano atuais não fecham prova específica; gate de flash é evidência reconciliada + revisão cruzada dos três fixes + autorização explícita. Slot B no lake; nunca A (H2:15,54; T:32,36; MP:7,15; fastboot_guard.sh:174-193).
3. **Módulos:** nenhum dos oito está provado sob KSU; Magisk deve permanecer ativo até prova por módulo e conclusão SP2/SP3 (CM:20,24-26; T:38,41-48,641-642).
4. **Segurança de recovery:** matriz, flash falho, boot-loop, interrupção e rollback no device seguem abertos; gate são backup/hash, guard, procedimento de slot validado e “sim” do dono (T:573-578,626; S:106-114; H3:34-35).
5. **Performance:** leitura sysfs parcial no host e escrita/rollback ainda não implementados; SP4 exige matriz HW, baseline, estabilidade, térmica e rollback; overclock permanece condicionado a margem medida (C:79-80; T:422-438,643; S:95-104).
6. **UI:** ui9 existe como artifacto, mas não tem validação visual documentada; instalar e capturar sob autorização (H3:14,28,49; LOOP_AUDITORIA_ui5_ui6.md:29-42).

## 8. Perguntas para o orquestrador

1. Qual evidência fecha C0.3 no device: aceitar H2:15 ou manter o gate parcial de T:32 até obter log vinculado à imagem?
2. Confirmar a próxima ação de device: instalar ui9 por `pm install -r` sem incremental e fazer captura/review?
3. A ordem do aceite KSU deve esperar a migração dos oito módulos e desativação autorizada do Magisk, ou existe teste aprovado que distingue KSU mantendo Magisk ativo?
4. Quais evidências mínimas validam cada migração de módulo antes de desligar Magisk?
5. Quem fará a revisão independente pendente dos fixes a-c antes de qualquer flash, dado o status modificado do checkout lake?

## Checklist de coleta

- [x] Dois handoffs, CAPABILITIES e COMPAT_MATRIX lidos.
- [x] Contagens TODOs por grep; arquivo não lido integralmente.
- [x] sha256sum de ui8/ui9 comparado com H3.
- [x] Git status e último commit consultados para app, lake, KernelSU, Magisk e raiz superroot.
- [x] Seções 1-6 verificadas contra as fontes citadas.
- [x] Única escrita: este arquivo.
