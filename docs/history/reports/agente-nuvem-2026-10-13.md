# Relatório do agente de nuvem (rodada de 2026-10-13, executada em 2026-10-13 e 2026-10-14)

## 1. Resumo

Branch `cloud/ultraroot-ultrakernel`, a partir de `main` (`f344b169f`), sem PR. Fila: 12 itens FEITO (K2, K3, K4, K6, K7 como proposta; U1 a U7), K5 parcial com a PATHMAP BLOQUEADA (`c8e7812` ausente) e K1 com o clang pinado BLOQUEADO (proxy 403); com o clang 18.1.3 a `Image` não é reprodutível, e a causa foi identificada (chave de assinatura de módulos gerada a cada build).
UltraRoot: 5 regras puras novas em `ultraroot/core-backend` com 58 testes de host (teste primeiro: 56 falhas com stubs; depois 58 de 58), rodados num projeto Kotlin-JVM descartável que aponta para os arquivos do repo. `:app:assembleDebug` e o módulo Android ficam NÃO VERIFICADOS (SDK e AGP em `dl.google.com`, 403).
Achados no caminho, corrigidos: V67 do lake falhava com OpenSSL 3.0 (`-not_before`), e o manifesto V8 precisava de regeneração a cada commit.
Nenhum passo de aparelho. Autor e committer: o e-mail noreply do GitHub usado nos commits antigos.

## 2. Tabela

| ID | estado | prova | commit |
|---|---|---|---|
| K1 | BLOQUEADO (r510928); com clang 18.1.3: não reprodutível, causa identificada | r510928: `curl: (56) CONNECT tunnel failed, response 403`; clang 18.1.3, variáveis fixas: sha256 A `7019f26b…96156`, B `080b4fe7…69d21`; `cmp -l | wc -l` → 1088; 1068 bytes em `system_certificate_list`, 20 em `_note_41` (build-id) | `6ab9b118f` |
| K2 | FEITO (proposta) | `docs/ultrakernel/UK2-TRIM-PROPOSTA.md`; `kernel/common/BUILD.bazel:154`; reconstrução da lista → `8962` | `88d6bc3da` |
| K3 | FEITO | real: `T1-AUDITED skip`, `44 passada(s), 0 falha(s)`, código 0; `T1_SYNTH=1`: `T1-AUDITED-SYNTH: 3 de 3 caso(s) OK`, código 0; sabotagem: `divergem`, `44 passada(s), 1 falha(s)`, código 1 | `c9aa2541d` |
| K4 | FEITO | cópia com V1 forçado: `causa raiz: V1; efeitos: V68, V77`, `FAIL (3 de 116 ...)`, código 1; repo: `run_all_checks: PASS`, 116 PASS, código 0 | `c77c0f710` |
| K5 | BLOQUEADO (PATHMAP); README corrigido | `git cat-file -t c8e7812` → `fatal: Not a valid object name c8e7812` | `c5d6726d9` |
| K6 | FEITO | `bash tools/verify_repo.sh`: antes `980 referências` (982 com os arquivos novos), depois `32 referências`, `VERIFICADOR OK`; sabotagem em `README.md` → `V4 link ausente`, `VERIFICADOR FALHOU` | `1f0674f68` |
| K7 | FEITO (proposta) | `docs/ultrakernel/UK7-PROPOSTA.md`; `olddefconfig` com `CONFIG_DEFAULT_BBR=y` → `rc=0`; diff: `CONFIG_DEFAULT_TCP_CONG="cubic"` → `"bbr"` | `b39538ff2` |
| U1 | FEITO | antes: `CODE-PATHS: ok=9 falta=1 externo=4 elidido=2`, código 1; depois: `CODE-PATHS: ok=10 falta=0 externo=4 elidido=2`, código 0 | `2c43391cf` |
| U2 | FEITO | teste primeiro (stubs): `SelfUpdatePolicyTest` 13 falhas, 1 passa; implementado: 14 de 14 PASSED (projeto JVM descartável, `ultraroot/gradlew -p <scratch>/jvm test`, BUILD SUCCESSFUL); sabotagem (sem checar cert): `rejectsDivergentCert FAILED`; `:app:assembleDebug` NÃO VERIFICADO (dl.google.com 403) | `ba40a2897` |
| U3 | FEITO | teste primeiro (stubs): `BackupRegistryTest` 13 de 13 FAILED; implementado: 13 PASSED, BUILD SUCCESSFUL; hash trocado → `Refused(HASH_MISMATCH)` (`swappedHashIsRefused`, `dryRunWithSwappedBytesIsRefused`) | `8ea51bb1e` |
| U4 | FEITO | teste primeiro (stubs): `OpLogTest` 9 FAILED, 1 PASSED; implementado: 10 PASSED; senha fictícia redigida (`keyValuePasswordIsRedacted`, `cliPasswordFlagIsRedacted`), chave PEM fictícia recusada (`privateKeyMaterialIsRefused`); `MAX_BYTES = null` (UNKNOWN); sabotagem: `privateKeyMaterialIsRefused FAILED` | `3b2ced83e` |
| U5 | FEITO | teste primeiro (stubs): `ModulePropTest` 10 FAILED; implementado: 10 PASSED; spec `kernel/upstream/KernelSU/website/docs/guide/module.md:100-121`; malformado recusado (`BAD_ID`, `ID_MISSING`, `BAD_VERSION_CODE`, `CR_LINE_BREAK`, `NO_SEPARATOR`, `DUPLICATE_KEY`) | `99280558c` |
| U6 | FEITO | teste primeiro (stubs): `ModulePackageClassifierTest` 11 FAILED; implementado: 11 PASSED; zip slip (`../`, absoluto, barra invertida) → `UNSAFE_ENTRY`; regras com fonte em `kernel/upstream/KernelSU` (module.rs:641-644, metamodule.rs:19-24, installer.sh:331-333); Magisk/APatch NÃO VERIFICADO; sabotagem: `zipSlipIsRefused FAILED` | `026b3197c` |
| U7 | FEITO | `docs/ultraroot/TODOs.md`: [x] só em 4 subitens com teste de host; demais [~] com NÃO VERIFICADO; `bash tools/check_code_paths.sh` → `CODE-PATHS: ok=15 falta=0 externo=4 elidido=2` | commit "Marca status honesto..." (último da branch) |

## 3. Bloqueios e decisões novas (registrados em `docs/PENDENCIAS-DO-DONO.md`)

- clang r510928: `curl -sI https://android.googlesource.com/...` → `curl: (56) CONNECT tunnel failed, response 403`.
- SDK Android: `curl -sI https://dl.google.com/android/repository/repository2-3.xml` → `CONNECT tunnel failed, response 403`.
- `:app:assembleDebug`: `HTTP 403: https://dl.google.com/dl/android/maven2/com/android/application/com.android.application.gradle.plugin/9.4.1/...pom` e `HTTP 429` em `repo.maven.apache.org`. Antes disso, `org.gradle.java.home=/usr/lib/jvm/java-21-openjdk` de `ultraroot/gradle.properties` não existe aqui (`Java home supplied is invalid`).
- services.gradle.org: não bloqueou (`curl -sI .../gradle-9.7.1-bin.zip` → `HTTP/1.1 307 Temporary Redirect`).
- Commit `c8e7812` do lake: `git cat-file -t c8e7812` → `fatal: Not a valid object name c8e7812`. Pedido do diff para a PATHMAP.
- Decisões novas: chave fixa para o teste de reprodutibilidade (ERR-13); proposta do UK-7; proposta do trim (UK-2); caminho do JDK em `gradle.properties`.

## 4. NÃO VERIFICADO

- `Image` pinada (r510928) reprodutível; build com chave de assinatura fixa.
- Build com `TRIM_UNUSED_KSYMS=y` e gate de KMI depois dele; conteúdo da lista reconstruída contra o `abi_symbollist.raw` oficial; regra interna do Kleaf.
- Build da `Image` com o fragmento do UK-7 e gate de KMI.
- PATHMAP do lake (seis arquivos do ERR-18).
- `:core-backend` como módulo Android, `:app:assembleDebug`, integração das regras novas com tela, instalação e aparelho.
- Regras de classificação de Magisk e APatch (APM, KPM): sem fonte no repo.

## 5. Como revisar

Branch `cloud/ultraroot-ultrakernel`. Commits em ordem: `88d6bc3da` (K2), `c5d6726d9` (K5), `2c43391cf` (U1), `c77c0f710` (K4), `c9aa2541d` (K3), `1f0674f68` (K6), `b39538ff2` (K7), `ba40a2897` (U2), `8ea51bb1e` (U3), `3b2ced83e` (U4), `99280558c` (U5), `026b3197c` (U6), `6ab9b118f` (K1), e o último (U7 e este relatório).

Comandos:

- Verificador: `bash tools/verify_repo.sh` (código 0; `AVISO V4 ... 32 referências`).
- U1: `bash tools/check_code_paths.sh`.
- K4: `cd kernel/lake && bash tools/run_all_checks.sh` (116 PASS, código 0); para a sabotagem, copiar `kernel/lake` para `<scratch>`, inserir `bad "sabotagem"` depois da linha 24 de `tools/check_docs_numbers.sh` e rodar de novo.
- K3: `bash kernel/lake/tools/selftest_fastboot_guard.sh`; `T1_SYNTH=1 bash ...`; `T1_SYNTH=1 T1_SYNTH_SABOTAGE=1 bash ...`.
- K2: comando de contagem em `docs/ultrakernel/UK2-TRIM-PROPOSTA.md` (8962).
- K7: comandos em `docs/ultrakernel/UK7-PROPOSTA.md`.
- K1: `make -C kernel/common O=<scratch>/out ARCH=arm64 LLVM=1 gki_defconfig`, `merge_config.sh` com `kernelsu.fragment`, `olddefconfig`, `prepare`, `Image`, duas vezes, com as variáveis `KBUILD_BUILD_*` do ERR-13; `sha256sum`, `cmp -l | wc -l`, e `llvm-nm -n vmlinux` para mapear os offsets (base `_text` = `ffffffc080000000`).
- U2 a U6: projeto Kotlin-JVM em `<scratch>/jvm` com `kotlin("jvm") version "2.4.20"`, `sourceSets` apontando para `ultraroot/core-backend/src/main/java` e `src/test/java` (só os 5 arquivos novos e seus testes), JUnit 4.13.2, repositório `https://maven-central.storage-download.googleapis.com/maven2` (o Maven Central respondeu 429); `ultraroot/gradlew -p <scratch>/jvm test`.

revisão pendente
