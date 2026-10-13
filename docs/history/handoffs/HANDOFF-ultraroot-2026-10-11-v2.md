# HANDOFF — UltraRoot (2026-10-11 v2)

Responder **pt-BR**. Projeto base: `[fora do repo]`. Não duplico o que já está nos artefatos — referencio por path.

## O que é
UltraRoot = plataforma/manager de root unificado (app PRÓPRIO) sobre o engine KernelSU-integrado, pro POCO C75 (lake, GKI 6.6.89). Fundir as FUNÇÕES dos managers (Magisk/KSU/APatch/SukiSU) via control-plane + adapters — NÃO fundir os mecanismos num runtime.

## Fontes de verdade (ler primeiro)
- Memória: `project_superroot_state.md` (estado corrente), `project_superkernel_vision.md` (overclock→Performance Engine; veredito: DVFS do lake bloqueia OC no driver, margem UNKNOWN), `project_lake_kernel_state.md`.
- Spec FINAL rev.7: `[fora do repo]/docs/superpowers/specs/2026-10-11-ultraroot-design.md` (44 capacidades REAIS obrigatórias; honesto, zero inventado/evasão; cross-revisado).
- Mapa: `docs/ultraroot/CAPABILITIES.md` (44 OBRIG, 14 colunas, status IMPL×VALID). Também: COMPAT_MATRIX.md, RECOVERY_SUITE.md, MODULE_PARITY.md, MODULE_MIGRATION_lake.md, HW_PERF_MATRIX_lake.md, accept_ksu_root.sh, DEVICE_PLAN_SP1.md, guard_hashes_sp1.txt.
- Plano SP1: `[fora do repo]/docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`.

## Estado do DEVICE agora (POCO C75)
- Kernel **C0.3** flashado+bootado (`[artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img`, sha 2977e1dc) — autoriza nosso manager pelo cert fixo efb8d49f.
- **Magisk + 8 módulos INTACTOS** (hosts, yt-noads, poco_perfgov, termuxadb_rootport = nativo-KSU; zygisk_vector, bc-poc = precisam Zygisk/SP2; always-trust-user-certs, tabs-ca-inject = precisam adapter/SP3). Nenhum incompatível.
- **manager-grant = NOT_TESTED** (o `su` ainda roteia pro Magisk; NÃO foi provado que o KSU concede root via nosso app). Honesto: não marcar validado sem a prova.
- App **UltraRoot reskin inc2b** (KSU manager com label UltraRoot) instalado: `me.weishu.kernelsu` versionCode **32673**, cert efb8d49f.

## Estado do APP PRÓPRIO (inc2 ACEITO)
Scaffold novo em `ultraroot/` (Compose, M3 dark, fundo #0B0C0E, primary #D6B25E, ModalNavigationDrawer próprio com 6 seções: Root Access, Module Center, System Toolkit, Kernel&Performance, Boot&Recovery, Extensions Hub). Backend ISOLADO `:core-backend` (namespace `dev.superroot.core`, libultraroot.so: GET_INFO/NEW_GET_ALLOW_LIST/GET_APP_PROFILE; cabeçalhos UAPI copiados; zero tela KSU). Kernel&Performance lê KsuBackend.readCapability() real; as outras 5 dizem "indisponível".

**Artefato canônico (host-completo, cross-review ACEITO):**
- APK: `ultraroot/out/UltraRoot-own-inc2-fix.apk`
- sha256: `a697228a22241b8edee89eaa1469eb12e80e7a50f302996adccc35af6165e813`
- Assinatura: **V2-only**, cert `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9` (mesma do C0.3)
- Package: `me.weishu.kernelsu` (applicationId inalterado — decisão (a): mantém autorização do C0.3 sem reflash; namespace do código = `dev.superroot.ultraroot`)
- versionCode: **2**, versionName: `0.1.0-inc2`, label: `UltraRoot`, activity: `dev.superroot.ultraroot.MainActivity`
- Tamanho: **23.3 MB** (era 32.6 MB; `ANDROID_STL=c++_static` eliminou `libc++_shared.so`, `libultraroot.so` stripped 8.7 KB, 5 símbolos JNI preservados)
- Testes host: **8/8** (RootAccessPresenterTest 5/5 + KsuBackendHostTest 3/3) — inclui regressão crítica: perfil null → "perfil indisponível" (NÃO allowSu=false), backend indisponível → "backend indisponível" sem crash.
- UI: grep limpo — nenhuma identidade KSU visível (string `kp_kernel_version` = "Versão do root no kernel: %1$d").

**Doc:** `docs/ultraroot/OWNAPP_INC2.md` (atualizado). O `UltraRoot-own-inc2.apk` anterior (sha b2b0d4bc) foi REJEITADO por vazar "Versão KSU" na UI e permanece no disco como histórico.

## GATE DEVICE — PRÓXIMO PASSO (só sob "sim, <passo>" presencial)

O aparelho tem `me.weishu.kernelsu` **vc 32673** (o reskin inc2b, mesmo cert efb8d49f). Nosso app é **vc 2**. Instalar vc2 sobre vc32673 é **downgrade** → `adb install -r` falha.

Duas saídas:
- **(a) rápido:** `adb install -r -d` (permite downgrade, mesmo cert) — resolve na hora, instala o app próprio no lugar do reskin.
- **(b) correto:** bump versionCode > 32673 (ex. 40000) num rebuild + re-sign + re-review (cross-review focado), depois `adb install -r` (upgrade limpo, substitui o reskin direito). Custa ~10 min de host + review.

**DECIDIDO 2026-10-11** ("sim, pode fazer os 2"): (a) testado → `INSTALL_FAILED_VERSION_DOWNGRADE` (device `user`, app não-debuggable: `-d` não vale). (b) executado → versionCode **40000**, sha256 `7a3ae0b1a287b39b774ab5c8d99f37e6af7d7fad10ef3c859e4edb5a4593dc5f` (`out/UltraRoot-own-inc2-vc40000.apk`, V2-only, cert efb8d49f), `adb install -r` = Success, device confirmado vc40000. Backup do reskin: `[fora do repo]/ultraroot/reskin-inc2b-vc32673.apk` (sha256 3429e23af62d1a2482e1cf0ee81177973f9e683ac176213c17fb395a0a13cc11). Re-review Codex #4 do vc40000: **ACEITO** (`docs/ultraroot/REVIEW_vc40000_codex4.md`; fora META-INF só `AndroidManifest.xml` difere, versionCode 2→40000; sabotagem detectada; fonte = UNKNOWN, sem snapshot aceito). Manager-grant ainda NOT_TESTED.

Se instalado (qualquer caminho), a sequência device:
1. Abrir o app próprio (ícone UltraRoot, label UltraRoot, activity dev.superroot.ultraroot.MainActivity).
2. Ir em **Root Access** → conceder root a app-teste (ex. Termux).
3. Rodar `docs/ultraroot/accept_ksu_root.sh` → aceite = `uid=0` + evidência KSU (ksud ou `/data/adb/ksu`, UAPI 5) + contexto sem magisk.

Se ACEITE: KSU concede root. Próximo: inc3 (grant/revoke UI + migrar 4 módulos nativos). Coroação (KSU vira dono do `su`, DESLIGAR Magisk) SÓ depois de migrar os 8 módulos.

## Agentes e regras
- Via **maestri**: `grok` (build/RE, confiável), `Codex #4` (análise/review, "braço direito"). **Cross-review obrigatório** (autor ≠ revisor). Delegação via maestri = decisão do orquestrador, sem pedir permissão. Harness Agent (subagentes): proibido, salvo justificativa clara E grok + Codex #4 terem falhado; Haiku 5.5 no máximo. **Claude Code #2 = NÃO confiável, manter fora.** Scratch de agente em disco (`[artefato local, fora do repo]/<agente>/`), NUNCA /tmp (tmpfs).
- Device SÓ sob "sim, <passo>" presencial. Protocolo: Z0 (detectar slot ativo; lake; is-userspace≠yes; partição 67108864; bateria≥60%) → T-1 (flash backup, prova caminho) → flash → reboot via `kernel/lake/tools/fastboot_guard.sh` (allowlist `guard_hashes_sp1.txt` = backup 3890fb96 + C0.3 2977e1dc). **Boot-safety: slot B, NUNCA slot A** (A=OS3.0.20.0 antigo). Backup: `[fora do repo]/mods/lake-kernel/backup-2026-10-05/`. Rollback = `fastboot_guard.sh rollback <backup>` (reativa só set_active b).
- Chave de assinatura FIXA (SEGREDO): `[chaves locais do dono, fora do repo]/superroot-release.jks` (cert público efb8d49f). Nunca commitar/publicar.
- **Legítimo**: sem evasão/ocultação "indetectável" (impossível vs atestação de HW; fora do escopo). Toda alegação com evidência (path/hash/cmd/data) senão UNKNOWN; compila≠validado. Nunca destruir dados. SELinux fica Permissive.
- Disco: 2º SSD fundido no btrfs do CachyOS (nvme0n1→/; ~488G livres; boot multi-device via hook systemd + initramfs regenerado). NUNCA tocar nvme1n1 (é o /).
- **Handoffs salvam no projeto, nunca em /tmp** (regra do dono 2026-10-10).

## Suggested skills
- `maestri` / `maestri-manager` (orquestrar grok/codex).
- `superpowers:writing-plans` + `superpowers:subagent-driven-development` (para inc3/device).
- `fable-method`, `think-twice` (sempre).
- `apk-reverse` (se precisar auditar backend no device).
- `hardware-firmware-validation` (se voltar a mexer no kernel/device).

---

**Para retomar:** leia este handoff, decida (a) ou (b) pro versionCode, dê "sim, <passo>" pro device, e prossiga do install do `UltraRoot-own-inc2-fix.apk` + validação do `manager-grant`.
