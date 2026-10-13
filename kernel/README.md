# kernel/

Três partes, todas dentro do repo:

- `common/`: árvore GKI no commit base `5a0ffb4`, com o delta nosso. Sem o `.git` e sem artefatos de build. Licença: GPL-2.0 (ver `common/COPYING`). Tem 82 mil arquivos (1,5 GB).
- `patches/ultrakernel-delta-base-5a0ffb4.diff`: o delta modificado (`BUILD.bazel`, `drivers/Kconfig`, `drivers/Makefile`).
- `lake/`: snapshot do repo do lake no commit `c8e7812` (ferramentas, dados e documentação). O remoto público do lake está em `ffe583a`. O commit `c8e7812` ainda não foi publicado.
- `upstream/KernelSU/`: arquivos do KernelSU no commit `6e3860443f8fea7c5fc226277e53888033796488`. Hashes em `PIN.md`.

## Delta nosso sobre o base

- Modificados: `BUILD.bazel`, `drivers/Kconfig` (`source "drivers/kernelsu/Kconfig"`), `drivers/Makefile` (`obj-$(CONFIG_KSU) += kernelsu/`).
- Novos: `drivers/kernelsu/` (96 arquivos, KernelSU integrado), `arch/arm64/configs/kernelsu.fragment` (`CONFIG_KSU=y`), `google_gki_ab13771415_modsign_cert.pem` (certificado público do GKI).
- Verificado no host: `gki_defconfig` + `kernelsu.fragment` + `olddefconfig` resulta em `CONFIG_KSU=y`.

## Fora do repo, de propósito

- Imagens de boot (`.img`), a `out/` do Bazel e a chave de teste do SGX (a chave de teste do SGX, bloqueada pelo GitHub e sem efeito no build). Ver `docs/history/EXCLUSOES.txt`.
- Os `dtbo` e as imagens stock do aparelho: ficam com o dono.

## Como montar um build

Sem o toolchain do lake, qualquer clang 17 ou mais novo serve para `gki_defconfig`. Sempre fora da árvore:

```
make -C kernel/common O=/caminho/fora/saida ARCH=arm64 LLVM=1 gki_defconfig
kernel/common/scripts/kconfig/merge_config.sh -m -O /caminho/fora/saida /caminho/fora/saida/.config kernel/common/arch/arm64/configs/kernelsu.fragment
make -C kernel/common O=/caminho/fora/saida ARCH=arm64 LLVM=1 olddefconfig
```

A tarefa completa do kernel está em `docs/ultrakernel/TODOS.md`.
