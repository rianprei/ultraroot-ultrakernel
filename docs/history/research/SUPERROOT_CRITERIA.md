# Critérios de "SuperRoot real" — definição de pronto (maestro, 2026-10-10)

Objetivo do dono: 1 APK = combinação suprema de Magisk+KernelSU+APatch+forks, com todas as features possíveis, **inbrickável**. Estes são os critérios MENSURÁVEIS pra dizer "chegamos". Cada um tem uma forma de VERIFICAR (não "parece pronto").

## Princípio
Core único = **KernelSU integrado** (decidido). "Combinar os 3" = 1 core + camada de compat de módulos + features portadas dos forks. Cada critério = capacidade observável no device, sob as travas de segurança (backup+guard+slot B, nunca A, nada sem "sim").

## TIER 0 — Fundação (sem isto, nada conta)
- [ ] **C0.1 Boot**: kernel KSU-integrado builda, passa o gate KMI (557 módulos compat) e BOOTA no C75. Verif: dmesg sem erro novo + 429 módulos + wifi/BT (= o gate T3 do kernel).
- [ ] **C0.2 Root real**: `su` concedido a app autorizado; negado a não-autorizado. Verif: `id` retorna uid 0 num app com grant; app sem grant não.
- [ ] **C0.3 Inbrickável**: ciclo instalar→rollback deixa o device bootável sempre. Verif: flash do SuperRoot-boot → se falhar, `fastboot_guard flash boot_b <backup>` restaura; provado em teste. Backup verificado antes de cada flash.

## TIER 1 — SuperRoot MVP (é "real" quando T0+T1 passam)
- [ ] **C1.1 Manager único (1 APK)**: um app gerencia grants de root + lista/ativa/desativa módulos + settings. Verif: APK único instala e opera os 3 sem segundo app.
- [ ] **C1.2 Módulos Magisk**: instala e roda módulo no formato Magisk (`module.prop`+scripts). Verif: 3 módulos Magisk populares carregam e fazem efeito.
- [ ] **C1.3 Módulos KernelSU**: idem formato KSU (webroot/action.sh/metamount). Verif: 3 módulos KSU carregam.
- [ ] **C1.4 App Profile / policy por app**: conceder/revogar root e perfil por UID. Verif: muda grant de um app e o efeito bate.
- [ ] **C1.5 Persistência**: tudo sobrevive a reboot. Verif: reboot → root+módulos continuam.

## TIER 2 — "Suprema" (absorver o melhor dos forks)
- [ ] **C2.1 Zygisk-compat**: roda módulos Zygisk (via ZygiskNext ou equivalente). Verif: 1 módulo Zygisk conhecido funciona.
- [ ] **C2.2 Ocultação/isolamento**: desmontar módulos/esconder root por UID selecionado (denylist/sulist-equivalente). Verif: app-alvo não vê os mounts de módulo. (HONESTO: reduz superfície, NÃO garante invisibilidade total nem aprovação de atestadores externos.)
- [ ] **C2.3 Comportamentos tipo-KPM**: portar o que o KPM do APatch oferece de útil (hooks de kernel) PARA o nosso core, sem o loader do KernelPatch. Verif: a feature-alvo específica funciona.
- [ ] **C2.4 susfs (se viável)**: integrar o patch de kernel susfs (dep externa simonpunk/susfs4ksu) ao nosso build. Verif: cliente susfs opera. (Opcional — fora do baseline inicial.)
- [ ] **C2.5 Features novas dos forks**: cada feature NOVA achada na varredura de forks (Fase 3) ou é absorvida, ou tem decisão explícita de "fora do escopo" com razão.

## TIER 3 — Robustez (o "inquebrável/imutável")
- [ ] **C3.1 Sobrevive a OTA/A-B**: re-aplica após update de sistema. Verif: simular/registrar o caminho. (Pesquisa-aberta; pode ser manual no início.)
- [ ] **C3.2 Update do próprio SuperRoot**: atualizar o engine sem brickar (gated). Verif: update→rollback OK.
- [ ] **C3.3 Matriz coberta**: toda feature PORTÁVEL da matriz está no engine; toda ACOPLADA ou foi reimplementada ou tem decisão registrada.

## DEFINIÇÃO DE "SUPERROOT REAL"
Mínimo aceitável = **TIER 0 + TIER 1 completos** (boota, roda root, 1 APK, módulos dos 2 formatos, persiste, inbrickável provado).
"Supremo" = + TIER 2. "Inquebrável" = + TIER 3.
NENHUM tier conta sem a verificação observável correspondente. Device só sob backup+guard+"sim".

## Como não parar até lá (loop)
Cada ciclo: pegar o próximo critério não-cumprido → build/implementa sob o core → gate (KMI/dmesg) local → device sob travas → marca [x] com evidência → próximo. Para só quando T0+T1 (real) e depois T2/T3 (supremo/inquebrável) estiverem [x] com prova.
