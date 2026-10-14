# Revisão independente — accept_ksu_root, fastboot_guard e diagnóstico UltraRoot

Escopo cumprido: leitura local e inspeção dos artefatos; nenhum adb/fastboot real foi executado, nenhum device foi acessado nesta revisão. A inspeção do estado do aparelho fica limitada aos artefatos históricos citados abaixo.

## A1 — accept_ksu_root.sh

**Veredito: FALHA** — a correção das aspas e as validações por conteúdo funcionam nos artefatos, mas as novas supressões de status permitem aprovar resposta parcial de comando que terminou com erro.

### Diff e sintaxe

Comando:
`diff -u [artefato local, fora do repo]/device-grant/before/accept_ksu_root.before-quote-fix.sh docs/ultraroot/accept_ksu_root.sh`

Saída literal relevante (diff rc=1, esperado porque há diferenças):
```diff
-  id_out="$(adb -s "$serial" shell su -c id | tr -d '\r')"
-  probe="$(adb -s "$serial" shell su -c 'if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info' | tr -d '\r')"
+  id_out="$(adb -s "$serial" shell su -c id | tr -d '\r' || true)"
+  probe="$(adb -s "$serial" shell "su -c 'if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info'" | tr -d '\r' || true)"
-  uapi="$(printf '%s\n' "$probe" | sed -n 's/^uapi_version:[[:space:]]*//p' | head -n 1)"
+  uapi="$(printf '%s\n' "$probe" | sed -n 's/^uapi_version:[[:space:]]*//p' | head -n 1 || true)"
```
As únicas linhas modificadas são as atuais 31, 32 e 44 ([accept_ksu_root.sh:31](docs/ultraroot/accept_ksu_root.sh:31), [accept_ksu_root.sh:32](docs/ultraroot/accept_ksu_root.sh:32), [accept_ksu_root.sh:44](docs/ultraroot/accept_ksu_root.sh:44)). `bash -n docs/ultraroot/accept_ksu_root.sh` imprimiu `bash_n_rc=0`.

A aspa dupla externa faz o shell local passar a expressão remota como um argumento: `su -c 'if ...; then ...; fi'`. As aspas simples internas sobrevivem ao parsing local e chegam ao shell remoto, onde agrupam todo o script como argumento de `su -c`. No original, as aspas simples eram consumidas localmente; o adb recebia a expressão sem esse agrupamento, e o shell remoto encontrava `then` fora do `if`. Isso coincide com as evidências de shim: `A_old.txt` contém `erro de sintaxe próximo ao token inesperado then`; `A_new.txt` não contém erro sintático e termina em `REPROVA sem ksud e sem /data/adb/ksu` porque o shim não fornece ksud.

### Validações e falha de status

As reprovações por saída vazia/inválida estão presentes: contexto Magisk sai 1 ([accept_ksu_root.sh:49](docs/ultraroot/accept_ksu_root.sh:49), [accept_ksu_root.sh:50](docs/ultraroot/accept_ksu_root.sh:50)); ausência simultânea de ksud e diretório KSU sai 1 ([accept_ksu_root.sh:60](docs/ultraroot/accept_ksu_root.sh:60), [accept_ksu_root.sh:62](docs/ultraroot/accept_ksu_root.sh:62)); UAPI diferente de 5 sai 1 ([accept_ksu_root.sh:65](docs/ultraroot/accept_ksu_root.sh:65), [accept_ksu_root.sh:67](docs/ultraroot/accept_ksu_root.sh:67)); `id_out` sem `uid=0(` sai 1 ([accept_ksu_root.sh:71](docs/ultraroot/accept_ksu_root.sh:71), [accept_ksu_root.sh:74](docs/ultraroot/accept_ksu_root.sh:74)); contexto vazio sai 1 ([accept_ksu_root.sh:78](docs/ultraroot/accept_ksu_root.sh:78), [accept_ksu_root.sh:80](docs/ultraroot/accept_ksu_root.sh:80)). Assim, `|| true` não converte esses valores vazios ou inválidos em aprovação.

Mas ele **mascara o status não zero** das coletas de `id_out` e `probe`: o `true` torna bem-sucedida a substituição de comando mesmo se adb/su falhar depois de emitir saída. Se a saída parcial ainda contiver `uid=0(`, marcador KSU e `uapi_version: 5`, as checagens de conteúdo podem aprovar apesar da falha de transporte/comando. Os arquivos de teste não cobrem esse caso. Portanto, a afirmação ampla de que `|| true` “não mascara falhas” não é verdadeira; as validações só cobrem conteúdo.

Evidência existente, lida sem repetir teste no aparelho:
- `A_old.txt`: `sh: -c: linha 1: erro de sintaxe próximo ao token inesperado then`.
- `A_new.txt`: `sh: linha 1: /data/adb/ksud: Arquivo ou diretório inexistente`; depois `REPROVA sem ksud e sem /data/adb/ksu`.
- `B2_ksu.txt`: `APROVA ksu serial=SERIALTEST uid=0 uapi=5`.
- `B2_magisk.txt`: `REPROVA contexto magisk: uid=0(root) gid=0(root) context=u:r:magisk:s0`.
- `B_noopt.txt`: `sem fixture: coleta adb exige ACCEPT_KSU_ALLOW_ADB=1`. Confirmação host-only com argumento serial e ambos os opt-ins removidos produziu essa mesma linha e `no_fixture_rc=2`; o gate exige opt-in na [accept_ksu_root.sh:23](docs/ultraroot/accept_ksu_root.sh:23) e sai na [accept_ksu_root.sh:25](docs/ultraroot/accept_ksu_root.sh:25), antes de verificar/invocar adb.
- `C_device.txt` (artefato anterior, não repetido nesta revisão): `REPROVA contexto magisk: uid=0(root) gid=0(root) groups=0(root) context=u:r:magisk:s0`.

## A2 — fastboot_guard.sh

**Veredito: OK, com selftest UNKNOWN** (o caminho de cache por operação está limpo estaticamente; o diretório-pai de cache permanece).

Comando: `git -C kernel/lake diff -- [fora do repo] tools/fastboot_guard.sh`.

Saída literal do diff:
```diff
-    GDIR="$(mktemp -d "${TMPDIR:-/tmp}/fastboot_guard.XXXXXX")" || refuse "mktemp falhou"
+    GDIR="$(mkdir -p "${TMPDIR:-$HOME/.cache/fastboot_guard}" && mktemp -d "${TMPDIR:-$HOME/.cache/fastboot_guard}/fastboot_guard.XXXXXX")" || refuse "mktemp falhou"
```
O mesmo hunk aparece uma vez no caminho `flash` ([fastboot_guard.sh:93](kernel/lake/tools/fastboot_guard.sh:93)) e uma vez em `rollback` ([fastboot_guard.sh:114](kernel/lake/tools/fastboot_guard.sh:114)); não há outras mudanças no diff. `bash -n kernel/lake/tools/fastboot_guard.sh` imprimiu `bash_n_rc=0`.

O trap está em [fastboot_guard.sh:41](kernel/lake/tools/fastboot_guard.sh:41); a função em [fastboot_guard.sh:40](kernel/lake/tools/fastboot_guard.sh:40) remove recursivamente `GDIR` no EXIT quando o diretório existe. A cópia da imagem e o arquivo temporário vivem dentro desse diretório por operação. Quando `TMPDIR` não está definido, o pai `$HOME/.cache/fastboot_guard` criado por `mkdir -p` não é removido e pode permanecer vazio; com `TMPDIR` definido, permanece o pai correspondente. Logo, não há vazamento do diretório/imagem temporários pelo fluxo de EXIT, mas há persistência do pai de cache.

A substituição de comando encadeia `mkdir -p ... && mktemp -d ...` e mantém `|| refuse "mktemp falhou"` nas linhas 93 e 114. Se mkdir ou mktemp falhar, o status não zero chega ao `|| refuse`; [fastboot_guard.sh:60](kernel/lake/tools/fastboot_guard.sh:60) imprime `BLOQUEADO pelo fastboot_guard` e [fastboot_guard.sh:61](kernel/lake/tools/fastboot_guard.sh:61) sai 2. O comportamento de recusa não é suprimido.

Selftest: **UNKNOWN — não executado**. Comandos `command -v fastboot; readlink -f "$(command -v fastboot)"` produziram literalmente `/usr/bin/fastboot` duas vezes, não um stub em scratch. Nenhum fastboot foi invocado.

## B — diagnóstico de “backend indisponível”

### Cadeia confirmada

1. A tela chama `KsuBackend.readAllowList()` em [RootAccessScreen.kt:28](ultraroot/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessScreen.kt:28) e mostra `ui.message` em [RootAccessScreen.kt:37](ultraroot/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessScreen.kt:37). O texto constante é `backend indisponível` em [KsuBackend.kt:30](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:30).
2. `readAllowList` chama JNI `EngineNatives.allowListUids()` ([KsuBackend.kt:71](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:71)); se JNI retorna null, converte em `KsuRead.Unavailable(UNAVAILABLE)` ([KsuBackend.kt:74](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:74)). `readNative` converte `UnsatisfiedLinkError` e falhas relacionadas em indisponível ([KsuBackend.kt:99](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:99), [KsuBackend.kt:102](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:102), [KsuBackend.kt:109](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:109)).
3. No JNI, `scan_driver_fd` procura `[ksu_driver]` em `/proc/self/fd` ([engine.cc:48](ultraroot/core-backend/src/main/cpp/engine.cc:48)) e devolve `found` ([engine.cc:54](ultraroot/core-backend/src/main/cpp/engine.cc:54)); `ksuctl` procura quando fd é negativo ([engine.cc:59](ultraroot/core-backend/src/main/cpp/engine.cc:59)) e chama `ioctl(driver_fd, op, arg)` ([engine.cc:62](ultraroot/core-backend/src/main/cpp/engine.cc:62)). `NEW_GET_ALLOW_LIST` falhando faz JNI retornar false/null ([engine.cc:78](ultraroot/core-backend/src/main/cpp/engine.cc:78), [engine.cc:109](ultraroot/core-backend/src/main/cpp/engine.cc:109)). A tela não recebe o errno.
4. No pin 6e386044, `NEW_GET_ALLOW_LIST` usa `manager_or_root` ([dispatch.c:771](kernel/upstream/KernelSU/kernel/supercall/dispatch.c:771)). Isso é UID 0 ou `is_manager()` ([perm.c:19](kernel/upstream/KernelSU/kernel/supercall/perm.c:19)); `is_manager` compara manager appId com `current_uid() % KSU_PER_USER_RANGE` ([manager_identity.h:49](kernel/upstream/KernelSU/kernel/manager/manager_identity.h:49)). Se a permissão falha, o handler retorna `-EPERM` ([dispatch.c:888](kernel/upstream/KernelSU/kernel/supercall/dispatch.c:888)). Se o FD for -1, a chamada falha no userspace antes de chegar ao handler. Ambos viram o mesmo null/mensagem no app.
5. Se allowlist funcionar mas `GET_APP_PROFILE` falhar, o app representa “perfil indisponível” (allowSu=null) ([KsuBackend.kt:80](ultraroot/core-backend/src/main/java/dev/superroot/core/KsuBackend.kt:80); [RootAccessState.kt:33](ultraroot/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessState.kt:33)), não “backend indisponível”. Isso localiza a mensagem atual no primeiro ioctl/lista ou no carregamento JNI.

### UAPI, structs, manager e identidade do APK

- O `diff -u` entre app `core-backend/src/main/cpp/uapi/supercall.h` e pin `uapi/supercall.h` terminou com `supercall_diff_rc=0`; o diff de `app_profile.h` terminou com `app_profile_diff_rc=0`. As definições relevantes coincidem.
- UAPI é 5 no header do kernel ([supercall.h:13](kernel/upstream/KernelSU/uapi/supercall.h:13)) e no build C0.3 registrado ([C03_stablekey_result.md:45](docs/history/research/phase4/C03_stablekey_result.md:45)). O log primário do build contém os valores nas linhas 36, 37 e 38 de `c03-vmlinux.log`: pacote `me.weishu.kernelsu`, signature size `818`, signature hash `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`.
- `GET_INFO` usa struct de quatro `__u32` (16 bytes) e `_IOR('K',2,...)`; `NEW_GET_ALLOW_LIST` usa dois `__u16` e array flexível `__u32 uids[0]` (header fixo 4 bytes), codificado como `_IOWR('K',6,...)`; `GET_APP_PROFILE` usa código `_IOC(_IOC_READ|_IOC_WRITE,'K',11,0)` e o payload `app_profile`. A definição de `app_profile` é igual nos dois headers ([app_profile.h:39](ultraroot/core-backend/src/main/cpp/uapi/app_profile.h:39)); no ABI AArch64 seus campos dão 784 bytes, e o kernel copia `sizeof(struct app_profile)` ([dispatch.c:364](kernel/upstream/KernelSU/kernel/supercall/dispatch.c:364)). Não achei divergência de magic, número, versão ou layout entre o código do app e o pin.
- O APK candidato consultado por `aapt2 dump badging` informa `package: name='me.weishu.kernelsu' versionCode='40000'`; Gradle mantém `applicationId = "me.weishu.kernelsu"` ([app/build.gradle.kts:18](ultraroot/app/build.gradle.kts:18)). `apksigner verify --verbose --print-certs` informa V2=true, V1/V3/V3.1/V3.2/V4=false, 1 signer e SHA-256 `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`.
- O KernelSU consulta a package list por `KSU_PACKAGE_NAME` ([throne_tracker.c:302](kernel/upstream/KernelSU/kernel/manager/throne_tracker.c:302)), pesquisa o APK em [throne_tracker.c:345](kernel/upstream/KernelSU/kernel/manager/throne_tracker.c:345) e atribui o appId em [throne_tracker.c:43](kernel/upstream/KernelSU/kernel/manager/throne_tracker.c:43) depois da validação V2 ([apk_sign.c:328](kernel/upstream/KernelSU/kernel/manager/apk_sign.c:328)). Assim, **o artefato candidato tem pacote e cert que satisfazem estaticamente os predicados do C0.3**. `versionCode` não é validado por `is_manager_apk`; pacote, assinatura V2 e UID manager importam. O pin genérico tem defaults diferentes em [Kbuild:138](kernel/upstream/KernelSU/kernel/Kbuild:138) e [Kbuild:139](kernel/upstream/KernelSU/kernel/Kbuild:139), mas o log do build C0.3 registra os overrides corretos em 818/efb8.
- O APK contém `lib/arm64-v8a/libultraroot.so`; a ELF extraída em scratch é AArch64/stripped, depende apenas de `liblog.so`, `libm.so`, `libdl.so`, `libc.so`, e exporta os cinco símbolos JNI `kernelVersion`, `kernelUapi`, `managerUapi`, `allowListUids`, `readProfile`. Não há dependência ausente de `libc++_shared.so` nesse artefato.

Comandos e trechos das saídas observadas:

```text
diff -u ultraroot/core-backend/src/main/cpp/uapi/supercall.h kernel/upstream/KernelSU/uapi/supercall.h; echo $?
supercall_diff_rc=0
diff -u ultraroot/core-backend/src/main/cpp/uapi/app_profile.h kernel/upstream/KernelSU/uapi/app_profile.h; echo $?
app_profile_diff_rc=0
aapt2 dump badging [fora do repo] ultraroot/out/UltraRoot-own-inc2-vc40000.apk | sed -n '1p'
package: name='me.weishu.kernelsu' versionCode='40000' versionName='0.1.0-inc2' platformBuildVersionName='17' platformBuildVersionCode='37' compileSdkVersion='37' compileSdkVersionCodename='17'
apksigner verify --verbose --print-certs [fora do repo] ultraroot/out/UltraRoot-own-inc2-vc40000.apk
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): false
Verified using v3.1 scheme (APK Signature Scheme v3.1): false
Verified using v3.2 scheme (APK Signature Scheme v3.2): false
Verified using v4 scheme (APK Signature Scheme v4): false
Number of signers: 1
V2 Signer: certificate SHA-256 digest: efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9
unzip -l [fora do repo] ultraroot/out/UltraRoot-own-inc2-vc40000.apk | rg 'libultraroot|libc\+\+_shared'
     8704  1981-01-01 01:01   lib/arm64-v8a/libultraroot.so
readelf -d [fora do repo] docs/history/reports/libultraroot-vc40000.so | rg 'NEEDED|SONAME'
NEEDED Shared library: [liblog.so]
NEEDED Shared library: [libm.so]
NEEDED Shared library: [libdl.so]
NEEDED Shared library: [libc.so]
SONAME Library soname: [libultraroot.so]
llvm-nm -D --defined-only [fora do repo] docs/history/reports/libultraroot-vc40000.so | rg 'Java_dev_superroot_core_EngineNatives'
0000000000004da0 T Java_dev_superroot_core_EngineNatives_kernelVersion
0000000000004e28 T Java_dev_superroot_core_EngineNatives_kernelUapi
0000000000004eac T Java_dev_superroot_core_EngineNatives_managerUapi
0000000000004eb4 T Java_dev_superroot_core_EngineNatives_allowListUids
0000000000005044 T Java_dev_superroot_core_EngineNatives_readProfile
rg -n 'Manager package name|Manager signature size|Manager signature hash' [artefato local, fora do repo]/grok/notes/c03-vmlinux.log
36:-- KernelSU Manager package name: me.weishu.kernelsu
37:-- KernelSU Manager signature size: 818
38:-- KernelSU Manager signature hash: efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9
wc -c [artefato local, fora do repo]/device-grant/logcat_ultraroot.txt
0 logcat_ultraroot.txt
```

### Causa provável e limites

**Causa provável imediata:** o primeiro `NEW_GET_ALLOW_LIST` não está retornando uma lista utilizável na sessão do app. O cenário de maior interesse é o runtime não ter um FD `[ksu_driver]` para esse processo ou o kernel ativo ainda não ter reconhecido/coroado o UID manager, resultando em EBADF/EPERM que o JNI reduz a null e o Kotlin reduz a “backend indisponível”. Não encontrei erro de ABI no fonte nem incompatibilidade estática de pacote/cert do APK candidato.

**Hipóteses descartadas para o artefato analisado:** (1) applicationId divergente: badging/Gradle e KSU package coincidem; (2) assinatura divergente: V2 e SHA coincidem com o registro C0.3; (3) número/magic/layout UAPI divergente: headers idênticos; (4) biblioteca/carga por falta de `libc++_shared.so` ou de exports JNI: o candidato empacota a lib arm64 com dependências do sistema e exports esperados. Essas conclusões são sobre o candidato, não provam qual arquivo está instalado no aparelho.

**B UNKNOWN:** sem adb e com `logcat_ultraroot.txt` de 0 bytes, não dá para observar o processo instalado, se o kernel em execução é o C0.3, se `track_throne` registrou o manager appId, se o FD foi injetado no processo, qual errno retornou nem se a lib carregou naquele device. `C_device.txt` anterior mostra `context=u:r:magisk:s0` para `su`, mas o Root Access não chama `su`; isso não prova o estado dos ioctls KSU.

### Teste sugerido — somente depois de aprovação do dono; não executado

1. Confirmar no device o pacote/cert do APK instalado e o KSU ativo/UAPI=5.
2. Em sessão de diagnóstico aprovada, registrar no processo do app se `/proc/self/fd` contém `[ksu_driver]`; registrar também `driver_fd`, retorno de `GET_INFO` e `NEW_GET_ALLOW_LIST` e `errno` (sem alterar allowlist/perfis).
3. Capturar logs do KernelSU em torno de `track_throne`: pacote encontrado, validação V2, “Crowning manager” e instalação do FD para o UID; comparar esse UID com o app.
4. Interpretar: FD ausente/EBADF aponta entrega do FD; FD válido + EPERM aponta manager appId/assinatura/package no kernel em execução; ENOTTY aponta [fora do repo] kernel/interface diferente; `UnsatisfiedLinkError` aponta carregamento/JNI. Reabrir o app depois que a identidade manager tiver sido registrada.

