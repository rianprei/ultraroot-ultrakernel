# Recovery suite — SP1

Data: 2026-10-11. Status de device: NOT_TESTED. Nada foi flashado nesta sessão.

| ID | Cenário | Onde se prova | Procedimento | Sucesso | Status |
|---|---|---|---|---|---|
| R-hash | Guard recusa imagem fora da allowlist | Host | `tools/selftest_fastboot_guard.sh` casos 14 e R4 | exit do caso OK; stub não recebe `flash` | host-simulável; ver o selftest do dia |
| R-slot-a | Rollback com slot ≠ b não escreve | Host | caso R2 do mesmo selftest (`current-slot: a`) | exit ≠ 0; log sem `flash` e sem `set_active` | host-simulável |
| R-reactivate | B unbootable, rollback reativa B | Host (argv); device NOT_TESTED | caso R1: `rollback` do backup com `slot-unbootable:b: yes` | log tem `flash boot_b` e a linha `set_active b`; não tem `set_active a` | argv no fake fastboot; boot real NOT_TESTED |
| R-sabotage | Pedido `set_active a` | Host | caso R3 | exit ≠ 0; stub não é chamado | host-simulável |
| R-flash-fail | Flash interrompido no meio | Device | voltar por teclas ao fastboot; `rollback` do backup; em seguida `tools/fastboot_guard.sh reboot`. Sem reboot entre o Z0.4 e o flash | o backup boota e isso é observado no Android | NOT_TESTED |
| R-bad-module | Módulo que quebra o boot | Device | teclas até o fastboot; `rollback`; depois `tools/fastboot_guard.sh reboot` | boot do backup observado; módulo não fica ativo | NOT_TESTED |
| R-bootloop | Boot-loop | Device | teclas até o fastboot; `rollback`; depois reboot pelo guard | `current-slot` continua `b`; boot do backup observado | NOT_TESTED |

O rollback de produção é `tools/fastboot_guard.sh rollback <backup>` com `FASTBOOT_GUARD_HASHES` em `guard_hashes_sp1.txt`. O alvo da reativação no código é o literal `b`. Esse comando termina no fastboot: reativa B e não liga o Android.

O boot seguinte é outro comando, `tools/fastboot_guard.sh reboot`, e só depois que o rollback saiu 0. O interlock desse reboot continua valendo (slot exatamente `b`, userspace diferente de yes). Entre o Z0.4 e o flash não há reboot. Se o cabo não servir, a recuperação é pelas teclas, de volta ao fastboot. O cenário de device só fecha quando o boot do backup é observado. Não existe reboot nu do binário fastboot.
