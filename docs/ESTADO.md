# Estado do projeto (2026-10-13)

Este documento resume o que está feito, o que está bloqueado e o que é arriscado. Cada item aponta a fonte. Se um item não tiver fonte, trate como NÃO VERIFICADO.

## Feito, com prova

- **H8 (escrita JNI de `SET_APP_PROFILE`):** conferência independente em `docs/history/reports/review_h8r_2026-10-13.txt`, veredito APROVADO e "H8 fecha". O único achado não bloqueante foi o caso `ret > 0` sem teste. A correção está em `ultraroot/core-backend/src/test/native/fill_test.cc`, na função `ioctlPositiveReturnIsPassedThrough`.
- **H1, H2, H3, H4, H5, H6, L1 e L2:** fechados segundo `docs/ultraroot/plan.md` §9 e `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-v3.md`.
- **H3, build ui10:** APK assinado em V2 (cert SHA-256 `efb8d49f…`), com registro em `docs/ultraroot/plan.md` §9. O APK em si não está no repo.
- **UltraKernel, host (2026-10-13):** `Image` compilada a partir do clone publicado (`CONFIG_KSU=y`, 0 avisos); gate de CRC PASS (2309 símbolos, 0 divergências); `run_all_checks` do lake com 116 de 116 na árvore corrigida; selftest do guard de fastboot com 44 de 44. Relatórios em `docs/history/reports/` (arquivos `uk2_…`, `uk3_…`, `uk4_…` e `uk5_…`, de 2026-10-13). Tarefas em `docs/ultrakernel/TODOS.md`, UK-2 a UK-5.
- **UltraRoot, host (2026-10-13):** a partir do clone, com cache Gradle vazio, `core-backend` passa em 20 testes, `app` passa em 41, e o APK de debug é gerado. Relatório: `docs/history/reports/app_gradle_clone_2026-10-13.txt`.
- **Agente de nuvem (2026-10-14), branch `cloud/ultraroot-ultrakernel`, revisão pendente:** K2 (proposta `docs/ultrakernel/UK2-TRIM-PROPOSTA.md`); K5 parcial (README do research corrigido; PATHMAP BLOQUEADA, `c8e7812` ausente); U1 (`bash tools/check_code_paths.sh` → `CODE-PATHS: ok=10 falta=0 externo=4 elidido=2`, código 0); K3 (fixture sintética do T-1: `T1-AUDITED-SYNTH: 3 de 3`, total 44 de 44); K6 (avisos V4 de 980 para 32, STRICT com zero links ausentes); K7 (proposta `docs/ultrakernel/UK7-PROPOSTA.md`, `olddefconfig` com 0); U2 (`SelfUpdatePolicy`, 14 testes de host); K4 (resumo do `run_all_checks` com causa e efeito, 116 de 116, código 0). Relatório: `docs/history/reports/agente-nuvem-2026-10-13.md`.

## Bloqueado pelo dono

- **D1 (instalar o ui10):** aguarda "sim, instalar ui10" (`docs/ultraroot/plan.md`, linha 92).
- **H7 (ligar a tela ao backend e à busca):** aguarda a escolha de copy e layout do E1. Duas opções em `docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md`.
- **E2 (origem do fd), que libera a chamada de `setAppProfile` na tela:** depende de D2, "sim, diagnóstico do fd" (`docs/ultraroot/plan.md`, linha 93).
- **E3, E4, E5, E6:** decisões em `docs/ultraroot/plan.md` §3, linhas 104 a 109.
- **Lake:** o commit `c8e7812` existe só localmente. Commit e push aguardam "sim, commitar" e "sim, push".

## Fato verificado que muda o próximo build

O APK `ui10` não contém o símbolo JNI `Java_dev_superroot_core_EngineNatives_setAppProfile`. Verificado no host: contagem 0 em `libultraroot.so` (arm64-v8a), enquanto `kernelFlags` aparece 1 vez. Por isso a H8 só entra no próximo build, depois de E2. D1 instala o ui10 como está.

## Riscos

- **Magisk coexistindo (E3):** a coroação completa depende de SP2 e SP3.
- **Grant não provado (E4):** "su via KSU não está provado". Qualquer texto de tela ou de documento que afirme que o root funciona está errado até a prova.
- **Cota:** a janela semanal do Codex estava esgotada em 2026-10-13, com reset em 6 dias e 16 horas, segundo `usage.js --refresh`. Enquanto isso, trabalho com Codex fica parado.
- **Lake sem publicação:** o repo público do lake está em `ffe583a`. O commit `c8e7812` (usado como snapshot em `kernel/lake/`) não está no GitHub.
- **Erros para revisão:** `docs/ERROS-PARA-REVISAO.md` lista os erros conhecidos, com evidência e estado. A correção da redação do certificado público do GKI (ERR-01) está publicada em `main`. Há um item de risco e uma decisão do dono sobre o histórico (ERR-03).
- **Dados pessoais:** serial de aparelho, IP privado e um e-mail foram redigidos. Os locais estão em `docs/history/REDACOES.txt`.

## Próximo passo técnico (sem aparelho)

- Escolher a copy do E1 (dono). Depois, implementar H7 com teste de integração.
- Próximo build com H8 incluída (depois de E2).
- Agente de nuvem: resolver os itens ABERTOS de `docs/ERROS-PARA-REVISAO.md`. As decisões do dono ficam em `docs/PENDENCIAS-DO-DONO.md`.
