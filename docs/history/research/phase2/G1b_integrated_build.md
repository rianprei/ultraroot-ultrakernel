# G1b — build de validação do KernelSU in-tree

Data: 2026-10-10. Local. Sem device, sem `insmod`, sem `//common:kernel_aarch64_dist`.

Commit do alvo: tiann/KernelSU `6e3860443f8fea7c5fc226277e53888033796488`. Checkout lido: `kernel/upstream/KernelSU`.

Veredito: **BLOQUEADO**. O Kleaf aceitou a fiação e gerou `CONFIG_KSU=y`. O link do `vmlinux` não foi iniciado: a máquina já estava sem swap livre.

## Onde parou

Depois de `//common:kernel_aarch64_config` (exit 0, 31,258 s, 315 actions, critical path 9,75 s). Antes de lançar `//common:kernel_aarch64`, a ação que compila o kernel e linka o `vmlinux`.

Nesse instante:

- `MemAvailable` = 1592528 kB
- `SwapFree` = 3776 kB
- uma leitura seguinte: `MemAvailable` 1537544 kB, `SwapFree` 2768 kB
- dois `rustc` com RSS 2127508 kB e 1473432 kB

Objetos do KernelSU não foram compilados. `nm` do `vmlinux` não existe. `gate_kmi_crc.sh` não rodou.

## Isolamento

Cópia por reflink, não worktree git: a árvore Kleaf é um `repo` de vários gitlinks, não um repositório único.

```
cp -a --reflink=always
```

Origem `~/lake-build/src`. Destino `[artefato local, fora do repo]/grok/ksu-build/`. Mesmo filesystem btrfs (`/dev/nvme1n1p2[/@home]`).

Entrou na cópia: `.repo`, `build`, `common`, `common-modules`, `external`, `kernel`, `prebuilts`, `tools`, e os symlinks `.bazelignore`, `MODULE.bazel`, `WORKSPACE`, `WORKSPACE.bzlmod`.

Ficou de fora: `out/` (produtos bazel anteriores, 5,9 G) e os symlinks `bazel-bin`, `bazel-out`, `bazel-src`, `bazel-testlogs` (apontam para o `out/` pristino).

Na criação, `btrfs filesystem du -s` da cópia: total 11,73 GiB, exclusive 0, shared 8,53 GiB. `common/.git` continua symlink para `../.repo/projects/common.git` dentro da cópia.

Depois do config: exclusive 249,68 MiB. Abaixo de ~10 GB de disco novo. A cópia ficou.

A árvore pristina não foi editada. SHA256 de `common/drivers/Makefile`, `common/drivers/Kconfig` e `arch/arm64/configs/gki_defconfig` em `~/lake-build/src` bateu com o snapshot de antes da cópia, medido de novo depois da fiação e depois do config.

A cópia herdou sujeira que já estava na origem, e que este passo não criou:

- `common`: `M BUILD.bazel`, `?? google_gki_ab13771415_modsign_cert.pem`
- `build/kernel`: `M kleaf/common_kernels.bzl`

É o patch do cert do Google já aplicado na working tree. O build de config rodou em cima disso.

## Arquivos tocados (só na cópia)

O guia que este pin ainda carrega é `kernel/setup.sh`: symlink de `KernelSU/kernel` em `drivers/kernelsu`, uma linha no `drivers/Makefile`, um `source` no `drivers/Kconfig`. O `native.glob(["**"])` de `common_kernel_sources` (`build/kernel/kleaf/common_kernels.bzl:528-530`) precisa dos fontes dentro de `common/`. A fiação foi uma cópia dos arquivos, não um symlink para fora do pacote.

| Arquivo na cópia | O que entrou |
|---|---|
| `common/drivers/kernelsu/` | cópia de `KernelSU/kernel`, 90 arquivos, 696 K. `Kbuild` tem precedência sobre `Makefile` (`scripts/Kbuild.include:64-66`). |
| `common/drivers/Makefile:203` | `obj-$(CONFIG_KSU) += kernelsu/` |
| `common/drivers/Kconfig:246` | `source "drivers/kernelsu/Kconfig"` imediatamente antes do `endmenu` da linha 248 |

`arch/arm64/configs/gki_defconfig` não ficou alterado. `check_defconfig` (`build/kernel/_setup_env.sh:228-243`) faz `diff` do `savedefconfig` contra esse arquivo, e o `savedefconfig` omite símbolo que já está no default. `CONFIG_KSU=y` no defconfig quebraria esse diff. O default do Kconfig foi deixado como está (`kernel/Kconfig` do pin: `default y`, `depends on KPROBES && EXT4_FS`).

`git diff --stat` na cópia, fora da sujeira herdada: `drivers/Kconfig` +2, `drivers/Makefile` +2, mais `?? drivers/kernelsu/`.

## Config

Comando, a partir de `[artefato local, fora do repo]/grok/ksu-build`:

```
SOURCE_DATE_EPOCH=1752273969
tools/bazel build --config=stamp --local_cpu_resources=2 --local_ram_resources=2560 //common:kernel_aarch64_config
```

O projeto usa o mesmo `--config=stamp` e o mesmo `SOURCE_DATE_EPOCH` (`scripts/build.sh:18` e `:131-132` de `poco-c75-lake-kernel`). O alvo aqui é o config, não o `_dist`. CPU e RAM foram limitados: o default do script é `nproc` e 8000 MB, e o host estava em ~2,6 GiB disponíveis no lançamento.

Log: `[artefato local, fora do repo]/grok/notes/g1b-config.log`.

```
INFO: Analyzed target //common:kernel_aarch64_config (62 packages loaded, 97621 targets configured).
INFO: Elapsed time: 31.258s, Critical Path: 9.75s
INFO: 315 processes: 269 internal, 46 linux-sandbox.
INFO: Build completed successfully, 315 total actions
```

Avisos no mesmo log, iguais em forma ao build de controle já visto: `repo` ausente no PATH do workspace status; `SOURCE_DATE_EPOCH` do diretório `common` caiu para 0 dentro do sandbox (`Unable to determine SOURCE_DATE_EPOCH for [tmp]/bazel-working-directory/_main/common, fallback to 0`). O `export` do epoch foi feito na casca. O sandbox não o usou para esse aviso.

`.config` gerado:

`ksu-build/out/bazel/output_user_root/5c8672881d94e891deb8966976ba8ef4/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64_config/out_dir/.config`

```
6698|# KernelSU
6700|CONFIG_KSU=y
6701|# CONFIG_KSU_DEBUG is not set
6702|# CONFIG_KSU_DISABLE_MANAGER is not set
6703|# CONFIG_KSU_DISABLE_POLICY is not set
```

A mesma stanza está no `.config` do sandbox `KernelConfig/138` (`out/android15-6.6/common/.config:6698-6704`). O alvo terminou sem `ERROR: savedefconfig does not match`. O Kconfig entrou e o `check_defconfig` passou.

## O que não rodou

`//common:kernel_aarch64` não foi lançado. Esse alvo é o `kernel_build` cujo `outs` inclui `vmlinux` e `vmlinux.symvers` (`build/kernel/kleaf/constants.bzl:24-29`) e cujos `make_goals` são `Image`, `Image.lz4`, `Image.gz`, `modules` (`common/BUILD.bazel:44-49`). É a ação `Building kernel (lto=default;trim)`. O `_dist` empacota; ficou de fora de propósito.

Sem `vmlinux`, não há símbolo `ksu_*` para mostrar. Sem `vmlinux.symvers` / `Module.symvers` deste build, o gate não tem entrada. O comando que o projeto usa, e que seria o próximo, é:

```
kernel/lake/tools/gate_kmi_crc.sh <symvers deste build>
```

Um argumento. A referência default é `data/official-vmlinux.symvers`. O PASS histórico do controle e do cert é `compared=2309 mismatches=0 missing_exports=0`.

## Veredito

**BLOQUEADO** no passo do link. A fiação in-tree foi aceita pelo Kleaf e `CONFIG_KSU=y` está no `.config` gerado. Falta compilar os objetos do KernelSU, linkar o `vmlinux`, achar um símbolo `ksu_*` nele, e rodar o gate dos 557 módulos. Isso pede o alvo `//common:kernel_aarch64` com swap livre.
