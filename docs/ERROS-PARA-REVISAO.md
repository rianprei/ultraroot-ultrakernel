# Erros para revisão

Este arquivo lista os erros conhecidos deste repo, com a evidência, o estado e o que o agente deve fazer. Ele foi escrito para o agente de nuvem, que só enxerga este repo. Leia o `AGENTS.md` antes.

Cada item tem prova: caminho e linha, comando com a saída esperada, ou relatório em `docs/history/reports/`. Afirmação sem prova é NÃO VERIFICADO.

## Estados

- **CORRIGIDO:** o erro foi corrigido e está publicado em `main`. Falta a revisão indicada no item.
- **ABERTO:** o agente corrige. Pela regra do dono (`AGENTS.md`), todo achado vira correção. Não existe achado "baixo" para deixar de lado.
- **DECISÃO DO DONO:** não altere nada sem a frase exata do dono. As frases estão em `docs/PENDENCIAS-DO-DONO.md`.

Nesta rodada, o dono pediu commit e push direto em `main`, sem PR. O fluxo de branch e PR do `AGENTS.md` continua escrito lá (ver ERR-20).

## Contrato para o agente

- **OBJETIVO:** revisar os itens CORRIGIDOS e resolver os ABERTOS que não são decisão do dono.
- **ESCOPO DE ESCRITA:** `docs/`, `README.md`, `kernel/README.md`, `tools/verify_repo.sh` e `kernel/lake/` (só nas correções listadas aqui).
- **ESCOPO DE LEITURA:** o repo inteiro. O agente não tem acesso ao computador do dono.
- **DEFINIÇÕES:** "corrigido" quer dizer que o item tem o antes e o depois, com comando e saída. "resolvido" quer dizer que o critério de aceite do item passa, com saída citada.
- **PREDICADO DE SUCESSO:** para todo item ABERTO que não é decisão do dono, existe um commit em que o critério de aceite passa, e a saída está colada no item. Para todo item DECISÃO DO DONO, nenhum arquivo muda.
- **RESULTADOS NÃO CONTABILIZADOS:** "parece correto"; corrigir o texto sem rodar o comando; reescrever histórico; instalar APK no aparelho; mudar opção de tuning sem aprovação; abrir PR sem pedido.
- **PROVA:** `bash tools/verify_repo.sh` sai com código 0, e a saída é colada no item.
- **PROIBIDO:** `git push --force` e reescrita de histórico; incluir imagem de boot, backup, chave ou e-mail; qualquer passo de aparelho sem a frase exata do dono.
- **TETO:** 60 chamadas de ferramenta por rodada (sugestão, não é regra do dono).

## Itens

### ERR-01: redação indevida do certificado público do GKI no lake (CORRIGIDO)

- **Onde:** `kernel/lake/docs/KMI-GATES.md:25`, `kernel/lake/docs/PLAN-AND-FINDINGS.pt-BR.md:53`, `kernel/lake/docs/research/CODEX5_build_signing.md:133`, `kernel/lake/docs/research/raw/OPENCODE4_review_codex5.md:156` e `kernel/lake/tools/selftest_backprop.sh:375`.
- **Defeito:** a primeira montagem trocou a fingerprint e o serial do certificado público do GKI por `[redigido: mac]` e `[redigido: serial]`. Sem a correção, `run_all_checks` do lake falha em V1, V68 e V77 (3 de 116).
- **Prova do defeito:** `docs/history/reports/uk3_run_all_checks_publicado_0f3fbe3c6_2026-10-13.txt`.
- **Correção:** as 5 linhas foram restauradas a partir de `c8e7812`. A restauração só foi feita porque a linha redigida era igual à original redigida, sem outras mudanças. Registro em `docs/history/REDACOES.txt`.
- **Revisão pedida:**
  1. Confirmar que as 5 linhas não têm dado pessoal de aparelho. O certificado é público.
  2. Rodar `kernel/lake/tools/run_all_checks.sh`. Esperado: `run_all_checks: PASS`, 116 de 116.
  3. Rodar `kernel/lake/tools/selftest_backprop.sh`. Esperado: `SELFTEST-BACKPROP PASS`, com 151 sabotagens.

### ERR-02: regra de MAC do verificador casava fingerprint de 32 pares (CORRIGIDO)

- **Onde:** `tools/verify_repo.sh`, regra V3 (variável `PII`).
- **Defeito:** a regra `([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}` casa qualquer janela de 6 pares. Uma fingerprint de 32 pares virava "MAC". Foi a causa do ERR-01.
- **Correção:** a regra exige borda (nenhum par vizinho). O próprio verificador tem um teste: MAC real casa, fingerprint de 32 pares não casa. A linha do CN público do certificado do GKI também é ignorada.
- **Revisão pedida:** sabotagem numa cópia descartável. Uma linha com MAC real deve falhar em V3. Uma linha com a fingerprint de 32 pares não deve falhar. Um serial de aparelho deve falhar. A prova já feita está em `docs/history/reports/` e no log do verificador da rodada.

### ERR-03: e-mail pessoal do dono em três commits publicados (CORRIGIDO para o futuro; histórico é DECISÃO DO DONO)

- **Onde:** commits `901a32718`, `739838c3a` e `0f3fbe3c6`. O autor e o committer desses três commits têm o e-mail pessoal do dono. Os commits anteriores usam o noreply do lake.
- **Defeito:** a decisão de 2026-10-13 manda usar o noreply do lake nos commits (`docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-ctx490k-b.md`, linha 16). O repo não tinha `user.email` local, então o git usava o global. Prova: `git config --show-origin --get user.email` mostra só o global.
- **Correção nesta rodada:** o repo passou a ter `user.email` local com o noreply do lake. Os commits novos usam o noreply.
- **Pendente (DECISÃO DO DONO):** trocar o autor dos três commits exige reescrever histórico público, com force push. Não fazer sem a frase "sim, reescrever histórico".
- **Revisão pedida:** `git log --format='%h %ae %ce' -n 8`, e o verificador mostra o aviso V9 com contagem 1 (um e-mail distinto, no histórico).

### ERR-04: contagens e pin do KernelSU erradas nos documentos (CORRIGIDO)

- **Onde:** `docs/PRE-REQUISITOS.md` e `kernel/upstream/KernelSU/PIN.md`.
- **Defeito:** a doc dizia "679 arquivos". O commit `6e3860443f8fea7c5fc226277e53888033796488` tem 680 entradas: 678 arquivos regulares e 2 links. A tabela de sha256 do `PIN.md` tem 7 linhas.
- **Prova:** `git ls-tree -r 6e3860443f8fea7c5fc226277e53888033796488` no repo `https://github.com/tiann/KernelSU.git` dá 680 linhas. Comparando os blobs dos 678 arquivos regulares com os do repo, dá 678 iguais.
- **Revisão pedida:** repetir a comparação de blobs. Esperado: 0 diferentes. O repo do KernelSU não está neste repo, então o agente precisa buscá-lo no GitHub.

### ERR-05: "testado a partir do clone publicado" usava outra árvore (CORRIGIDO)

- **Onde:** `docs/PRE-REQUISITOS.md` (seção de testes) e `docs/ultrakernel/TODOS.md` (UK-2).
- **Defeito:** a `Image` anterior foi compilada numa cópia de trabalho do commit `5bd40d675`, com a correção de libbpf ainda sem commit. Não era o commit publicado.
- **Correção:** o build foi refeito a partir de `0f3fbe3c6`. Relatório: `docs/history/reports/uk2_image_clone_2026-10-13.txt`.
- **Regra para revisão:** todo "testado a partir do clone" precisa de log de um clone do commit citado.

### ERR-06: primeira compilação do Gradle documentada com `--offline` (CORRIGIDO)

- **Onde:** `README.md`, seção "Como construir (host)".
- **Defeito:** com cache vazio, `--offline` falha. A primeira compilação precisa de rede.
- **Prova:** `docs/history/reports/app_gradle_clone_2026-10-13.txt`, fase 1, sem `--offline`, com cache novo. Resultado: BUILD SUCCESSFUL.
- **Revisão pedida:** repetir com `GRADLE_USER_HOME` vazio.

### ERR-07: JDK e SDK não documentados (CORRIGIDO)

- **Onde:** `README.md` e `docs/PRE-REQUISITOS.md`.
- **Defeito:** nesta máquina, o `java` padrão é o 25. O projeto usa JDK 21 (`ultraroot/app/build.gradle.kts`, `sourceCompatibility`). `ANDROID_HOME` e a plataforma `android-37.0` não estavam documentados.
- **Revisão pedida:** sem `JAVA_HOME`, o build deve falhar com mensagem clara, ou o erro observado deve ser registrado. NÃO VERIFICADO: não foi testado sem `JAVA_HOME`.

### ERR-08: UK-0 marcado FEITO só com o clang do sistema (CORRIGIDO)

- **Onde:** `docs/ultrakernel/TODOS.md`, UK-0.
- **Defeito:** o estado citava só o clang 23.1.1, que não é o toolchain pinado (r510928).
- **Correção:** o estado cita também o r510928, no `gki_defconfig` e no `make Image` (ver UK-2).

### ERR-09: índice e status do UltraKernel desatualizados (CORRIGIDO)

- **Onde:** `docs/README.md` (linha do UltraKernel) e `docs/ultrakernel/README.md`.
- **Defeito:** o índice dizia "visão, não iniciado". O status não refletia os itens UK-2 a UK-6. Havia uma frase repetida e uma referência a "fase 5 do rascunho" sem fonte no repo.
- **Correção:** status atualizado e frase repetida removida. A referência sem fonte também saiu.

### ERR-10: afirmação sobre clang no `kernel/README.md` sem prova (CORRIGIDO)

- **Onde:** `kernel/README.md`, seção "Como montar um build".
- **Defeito:** "qualquer clang 17 ou mais novo serve" não tinha prova contra o gate de KMI.
- **Correção:** o texto diz que o gate foi conferido só com o r510928.
- **Revisão pedida:** NÃO VERIFICADO para outros clangs. Testar, ou manter a ressalva.

### ERR-11: `TRIM_UNUSED_KSYMS` é `n` no build e `y` no aparelho (DECISÃO DO DONO para aplicar; investigação é do agente)

- **Onde:** `docs/ultrakernel/UK6-INVENTARIO-TUNABLES.md`, seção "Diferenças"; `kernel/common/kernel/module/Kconfig:386`; `kernel/lake/data/official-kernel_aarch64.config`.
- **Fato:** o gate de KMI passa (2309 símbolos, 0 divergências, `docs/history/reports/uk4_gate_kmi_crc_2026-10-13.txt`). Com a opção desligada, o kernel exporta o conjunto completo de símbolos. No aparelho, ela está ligada.
- **NÃO VERIFICADO:** qual passo do build do aparelho liga a opção. O `BUILD.bazel` do delta é do Kleaf, e o workspace dele não está no repo.
- **Ação do agente:** procurar no repo o que liga o trim no build do aparelho. Propor a mudança no build `make`. Aplicar só com a aprovação do dono, e depois rodar o gate de novo.
- **Investigação (2026-10-14, agente de nuvem):** feita em `docs/ultrakernel/UK2-TRIM-PROPOSTA.md`. `trim_nonlisted_kmi: True` em `kernel/common/BUILD.bazel:154`; config do aparelho com `CONFIG_UNUSED_KSYMS_WHITELIST="abi_symbollist.raw"` (`kernel/lake/data/official-kernel_aarch64.config:837`). A união das 32 listas `android/abi_gki_aarch64*` reconstruída no host tem 8962 símbolos, o mesmo número do `abi_symbollist.raw` oficial (`kernel/lake/docs/research/CODEX2_kleaf_repro.md:50`). A regra do Kleaf que faz a tradução está fora do repo: NÃO VERIFICADO. Aplicar continua DECISÃO DO DONO.

### ERR-12: variante do aparelho, 4G ou 5G (DECISÃO DO DONO)

- **Onde:** `README.md` (modelo `2410FPCC5G`) e `docs/ultrakernel/TODOS.md` (UK-0).
- **Fato:** o modelo tem final `5G`. O pedido do dono fala em POCO C75 4G.
- **Ação:** não mudar a doc. O UK-0 segue PENDENTE-DONO.

### ERR-13: a `Image` não é reprodutível bit a bit (ABERTO)

- **Onde:** `docs/history/reports/uk2_image_clone_2026-10-13.txt` e `docs/PRE-REQUISITOS.md`.
- **Fato:** duas compilações da mesma árvore geram `Image` do mesmo tamanho (36.395.520 bytes), com 1153 bytes diferentes. A única diferença de fonte entre as duas árvores é `kernel/common/tools/lib/bpf/libbpf.c`, ferramenta do host. sha256 do build antigo: `1fe3d6b8…7861a`. Do build novo: `5d50b74c…bb00d`.
- **NÃO VERIFICADO:** a causa dos 1153 bytes.
- **Ação:** achar a causa. Listar os offsets diferentes e identificar as strings ou dados neles. Se for timestamp, usuário ou host do build, testar `KBUILD_BUILD_TIMESTAMP`, `KBUILD_BUILD_USER` e `KBUILD_BUILD_HOST` fixos, e comparar duas compilações. Registrar o resultado neste item.

### ERR-14: caso T-1 contra o backup auditado é pulado no selftest do guard (CORRIGIDO com fixture sintética na branch `cloud/ultraroot-ultrakernel`, revisão pendente)

- **Onde:** `kernel/lake/tools/selftest_fastboot_guard.sh` (ramo T1-AUDITED) e `docs/history/reports/uk5_selftest_fastboot_guard_2026-10-13.txt`.
- **Fato:** `GUARD-SELFTEST: 44 passada(s), 0 falha(s)`. O ramo do backup auditado não roda: `T1-AUDITED skip`, porque a imagem fica fora do repo.
- **Ação:** não incluir a imagem, que é do dono. Avaliar uma fixture sintética que cubra o mesmo ramo, ou manter o skip documentado no UK-5.
- **Correção (2026-10-14):** `kernel/lake/tools/selftest_fastboot_guard.sh` aceita a variável de teste `T1_SYNTH=1`, que copia a imagem sintética do selftest para uma fixture e usa o hash dela (`T1_HASH_OVERRIDE` só vale com `T1_SYNTH=1`). Sem a variável, o caminho real e a constante `T1_HASH` ficam intocados. Saídas: sem variável, `T1-AUDITED skip` e `GUARD-SELFTEST: 44 passada(s), 0 falha(s)`, código 0. Com `T1_SYNTH=1`: `T1-AUDITED-SYNTH: 3 de 3 caso(s) OK`, `T-1 pronto: sim`, `GUARD-SELFTEST: 44 passada(s), 0 falha(s)`, código 0. Com `T1_SYNTH=1 T1_SYNTH_SABOTAGE=1` (um byte trocado no offset 4096 depois do hash): `T-1 backup presente mas hash/tamanho/magic divergem`, `T-1 pronto: não`, `GUARD-SELFTEST: 44 passada(s), 1 falha(s)`, código 1.
- **Contagem:** o total continua 44, porque `t1_run` não soma em `pass` (conta só em `t1_fail`). Os três casos do ramo (aceite, hash trocado, partição 3ffffff) aparecem na linha separada `T1-AUDITED-SYNTH`.

### ERR-15: o resumo do `run_all_checks` repete a mesma causa em três linhas (CORRIGIDO na branch `cloud/ultraroot-ultrakernel`, revisão pendente)

- **Onde:** `kernel/lake/tools/run_all_checks.sh`, função `script_ok` e o bloco de resumo.
- **Fato:** quando `check_docs_numbers.sh` falha no V1, o resumo também marca V68 e V77 como FAIL. `script_ok` exige código 0 do script inteiro, então a falha de um item vira falha de outros.
- **Ação:** deixar a causa raiz visível e marcar os demais como efeito de V1. A contagem (116) e o código de saída não podem mudar.
- **Correção (2026-10-14):** `kernel/lake/tools/run_all_checks.sh` registra os scripts de cada invariante em `script_ok` e, depois do resumo, imprime `causa raiz: <Vn>; efeitos: <Vm>, ...`. As linhas `FAIL`, a contagem e o código de saída não mudaram. Prova com V1 forçado numa cópia: `V1 FAIL`, `V68 FAIL`, `V77 FAIL`, `causa raiz: V1; efeitos: V68, V77`, `run_all_checks: FAIL (3 de 116 verificações falharam)`, código 1. No repo: `run_all_checks: PASS`, 116 linhas PASS, código 0.
- **Achado no caminho:** neste ambiente (OpenSSL 3.0.13) o V67 falhava: `openssl req -not_before` só existe no OpenSSL 3.4 ou mais novo, e o `set -e` de `kernel/lake/tools/verify_modsig.sh` derrubava o selftest no caso 7. Correção: função `mkcert_dated` com fallback para `openssl ca -selfsign -startdate/-enddate`. Conferido à parte: cert vencido gerado pelo fallback → `Verify error: certificate has expired`; com datas válidas → `CMS Verification successful`.

### ERR-16: avisos V4 do verificador (CORRIGIDO na branch `cloud/ultraroot-ultrakernel`: 980 para 32, revisão pendente)

- **Onde:** documentos históricos em `docs/history/`.
- **Fato:** o verificador conta 975 referências a arquivos que ficaram fora do repo, de propósito (ver `docs/history/EXCLUSOES.txt`).
- **Ação:** reduzir as referências, ou marcar as que apontam para fora do repo.
- **Correção (2026-10-14):** antes, `bash tools/verify_repo.sh` → `AVISO V4 (histórico, não bloqueia): 980 referências a arquivos fora do repo` (com os arquivos novos desta rodada, 982). Causas e tratamento: (a) a regex casava caminhos no meio de outros (`dev/superroot/ultraroot/ui/...`); agora exige fronteira; (b) arquivos de `kernel/lake/` citam caminhos relativos à raiz do lake; (c) caminhos do KernelSU resolvem em `kernel/upstream/KernelSU/`; (d) caminhos da árvore do kernel resolvem em `kernel/common/`. As resoluções (b) a (d) e a marca não valem nos documentos STRICT. Depois disso, 336 avisos. As 304 referências reais em `docs/` receberam a marca `[fora do repo] <caminho>`, definida em `docs/history/EXCLUSOES.txt` (541 ocorrências em 92 arquivos, sem apagar texto). Depois: `AVISO V4 (histórico, não bloqueia): 32 referências a arquivos fora do repo`, todas em `kernel/lake/`, que não foi marcado (ERR-18). Sabotagem: link ausente em `README.md`, com ou sem marca → `V4 link ausente` e `VERIFICADOR FALHOU`.

### ERR-17: APK de debug com o package do manager instalado no aparelho (ABERTO, risco)

- **Onde:** `ultraroot/app/build.gradle.kts`, linha 18 (`applicationId = "me.weishu.kernelsu"`).
- **Fato:** o package é o do KernelSU. O APK de debug usa a assinatura de debug padrão do AGP, que é diferente da do manager instalado no aparelho do dono. Instalar por cima falha, e desinstalar exige a frase do dono.
- **Ação:** não instalar APK de debug no aparelho. Qualquer instalação é o passo D1 ou D4 e depende da frase exata do dono.

### ERR-18: cópia do lake diverge do commit `c8e7812` em seis arquivos (ABERTO; README corrigido, PATHMAP BLOQUEADA)

- **Onde:** `kernel/lake/docs/research/CODEX2_kleaf_repro.md`, `kernel/lake/docs/research/CODEX4_dew_tree.md`, `kernel/lake/docs/research/raw/FIX8_codex_report.md`, `kernel/lake/docs/research/raw/FIX9_codex_report.md`, `kernel/lake/docs/research/raw/FIX11_codex_report.md` e `kernel/lake/docs/research/README.md`.
- **Fato:** há dois tipos de diferença. Uma é a redação de e-mails de autor de commit, registrada em `docs/history/REDACOES.txt`. A outra é a reescrita de caminhos, como o caminho do repo do lake (`<HOME>/...poco-c75-lake-kernel`), que virou `<HOME>/kernel/lake`. Além disso, `kernel/lake/pending/` não existe em `c8e7812`, e isso é esperado. O `kernel/lake/docs/research/README.md` diz "unedited output", e a cópia do repo não é.
- **Prova:** `git -C <clone do lake> archive c8e7812`, comparado com `kernel/lake/` por `diff -r`.
- **Ação:** registrar as reescritas de caminho em `docs/history/PATHMAP.md`, ou ajustar o `kernel/lake/docs/research/README.md` para dizer que a cópia tem reescritas. Não restaurar os e-mails.
- **Resultado (2026-10-14, agente de nuvem):** parcial. O `kernel/lake/docs/research/README.md` agora declara que a cópia do repo tem reescritas de caminho e e-mails redigidos, e mantém a exceção que já existia. A PATHMAP está BLOQUEADA: `git cat-file -t c8e7812` → `fatal: Not a valid object name c8e7812`. O pedido do diff ficou em `docs/PENDENCIAS-DO-DONO.md`, seção Lake. Os seis arquivos listados acima vêm de afirmação do dono e não foram verificados aqui.

### ERR-19: commit do lake `c8e7812` não publicado (DECISÃO DO DONO)

- **Onde:** `docs/PENDENCIAS-DO-DONO.md`, seção Lake.
- **Ação:** nada no repo do lake sem a frase "sim, commitar" e depois "sim, push".

### ERR-20: `AGENTS.md` diz branch e PR, mas o dono pediu commit direto em `main` nesta rodada (DECISÃO DO DONO)

- **Onde:** `AGENTS.md`, seção "Fluxo de trabalho".
- **Fato:** a rodada de 2026-10-13 foi publicada direto em `main`, sem PR, por pedido explícito do dono.
- **Ação:** o dono decide se o fluxo muda para tudo ou só para esta rodada. Até lá, o `AGENTS.md` vale, e nenhum PR é aberto sem pedido.

## Pontos a revisar (não são erros confirmados)

- O inventário do UK-6 (`docs/ultrakernel/UK6-INVENTARIO-TUNABLES.md`) tem arquivo e linha para cada opção. A revisão do dono é o critério da tarefa.
- O `.config` do build bate com o do aparelho em 6168 opções. As diferenças estão no inventário.
- O gate de KMI passou só com o clang r510928 e com a configuração de `gki_defconfig` mais o fragmento de KernelSU.
