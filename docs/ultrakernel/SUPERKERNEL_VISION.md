# SuperKernel / UltraKernel — visão (projeto FUTURO, após Super/UltraRoot)

Dono, 2026-10-11: quer um kernel "perfeito, absoluto, que faça tudo" — insatisfeito que o nosso kernel atual (GKI stock+cert+KSU) não permite overclock. Registrado pra construir DEPOIS do Super/UltraRoot. Overclock é projeto legítimo (comunidade custom-kernel faz).

## Tradução honesta
| Pedido | Alvo real | Teto honesto |
|---|---|---|
| "faz tudo / overclock" | OC/undervolt de CPU+GPU, governors, scheduler, I/O, TCP, thermal headroom | limite físico: silício/thermal/estabilidade; OC demais = instável/superaquece/degrada |
| "perfeito/absoluto" | kernel maximamente customizado e potente | "perfeito" não existe; "muito poderoso e afinado" existe |

## Onde cada coisa mora (o mapa técnico)
**DENTRO do GKI (a gente controla, baixo risco):** scheduler/governor tunables, I/O scheduler, TCP congestion, sysctl, configs de kernel, LMK/compaction. Dá pra afinar bastante aqui sem tocar vendor.
**FORA do GKI = camada de vendor (o overclock de verdade, ALTO risco):**
- **Tabelas OPP** (operating points: pares freq×voltagem de CPU/GPU) — no **device tree (DTB)** / flashável via **DTBO**. Overclock = adicionar/editar pontos OPP mais altos.
- **Drivers cpufreq/gpufreq** da MediaTek (vendor `.ko` FECHADOS) — impõem os limites; podem precisar de RE/patch pra aceitar OPP novos.
- **Thermal** (mtk_thermal, limites de throttling) — subir clock sem ajustar thermal = throttle imediato ou superaquecimento.
- **Undervolt** — mexe nas voltagens do OPP; ganho de bateria/calor, risco de instabilidade.

## Caminho (quando começar)
1. RE do DTBO do lake: achar e mapear as tabelas OPP de CPU (MT6768/6769) e GPU (Mali).
2. RE dos vendor cpufreq/gpufreq/thermal `.ko` (via REA/Ghidra) — entender onde os limites são impostos.
3. Editar OPP no DTBO (ou overlay) + patch vendor se necessário → flashar DTBO sob guard+backup (DTBO tem backup: dtbo_a/dtbo_b já no backup-2026-10-05).
4. Gates próprios: estabilidade (stress), temperatura, não-brick (rollback de DTBO provado), bateria.
5. Juntar com o kernel-KSU → o "UltraKernel": root + OC + tuning, tudo gated.

## Risco (por que é projeto separado e DEPOIS)
DTBO/vendor/thermal = zona de brick e de dano físico (superaquecimento). Mais perigoso que o boot. Exige a mesma disciplina do kernel: backup conferido (dtbo já salvo), fastboot_guard, slot B nunca A, rollback provado, nada sem "sim", bateria. NUNCA preloader/lk/seccfg.

## Status: VISÃO SÓ. Não iniciado. Prioridade atual = Super/UltraRoot (C0.1 em validação no device).
