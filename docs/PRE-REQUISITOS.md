# Pré-requisitos: o que o repo tem, o que falta e o que foi testado

Para o agente em nuvem: este repo tem o código e a lista de tarefas. Para compilar, ele precisa das ferramentas abaixo.

## Está no repo (completo)

- `kernel/common/`: árvore GKI no commit base `5a0ffb4`, com o delta do KernelSU e a correção do libbpf: 82.342 arquivos regulares e 64 links simbólicos. Sem `.git` e sem artefatos de build.
- `kernel/lake/`: snapshot completo do lake no commit `c8e7812` (ferramentas, dados, docs).
- `kernel/upstream/KernelSU/`: árvore do KernelSU no commit `6e38604`: 678 arquivos regulares e 2 links. Os blobs git dos 678 arquivos foram conferidos contra o commit. `PIN.md` traz o sha256 de sete deles.
- `ultraroot/`: app Compose, core-backend (Kotlin, C++ e CMake), wrapper do Gradle e teste nativo.
- `docs/`: plano, TODOs de cada parte, estado, pendências do dono e histórico.
- `tools/verify_repo.sh`: verificador. Deve sair com código 0.

## Precisa ser instalado na máquina do agente

- Host: `bc`, `flex`, `bison`, `perl`, `openssl` (cabeçalhos), `libelf`, `pahole`, `make`. No Arch: `pacman -S bc flex bison perl openssl libelf pahole`.
- Toolchain do GKI: clang `r510928` (Android, versão 18.0.0). Não está no repo porque é grande. Nesta máquina ele está em `~/lake-build/src/prebuilts/clang/host/linux-x86/clang-r510928/`. O clang do sistema (23.1.1) também foi testado, mas o gate de CRC (UK-4) só foi conferido com o clang pinado.
- UltraRoot:
  - JDK 21. Se o JDK padrão da máquina for outro, defina `JAVA_HOME`. Nesta máquina o padrão é o 25.
  - Android SDK em `ANDROID_HOME`, com a plataforma `android-37.0` e as build-tools `37.0.0`. O repo não traz `local.properties`.
  - NDK `29.0.14206865` e CMake `3.22.1`.
  - A primeira compilação precisa de rede, sem `--offline`, para baixar a distribuição do Gradle e as dependências. Depois disso, `--offline` funciona com o cache.

## Fora do repo, de propósito

- Workspace Bazel/Kleaf do Android (`build/`, `prebuilts/`, `external/`). O `BUILD.bazel` do delta só funciona com ele. O caminho reproduzível no repo é o `make`.
- Chaves de assinatura (`keys/`). Só o dono tem.
- Imagens de boot stock, `dtbo` e backups do aparelho. São do dono (UK-8, UK-12).
- Magisk (5,1 GB), `backups/` e `scratch/`. Não são usados para compilar o kernel nem o app.
- Aparelho: não existe na nuvem. Qualquer passo de aparelho só com "sim, <passo>".

## Testado a partir do clone publicado (commit `0f3fbe3c6`, 2026-10-13)

- `tools/verify_repo.sh`: VERIFICADOR OK na árvore corrigida (ver "Erros" abaixo).
- Kernel: `gki_defconfig` + `kernelsu.fragment` + `olddefconfig`, com `CONFIG_KSU=y`.
- Kernel: `make prepare` e `make Image` com o clang `r510928` e `-j6`: exit 0, 0 avisos. `Image` de 36.395.520 bytes, sha256 `5d50b74c955cd7e2ab007f72eb5b6ce02f9f3e5325f637cf85ce02bbb8ebb00d`. `vmlinux` de 179.941.272 bytes. Tempo: 11 minutos com 6 núcleos, com outras verificações rodando em paralelo. Relatório: `docs/history/reports/uk2_image_clone_2026-10-13.txt`.
- Kernel, gate de CRC (UK-4): `gate_kmi_crc.sh` com o `vmlinux.symvers` desse build dá PASS, com 2309 símbolos comparados e 0 divergências. Relatório: `docs/history/reports/uk4_gate_kmi_crc_2026-10-13.txt`.
- Kernel, reprodutibilidade: a `Image` de 2026-10-13 difere em 1153 bytes da `Image` de um build anterior, de mesmo tamanho. A única diferença de fonte entre as duas árvores é o `libbpf.c` (ferramenta do host). A causa exata da diferença não foi verificada. Este repo não garante `Image` idêntica bit a bit entre builds.
- UltraRoot, cache Gradle vazio: `:core-backend:testDebugUnitTest` com 20 de 20 testes passando, `:app:testDebugUnitTest` com 41 de 41 e `:app:assembleDebug` gerando o APK de debug (30 MB). O `build.gradle.kts` não define `signingConfig`, então a assinatura é a de debug padrão do AGP. Não conferi isso com `apksigner`, porque o clone foi apagado depois do teste. Os comandos documentados com `--offline` também passam. Relatório: `docs/history/reports/app_gradle_clone_2026-10-13.txt`.
- UltraRoot, teste nativo no host: 12 de 12 PASS, pelo verificador (V6).

## Erros encontrados nos testes (e o que foi feito)

- `make prepare` falhava em `tools/bpf/resolve_btfids` com `-Werror` (ponteiro const descartado em `libbpf.c`). Correção de uma linha, em `kernel/common/tools/lib/bpf/libbpf.c`, com diff em `kernel/patches/host-libbpf-const-char.diff`. O BTF não foi desligado: o aparelho usa `CONFIG_DEBUG_INFO_BTF=y`.
- `bc` não estava instalado: a compilação do kernel precisa dele. Instalado.
- A redação de dado pessoal da primeira montagem trocou a fingerprint pública do cert do GKI por `[redigido: mac]` em 5 linhas do lake. O `run_all_checks` do lake falhava em 3 de 116 (V1, V68, V77). Corrigido e publicado em `main`: as linhas foram restauradas, e a regra de MAC do verificador ganhou borda e teste. Ver `docs/history/REDACOES.txt`. Ver `docs/ERROS-PARA-REVISAO.md`, ERR-01.
