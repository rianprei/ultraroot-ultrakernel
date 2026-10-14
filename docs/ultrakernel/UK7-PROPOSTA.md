# UK-7: proposta de fragmento de tuning (só proposta)

Estado: proposta para a revisão do dono. O arquivo `arch/arm64/configs/ultrakernel.fragment` NÃO foi gravado: falta a aprovação do dono (`docs/ultrakernel/TODOS.md`, UK-7). Base: inventário `docs/ultrakernel/UK6-INVENTARIO-TUNABLES.md`. Os caminhos de definição são relativos a `kernel/common/`.

## Regra usada

O kernel precisa continuar compatível com os módulos de vendor do aparelho (gate de KMI, UK-4). Por isso a proposta só muda opções que não alteram layout de estrutura nem constante de compilação vista por módulo. Valores de sysctl ficam fora do fragmento (ajuste em tempo de execução) e sem número inventado: UNKNOWN até medir no aparelho.

## Proposta

| Opção | Atual (build = aparelho) | Proposto | Motivo |
|---|---|---|---|
| `CONFIG_DEFAULT_BBR` | n (`CONFIG_DEFAULT_TCP_CONG="cubic"`, `kernel/lake/data/official-kernel_aarch64.config:1140`) | y | BBR já está compilado (`CONFIG_TCP_CONG_BBR=y`, inventário). Muda só o padrão do controle de congestionamento. Ganho no aparelho: NÃO VERIFICADO. Alternativa sem rebuild: `net.ipv4.tcp_congestion_control=bbr` em tempo de execução. |
| `CONFIG_HZ_250` / `CONFIG_HZ_300` / `CONFIG_HZ_1000` | 250 | manter 250 | `HZ` é constante de compilação (`include/asm-generic/param.h:8`, `# define HZ CONFIG_HZ`). Módulos de vendor compilados com 250 calculariam tempo errado. |
| `CONFIG_SCHED_AUTOGROUP` | n | manter n | Acrescenta campo em `struct signal_struct` (`include/linux/sched/signal.h:177`): muda layout visto por módulo. |
| `CONFIG_CFS_BANDWIDTH` | n | manter n | Acrescenta campos nas estruturas do scheduler (`kernel/sched/sched.h:341`, `kernel/sched/sched.h:646`). Efeito no KMI: NÃO VERIFICADO; mantido por precaução. |
| `CONFIG_KSM` | n | manter n | Acrescenta campos em `struct mm_struct` (`include/linux/mm_types.h:981-997`): muda layout. |
| `CONFIG_ZSWAP` | n | manter n | Acrescenta campos e eventos (`include/linux/memcontrol.h:233`, `include/linux/vm_event_item.h:148`): muda layout e numeração de eventos. |
| `CONFIG_ZRAM_WRITEBACK` | n | manter n | `zram` é módulo (`m`) do aparelho; mudar exige trocar o módulo, fora do escopo do fragmento. |
| `CONFIG_UCLAMP_TASK`, `CONFIG_UCLAMP_TASK_GROUP`, `CONFIG_ENERGY_MODEL`, `CONFIG_SCHED_MC`, `CONFIG_PREEMPT` | y | manter y | Já ligados no build e no aparelho. |
| Governors de CPUFreq (`SCHEDUTIL` padrão, `PERFORMANCE`, `POWERSAVE`, `CONSERVATIVE`) | y | manter | Já ligados. `USERSPACE` e `ONDEMAND` (n) não entram: sem medição que justifique. |
| devfreq (`SIMPLE_ONDEMAND`, `PERFORMANCE`, `POWERSAVE`, `USERSPACE`, `PASSIVE`) | y | manter | Já ligados. |
| I/O (`DEADLINE`, `KYBER`, `BFQ`, `BFQ_GROUP_IOSCHED`, `BLK_CGROUP`) | y | manter | Já ligados; a escolha do escalonador é em tempo de execução. |
| `CONFIG_TRANSPARENT_HUGEPAGE` | y | manter | Já ligado. |
| Rede (`TCP_CONG_ADVANCED`, `TCP_CONG_BBR`, `TCP_CONG_CUBIC`, `NET_SCH_FQ`, `NET_SCH_FQ_CODEL`) | y | manter | Já ligados. |
| `CONFIG_TRIM_UNUSED_KSYMS` | n no build, y no aparelho | ver `docs/ultrakernel/UK2-TRIM-PROPOSTA.md` | Não é tuning; decisão separada do dono. |
| sysctl (`vm.swappiness`, `vm.dirty_ratio`, `vm.dirty_background_ratio`, `vm.vfs_cache_pressure`, `vm.min_free_kbytes`, `kernel.sched_util_clamp_min`, `kernel.sched_util_clamp_max`) | padrão do aparelho: NÃO VERIFICADO | UNKNOWN | Ajuste em tempo de execução; valor só depois de medir no aparelho (passo de aparelho, fora desta rodada). |

## Fragmento testado (fora do repo)

```
CONFIG_DEFAULT_BBR=y
```

Teste no host, diretório de saída fora do repo (`<scratch>/k7/out`), clang 18.1.3 do sistema:

```
make -C kernel/common O=<scratch>/k7/out ARCH=arm64 LLVM=1 gki_defconfig
kernel/common/scripts/kconfig/merge_config.sh -m -O <scratch>/k7/out <scratch>/k7/out/.config kernel/common/arch/arm64/configs/kernelsu.fragment
make -C kernel/common O=<scratch>/k7/out ARCH=arm64 LLVM=1 olddefconfig      # cópia: base.config
kernel/common/scripts/kconfig/merge_config.sh -m -O <scratch>/k7/out <scratch>/k7/out/.config <scratch>/k7/ultrakernel.fragment
make -C kernel/common O=<scratch>/k7/out ARCH=arm64 LLVM=1 olddefconfig
rc=0
```

Mensagens: `Value of CONFIG_DEFAULT_BBR is redefined by fragment ...` (merge_config) e `.config:7750:warning: override: DEFAULT_BBR changes choice state` (kconfig, esperado ao trocar a opção de um `choice`). Nenhum erro.

Diferença de config (`diff base.config .config`):

```
< CONFIG_DEFAULT_CUBIC=y
< # CONFIG_DEFAULT_BBR is not set
> # CONFIG_DEFAULT_CUBIC is not set
> CONFIG_DEFAULT_BBR=y
< CONFIG_DEFAULT_TCP_CONG="cubic"
> CONFIG_DEFAULT_TCP_CONG="bbr"
```

## NÃO VERIFICADO

- Build da `Image` com o fragmento e o gate de KMI (UK-4) depois dele.
- Efeito de BBR no aparelho (latência, throughput, bateria).
- Valores atuais dos sysctl no aparelho.
