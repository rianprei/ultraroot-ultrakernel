# C0.1 — build do vmlinux com KernelSU

Data da corrida que passou: 2026-10-11. Pin KernelSU `6e3860443f8fea7c5fc226277e53888033796488`.

A pristina `~/lake-build/src` não foi editada. O build foi na cópia reflink `[artefato local, fora do repo]/grok/c01-build/src`. O `git status` da pristina continua só com o que já estava antes: `M BUILD.bazel` e `?? google_gki_ab13771415_modsign_cert.pem`.

## O que foi editado na cópia

- `common/drivers/kernelsu/` — cópia de `KernelSU/kernel/` (90 arquivos, `Kbuild` preservado) mais `uapi/` (6 headers do mesmo pin). O `Kbuild` inclui `"uapi/app_profile.h"` com `-I$(KSU_KERNEL_DIR)`. Sem esses headers o primeiro link parou em `policy/allowlist.h:6`.
- `common/drivers/Makefile` — `obj-$(CONFIG_KSU) += kernelsu/`.
- `common/drivers/Kconfig` — `source "drivers/kernelsu/Kconfig"` antes do `endmenu`.
- `common/arch/arm64/configs/kernelsu.fragment` — uma linha, `CONFIG_KSU=y`.
- `common/BUILD.bazel` do alvo `kernel_aarch64` — `defconfig_fragments` aponta esse fragmento. A linha `system_trusted_key` do cert do Google ficou no lugar.

`gki_defconfig` não foi alterado. `setup.sh` do KernelSU não rodou.

## Config final

Arquivo: `bazel-bin/common/kernel_aarch64_config/out_dir/.config`.

- `CONFIG_KSU=y` na linha 6700. `CONFIG_KPROBES=y` na 681. `CONFIG_EXT4_FS=y` na 6717.
- `CONFIG_LTO_NONE=y` na 738. A ação do Kleaf se chama `kernelsu.fragment;lto=none;trim`.
- `CONFIG_SYSTEM_TRUSTED_KEYS="google_gki_ab13771415_modsign_cert.pem"` na 7256.

Flags: `--config=stamp --jobs=1 --local_cpu_resources=1 --local_ram_resources=4096 --//build/kernel/kleaf:lto=none`. `SOURCE_DATE_EPOCH=1752273969` no shell. O sandbox avisou que caiu para 0 lá dentro.

## Link

Alvo `//common:kernel_aarch64`. A corrida que passou levou 613 s e saiu 0. Saída: `bazel-bin/common/kernel_aarch64/vmlinux` (177992368 bytes).

`nm` achou 259 símbolos com `ksu_` no nome. `kernelsu_init` está em `vmlinux`. O check `kmi_symbol_list_strict_mode` dessa mesma ação também passou.

MemAvailable mínimo observado: 5407064 kB (cerca de 5,2 GiB). Disco máximo: 48%. A guarda de 1 GiB não disparou.

## Gate

```
gate_kmi_crc.sh bazel-bin/common/kernel_aarch64/Module.symvers
modules.files=557 modules.unique=370
symbols.required=4138 symbols.reference_exports=8795 symbols.reference_provides=2309
compared=2309 mismatches=0 missing_exports=0 conflicting_crcs=0
export_type_mismatches=0 namespace_mismatches=0
unresolved=0
PASS
```

## Veredito

C0.1 VALIDADO. O vmlinux com KernelSU builtin linkou e o gate dos 557 passou com mismatches=0.

## Aberto

- Sem device, sem flash, sem boot.
- `ksud` e o APK não foram compilados.
- O `Kbuild` não viu um git próprio do KernelSU dentro da árvore do kernel, então a versão compilada cai no fallback `KSU_VERSION=16` (`kernel/Kbuild:125-126` do pin).
- Vermagic desse `Image` não foi comparado com o aparelho.
