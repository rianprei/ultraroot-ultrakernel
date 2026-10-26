# Plano de boot do rianprei-ultra-kernel no POCO C75 (lake)

Base: o protocolo do lake, `kernel/lake/docs/DEVICE-TEST-PROTOCOL.md`, que já passou neste aparelho (Z0 em 2026-10-09; T-1 e T3 em 2026-10-10). Este plano não inventa passo novo: repete o caminho provado, trocando só a imagem do T3. Todo comando de aparelho é seu, e só com a frase do passo.

## O que "100%" significa aqui, sem exagero

- **Sucesso do boot:** não existe prova de 100% antes do boot real. O que existe é a soma das provas abaixo, com o mesmo caminho que já bootou no T3.
- **Rollback:** é o que tem a garantia mais forte. O flash grava só o `boot_b`. Preloader, `lk`, `vbmeta`, `dtbo`, `vendor_boot` e o slot A não são tocados. O fastboot mora no `lk`, então ele continua alcançável por teclas mesmo se o kernel novo não subir. A volta é gravar o backup `3890fb96…829fc7`, rota já executada no T-1 (OKAY em 2,3 s).

## Provas já feitas (host e aparelho)

| Prova | Resultado | Fonte |
|---|---|---|
| Bootloader desbloqueado | `unlocked=yes` (medido) | protocolo, "Z0 results (2026-10-09)" |
| Kernel recompilado e reempacotado sem assinatura do Google boota | T3 PASS: boot em cerca de 60 s, 429 de 429 módulos, Wi-Fi e Bluetooth, 0 erro novo no dmesg | protocolo, "T3 result (2026-10-10)" |
| Rollback pelo flash do backup | T-1 PASS: OKAY em 2,3 s, `uname -r` e módulos iguais | protocolo, "T-1 result (2026-10-10)" |
| Fastboot por teclas | medido: só com o cabo desconectado durante Vol− + Power | protocolo, "Z0 results" |
| Gate de KMI do ULTRA | `compared=2309 mismatches=0` | `docs/history/reports/ultra_kernel_2026-10-26.txt` |
| Boot do ULTRA em QEMU arm64 | sobe inteiro, KernelSU inicializa, 0 Oops, 0 BUG, 0 WARNING; mesmo ponto final da `Image` oficial | idem |
| `uname -r` igual ao stock e ao T3 | `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k` | idem |
| Toolchain igual ao stock | clang r510928 e pahole 1.25 | idem |
| Módulos GKI aceitos | o certificado público do Google está embutido como chave confiável | idem |
| Backup íntegro | 12 de 12 partições conferem com o `SHA256SUMS.log` | idem |
| Guard | 44 de 44, mais o ramo T-1 contra o backup real | idem |

## Diferenças contra o T3 (o que o T3 não prova)

1. O ULTRA tem 575 opções a mais e um `.config` com 660 linhas novas. Coberto pelo gate de KMI e pelo QEMU; no aparelho, NÃO VERIFICADO.
2. Os módulos GKI do `system_dlkm` vão ser verificados contra o certificado do Google, embutido como chave confiável. No T3 ele era a própria chave do build. O efeito no aparelho é o mesmo pela regra do kernel (`certs/`), mas não foi medido.
3. O build é `make`, e não Kleaf. Mesmo compilador, mesmo pahole, mesmo `uname -r`.

## Passos (cada um com a sua frase)

### P0. Host, antes de ligar o cabo

1. Conferir a imagem final (v6): `sha256sum boot_b_ultra.img` → `36bc72755e82152f510d298dd1ac1641b604143abbec24c4876c5f1a5cf0d85e`, 67108864 bytes. Fora do repo, na pasta de trabalho do kernel (`repack6/`).
2. Allowlist do guard, um hash por linha: o do backup (`3890fb96…829fc7`) e o da imagem nova. Sem o hash na lista, o guard recusa o flash.
3. Bateria ≥ 60 %, carregador desconectado, cabo de dados já conferido.

### P1. Z0 de hoje (leitura) — frase: "sim, preflight Z0"

Exatamente o Z0 do protocolo (Z0.1 a Z0.7), com a allowlist fechada de `getvar`. Precisa dar: `product=lake`, `current-slot=b`, `unlocked=yes`, `slot-successful:b=yes`, `slot-unbootable:b=no`, `partition-size:boot_b=4000000`. Qualquer diferença: PARAR. Antes de sair, capturar a linha de base: `uname -r`, `/proc/modules` (`baseline_modules.txt`) e o dmesg do stock.

### P2. Flash do ULTRA — frase: "sim, flash do ULTRA-kernel no boot_b"

1. Pre-flight do T3: `partition-size:boot_b` igual a 4000000, arquivo com 67108864 bytes, hash igual ao registrado.
2. `kernel/lake/tools/fastboot_guard.sh flash boot_b boot_b_ultra.img`. O guard confere hash, tamanho, magic `ANDROID!`, tamanho da partição e `current-slot=b` antes de gravar.
3. Antes do reboot, ler de novo `slot-successful:b`, `slot-unbootable:b` e `slot-retry-count:b` (T2b-post do protocolo).
4. `kernel/lake/tools/fastboot_guard.sh reboot`.

### P3. Aceitação (comandos do protocolo, sem redigitar)

- `adb shell getprop sys.boot_completed` = 1, em até 3 minutos.
- `adb shell uname -r` = `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`, e `adb shell cat /proc/version` contém `rianprei-ultra-kernel@build`.
- `adb shell dmesg > ultra_dmesg.txt; kernel/lake/tools/dmesg_new_errors.sh ultra_dmesg.txt` → código 0, sem saída.
- Módulos: `/proc/modules` é um superconjunto de `baseline_modules.txt`.
- Wi-Fi e Bluetooth ligam.

### P4. Rollback (se qualquer item falhar)

| Sintoma | Ação |
|---|---|
| Bootloop ou tela preta | Power longo até desligar. Cabo desconectado, Vol− + Power até FASTBOOT, soltar, reconectar o cabo. `kernel/lake/tools/fastboot_guard.sh flash boot_b <backup>/boot_b.img`, depois `reboot`. |
| Boota, mas a aceitação falha | O mesmo flash do backup, a partir do fastboot. |
| "Can't load Android system" | Nunca "Factory data reset". Escolha "Try again" e depois o rollback. |
| Qualquer `FAILED` do fastboot | PARAR, sem segunda tentativa. Fotografar e anotar. |
| `current-slot` diferente de `b` | Desligar por teclas e parar. Nunca `set_active`. |

## Riscos residuais

- **R2 do protocolo:** o LK aceitar a imagem. Coberto pelo T3, que aceitou uma imagem do mesmo tipo.
- **R4, contador de tentativas:** `slot-retry-count:b` = 1. Uma falha de boot pode esgotar o contador e marcar o `b` como não bootável. O rollback pelo fastboot continua valendo, mas é preciso agir na primeira falha, sem esperar loops.
- **Fallback para o slot A:** o slot A está `unbootable=yes`, e o LK não escolhe slot com prioridade 0 (protocolo, Z0). Medição no aparelho do fallback: NÃO VERIFICADO.
- Falha física durante a gravação (energia, cabo): mitigada por bateria ≥ 60 % e mãos fora do aparelho durante os 2 s de gravação.
