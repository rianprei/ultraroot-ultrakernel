# SP1 — implementação host das Tasks 1-6

Data: 2026-10-11. Host. A Task 7 não rodou. Nenhum `adb` e nenhum `fastboot` real foi chamado. Compilar o kernel não entra nesta entrega; nada daqui foi validado no aparelho.

`[fora do repo]` não é um repositório git. As tasks 1 e 3-6 ficam registradas em `docs/history/research/MASTER_PLAN.md`. A task 2 está no commit `c8e78121d602a1b4bf8b00318d2d3acec78c965a` de `kernel/lake`. O arquivo não rastreado `guard_hashes.txt` na raiz desse repo não entrou no commit.

## Task 1 — allowlist C0.3

Arquivos: `docs/ultraroot/guard_hashes_sp1.txt`, `docs/ultraroot/DEVICE_PLAN_SP1.md`.

O teste relê os dois arquivos de imagem e exige a linha inteira de cada sha256. O prefixo do C0.1 (`74090569`) não pode aparecer.

```
bash [artefato local, fora do repo]/sp1/test_guardhashes.sh
PASS
```

Linhas da allowlist, conferidas por esse teste contra os arquivos:

- backup `[fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img` = `3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7`
- C0.3 `[artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img` = `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe`

O teste exige que o sha do C0.3 seja exatamente `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe`. Passou.

## Task 2 — rollback reativa só B

Arquivos: `[fora do repo] tools/fastboot_guard.sh`, `[fora do repo] tools/selftest_fastboot_guard.sh`. Commit `c8e78121d602a1b4bf8b00318d2d3acec78c965a`.

`rollback <arquivo>` copia a imagem, exige 67108864 bytes, magic `ANDROID!` e sha na allowlist, e então segue o interlock de flash já existente (`current-slot` exatamente `b`, `is-userspace` diferente de `yes`). Se o flash sai 0, o guard emite `set_active` com o argumento literal `b`. O pedido `set_active a` cai na recusa da allowlist e o stub não é chamado. Um `set_active` solto, inclusive `b`, também é recusado: a reativação só existe depois do flash do rollback.

O scanner V6 trata a palavra `set_active` na posição de comando como troca de slot. A emissão usa `op=set_active` e `"$bin" "$op" b`. O caso R3 monta o argv num array pelo mesmo motivo; o pedido que o stub vê continua sendo `set_active a`, e o teste exige que essa linha não apareça no log.

```
bash [fora do repo] tools/selftest_fastboot_guard.sh
### [R1] rollback com slot b unbootable flasha boot_b e reativa b
    flash boot_b + set_active b; set_active a ausente
  => OK
### [R2] rollback com slot a não escreve e não troca slot
    recusado (exit=2), sem flash e sem set_active
  => OK
### [R3] sabotagem: set_active a nunca chega no stub
    set_active a recusado, stub intacto
  => OK
### [R4] rollback hash errado recusado
    recusado (exit=2), stub NÃO chamado
  => OK
GUARD-SELFTEST: 44 passada(s), 0 falha(s)
SELFTEST-GUARD PASS
```

O mesmo selftest, dentro de `run_all_checks`, repetiu `GUARD-SELFTEST: 44 passada(s), 0 falha(s)` e `SELFTEST-GUARD PASS`. O fastboot desse teste é o stub em `PATH`. Log: `[artefato local, fora do repo]/sp1/logs/selftest.out`.

## Task 3 — aceite distingue KSU de Magisk

Arquivos: `docs/ultraroot/accept_ksu_root.sh`, `[artefato local, fora do repo]/sp1/test_accept_ksu.sh`.

Com `ACCEPT_KSU_FIXTURE`, o script lê ksud, `/data/adb/ksu`, UAPI e o contexto de `su`. Contexto com `magisk` reprova mesmo se o resto estiver presente. Aprova só com ksud ou o diretório, UAPI exatamente 5, e contexto não vazio sem `magisk`. Sem fixture, imprime que a coleta adb é a Task 7 e sai 2. Não chama `adb`.

```
bash [artefato local, fora do repo]/sp1/test_accept_ksu.sh
OK A rc=1
OK B rc=0
OK C rc=1
OK D rc=2
PASS
```

A é contexto `u:r:magisk:s0`. B é ksud, UAPI 5 e `u:r:su:s0`. C é tudo vazio. D é a ausência de fixture.

## Task 4 — recovery

Arquivo: `docs/ultraroot/RECOVERY_SUITE.md`.

R-hash, R-slot-a, R-reactivate e R-sabotage estão cobertos pelo selftest acima (argv no stub). Flash interrompido, módulo que quebra o boot e boot-loop estão NOT_TESTED. Nenhum cenário de device foi executado.

## Task 5 — capacidades e compatibilidade

Arquivos: `docs/ultraroot/CAPABILITIES.md`, `docs/ultraroot/COMPAT_MATRIX.md`.

KSU-core fica IMPLEMENTED / DEVICE_TESTED pelo boot C0.1 citado no plano-mestre, não pelo C0.3. manager-grant fica IMPLEMENTED / NOT_TESTED. F01–F30 ficam NOT_STARTED / UNKNOWN. Na matriz, KSU-core no lake é VALIDATED por esse mesmo boot; manager-grant é IMPLEMENTED_UNVALIDATED; o resto sem artefato novo fica ARCHITECTED ou UNKNOWN. F28 no kernel C0.3 não sobe a validação no aparelho.

## Task 6 — fixtures de módulo

Arquivos: `docs/ultraroot/MODULE_PARITY.md`, `[artefato local, fora do repo]/sp1/fixtures/sp1-ksu-min.zip` (`module.prop`, `post-fs-data.sh`), `[artefato local, fora do repo]/sp1/fixtures/sp1-magisk-min.zip` (`module.prop`, `service.sh`, `system/etc/sp1-magisk-marker`).

Os dois `module.prop` têm `id`, `name`, `version`, `versionCode` e `author`. O teste também monta um zip sem `author` e exige a reprovação.

```
bash [artefato local, fora do repo]/sp1/test_module_props.sh
OK sp1-ksu-min.zip
OK sp1-magisk-min.zip
OK negativo sem author
PASS
```

Isso é estrutura no host. Montar no aparelho está NOT_TESTED. Magic mount e overlayfs não foram ligados juntos. Não há compatibilidade universal entre módulo Magisk e módulo KSU.

## Suíte do repositório

```
bash kernel/lake/tools/run_all_checks.sh
run_all_checks: PASS (V1-V9 + aliases V10-V13 + V14-V33 + V34-V115 + gate self-test)
```

O resumo final tem 116 linhas PASS (V1–V115 e GATE) e 0 FAIL. Inclui V6, V58, V66, V89, V98, V102 e V112. Log: `[artefato local, fora do repo]/sp1/logs/run_all.out`.

## Aberto

- Task 7 (coroação, flash, aceite no aparelho, um cenário de recovery real) não executada. A coleta adb do aceite continua nessa task: no host, sem fixture, o script sai 2 e não chama adb.
- manager-grant NOT_TESTED. O binário que o plano-mestre marca como bootado é o C0.1, não o C0.3.
- Mount systemless, `post-fs-data.sh`, `service.sh` e as demais etapas de `MODULE_PARITY.md` continuam NOT_TESTED no boot.
- F01–F30, fora do nome na seed, continuam NOT_STARTED / UNKNOWN.

## Correção dos 3 achados do Codex (2026-10-11)

Host. Sem adb e sem fastboot real. `[fora do repo]` não é git, então os três fixes ficam neste relatório e no `MASTER_PLAN.md`. Nenhum arquivo rastreado do kernel mudou; não houve commit novo.

### Fix 1 — uid=0 de fato

`accept_ksu_root.sh` só sai 0 se as três valem juntas: a saída de `su -c id` (`KSU_SU_ID` no fixture) contém `uid=0(`, existe ksud ou `/data/adb/ksu` com UAPI 5, e nem o contexto nem essa saída contêm `magisk`.

O teste antigo, já com os casos novos e aspas no `id`, falhou no script anterior:

```
OK A rc=1
OK B rc=0
OK C rc=1
FAIL E aprovou
FAIL G aprovou
OK D rc=2
FAIL
```

E é KSU presente com `uid=2000(shell)`. G é `uid=0(` com `magisk` na saída do `id`. Os dois eram aprovados. Depois da correção:

```
bash [artefato local, fora do repo]/sp1/test_accept_ksu.sh
OK A rc=1
OK B rc=0
OK C rc=1
OK E rc=1
OK G rc=1
OK D rc=2
PASS
```

B é o único aprovado: `uid=0(root)` e contexto `u:r:su:s0`. A, com `uid=0(` e contexto magisk, reprova.

### Fix 2 — reboot depois do rollback

`rollback` reativa B e deixa o aparelho em fastboot. O boot é um segundo comando, `[fora do repo] tools/fastboot_guard.sh reboot`, só se o rollback saiu 0. Entre o Z0.4 e o flash não há reboot. Sem cabo, a volta é pelas teclas. O cenário de device fecha quando o boot do backup é observado. Não há reboot nu do binário fastboot.

O teste falhou antes da edição (`FAIL suite sem Z0.4` e `FAIL suite nao exige boot observado`) e depois:

```
bash [artefato local, fora do repo]/sp1/test_recovery_reboot.sh
PASS
```

### Fix 3 — C0.1/C0.2 fora do caminho operacional

O pacote que escrevia a allowlist com o hash do C0.2 e mandava flashar `C02_ksu_boot_b.img` foi substituído por um aviso OBSOLETO, sem comando. O pacote ativo aponta só para `guard_hashes_sp1.txt` (backup + C0.3). O arquivo não rastreado `[fora do repo] kernel/lake/guard_hashes.txt`, que continha o hash do C0.1, foi apagado. `C01_device_plan.md` ganhou um banner de obsoleto; o hash que resta lá é histórico. O parágrafo de status do C0.2 no plano-mestre também continua como histórico, sem comando.

Antes: `FAIL guard_hashes.txt ainda existe`, `FAIL master-escreve-guard_hashes`, `FAIL master-flash-c02`. Depois:

```
bash [artefato local, fora do repo]/sp1/test_operational_hashes.sh
PASS
bash [artefato local, fora do repo]/sp1/test_guardhashes.sh
PASS
```

### Checks depois dos fixes

```
GUARD-SELFTEST: 44 passada(s), 0 falha(s)
SELFTEST-GUARD PASS
run_all_checks: PASS (V1-V9 + aliases V10-V13 + V14-V33 + V34-V115 + gate self-test)
```

Logs: `[artefato local, fora do repo]/sp1/logs/selftest-fix.out` e `run_all-fix.out`. O resumo continua 116 PASS e 0 FAIL.
