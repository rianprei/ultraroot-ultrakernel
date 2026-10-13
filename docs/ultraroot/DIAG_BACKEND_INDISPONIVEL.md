# Diagnóstico: "backend indisponível" no UltraRoot (2026-10-11/12)

## Sintoma
Root Access e Kernel & Performance mostram "backend indisponível" no aparelho 2410FPCC5G (lake, kernel 6.6.89-android15-8-4k).

## Evidências (somente leitura no aparelho)
1. APK instalado = artefato. `pm path` → `/data/app/~~QEuahgjQJQOP9BU_bBc9Hg==/me.weishu.kernelsu-.../base.apk`; sha256 `a4a0403772bb12c207c608508831a2f1d82a79d5d2b54aa87d05b764905c632b` (ui2), igual ao artefato.
2. Lado do kernel: `uname -r` = `6.6.89-android15-8-4k`; `/data/adb` tem `ksu` e `ksud`.
3. Processo do app (pid 23512, antes da ui3): `ls -l /proc/<pid>/fd | grep -i ksu` → 0 linhas. Sem FD `[ksu_driver]`.
4. dmesg, busca de manager (últimas 2 execuções no dump):
   - `KernelSU: Searching manager me.weishu.kernelsu...`
   - `search_manager: dir: /data/app got magic! 0xf2f52010` (F2FS)
   - `search_manager: skip: /data/app/~~pUG9J92FSqwmJKf9_IaCEw== magic: 0x5346434e49 expected: 0xf2f52010` (e as demais pastas de app)
   - `Search manager finished` sem manager encontrado.
5. Pasta atual do app: `stat -f -c %T` → `fstype=0x46434e49` (incfs), montada (`/proc/mounts`).
6. Código (`kernel/manager/throne_tracker.c`, `search_manager`): o magic de referência é o do superbloco de `/data/app`; pastas filhas com magic diferente são puladas e o APK não é examinado.

## Causa provável
O `adb install` usou "Performing Incremental Install". Isso cria a pasta do app em incremental-fs (incfs), que tem magic `0x5346434e49`. A busca do manager do KernelSU pula essa pasta, então o manager não é coroado e o `ioctl` não entrega o FD. Sem FD, a leitura da allowlist falha e a UI mostra "backend indisponível". Outros apps instalados em incfs também são pulados (`~~qKHc…`, `~~LOI4…`, `~~KeEa…`, `~~QEua…`).

Hipóteses descartadas (ver também `REVIEW_accept_guard_diagnostico_codex4.md`): applicationId, assinatura (cert `efb8d49f`, V2 apenas), layout de UAPI, carga da biblioteca nativa.

## Teste proposto (depende de "sim")
Reinstalar o mesmo APK ui3 (vc40003) com `pm install -r` a partir de `/data/local/tmp`, sem o caminho incremental do adb. Resultado esperado:
- `stat -f` da nova pasta do app = F2FS (`0xf2f52010`).
- dmesg da busca do manager com `Found manager base.apk ... is_manager: 1`.
- Root Access lê a allowlist (ou mostra outro erro, que passa a ser diagnóstico novo).
Se o teste falhar, o APK e os dados permanecem (`-r`), e nada é gravado em partição.

## Não verificado
- Se a checagem de assinatura (`check_v2_signature`, EXPECTED_SIZE 818 = tamanho do certificado DER, EXPECTED_HASH `efb8d49f…`) passa para este APK: a leitura do código diz que sim, mas só o teste confirma.
- Retorno de errno dos `ioctl` na sessão do app (não há log do app; `logcat` não traz linhas).
- Efeito em outros apps instalados em incfs (não testado).

## Resultado do teste (2026-10-12)
- `pm install -r` do ui3 (vc40003) a partir de `/data/local/tmp` (sha256 conferido no aparelho) → Success. Pasta nova do app = F2FS.
- dmesg: `Found manager base.apk at path: /data/app/~~pVyoCVgeyGmjxz_t6gdmfg==/.../base.apk, is_manager: 1` e `Crowning manager: uid=10296`. A hipótese se confirma: a causa era a pasta incfs da instalação incremental.
- Root Access passou de "backend indisponível" para o estado vazio real ("Nenhum app com root"): a allowlist do kernel é lida.
- Ainda não há grant. A UI da inc2/ui3 é somente leitura; conceder e revogar é da inc3. Aceite (`accept_ksu_root.sh`) continua REPROVA por contexto magisk: o `su` do adb ainda roteia pelo Magisk (coroação pendente).
- Evidências: `docs/history/evidence/2026-10-12_*`.
