# HANDOFF — UltraRoot, 2026-10-13, passagem de 490 mil tokens (parte b)

Para: o próximo agente (eu, depois de /compact ou de uma sessão nova). Não é texto para o Rian.
Substitui como ponto de retomada apenas o bloco "Repo público" abaixo; o restante continua em `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-v3.md`.

## Linha do tempo desta parte
- H8 fechado: conferência independente do OpenCode #2 (`docs/history/reports/review_h8r_2026-10-13.txt`): APROVADO, "H8 fecha". Achado não bloqueante (caso `ret > 0` sem teste) entra na montagem do repo, teste T9.
- Codex #4 sem cota (janela semanal esgotada; reset em 6d 16h, medido por `usage.js --refresh`). Não usar Codex até o reset.
- OpenCode padrão (`omniroute/auto/coding:reliable`) roteia para Requesty, sem saldo. Usar modelo NVIDIA explícito com `-m` (`nvidia/moonshotai/kimi-k3`, `nvidia/z-ai/glm-5.3`).
- TUI do OpenCode não aceita prompt colado: usar `opencode run --auto -m <modelo> "$(cat contrato)"` por script.
- Agentes OpenCode #4 e OpenCode #5 encerrados com `maestri dismiss`; nenhum executor ativo; nenhum artefato deles no repo (sem saída útil).
- Revisão Haiku do contrato de montagem: 13 achados, 8 bloqueantes; corrigidos no contrato v2 (`[fora do repo] docs/history/prompts/prompt_repo_montagem_opencode4_v2_2026-10-13.txt`).

## Repo público (decisão do dono, 2026-10-13)
- Nome: `rianprei/ultraroot-ultrakernel`. Criar privado, verificar, depois tornar público.
- Sem trailer de IA em commit (skill no-ai-attribution). E-mail do commit: o noreply do lake.
- Sem licença definida (todos os direitos reservados) — pendência do dono.
- Fora do repo: keys, backups, Magisk, scratch (exceto relatórios e código listados), APK/IMG/SO, dmesg bruto do aparelho, capturas PNG em `docs/history/evidence/`.
- Lake: só `[fora do repo] tools/fastboot_guard.sh`, `[fora do repo] tools/selftest_fastboot_guard.sh` e README, no commit c8e7812 (não publicado no GitHub; remoto público em ffe583a). Diff de trabalho em arquivo separado.
- Executor da montagem: nenhum agente. Feito direto, com scripts e verificador mecânico (`tools/verify_repo.sh`), sabotagens e clone de conferência. Sem revisão cruzada por agente (o dono pediu para parar de rodar agentes); registrar isso no relatório final.

## Pendências do dono
- Licença do repo.
- "sim, instalar ui10" (D1) — APK ui10 não contém o símbolo de `setAppProfile`; H8 entra no próximo build, depois de E2.
- Lake: "sim, commitar" e "sim, push" para c8e7812.
- Escolha de copy E1 (duas opções no repo, `docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md`).

## Como retomar
1. Ler o `docs/ultraroot/plan.md` §1 e §9 (no repo: `docs/ultraroot/plan.md`).
2. Ver o estado do repo: `git -C [fora do repo]/ultraroot-ultrakernel log --oneline` e `tools/verify_repo.sh`.
3. Não rodar agentes sem pedido explícito do dono. Não rodar aparelho sem "sim, <passo>".
