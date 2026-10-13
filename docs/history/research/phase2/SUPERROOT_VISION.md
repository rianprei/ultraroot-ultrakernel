# SuperRoot — visão/requisitos do dono (norte do projeto)

Dono, 2026-10-10: "app de root com TODAS as features possíveis — a combinação suprema de Magisk + KernelSU + APatch, forks entram como features — indestrutível, imutável, invencível, inquebrável, INBRICKÁVEL."

## Tradução em requisitos de engenharia (honesta)
1. **Superset de features** — hospedar o melhor de KSU+Magisk+APatch+forks. A matriz do grok é o catálogo; portáveis a gente hospeda, acopladas a gente reimplementa sob 1 core. Entrega incremental, não tudo no dia 1.
2. **1 APK / 1 engine** — um core (decisão: KernelSU integrado) + camadas que rodam módulos Magisk/KSU + comportamentos tipo-KPM + UI unificada.
3. **INBRICKÁVEL (requisito nº1)** — vem da NOSSA infra de segurança de flash, não do engine: backup verificado, fastboot_guard, slot B (NUNCA A), gates (KMI/CRC/dmesg), recuperação por teclas, nada flasha sem "sim". Honestidade: nada é 100% inbrickável; a gente engenheira o risco PRA PERTO DE ZERO com o método já provado no T3. Não prometo mágica.
4. **Imutável/indestrutível/invencível** → metas concretas: reversível sempre (rollback provado), resiliente (sobrevive a OTA/A-B — lacuna a resolver), robusto (nada entra sem gate).

## Impacto na decisão de core
O requisito INBRICKÁVEL REFORÇA KernelSU **integrado no nosso source** sobre APatch: a garantia de não-brick depende de controlarmos cada byte do que é gravado, sob os nossos gates. No caminho integrado nós OWN o build; no APatch a gente depende de um patcher de imagem de terceiro (menos controle = mais risco de brick). Logo: integrado serve melhor ao requisito nº1.
