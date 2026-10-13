# UltraRoot e UltraKernel

Repositório único com o plano, as tarefas, o histórico e o código do **UltraRoot** (gerenciador de root) e a visão do **UltraKernel** (kernel customizado, depois do UltraRoot). O objetivo é que um agente de IA que só enxerga este repositório consiga retomar o trabalho sem nenhum outro contexto.

## O que é cada parte

- **UltraRoot:** gerenciador de root em Jetpack Compose (Material 3) sobre o KernelSU integrado ao kernel "lake" do POCO C75 (codename `lake`, kernel `6.6.89-android15-8-4k`). Fonte: `docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md`, linhas 4 e 8.
- **UltraKernel:** visão futura de kernel com ajuste de desempenho e overclock, feita só depois do UltraRoot. Status: **não iniciado**. Fonte: `docs/ultrakernel/SUPERKERNEL_VISION.md`, linha 29.

## Estado (2026-10-13)

Os números completos estão em `docs/ultraroot/plan.md` (§9, estado de execução) e `docs/ESTADO.md`. Resumo:

- **Fechados no host:** H1 (flags do GET_INFO), H2 (grant e revoke, só host), H3 (build ui10, assinado em V2), H4 (documentação), H5, H6, H8 (escrita JNI de `SET_APP_PROFILE`, sem chamada na tela) e as entregas L1 e L2.
- **Bloqueado pelo dono:** H7 depende da escolha de copy do E1. As duas opções estão em `docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md`.
- **Aguardando "sim, <passo>":** D1 (instalar o ui10 no aparelho). Os passos D0 a D7 só rodam com a frase exata do dono. Lista em `docs/PENDENCIAS-DO-DONO.md`.

## Como construir (host)

O projeto Gradle fica em `ultraroot/`.

- Gradle 9.7.1 pelo wrapper (`ultraroot/gradle/wrapper/gradle-wrapper.properties`).
- JDK 21 (`ultraroot/app/build.gradle.kts`, `sourceCompatibility`).
- SDK: `compileSdk` e `targetSdk = 37`, `minSdk = 31` (`ultraroot/app/build.gradle.kts`).
- NDK `29.0.14206865` e CMake `3.22.1` (`ultraroot/core-backend/build.gradle.kts`).
- Comandos usados nas conferências (ver `docs/history/reports/h8r1_errno_2026-10-13.txt`, T4 e T5):

```
cd ultraroot
./gradlew :core-backend:externalNativeBuildDebug --offline
./gradlew :core-backend:testDebugUnitTest --offline
```

- Teste nativo no host (sem Android), a partir da raiz do repo:

```
g++ -std=c++17 -Wall -Werror -I ultraroot/core-backend/src/test/native/shim \
    -I ultraroot/core-backend/src/main/cpp \
    ultraroot/core-backend/src/test/native/fill_test.cc -o build/fill_test && ./build/fill_test
```

- Verificador do repo: `tools/verify_repo.sh` (segredos, dados pessoais, caminhos locais, links internos, integridade do pin do KernelSU, teste nativo e manifesto).

## Segurança e limites

- Legítimo, sem evasão e sem ocultação indetectável: nada de DenyList, hide ou umount por perfil.
- Chaves de assinatura não entram no repo. A senha nunca é lida, impressa nem gravada.
- Boot só no slot B, nunca no A.
- Nenhum passo de aparelho sem a frase exata do dono ("sim, <passo>").

## Licença

Nenhuma licença definida. Todos os direitos reservados. A escolha é decisão do dono (ver `docs/PENDENCIAS-DO-DONO.md`).

## Por onde começar

1. `AGENTS.md`: regras de trabalho para agentes.
2. `docs/ESTADO.md`: estado atual e o que está bloqueado.
3. `docs/README.md`: índice de todos os documentos.
