# Linha do tempo (resumo)

- **2026-10-05:** backup do lake (incluindo dtbo_a e dtbo_b). Fonte: `docs/ultrakernel/SUPERKERNEL_VISION.md`, linha 22.
- **2026-10-10:** lake com T-1 e T3 validados no aparelho. Fonte: `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-v3.md`, linha 12.
- **2026-10-11:** especificação rev.7 aprovada; SP1 no host (tarefas 1 a 6); manager e kernel C0.3 verificados no host. Fonte: `docs/history/research/MASTER_PLAN.md`, linhas 35 a 63.
- **2026-10-12:** "backend indisponível" resolvido com `pm install -r` a partir de `/data/local/tmp` (incfs). Fonte: `docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md`.
- **2026-10-13 (verificação do clone publicado, commit `0f3fbe3c6`):** `Image` compilada a partir do clone, com `CONFIG_KSU=y` e 0 avisos; gate de CRC PASS (2309 símbolos, 0 divergências); UK-3 com 116 de 116 após corrigir a redação indevida do cert público (no commit publicado havia 3 falhas); UK-5 com 44 de 44; UltraRoot com cache Gradle vazio: 20 e 41 testes passam, e o APK de debug é gerado. Fonte: `docs/history/reports/uk2_image_clone_2026-10-13.txt`, `uk3_run_all_checks_corrigido_2026-10-13.txt`, `uk4_gate_kmi_crc_2026-10-13.txt`, `uk5_selftest_fastboot_guard_2026-10-13.txt`, `app_gradle_clone_2026-10-13.txt`.
- **2026-10-13:** H1 (flags GET_INFO), H2 (grant e revoke no host), H3 (build ui10 assinado em V2), H4 (docs), H5 e H6; H8 (escrita JNI) com revisão aprovada. Fonte: `docs/history/handoffs/HANDOFF-ultraroot-2026-10-13-v3.md`, linhas 11 a 21; `docs/history/reports/review_h8r_2026-10-13.txt`.
