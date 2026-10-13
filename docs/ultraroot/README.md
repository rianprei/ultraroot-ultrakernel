# Índice dos documentos do UltraRoot

Ordem de leitura: `plan.md` (§1 e §9), depois `TODOs.md` (§1), depois o resto conforme a tarefa.

| Documento | O que é |
|---|---|
| `plan.md` | Plano de execução: regras fixas (§1), estado de partida (§2), rota (§3), SP1 (§4), critérios (§5), riscos (§6), estado (§9) |
| `TODOs.md` | Checklist de produto. §1 é o SP1 |
| `CAPABILITIES.md` | O que o app promete e o que o backend entrega hoje |
| `COMPAT_MATRIX.md` | Compatibilidade de versões: flags do GET_INFO e UAPI |
| `DESIGN_M3_SELECAO.md` | Escolhas de design Material 3 |
| `DEVICE_PLAN_SP1.md` | Plano de aparelho do SP1 (todo passo exige "sim, <passo>") |
| `DIAG_BACKEND_INDISPONIVEL.md` | Diagnóstico de "backend indisponível" (causa: incfs; solução: `pm install -r` a partir de `/data/local/tmp`) |
| `OWNAPP_INC1.md`, `OWNAPP_INC2.md` | Incrementos do app próprio (assinatura, zipalign, badging) |
| `MAGISK_KSU_UI_INVENTORY.md` | Inventário das telas do Magisk e do KernelSU, base da tela própria |
| `MANAGER_AUDIT.md` | Auditoria do gerenciador |
| `MODULE_MIGRATION_lake.md` | Migração dos módulos nativos para o lake |
| `MODULE_PARITY.md` | Paridade de módulos entre Magisk e KSU |
| `RECOVERY_SUITE.md` | Suíte de recuperação (rollback e guard) |
| `SP1_IMPL_REPORT.md` | Relatório de implementação do SP1 no host |
| `guard_hashes_sp1.txt` | Allowlist de hashes do guard do fastboot |
| `accept_ksu_root.sh` | Script de aceite (exige `uid=0(` e UAPI 5 no aparelho) |

Relatórios de revisão e de execução estão em `docs/history/reports/`. Os handoffs estão em `docs/history/handoffs/`.
