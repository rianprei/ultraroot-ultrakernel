OBSOLETO como alvo operacional. Não flashar esta imagem. O alvo único é o C0.3, allowlist `docs/ultraroot/guard_hashes_sp1.txt`. O restante deste arquivo é histórico.

# C0.1 — plano de flash e aceite

Data: 2026-10-11. Preparo no host. Nenhum comando adb ou fastboot rodou para produzir este arquivo. A escrita na partição continua parada até o dono autorizar, no dia, com o aparelho no fastboot alcançado pelas teclas.

## Imagem

| Campo | Valor medido |
|---|---|
| Arquivo | `[artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img` |
| Tamanho | 67108864 |
| Magic | `ANDROID!` |
| header_version | 4 |
| ramdisk_size | 0 |
| signature_size | 0 |
| kernel_size | 14634767 |
| sha256 | `74090569d7fc4883159ea1fe14a484c4e3a3b14458aa84605aa3f520d78a1120` |

Kernel empacotado: `[artefato local, fora do repo]/grok/c01-build/src/bazel-bin/common/kernel_aarch64/Image.gz` (gzip `1f8b`, 14634767 bytes, descompacta para 36657664). O `repack_boot_v2.py` fez roundtrip: os 14634767 bytes no `boot.img` são esse gzip. A imagem descompactada contém a string `kernelsu_init` e `KernelSU`.

Comando que gerou o arquivo, receita do T2, defaults da ferramenta (`--footer-algorithm NONE`, `--rollback-index 0`, partição 67108864):

```text
python3 [fora do repo]/mods/lake-kernel/research/repack_boot_v2.py \
  [fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img \
  [artefato local, fora do repo]/grok/c01-build/src/bazel-bin/common/kernel_aarch64/Image.gz \
  [artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img \
  --drop-signature
```

O salt do footer saiu do avbtool, sem `--salt` na linha. Outro repack não reproduz este sha256. Salt medido: `5250bacfd9d41a2b9cdd0c611a9ca4d74b786a5d4a8c48e9ba5750c963d077f3`. Digest sha256 do hash descriptor: `dc30f7540f82a925a376261e12f30b6136e97212ce132e7dfdb81d5124e2223c`.

## Comparação com o backup

Backup: `[fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img`.

sha256 `3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7`. Tamanho 67108864. Magic `ANDROID!`. header_version 4. ramdisk_size 0. signature_size 0. kernel_size 14555421.

O header de 4096 bytes é igual nos dois arquivos fora do `kernel_size` (offsets 8–10). `ramdisk_size` continua 0.

O segmento do kernel diverge: outro gzip, outro tamanho. O fim do kernel alinhado a 4096 passa de 14561280 para 14639104.

O footer também diverge. No backup, `avbtool info_image` lê original image size 14577664, vbmeta em 14577664 com 1664 bytes, algoritmo `SHA256_RSA2048`, rollback 0, avbtool 1.3.0, e as props `com.android.build.boot.os_version=15`, fingerprint `Redmi/vnd_lake/lake:15/AP3A.240905.015.A2/OS3.0.306.0.WGTMIXM:user/release-keys`, `security_patch=2026-02-01`. Entre o kernel alinhado (14561280) e esse vbmeta há 16384 bytes, com magic `AVB0` em 14561280 e 14563520. A imagem nova não tem esse bloco: o vbmeta regenerado está em 14639104, 512 bytes, algoritmo `NONE`, rollback 0, sem essas props. Os dois footers `AVBf` ficam no offset 67108800 e os 64 bytes diferem. De 14639616 até o byte anterior ao footer, os dois arquivos são iguais (zeros).

`DIFERE_DO_BACKUP`: não. Mudam o kernel, o campo `kernel_size` e o footer AVB. O ramdisk permanece ausente.

## Vermagic e os 557 módulos

Vermagic extraído do kernel dentro de `C01_ksu_boot_b.img`:

```text
6.6.89-android15-8-4k SMP preempt mod_unload modversions aarch64
```

O `uname -r` que este kernel deve mostrar é `6.6.89-android15-8-4k`. A string de release do aparelho no estoque medido é `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`. O trecho depois do primeiro espaço é o mesmo nos dois: `SMP preempt mod_unload modversions aarch64`.

`same_magic` em `kernel/module/version.c` (cópia do build), quando o módulo tem CRC, avança os dois lados até o primeiro espaço e compara o resto. O gate desta imagem, em `C01_build_result.md`, foi `modules.files=557`, `compared=2309`, `mismatches=0`. Por CRC e por esse trecho do vermagic, os 557 não ficam bloqueados. Isso é leitura de host. Ninguém carregou esses módulos com este kernel.

O cert do Google continua no `.config` do build (`CONFIG_SYSTEM_TRUSTED_KEYS`). A aceitação da assinatura no boot deste arquivo não foi medida.

## Plano no dia da escrita

Diretório dos comandos: `kernel/lake`. Cada fastboot passa por `tools/fastboot_guard.sh`. Fastboot nu fica de fora. Nada de `set_active`. Nada de flash em `boot_a` nem no slot A. O slot A medido no protocolo é OS3.0.20.0 por cima de dados do OS3.0.306.0 e `unbootable=yes`.

O protocolo só libera o flash de kernel novo depois do T-1 do backup no mesmo dia. A ordem abaixo é essa.

### Hashes, no host, antes do cabo

Arquivo novo do dia, duas linhas, nada mais:

```text
3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7
74090569d7fc4883159ea1fe14a484c4e3a3b14458aa84605aa3f520d78a1120
```

```text
export FASTBOOT_GUARD_HASHES="$PWD/guard_hashes.txt"
```

Conferir de novo os dois sha256 nos arquivos antes de exportar. O guard recusa um flash cujo sha256 não está nessa lista. Tamanho 67108864 e magic `ANDROID!` também são gates do wrapper.

### Z0

Bateria em 60% ou mais. Cabo de dados. Tela ligada.

1. Desligar, soltar o cabo, segurar Vol− e Power até a tela FASTBOOT, soltar, reconectar.
2. `tools/fastboot_guard.sh devices` — um aparelho. Outro resultado encerra o dia.
3. Só estes getvar, pelo guard: `product`, `current-slot`, `slot-count`, `is-userspace`, `unlocked`, `max-download-size`, `partition-size:boot_b`, `slot-successful:a`, `slot-successful:b`, `slot-unbootable:a`, `slot-unbootable:b`, `slot-retry-count:a`, `slot-retry-count:b`, `battery-soc-ok`, `battery-voltage`.
4. Segue só se `product=lake`, `current-slot=b`, `is-userspace` é `no` ou `Variable not found` (`yes` é fastbootd e para), `unlocked=yes`, `slot-successful:b=yes`, `slot-unbootable:b=no`, e `partition-size:boot_b` vale 67108864 (`4000000` ou `0x4000000`). `current-slot` diferente de `b`: desligar pelas teclas e parar. Sem `fastboot reboot`.
5. Guardar a saída em `z0_fastboot_before.txt`.
6. No dia do T-1 não há reboot entre o Z0.4 e o flash do backup. Ficar no fastboot.

### T-1, backup idêntico

`tools/fastboot_guard.sh flash boot_b [fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img`

Esperado: `OKAY`. Depois `tools/fastboot_guard.sh reboot`. No Android, `ro.build.version.incremental` continua a linha OS3.0.306.0 e `uname -r` continua `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`. Voltar ao fastboot pelas teclas. Gates: `slot-successful:b=yes`, `slot-unbootable:b=no`, `current-slot=b`.

### Flash desta imagem

Só com T-1 verde, leitura T2b-pre verde (`slot-successful:b=yes`, `slot-unbootable:b=no`) e o dono de acordo.

`tools/fastboot_guard.sh flash boot_b [artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img`

Esperado: `OKAY`, sem `FAILED`. Não reiniciar ainda. Repetir os seis getvar de slot. Com `slot-successful:b=yes` e `slot-unbootable:b=no`, aí `tools/fastboot_guard.sh reboot`.

### Aceite, estilo T3, mais o KernelSU

Baseline do dia, antes do Z0, com o cabo: lista de módulos, dmesg, e o registro de `/sys/fs/pstore` como no protocolo. O número 429 é o exemplo da unidade auditada. O critério é o conjunto do dia.

Depois do Android subir:

```text
adb shell dmesg > t3_dmesg.txt
tools/dmesg_new_errors.sh t3_dmesg.txt
```

Esperado: exit 0 e saída vazia.

Módulos: o conjunto de `/proc/modules` contém o baseline do dia.

`uname -r` igual a `6.6.89-android15-8-4k`. `sys.boot_completed=1`. `ro.build.version.incremental` ainda a linha OS3.0.306.0. Outra linha de build: desligar, não desbloquear a tela, não reiniciar.

Wi-Fi associado e Bluetooth liga.

KernelSU: o manager mostra o kernel ativo, ou `adb shell su -c id` imprime `uid=0`. O boot.img não traz o manager nem o ksud. Sem o app instalado, esse critério fica em aberto e o dia não fecha como passe do C0.1. O build compilou `KSU_VERSION` 16 porque a cópia dentro da árvore não é um git próprio. Se o manager recusar essa versão, registrar a recusa e ir ao rollback.

### Rollback

Qualquer falha de boot, dmesg novo, módulo faltando, Wi-Fi ou Bluetooth, ou recusa do manager: desligar pelas teclas, entrar no fastboot, e

```text
tools/fastboot_guard.sh flash boot_b [fora do repo]/mods/lake-kernel/backup-2026-10-05/boot_b.img
```

O sha256 dessa linha é `3890fb9664c6543a4939b18b3845f05995221672fb4af905d2367ef12b829fc7`, já na lista do dia. Depois do `OKAY`, reboot pelo guard só com `current-slot=b` e `slot-unbootable:b=no`. Confirmar `uname -r` de volta em `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`.

Tela preta ou fastboot ausente seguem a tabela do `DEVICE-TEST-PROTOCOL.md`. Uma tentativa de voltar pelas teclas. Sem troca de cabo no meio. Sem segundo flash se o guard recusou antes do `OKAY`.

## Aberto

- Aparelho não foi ligado a este preparo. Flash não ocorreu.
- O footer novo é `NONE` e perdeu as props do vbmeta de estoque. O T2 usou a mesma receita. O comportamento do LK com este arquivo não foi medido.
- `KSU_VERSION` 16. Manager e ksud fora desta imagem.
- Os 557 passam no gate de CRC e no trecho pós-espaço do vermagic. A carga no aparelho, e a assinatura dos módulos neste boot, continuam por medir.
