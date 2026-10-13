# Cross-review — UltraRoot inc2 vc40000

VEREDITO: ACEITO

ACHADOS:
- `app/build.gradle.kts:21 — versionCode = 40000 — UNKNOWN se esta é a única mudança no fonte em relação ao estado aceito: não há snapshot aceito disponível, e o diretório não é um checkout Git. O conteúdo final do APK foi comparado separadamente; fora META-INF, só AndroidManifest.xml difere.`

EVIDÊNCIAS

## 1. SHA-256

Comando (cwd: `ultraroot`):
```sh
sha256sum out/UltraRoot-own-inc2-fix.apk out/UltraRoot-own-inc2-vc40000.apk
```
Saída:
```
a697228a22241b8edee89eaa1469eb12e80e7a50f302996adccc35af6165e813  out/UltraRoot-own-inc2-fix.apk
7a3ae0b1a287b39b774ab5c8d99f37e6af7d7fad10ef3c859e4edb5a4593dc5f  out/UltraRoot-own-inc2-vc40000.apk
```
Ambos coincidem exatamente com os hashes esperados.

## 2. Comparação entrada a entrada, ignorando META-INF

Comandos:
```sh
unzip -q ultraroot/out/UltraRoot-own-inc2-fix.apk -d docs/history/reports/accepted
unzip -q ultraroot/out/UltraRoot-own-inc2-vc40000.apk -d docs/history/reports/candidate
diff -qr --exclude='META-INF' docs/history/reports/accepted docs/history/reports/candidate
python3 docs/history/reports/compare_entries.py ultraroot/out/UltraRoot-own-inc2-fix.apk ultraroot/out/UltraRoot-own-inc2-vc40000.apk
```
Saída:
```
Files docs/history/reports/accepted/AndroidManifest.xml and docs/history/reports/candidate/AndroidManifest.xml differ
left non-META-INF entries=59
right non-META-INF entries=59
different entries=1
AndroidManifest.xml
```
Lista completa de conteúdo diferente fora de META-INF: somente `AndroidManifest.xml`.

## 3. Badging

Comandos:
```sh
[caminho local]/Android/Sdk/build-tools/37.0.0/aapt2 dump badging ultraroot/out/UltraRoot-own-inc2-fix.apk > docs/history/reports/accepted.badging.txt
[caminho local]/Android/Sdk/build-tools/37.0.0/aapt2 dump badging ultraroot/out/UltraRoot-own-inc2-vc40000.apk > docs/history/reports/candidate.badging.txt
diff -u docs/history/reports/accepted.badging.txt docs/history/reports/candidate.badging.txt
```

Saída literal do diff:
```diff
-package: name='me.weishu.kernelsu' versionCode='2' versionName='0.1.0-inc2' platformBuildVersionName='17' platformBuildVersionCode='37' compileSdkVersion='37' compileSdkVersionCodename='17'
+package: name='me.weishu.kernelsu' versionCode='40000' versionName='0.1.0-inc2' platformBuildVersionName='17' platformBuildVersionCode='37' compileSdkVersion='37' compileSdkVersionCodename='17'
 minSdkVersion:'31'
 targetSdkVersion:'37'
 uses-permission: name='me.weishu.kernelsu.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION'
```
Único campo alterado no badging: `versionCode`, 2 → 40000.

## 4. Manifest compilado

Comandos:
```sh
[caminho local]/Android/Sdk/build-tools/37.0.0/aapt2 dump xmltree ultraroot/out/UltraRoot-own-inc2-fix.apk --file AndroidManifest.xml > docs/history/reports/accepted.manifest.xmltree.txt
[caminho local]/Android/Sdk/build-tools/37.0.0/aapt2 dump xmltree ultraroot/out/UltraRoot-own-inc2-vc40000.apk --file AndroidManifest.xml > docs/history/reports/candidate.manifest.xmltree.txt
diff -u docs/history/reports/accepted.manifest.xmltree.txt docs/history/reports/candidate.manifest.xmltree.txt
```

Saída literal do diff:
```diff
 N: android=http://schemas.android.com/apk/res/android (line=2)
   E: manifest (line=2)
-    A: http://schemas.android.com/apk/res/android:versionCode(0x0101021b)=2
+    A: http://schemas.android.com/apk/res/android:versionCode(0x0101021b)=40000
     A: http://schemas.android.com/apk/res/android:versionName(0x0101021c)="0.1.0-inc2" (Raw: "0.1.0-inc2")
     A: http://schemas.android.com/apk/res/android:compileSdkVersion(0x01010572)=37
     A: http://schemas.android.com/apk/res/android:compileSdkVersionCodename(0x01010573)="17" (Raw: "17")
```

## 5. Assinatura do candidato

Comando:
```sh
[caminho local]/Android/Sdk/build-tools/37.0.0/apksigner verify --verbose --print-certs ultraroot/out/UltraRoot-own-inc2-vc40000.apk
```
Saída literal completa:
```
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::loadLibrary has been called by org.conscrypt.NativeLibraryUtil in an unnamed module (file:[caminho local]/Android/Sdk/build-tools/37.0.0/lib/apksigner.jar)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled

Verifies
Verified using v1 scheme (JAR signing): false
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): false
Verified using v3.1 scheme (APK Signature Scheme v3.1): false
Verified using v3.2 scheme (APK Signature Scheme v3.2): false
Verified using v4 scheme (APK Signature Scheme v4): false
Verified for SourceStamp: false
Number of signers: 1
V2 Signer: certificate DN: CN=SuperRoot, OU=lake, O=SuperRoot, C=BR
V2 Signer: certificate SHA-256 digest: efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9
V2 Signer: certificate SHA-1 digest: 6a865c03f0ff7d6c9bd39b77975fccff31df11bc
V2 Signer: certificate MD5 digest: 7e78f0198abd4470f5cf1418d9dcf218
V2 Signer: key algorithm: RSA
V2 Signer: key size (bits): 2048
V2 Signer: public key SHA-256 digest: 669135ac4a00dfff858b72288ada79c56907d9234107dba9b7ce50eb65fcf15c
V2 Signer: public key SHA-1 digest: a7977c93076a405e07c08901b363500bf7d06efc
V2 Signer: public key MD5 digest: 26a832c7559c2bf5d8e59a38234a7b9f
```

## 6. Strings de identidade nas resources

Comando:
```sh
[caminho local]/Android/Sdk/build-tools/37.0.0/aapt2 dump resources ultraroot/out/UltraRoot-own-inc2-vc40000.apk | rg -i 'KSU|KernelSU|weishu'
```
Saída:
```
Package name=me.weishu.kernelsu id=7f
```

## 7. Fonte

Comandos e saídas:
```
$ rg --files --hidden ultraroot | rg '(^|/)app/build.gradle.kts$'
ultraroot/app/build.gradle.kts

$ git -C ultraroot rev-parse --show-toplevel
fatal: not a git repository (or any parent up to mount point /)
Stopping at filesystem boundary (GIT_DISCOVERY_ACROSS_FILESYSTEM not set).

$ rg -n 'versionCode|versionName' app/build.gradle.kts
21:        versionCode = 40000
22:        versionName = "0.1.0-inc2"
```
Resultado: UNKNOWN quanto a ser a única mudança relativa ao fonte aceito; não existe cópia/snapshot do estado aceito disponível para diff. Linhas atuais registradas acima.

## 8. Prova de sabotagem do comparador

Comando: `python3 docs/history/reports/sabotage_apk.py`. O script copiou o candidato para `docs/history/reports/sabotado.apk`, regravou a cópia alterando um byte de `classes.dex`, e chamou o mesmo comparador da etapa 2 contra o candidato.

Saída literal:
```
mutated classes.dex[0]: 0x64 -> 0x65; bytes_changed=1
copied candidate then wrote modified archive: docs/history/reports/sabotado.apk
left non-META-INF entries=59
right non-META-INF entries=59
different entries=1
classes.dex
```
A comparação detectou a entrada adulterada; a prova passou.
