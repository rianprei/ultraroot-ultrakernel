# Matriz HW/performance — POCO C75 (lake)

Data: 2026-10-11. Host, só leitura. Sem adb, sem fastboot, sem build, sem aplicar ajuste.

Veredito de overclock: **bloqueado na tabela OPP que o driver consulta**. Não há ponto acima de 1800 MHz (A55), 2000 MHz (A75) ou 1000 MHz (GPU) nessas tabelas. Frequência pedida fora da tabela é recusada pelo `CPU_DVFS.ko`. Margem de silício, PLL e efuse deste aparelho: **UNKNOWN**.

## De onde saiu o número

| Fonte | O que é |
|---|---|
| `backup-2026-10-05/vendor_boot_b.img` | DTB único. Header v4, `dtb_size` 165322, tabela Android em offset 30535680, FDT em **30535744**. Cópia `[artefato local, fora do repo]/grok/hwperf/dt/vb_b_entry0.dtb`. |
| `dtbo_b.img` | Um overlay, 172248 bytes, sha256 `b6717ff3d7d9080847e7b9bfa6043ab4bbb8995f2f20d151ece43b799288f5a5`. Não contém `opp-hz`. `dtbo_a.img` tem outro sha256 (`18e500ec…` ≠ `c58af56d…`). O overlay A não foi descompilado. |
| `super.img` | `lpdump`: partição `vendor_dlkm_a`, 29584 setores (15147008 bytes). Extraída em `[artefato local, fora do repo]/grok/hwperf/ko/vendor_dlkm_a.img` (EROFS, LZ4). `vendor_dlkm_b` não está no slot 0 do metadata. Sem `fsck.erofs` e sem permissão de `mount`, o EROFS não foi aberto. |
| Módulos legíveis | `[fora do repo]/mods/lake-kernel/audit/modules/vendor_dlkm/`. Vermagic `6.6.89-android15-8-g810fd09a116c-4k SMP preempt mod_unload modversions aarch64`. Não foi feita comparação byte a byte com o EROFS. |
| GKI | `.config` do build desta árvore em `c01-build/.../kernel_aarch64_config/out_dir/.config`. Não é o config do boot stock (`g810fd09a116c`). |

SoC no DTB, linha 5 de `vb_b_entry0.dts`: `compatible = "mediatek,MT6768"`. GPU, linha 4591: `compatible = "mediatek,mali", "arm,mali-valhall"`, `reg` `0x13040000`. PMIC: `mt6358regulator` sob `pwrap@1000d000`.

As frequências abaixo são os `opp-hz` do FDT (célula alta 0, célula baixa em Hz). As tensões de CPU são a célula única de `opp-microvolt`, em µV. O `dtc` imprimiu lixo em três nós (`"" , "\f5"`); o FDT desses nós é uma célula só, e o valor usado aqui é o do blob.

## OPP de CPU

Seis `arm,cortex-a55` (`cpu@000`–`cpu@101`, linhas 490–566) usam `operating-points-v2 = <0x15>`, o phandle de `opp_table0` (linha 317). Dois `arm,cortex-a75` (`cpu@102`, `cpu@103`, linhas 568–591) usam `<0x1d>`, o phandle de `opp_table1` (linha 403). `opp-shared` nos dois.

`clock-frequency` do A55: `0x65633340` = 1701000000 Hz. Do A75: `0x8166d4c0` = 2171000000 Hz. Esses inteiros **não aparecem** no `CPU_DVFS.ko` (contagem 0). O teto da tabela A55 (1800 MHz) fica acima do `clock-frequency`. O `clock-frequency` do A75 (2171 MHz) fica 171 MHz acima do teto da tabela, e não é um `opp-hz`.

### Cluster 0 — A55 — `opp_table0`

| MHz | µV |
|---|---|
| 500 | 650000 |
| 774 | 675000 |
| 850 | 700000 |
| 900 | 718750 |
| 950 | 731250 |
| 999 | 743750 |
| 1050 | 762500 |
| 1100 | 775000 |
| 1175 | 800000 |
| 1275 | 831250 |
| 1325 | 843750 |
| 1375 | 856250 |
| 1450 | 881250 |
| 1500 | 893750 |
| 1625 | 931250 |
| **1800** | **1006250** |

### Cluster 1 — A75 — `opp_table1`

| MHz | µV |
|---|---|
| 850 | 675000 |
| 909 | 700000 |
| 998 | 731250 |
| 1087 | 768750 |
| 1176 | 800000 |
| 1295 | 843750 |
| 1354 | 868750 |
| 1443 | 900000 |
| 1532 | 931250 |
| 1621 | 968750 |
| 1710 | 1000000 |
| 1800 | 1031250 |
| 1850 | 1050000 |
| 1900 | 1062500 |
| 1950 | 1075000 |
| **2000** | **1087500** |

## OPP de GPU

`mali@13040000` aponta `operating-points-v2` para o phandle `0x8c` (`opp-table0`, linha 4633). 31 pontos, de 315 MHz a **1000 MHz**. A célula `opp-microvolt` do topo é **95000**, a do piso é **61875**.

Isso não é µV no mesmo sentido da CPU. O mínimo do buck `vgpu` é 500000 µV. 95000 µV estaria abaixo desse mínimo. O `mtk_gpufreq_mt6768.ko`, no offset 24672, tem o registro `1000000, 95000, 105000, 1, 1875, 0` e o piso `315000` kHz. O 95000 do DTB é o mesmo inteiro do `.ko`, não uma tensão de PMIC lida em µV. O fator que levaria esse inteiro a mV não está no fonte aberto. Não converti.

`gpufreq` (linha 4608): `compatible = "mediatek,gpufreq"`, `nvmem-cell-names = "efuse_segment_cell"`. `gpufreq_wrapper` (linha 4617): `gpufreq-version = <2>`, `dual-buck = <0>`, `gpueb-support = <0>`.

## Outras tabelas no mesmo DTB

Não são CPU nem GPU.

| Tabela | Pontos (Hz, µV) | Teto |
|---|---|---|
| `opp-table-mm` | 228e6/650000, 312e6/700000, 457e6/800000 | 457 MHz |
| `opp-table-venc` | 312e6/650000, 416e6/700000, 457e6/800000 | 457 MHz |
| `opp-table-cam` | 228e6/650000, 312e6/700000, 546e6/800000 | 546 MHz |

## Reguladores (MT6358)

Células `regulator-min-microvolt` / `regulator-max-microvolt`:

| Nó | Nome | min µV | max µV |
|---|---|---|---|
| `buck_vproc11` linha 2048 | vproc11 | 500000 | 1293750 |
| `buck_vproc12` linha 2057 | vproc12 | 500000 | 1293750 |
| `buck_vgpu` linha 2066 | vgpu | 500000 | 1293750 |
| `ldo_vsram_gpu` linha 2201 | vsram_gpu | 500000 | 1293750 |

O teto A75 usa 1087500 µV. O máximo do buck é 1293750 µV. A diferença aritmética é 206250 µV. Isso é a folga do nó do regulador, não um OPP. Os nós `cpu@` lidos não têm `cpu-supply`. Qual buck alimenta qual cluster não está nesse trecho do DTB. O `CPU_DVFS.ko` tem a string `regulator map fail`.

## Onde o teto é imposto

`CPU_DVFS.ko` (161720 bytes), fonte no vermagic string `drivers/misc/mediatek/cpufreq_v2/src/mtk_cpufreq_main.c`:

- Símbolos `dev_pm_opp_of_cpumask_add_table` e `dev_pm_opp_of_get_sharing_cpus`: a tabela do DTB entra no OPP framework.
- String `frequency %dKHz! is not found in CPU opp table`: kHz que não está na tabela não é aceito em `/proc/cpufreq/%s/cpufreq_freq`.
- O `.ko` contém o inteiro 2000000 (7 vezes) e 1800000 (10 vezes). Não contém 2171000 nem 1701000.
- Efuse: `efuse_segment_cell`, `efuse_ptpod_cell`, `_mt_cpufreq_get_cpu_level`. O segmento deste aparelho não foi lido.
- Turbo: `/proc/cpufreq/%s/cpufreq_turbo_mode` e `mt_cpufreq_turbo_action`. A função não foi desassemblada. Não há OPP acima de 2000/1800 nas tabelas dumpadas.
- Também existem `/proc/cpufreq/%s/cpufreq_volt` e `cpufreq_oppidx`. Escrever tensão não é ajuste seguro.
- Atributo `cpufreq_freq_attr_scaling_available_freqs`: o core publica as frequências da tabela em sysfs quando o driver registra. O registro está na string `fail to register dvfs driver`. O sysfs vivo do aparelho não foi lido.

`mtk_ppm_v3.ko`: `/proc/ppm/policy_status`, `/proc/ppm/enabled`, `ppm update cpufreq limit ,cluster %d, min freq %d ------max freq %d`, `mt_ppm_cpu_thermal_protect`. Isso corta o intervalo. Não cria frequência.

`thermal_monitor.ko`: `/proc/mtkcooler`, `Free all static thermal limit`. Cap térmica. Não cria OPP.

`mtk_gpufreq_mt6768.ko`: `__gpufreq_init_opp_table`, `__gpufreq_init_segment_id`, `efuse_segment_cell`. O registro de 1000 MHz / 95000 casa com o DTB. `gpufreq_set_limit` está no wrapper. Não achei inteiro de kHz de GPU acima de 1000000 nessa varredura (os u32 entre 1000000 e 1600000 são outros valores, tipo 1114112).

DTBO: fragmentos de `thermal-sensor5` e `thermal-message`. Sem tabela de frequência.

## Matriz

| Componente | Recurso | Classe | Evidência |
|---|---|---|---|
| CPU A55 | escolher freq da tabela, teto 1800 MHz | implementado no `.ko`; efeito no aparelho UNKNOWN | FDT `opp_table0`; `CPU_DVFS.ko` |
| CPU A75 | escolher freq da tabela, teto 2000 MHz | idem | FDT `opp_table1` |
| CPU | freq acima da tabela | bloqueado (lookup da OPP) | string `is not found in CPU opp table` |
| CPU | `clock-frequency` 2171 MHz no A75 | não é OPP | célula no `cpu@102`; inteiro ausente no `.ko` |
| CPU | efuse / cpu level | pode escolher tabela mais baixa; bin deste telefone UNKNOWN | `efuse_segment_cell` |
| CPU | turbo em `/proc/cpufreq` | interface existe; efeito UNKNOWN | `cpufreq_turbo_mode`; função não desassemblada |
| CPU | escrever µV em `cpufreq_volt` | interface existe; não é ajuste de teto seguro | string do proc |
| CPU | PPM / thermal | bloqueado para cima; só reduz | `mtk_ppm_v3.ko`, `thermal_monitor.ko` |
| GPU | tabela 315–1000 MHz | implementado no DTB e no `.ko` | `opp-table0`; offset 24672 do `mtk_gpufreq_mt6768.ko` |
| GPU | freq acima de 1000 MHz | não há OPP; efuse pode limitar para baixo | `efuse_segment_cell`; sem kHz maior na varredura |
| GPU | célula 95000 | não é µV do PMIC | buck mínimo 500000 µV; mesmo inteiro no `.ko` |
| GPU | sysfs devfreq vivo | UNKNOWN-pendente | sem leitura do aparelho |
| PMIC vproc/vgpu | máximo 1.29375 V | folga de tensão no nó, sem freq correspondente | linhas 2048–2070 |
| MM / venc / cam | tetos 457 / 457 / 546 MHz | tabelas próprias, não são OC de CPU/GPU | FDT |
| Governor | performance, powersave, conservative, schedutil (default) | ligados no `.config` deste build GKI | linhas 620–631 desse `.config` |
| Governor | userspace, ondemand | não ligados nesse `.config` | `# CONFIG_CPU_FREQ_GOV_USERSPACE is not set` |
| sysfs `scaling_max_freq` | dentro de `scaling_available_freqs` | interface do core, se o driver registrou | símbolo no `CPU_DVFS.ko`; aparelho não lido |
| I/O | mq-deadline, kyber, bfq | ligados nesse `.config` | linhas 878–880 |
| zRAM | módulo | `zram.ko` no ramdisk de `vendor_boot` do backup (94 kB) e `CONFIG_ZRAM=m` | `audit/bootimg/ramdisks/vb_b_r00/lib/modules/zram.ko` |
| cgroup | cpu, mem, io, throttle | ligados no `gki_defconfig` e no `.config` | `CONFIG_CGROUP_SCHED`, `CONFIG_MEMCG`, `CONFIG_BLK_CGROUP`, `CONFIG_BLK_DEV_THROTTLING` |
| Slot A | OPP | UNKNOWN | `dtbo_a` difere de `dtbo_b`; DTB A não extraído |
| EROFS `vendor_dlkm_a` | módulos dentro da imagem | UNKNOWN até descompactar | imagem extraída; sem `fsck.erofs` e mount negado |

O `.config` citado é o da árvore GKI compilada neste host (build com `kernelsu.fragment`). O config embutido no boot stock não foi extraído. `gki_defconfig` sozinho só marca `POWERSAVE` e `CONSERVATIVE`; o `.config` gerado é que liga schedutil, performance e os iosched.

## Overclock: veredito por evidência

Não há margem de frequência demonstrada acima das tabelas.

Trava que está no artefato:

1. O OPP do DTB para em 1800 MHz (A55, 1.00625 V) e 2000 MHz (A75, 1.0875 V). GPU para em 1000 MHz.
2. `CPU_DVFS.ko` carrega essa tabela (`dev_pm_opp_of_cpumask_add_table`) e recusa kHz ausente dela.
3. Os inteiros 1800000 e 2000000 kHz estão no `.ko`. 2171000 e 1701000 não estão.
4. PPM e o cooler térmico só escrevem limite para baixo.

O que não é margem:

- O buck vai até 1293750 µV e o topo A75 usa 1087500 µV. Folga de tensão no nó, sem `opp-hz` correspondente.
- `clock-frequency` = 2171 MHz no A75 é uma propriedade do nó `cpu@`, não um ponto da tabela, e não está no `.ko`.

UNKNOWN, e por isso não chamo o silício de incapaz nem de capaz:

- segmento efuse deste aparelho (pode estar numa tabela mais baixa que a do DTB);
- o que `mt_cpufreq_turbo_action` faz;
- se o PLL aceitaria um divisor acima de 2.0 / 1.8 / 1.0 GHz;
- DTB do slot A;
- sysfs real do telefone.

## Ajustável sem sair da tabela

Governors já ligados neste `.config`: performance, powersave, conservative, schedutil. `scaling_min_freq` / `scaling_max_freq` dentro das frequências publicadas. Scheduler de bloco: mq-deadline, kyber, bfq. zRAM: o módulo está no `vendor_boot` do backup. Cgroup de cpu, memória e I/O para limitar, não para subir OPP.

Fora dessa lista: `cpufreq_volt`, turbo, segmento efuse, OPP novo.
