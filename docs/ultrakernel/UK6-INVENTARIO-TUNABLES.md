# UK-6: inventário de tunables do GKI

Status: lista pronta para revisão do dono (critério da UK-6). Só leitura: nenhuma opção foi alterada, e o vendor não foi tocado.

Fontes:
- Código: `kernel/common/` (GKI `5a0ffb4` mais o delta). Caminhos abaixo são relativos a `kernel/common/`.
- Build: o `.config` final de `gki_defconfig` + `kernelsu.fragment` + `olddefconfig`, com clang r510928 (rodada de 2026-10-13, ver `docs/PRE-REQUISITOS.md`).
- Aparelho: `kernel/lake/data/official-kernel_aarch64.config` (config oficial do stock do POCO C75).

Cada linha foi conferida com `grep` no código. "Definição" é o arquivo e a linha do `Kconfig`. "Build" é o valor no `.config` final. Quando a opção não tem linha no `.config`, a célula diz `(ausente)`.

## Scheduler e CPU

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_UCLAMP_TASK` | `init/Kconfig:812` | y | y | util clamp por tarefa |
| `CONFIG_UCLAMP_TASK_GROUP` | `init/Kconfig:1039` | y | y | util clamp por cgroup |
| `CONFIG_ENERGY_MODEL` | `kernel/power/Kconfig:340` | y | y | modelo de energia (EAS) |
| `CONFIG_SCHED_MC` | `arch/arm/Kconfig:1027` | y | y | |
| `CONFIG_SCHED_AUTOGROUP` | `init/Kconfig:1279` | n | n | |
| `CONFIG_CFS_BANDWIDTH` | `init/Kconfig:1011` | n | n | cotas de CPU por cgroup |
| `CONFIG_PREEMPT` | `kernel/Kconfig.preempt:51` | y | y | preempção completa |
| `CONFIG_HZ_250` | `arch/arm/Kconfig:1186` | y | y | frequência do tick |
| `CONFIG_HZ_300` | `arch/arm/Kconfig:1189` | n | n | |
| `CONFIG_HZ_1000` | `arch/arm/Kconfig:1195` | n | n | |

## CPUFreq (governors)

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_CPU_FREQ` | `drivers/cpufreq/Kconfig:4` | y | y | |
| `CONFIG_CPU_FREQ_DEFAULT_GOV_SCHEDUTIL` | `drivers/cpufreq/Kconfig:105` | y | y | governor padrão |
| `CONFIG_CPU_FREQ_GOV_SCHEDUTIL` | `drivers/cpufreq/Kconfig:196` | y | y | |
| `CONFIG_CPU_FREQ_GOV_PERFORMANCE` | `drivers/cpufreq/Kconfig:117` | y | y | |
| `CONFIG_CPU_FREQ_GOV_POWERSAVE` | `drivers/cpufreq/Kconfig:128` | y | y | |
| `CONFIG_CPU_FREQ_GOV_CONSERVATIVE` | `drivers/cpufreq/Kconfig:171` | y | y | |
| `CONFIG_CPU_FREQ_GOV_USERSPACE` | `drivers/cpufreq/Kconfig:139` | n | n | |
| `CONFIG_CPU_FREQ_GOV_ONDEMAND` | `drivers/cpufreq/Kconfig:152` | n | n | |

A tabela de frequências (OPP) não está nesta lista. Ela vem do device tree (DTBO) e do driver de vendor (ver UK-8 e UK-9).

## devfreq (GPU e barramentos)

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_PM_DEVFREQ` | `drivers/devfreq/Kconfig:2` | y | y | |
| `CONFIG_DEVFREQ_GOV_SIMPLE_ONDEMAND` | `drivers/devfreq/Kconfig:34` | y | y | |
| `CONFIG_DEVFREQ_GOV_PERFORMANCE` | `drivers/devfreq/Kconfig:43` | y | y | |
| `CONFIG_DEVFREQ_GOV_POWERSAVE` | `drivers/devfreq/Kconfig:51` | y | y | |
| `CONFIG_DEVFREQ_GOV_USERSPACE` | `drivers/devfreq/Kconfig:59` | y | y | |
| `CONFIG_DEVFREQ_GOV_PASSIVE` | `drivers/devfreq/Kconfig:68` | y | y | |

## I/O

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_MQ_IOSCHED_DEADLINE` | `block/Kconfig.iosched:4` | y | y | |
| `CONFIG_MQ_IOSCHED_KYBER` | `block/Kconfig.iosched:10` | y | y | |
| `CONFIG_IOSCHED_BFQ` | `block/Kconfig.iosched:19` | y | y | |
| `CONFIG_BFQ_GROUP_IOSCHED` | `block/Kconfig.iosched:30` | y | y | |
| `CONFIG_BLK_CGROUP` | `init/Kconfig:970` | y | y | |

## Memória, zram e swap

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_ZRAM` | `drivers/block/zram/Kconfig:2` | m | m | |
| `CONFIG_ZRAM_WRITEBACK` | `drivers/block/zram/Kconfig:58` | n | n | |
| `CONFIG_ZSMALLOC` | `mm/Kconfig:203` | m | m | |
| `CONFIG_ZSWAP` | `mm/Kconfig:25` | n | n | |
| `CONFIG_ZSWAP_DEFAULT_ON` | `mm/Kconfig:38` | (ausente) | (ausente) | só existe com ZSWAP |
| `CONFIG_TRANSPARENT_HUGEPAGE` | `mm/Kconfig:842` | y | y | |
| `CONFIG_KSM` | `mm/Kconfig:754` | n | n | |

Não há opção de LMK no kernel nesta árvore: a busca por `ANDROID_SIMPLE_LMK` em `kernel/common/` não achou nada.

## Rede

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_TCP_CONG_ADVANCED` | `net/ipv4/Kconfig:469` | y | y | |
| `CONFIG_TCP_CONG_BBR` | `net/ipv4/Kconfig:666` | y | y | |
| `CONFIG_TCP_CONG_CUBIC` | `net/ipv4/Kconfig:496` | y | y | |
| `CONFIG_DEFAULT_BBR` | `net/ipv4/Kconfig:715` | n | n | |
| `CONFIG_NET_SCH_FQ` | `net/sched/Kconfig:299` | y | y | |
| `CONFIG_NET_SCH_FQ_CODEL` | `net/sched/Kconfig:277` | y | y | |

## Vendor e KernelSU (não é tuning)

| Opção | Definição | Build | Aparelho | Nota |
|---|---|---|---|---|
| `CONFIG_ANDROID_VENDOR_HOOKS` | `drivers/android/Kconfig:61` | y | y | usado pelos módulos de vendor |
| `CONFIG_KSU` | `drivers/kernelsu/Kconfig:3` | y | (ausente) | delta nosso, ligado por `kernelsu.fragment` |
| `CONFIG_TRIM_UNUSED_KSYMS` | `kernel/module/Kconfig:386` | n | y | ver a seção de diferenças |

## sysctl (ajuste em tempo de execução)

Valores em `/proc/sys/`. Não exigem recompilar. Persistir após o boot depende de como o aparelho aplica o valor, e isso não foi verificado.

| Knob | Definição | Nota |
|---|---|---|
| `vm.swappiness` | `kernel/sysctl.c:2091` | |
| `vm.dirty_ratio` | `mm/page-writeback.c:2270` | |
| `vm.dirty_background_ratio` | `mm/page-writeback.c:2253` | |
| `vm.vfs_cache_pressure` | `kernel/sysctl.c:2147` | |
| `vm.min_free_kbytes` | `mm/page_alloc.c:6526` | |
| `net.ipv4.tcp_congestion_control` | `net/ipv4/sysctl_net_ipv4.c:891` | |
| `kernel.sched_util_clamp_min` | `kernel/sched/core.c:4817` | |
| `kernel.sched_util_clamp_max` | `kernel/sched/core.c:4824` | |

## Diferenças entre o build e o config do aparelho

Comparação do `.config` final do build com `official-kernel_aarch64.config`:

- Iguais: 6168 opções.
- Valores diferentes: 2.
- Só no build: 4 (`CONFIG_KSU=y`, `KSU_DEBUG=n`, `KSU_DISABLE_MANAGER=n`, `KSU_DISABLE_POLICY=n`). Vêm do delta do KernelSU, como esperado.
- Só no aparelho: 3 (`CONFIG_CC_CAN_LINK=y`, `CONFIG_UAPI_HEADER_TEST=y`, `CONFIG_UNUSED_KSYMS_WHITELIST="abi_symbollist.raw"`).

Os 2 valores diferentes:

- `CONFIG_PAHOLE_VERSION`: 132 no build e 125 no aparelho. É a versão da ferramenta, não é tuning.
- `CONFIG_TRIM_UNUSED_KSYMS`: n no build e y no aparelho. Com ele ligado, o kernel exporta só os símbolos da lista de permissão. O build exporta o conjunto completo. O gate KMI (UK-4) passa mesmo assim, com 0 símbolos faltando.

O que não foi verificado: qual passo do build do aparelho liga `TRIM_UNUSED_KSYMS`. O `BUILD.bazel` do delta é do Kleaf, e o workspace dele não está no repo.

## Decisões do dono antes do UK-7

1. Revisar esta lista (critério da UK-6).
2. Decidir se o build deve igualar o aparelho em `TRIM_UNUSED_KSYMS`. Se sim, o gate de KMI precisa rodar de novo.
3. Escolher quais opções de tuning entram em `arch/arm64/configs/ultrakernel.fragment`. Até lá, nenhuma opção acima muda.
4. Overclock e undervolt não estão nesta lista. Eles dependem de OPP, de tensão e de driver de vendor (UK-8, UK-9 e UK-10).
