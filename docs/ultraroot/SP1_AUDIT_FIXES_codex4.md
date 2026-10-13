# SuperRoot SP1 — correções da auditoria final (Codex #4)

**VEREDITO:** itens (a), (b) e (c) verificados/corrigidos com evidência host. Nenhum device foi consultado ou alterado. Nenhum `adb` ou `fastboot` real foi executado. Todos os comandos de coleta KSU usaram stubs em `[excluído: pasta sp1-audit de testes locais; fora do repo]/stubs/`; o selftest do guard cria um `fastboot` fake e recebeu `TMPDIR` dentro do scratch autorizado.

A busca requisitada encontrou o contrato em `docs/history/research/phase4/CONTRATO_codex_final_audit.txt`. O contrato registra que C0.1 bootou e pede verificar risco de boot/root/rollback (linhas 3–10). A lista literal não inclui um relatório de resultados; usei os critérios do contrato e os artefatos listados pelo pedido.

```text
$ grep -rl FINAL_AUDIT [fora do repo] 2>/dev/null
docs/history/research/phase4/CONTRATO_codex_final_audit.txt
docs/history/reports/inventory_matches.txt
docs/history/reports/todo_structure.txt
docs/history/reports/todos-sabotado.md
docs/ultraroot/TODOs.md
```

## ITEM (a) — C0.3 é o único alvo atual de flash

`guard_hashes_sp1.txt` já continha o hash do backup e o hash completo de C0.3. O hash C0.1 não está na allowlist:

```text
$ sha256sum [artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img [artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img
2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe  [artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img
74090569d7fc4883159ea1fe14a484c4e3a3b14458aa84605aa3f520d78a1120  [artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img

$ nl -ba docs/ultraroot/guard_hashes_sp1.txt
     1  3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7
     2  2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe
```

Corrigi o plano para não tratar o par C0.3 como já validado no aparelho, rotulei as referências a C0.1 como históricas e deixei o flash da Task 7 apontando a C0.3:

```text
$ rg -n -i 'C0\.1|C01|74090569|C0\.3|C03|2977e1dc|ACCEPT_KSU_ALLOW_ADB' [fora do repo]/docs/superpowers/plans/2026-10-11-ultraroot-sp1.md
5:**Goal:** Fazer o KernelSU conceder root pelo NOSSO manager no POCO C75 (coroação), com a tooling de flash/aceite/recuperação corrigida e os artefatos de cobertura iniciados — sem escrever código novo de manager (usa C0.3 + APK release com certificado já verificado).
7:**Architecture:** O alvo da coroação é kernel C0.3 `2977e1dc` + manager release cert `efb8d49f`; a validação de C0.3 no device continua pendente da Task 7. O par é o control-plane+adapter baseline do backend KSU-integrado. SP1 NÃO modifica o manager/ksud (isso é SP3). SP1 entrega: (1) 3 fixes da auditoria na tooling do repo `poco-c75-lake-kernel`; (2) scaffolds dos artefatos de cobertura em `docs/ultraroot/`; (3) harness de paridade modular por etapas (fixtures host + checklist device-gated); (4) o procedimento de coroação+aceite no device, executado SÓ sob "sim" do dono.
25:3. **Hash errado na allowlist** — guard_hashes apontando hashes históricos de C0.1/C0.2 em vez do alvo atual C0.3 faria o guard recusar (ou pior, aceitar imagem errada). → Task 1.
39:- [ ] **Step 1: Teste que falha** — script que monta guard_hashes a partir dos arquivos reais e exige que contenha EXATAMENTE os sha de backup e C0.3, e NÃO os hashes históricos de C0.1(`74090569`)/C0.2:
49:grep -q 74090569 docs/ultraroot/guard_hashes_sp1.txt && { echo FAIL c01-present; exit 1; } # hash histórico de C0.1, proibido na allowlist do alvo atual
98:- [ ] **Step 1:** CAPABILITIES.md com o cabeçalho de campos (ID, descrição, origem/provenance, status IMPL, status VALID, deps, android/kernel/arch/device, riscos, teste+evidência, lacunas) e SEED das capacidades já conhecidas das matrizes `phase1/GROK_matriz_features.md` + `phase3/*`. Status honesto: KSU-core = IMPLEMENTED; a evidência DEVICE_TESTED de C0.1 é histórica, enquanto C0.3 é o alvo atual e permanece NOT_TESTED até Task 7; manager-grant = IMPLEMENTED/NOT_TESTED (aguarda coroação); resto = NOT_STARTED/UNKNOWN.
114:- [ ] **Step 3 (device):** T-1 flash backup → C0.3 flash → reboot (guard).
116:- [ ] **Step 5 (aceite):** `ACCEPT_KSU_ALLOW_ADB=1 accept_ksu_root.sh <serial>` (Task 3; coleta adb exige opt-in explícito) → root POR KSU; `dmesg_new_errors` rc0; 429 módulos; wifi/bt; 1 módulo KSU e 1 Magisk da Task 6 (paridade por etapa, declarar o que montou). Registrar evidência (path/hash/data) no CAPABILITIES/COMPAT.
```

Resultado: não há C0.1/C01/74090569 como alvo de flash; C0.1 fica explicitamente histórico. `guard_hashes_sp1.txt` não foi alterado.

## ITEM (b) — rollback só pode reativar B

O snapshot `scratch/grok/sp1-audit/before/fastboot_guard.sh` é byte-idêntico ao guard atual, portanto não serve como versão anterior ao fix:

```text
$ cmp -s [excluído: pasta sp1-audit de testes locais; fora do repo]/before/fastboot_guard.sh kernel/lake/tools/fastboot_guard.sh
grok_before_equals_current_rc=0
```

A comparação FALHA/PASSA usa a versão histórica no pai do commit que introduziu a rota:

```text
$ git -C kernel/lake rev-parse c8e7812^ c8e7812
ffe583a0e8b40bfbc9610f7adcf81a6fd98cf218
c8e78121d602a1b4bf8b00318d2d3acec78c965a

$ git -C kernel/lake log -1 --format='%h %s' c8e7812
c8e7812 guard: rollback flashes boot_b then reactivates slot b

$ git -C kernel/lake show c8e7812^:tools/fastboot_guard.sh | rg -n 'rollback|DO_REACTIVATE|set_active'
(no matches; rg exit 1)
```

No guard atual, `rollback` valida o arquivo, troca para `flash boot_b`, e marca `DO_REACTIVATE=1` (linhas 108–128). O caminho flash exige `current-slot == b` (linhas 159–168). Após flash com retorno zero, o único argumento enviado é o literal `b` (linhas 184–192); não há expressão de slot derivada de entrada do operador:

```text
$ rg -n 'set_active' kernel/lake/tools/fastboot_guard.sh
189:  op=set_active
190:  "$bin" "$op" b

$ rg -n 'set_active[[:space:]]+a' kernel/lake/tools/fastboot_guard.sh
no literal set_active a target
```

Estendi `selftest_fastboot_guard.sh`: o stub registra a resposta `slot-unbootable:b: yes`; R1 exige essa resposta, a consulta, `set_active b` e ausência de `set_active a`. A execução anterior válida retornou FALHA em R1; a primeira tentativa antes disso foi inválida (guard copiado sem bit executável, rc 126). Depois apliquei `chmod +x` somente no scratch e rodei de novo:

```text
$ TMPDIR=[excluído: pasta sp1-audit de testes locais; fora do repo]/tmp /usr/bin/bash [excluído: pasta sp1-audit de testes locais; fora do repo]/before_tree/tools/selftest_fastboot_guard.sh
before_exit=1
### [R1] rollback com slot b unbootable flasha boot_b e reativa b
    ESPERAVA flash boot_b e set_active b sem set_active a; rc=2
  => FALHA
GUARD-SELFTEST: 43 passada(s), 1 falha(s)
V66 FAIL fastboot guard behavioral selftest
SELFTEST-GUARD FAIL

$ TMPDIR=[excluído: pasta sp1-audit de testes locais; fora do repo]/tmp /usr/bin/bash [excluído: pasta sp1-audit de testes locais; fora do repo]/after_tree/tools/selftest_fastboot_guard.sh
after_exit=0
### [R1] rollback com slot b unbootable flasha boot_b e reativa b
    fastboot stub + estado:
getvar partition-size:boot_b
getvar current-slot
getvar is-userspace
flash boot_b [excluído: pasta sp1-audit de testes locais; fora do repo]/tmp/fastboot_guard.EZYZCl/image.bin sha=a654ecfcdc6439a3a421c6c5a29ed998a7f8b91e47b1c6808c172f8354e5372f mode=400
getvar slot-unbootable:b
FAKE-STATE slot-unbootable:b: yes
set_active b
    set_active b chamado; set_active a ausente
  => OK
GUARD-SELFTEST: 44 passada(s), 0 falha(s)
V66 OK fastboot guard behavioral selftest (fake fastboot, allow+deny+update+oem+TOCTOU)
SELFTEST-GUARD PASS
```

O guard de produção não precisava de alteração; o selftest foi reforçado para provar que a resposta do cenário é `unbootable=yes` e para imprimir o log do stub.

## ITEM (c) — aceite distingue KSU; fixture não pode ser sobrescrita pelo ambiente

A revisão do diff do Grok identificou um falso positivo reproduzível: antes da correção, variáveis `KSU_*` do ambiente sobrescreviam o que o `adb` coletava. O `adb` falso devolveu `uid=2000(shell)` e “no KSU evidence”, mas variáveis forjadas fizeram o script aprovar:

```text
$ nl -ba [excluído: pasta sp1-audit de testes locais; fora do repo]/stubs/negative-adb/adb
3: if [ "${6:-}" = id ]; then
4:   printf '%s\n' 'uid=2000(shell) gid=2000(shell) context=u:r:shell:s0'
6:   printf '%s\n' 'no KSU evidence'

$ env PATH=[excluído: pasta sp1-audit de testes locais; fora do repo]/stubs/negative-adb:/usr/bin:/bin STUB_LOG=[excluído: pasta sp1-audit de testes locais; fora do repo]/negative-adb-before.log KSU_SU_CONTEXT=u:r:su:s0 KSU_UAPI=5 KSU_HAS_KSUD=yes KSU_HAS_DIR=yes KSU_SU_ID='uid=0(root) gid=0(root) context=u:r:su:s0' /usr/bin/bash [excluído: pasta sp1-audit de testes locais; fora do repo]/accept-before-codex4-fix.sh SERIALTEST
negative_adb_forged_env_before_rc=0
APROVA ksu serial=SERIALTEST uid=0 uapi=5
ADB -s SERIALTEST shell su -c id
ADB -s SERIALTEST shell su -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info
```

Corrigi `accept_ksu_root.sh`: os campos `KSU_*` só são lidos quando `ACCEPT_KSU_FIXTURE` está explicitamente definido. Sem fixture, coleta adb exige `ACCEPT_KSU_ALLOW_ADB=1`; sem opt-in o script retorna 2 antes de procurar ou executar `adb`. Atualizei a Task 7 do plano para registrar esse opt-in explícito. Repeti os cenários:

```text
fixture-magisk-after rc=1
REPROVA contexto magisk: u:r:magisk:s0
fixture-ksu-after rc=0
APROVA ksu serial=SERIALTEST uid=0 uapi=5
fixture-no-root-after rc=1
REPROVA sem ksud e sem /data/adb/ksu

no-fixture-stub-path-after rc=2
sem fixture: coleta adb exige ACCEPT_KSU_ALLOW_ADB=1
stub_log=absent (adb stub not called)

magisk adb=[excluído: pasta sp1-audit de testes locais; fora do repo]/stubs/magisk/adb
stub-magisk-after rc=1
REPROVA contexto magisk: uid=0(root) gid=0(root) context=u:r:magisk:s0
ADB -s SERIALTEST shell su -c id
SU -c id
ADB -s SERIALTEST shell su -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info
SU -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info

ksu adb=[excluído: pasta sp1-audit de testes locais; fora do repo]/stubs/ksu/adb
stub-ksu-after rc=0
APROVA ksu serial=SERIALTEST uid=0 uapi=5
ADB -s SERIALTEST shell su -c id
SU -c id
ADB -s SERIALTEST shell su -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info
SU -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info

negative-adb-forged-after rc=1
REPROVA sem ksud e sem /data/adb/ksu
ADB -s SERIALTEST shell su -c id
ADB -s SERIALTEST shell su -c if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info
```

## Limites e revisão pendente

- Nenhum build, flash, reboot, instalação, `adb` real ou `fastboot` real foi executado. O hash foi lido localmente; boot de C0.3, grant real do manager, output real de `ksud debug info` e recuperação no bootloader continuam device-gated.
- Em execução de device, o operador terá de usar `ACCEPT_KSU_ALLOW_ADB=1`; sem isso o script encerra antes de chamar adb.
- Autor das mudanças: Codex #4. **revisor pendente: grok (quota)**.
