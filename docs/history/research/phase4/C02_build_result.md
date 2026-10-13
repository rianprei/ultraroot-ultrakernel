# C0.2 — kernel com a versão real e o cert do nosso manager

Data: 2026-10-11. Host. A pristina `~/lake-build/src` não foi editada (continua sem `drivers/kernelsu`). O rebuild foi na cópia `[artefato local, fora do repo]/grok/c01-build/src`. Sem adb, sem fastboot, sem install.

Veredito: **C0.2 PRONTO**.

## Edições

Arquivo `common/drivers/kernelsu/Kbuild` da cópia.

- Linha 121: `KSU_VERSION := 32673`. O pin `6e386044` tem `rev-list --count` 2673, e a fórmula do projeto é `30000 + 2673`. A cópia mora dentro do git do kernel, então o detector do próprio `Kbuild` não marca `KSU_GIT_VERSION_VALID` e o fallback de estoque compilava 16. O sandbox do Bazel também não leva um `.git` aninhado. Por isso o número fica fixo nessa linha, com o `ccflags` `-DKSU_VERSION=$(KSU_VERSION)` na linha 123.
- Linhas 135–136: `KSU_EXPECTED_SIZE ?= 744` e `KSU_EXPECTED_HASH ?= b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f`. Esse é o cert V2 do APK `KernelSU_v3.3.0-72-g6e386044_32673-debug.apk`.
- `manager/apk_sign.c:328` não mudou. Já chama `check_v2_signature(path, EXPECTED_SIZE, EXPECTED_HASH)`, e esses macros saem do `ccflags` nas linhas 144–145 do `Kbuild`.

## Build

Mesma receita do C0.1: `--jobs=1`, `--local_cpu_resources=1`, `--local_ram_resources=4096`, `--//build/kernel/kleaf:lto=none`, `SOURCE_DATE_EPOCH=1752273969`. Guarda pararia o Bazel se `MemAvailable` ficasse abaixo de 1 GiB por 30s. Não disparou. Mínimo 5275100 kB. Disco 48%. Exit 0 em 564s. Log `[artefato local, fora do repo]/grok/notes/c02-vmlinux.log`.

O `Image` descompactado e o `vmlinux` contêm:

```text
KernelSU: welcome to KernelSU version 32673, package name me.weishu.kernelsu
b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f
```

O hash antigo `c371061b19d8c7d7d6133c6a9bafe198fa944e50c1b31c9d8daa8d7f1fc2d2d6` não aparece no kernel empacotado. `kernelsu_init` continua em `ffffffc081d2fb40`.

## Gate

`tools/gate_kmi_crc.sh` em `Module.symvers`, exit 0:

```text
modules.files=557 modules.unique=370
compared=2309 mismatches=0 missing_exports=0 conflicting_crcs=0
export_type_mismatches=0 namespace_mismatches=0 unresolved=0
PASS
```

## Boot

`repack_boot_v2.py` com `--drop-signature`, footer `NONE`, rollback 0, partição 67108864. Roundtrip do gzip ok.

`[artefato local, fora do repo]/grok/c01-build/C02_ksu_boot_b.img`

| Campo | Valor |
|---|---|
| Tamanho | 67108864 |
| Magic | `ANDROID!` |
| header_version | 4 |
| ramdisk_size | 0 |
| signature_size | 0 |
| kernel_size | 14634809 |
| sha256 | `9a34176da6bcf22695a44b2e3a0d2bd2e844c357ee2858e0b5360e9f79b92c3b` |
| vermagic | `6.6.89-android15-8-4k SMP preempt mod_unload modversions aarch64` |

Contra o `C01_ksu_boot_b.img`: o header de 4096 bytes é igual fora do byte no offset 8 (`kernel_size` 14634767 → 14634809). Os dois gzips caem na mesma página alinhada, fim em 14639104. O footer `AVBf` de 64 bytes é idêntico (algoritmo `NONE`, vbmeta em 14639104, 512 bytes). O blob vbmeta de 512 bytes mudou: salt novo `e905cf1351be4b6919359e9732eb76020e50be8571bc33b360e6d4bf55bd2f09`, digest `3bd2902505ca1a68ac7e834ac097eea0243e9c1998e2f7083b75d561500152ac`. O resto do arquivo, do fim desse vbmeta até o footer, é igual. Então não é só o segmento do kernel: mudam o kernel, um byte do `kernel_size` e o vbmeta.

## Produção

O cert casado aqui é o debug keystore desta máquina (`~/.android/debug.keystore`, DN Android Debug). Ele permanece estável enquanto esse arquivo existir. Outra máquina, ou um keystore apagado, gera outro par tamanho/hash e este kernel deixa de reconhecer o APK. Para produção, a assinatura do manager tem de ser uma chave fixa, e o `Kbuild` tem de nascer com o tamanho e o hash dessa chave.

## Aberto

- Nada foi escrito no aparelho. O boot que está rodando continua o do C0.1, versão 16, hash `c371061b…`.
- O vbmeta deste `C02` é `NONE`, como o do C0.1. O salt não se repete num repack sem `--salt`.
- Chave fixa de produção ainda não existe.
