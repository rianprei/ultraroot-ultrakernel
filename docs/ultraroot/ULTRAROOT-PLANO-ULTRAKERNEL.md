# UltraRoot sobre o ULTRA-kernel: plano

Objetivo do dono: um kernel que nunca mais precise ser recompilado, e um UltraRoot que use tudo o que esse kernel oferece. Este plano liga os dois. Regras de `AGENTS.md` valem: aparelho só com "sim, <passo>", slot B, autor ≠ revisor, prova em todo item.

## Fato verificado que liga os dois

- A UAPI do KernelSU embutido no ULTRA-kernel é a mesma do app: os 6 headers de `kernel/common/drivers/kernelsu/uapi/` são idênticos byte a byte aos de `ultraroot/core-backend/src/main/cpp/uapi/` (`cmp`, 6 de 6 iguais, 2026-10-25). Versão do KernelSU no kernel: `KSU_VERSION := 32673` (`kernel/common/drivers/kernelsu/Kbuild:121`).
- Achado: `kernel/common/drivers/kernelsu/include/uapi` é um symlink para `../../uapi`, que não existe (`readlink -f` aponta para `kernel/common/drivers/uapi`). O build não usa esse caminho: o código inclui `"uapi/..."` por `-I$(KSU_KERNEL_DIR)` (`kernel/common/drivers/kernelsu/Kbuild:89`), que resolve em `drivers/kernelsu/uapi/`. Corrigir o symlink ou removê-lo: decisão junto com o próximo delta do kernel.

## Por que "nunca mais recompilar" depende do UltraRoot

O ULTRA-kernel liga as opções seguras como módulo (`=m`) para drivers USB e como `=y` para rede, filesystems e crypto. Os módulos não vão para a `Image`: precisam de alguém que os entregue e carregue no aparelho. Esse papel é do UltraRoot.

## Fases

### R1. Detecção do ULTRA-kernel (host primeiro)

| ID | O que | Aceite | Gate |
|---|---|---|---|
| R1.1 | Parser puro de `/proc/config.gz` (o kernel tem `CONFIG_IKCONFIG_PROC=y`): lê opções, valor e origem | testes de host com fixture gzip sintética; entrada corrompida recusada | — |
| R1.2 | Marca do ULTRA-kernel: detectar pelo config (fragmento aplicado) e pelo `uname -r` | teste com dois configs sintéticos (stock e ULTRA) | — |
| R1.3 | Tela "Kernel": capacidades ligadas (bonding, team, BBR, qdiscs, NTFS3, dm-*, drivers USB), com estado real lido do aparelho | presenter com teste; aparelho NÃO VERIFICADO | E1 (copy e layout) |

### R2. Central de módulos do kernel (`.ko`)

| ID | O que | Aceite | Gate |
|---|---|---|---|
| R2.1 | Pacote `ultrakernel-modules`: os `.ko` do build ULTRA, mais `modules.dep` e `modules.alias`, como módulo KSU (`module.prop` validado pelo `ModulePropParser`, U5) | `ModulePackageClassifier` (U6) aceita o pacote; sha256 de cada `.ko` registrado | — |
| R2.2 | Carregar e descarregar sob demanda (`modprobe -d <dir>`), com registro no `OpLog` (U4) | teste de host da linha de comando montada; aparelho NÃO VERIFICADO | — |
| R2.3 | Autocarga por hotplug: dispositivo USB plugado → `modalias` → módulo certo. É isso que faz um adaptador Wi-Fi ou Ethernet USB funcionar sem recompilar | teste de host do casamento alias → módulo com `modules.alias` real do build | aparelho: "sim, teste de hotplug USB" |
| R2.4 | Módulos futuros: compilados à parte contra os headers do kernel e assinados com a chave fixa do ULTRA-kernel (fora do repo) | um módulo de teste compilado fora da árvore carrega no kernel do host de teste (QEMU arm64) | chave fixa (decisão do dono) |

### R3. Ajustes em tempo de execução (sem recompilar)

| ID | O que | Aceite | Gate |
|---|---|---|---|
| R3.1 | TCP: algoritmo de congestionamento (`net.ipv4.tcp_congestion_control`), entre os compilados no ULTRA | lista lida de `tcp_available_congestion_control`; teste de host do parser | — |
| R3.2 | Fila de saída por interface (`tc qdisc`: cake, fq_pie, fq_codel) | comando montado com teste; aparelho NÃO VERIFICADO | — |
| R3.3 | I/O scheduler, compressão do zram, governor de CPU | só opções que o kernel expõe; valores padrão UNKNOWN até medir | — |
| R3.4 | Toda mudança tem rollback e linha no `OpLog` | teste: aplicar e reverter volta ao valor lido antes | — |

### R4. Rede: Wi-Fi + Ethernet ao mesmo tempo

| ID | O que | Aceite | Gate |
|---|---|---|---|
| R4.1 | Bonding `active-backup` (failover) e `balance-*` com `wlan0` e `eth0` USB | NÃO VERIFICADO: o `ConnectivityService` e o `netd` do Android disputam as interfaces | aparelho: "sim, teste de bonding" |
| R4.2 | MPTCP: fora. Quebra o KMI (223 símbolos, gate de 2026-10-26), e Wi-Fi, GPU e modem param | — | — |

### R5. Boot e flash do ULTRA-kernel

| ID | O que | Aceite | Gate |
|---|---|---|---|
| R5.1 | Acervo de backup ligado ao `BackupRegistry` (U3): `boot_b` com hash antes de tudo | dry-run de restauração passa | D0 |
| R5.2 | Repack do `boot_b` com a `Image` ULTRA (`kernel/lake/tools/repack_boot_v2.py`) e tamanho conferido contra 67108864 | teste de host com a imagem stock do dono (fora do repo) | entrada do dono (UK-11) |
| R5.3 | Flash só no slot B, pelo `kernel/lake/tools/fastboot_guard.sh` | selftest do guard 44 de 44 | D6 |
| R5.4 | Drill de rollback | rollback provado no aparelho | D7 |

### R6. O que já está pronto e só falta ligar na tela

- `SelfUpdatePolicy` (U2), `BackupRegistry` (U3), `OpLog` (U4), `ModulePropParser` (U5), `ModulePackageClassifier` (U6): lógica pura com 58 testes de host. Integração com a tela e com o aparelho: NÃO VERIFICADO.
- SP1 (H1, H2, H8): fechado no host. D1 a D7 e E1 a E6 seguem em `docs/PENDENCIAS-DO-DONO.md`.

## Ordem

1. Fechar o ULTRA-kernel (gate de KMI verde, chave fixa, `IKHEADERS=y`, tamanho da `Image`).
2. R1.1, R1.2, R2.1, R2.2, R3.1, R3.4: só host, com testes. Podem começar já.
3. E1 (dono) libera R1.3 e as telas de R2 e R3.
4. D0, depois R5.1 e R5.2, depois D6 (flash no slot B) e D7.
5. Depois do boot do ULTRA-kernel: R2.3, R4.1 no aparelho.

## Decisões do dono que este plano pede

- Chave fixa de assinatura de módulos do ULTRA-kernel, guardada fora do repo (libera R2.4 e fecha o ERR-13).
- E1 (copy e layout) para as telas novas.
- Imagem stock do `boot_b` para o teste de repack (R5.2).
