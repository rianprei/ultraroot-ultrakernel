# HANDOFF — UltraRoot (2026-10-12 v4)

Modo: documento. Salvo no projeto (regra do dono: nunca em /tmp). Responder em pt-BR.
Antecedentes: `docs/history/handoffs/HANDOFF-ultraroot-2026-10-12-v3.md` (estado do device) e `docs/history/handoffs/HANDOFF-ultraroot-2026-10-11-v2.md` (protocolo de flash e regras).

## Foco da próxima sessão

Código real do UltraRoot, não só UI. Pedido do dono em 2026-10-12: "bora pro código real logo do UltraRoot e não ficar só em UI". Ordem:

1. Backend (`core-backend`): estado honesto do backend, com ativo, ausente ou incompatível, motivo, versão e modo. Testes no host.
2. App (lógica): busca e filtro no Root Access, sobre a lista de `AllowEntry`. Testes no host.
3. Inc3 no host: conceder e revogar por app, com confirmação e estado por app, usando fakes. Nada toca o kernel antes do gate "sim, grant teste".

## Estado verificado nesta sessão

### Código

- Projeto: `ultraroot` (não é repositório git). Módulos: `app`, `core-backend`, `out/` (APKs), `assets/` (ícone-fonte).
- `core-backend/src/main/java/dev/superroot/core/KsuBackend.kt` (116 linhas): `KsuCapability`, `AllowGrant`; `sealed KsuRead` (`Ok`, `Unavailable(reason)`); `sealed AllowEntry` (`Confirmed(grant)`, `Unavailable(uid, reason)`); `object KsuBackend` com `readCapability()`, `readAllowList()`, `interpretProfile()`; constantes `UNAVAILABLE` e `PROFILE_UNAVAILABLE`.
- `EngineNatives.kt` (26 linhas): externals `kernelVersion`, `kernelUapi`, `managerUapi`, `allowListUids`, `readProfile`.
- Nativo: `core-backend/src/main/cpp/engine.cc` e `CMakeLists.txt`. UAPI copiada em `src/main/cpp/uapi/` (app_profile.h, feature.h, ksu.h, selinux.h, sulog.h, supercall.h). `engine.cc` NÃO foi lido nesta sessão. Ler antes de mudar o JNI.
- Testes do backend: `core-backend/src/test/.../KsuBackendHostTest.kt`, 3 testes (sem lib dá indisponível; perfil falho não vira "sem root").
- App: `app/src/main/java/dev/superroot/ultraroot/ui/RootAccessState.kt` (46 linhas): `ROOT_EMPTY`, `ROOT_PROFILE_UNAVAILABLE`, `UNKNOWN_PACKAGE = "—"`, `RootRow`, `sealed RootAccessUi` (`Unavailable`, `Empty`, `Listed`) e `presentRootAccess(read)`. Testes: `RootAccessPresenterTest.kt` (5), `ContrastTest`, `StringsIdentityTest`. Registro anterior: 9/9 em ui9; não reexecutado nesta sessão.
- Versão no código: versionCode 40009, versionName `0.2.0-ui9` (`app/build.gradle.kts:21-22`). applicationId `me.weishu.kernelsu`: NÃO mudar, é a autorização do C0.3.
- Não existe ainda: comando de SET (grant/revoke) no backend, busca e filtro, e estado "incompatível" como tipo próprio.

### Device (documentos; não consultado nesta sessão)

- Conexão, serial e IP: ver v3. Kernel `6.6.89-android15-8-4k`.
- Instalado: ui8 (vc40008), sha `5861465c…`. ui9 (vc40009) construído, NÃO instalado, sha `671d0c9c…`.
- Manager coroado (uid 10296). Allowlist vazia. Grant NOT_TESTED. Accept reprova com contexto `magisk`.
- C0.3: sha `2977e1dc…` (conferido nesta sessão). Gate host: 557 módulos (370 únicos), 2309 símbolos, 0 mismatches. Status no device UNKNOWN: TODOs marcam `[~]`, sem log de boot ligado ao hash.

### Inventário Magisk/KSU: fechado pelo dono em 2026-10-12

- `docs/ultraroot/MAGISK_KSU_UI_INVENTORY.md` (124 linhas; 306 citações existem).
- Relatório das revisões: `docs/ultraroot/REVIEW_MAGISK_KSU_INVENTORY_haiku55.md`.
- Decisões que valem para o código:
  - Notificação por app: classificação (c), sem equivalente verificado no pin.
  - Logging: classificação (b), só SU Log global.
  - Mount namespace por perfil: (c), gate, fora da lista recomendável.
  - Umount por perfil e DenyList: excluídos, por ocultação.
  - "Isolated namespace": string do Magisk sem uso, fora.
- Pendência de registro: a última reaprovação do Claude Code #2 não mostrou o veredito de cabeçalho nem P1/P2 na saída salva. As duas correções finais da linha 85 não foram reconferidas pelo revisor.

## Regras que não mudam

- Device SÓ com "sim, <passo>" explícito. Leitura no device também conta.
- Boot: slot B, nunca A. No POCO, `boot_b` é o ativo; A é o OS antigo, inbootável.
- Legítimo: sem ocultação indetectável e sem bypass de atestação. Nada de DenyList, hide ou umount por perfil.
- Toda alegação com prova (arquivo:linha, hash, comando ou data). Sem prova, UNKNOWN. Compila não é validado.
- Cross-review: autor ≠ revisor.
- Subagentes Haiku: só com autorização por uso e teto de tokens no prompt. O dono pediu o Claude Code #2 no lugar do Haiku para revisões.
- Scratch de agente em `[artefato local, fora do repo]/<agente>/`, nunca `/tmp`.
- Chave de assinatura em `[chaves locais do dono, fora do repo]/`: NÃO abrir nem imprimir (há senha). Cert público `efb8d49f…`.

## Agentes (maestri list: grok, Codex #4, Claude Code #2)

- grok: build e RE. Quota pendente no último registro (TODOs:36). Verificar antes de delegar.
- Codex #4: análise, dossiês e edição de documentos. Autor do inventário. Prompts com /prompt-techniques (objetivo, escopo, arquivos, tarefa, todo, prova).
- Claude Code #2: revisor, sem skills, com teto de chamadas no prompt. Não confiável no histórico: conferir cada prova. Contexto alto (cerca de 474 mil em 2026-10-12).

## Próximos passos (em ordem)

1. Código, sem device (host):
   - a. Backend: classificar `readCapability` em ativo, ausente ou incompatível (`kernelUapi ≠ managerUapi`), com motivo, versão e modo. Antes, ler `engine.cc`.
   - b. Presenter: busca e filtro do Root Access (texto por package e UID; filtro de usuário ou sistema só se a fonte dos dados existir; concedido ou não). Testes primeiro (TDD).
   - c. Grant e revoke: interface no backend com fakes; confirmação e estado por app na lógica. Nenhuma chamada real ao kernel.
   - Cada incremento passa por revisão de outro agente.
2. Device, só com gate:
   - "sim, instalar ui9": `pm install -r`, sem incremental. Depois captura e design-review.
   - "sim, grant teste": Termux como app de teste. Depois `accept_ksu_root.sh` com `ACCEPT_KSU_ALLOW_ADB=1`.
3. Coroação: só depois de migrar os 8 módulos do Magisk (HANDOFF v2).
   - 4 nativos KSU: hosts, yt-noads, poco_perfgov, termuxadb_rootport.
   - 2 com Zygisk (SP2): zygisk_vector, bc-poc.
   - 2 com adapter (SP3): always-trust-user-certs, tabs-ca-inject.

## Pendências de decisão do dono

- Status do C0.3 no device: aceitar a alegação de boot do HANDOFF v2, ou manter UNKNOWN até haver log ligado ao hash.
- Revisão cruzada dos ajustes a–c do flash (`fastboot_guard`): autor Codex #4; revisor grok pendente por quota.
- Lake: commit c8e7812 não enviado. `[fora do repo] tools/fastboot_guard.sh` e `[fora do repo] tools/selftest_fastboot_guard.sh` modificados e sem commit. Push só com "sim".

## Referências (não duplicadas aqui)

- Spec rev.7, APROVADO em 2026-10-11: `[fora do repo] docs/superpowers/specs/2026-10-11-ultraroot-design.md`.
- Plano SP1: `[fora do repo] docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`.
- Capacidades (44 OBRIG, 8 OBRIG-PROPOSTO): `docs/ultraroot/CAPABILITIES.md`. Compatibilidade: `COMPAT_MATRIX.md`.
- Diagnóstico do "backend indisponível" (causa: instalação incremental do adb; resolvida): `docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md`.
- Aceite: `docs/ultraroot/accept_ksu_root.sh` (exige `ACCEPT_KSU_ALLOW_ADB=1`).
- Dossiê do Codex: `docs/ultraroot/CONTEXTO_codex4_2026-10-12.md`.

## Suggested skills

- `maestri`: orquestrar grok, Codex #4 e Claude Code #2.
- `prompt-techniques`: antes de qualquer prompt a agente.
- `superpowers:test-driven-development`: backend e presenter com testes no host.
- `superpowers:writing-plans`: se o inc3 crescer.
- `superpowers:verification-before-completion`: nada de "pronto" sem saída de teste.
- `superpowers:systematic-debugging`: falhas de aceite ou de grant.
- `material-3`, `android-compose-design`, `design-review`: só ao voltar à UI (captura do ui9).

## Riscos desta sessão

- Passou dos 50% de contexto (cerca de 490 mil tokens) sem o aviso combinado. Corrigido ao gerar este handoff.
- Saídas do Claude Code #2 chegaram truncadas no começo. Quando faltar cabeçalho ou veredito, registrar como não visto.
