# HANDOFF — UltraRoot (2026-10-13)

Para: eu, depois do /compact. Não é texto para o Rian.
Motivo: passei de 490.000 tokens de contexto (606.000 agora) e não avisei no momento certo. O aviso é dado agora, e este handoff saiu automático no projeto, como a regra manda.

## Linha do tempo
- 2026-10-10: lake T-1 e T3 gravados e PASS no aparelho. Kernel custom rodando (memória do projeto lake).
- 2026-10-11: spec rev.7 aprovado ("sim pros 3"). Plano SP1 escrito em `[fora do repo] docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`. APK vc40000 instalado no aparelho (`docs/history/evidence/2026-10-11_install_vc40000.txt`).
- 2026-10-12 manhã: diagnóstico de "backend indisponível". A causa era a instalação incremental (incfs). `pm install -r` a partir de /data/local/tmp: manager coroado (uid 10296) e allowlist lida (vazia).
- 2026-10-12: inventário Magisk/KSU fechado pelo dono. Handoff v4 gerado.
- 2026-10-12 tarde: estado honesto (`BackendState`) implementado. Revisão do Claude #2: REPROVADO, corrigido. Busca e filtro no presenter: revisão do Claude #2 e do Codex #4, corrigido. `OWNAPP_INC1.md:34` corrigido (o `.so` só procura o fd).
- 2026-10-13: maestri reiniciado. Testes do host: core-backend 10/10, app 26/26. `docs/ultraroot/plan.md` criado (rota H, L, D, E). Modelo da sessão trocado para Haiku 5.5 via /model, com effort max na sessão.
- 2026-10-13: **o dono substituiu o Claude Code #2 pelo OpenCode #2.** O revisor passa a ser o OpenCode #2. Confiabilidade dele desconhecida: cada prova é conferida.

## Estado
Ver `docs/ultraroot/plan.md` §2 (verificado em 2026-10-13). Não repetir aqui.

## Em andamento (no momento deste handoff)
- H1, dono Codex #4: expor os flags do GET_INFO (LKM, MANAGER, LATE_LOAD). Prompt: `docs/history/prompts/prompt_h1_flags_2026-10-13.txt`. Relatório esperado: `docs/history/reports/h1_flags_2026-10-13.txt`.
- L1, revisor OpenCode #2: revisão do diff não commitado do lake. Prompt: `docs/history/prompts/prompt_l1_lake_2026-10-13.txt`. Relatório esperado: `docs/history/reports/review_lake_guard_2026-10-13.txt`.
- Se algum relatório não existir, consultar `maestri check "<agente>"` antes de despachar outro.

## Como retomar
1. Ler `plan.md` §3 (rota) e §7 (próximos passos).
2. Conferir os relatórios de H1 e L1. Conferir cada prova no arquivo citado antes de aceitar, principalmente as do OpenCode #2.
3. Se o OpenCode #2 travar em "Permission required … Access external directory": `maestri check "OpenCode #2"`. Se aparecer "Allow once" ou "Allow always", responder com `maestri ask "OpenCode #2" --raw` enviando seta direita, Enter e Enter (confirmação), conforme `reference_opencode_permission_prompt` na memória.
4. Aparelho: nada sem "sim, <passo>", nem leitura.

## Decisões pendentes do dono
- E1 a E6 em `plan.md` §3, fase E.

## Riscos
- `plan.md` §6.
- R8 (novo): OpenCode #2 substitui o Claude Code #2. Confiabilidade e comportamento de permissões desconhecidos.

## Suggested skills
- `maestri`: orquestrar Codex #4 e OpenCode #2, incluindo o prompt de permissão do OpenCode.
- `prompt-techniques`: antes de qualquer prompt a agente.
- `superpowers:test-driven-development`: H1, H2, H6, H7, H8 (testes antes da implementação).
- `superpowers:verification-before-completion`: nada de "pronto" sem saída de teste.
- `superpowers:systematic-debugging`: falha de compilação nativa ou de aparelho.
- `design-review`, `android-compose-design`, `material-3`: H7 e D1.

## Riscos desta sessão
- Passou de 490k sem aviso. Aviso dado nesta rodada.
- Saídas de agentes podem chegar truncadas. O que faltar fica registrado como NÃO VERIFICADO, nunca como aprovado.

## Atualização (2026-10-13, depois da escrita deste handoff)

- L1 concluída e verificada pelo maestro. O selftest do lake foi corrigido: o R1 agora exige a ordem flash boot_b antes de set_active b. Ver `plan.md` §9.
- H5 despachado ao OpenCode #2 (tarefa b9sqcy03t). H1 segue no Codex #4 (tarefa bp4aiaelh).
- Contexto em cerca de 691 mil tokens. /compact continua pendente; o aviso já foi dado.

## Atualização 2 (2026-10-13)

- L1 e L2 concluídas (ver `plan.md` §9). Selftest do lake corrigido e passando.
- H5 concluído: linha 85 do inventário ajustada em "fluxos do Home" para os diálogos reais de HomeScreen.kt. Antes do ajuste, o texto foi guardado em `[fora do repo] docs/history/prompts/MAGISK_KSU_UI_INVENTORY.antes_H5_2026-10-13.md`. O veredito de cabeçalho do REVIEW existe em `REVIEW_MAGISK_KSU_INVENTORY_haiku55.md:4`. A reaprovação do Claude na rodada 6 (P1 e P2) segue NÃO VERIFICADA.
- H6 (leitura, Tasks 3, 4 e 6 do SP1) despachado ao OpenCode #2 (tarefa beq3zoeth). Relatório esperado em `docs/history/reports/review_h6_sp1_tasks_2026-10-13.txt`.
- H1 segue no Codex #4 (tarefa bp4aiaelh), perto do fim. Relatório esperado em `docs/history/reports/h1_flags_2026-10-13.txt`.
- Contexto em cerca de 724 mil tokens. /compact continua pendente.

## Atualização 3 (2026-10-13)

- H1 concluído pelo Codex #4 e verificado pelo maestro (ver `plan.md` §9). Relatório em `docs/history/reports/h1_flags_2026-10-13.txt`.
- Pendente: revisão cruzada de H1 pelo OpenCode #2, que está ocupado com H6 (tarefa beq3zoeth). Depois do H6, despachar a revisão de H1 ao OpenCode #2.
- H6 (leitura de Tasks 3, 4 e 6) ainda em andamento no OpenCode #2.
- Próximo passo da rota: H2, inc3 no host com fakes, pelo Codex #4.
- Contexto em cerca de 734 mil tokens. /compact pendente. Rode /compact antes de seguir.

## Atualização 4 (2026-10-13)

- H6 concluído. Os artefatos das Tasks 3, 4 e 6 existem. `test_accept_ksu.sh` e `test_module_props.sh` passaram em host (rc 0, PASS). O revisor OpenCode #2 errou algumas linhas citadas; corrigidas em `plan.md` §9.
- H1 (flags do GET_INFO, Codex #4) verificado pelo maestro. A revisão cruzada foi despachada ao OpenCode #2 com `&` no shell, sem notificação. Conferir `[fora do repo] docs/history/reports/log_h1_revisao_2026-10-13.log` e `docs/history/reports/review_h1_flags_2026-10-13.txt`.
- Próximo passo da rota depois da revisão de H1: H2 (inc3 no host com fakes, Codex #4).
- Contexto em cerca de 757 mil tokens. /compact pendente. Rode /compact antes de seguir.
