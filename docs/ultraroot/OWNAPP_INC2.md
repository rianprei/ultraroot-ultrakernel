# UltraRoot — app próprio, incremento 2

Projeto: `ultraroot/`. Continua o app próprio do incremento 1. Não é tela do manager KernelSU. Não foi instalado. Host only.

## Root Access

A seção Root Access deixa de ser placeholder. `UltraRootApp` chama `RootAccessScreen`. A tela lê `KsuBackend.readAllowList()` em `Dispatchers.IO` e desenha o resultado de `presentRootAccess`.

O caminho nativo é `EngineNatives.allowListUids` → ioctl `KSU_IOCTL_NEW_GET_ALLOW_LIST` (`core-backend/src/main/cpp/engine.cc`). Cada uid passa por `readProfile`. Perfil null vira linha com `allowSu` ausente e o texto `perfil indisponível`. `allowSu=false` lido de verdade continua `false`. Não há grant nem revoke.

Estados desenhados:

| Estado | Texto |
|---|---|
| leitura ainda não voltou | `Lendo a allowlist do kernel…` |
| backend indisponível | `backend indisponível` |
| lista vazia | `nenhum app com root concedido` |
| lista com entradas | `ListItem`: pacote (ou `—` se o nome vier vazio) e `uid N  allowSu=true/false` |
| perfil da entrada falhou | a entrada entra na lista; supporting é `uid N  perfil indisponível` |

Tema: o mesmo Material 3 dark do incremento 1 (`surface` `#14161A`, texto `#E8E6E3`). Sem tela, ícone ou string do manager KSU.

## Prova no host

Reexecução `./gradlew :core-backend:testDebugUnitTest :app:testDebugUnitTest --rerun-tasks` em 2026-10-11. `BUILD SUCCESSFUL`. 8 testes, 0 falhas.

`RootAccessPresenterTest` (`app/build/test-results/testDebugUnitTest/TEST-dev.superroot.ultraroot.ui.RootAccessPresenterTest.xml`, timestamp `2026-10-11T21:33:03.794Z`, tests=5 failures=0):

- `unavailableShowsBackendMessage` — `KsuRead.Unavailable` vira a mensagem `backend indisponível`.
- `emptyAllowListShowsEmptyState` — lista vazia usa `nenhum app com root concedido`.
- `populatedAllowListShowsUidPackageAndAllowSu` — uid 10123 / `dev.example.tool` / allowSu true e uid 10147 / `dev.example.plain` / allowSu false.
- `failedProfileDoesNotBecomeAllowSuFalse` — uid 1000 fica com `allowSu` null.
- `readAllowListWithoutLibIsUnavailable` — `KsuBackend.readAllowList()` nesta JVM, sem `libultraroot.so`, devolve `Unavailable` e a mesma mensagem. Não estoura.

`KsuBackendHostTest` (timestamp `2026-10-11T21:33:02.375Z`, tests=3 failures=0): capability sem lib, allowlist sem lib, perfil null não vira `allowSu=false`.

Depois da troca da string, a mesma dupla rodou de novo com `--rerun-tasks`. `KsuBackendHostTest` timestamp `2026-10-11T21:43:10.348Z`, tests=3 failures=0. `RootAccessPresenterTest` timestamp `2026-10-11T21:43:11.458Z`, tests=5 failures=0.

O host não executa o ioctl. Lista vazia e lista populada foram provadas no presenter, com `KsuRead` montado no teste. O caso sem lib chama o backend de verdade.

## Strip

`libc++_shared` saiu do release. `core-backend` liga com `-DANDROID_STL=c++_static`. O link de `libultraroot.so` usa `-Wl,--gc-sections -Wl,--strip-all`.

Antes, extraído de `out/UltraRoot-own-inc1.apk` para `out/measure-inc1/`:

```
-rw-r--r-- 1 rianprei rianprei  9290184 11 out 18:29 libc++_shared.so
-rw-r--r-- 1 rianprei rianprei    71968 11 out 18:29 libultraroot.so
```

`file` no `libultraroot.so` do inc1: `ELF 64-bit LSB shared object, ARM aarch64, version 1 (SYSV), dynamically linked, for Android 31, built by NDK r29 (14206865), BuildID[sha1]=b95e95d5243bc22f61f4562cb54c41de7c50f164, with debug_info, not stripped`.

`libc++_shared.so` do inc1: `with debug_info, not stripped`, NDK r28, 9290184 bytes.

APK inc1: 32650823 bytes. `out/UltraRoot-own-inc1.apk`.

Depois, `lib/arm64-v8a/libultraroot.so` extraído do APK assinado do inc2 para `out/measure-inc2-signed/libultraroot.so`:

```
-rw-r--r-- 1 rianprei rianprei     8704 11 out 18:33 libultraroot.so
```

`file`: `ELF 64-bit LSB shared object, ARM aarch64, version 1 (SYSV), dynamically linked, for Android 31, built by NDK r29 (14206865), BuildID[sha1]=f3467d50691d094cea0c9c42b6d187d97fd08ed6, stripped`.

`unzip -l` do APK assinado não lista `libc++_shared.so`. O `.so` nosso continua só em `arm64-v8a`, 8704 bytes. `llvm-nm -D` ainda exporta `kernelVersion`, `kernelUapi`, `managerUapi`, `allowListUids`, `readProfile`.

APK inc2 assinado: 23295483 bytes. Diferença contra o inc1: 9355340 bytes.

O log `out/gradle-own-inc2.log` linha 66 ainda diz `Unable to strip the following libraries, packaging them as they are: libultraroot.so`. O linker já tinha passado `--strip-all`. O `file` do `.so` empacotado diz `stripped`.

`libandroidx.graphics.path.so` continua no APK (arm64-v8a 10096, armeabi-v7a 7252, x86 9284, x86_64 10760). É dependência do Compose, não a STL de 9 MB.

## Correção do texto visível

O cross-review rejeitou o APK `out/UltraRoot-own-inc2.apk` (sha256 `b2b0d4bcb0a460a287e5bbd2347b149737bf63066622655d1494a92a27b93eaa`) porque `strings.xml` exibia `Versão KSU: %1$d`. O `name` `kp_kernel_version` ficou. O valor passou a `Versão do root no kernel: %1$d`.

`rg -n -i 'Vers.o KSU|KernelSU|Kernel SU|\bKSU\b|weishu' app/src/main` ficou vazio. Sobram só símbolos de código, fora desse padrão: `KsuBackend`, `KsuRead`, `KsuCapability`. Não são texto de tela. `applicationId` continua `me.weishu.kernelsu` no Gradle, fora de `app/src/main`.

`aapt2 dump resources` do APK corrigido casa `KSU|KernelSU|weishu` só na linha `Package name=me.weishu.kernelsu id=7f`. O recurso `string/kp_kernel_version` vale `Versão do root no kernel: %1$d`.

## APK

Artefato rejeitado, ainda no disco: `[fora do repo] ultraroot/out/UltraRoot-own-inc2.apk`  
sha256 `b2b0d4bcb0a460a287e5bbd2347b149737bf63066622655d1494a92a27b93eaa`

Artefato corrigido: `[fora do repo] ultraroot/out/UltraRoot-own-inc2-fix.apk`  
sha256 `a697228a22241b8edee89eaa1469eb12e80e7a50f302996adccc35af6165e813`

Os dois têm 23295483 bytes. O sha256 mudou. O `.so` extraído do APK corrigido continua `stripped`, BuildID `f3467d50691d094cea0c9c42b6d187d97fd08ed6`, e `libc++_shared` continua ausente.

`apksigner verify --print-certs` do APK corrigido: um signer, v1 false, v2 true, v3 false, v3.1 false, v3.2 false, v4 false. DN `CN=SuperRoot, OU=lake, O=SuperRoot, C=BR`. Cert SHA-256 `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`. RSA 2048. DER exportado com `keytool -exportcert` (JDK 21): 818 bytes, mesmo SHA-256. O `.der` foi apagado.

`aapt2 dump badging`: package `me.weishu.kernelsu`, versionCode 2, versionName `0.1.0-inc2`, label `UltraRoot`, activity `dev.superroot.ultraroot.MainActivity`. `applicationId` no `app/build.gradle.kts` continua `me.weishu.kernelsu`. Namespace do código `dev.superroot.ultraroot`. Namespace do backend `dev.superroot.core`.

## Aberto

Não instalado. O ioctl `NEW_GET_ALLOW_LIST` não rodou no aparelho. Grant e revoke ficam para o incremento 3. Módulos, ferramentas de sistema, boot slot e extensões continuam placeholder. A coroa do manager no aparelho continua `NOT_TESTED`. ABI do nosso `.so`: só arm64-v8a. O aviso do AGP sobre strip não impede o `file` de dizer `stripped`, e a lib do graphics path do AndroidX permanece.
