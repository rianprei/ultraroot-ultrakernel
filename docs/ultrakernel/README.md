# UltraKernel: índice e estado

**Status:** a árvore do kernel está no repo (`kernel/`). No host, estão feitos os itens UK-0 a UK-6: build da `Image`, gate de CRC, selftests do lake e o inventário de tunables (`UK6-INVENTARIO-TUNABLES.md`, aguardando a revisão do dono). A lista completa está em `TODOS.md`. Overclock e undervolt dependem de dados do aparelho e de decisões do dono (ver `docs/PENDENCIAS-DO-DONO.md`). A visão original está em `SUPERKERNEL_VISION.md`, linha 29.

## O que está aqui

- `SUPERKERNEL_VISION.md`: visão original, com a tradução honesta do pedido (linhas 5 a 9) e o mapa técnico (linhas 11 a 17).

## Premissas

- Overclock e tuning fora do GKI (tabelas OPP e DTBO) têm risco de brick e de dano físico (`SUPERKERNEL_VISION.md`, linhas 26 e 27). Exige a mesma disciplina do kernel: backup conferido, guard de fastboot, slot B, rollback provado, bateria.
- Nunca mexer em preloader, lk e seccfg (`SUPERKERNEL_VISION.md`, linha 27).
- "Perfeito" não existe. O alvo é "muito poderoso e afinado", dentro dos limites físicos.

## Relação com o UltraRoot

O UltraKernel só começa depois do UltraRoot. O roteiro detalhado ainda não foi escrito.

A junção (root, overclock e tuning, tudo com gate) fica para depois do UltraRoot.
