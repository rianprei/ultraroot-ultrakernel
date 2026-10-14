# HANDOFF — UltraRoot (2026-10-13, ~475 mil tokens de contexto)

Para: eu, depois do /compact. Não é texto para o Rian.
Motivo: o contexto chegou perto de 490 mil. Este handoff sai automático no projeto, como a regra manda. O estado detalhado está em `docs/ultraroot/plan.md` (§3 rota, §9 execução). Não repetir aqui o que está lá.

## Linha do tempo (2026-10-13, depois do handoff anterior das 10:04)
- H1 (flags GET_INFO): Codex entregou. Revisão do OpenCode #2 deu APROVADO COM CORREÇÕES. Maestro não aceitou: `readMode()` devolvia `Ok` com flags zeradas quando o driver não respondia. Correção H1F (Codex) com `decodeKernelMode` e sabotagens S1/S2 (revisor reproduziu numa cópia). Achado de KDoc ("(the lake kernel)" sem prova) corrigido (H1KDOC). Confirmação do OpenCode #2: FECHADO. **H1 fechado.**
- H2 (grant/revoke, só host, sem JNI de escrita): Codex entregou (11 testes). Maestro achou 3 defeitos que os testes não cobriam (exceção deixava Confirming; uid repetido; lista por referência). H2F corrigiu (14 testes). Revisão do OpenCode #2: APROVADO COM CORREÇÕES (teste de uid com mesmo nome não discriminava; comentários em inglês). H2R1 corrigiu. Confirmação do OpenCode #2: FECHADO. **H2 fechado.**
- H3 (build ui10): Codex preparou APK release não assinado → zipalign → `out/UltraRoot-own-ui10-aligned.apk` (sha `cc7212b6…`), badging `me.weishu.kernelsu`/40010/`0.2.0-ui10`/`UltraRoot`. Revisão de leitura do OpenCode #2: PRONTO PARA ASSINATURA. **H3 preparo aceito; assinatura pendente do dono.**
- Assinatura (dono): 1ª tentativa falhou por quebra de linha no comando (o shell executou o `.jks` como programa). 2ª tentativa: `keystore password was incorrect`. Nenhum APK assinado foi criado (conferido). O dono perguntou "qual a senha?": NÃO li o `.pass` e não sei a senha. Opções em aberto: (a) digitar de novo; (b) `apksigner --ks-pass file:…superroot-release.pass`, só com "sim, usar o .pass".
- H4 (docs): Codex editou TODOs.md, CAPABILITIES.md, COMPAT_MATRIX.md (11:19–11:22). Escopo conferido: HANDOFF e MAGISK tocados antes (10:02/10:04); plan.md tocado pelo maestro às 11:14:35 (causa da falha R1 que o próprio autor registrou; não é mudança do Codex). **Revisão do OpenCode #2 em andamento** (`[fora do repo] docs/history/reports/log_h4_review_2026-10-13.log`, relatório esperado `docs/history/reports/review_h4_docs_2026-10-13.txt`).
- H8 (escrita nativa SET_APP_PROFILE, SEM chamada na UI): Codex em andamento (`[fora do repo] docs/history/reports/log_h8_2026-10-13.log`; relatório esperado `[fora do repo] ultraroot/core-backend/src/test/native_profile_write_2026-10-13.txt`). Escopo: `core-backend/src/main/cpp/app_profile_fill.h` (novo), `engine.cc`, `EngineNatives.kt`. Sabotagens S8 (uid) e S9 (tamanho da key) no host; static_assert no NDK.

## Estado (ver plan.md §9 para detalhes)
- Fechados: H1, H2, H5, H6. H3 preparo aceito.
- Em andamento: H4 (revisão), H8 (Codex).
- Bloqueados por decisão/aparelho: H3 assinatura (dono); H7 (E1 copy/layout); H8 UI (E2); D0–D7 (cada um com "sim, <passo>"); L3 lake (commit "sim, commitar", push "sim, push"); E3/E4 (Magisk/aceite parcial).
- Arquivos-chave: `ultraroot/` (código); `out/UltraRoot-own-ui10-aligned.apk` (APK pronto para assinar); `[chaves locais do dono, fora do repo]/` (NÃO abrir, NÃO imprimir, NÃO ler o `.pass`).

## Como retomar (ordem)
1. Ler `plan.md` §3 e §9.
2. Ver o relatório da H4 do OpenCode #2 (`review_h4_docs_2026-10-13.txt`). Conferir CADA linha citada com `grep -n` antes de aceitar. Achado verificado → correção pelo Codex #4; sem triagem por severidade.
3. Ver o relatório da H8 do Codex #4 (`h8_profile_write_2026-10-13.txt`). Conferir: sha de `uapi/app_profile.h` igual ao pin; S8 e S9 com a linha da falha; static_assert no NDK (ou NÃO VERIFICADO); `grep -rn setAppProfile` só com a declaração. Depois, revisão do OpenCode #2 em leitura e sabotagens em CÓPIA.
4. Assinatura: se o dono disser "sim, usar o .pass" → rodar apksigner com `--ks-pass file:`. Se disser "digitei": conferir `apksigner verify --print-certs out/UltraRoot-own-ui10.apk` (cert SHA-256 `efb8d49f…`, v2 true), sha do APK assinado registrado em `out/`, e `aapt2 dump badging`. Nunca tentar senhas por conta própria.
5. Antes do H7: trazer E1 (duas opções de copy e layout dos cartões: ativo, sem resposta do kernel, incompatível; e busca/filtro) ao dono.
6. Contexto: ao cruzar 490 mil, avisar o dono para `/compact` e gerar outro handoff, como a regra manda.

## Decisões pendentes do dono
- Senha do keystore: digitar (a) ou `.pass` via `--ks-pass file:` (b). Recomendação: (a), como está no plano.
- E1 (copy e layout), E2 (origem do fd; D2 é leitura no aparelho: "sim, diagnóstico do fd"), E3 (ordem com Magisk), E4 (aceite parcial do grant).
- Lake: commit e push (c8e7812 e o diff não commitado).
- Passos do aparelho D0–D7, cada um com "sim, <passo>".

## Riscos
- Senha do keystore desconhecida: assinatura bloqueada até o dono.
- Cota do Codex: a TUI mostrou "weekly limit: 22% left". Pode esgotar no meio de uma tarefa.
- Revisor OpenCode #2: o modelo mudou durante a sessão (GLM-5.3 Nvidia, antes outro). Cada citação de linha foi conferida; várias estavam erradas. Continuar conferindo.
- E2 (fd) não medido: H8 UI e D4 bloqueados até o D2.
- Coexistência com Magisk (E3): coroação completa depende de SP2 e SP3.
- Shell: comandos do dono com `!` precisam caber numa linha (quebra de linha executa o pedaço seguinte como programa).

## Regras aprendidas nesta sessão
- Notificação de término do background = o `maestri ask` voltou. Não prova que o trabalho está certo. Conferir o disco (hash, XML, mtime) antes de aceitar.
- Relatório do autor e parecer do revisor: os dois erraram linhas. Sempre `grep -n` / `cat -n` antes de citar.
- Sabotagem tem que ser reproduzida por outro agente, numa cópia (nunca no original). Restaurar só com sha256 conferido.
- Para reconstruir a prova de "única mudança", remover o acréscimo e comparar o hash com o anterior.
- zsh: aspas em `--include='*.c'` (o glob sem aspas dá erro).
- Ferramentas do SDK fora do PATH: `~/Android/Sdk/build-tools/37.0.0/` (aapt2, apksigner, zipalign). `keytool` e `java` em `/usr/bin`.
- Prompt para agente: `/prompt-techniques` antes (objetivo, escopo, arquivos, tarefa, todo, prova). Sabotagem obrigatória em código.

## Suggested skills
- `maestri`: orquestrar Codex #4 e OpenCode #2 (inclusive a permissão "Access external directory").
- `prompt-techniques`: antes de qualquer prompt a agente.
- `superpowers:verification-before-completion`: nada de "pronto" sem saída de teste ou hash.
- `superpowers:systematic-debugging`: falha de compilação nativa (H8) ou de assinatura.
- `superpowers:test-driven-development`: H7 e H8.
- `design-review`, `android-compose-design`, `material-3`: H7 e E1 (opções de copy e layout).

## Atualização (≈495 mil tokens, depois da assinatura)
- Dono autorizou: "pode usar o pass" (para o build ui10). Escopo: ESTE build. Próximas assinaturas: perguntar de novo.
- 1ª assinatura (com `--ks-pass file:` e o `.pass`): saiu **v3 apenas**. REJEITADA e renomeada para `out/UltraRoot-own-ui10-v3only-REJEITADO.apk` (não apagada). Motivo: `DIAG_BACKEND_INDISPONIVEL.md:31` e `OWNAPP_INC2.md:92` mostram que o C0.3 checa a assinatura **v2** e os APKs anteriores eram V2-only.
- 2ª assinatura, V2-only (`--v1-signing-enabled false --v2-signing-enabled true --v3-signing-enabled false`): **APK final** `out/UltraRoot-own-ui10.apk`. `apksigner verify`: v1 false, v2 true, v3 false, cert SHA-256 `efb8d49f…`, 1 signer. Badging: `me.weishu.kernelsu` / 40010 / `0.2.0-ui10` / `UltraRoot`.
- SHA-256 do APK final: `b5f5bb369caf64af01c4897d9a1e07…` (registrado em `out/UltraRoot-own-ui10.sha256.txt`). Logs: `[fora do repo] docs/history/prompts/sign_ui10_v2_2026-10-13.log`, `[fora do repo] docs/history/prompts/verify_ui10_v2_2026-10-13.txt`.
- Pendente: revisão de leitura do APK final pelo OpenCode #2 (ainda NÃO despachada). H4 review e H8 seguem.
- Lição: `apksigner sign` sem flags pode sair só com v3. Sempre passar `--v1/--v2/--v3-signing-enabled` explicitamente.
- Próximo passo de aparelho (D1): só com "sim, instalar ui10".
- Revisão de leitura do APK final pelo OpenCode #2 DESPACHADA (docs/history/prompts/prompt_ui10_final_revisao_2026-10-13.txt). Relatório: docs/history/reports/review_ui10_final_2026-10-13.txt. Conferir antes de dizer que o APK está aprovado.
- H4 APROVADO pelo OpenCode #2 (review_h4_docs_2026-10-13.txt, sem achados). APK final conferido pelo OpenCode #2 (review_ui10_final_2026-10-13.txt): PRONTO PARA INSTALAR. Pendente: revisão do H8 (review_h8_2026-10-13.txt, despachada, inclui achado do errno em engine.cc:227).
