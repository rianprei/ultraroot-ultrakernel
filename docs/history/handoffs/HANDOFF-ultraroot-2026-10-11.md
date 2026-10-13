# HANDOFF — UltraRoot (2026-10-11)

Responder **pt-BR**. Projeto base: `[fora do repo]`. Não duplico aqui o que já está nos artefatos — referencio por path.

## O que é
UltraRoot = plataforma/manager de root unificado (app PRÓPRIO) sobre o engine KernelSU-integrado, pro POCO C75 (lake, GKI 6.6.89). Fundir as FUNÇÕES dos managers (Magisk/KSU/APatch/SukiSU) via control-plane + adapters — NÃO fundir os mecanismos num runtime.

## Fontes de verdade (ler primeiro)
- Memória: `project_superroot_state.md` (estado corrente, detalhado), `project_superkernel_vision.md` (overclock→Performance Engine; veredito: DVFS do lake bloqueia OC no driver, margem UNKNOWN), `project_lake_kernel_state.md`.
- Spec FINAL rev.7: `[fora do repo]/docs/superpowers/specs/2026-10-11-ultraroot-design.md` (44 capacidades REAIS obrigatórias; honesto, zero inventado/evasão; cross-revisado).
- Mapa: `docs/ultraroot/CAPABILITIES.md` (44 OBRIG, 14 colunas, status IMPL×VALID). Também: COMPAT_MATRIX.md, RECOVERY_SUITE.md, MODULE_PARITY.md, MODULE_MIGRATION_lake.md, HW_PERF_MATRIX_lake.md, accept_ksu_root.sh, DEVICE_PLAN_SP1.md, guard_hashes_sp1.txt.
- Plano SP1: `[fora do repo]/docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`.

## Estado do DEVICE agora (POCO C75, serial/IP no terminal, não publicar)
- Kernel **C0.3** flashado+bootado (`[artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img`, sha 2977e1dc) — autoriza nosso manager pelo cert fixo efb8d49f.
- Manager **UltraRoot (reskin inc2b)** instalado (package me.weishu.kernelsu); **KSU vivo** (ksud 3.3.0-72 uapi 5).
- **Magisk + 8 módulos INTACTOS** (hosts, yt-noads, poco_perfgov, termuxadb_rootport = nativo-KSU; zygisk_vector, bc-poc = precisam Zygisk/SP2; always-trust-user-certs, tabs-ca-inject = precisam adapter/SP3). Nenhum incompatível.
- **manager-grant = NOT_TESTED** (o `su` ainda roteia pro Magisk; NÃO foi provado que o KSU concede root via nosso app). Honesto: não marcar validado sem a prova.

## DIREÇÃO ATUAL (o pedido que interrompeu, AINDA NÃO INICIADO)
Construir um **APP PRÓPRIO** (não reskin do KSU manager): **Material 3** (https://m3.material.io/), tema **DARK/preto**, UI/UX/arquitetura 100% nossas, todas as funções do UltraRoot. Reusar SÓ a camada de backend (JNI/ioctl pro engine KSU — version/UAPI/allowlist/perfil) como `:core-backend` isolado; nada de UI/identidade do KSU. Projeto novo em `ultraroot/` (Compose novo). O dispatch desse 1º incremento foi INTERROMPIDO — o contrato (`CONTRATO_grok_ownapp.txt`) NÃO chegou a ser escrito. 6 seções: Root Access, Module Center, System Toolkit, Kernel&Performance, Boot&Recovery, Extensions Hub.
**Decisão pendente do dono:** package. O C0.3 autoriza `me.weishu.kernelsu` pelo cert efb8d49f; rebrand de package (ex. dev.superroot.ultraroot) exige recasar o EXPECTED_HASH/cert no kernel (novo build C0.4) — não quebrar a autorização sem avisar.

## O que já foi feito (reskin, serve de ponte)
App reskin inc1+inc2+inc2b (cópia do manager KSU em `[artefato local, fora do repo]/grok/ultraroot-mgr/`): label UltraRoot, seções Kernel&Performance + Root Access Center (lê allowlist real, só-leitura, leitura-falha→"indisponível"), host-safe. Host tests 3/3. APK `out/UltraRoot-mgr-inc2b.apk` (cert efb8d49f). O app PRÓPRIO substitui isso; o reskin é ponte até cobrir as funções.

## Próximos passos
1. **App próprio M3-dark** (1º incremento: scaffold + tema dark + navegação 6 seções + Kernel&Performance lendo dado real via :core-backend). Decidir package com o dono.
2. Validar **manager-grant** no device (abrir o app, conceder root a app-teste, rodar `accept_ksu_root.sh` → uid=0+KSU+não-magisk) — precisa device + "sim".
3. inc3: grant/revoke. Migrar os 4 módulos nativos pro KSU (host-prep; install via KSU = device-gated).
4. **Coroação** (KSU vira dono do `su`, DESLIGAR Magisk) SÓ depois de migrar os 8 módulos — senão perde função.
5. Performance Engine (SP4): tuning seguro (governors/IO/zram) já; OC só se a HW matrix provar margem (hoje bloqueado no driver).

## Agentes e regras
- Via **maestri**: `grok` (build/RE, confiável), `Codex #4` (análise/review, "braço direito"). Cross-review obrigatório (autor≠revisor). Subagentes **Haiku 5.5 no máximo** (autorizado). **Claude Code #2 = NÃO confiável, manter fora.** Scratch de agente em disco (`[artefato local, fora do repo]/<agente>/`), NUNCA /tmp (tmpfs).
- Device SÓ sob "sim, <passo>" presencial. Protocolo: Z0 (detectar slot ativo; lake; is-userspace≠yes; partição 67108864; bateria≥60%) → T-1 (flash backup, prova caminho) → flash → reboot via `kernel/lake/tools/fastboot_guard.sh` (allowlist `guard_hashes_sp1.txt` = backup 3890fb96 + C0.3 2977e1dc). **Boot-safety: slot B, NUNCA slot A** (A=OS3.0.20.0 antigo). Backup: `[fora do repo]/mods/lake-kernel/backup-2026-10-05/` (+ a cópia do 2º disco FOI apagada no merge — só a do disco principal resta). Rollback = `fastboot_guard.sh rollback <backup>` (reativa só set_active b).
- Chave de assinatura FIXA (SEGREDO): `[chaves locais do dono, fora do repo]/superroot-release.jks` (cert público efb8d49f). Nunca commitar/publicar.
- **Legítimo**: sem evasão/ocultação "indetectável" (impossível vs atestação de HW; fora do escopo). Toda alegação com evidência (path/hash/cmd/data) senão UNKNOWN; compila≠validado. Nunca destruir dados. SELinux fica Permissive.
- Disco: 2º SSD foi fundido no btrfs do CachyOS (nvme0n1→/; ~488G livres; boot multi-device via hook systemd + initramfs regenerado). NUNCA tocar nvme1n1 (é o /).

## Suggested skills
- `superpowers:writing-plans` + `superpowers:executing-plans`/`subagent-driven-development` (para o app próprio).
- `fable-method`, `think-twice` (sempre).
- `maestri` / `maestri-manager` (orquestrar grok/codex).
- `apk-reverse`, `ghidra-reverse`, `radare2` (backend/engine RE se preciso).
- `dataviz` + Material 3 refs (para a UI dark).
- `hardware-firmware-validation` (se voltar a mexer no device).
