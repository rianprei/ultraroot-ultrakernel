# SuperRoot/UltraRoot — plano-mestre (sessão autônoma 2026-10-11)

Dono fora até digitar "voltei". Regra dura: **host-only enquanto ausente; NENHUMA escrita no device (flash nem install) sem "sim" presencial.** Tudo abaixo que for device fica PREPARADO e enfileirado pro retorno.

Visão: `SUPERROOT_CRITERIA.md` + `ULTRAROOT_CRITERIA.md`. Tetos honestos: "absoluto/indetectável/onipotente" não existem literalmente; evasão de detecção não é maximizada; atestação de hardware não se vence. Entrego o root-manager mais completo e potente LEGÍTIMO.

## Estado
- **L0 — Core kernel (KSU integrado): ✅ C0.1 VALIDADO NO DEVICE** (boota, 429 módulos, hooks ativos, 0 erro novo). Root atual no aparelho = Magisk herdado (não KSU ainda).

## Sequência (cada camada: build host → gate host → [device-validate sob "sim" no retorno])
- **L1 — Userspace/manager (ksud + APK)** → KSU vira o root-granter. (C1.1/U3)
  - Host: compilar ksud (Rust+NDK) + manager APK (gradle). Corrigir KSU_VERSION (fallback 16).
  - Device (no retorno): instalar, KSU concede root, confirmar `su` em contexto ksu (não magisk).
  - STATUS: em build (grok).
- **L2 — Compat de módulos** (rodar módulos formato Magisk + formato KSU). (C1.2/C1.3)
  - Host: implementar/validar o contrato de módulo (module.prop/webroot/action.sh/metamount — os PORTÁVEIS da matriz) rodando os dois formatos.
- **L3 — Zygisk-compat** (rodar módulos Zygisk, via ZygiskNext). (C2.1)
- **L4 — Absorver features dos forks no-alvo** (da Fase 3): SUSFS (kernel patch, kutemeikito/ExtremeXT/simonpunk), `.rc` por módulo (YuKongA), scripts globais por estágio (MlgmXyysd/F-19-F), install em lote (Mkmo), custom umount por path (APatch). (U2)
- **L5 — Policy/perfis/WebUI/self-update** no manager. (U3/C1.4)
- **L6 — Robustez**: auto-recuperação (módulo quebrado → boota e desabilita), sobrevivência a OTA/A-B. (U5/U6/C3)
- **Parado: SuperKernel/overclock** (vendor/DTBO) — projeto separado pós-root.

## Disciplina da sessão autônoma
1. Uma camada por vez; grok/codex constroem; **cross-review obrigatório** (autor ≠ revisor) antes de aceitar.
2. Todo build em cópia reflink / scratch no disco (nunca pristina, nunca /tmp). Disco tem ~486G.
3. Cada camada só é "feita (host)" com evidência (artefato + gate). Device fica enfileirado.
4. `-X GET` sempre no gh; nunca criar repo; nunca tocar device.
5. Registrar cada avanço em `project-superroot-state` (memória) + aqui.
6. Ao fim do host-possível: montar o PACOTE de retorno (o que instalar/flashar no device, em ordem, sob "sim") e parar, esperando "voltei".

## Pacote de retorno (vai sendo preenchido)
- [ ] L1: ksud + APK + plano de install (adb install + push ksud).
- [ ] (device) C0.1 já flashado; confirmar estabilidade no uso real (watch de ~30 min — observacional do dono).

## Progresso autônomo 2026-10-11
- **L1 manager: artefatos PRONTOS** (ksud + APK v3.3.0-72_32673-debug), toolchain completo (rust+ndk29+jdk21+sdk37). MAS achado: KSU não concede root ainda — (a) KSU_VERSION do kernel caiu em 16 (userspace 32673); (b) assinatura esperada no kernel (c371061b…) ≠ cert do nosso APK (b830cb…). → **C0.2**: rebuild do kernel casando assinatura+versão (grok) + ultrareview do mecanismo (codex). Em andamento.
- **Pacote de retorno (device, sob "sim"):** [1] flashar C0.2_ksu_boot_b.img (kernel que autoriza nosso manager); [2] adb install do APK + push ksud; [3] confirmar `su` em contexto ksu (não magisk) = KSU concede root. Ordem e comandos a finalizar quando C0.2+review fecharem.

## Ultrareview da auth (codex, 2026-10-11) — condições pro KSU coroar o manager
Casar assinatura é o principal, MAS também: (1) pacote = me.weishu.kernelsu SEM sufixo .debug [✅ codex confirmou applicationId ok]; (2) CONFIG_KSU_DISABLE_MANAGER off [✅ C0.1]; (3) UAPI=5 casar kernel×userspace [⏳ verificar C0.2]. KSU_VERSION 16 vs 32673 = só reporting, NÃO barra root (corrigir mesmo assim). Risco: applicationIdSuffix .debug ou IS_PR_BUILD quebra a detecção; chave debug muda a cada rebuild → usar chave FIXA em produção. 7 achados em REVIEW_sig_por_codex.md.
ACEITE C0.2 (host): assinatura esperada=b830cb…; UAPI 5; pacote intacto; DISABLE_MANAGER off; gate_kmi_crc 0 mismatch; repack C02 verificado.

## L0+L1 HOST-COMPLETO E VERIFICADO (2026-10-11, autônomo)
C0.2 kernel (sha 9a34176da6bcf22695a44b2e3a0d2bd2e844c357ee2858e0b5360e9f79b92c3b) autoriza o nosso manager. Verificado POR MIM: APK cert sha256 = b830cb… == EXPECTED_HASH do kernel (apksigner v2); package me.weishu.kernelsu (aapt2); UAPI=5 (supercall.h); KSU_VERSION 32673; gate 2309/0/0; img 67108864/ANDROID!/v4. Manager: ksud + APK em [artefato local, fora do repo]/grok/manager/out/. Ressalva: cert é debug keystore desta máquina (rebuild do APK muda o cert → re-casar; usar chave FIXA p/ produção).

## PACOTE DE RETORNO C0.2 — OBSOLETO (não executar)
O bloco que mandava flashar a imagem C0.2 e instalar o APK debug foi retirado. Não é alvo operacional. A única imagem operacional é o C0.3, e a allowlist é `docs/ultraroot/guard_hashes_sp1.txt`.

## C0.3 — par ESTÁVEL (chave fixa) — host-completo e VERIFICADO (autônomo 2026-10-11)
Chave release fixa em [chaves locais do dono, fora do repo]/superroot-release.jks (SEGREDO, 0400, gitignored, senha nunca em relatório). APK release: [artefato local, fora do repo]/grok/manager/out/SuperRoot-manager-release.apk, cert sha256 efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9. Kernel C0.3 EXPECTED_HASH = esse cert (verificado por apksigner POR MIM: bate). C03_ksu_boot_b.img sha 2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe, 67108864, ANDROID!, gate 2309/0/0, package me.weishu.kernelsu, UAPI 5. **Use C0.3 (não C0.2) no device** — chave fixa = manager rebuilável sem reflash.

## PACOTE OPERACIONAL (único; device sob "sim", fastboot por teclas, bateria>=60%)
Allowlist já pronta: `export FASTBOOT_GUARD_HASHES=docs/ultraroot/guard_hashes_sp1.txt`
Não criar `guard_hashes.txt` na raiz do kernel. Entre o Z0.4 e o flash não há reboot. Reboot só com `tools/fastboot_guard.sh reboot`, ou pelas teclas.
# Z0 getvars → T-1 flash do backup pelo guard → reboot pelo guard → flash de C03_ksu_boot_b.img pelo guard → reboot pelo guard
# adb install de [artefato local, fora do repo]/grok/manager/out/SuperRoot-manager-release.apk e abrir o manager
# ACEITE: accept_ksu_root.sh exige uid=0( na saída de su -c id, UAPI 5 e contexto sem magisk
# ROLLBACK: guard rollback do backup; em seguida guard reboot; o cenário fecha quando o boot do backup é observado

## SP1 host Tasks 1-6 (2026-10-11)
Host-only. Task 7 não rodou. Nenhum adb e nenhum fastboot real. `[fora do repo]` não é repositório git; este parágrafo registra as tasks 1 e 3-6. A task 2 está no commit `c8e78121d602a1b4bf8b00318d2d3acec78c965a` de `kernel/lake` (`tools/fastboot_guard.sh` + `tools/selftest_fastboot_guard.sh`). O `guard_hashes.txt` solto na raiz desse repo não entrou no commit.

Allowlist do dia: `docs/ultraroot/guard_hashes_sp1.txt` com o backup `3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7` e o C0.3 `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe`. O prefixo C0.1 `74090569` não está no arquivo. `rollback` flasha `boot_b` pelos gates já existentes e, só se esse flash sair 0 e o slot atual for `b`, emite `set_active b`. `set_active a` continua recusado. Aceite: `docs/ultraroot/accept_ksu_root.sh` (sem fixture sai 2 e não chama adb). Selftest do guard: 44/44. `run_all_checks`: 116/116. Relatório: `docs/ultraroot/SP1_IMPL_REPORT.md`.

## SP1 fixes do review (2026-10-11, host)
`[fora do repo]` não é git; este parágrafo registra os três fixes. Nenhum arquivo rastreado do kernel mudou. O `guard_hashes.txt` não rastreado, que listava o hash do C0.1, foi removido.
Aceite agora exige `uid=0(` na saída de `su -c id`, evidência KSU (ksud ou `/data/adb/ksu`, UAPI 5) e contexto sem magisk. Sem fixture continua saindo 2, sem adb.
Recovery: depois do rollback, reboot só por `tools/fastboot_guard.sh reboot` ou pelas teclas. Sem reboot entre o Z0.4 e o flash. O cenário de device fecha quando o boot é observado.
