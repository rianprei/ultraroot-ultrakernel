# HANDOFF — UltraRoot, fim da sessão de 2026-10-13 (~540 mil tokens)

Para: o próximo agente (eu, depois de /compact ou de uma sessão nova). Não é texto para o Rian.
Este arquivo substitui `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-ctx490k.md`. Não copia o que já está em outros artefatos: aponta para eles.

## Como usar
- Rota e status detalhado: `docs/ultraroot/plan.md` (§3 rota, §9 execução, §1 regras fixas).
- Checklist do produto: `docs/ultraroot/TODOs.md` (§1 é o SP1).
- Handoffs anteriores: `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13.md` (até 10:04) e `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-ctx490k.md` (até 11:45).

## Linha do tempo de 2026-10-13
- Antes desta data (resumo, já em plan.md §2): lake T-1 e T3 PASS em 10-10; spec rev.7 aprovado em 10-11; "backend indisponível" resolvido em 10-12 com `pm install -r` (incfs); manager coroado uid 10296 (dado de 10-12, não medido hoje).
- 10:02–10:04: inventário Magisk/KSU, linha 85 (H5); handoff anterior.
- H1 (flags GET_INFO): Codex entregou → OpenCode #2 APROVADO COM CORREÇÕES → maestro não aceitou (`readMode` devolvia Ok com zeros quando o driver não respondia; citações erradas) → H1F (Codex, `decodeKernelMode`, sabotagens S1/S2 reproduzidas pelo revisor numa cópia) → achado do KDoc "(the lake kernel)" corrigido (H1KDOC) → confirmação do OpenCode: FECHADO.
- H2 (grant/revoke, só host, sem JNI de escrita): Codex (11 testes) → maestro achou 3 defeitos que os testes não cobriam (exceção deixava Confirming; uid repetido; lista por referência) → H2F (14 testes) → OpenCode: APROVADO COM CORREÇÕES (teste de uid com mesmo nome; comentários em inglês) → H2R1 → confirmação do OpenCode, com S7 reproduzida: FECHADO (11:0x).
- H3 (build ui10): Codex prepara APK release não assinado, zipalign, badging (40010, 0.2.0-ui10, UltraRoot) → OpenCode: PRONTO PARA ASSINATURA (11:13).
- Assinatura do build ui10: dono disse "sim, assinar" → 1ª tentativa falhou (comando quebrado em duas linhas; o shell executou o .jks) → 2ª: senha do keystore incorreta (digitada pelo dono) → dono perguntou "qual a senha?" e eu não li o `.pass` nem informei a senha → dono: "pode usar o pass" (autorização só para este build) → 1ª assinatura com `--ks-pass file:` saiu **v3 apenas**: REJEITADA e renomeada para `out/UltraRoot-own-ui10-v3only-REJEITADO.apk` (motivo: `docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md:31` diz que o C0.3 checa a assinatura v2; `OWNAPP_INC2.md:92` mostra os APKs anteriores V2-only) → 2ª assinatura V2-only: `out/UltraRoot-own-ui10.apk` (ver Artefatos).
- H4 (docs): Codex atualizou TODOs.md, CAPABILITIES.md e COMPAT_MATRIX.md. OpenCode: APROVADO sem achados (11:39).
- APK final: OpenCode #2 conferiu em leitura: PRONTO PARA INSTALAR (11:43).
- H8 (escrita nativa `SET_APP_PROFILE`, SEM chamada na UI): Codex entregou (helper `app_profile_fill.h`, JNI `setAppProfile` em `engine.cc`, declaração em `EngineNatives.kt`; 8 casos de host; S8 e S9 derrubam os casos certos; `.so` de debug regenerado às 11:40:39). Revisão do OpenCode #2 despachada e ainda não voltou. Achado do maestro, a confirmar: `engine.cc:227` devolve o `-1` do ioctl e perde o `errno` (o D2 precisa dele).
- Estado do aparelho nesta sessão: NENHUMA operação (sem adb, sem fastboot, sem leitura). Tudo é host.

## Estado (detalhe em plan.md §9)
- Fechados: H1, H2, H3 (preparo e assinatura V2), H4, H5, H6, L1, L2.
- Em andamento: revisão do H8 (OpenCode #2).
- Pronto e esperando o dono: instalar o ui10 (D1).

## Bloqueios e decisões do dono
- D1: "sim, instalar ui10". O APK final está pronto (ver Artefatos). Instalar com `pm install -r` a partir de `/data/local/tmp` (incfs, plan §3 D1).
- E1 (copy e layout dos cartões: ativo, sem resposta do kernel, incompatível; busca e filtro): bloqueia H7.
- E2 (origem do fd `[ksu_driver]`): bloqueia a chamada de `setAppProfile` na UI e o D4. O D2 é leitura no aparelho: "sim, diagnóstico do fd".
- E3 (ordem com o Magisk): coroação completa depende de SP2 e SP3.
- E4 (aceite parcial do grant): "su via KSU" não está provado.
- E5 e E6: propostas no plan §3, sem decisão.
- Lake: commit "sim, commitar"; push "sim, push" (revisão brutal antes). Estado: c8e7812 local, à frente de origin; dois arquivos modificados sem commit: `tools/fastboot_guard.sh` e `tools/selftest_fastboot_guard.sh`.
- Passos de aparelho D0–D7: cada um só com "sim, <passo>" (inclusive leitura).
- Assinatura: a autorização do `.pass` valeu só para o build ui10. Próxima assinatura: perguntar de novo.

## Artefatos-chave (caminhos; não copiar conteúdo)
- APK final: `ultraroot/out/UltraRoot-own-ui10.apk`. SHA-256 em `out/UltraRoot-own-ui10.sha256.txt`. Esquema V2 (v1 false, v2 true, v3 false), 1 signer, cert SHA-256 `efb8d49f…`.
- APK REJEITADO: `out/UltraRoot-own-ui10-v3only-REJEITADO.apk`. NÃO instalar.
- Alinhado de origem (não assinado): `out/UltraRoot-own-ui10-aligned.apk`.
- Logs de assinatura: `docs/history/prompts/sign_ui10_v2_2026-10-13.log` e `verify_ui10_v2_2026-10-13.txt`.
- Revisões (OpenCode #2): `docs/history/reports/review_{h1f,h1kdoc_confirma,h2,h2r1_confirma,h3_badging,h4_docs,ui10_final}_2026-10-13.txt`. H8 pendente: `review_h8_2026-10-13.txt`.
- Relatórios do Codex #4: `docs/history/reports/{h1_fix_readmode,h1_kdoc_fix,h2_grant_flow,h2_fix,h2_review_fix,h3_build_ui10,h4_docs,h8_profile_write}_2026-10-13.txt`.
- Prompts enviados: `docs/history/prompts/prompt_*_2026-10-13.txt`.

## Chaves: não abrir, não imprimir, não ler
- `[chaves locais do dono, fora do repo]/` tem o keystore (`.jks`), o certificado público (`.der`) e o arquivo de senha (`.pass`). Nenhum agente abre esses arquivos. A senha do keystore não está em nenhum artefato nem neste handoff.

## Como retomar (ordem)
1. Ler `plan.md` §9 e o SP1 em `TODOs.md` §1. Não repetir o que está lá.
2. Ver `docs/history/reports/review_h8_2026-10-13.txt`. Se o achado do `errno` for confirmado, pedir ao Codex #4 a correção (retorno `-errno`, com `cerrno` incluído), sabotagem no host e revisão do diff. Conferir cada linha citada com `grep -n` antes de aceitar.
3. Esperar o dono. Com "sim, instalar ui10": D1, com `pm install -r` a partir de `/data/local/tmp`, sha no aparelho e captura de tela.
4. Com "sim, diagnóstico do fd": D2 (leitura). O resultado decide E2.
5. H7 (ligar classifyBackend, KsuMode, busca e filtro à tela): só depois de E1. Antes, trazer duas opções de copy e layout ao dono.
6. Lake: só com "sim, commitar" e "sim, push".
7. Ao cruzar 490 mil tokens de contexto: avisar o dono para `/compact` e gerar outro handoff (regra do dono).

## Regras de processo (detalhes em plan.md §1)
- Autor ≠ revisor. Achado de revisão: conferir a citação com `grep -n` ou `cat -n` (revisores e autores erraram linhas várias vezes). Sem triagem por severidade: todo achado verificado vira correção.
- O maestro não escreve código: execução pelo Codex #4; revisão e sabotagem de conferência pelo OpenCode #2 (sabotagem só em cópia). Nada de subagentes do harness.
- Prompt a agente: `/prompt-techniques` antes (objetivo, escopo, arquivos, tarefa, todo, prova). Código sempre com prova de sabotagem.
- Aparelho: nada sem "sim, <passo>". Boot é o slot b, nunca o a.
- Assinatura: comando em UMA linha. Sempre passar `--v1-signing-enabled false --v2-signing-enabled true --v3-signing-enabled false`, porque o padrão pode sair só com v3.
- Dispatch em background: a notificação só diz que o `maestri ask` voltou. Conferir o disco (hash, XML, mtime) antes de aceitar.
- zsh: aspas em `grep --include='*.c'`. Ferramentas do SDK fora do PATH: `~/Android/Sdk/build-tools/37.0.0/` (aapt2, apksigner, zipalign). `keytool` e `java` em `/usr/bin`.

## Riscos
- E2 não medido: a escrita na UI e o D4 ficam bloqueados até o D2.
- Magisk coexistindo (E3): a coroação completa depende de SP2 e SP3.
- Grant no aparelho não provado (E4).
- Cota do Codex: a TUI mostrou "weekly limit: 22% left". Pode acabar no meio de uma tarefa.
- Revisor OpenCode #2: o modelo mudou durante a sessão (GLM-5.3 Nvidia). Erros de citação frequentes. Conferir cada linha.
- Lake com dois arquivos modificados e c8e7812 não enviado.
- Contexto: esta sessão passou de 540 mil de 980 mil. Compactar.

## Suggested skills
- `maestri`: orquestrar Codex #4 e OpenCode #2 (inclusive o prompt de permissão "Access external directory").
- `prompt-techniques`: antes de qualquer prompt a agente.
- `superpowers:verification-before-completion`: nada de "pronto" sem saída de teste ou hash conferido.
- `superpowers:systematic-debugging`: falha de compilação nativa, de assinatura ou de instalação.
- `superpowers:test-driven-development`: H7 e qualquer código novo.
- `design-review`, `android-compose-design`, `material-3`: H7 e E1.

## Dados sensíveis
- Nenhuma senha, chave privada ou conteúdo do `.pass` está neste arquivo. Os caminhos de chave são referências, não valores.
- H8 (escrita nativa): OpenCode #2 APROVADO COM CORREÇÕES (errno em engine.cc:227). Correção H8R1 despachada ao Codex #4 (ultraroot/core-backend/src/test/nativer1_errno_2026-10-13.txt). Pendente: conferir o relatório e a revisão de leitura da correção.
- H8R1/H8R2: ioctl_status feito; teste EPERM não discriminava (EPERM=1=-(-1)); troca por EBADF (9) em andamento (Codex #4, ultraroot/core-backend/src/test/nativer2_ebadf_2026-10-13.txt). Pendente: conferir a sabotagem S10 e a revisão de leitura do OpenCode #2.
