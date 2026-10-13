# UltraKernel: índice e estado

**Status: visão apenas. Não iniciado.** Fonte: `docs/ultrakernel/SUPERKERNEL_VISION.md`, linha 29. A prioridade atual é o UltraRoot (`docs/history/research/MASTER_PLAN.md`, linha 21).

## O que está aqui

- `SUPERKERNEL_VISION.md`: visão original, com a tradução honesta do pedido (linhas 5 a 9) e o mapa técnico (linhas 11 a 17).

## Premissas

- Overclock e tuning fora do GKI (tabelas OPP e DTBO) têm risco de brick e de dano físico (`SUPERKERNEL_VISION.md`, linhas 26 e 27). Exige a mesma disciplina do kernel: backup conferido, guard de fastboot, slot B, rollback provado, bateria.
- Nunca mexer em preloader, lk e seccfg (`SUPERKERNEL_VISION.md`, linha 27).
- "Perfeito" não existe. O alvo é "muito poderoso e afinado", dentro dos limites físicos.

## Relação com o UltraRoot

O UltraKernel só começa depois do UltraRoot. O roteiro detalhado ainda não foi escrito.

O UltraKernel só começa depois do UltraRoot. A junção (root mais overclock mais tuning, tudo com gate) está na fase 5 do rascunho.
