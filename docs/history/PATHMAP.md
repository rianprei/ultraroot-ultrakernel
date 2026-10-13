# Mapa de caminhos (origem local → repo)

Este arquivo é a única exceção: contém caminhos originais de propósito.

| Origem | Destino no repo |
|---|---|
| `~/superroot/docs/ultraroot/` | `docs/ultraroot/` |
| `~/superroot/docs/ultraroot/HANDOFF-*.md` | `docs/history/handoffs/` |
| `~/superroot/docs/ultraroot/evidence/` | `docs/history/evidence/` (PNG excluído) |
| `~/superroot/scratch/grok/ultraroot-app/` | `ultraroot/` (sem build, chaves e binários) |
| `~/superroot/scratch/codex4/h8/` | `ultraroot/core-backend/src/test/native/` |
| `~/superroot/scratch/grok/src/KernelSU/` (commit 6e38604) | `kernel/upstream/KernelSU/` (7 arquivos) |
| `~/superroot/research/` | `docs/history/research/` (sem c01_dmesg.txt; visão do UltraKernel em `docs/ultrakernel/`) |
| `~/superroot/scratch/codex4/*.txt` e `~/superroot/scratch/opencode2/review_*.txt` | `docs/history/reports/` |
| `~/superroot/scratch/maestro/prompt_*.txt` | `docs/history/prompts/` |
| `~/Documentos/mods/poco-c75-lake-kernel/` (commit c8e7812) | `kernel/lake/` (snapshot completo, `git archive`) |
| `~/superroot/scratch/grok/c01-build/src/common/` (base 5a0ffb4 + delta) | `kernel/common/` (sem `.git`; delta em `kernel/patches/`) |

Exceções de /tmp/: nenhuma no repo além de `tools/verify_repo.sh`, que cita o padrão para detectá-lo.

## Exceções declaradas
- `/tmp/` aparece como exemplo de procedimento em textos do lake (`kernel/lake/`). O repo não grava nada em `/tmp`. O verificador não bloqueia esses textos.
- `kernel/lake/tools/selftest_backprop.sh` tem um token falso (`ghp_` seguido de letras `A`) como fixture do teste de redação. O verificador não bloqueia esse arquivo.
- `kernel/common/**/*.log`: fixtures do KUnit do upstream, não logs nossos.
