# Pré-requisitos: o que o repo tem, o que falta e o que foi testado

Para o agente em nuvem: este repo tem o código e a lista de tarefas. Para compilar, ele precisa das ferramentas abaixo.

## Está no repo (completo)

- `kernel/common/`: árvore GKI no commit base `5a0ffb4`, com o delta do KernelSU e a correção do libbpf (82.342 arquivos). Sem `.git` e sem artefatos de build.
- `kernel/lake/`: snapshot completo do lake no commit `c8e7812` (ferramentas, dados, docs).
- `kernel/upstream/KernelSU/`: árvore inteira do KernelSU no commit `6e38604` (679 arquivos).
- `ultraroot/`: app Compose, core-backend (Kotlin, C++ e CMake), wrapper do Gradle e teste nativo.
- `docs/`: plano, TODOs de cada parte, estado, pendências do dono e histórico.
- `tools/verify_repo.sh`: verificador. Deve sair com código 0.

## Precisa ser instalado na máquina do agente

- Host: `bc`, `flex`, `bison`, `perl`, `openssl` (cabeçalhos), `libelf`, `pahole`, `make`. No Arch: `pacman -S bc flex bison perl openssl libelf pahole`.
- Toolchain do GKI: clang `r510928` (Android, versão 18.0.0). Não está no repo porque é grande. Nesta máquina ele está em `~/lake-build/src/prebuilts/clang/host/linux-x86/clang-r510928/`. O clang do sistema (23.1.1) também foi testado, mas o GKI deve ser compilado com o clang pinado, para o KMI bater com os módulos do aparelho.
- UltraRoot: JDK 21, Android SDK com `compileSdk` 37 e build-tools 37.0.0, NDK `29.0.14206865`, CMake `3.22.1`. A primeira compilação precisa de rede para baixar as dependências. Depois, `--offline` funciona com o cache.

## Fora do repo, de propósito

- Workspace Bazel/Kleaf do Android (`build/`, `prebuilts/`, `external/`). O `BUILD.bazel` do delta só funciona com ele. O caminho reproduzível no repo é o `make`.
- Chaves de assinatura (`keys/`). Só o dono tem.
- Imagens de boot stock, `dtbo` e backups do aparelho. São do dono (UK-8, UK-12).
- Magisk (5,1 GB), `backups/` e `scratch/`. Não são usados para compilar o kernel nem o app.
- Aparelho: não existe na nuvem. Qualquer passo de aparelho só com "sim, <passo>".

## Testado a partir do clone publicado

- `tools/verify_repo.sh`: VERIFICADOR OK.
- Kernel: `gki_defconfig` + `kernelsu.fragment` + `olddefconfig`, com `CONFIG_KSU=y`.
- Kernel: `make prepare` com o clang `r510928`, depois da correção do libbpf e com `bc` instalado: exit 0.
- Kernel: `make Image` (clang r510928, `-j6`): exit 0, sem avisos. Gera `arch/arm64/boot/Image` (36 MB, sha256 `1fe3d6b8…7861a`) e `vmlinux` (180 MB). Tempo medido: cerca de 9 minutos com 6 núcleos (log de 19:12 a 19:21).
- UltraRoot: `./gradlew :core-backend:testDebugUnitTest --offline` (BUILD SUCCESSFUL) e `./gradlew :core-backend:externalNativeBuildDebug --offline`, que gerou `libultraroot.so` (arm64-v8a).

## Erros encontrados nos testes (e o que foi feito)

- `make prepare` falhava em `tools/bpf/resolve_btfids` com `-Werror` (ponteiro const descartado em `libbpf.c`). Correção de uma linha, em `kernel/common/tools/lib/bpf/libbpf.c`, com diff em `kernel/patches/host-libbpf-const-char.diff`. O BTF não foi desligado: o aparelho usa `CONFIG_DEBUG_INFO_BTF=y`.
- `bc` não estava instalado: a compilação do kernel precisa dele. Instalado.
