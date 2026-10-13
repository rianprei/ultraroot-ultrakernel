# Manager — userspace do KernelSU

Data: 2026-10-11. Host. Nenhum `adb install`, nenhum push e nenhum flash.

Pin: `6e3860443f8fea7c5fc226277e53888033796488`. Árvore de build: `[artefato local, fora do repo]/grok/manager/KernelSU` (cópia do checkout raso, depois `git fetch --unshallow`). HEAD permanece o pin. `rev-list --count HEAD` = 2673. `git describe --tags --always` = `v3.3.0-72-g6e386044`. O checkout original em `kernel/upstream/KernelSU` continua raso.

Veredito: **MANAGER PRONTO**.

## Toolchain

| Peça | Estado |
|---|---|
| Rust | tinha `rustc`/`cargo` 1.97.1. Faltava o target `aarch64-linux-android`. `rustup target add` instalou. |
| cargo-ndk | faltava. `cargo install cargo-ndk --locked` → 4.1.2. |
| JDK | tinha 25 e 21. O projeto declara compatibilidade 21. O Gradle rodou com `JAVA_HOME=/usr/lib/jvm/java-21-openjdk`. Gradle wrapper 9.7.1. |
| SDK | já estava em `~/Android/Sdk`: platform `android-37.0`, build-tools `37.0.0`, cmdline-tools, licenças aceitas. `compileSdk` 37, `targetSdk` 37, `minSdk` 31. |
| NDK | o pin pede `29.0.14206865` (`manager/gradle/libs.versions.toml`). No disco havia `23.2.8568313` e o `ndk/magisk` r30-beta2. `sdkmanager` instalou `ndk;29.0.14206865`, exit 0. O native build do Gradle ainda puxou CMake 3.22.1 para o mesmo SDK. |
| libclang | 23.1.1, usado pelo bindgen do ksud. |

Disco no início: 486G livres.

## Versão

A fórmula do pin é `30000 + git rev-list --count HEAD`, nos dois lados (`userspace/ksud/build.rs` e `manager/build.gradle.kts`). No clone raso a conta era 1, então a versão sairia 30001. O unshallow da cópia de scratch deu 2673. Versão real deste pin: **32673**, nome **v3.3.0-72-g6e386044**.

O vmlinux do C0.1 contém a string `KernelSU: welcome to KernelSU version 16, package name me.weishu.kernelsu`. Esse 16 é o fallback do `kernel/Kbuild` quando a cópia dentro da árvore do kernel não é um git próprio. Este userspace não altera esse kernel. O manager lê a versão pelo kernel (`Natives.getVersion`). Com o kernel atual, a tela mostra 16. O `ksud` deste build carrega 32673.

## ksud

```text
cargo ndk -t arm64-v8a -P 31 build --release
```

`ANDROID_NDK_HOME=~/Android/Sdk/ndk/29.0.14206865`. O default do cargo-ndk é API 21. Nesse nível o link falhou em `sigtimedwait` e `__system_property_read_callback`. API 31 é o `minSdk` do manager. O segundo link saiu 0 em 32s.

| Arquivo | O que é |
|---|---|
| `[artefato local, fora do repo]/grok/manager/out/ksud` | ELF aarch64, Android 31, NDK r29, não stripped, 4904736 bytes. sha256 `f3434e6e46a0c7b6233a8cbe71f50ac76ec5c79766ed8e1149d09ce6e44a52ff`. Strings `32673` e `3.3.0-72-g6e386044`. |
| `[artefato local, fora do repo]/grok/manager/out/libksud.so` | A cópia stripped que o Gradle embutiu no APK, 3504096 bytes. sha256 `8c600dc9a063e91d45d8ab556d8822e1f768bf274a367de450eeec8b0b19f410`. Mesma versão nas strings. |

## APK

`./gradlew assembleDebug --no-daemon` no `manager/`, exit 0, 4 min 15 s, 52 tasks. Assinatura de debug: o plugin registrou fallback porque `KEYSTORE_FILE` não existe.

`[artefato local, fora do repo]/grok/manager/out/KernelSU_v3.3.0-72-g6e386044_32673-debug.apk`

- 31143068 bytes. sha256 `13ce313fe817fbd881db537fb64946d29507ee01e6f27597ac78adc402e4f29b`.
- `aapt dump badging`: package `me.weishu.kernelsu`, versionCode 32673, versionName `v3.3.0-72-g6e386044`, sdkVersion 31, targetSdk 37, compileSdk 37, label KernelSU.
- `lib/arm64-v8a/libksud.so` está no APK. Também há `libkernelsu.so` e `libadbroot.so` para arm64, riscv64 e x86_64. `libksud.so` só em arm64.

Certificado V2 medido com `apksigner verify --print-certs`, conferido no DER exportado do debug keystore (`android-debug-cert.der`, 744 bytes):

- DN `C=US, O=Android, CN=Android Debug`
- sha256 `b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f`

O `kernel/Kbuild` deste pin, sem override, grava `EXPECTED_SIZE=0x033b` (827) e `EXPECTED_HASH=c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6`. `is_manager_apk` exige os dois. Tamanho 744 e esse sha256 não batem. O kernel do C0.1 não trata este APK como manager.

## Plano de instalação

Parado até um "sim" do dono. Nada disto rodou.

O app, depois de instalado, chama o daemon em `nativeLibraryDir/libksud.so` (`KsuCli.kt`). O binário do ksud, por sua vez, usa `/data/adb/ksud` e o link `/data/adb/ksu/bin/ksud` (`userspace/ksud/src/defs.rs`). O root de hoje no aparelho é o Magisk herdado. O plano usa esse `su` só para criar o caminho do daemon. Não desinstala o Magisk.

```text
adb install -r [artefato local, fora do repo]/grok/manager/out/KernelSU_v3.3.0-72-g6e386044_32673-debug.apk
adb push [artefato local, fora do repo]/grok/manager/out/ksud /data/local/tmp/ksud
adb shell su -c 'install -m 755 /data/local/tmp/ksud /data/adb/ksud && mkdir -p /data/adb/ksu/bin && ln -sfn /data/adb/ksud /data/adb/ksu/bin/ksud'
```

Isso coloca o APK e o binário. Não faz o kernel aceitar o app. Para o manager ser reconhecido, o próximo kernel precisa nascer com `KSU_VERSION=32673`, `KSU_EXPECTED_SIZE=744` e `KSU_EXPECTED_HASH=b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f`. Esse rebuild não faz parte deste entregável.

## Aberto

- Kernel em execução anuncia versão 16. Userspace deste pin é 32673.
- Certificado debug (744 bytes, `b830cb51…`) diferente do par tamanho/hash gravado no C0.1.
- Install no aparelho não foi feito. Magisk continua sendo o root presente.
- `libksud.so` dentro do APK é o binário stripped. O `out/ksud` é o release com símbolos.
