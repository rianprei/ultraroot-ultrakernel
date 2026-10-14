# UK-2: proposta para `TRIM_UNUSED_KSYMS` no build `make` (ERR-11)

Estado: proposta. Nada foi aplicado. Aplicar depende da decisão do dono (`docs/PENDENCIAS-DO-DONO.md`, seção UltraKernel). Depois de aplicar, o gate de KMI (UK-4) roda de novo.

## O que liga a opção no build do aparelho

1. O alvo `kernel_aarch64` do Kleaf declara `"trim_nonlisted_kmi": True` em `kernel/common/BUILD.bazel:154`, com `"kmi_symbol_list": "android/abi_gki_aarch64"` (`kernel/common/BUILD.bazel:152`) e `"additional_kmi_symbol_lists": [":aarch64_additional_kmi_symbol_lists"]` (`kernel/common/BUILD.bazel:153`).
2. O filegroup `aarch64_additional_kmi_symbol_lists` lista 31 arquivos `android/abi_gki_aarch64_*` em `kernel/common/BUILD.bazel:111-144`. Contagem: `sed -n 109,147p kernel/common/BUILD.bazel | grep -o '"android/[^"]*"' | wc -l` → `31`. O lake fala em "32 additional lists" (`kernel/lake/docs/PLAN-AND-FINDINGS.pt-BR.md:43`). A diferença entre 31 e 32 está NÃO VERIFICADA.
3. O config do aparelho tem `CONFIG_TRIM_UNUSED_KSYMS=y` (`kernel/lake/data/official-kernel_aarch64.config:836`) e `CONFIG_UNUSED_KSYMS_WHITELIST="abi_symbollist.raw"` (`kernel/lake/data/official-kernel_aarch64.config:837`).
4. A regra do Kleaf que traduz `trim_nonlisted_kmi` nessas duas opções de config fica em `build/kernel/kleaf/` (carregado em `kernel/common/BUILD.bazel:16`). Esse diretório não está no repo: [fora do repo] kernel/common/build/kernel (o `ls` desse caminho responde `No such file or directory`). A tradução está NÃO VERIFICADA aqui; o que existe é a coincidência entre o `BUILD.bazel` e o config do aparelho.
5. O kernel consome a opção em `kernel/common/Makefile:1198-1202` (com trim, `KBUILD_MODULES := 1`: os módulos precisam ser compilados para saber quais símbolos ficam) e em `kernel/common/scripts/Makefile.modpost:134-138` (`ksym-wl := $(CONFIG_UNUSED_KSYMS_WHITELIST)`; caminho relativo que não existe no diretório de saída recebe o prefixo `$(srctree)/`).
6. A opção só aparece com `EXPERT` (`kernel/common/kernel/module/Kconfig:387`), e `CONFIG_EXPERT=y` está em `kernel/common/arch/arm64/configs/gki_defconfig:46`.

## Reconstrução da lista no host (prova)

O `abi_symbollist.raw` do build oficial tem 8962 linhas (`kernel/lake/docs/research/CODEX2_kleaf_repro.md:50`). A união dos nomes de símbolo das 32 listas (a principal e as 31 adicionais), sem cabeçalho `[abi_symbol_list]`, sem comentário e sem linha vazia, dá o mesmo número:

```
cd kernel/common
sed -n 109,147p BUILD.bazel | grep -o '"android/[^"]*"' | tr -d '"' > <scratch>/lists.txt
cat android/abi_gki_aarch64 $(cat <scratch>/lists.txt) | grep -v '^\s*#' | grep -v '^\s*\[' \
  | sed 's/^\s*//;s/\s*$//' | grep -v '^$' | sort -u | wc -l
8962
```

A contagem bate. O conteúdo não foi comparado com o `abi_symbollist.raw` oficial, que não está no repo: NÃO VERIFICADO.

## Mudança proposta no build `make`

Sem tocar em `kernel/common/` nem em `arch/arm64/configs/`:

1. Gerar `<saida>/abi_symbollist.raw` com o comando acima (saída para o arquivo, no diretório `O=`).
2. Fragmento fora do repo, aplicado com `merge_config.sh` depois do `kernelsu.fragment`:
   ```
   CONFIG_TRIM_UNUSED_KSYMS=y
   CONFIG_UNUSED_KSYMS_WHITELIST="abi_symbollist.raw"
   ```
   Pela regra de `kernel/common/scripts/Makefile.modpost:136`, o caminho relativo é resolvido primeiro no diretório de saída, onde o arquivo existe.
3. `make olddefconfig`, depois `make Image modules` (o trim exige módulos, `kernel/common/Makefile:1198-1202`).
4. Rodar o gate de CRC (`kernel/lake/tools/gate_kmi_crc.sh`) e comparar com `docs/history/reports/uk4_gate_kmi_crc_2026-10-13.txt`.

## NÃO VERIFICADO

- O build com trim ligado não foi feito. Tempo, sucesso e resultado do gate: NÃO VERIFICADO.
- Igualdade de conteúdo da lista reconstruída com o `abi_symbollist.raw` oficial.
- A regra interna do Kleaf (item 4 acima).
- O efeito de `protected_exports_list` (`kernel/common/BUILD.bazel:155`) no build `make`.
