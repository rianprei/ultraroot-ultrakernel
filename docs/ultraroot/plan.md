# PLAN — UltraRoot SP1: rota até o fim

- Data: 2026-10-13
- Autor: maestro (Claude). Executor: Codex #4. Revisor: OpenCode #2. grok não aparece no `maestri list` de hoje.
- Base: spec rev.7 (aprovado em 2026-10-11) · plano SP1 `[fora do repo] docs/superpowers/plans/2026-10-11-ultraroot-sp1.md` · handoff v4 (2026-10-12). Este arquivo substitui a lista de próximos passos do handoff v4.
- Legenda: [x] feito · [~] parcial · [ ] pendente · [!] espera "sim, <passo>" ou decisão do dono.

## 0. O que "terminar" significa

1. Tudo que dá para fazer sem aparelho está feito, testado, revisado por outro agente e com prova em arquivo.
2. Todo item de aparelho tem resultado registrado (passou, falhou ou NÃO TESTADO) ou espera um "sim, <passo>" do dono.
3. Pelo spec §12, o SP1 não termina quando compila. Termina com critérios cumpridos e lacunas declaradas.

Eu sigo sem parar até o primeiro gate de aparelho ou de decisão. Nesse ponto paro e peço o passo exato.

## 1. Regras fixas

1. Aparelho: só com "sim, <passo>" explícito. Vale também para leitura.
2. Boot: slot ativo. No POCO C75 o ativo é boot_b. O slot A é o OS antigo e nunca é alvo. Rollback só pelo `[fora do repo] tools/fastboot_guard.sh`.
3. Legítimo: nada de DenyList, hide ou umount por perfil.
4. Autor ≠ revisor. Revisor principal: OpenCode #2. Os achados dele são conferidos linha a linha antes de aceitar.
5. Todo achado de revisão é corrigido, sem triagem por severidade. Achado que não pode ser corrigido vira bloqueio com motivo escrito.
6. Prompt a agente: `/prompt-techniques` antes de escrever. Estrutura: objetivo, escopo, arquivos, tarefa, todo, prova. Código sempre com prova de sabotagem.
7. Scratch de agente: `[artefato local, fora do repo]/<agente>/`. Nunca `/tmp`.
8. Commit e push: só com "sim". O lake tem c8e7812 não enviado e duas alterações sem commit.
9. Toda alegação com prova: arquivo:linha, saída, hash. Sem prova, NÃO VERIFICADO.
10. Chave de assinatura em `[chaves locais do dono, fora do repo]/`: não abrir, não imprimir, não gravar senha.
11. Subagentes do harness: não usar.
12. Handoff automático em `docs/ultraroot/` ao passar de 490.000 tokens de contexto. Nunca em `/tmp`.

## 2. Estado de partida (verificado em 2026-10-13)

### Código host (não é git)
- Projeto: `ultraroot/`.
- Testes: core-backend 10/10 · app 26/26 (último build verde).
- Estado honesto (`core-backend/.../BackendState.kt`): feito, com 7 testes. Revisado por OpenCode #2 (REPROVADO, corrigido).
- Busca e filtro do presenter (`app/.../RootAccessState.kt`): feito, com 17 testes de filtro. Revisado por OpenCode #2 (APROVADO COM CORREÇÕES, corrigido) e por Codex #4 (APROVADO COM CORREÇÕES, corrigido). Ainda não ligado à tela.
- Versão no código: versionCode 40009, "0.2.0-ui9". O código mudou depois do ui9. Precisa de ui10.

### Aparelho (último estado documentado, 2026-10-12; nada consultado hoje)
- ui8 (vc40008) instalado; ui9 construído, não instalado.
- Manager coroado: uid 10296 (dmesg, 2026-10-12).
- Allowlist: lida, vazia. Grant: NOT_TESTED.
- `accept_ksu_root.sh` reprova por contexto magisk: o Magisk ainda é dono do su.
- C0.3 no aparelho: UNKNOWN (sem log de boot ligado ao hash).
- Origem do fd `[ksu_driver]`: não medida. Antes da ui3, o processo não tinha o fd (`docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md:9`).

### Lake (`kernel/lake`)
- HEAD c8e7812 "guard: rollback flashes boot_b then reactivates slot b". Local, à frente 1 de origin.
- Sem commit: `[fora do repo] tools/fastboot_guard.sh` e `[fora do repo] tools/selftest_fastboot_guard.sh` (9 inserções, 5 remoções).
- O selftest usa stub de fastboot no PATH. Não toca o aparelho.

### Agentes (maestri list, 2026-10-13)
- Conectados: Codex #4, OpenCode #2. O OpenCode #2 substituiu o Claude Code #2 em 2026-10-13 (mensagem do dono). A confiabilidade dele como revisor é desconhecida: cada prova é conferida.
- Operação do OpenCode #2: se travar em "Permission required … Access external directory", responder com `maestri ask "OpenCode #2" --raw '\e[C\n\n'` (seta direita, Enter, Enter). Checar o estado com `maestri check "OpenCode #2"`.
- Não aparece: grok.

### Docs
- `docs/ultraroot/TODOs.md` está desatualizado: cita vc40000 como APK do aparelho.
- `docs/ultraroot/OWNAPP_INC1.md:34` corrigido: o `.so` procura o fd, não o abre.

## 3. Rota

### Fase H — host (executo com os agentes, sem parar)

| ID | O que | Dono (executor → revisor) | Aceite | Gate |
|---|---|---|---|---|
| H1 | Expor `flags` do GET_INFO (LKM, MANAGER, LATE_LOAD): JNI `kernelFlags`, `decodeKsuMode`, `KsuBackend.readMode()` | Codex #4 → OpenCode #2 | KsuModeTest verde (7 testes); core-backend verde; compilação nativa verde ou NÃO VERIFICADO com motivo; sabotagem da máscara LKM pega; revisão sem achado aberto | — |
| H2 | Inc3 no host: porta `RootGrantPort` (grant e revoke), fake nos testes, estado de confirmação e resultado por app na lógica. Nenhum JNI de escrita | Codex #4 → OpenCode #2 | testes TDD; sabotagem na confirmação; revisão sem achado aberto | — |
| H3 | Build ui10: versionCode 40010, versionName "0.2.0-ui10"; release; zipalign; apksigner (procedimento em `docs/ultraroot/OWNAPP_INC2.md`). O `OWNAPP_INC2.md` não traz o comando de assinatura: o comando está no relatório do Codex | Codex #4 prepara até o APK alinhado; OpenCode #2 confere o badging e o zipalign | `aapt dump badging` com `me.weishu.kernelsu` e 40010; cert `efb8d49f…`; sha256 registrado em `out/` | [!] senha da chave, digitada pelo dono no momento da assinatura |
| H4 (FECHADO em 2026-10-13: Codex entregou; OpenCode #2 APROVADO, sem achados) | Docs: TODOs.md (vc40000 → ui8/ui9, estado honesto, busca e filtro, H1 e H2), CAPABILITIES.md (KSU-ROOT, UR-DASH, UR-PERF-READ), COMPAT_MATRIX.md (células de flags) | Codex #4 → OpenCode #2 | cada linha alterada tem prova (arquivo:linha) | — |
| H5 | Reconferir as duas correções finais da linha 85 do inventário, pendentes desde 2026-10-12, e o veredito de cabeçalho | OpenCode #2 (leitura) | veredito com arquivo:linha | — |
| H6 | Verificar Task 3 (cenários A/B/C do accept), Task 4 (lista de cenários de recovery) e Task 6 (fixtures de paridade). Criar o que faltar | Codex #4 → OpenCode #2 | cada cenário com saída; itens faltantes criados | — |
| H7 | Ligar `classifyBackend` e `KsuMode` à tela de kernel; busca e chips na tela de root; NoMatch alcançável | Codex #4 → OpenCode #2 → design review | teste de integração do presenter com os critérios da tela; design review sem achado aberto | [!] E1 (copy e layout) |
| H8 | Escrita JNI de perfil (`SET_APP_PROFILE`): export no `engine.cc` e conferência da struct contra `uapi/app_profile.h`. Sem chamada na UI | Codex #4 → OpenCode #2 | compilação nativa; struct conferida campo a campo | [!] E2 (origem do fd) antes de liberar a chamada na UI |

Ordem de H: H1 → (H2 ∥ H6) → H5 → H3 → H4 → H7 (depois de E1) → H8 (depois de E2).

### Fase L — lake (paralela; bloqueia o flash)

| ID | O que | Dono | Aceite | Gate |
|---|---|---|---|---|
| L1 | Revisar o diff não commitado (2 arquivos) e o commit c8e7812 | OpenCode #2 (o autor do diff é o Codex #4) | achados corrigidos; veredito com prova | — |
| L2 | Rodar `[fora do repo] tools/selftest_fastboot_guard.sh` (stub, host) | maestro | exit 0; cobre rollback, recusa de set_active a e recusa fora da allowlist | — |
| L3 | Commit local e push do lake | maestro | commit com a mensagem e a coautoria do padrão; push só depois de revisão brutal (regra do dono) | [!] "sim, commitar" e "sim, push" |

### Fase D — aparelho (cada passo exige "sim, <passo>")

| ID | Passo | Pré-requisito | Aceite | Gate |
|---|---|---|---|---|
| D0 | Preflight Z0 (leitura): slot ativo b, lake, is-userspace, partição 67108864, bateria | L2 | todos os campos registrados | [!] "sim, preflight Z0" |
| D1 | Instalar ui10 com `pm install -r` a partir de /data/local/tmp; sha256 no aparelho; captura de tela | H3, H7 | sha256 confere; captura salva; design review | [!] "sim, instalar ui10" |
| D2 | Diagnóstico do fd (somente leitura): `/proc/<pid>/fd`, GET_INFO com flags, NEW_GET_ALLOW_LIST com errno, dmesg de track_throne | D1 | fd presente ou ausente, com prova; flags lidos | [!] "sim, diagnóstico do fd" |
| D3 | Log de boot do C0.3 ligado ao hash (fecha o UNKNOWN) | — | log com hash conferido | [!] "sim, log de boot do C0.3" |
| D4 | Grant teste: Termux como app de teste, concedido pela UI | H2, H8, E4 | allowlist mostra o app; allow_su=true; registrado como aceite parcial (E4) | [!] "sim, grant teste" |
| D5 | Migrar os 4 módulos nativos KSU: hosts, yt-noads, poco_perfgov, termuxadb_rootport. Um por vez | D4, E3 | cada módulo com comportamento verificado no aparelho | [!] "sim, migrar <módulo>" |
| D6 | Flash SP1 e coroação (Task 7): T-1 backup, flash do C0.3, reboot com guard, adb install do release, abrir o manager, aceite | L1, L2, L3, E3, D0 a D3 | aceite do `accept_ksu_root.sh`, ou lacuna registrada; dmesg_new_errors com rc 0 | [!] "sim, flash SP1" |
| D7 | Drill de rollback (cenário da Task 4): rollback do boot_b e volta ao backup | D6 | rota de rollback provada | [!] "sim, drill de rollback" |

### Fase E — decisões suas (cada uma bloqueia o passo indicado)

| ID | Decisão | Minha proposta | Bloqueia |
|---|---|---|---|
| E1 | Copy e layout dos cartões (ativo, sem resposta do kernel, incompatível) e da busca e do filtro | Eu trago duas opções de texto e de layout antes de implementar | H7, D1 (captura) |
| E2 | Origem do fd: depender do fd que já aparece no processo, ou o app instalar o fd pelo hook de reboot (`core-backend/src/main/cpp/uapi/supercall.h:15-17`; o `ksud` faz isso em `userspace/ksud/src/ksucalls.rs:126-128` do pin) | Esperar o D2 e decidir com a medida | H8, D4 |
| E3 | Ordem com o Magisk. A coroação completa exige migrar os 8 módulos. 4 deles precisam de Zygisk (SP2) ou de adapter (SP3) | Fechar o SP1 sem desligar o Magisk. Migrar só os 4 nativos (D5). Coroação completa e aceite pleno ficam para depois de SP2 e SP3, registrados como lacuna | D5, D6 |
| E4 | Aceite do grant sem o Magisk desligado. Sem isso, nenhuma prova mostra que o su do app passa pelo KSU | Aceite parcial: allowlist mostra o app e allow_su=true. "su via KSU não provado" fica como lacuna | D4 |
| E5 | Estado "biblioteca ausente" separado de "sem resposta do kernel" | Não separar. O motivo já diz a diferença | — |
| E6 | Filtro de sistema e de usuário (exige o tipo do app, fonte nova) | Fora do SP1 | — |

## 4. Status das tarefas do SP1 (`[fora do repo] docs/superpowers/plans/2026-10-11-ultraroot-sp1.md`)

| Task | Conteúdo | Status | Próximo |
|---|---|---|---|
| 1 | Guard hashes apontam para o C0.3 | [~] feito; revisão pendente | L1 cobre o lake |
| 2 | Rollback reativa o b, nunca o a | [~] código e caso de selftest existem; selftest não reexecutado hoje | L2 |
| 3 | accept distingue KSU de Magisk | [~] script sem `|| true`; cenários A, B e C não reexecutados | H6 |
| 4 | Scaffold de recovery | [~] RECOVERY_SUITE.md existe; conteúdo não reconferido | H6 |
| 5 | CAPABILITIES.md e COMPAT_MATRIX.md | [~] existem; células a preencher | H4 |
| 6 | Paridade modular, fixtures de host | [~] MODULE_PARITY.md existe; fixtures não confirmados | H6 |
| 7 | Coroação e aceite no aparelho | [!] bloqueado | D0 a D7, E3 |

## 5. Critérios de fechamento do SP1

O SP1 fecha quando:
1. H1 a H6 estão concluídos, revisados e com prova.
2. L1 e L2 estão concluídos. L3 só com "sim".
3. D0 a D7 têm resultado registrado: passou, falhou ou NÃO TESTADO, ou foram adiados por você (E3 ou E4).
4. Cada lacuna está declarada, com motivo (spec §12).
5. O handoff final e os TODOs estão atualizados.

## 6. Riscos

- R1. A coroação completa depende de SP2 e SP3 (E3).
- R2. A origem do fd não está medida. D2 vem antes de H8 e D4.
- R3. O C0.3 no aparelho está em UNKNOWN. D3 resolve.
- R4. O grok está fora. OpenCode #2 é o único revisor. Eu confiro cada prova dele.
- R5. O lake tem alterações sem commit. L1 e L3 resolvem.
- R6. O TODOs.md está desatualizado. H4 resolve.
- R7. Contexto longo. O handoff automático sai a cada 490.000 tokens.

## 7. Próximos passos imediatos (atualizado em 2026-10-13)

1. Correção do KDoc do H1 (Codex #4, em andamento). Ao retorno: diff de uma linha, 20 testes do core-backend, sha dos outros quatro arquivos do H1.
2. Revisão do H2 final pelo OpenCode #2 (em andamento). Ao retorno: conferir cada linha citada e cada sabotagem reproduzida antes de aceitar.
3. Achados da revisão do H2, se houver: uma rodada de correção no Codex #4, depois revisão do que mudou.
4. H4 (docs: TODOs.md, CAPABILITIES.md, COMPAT_MATRIX.md), pelo Codex #4, só depois de H1 e H2 aceitos.
5. H3 (build ui10): o maestro prepara tudo. A senha da assinatura é do dono.
6. Cada achado de revisão: conferir a citação linha a linha antes de aceitar. Achado verificado vira correção, sem triagem por severidade.

## 8. Anexos

- Testes do host (caminhos absolutos no comando real): `cd ultraroot && TMPDIR=[fora do repo] docs/history/reports/tmp JAVA_TOOL_OPTIONS='-Djava.io.tmpdir=[fora do repo] docs/history/reports/tmp' ./gradlew :core-backend:testDebugUnitTest :app:testDebugUnitTest --offline`
- Selftest do lake: `bash kernel/lake/tools/selftest_fastboot_guard.sh`
- Mapa: código em `ultraroot/`; docs em `docs/ultraroot/`; spec e plano SP1 em `[fora do repo]/docs/superpowers/`; evidências em `docs/history/evidence/`; revisões em `[artefato local, fora do repo]/claude2/` e `docs/history/reports/`; lake em `kernel/lake/`.

## 9. Estado de execução (2026-10-13)

- [~] H8 (escrita nativa de perfil `SET_APP_PROFILE`, SEM chamada na UI): Codex #4 entregou (`[fora do repo] ultraroot/core-backend/src/test/native_profile_write_2026-10-13.txt`). OpenCode #2: APROVADO COM CORREÇÕES (`docs/history/reports/review_h8_2026-10-13.txt`). Achado único, conferido pelo maestro (`engine.cc:227` repassa o `-1` do ioctl e perde o `errno`; `allowlist.c:171-174` rejeita com `-EINVAL`).
  - Correção H8R1 feita (Codex #4; `[fora do repo] ultraroot/core-backend/src/test/nativer1_errno_2026-10-13.txt`): `ioctl_status` em `app_profile_fill.h:9`, `engine.cc:227-228` trocado, 3 casos novos. NDK arm64 OK; Kotlin 20/20 (UP-TO-DATE, código Kotlin inalterado). Falha no teste: o caso EPERM não discrimina, porque em Linux EPERM = 1 = -(-1), o retorno bruto do ioctl. S10 só derrubou EINVAL. Conferido pelo maestro (`errno` do host: EPERM=1, EBADF=9).
  - Correção H8R2 em andamento (Codex #4; `[fora do repo] ultraroot/core-backend/src/test/nativer2_ebadf_2026-10-13.txt`): caso EPERM trocado por EBADF (9), que é o erro real sem fd do driver. S10 precisa derrubar os dois casos de errno. Depois: conferência do OpenCode #2.
  - H8 fica [x] quando a correção for conferida. Teste de escrita no aparelho: NÃO VERIFICADO (fora do escopo; D4 depende de E2 e E4).

- [x] H3 (FECHADO no preparo e na assinatura V2; D1 aguarda "sim, instalar ui10"; OpenCode #2 conferiu o APK final: PRONTO PARA INSTALAR, `review_ui10_final_2026-10-13.txt`). Codex #4 entregou (`docs/history/reports/h3_build_ui10_2026-10-13.txt`). Conferido pelo maestro: diff só nas linhas 21-22; `zipalign -c` OK; badging `me.weishu.kernelsu` / 40010 / `0.2.0-ui10` / `UltraRoot`; `apksigner verify` dá não assinado, como esperado; sha do APK alinhado `cc7212b6…`. Revisão de leitura do OpenCode #2 (`docs/history/reports/review_h3_badging_2026-10-13.txt`): PRONTO PARA ASSINATURA. Confirmou diff, sha, zipalign, badging, `apksigner verify` como não assinado, e o aviso de strip (`OWNAPP_INC2.md:70`, `.so` com `stripped`). Falta só a assinatura do dono. (`docs/history/prompts/prompt_h3_build_2026-10-13.txt`). Relatório esperado: `docs/history/reports/h3_build_ui10_2026-10-13.txt`. Escopo: `versionCode` 40010 e `versionName` "0.2.0-ui10" em `app/build.gradle.kts`; APK release não assinado; `zipalign`; `aapt2 dump badging`. Não assina.
  - Assinatura: FEITA em 2026-10-13, com o `.pass` autorizado pelo dono ("pode usar o pass", só para este build). 1ª saiu v3 apenas: REJEITADA, renomeada para `out/UltraRoot-own-ui10-v3only-REJEITADO.apk`. 2ª saiu V2-only, como o C0.3 exige: `out/UltraRoot-own-ui10.apk`, cert `efb8d49f…`, sha `b5f5bb36…`, registrado em `out/UltraRoot-own-ui10.sha256.txt`. Próximas assinaturas: perguntar de novo.
  - Ferramentas do SDK fora do PATH: `~/Android/Sdk/build-tools/37.0.0/` (aapt2, apksigner, zipalign).

- [x] L1: revisão do diff do lake pelo OpenCode #2 (`docs/history/reports/review_lake_guard_2026-10-13.txt`). Verificada linha a linha pelo maestro (`[fora do repo] docs/history/prompts/L1_verificacao_2026-10-13.txt`). Um gap confirmado, na ordem flash e set_active b no R1 do selftest, corrigido. Diff do lake: `[fora do repo] tools/fastboot_guard.sh` (sem mudança minha) e `[fora do repo] tools/selftest_fastboot_guard.sh` (com a checagem de ordem). Commit: [!] "sim, commitar".
- [x] L2: selftest do lake em host, com stub. Depois da correção: rc 0, 90 casos OK, 0 falhas (`[fora do repo] docs/history/prompts/selftest_pos_fix_2026-10-13.out`).
- [ ] H2, H3, H4, H7, H8, D0 a D7 e E1 a E6: pendentes, conforme §3.
- [x] H5: reconferência da linha 85 pelo OpenCode #2 (`docs/history/reports/review_h5_inventario_2026-10-13.txt`). Veredito do revisor: APROVADO, com T2 parcial em HomeScreen.kt:887-908.
  - Correção aplicada: "fluxos do Home" virou "diálogos do Home de desinstalar/restaurar e de instalar o manager", que é o que as linhas 883-908 mostram. Texto anterior guardado em `[fora do repo] docs/history/prompts/MAGISK_KSU_UI_INVENTORY.antes_H5_2026-10-13.md`. Se o dono preferir o texto fechado em 2026-10-12, reverter.
  - Erro de citação do revisor: o veredito `APROVADO COM CORREÇÕES` está em `REVIEW_MAGISK_KSU_INVENTORY_haiku55.md:4`, e não na linha 8 como ele disse.
  - Pendência que continua NÃO VERIFICADA: P1 e P2 da reaprovação do Claude Code #2 (rodada 6), porque a saída salva não as mostra (`REVIEW_MAGISK_KSU_INVENTORY_haiku55.md:164`).
- [x] H1 (fechado em 2026-10-13) → H1F: defeito real corrigido pelo Codex #4 (`docs/history/reports/h1_fix_readmode_2026-10-13.txt`). Achado original: `readMode()` devolvia `Ok` com flags zerados quando o driver não respondia (`engine.cc:67-75`, `:105-106`; `readNative` só converte erro de biblioteca). Correção: `decodeKernelMode(version, flags)` em `KsuMode.kt:24-29` e `readMode` em `KsuBackend.kt:70-76`.
  - Conferido pelo maestro no disco: XML de 10:21:06, posterior à última edição (10:20:53). KsuModeTest 10/0, KsuBackendHostTest 3/0, BackendStateTest 7/0, total 20. sha256 atuais: KsuMode.kt `cb26f43…`, KsuBackend.kt `98122bd…`, KsuModeTest.kt `f148e91…`. Escopo: os cinco arquivos de H1 e nada mais. `engine.cc` e `EngineNatives.kt` iguais aos de antes.
  - Revisão do H1 pelo OpenCode #2 (`review_h1_flags_2026-10-13.txt`): APROVADO, mas NÃO aceito. Não viu o defeito e errou citações (`engine.cc:89-92` é `kernelVersion`; bits em `supercall.h:28-30`).
  - Erro do meu prompt H1F: a rubrica R3 pedia o sha de T0 como alvo de restauração. O alvo certo é a cópia pré-sabotagem. O autor usou o certo e registrou.
  - Revisão do H1F pelo OpenCode #2 (`docs/history/reports/review_h1f_2026-10-13.txt`): APROVADO COM CORREÇÕES. Defeito original fechado; sabotagens S1 e S2 reproduzidas numa cópia, caindo nos testes esperados. Um achado: o KDoc de `KsuMode.kt:5` afirma "(the lake kernel)" sem prova.
  - Conferido pelo maestro: o achado procede. Além disso, o revisor não viu três `CONFIG_KSU` (`[fora do repo] docs/research/CODEX4_dew_tree.md:69` e `[fora do repo] docs/research/OPENCODE3_mtk_gki_failures.md:18`, além dos dois que ele citou). Nenhum é medida do build do lake, então o veredito não muda.
  - Correção feita (Codex #4, ULTRAROOT-H1KDOC; relatório `docs/history/reports/h1_kdoc_fix_2026-10-13.txt`). Conferido pelo maestro no disco: `diff` mostra só a linha 5; sha dos outros quatro arquivos do H1 iguais aos de T0; XML de 13:44:21 com 20 testes e 0 falhas, posterior à edição (10:44:19).
  - FECHADO: o OpenCode #2 confirmou o diff (`docs/history/reports/review_h1kdoc_confirma_2026-10-13.txt`, veredito FECHADO). O maestro conferiu de novo: diff de uma linha, sha `47148567…`, `dispatch.c:44-45` sustenta a frase, sem menção ao lake, 20/20 nos XMLs.
  - H1 vira [x] só com a revisão do H1F sem achado aberto. Teste em aparelho: NÃO VERIFICADO (fora do escopo).
- [x] H2 (fechado em 2026-10-13): Codex #4 entregou (`docs/history/reports/h2_grant_flow_2026-10-13.txt`, 11 testes novos, app 37). Conferido pelo maestro no disco: sha dos sete arquivos existentes igual ao T0 do relatório; nenhuma tela referencia o fluxo; app depende de core-backend.
  - Lido o código: três defeitos que os testes não cobrem. (1) `confirm()` só fecha a barreira depois do laço: se a porta lançar, o estado fica `Confirming` e uma nova chamada repete escritas. (2) `request()` aceita uid repetido. (3) `request()` guarda a lista por referência: alterar a lista depois de confirmar muda o que roda.
  - Correção H2F feita pelo Codex #4 (`docs/history/reports/h2_fix_2026-10-13.txt`). Lido pelo maestro no código: F1 (`RootGrantFlow.kt:41`, `:55-58`), F2 (`:31-33`), F3 (`:34`). Testes novos em `RootGrantFlowTest.kt` perto das linhas 182, 192 e 208. XML de 13:40:01: fluxo 14/0, app 40/0. Sha conferidos com o relatório.
  - Revisão do H2 final pelo OpenCode #2 (`docs/history/reports/review_h2_2026-10-13.txt`): APROVADO COM CORREÇÕES. Contrato R-a a R-f conferido por linha; F1, F2 e F3 provadas; S4, S5 e S6 reproduzidas numa cópia nas mesmas linhas do autor.
  - Achado 1 (conferido pelo maestro, procede): `requestWithRepeatedUidIsRefused` usa `listOf(a, a)`, mesmo uid e mesmo nome. Não distingue checagem por uid de checagem por objeto. Correção H2R1: teste novo `requestWithSameUidAndOtherPackageIsRefused` e sabotagem S7 (`RootGrantFlow.kt:31` trocado por `targets.toSet()`).
  - Achado 2 (conferido, procede com citação errada): comentários em inglês em `RootGrantFlow.kt:40` e `:56`, quando a camada de UI comenta em português (`RootAccessState.kt:19, :61, :68`). O revisor citou `KsuBackend.kt:12`; a frase em inglês está em `BackendState.kt:12`. Correção H2R1: traduzir os dois comentários.
  - Correção H2R1 feita (Codex #4; relatório `docs/history/reports/h2_review_fix_2026-10-13.txt`). Conferido pelo maestro: diff só nas linhas 40 e 56 (comentários em português); teste novo em `RootGrantFlowTest.kt:233-242`, com a falha da S7 na linha 239; remover o bloco reconstrói o arquivo com sha `e78ba01e…` (única mudança no arquivo de teste); XML 15/0 e app 41/0, posteriores à última edição.
  - FECHADO: o OpenCode #2 confirmou (`docs/history/reports/review_h2r1_confirma_2026-10-13.txt`, veredito FECHADO). Reproduziu a S7 numa cópia nova (falha só no teste novo, linha 239; o antigo segue verde), e reconstruiu o arquivo de teste com `head -n 231` (sha `e78ba01e…`). Maestro: conferiu o diff, a linha da falha e os XMLs 15/0 e 41/0.
  - Confirmação do OpenCode #2 sobre o H2R1 e sobre o KDoc do H1: em andamento (`review_h1kdoc_confirma_2026-10-13.txt` e o da rodada H2R1). H1 e H2 só viram [x] com essa confirmação.
  - Depois: revisão do H2 final pelo OpenCode #2. Limite conhecido, registrado e não corrigido: o fluxo não é thread-safe; a UI chama de uma thread só.
- [x] H6: artefatos das Tasks 3, 4 e 6 conferidos. Nenhum item faltando. Relatório do OpenCode #2: `docs/history/reports/review_h6_sp1_tasks_2026-10-13.txt`.
  - Testes executados pelo maestro, em host, sem device: `scratch/sp1/test_accept_ksu.sh` rc 0, PASS (saída em `[fora do repo] docs/history/prompts/h6_test_accept_2026-10-13.out`); `scratch/sp1/test_module_props.sh` rc 0, PASS (saída em `[fora do repo] docs/history/prompts/h6_test_module_props_2026-10-13.out`). Nenhum dos dois chama adb nem fastboot.
  - Erros de citação do revisor, corrigidos aqui: `accept_ksu_root.sh` tem 99 linhas (ele disse 120); `test_accept_ksu.sh` tem 69 (ele disse 74); `test_module_props.sh` tem 59 (ele disse 52); os cenários A, B e C ficam nas linhas 19, 26 e 33 do teste, e as execuções em 55 a 57.
- [x] H1 revisão cruzada 1 (OpenCode #2): recebida em `review_h1_flags_2026-10-13.txt`. Verdito APROVADO, contestado pelo maestro (ver bullet H1 acima). Log do TUI sem conteúdo útil além do relatório.
