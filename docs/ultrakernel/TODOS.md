# UltraKernel: lista de tarefas (UK-n)

Alvo: POCO C75 (modelo 2410FPCC5G, codename `lake`). Confirmar que este modelo é a variante 4G (UK-0).
Base: GKI commit `5a0ffb4` em `kernel/common/`, mais o delta nosso (`kernel/patches/ultrakernel-delta-base-5a0ffb4.diff`, `kernel/README.md`).
Regra: nada de aparelho sem "sim, <passo>". Nada de escrita em partição sem backup conferido, slot B, guard e rollback.

Estados: FEITO (com prova), NÃO INICIADO, PENDENTE-DONO (depende de entrada ou decisão sua).

## Host (sem aparelho)

- **UK-0. Ambiente de build.** clang r510928 (GKI), make, flex, bison, bc, perl, libelf, pahole, cabeçalhos do openssl. Registrar versões. Critério: `make ARCH=arm64 LLVM=1 O=<saida> gki_defconfig` sai com 0. Estado: FEITO no host (clang 23.1.1, `/usr/bin/clang`, gki_defconfig exit 0). Confirmar a variante 4G do 2410FPCC5G: PENDENTE-DONO (fonte: `docs/ultraroot/DIAG_BACKEND_INDISPONIVEL.md`, linha 4).
- **UK-1. Configuração com KernelSU.** `gki_defconfig` + `arch/arm64/configs/kernelsu.fragment` + `olddefconfig`. Critério: `CONFIG_KSU=y` no `.config`. Estado: FEITO no host (saída fora do repo).
- **UK-2. Build completo do kernel.** `make ARCH=arm64 LLVM=1 O=<saida> -j<n> Image`, e `modules` se o gate pedir. Critério: `Image` gerado, sha256 registrado. Estado: FEITO no host (clang r510928, sem avisos; `Image` sha256 `1fe3d6b8…7861a`, ver `docs/PRE-REQUISITOS.md`).
- **UK-3. Gates existentes do lake.** Rodar `kernel/lake/tools/run_all_checks.sh` e os `selftest_*.sh`. Critério: saída registrada, sem falha sem correção. Estado: NÃO INICIADO.
- **UK-4. Gate KMI/CRC de módulos.** `kernel/lake/tools/gate_kmi_crc.sh` contra `kernel/lake/tools/data/modules_required_crcs.tsv`. Depende de UK-2. Critério: zero divergência. Estado: NÃO INICIADO.
- **UK-5. Selftest do guard de fastboot.** `kernel/lake/tools/selftest_fastboot_guard.sh` (só host). Critério: 44 de 44 (fonte: `docs/history/research/MASTER_PLAN.md`, linha 63). Estado: NÃO INICIADO.
- **UK-6. Inventário de tunables dentro do GKI.** Scheduler, governors, I/O, TCP, LMK, sysctl. Lista com a opção `CONFIG_*` e o caminho do arquivo. Sem tocar vendor. Critério: lista revisada pelo dono. Estado: NÃO INICIADO.
- **UK-7. Fragmento de tuning.** `arch/arm64/configs/ultrakernel.fragment` com as opções aprovadas no UK-6, mais build de host. Critério: `olddefconfig` sem erro e diff de config registrado. Depende de UK-6 e da aprovação do dono (PENDENTE-DONO). Estado: NÃO INICIADO.

## Entrada do dono (fora do repo)

- **UK-8. Tabelas OPP do DTBO.** Mapear as tabelas de CPU (MT6768/6769) e GPU (Mali). Precisa do `dtbo_a`/`dtbo_b` do aparelho, que está no backup do dono, fora do repo. Não versionar imagens de partição. Entrada: PENDENTE-DONO. Fonte: `docs/ultrakernel/SUPERKERNEL_VISION.md`, linha 20.
- **UK-9. Drivers vendor de frequência e térmico.** RE de `cpufreq`, `gpufreq` e `thermal` (`.ko` fechados). Precisa dos módulos do aparelho. Entrada: PENDENTE-DONO. Fonte: `SUPERKERNEL_VISION.md`, linha 21.
- **UK-10. Limites de overclock e undervolt.** Valores de frequência, tensão e temperatura máxima. Decisão: PENDENTE-DONO. Fonte: `SUPERKERNEL_VISION.md`, linhas 8 e 16.
- **UK-11. Empacotamento do boot.** `kernel/lake/tools/repack_boot_v2.py` sobre a imagem stock do dono. Entrada: PENDENTE-DONO.

## Aparelho (só com "sim, <passo>")

- **UK-12. Backup e guard antes de qualquer escrita.** Backup de `boot_b` e `dtbo_b` conferido por hash. Fonte: `docs/ultraroot/guard_hashes_sp1.txt`. Estado: PENDENTE-DONO.
- **UK-13. Flash no slot B com guard.** Só depois de UK-2, UK-3 e UK-4 passarem. Estado: PENDENTE-DONO.
- **UK-14. Rollback provado.** Voltar ao backup e observar o boot. Estado: PENDENTE-DONO.
- **UK-15. Gates de uso.** Estabilidade (stress), temperatura e bateria, com registro. Estado: PENDENTE-DONO.

## Fora do escopo agora

Preloader, lk e seccfg: nunca (`SUPERKERNEL_VISION.md`, linha 27). Junção com o root (UK-16) só depois do UltraRoot coroado (E3, `docs/ultraroot/plan.md`, linha 106).
