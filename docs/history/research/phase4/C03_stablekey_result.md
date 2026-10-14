# C0.3 — chave de assinatura fixa

Data: 2026-10-11. Host. A pristina `~/lake-build/src` não foi editada (continua sem `drivers/kernelsu`). O rebuild foi na cópia `[artefato local, fora do repo]/grok/c01-build/src`. Sem adb, sem fastboot, sem install.

Veredito: **C0.3 PRONTO**.

A senha não está neste arquivo. O keystore fica em `[chaves locais do dono, fora do repo]/superroot-release.jks`. O diretório `[chaves locais do dono, fora do repo]/` está na linha `keys/` de `[fora do repo]/.gitignore`. `[fora do repo]` não é um repositório git.

## Certificado

Chave RSA 2048, alias `superroot`, DN `CN=SuperRoot, OU=lake, O=SuperRoot, C=BR`. O certificado público exportado tem 818 bytes.

| Campo | Valor |
|---|---|
| Keystore | `[chaves locais do dono, fora do repo]/superroot-release.jks` |
| Cert DER | `[chaves locais do dono, fora do repo]/superroot-release-cert.der` |
| SHA-256 do certificado | `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9` |
| Tamanho | 818 |

`manager/apk_sign.c` compara o certificado cru do bloco v2. `CERT_MAX_LENGTH` é 1024 (linha 118). Um certificado maior que isso é recusado antes do hash. 818 cabe. O único id de assinatura aceito, fora o padding `0x42726577`, é `0x7109871a` (v2, linha 247).

## Manager

`[artefato local, fora do repo]/grok/manager/out/SuperRoot-manager-release.apk`

| Campo | Valor |
|---|---|
| Tamanho | 31144711 |
| sha256 do APK | `c8b66244d91dabac62cd1dc95363e6aa458a203cc8dbbd3bd2635f7b69f73956` |
| v1 | false |
| v2 | true |
| v3 / v3.1 / v3.2 / v4 | false |
| SHA-256 do certificado v2 | `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9` |
| Chave | RSA 2048 |

O APK de debug continua no mesmo diretório. Este kernel não reconhece o certificado de debug.

## Kernel

Arquivo `common/drivers/kernelsu/Kbuild` da cópia.

- Linha 121: `KSU_VERSION := 32673`.
- Linhas 135–136: `KSU_EXPECTED_SIZE ?= 818` e `KSU_EXPECTED_HASH ?= efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`.
- Pacote padrão `me.weishu.kernelsu`. O log do build imprimiu esse nome, tamanho 818 e o hash acima.
- `uapi/supercall.h` linha 13: `KERNEL_SU_UAPI_VERSION = 5`. Não foi alterado.
- `.config` linha 6700 `CONFIG_KSU=y`. Linha 6702 `# CONFIG_KSU_DISABLE_MANAGER is not set`.
- `manager/apk_sign.c:328` continua `check_v2_signature(path, EXPECTED_SIZE, EXPECTED_HASH)`.

Mesma receita: `--jobs=1`, `--local_cpu_resources=1`, `--local_ram_resources=4096`, `--//build/kernel/kleaf:lto=none`, `SOURCE_DATE_EPOCH=1752273969`. A guarda de RAM não disparou. Mínimo 5035288 kB. Disco 49%. Bazel exit 0 em 519.766s. O script registrou `BUILD_EXIT rc=0`. Log `[artefato local, fora do repo]/grok/notes/c03-vmlinux.log`.

O `Image` (36657664 bytes, mtime 2026-10-11 13:39:11), o `Image.gz` descompactado e o `vmlinux` (177992256 bytes) contêm:

```text
KernelSU: welcome to KernelSU version 32673, package name me.weishu.kernelsu
efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9
```

Os hashes `b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f` e `c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6` não aparecem. A string `version 16,` não aparece. `kernelsu_init` continua em `ffffffc081d2fb40`. Vermagic `6.6.89-android15-8-4k SMP preempt mod_unload modversions aarch64`.

`Image.gz` tem 14634849 bytes, sha256 `899e7cc802fd3c7da2a5c76a2b7d612b3765a020e4f5e7fcb62692fc22157068`.

## Gate

`[fora do repo] tools/gate_kmi_crc.sh` em `kernel_aarch64_Module.symvers`, exit 0:

```text
modules.files=557 modules.unique=370
symbols.required=4138
compared=2309 mismatches=0 missing_exports=0 conflicting_crcs=0
export_type_mismatches=0 namespace_mismatches=0 unresolved=0
PASS
```

## Boot

`repack_boot_v2.py` com `--drop-signature`, footer `NONE`, rollback 0, partição 67108864. Roundtrip do gzip ok: os 14634849 bytes do kernel no `OUT` são o `Image.gz`.

`[artefato local, fora do repo]/grok/c01-build/C03_ksu_boot_b.img`

| Campo | Valor |
|---|---|
| Tamanho | 67108864 |
| Magic | `ANDROID!` |
| header_version | 4 |
| ramdisk_size | 0 |
| signature_size | 0 |
| kernel_size | 14634849 |
| sha256 | `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe` |
| vermagic | `6.6.89-android15-8-4k SMP preempt mod_unload modversions aarch64` |

Contra o `C02_ksu_boot_b.img`: o header de 4096 bytes é igual fora do byte no offset 8 (`kernel_size` 14634809 → 14634849). Os dois gzips caem na mesma página alinhada, fim em 14639104. O footer `AVBf` de 64 bytes é idêntico (algoritmo `NONE`, vbmeta em 14639104, 512 bytes). O blob vbmeta de 512 bytes mudou: salt `2ecccaf94f5b1535905cb873e8246d640800002558bf627b2a5956372def6cba`, digest `26ba2e213b055273d8c04c6f6638a5d04e63dea1c01226089cb780017d880bc4`. Do fim desse vbmeta até o footer, os bytes são iguais. Mudam o kernel, um byte do `kernel_size` e o vbmeta.

## Aberto

- Nada foi escrito no aparelho.
- O vbmeta deste `C03` é `NONE`. O salt não se repete num repack sem `--salt`.
- O par estável é este keystore com este kernel. O `C02_ksu_boot_b.img` ainda espera o certificado de debug.
