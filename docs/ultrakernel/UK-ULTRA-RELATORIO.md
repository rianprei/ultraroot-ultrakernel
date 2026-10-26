# ULTRA-kernel: avaliação de todas as opções desligadas

Objetivo do dono: um kernel que nunca mais precise ser recompilado. Base: `gki_defconfig` + `kernelsu.fragment` (`kernel/README.md`). Toolchain: clang r510928 (`Android (11368308, +pgo, +bolt, +lto, +mlgo, based on r510928) clang version 18.0.0`). Resultado: `kernel/common/arch/arm64/configs/ultrakernel.fragment` (588 opções; nome do kernel `rianprei-ultra-kernel`). Tabela completa, uma linha por opção: `docs/ultrakernel/data/ultra-avaliacao.tsv`.

## Método

1. Lista: as 3938 opções "is not set" do `.config` da base.
2. Kconfig real, uma por vez: liga só a opção (`=y`) sobre a base e roda `olddefconfig`. Registra se ela fica ligada, o que ela puxa junto e o que ela desliga.
3. KMI: a opção, ou qualquer coisa que ela puxa, aparece em `#if` de header em `include/`, `arch/arm64/`, `kernel/`, `mm/`, `block/`, `fs/`, `net/`, `security/`, `crypto/`, `lib/`, `init/`, `ipc/`, `io_uring/`, `virt/` ou `certs/`? Então pode mudar tipo ou constante vista pelos módulos de vendor. O gate de CRC não pega mudança de constante (por exemplo, `HZ`), por isso essas opções ficam de fora mesmo sem build.
4. Filtros: depuração e teste; relaxamento de segurança; comportamento que quebra boot ou serviço do Android; hardware que o POCO C75 (MT6768) não tem. Driver de dispositivo USB conta como útil (OTG); controlador USB de outro SoC ou PCI, não.
5. Revisão manual das candidatas que não são driver USB.
6. Build completo com o fragmento, `Image` e módulos, e gate de KMI (`kernel/lake/tools/gate_kmi_crc.sh`).

## Resultado por veredito

| Veredito | Opções | O que significa |
|---|---|---|
| INCLUIDO | 553 | Entra no ULTRA. Drivers de dispositivo como módulo (`=m`); rede, filesystems, crypto e dm como `=y` ou `=m` conforme o tipo. |
| INCLUIDO-REVISADO | 15 | Aparece em header só como protótipo ou wrapper inline (`include/net/tc_wrapper.h`, `include/net/pkt_sched.h`), ou é a troca do padrão TCP para BBR. Gate verde. |
| SEM-HARDWARE | 2652 | Drivers de outros SoCs, PCI, sensores, codecs e chips de placa que o lake não tem. |
| RISCO-KMI | 443 | Mudam tipo ou constante vista pelos módulos de vendor. |
| DEBUG | 132 | Depuração, teste ou rastreio: custo de desempenho ou panic de propósito. |
| CONFLITO | 88 | Desligariam ou mudariam uma opção já ligada (choice). |
| INALCANCAVEL | 30 | Dependência que não existe em arm64/GKI, ou exige build de userspace dentro do kernel. |
| COMPORTAMENTO | 15 | Quebram boot ou serviço (por exemplo, `INITRAMFS_FORCE` ignora o ramdisk; `PSI_DEFAULT_DISABLED` desliga o PSI do lmkd; `TRIM_UNUSED_KSYMS` tira exports dos módulos de vendor; `USB_OTG_PRODUCTLIST` restringe o OTG). |
| SEGURANCA | 10 | `/dev/mem`, `/proc/kcore`, desligar o SELinux, syscalls obsoletas, junção de slabs. |

Somando com as 7 opções que dependiam de pai desligado (modos de team, `NET_SCH_FQ_PIE`, `IKHEADERS=y`), o fragmento tem 575 linhas. Diferença no `.config` contra a base: 660 linhas novas ou mudadas, e 4 que saem: `DEFAULT_CUBIC` e `DEFAULT_TCP_CONG="cubic"` (padrão vira BBR), `IKHEADERS=m` (vira `y`) e `MODULE_SIG_KEY` (chave fixa, abaixo).

## O que o kernel ganha

- Rede: BBR como padrão e 15 algoritmos TCP; bonding e team (Wi-Fi + Ethernet em failover ou balanceamento); 20 qdiscs (cake, fq_pie, hfsc etc.); classificadores e ações de `tc`; alvos e matches extras de iptables e ip6tables; ipset; túneis (FOU, GENEVE, GTP, L2TPv3); PPP e PPPoE; WireGuard já vinha ligado.
- USB por OTG, como módulo: Ethernet e modem USB (RNDIS, QMI, MBIM, NCM e outros), Wi-Fi USB, Bluetooth USB, câmeras e receptores, áudio USB, seriais, CAN, storage e leitores, adaptadores de vídeo USB.
- Armazenamento: NTFS3, XFS, exFAT já vinha, HFS+, UDF, ISO9660, e alvos de device-mapper (cache, thin, writecache, mirror e outros).
- Crypto: algoritmos e interfaces de userspace extras.
- Módulos futuros: headers do kernel embutidos (`IKHEADERS=y`, em `/sys/kernel/kheaders.tar.xz`), todos os exports mantidos (`TRIM_UNUSED_KSYMS=n`) e chave fixa de assinatura de módulos.

## O que ficou de fora e por quê (prova de build)

| Opção | Resultado do gate |
|---|---|
| `MPTCP` | 223 símbolos divergentes |
| `IP_ROUTE_MULTIPATH` | 203 símbolos divergentes |
| `IPV6_SUBTREES` | 203 símbolos divergentes |
| `BTRFS_FS` | 17 símbolos divergentes: faz `select BLK_CGROUP_PUNT_BIO` (`kernel/common/fs/btrfs/Kconfig:5`), que muda `struct blkcg_gq` (`kernel/common/block/blk-cgroup.h:76`) |

Com essas opções, os módulos de vendor (Wi-Fi, GPU, modem) recusam carregar. Elas só entram com módulos de vendor recompilados, e o código-fonte deles não é público.

## Chave de assinatura fixa

A chave de módulos é gerada fora do repo (`[chaves locais do dono, fora do repo]/ultrakernel_modsign.pem`, permissão 600) e entra no build por um fragmento também fora do repo (`CONFIG_MODULE_SIG_KEY`). Com ela, um módulo compilado depois carrega neste kernel sem recompilar a `Image`. Perder a chave não impede o boot: `MODULE_SIG_FORCE` continua `n`, então um módulo assinado com outra chave carrega com taint. Mas os exports protegidos (`MODULE_SIG_PROTECT=y`) só ficam disponíveis para módulos assinados com ela.

## Builds e provas

Ver `docs/history/reports/ultra_kernel_2026-10-26.txt`.

## Limites

- Nada foi testado no aparelho. Boot, Wi-Fi, módulos USB e bonding: NÃO VERIFICADO.
- As 443 opções RISCO-KMI não foram compiladas uma a uma. O índice de headers acertou nos 4 casos compilados (os 4 quebraram o gate). Entre elas pode haver opções seguras que só uma revisão linha a linha libera.
- O veredito SEM-HARDWARE usa o caminho do Kconfig e as dependências declaradas, sem a árvore de dispositivos do aparelho.
