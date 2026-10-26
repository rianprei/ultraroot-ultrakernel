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

## Resultado do P1 (Z0, 2026-10-26, frase do dono: "sim, preflight Z0")

Só leitura, tudo pelo `kernel/lake/tools/fastboot_guard.sh`. Serial não registrado. Capturas fora do repo.

- Z0.2: `devices` → 1 aparelho em fastboot.
- Z0.3/Z0.4: PASS. `product=lake`, `current-slot=b`, `slot-count=2`, `is-userspace=no`, `unlocked=yes`, `max-download-size=0x8000000`, `partition-size:boot_b=4000000`, `slot-successful:a=no`, `slot-successful:b=yes`, `slot-unbootable:a=yes`, `slot-unbootable:b=no`, `slot-retry-count:a=0`, `slot-retry-count:b=1`, `battery-soc-ok=yes`, `battery-voltage=4254mV`. Idêntico ao Z0 de 2026-10-09, exceto a tensão da bateria.
- Z0.6: `reboot` pelo guard → OKAY. Android: `sys.boot_completed=1`, slot `_b`, `OS3.0.306.0.WGTMIXM`, 429 módulos.
- Kernel em execução: `6.6.89-android15-8-4k (kleaf@build-host)`, com `KernelSU: welcome to KernelSU version 32673`. É o C0.3 (`uname -r` previsto em `docs/history/research/phase4/C01_device_plan.md:56`). Isso fecha o D3 (o C0.3 estava UNKNOWN) e mostra que um `uname -r` diferente do stock carrega os 429 módulos de vendor.
- Linha de base para o P3: `baseline_modules.txt` (429), dmesg com 20708 linhas. `kernel/lake/tools/check_regex_controls.sh` → código 0; `kernel/lake/tools/dmesg_new_errors.sh` no dmesg atual → código 0, 0 linhas.
- Alvo de rollback: o backup auditado `3890fb96…829fc7` é o stock, de antes do C0.3. Voltar para ele é seguro (T-1 provado), mas tira o KernelSU até um novo flash.

## Resultado do P2 e do P3 (2026-10-26, frase do dono: "sim, flash do ULTRA-kernel no boot_b")

- Pre-flight: 1 aparelho; `current-slot=b`, `is-userspace=no`, `unlocked=yes`, `partition-size:boot_b=4000000`, `slot-successful:b=yes`, `slot-unbootable:b=no`, `slot-retry-count:b=1`; arquivo com 67108864 bytes, magic `ANDROID!`, sha256 `36bc72755e82152f510d298dd1ac1641b604143abbec24c4876c5f1a5cf0d85e`.
- Flash pelo guard, com a allowlist de dois hashes (backup e v6): `Sending 'boot_b' (65536 KB) OKAY [1.414s]`, `Writing 'boot_b' OKAY [0.883s]`, total 2,299 s.
- T2b-post, antes do reboot: slots iguais aos de antes (`b` successful=yes, unbootable=no, retry=1; `a` successful=no, unbootable=yes).
- Reboot pelo guard: OKAY. adb em cerca de 33 s; `sys.boot_completed=1` em cerca de 64 s.
- Identidade: `uname -r` = `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`; `/proc/version` com `(rianprei-ultra-kernel@build)`; `OS3.0.306.0.WGTMIXM`; slot `_b`.
- Módulos: 428 contra 429 da linha de base. Falta só `kheaders`, que no ULTRA é embutido (`IKHEADERS=y`): `/sys/kernel/kheaders.tar.xz` existe, com 4159396 bytes. Nenhum módulo de vendor faltando.
- dmesg (lido com `su -c dmesg`, 18986 linhas, buffer a partir de 10,28 s): 0 panic, 0 Oops, 0 BUG, 0 `Unknown symbol`, 0 `disagrees about version`, 0 `exports protected symbol`, 0 `Invalid module format`, 0 falha de verificação de módulo, 0 kCFI. Os 3 `WARNING:` (BHB do eBPF sem privilégio e dois `tick_nohz_idle_*`) existem iguais no C0.3. `dmesg_new_errors.sh` → código 0, 0 linhas. Os primeiros 10 s do boot saíram do buffer circular: NÃO VERIFICADOS por log (pstore vazio, `last_kmsg` com 0 bytes); a cobertura dessa janela é a contagem de módulos.
- Funções: `wlan0` UP, `Wi-Fi is enabled`, Bluetooth `enabled: true`; `tcp_congestion_control=bbr`, 17 algoritmos disponíveis; `bond0` presente; interfaces do modem (`ccmni*`) presentes.

Veredito: P3 PASS. O rianprei-ultra-kernel v6 está no `boot_b` e boota. Observação de uso real (30 minutos, do protocolo): com o dono.

### Achados da aceitação

1. `SECURITY_DMESG_RESTRICT=y` entrou no fragmento (o classificador marcou como candidato, e a revisão manual não tirou). Efeito no aparelho: `adb shell dmesg` → `klogctl: Operation not permitted`, `kernel.dmesg_restrict=1`. O protocolo do lake lê o dmesg pelo shell. Contorno usado: `su -c dmesg`. Correção definitiva: tirar a opção no próximo build, que pede novo flash e a frase do dono.
2. `kernel/lake/tools/dmesg_new_errors.sh` dava código 0 para uma entrada que não é log (`adb: more than one device/emulator`; `dmesg: klogctl: Operation not permitted`; arquivo vazio). Corrigido: agora recusa com código 2. Teste novo no V114 (`tests/fixtures/dmesg_not_a_log.txt`); sabotagem numa cópia → `V114 FAIL`; `run_all_checks` 116 de 116.
3. O aparelho aparecia duas vezes no adb (USB e adb sem fio ativo). Os comandos da aceitação usaram `adb -d` (só USB). Se o adb sem fio não for intencional, desligue-o nas opções de desenvolvedor.
