# Pendências do dono

Cada item diz o que falta e a frase exata que libera o passo. Nenhum agente executa um passo destes sem a frase.

## Aparelho (D0 a D7)

- **D1, instalar o ui10:** "sim, instalar ui10" (`docs/ultraroot/plan.md`, linha 92). Antes de instalar, conferir o sha256 do APK (`out/`, fora do repo).
- **D2, diagnóstico do fd (leitura):** "sim, diagnóstico do fd" (`docs/ultraroot/plan.md`, linha 93). Libera a decisão E2.
- **D3, log de boot do C0.3:** frase na linha 94 de `docs/ultraroot/plan.md`.
- **D4, grant de teste:** "sim, grant teste" (`docs/ultraroot/plan.md`, linha 95).
- **D5, migrar módulos nativos:** "sim, migrar <módulo>" (`docs/ultraroot/plan.md`, linha 96), um por vez.
- **D6 e D7 (flash do SP1 e drill de rollback):** linhas 97 e 98 de `docs/ultraroot/plan.md`. Só com backup conferido, slot B e guard.

## Decisões (E1 a E6)

- **E1, copy e layout dos cartões e da busca:** layout DECIDIDO em 2026-10-26: Material Design 3 (o app já usa `androidx.compose.material3`). Copy: o agente assumiu a opção A de `docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md` (a recomendada); confirmar A ou trocar por B. Libera H7.
- **E2, origem do fd:** decidir depois do D2 (`docs/ultraroot/plan.md`, linha 105). Libera a chamada de `setAppProfile` na tela.
- **E3, ordem com o Magisk:** `docs/ultraroot/plan.md`, linha 106.
- **E4, aceite do grant sem o Magisk desligado:** `docs/ultraroot/plan.md`, linha 107. Até decidir, "su via KSU" continua não provado.
- **E5 e E6:** `docs/ultraroot/plan.md`, linhas 108 e 109.

## Lake

- **Commit do lake:** "sim, commitar" para `c8e7812` (hoje só local).
- **Push do lake:** "sim, push", depois de revisão brutal do diff.
- **PATHMAP do lake (ERR-18, K5):** o commit `c8e7812` não existe no ambiente de nuvem (`git cat-file -t c8e7812` → `fatal: Not a valid object name c8e7812`). Pedido: rodar no clone do lake `git -C <clone do lake> archive c8e7812 | tar -x -C <scratch>/lake-c8e7812` e `diff -r <scratch>/lake-c8e7812 kernel/lake`, e colar em `docs/history/PATHMAP.md` as reescritas de caminho, por arquivo e por tipo, sem os e-mails.
- **Diff de trabalho:** `kernel/lake/pending/diff-nao-commitado-2026-10-13.diff` não está commitado. Decidir se entra.

## Repo

- **Licença:** nenhuma definida. Todos os direitos reservados. Opções: MIT ou Apache-2.0 para o código próprio. Código do KernelSU em `kernel/upstream/` mantém a licença original.
- **Capturas de tela do aparelho:** duas PNG ficaram fora do repo (ver `docs/history/evidence/NAO-INCLUIDO.txt`). Podem conter notificações ou dados pessoais. Se quiser incluir, depois de você olhar.
- **Correção de redação (publicada em `main`):** a fingerprint e o serial do certificado público do GKI, redigidos por engano em 5 linhas do lake, foram restaurados. A regra V3 do verificador foi corrigida e testada. Detalhes em `docs/ERROS-PARA-REVISAO.md`, ERR-01 e ERR-02.
- **E-mail pessoal em três commits publicados (decisão sua):** `901a32718`, `739838c3a` e `0f3fbe3c6` têm o seu e-mail pessoal no autor. Trocar isso exige reescrever o histórico público, com force push. Não faço sem a frase "sim, reescrever histórico". Detalhes em `docs/ERROS-PARA-REVISAO.md`, ERR-03.
- **Fluxo de PR (decisão sua):** o `AGENTS.md` diz branch e PR. Nesta rodada, você pediu commit direto em `main`. Detalhes em `docs/ERROS-PARA-REVISAO.md`, ERR-20.

## Ambiente do agente de nuvem (2026-10-14)

- **clang r510928:** `android.googlesource.com` recusado pelo proxy (`curl: (56) CONNECT tunnel failed, response 403`). Builds da rodada usaram o clang 18.1.3 do sistema. Liberar o host no ambiente, ou entregar o toolchain, para repetir K1 com o clang pinado.
- **SDK Android e AGP:** `dl.google.com` recusado (`CONNECT tunnel failed, response 403`); `./gradlew :app:assembleDebug` falha com `HTTP 403: https://dl.google.com/dl/android/maven2/com/android/application/com.android.application.gradle.plugin/9.4.1/...` e `HTTP 429` no Maven Central. `:core-backend` (módulo Android) e `:app:assembleDebug` ficam NÃO VERIFICADOS nesta rodada; a lógica pura nova foi testada num projeto Kotlin-JVM descartável que aponta para os arquivos do repo.
- **JDK no `ultraroot/gradle.properties`:** `org.gradle.java.home=/usr/lib/jvm/java-21-openjdk` não existe neste ambiente (`Java home supplied is invalid`); aqui o caminho é `java-21-openjdk-amd64`. Decidir se o caminho sai do arquivo versionado.

## UltraKernel

- **Chave de assinatura de módulos (ERR-13):** DECIDIDO em 2026-10-26 ("sim"). Chave fixa gerada fora do repo, em `[chaves locais do dono, fora do repo]/ultrakernel_modsign.pem`, permissão 600, sem senha (o kernel exige chave sem senha). Guardar cópia de segurança: sem ela, módulo novo não usa os exports protegidos.
- **Flash do ULTRA-kernel (D6, slot B):** `boot_b_ultra.img` (v6, sha256 `36bc72755e82152f510d298dd1ac1641b604143abbec24c4876c5f1a5cf0d85e`; plano em `docs/ultrakernel/UK-ULTRA-PLANO-BOOT.md`) montado no host a partir do backup auditado (`3890fb96…829fc7`), fora do repo. Antes do flash: D0 (preflight), e incluir esse hash na allowlist do guard (`docs/ultraroot/guard_hashes_sp1.txt` aponta para o C0.3; o agente não mexe na allowlist sem pedido). Flash só com "sim, flash do ULTRA-kernel no boot_b", depois de D0 e do guard.

- **Revisão do inventário (UK-6):** `docs/ultrakernel/UK6-INVENTARIO-TUNABLES.md`. Libera a escolha de opções para o UK-7.
- **Proposta do UK-7:** `docs/ultrakernel/UK7-PROPOSTA.md` (só `CONFIG_DEFAULT_BBR=y`; o resto fica como está, por compatibilidade com os módulos de vendor). Aprovar libera gravar `arch/arm64/configs/ultrakernel.fragment`, compilar e rodar o gate de KMI.
- **TRIM_UNUSED_KSYMS:** o build deixa a opção `n`, e o config do aparelho tem `y`. Decidir se o build deve igualar o aparelho. Se sim, o gate de KMI (UK-4) roda de novo. Detalhes na seção "Diferenças" do inventário. Proposta de mudança no build `make`: `docs/ultrakernel/UK2-TRIM-PROPOSTA.md`.
- **Variante do aparelho (UK-0):** o modelo `2410FPCC5G` tem final `5G`, e o pedido fala em 4G. Confirmar qual é o aparelho antes de qualquer build para ele.
