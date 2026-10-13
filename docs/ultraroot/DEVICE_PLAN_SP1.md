# DEVICE_PLAN SP1 — coroação (não executado)

Data do plano: 2026-10-11. Host. A Task 7 não rodou. Nenhum `adb` e nenhum `fastboot` real saiu desta sessão.

## Imagem e allowlist

| Papel | Caminho | sha256 |
|---|---|---|
| Backup de `boot_b` | `[fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img` | `3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7` |
| Kernel C0.3 | `[artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img` | `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe` |

Allowlist do guard neste plano: `docs/ultraroot/guard_hashes_sp1.txt`. As duas linhas são esses sha256, uma por linha. Os sha de C0.1 (`74090569…`) e de C0.2 não entram.

Manager que o C0.3 autoriza: `[artefato local, fora do repo]/grok/manager/out/SuperRoot-manager-release.apk`, certificado público `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`.

## Slot

Antes de qualquer escrita, ler o slot ativo. No POCO C75 (lake) a exceção documentada é: o slot que boota é B. Slot A é o OS 3.0.20.0 antigo e está unbootable. `set_active a` fica proibido. Se o slot lido não for `b`, a frase do guard é `desligue por teclas e encerre`.

## Roteiro gated

Só com "sim" presencial, aparelho em fastboot pelas teclas, bateria ≥ 60%. Wrapper: `tools/fastboot_guard.sh` com `--hashes` apontando para `guard_hashes_sp1.txt`.

1. Z0: `getvar current-slot` tem de ser `b`; produto lake; `is-userspace` não é `yes`; `partition-size:boot_b` parseia para 67108864.
2. Z0.4 não é seguido de reboot. O flash vem na sequência da leitura.
3. T-1: `flash boot_b` do backup. O reboot, se houver, é `tools/fastboot_guard.sh reboot`, depois do flash sair 0.
4. Flash: `flash boot_b` do `C03_ksu_boot_b.img`, e o reboot seguinte também pelo guard.
5. Coroação: `adb install` do APK release. Abrir o manager. Desativar o Magisk herdado, sem desinstalar.
6. Aceite: `docs/ultraroot/accept_ksu_root.sh <serial>`. Exit 0 só com as três juntas: `su -c id` contém `uid=0(`, ksud ou `/data/adb/ksu` com UAPI 5, e contexto sem `magisk`.
7. Recuperação de ensaio: `rollback <backup>` no guard. Isso flasha `boot_b` e reativa o slot `b`, e o aparelho continua em fastboot. Em seguida, `tools/fastboot_guard.sh reboot`. O cenário só fecha quando o boot do backup é observado. Sem cabo, teclas. Não existe caminho que envie `set_active a`.

Comando de ensaio do rollback, quando o gate abrir:

```bash
FASTBOOT_GUARD_HASHES=docs/ultraroot/guard_hashes_sp1.txt \
  kernel/lake/tools/fastboot_guard.sh \
  rollback [fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img
# só com o rollback em 0; o guard recusa se o slot não for b
FASTBOOT_GUARD_HASHES=docs/ultraroot/guard_hashes_sp1.txt \
  kernel/lake/tools/fastboot_guard.sh \
  reboot
```
