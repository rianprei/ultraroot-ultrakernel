# Fonte dos módulos de vendor do lake: auditoria das pesquisas (2026-10-26)

Pergunta: dá para ligar `CONFIG_MPTCP` e `CONFIG_IP_ROUTE_MULTIPATH` sem perder nenhum dos 108 módulos que quebram o KMI (`docs/ultrakernel/UK-ULTRA-RELATORIO.md`)? Para isso, cada um dos 108 precisa de fonte idêntica à usada pela Xiaomi. Entrada: seis pesquisas externas, fora do repo: três relatórios de deep research produzidos com `docs/ultrakernel/PROMPT-DEEP-RESEARCH-LAKE-OSS.md`, uma do agente Antigravity, `RELATORIO_FONTES_LAKE.md` e `RELATORIO-FONTE-MODULOS-VENDOR-LAKE.md` (2026-10-27). Cada afirmação deles foi conferida aqui por comando.

## Veredito

**Não dá hoje.** Prova:

1. 20 dos 108 módulos não têm objeto com o mesmo nome nem na árvore oficial da Xiaomi para o aparelho-irmão (`MiCode/MTK_kernel_device_modules` e `MiCode/mtkcam-kernel_device_modules`, branch `dew-v-oss`) nem na árvore Samsung A05. São eles: 13 painéis `dsi_panel_c3n_*`; os sensores de câmera `gc_gc6133c_ii` e `sc_sc080cs_i`; `millet_core`; `snd-soc-fs15xx`, `sgm41543_charger`, `upm6920_charger` e `tcpc_upm7610`. Prova: `docs/ultrakernel/data/lake-108-origem.tsv`, linhas com `AUSENTE` nas duas colunas. Sem eles, o kernel com MPTCP fica sem tela, carga, áudio do alto-falante e duas câmeras.
2. Onde há fonte, ela nem sempre é a do lake. Nas duas árvores, o `srcversion` dá o mesmo valor em `mtk_wdt` e `snd-soc-mt6660` e dá valor diferente em `tcpc_rt1711h`. Esse é um arquivo único sem header local, então a diferença está provada. O mesmo vale para o `mali_prot_alloc_mt6768_r49` na árvore Samsung. Tabela abaixo.
3. Um módulo recompilado com fonte diferente pode até carregar, mas comportamento igual não está provado. Isso contraria o critério "0 erros".

Caminho que resolve: a fonte do lake pela GPL (pedido pronto em `docs/ultrakernel/PEDIDO-GPL-LAKE.md`). Até lá, MPTCP e multipath continuam fora (`docs/ultraroot/ULTRAROOT-PLANO-ULTRAKERNEL.md`, R4.2).

## Fatos novos medidos

| Fato | Prova |
|---|---|
| Os commits `810fd09a116c` e `f597b3de5ef5` (vermagic dos módulos de vendor) não existem no ACK | `curl https://android.googlesource.com/kernel/common/+/<hash>?format=JSON` → `NOT_FOUND` nos dois; o commit do GKI do aparelho, `5a0ffb447c1d`, resolve no mesmo endpoint |
| O branch `dew-v-oss` do MiCode é do Redmi 15C / POCO C85, kernel 6.6.58, só a árvore comum (sem `drivers/misc/mediatek`, sem `kernel_device_modules`) | API do GitHub: commit `e91fa1498975`, mensagem "Xiaomi kernel changes for REDMI 15C / REDMI 15C / POCO C85 Android V … MTK release TAG: lc-master-v-t-alps-release-v0.mp1.rc-V4"; `Makefile` `SUBLEVEL = 58`; `contents/drivers/misc/mediatek` → 404 |
| Os `.ko` do aparelho trazem o caminho do fonte: `../kernel_device_modules-6.6/…` (30 dos 108), `../vendor/mediatek/kernel_modules/…` (5) e árvore do kernel (7) | `strings <mod>.ko`; coluna `caminho_embutido_no_ko` da tabela |
| Mali do lake: `../vendor/mediatek/kernel_modules/gpu/mt6768/mali_avalon/mali-r49p1`, versão `r49p1-03bet0` | `strings mali_mgm_mt6768_r49.ko`, `strings mali_kbase_mt6768_r49.ko` |
| `MiCode/MTK_kernel_device_modules`, branch `dew-v-oss` (commit `cc23c641b9a4`, 2025-09-19), é a árvore oficial de módulos de vendor do Redmi 15C / POCO C85, sobre a mesma tag MTK. Somada à `MiCode/mtkcam-kernel_device_modules` (`dew-v-oss`, `11809e1d87c4`), declara objeto para 79 dos 108 (os 3 do Mali com nome por variável, `mali_*_$(MTK_PLATFORM_VERSION)_r49`) e cobre `gf_fingerprint`, `simtray`, `wifi_ant_check`, `mtk_spi` (SPI da digital FPC: alias `fpc,fpc_spi` igual ao do lake) e `sp_sp0821_iii`, que faltam na Samsung. Traz o Mali `mali-r49p1` em `drivers/gpu/mediatek/gpu_mali/mali_avalon/`; não traz a conectividade (`vendor/mediatek/kernel_modules/connectivity`) | API do GitHub (mensagem do commit: "MTK release TAG: lc-master-v-t-alps-release-v0.mp1.rc-V4"); `grep` nos `Makefile`/`Kbuild` do clone |
| A árvore Samsung do Galaxy A05/A05s (`mt6768-mainline/android_kernel_samsung_mt6768`) tem o mesmo layout (`kernel/kernel_device_modules-6.6`, `vendor/mediatek/kernel_modules/gpu/mt6768` → `gpu_mali`, `MALI_RELEASE_NAME ?= '"r49p1-03bet0"'`) e declara objeto para 83 dos 108 | `git ls-tree`, `grep` nos 4298 `Makefile`/`Kbuild`/`*.mk` |
| Os módulos sem fonte são do ODM Longcheer: `dsi_panel_c3n_null_dsc_vdo`, `gc_gc6133c_ii`, `sc_sc080cs_i` e `sp_sp0821_iii` têm `author` com domínio `longcheer.com` | `strings <mod>.ko` (os e-mails ficam fora do repo) |
| O `mtk_wdt` e o `snd-soc-mt6660` do lake não vêm do ACK: o `srcversion` do ACK diverge (`BCD5D1B9C5A1D6C0B6BC1E8` e `DE5AF57AA0F03D5025AEA1D`), e a árvore de vendor bate | `python3 -I kernel/lake/tools/srcversion.py kernel/common/drivers/watchdog/mtk_wdt.c` (árvore do repo: ACK `5a0ffb447c1d` mais KernelSU) |
| A Xiaomi monta os módulos com uma árvore do ODM que não publica: `EXT_MODULES` inclui `../vendor/longcheer/mtk/kernel_modules` | `build.config.mtk_kernel_device_modules:22` do `MiCode/MTK_kernel_device_modules@dew-v-oss` |
| Terceira geração de vermagic: `6.6.89-android15-8-gbdb5aebb8ade-4k` em 170 módulos do ramdisk do `vendor_boot` do slot A (`vb_a_r00`), o slot inativo. Também não está no ACK | `modinfo -F vermagic` no inventário local; gitiles → `NOT_FOUND` |
| `MiCode/MTK_kernel_device_modules@spark-w-oss` (`ee2aaed40bed`, 2026-07-04, Redmi Pad SE 4G, outra tag MTK) repete o padrão: `srcversion` idêntico em `mtk_wdt`, `snd-soc-mt6660`, `mtk_charger_algorithm_class`, `usb_meta` e `barohub`; diferente nas duas pilhas Type-C (`tcpc/` e `sc_tcpc/`: `tcpc_rt1711h`, `rt_pd_manager`, `tcpci_late_sync`), nos dois `charger_class`, no `mtk_chg_type_det` e no Mali (`mali_prot_alloc`, `mali_mgm`). Sem `upm6920`/`upm7610`, sem painel `c3n` | `kernel/lake/tools/srcversion.py` no clone; `git ls-tree` |
| Motorola lamu (espelho `OWLXS/android_kernel_motorola_lamu-modules`, MT6769, 6.6): `oem/tinno_charger/switch_charger/sgm41542/sgm415xx.c` é a mesma família do `sgm41543_charger` do lake (compatibles `sgm,sgm41541`/`41542`/`41543`/`41513`/`41516` nos dois), de outro ODM (Tinno). `oem/audiopa/fs1815` é FourSemi, mas outro chip (`foursemi,fs1815`; o lake usa `foursemi,fs15xx`). Fonte compatível, não idêntica; o `sgm41543_charger` do lake não tem `srcversion` para provar | `git ls-tree`; `modinfo -F alias` no `.ko` do lake; `grep` no fonte |
| `CONFIG_MPTCP` não pode ser módulo | `kernel/common/net/mptcp/Kconfig:3` (`bool`) |

## `srcversion`: aparelho contra a árvore Samsung e o `dew-v-oss` oficial

Ferramenta: `kernel/lake/tools/srcversion.py`, que recalcula o hash do `modpost` sem compilar. Validação: 8 de 8 módulos do build v6 conferem com `modinfo -F srcversion`, e um `.o.cmd` real dá o mesmo valor. Teste: `kernel/lake/tests/test_srcversion.py`, rodado pelo V65. Sabotagens: com a ordem de bytes do MD4 trocada, 3 falhas; sem o filtro de diretório, 1 falha.

| Módulo | Está nos 108 | `srcversion` do aparelho | Árvore Samsung | MiCode `dew-v-oss` |
|---|---|---|---|---|
| `mtk_wdt` | sim | `9669582EAC146E40FA39B58` | IDÊNTICO (as 4 cópias publicadas do A05) | IDÊNTICO |
| `snd-soc-mt6660` | sim | `00343172686151BE18DC96F` | IDÊNTICO | IDÊNTICO |
| `barohub` | não | `542FCD5C5660DB8C9FC0ADA` | IDÊNTICO | IDÊNTICO |
| `maghub` | não | `80C12C0122AE406D99B9CE6` | IDÊNTICO | IDÊNTICO |
| `mtk_charger_algorithm_class` | não | `E974FAEF007AC1DA0CEFFAC` | IDÊNTICO | IDÊNTICO |
| `usb_meta` | não | `C976D6E4EA1629FEAED5551` | IDÊNTICO | IDÊNTICO |
| `c2k_usb` (3 objetos) | não | `7EE82CA52CE2FF379AA6DC0` | IDÊNTICO | IDÊNTICO |
| `tcpc_rt1711h` | sim | `93338420C6A6356E6D90B4D` | DIFERENTE (prova: arquivo único, sem header local) | DIFERENTE (prova) |
| `rt_pd_manager` | não | `B3937EA9C4B2F3EC9F9E549` | DIFERENTE (prova: arquivo único, sem header local) | DIFERENTE (prova) |
| `mali_prot_alloc_mt6768_r49` | sim | `0538CFDB83B3AC9A2F1CFB0` | DIFERENTE (prova: arquivo único, sem header local) | DIFERENTE (prova; `70382D5E58230EC02D61CEA`, igual à cópia `kernel_device_modules` da Samsung) |
| `mali_mgm_mt6768_r49` | sim | `88415118DF4F3F9263EF15A` | DIFERENTE (testadas as variantes com e sem o header condicional) | DIFERENTE (com e sem o header) |
| `charger_class` | não | `4E620520A1BC3D30EF3C5E6` | DIFERENTE (testadas as variantes `c`, `c+h`, `h+c`) | DIFERENTE |
| `mtk_chg_type_det` | não | `0B95E4F49E532F4572CCEEE` | DIFERENTE (7 headers locais; ordem inferida) | DIFERENTE |
| `tcpci_late_sync` | não | `9C16E05401480B7354F6B27` | DIFERENTE (1 header local) | DIFERENTE |
| `tcpc_class` (vários objetos) | sim | `575D16703AE3F264E2F098A` | DIFERENTE (com e sem os objetos de PD; ordem inferida) | não calculado |
| `tcpc_upm7610`, `snd-soc-fs15xx` | sim | na tabela de dados | SEM FONTE | SEM FONTE |
| `mali_kbase_mt6768_r49`, `isee`, `icnl9916_spi`, `mtk_fpsgo` | só `mali_kbase` | na tabela de dados | NÃO CALCULADO | NÃO CALCULADO |

Leitura: o código MediaTek comum do lake é o mesmo snapshot do A05 e do `dew`; os mesmos 7 módulos são idênticos nas duas árvores. As diferenças aparecem justamente onde o fabricante mexe: carga, Type-C/PD e o Mali. O `dew` também difere do lake nesses pontos, então o aparelho-irmão oficial não substitui a fonte do lake. A árvore `rsuntk/android_kernel_device_modules-6.6` dá os mesmos 5 casamentos de arquivo único e valores próprios no Type-C (outra variante do A05).

## Avaliação dos três relatórios

| Relatório | Situação | Erros encontrados |
|---|---|---|
| `deep-research-report(1).md` | Inútil: a IA respondeu "tema não especificado" e não recebeu o prompt | não se aplica |
| "auditoria das fontes dos módulos de vendor…" | Bom, com ressalvas. Confirmados: issues #41071 (2026-08-02) e #41145 (2026-09-17), `dew-v-oss` existe, árvores Samsung existem | Supôs que o `dew-v-oss` do `Xiaomi_Kernel_OpenSource` poderia ter os módulos de vendor: não tem (prova acima). Eles estão em outro repositório, `MiCode/MTK_kernel_device_modules`, que ele não achou. Deixou os commits como UNKNOWN: agora está medido que não estão no ACK. Não achou as issues #39022 (2025-02-01, aberta), #40912 (fechada em 2026-03-31) e #41100 (2026-08-23, aberta), todas pedindo a fonte do lake. A versão em inglês do pedido GPL deixou a lista dos 108 como marcador |
| Antigravity (colada na conversa, arquivo `AUDITORIA_FONTES_KERNEL_LAKE_GPL.md` fora do repo) | A mais útil: achou `MiCode/MTK_kernel_device_modules` e `mtkcam-kernel_device_modules` (`dew-v-oss`), conferidos acima | Marca "Nível 3 (modinfo conferido)" em 77 módulos sem `srcversion` calculado. No `tcpc_rt1711h`, com a mesma fonte do `dew`, o `srcversion` prova fonte diferente. Conta o `sgm41543_charger` como achado numa árvore Motorola MT6853 (branch padrão `fourteen`, outro SoC): isso é fonte compatível, não idêntica. Dá o `Xiaomi_Kernel_OpenSource/dew-v-oss` como 6.6.89, mas é 6.6.58 (`Makefile`). Diz que `scp` vem de `vow/ver02`, mas o `.ko` mostra `scp/cm4/v01`. Os endereços de e-mail que ele dá para a Xiaomi (`opensource@`, `legalqa@`) e para a SFC não foram conferidos (NÃO VERIFICADO). Ele também manda o pedido para o e-mail pessoal de um engenheiro da Xiaomi; esse e-mail fica fora do repo (dado pessoal) e não é canal de conformidade. A afirmação de que "a tela trava" sem o driver do painel é inferência |
| `RELATORIO-FONTE-MODULOS-VENDOR-LAKE.md` | Boa consolidação; parte dela repete esta auditoria. Trouxe o `spark-w-oss`, o espelho Motorola lamu e o `vendor/longcheer` no `build.config` (conferidos acima) | Diz que 38 dos 108 têm `srcversion` no `.ko`: são 9. Repete o erro antigo deste documento, "70 dos 108" sem `srcversion`: o certo é 99. Dá o Mali contra o `dew` como "não calculado": está calculado e difere. Conta `tcpc_class` e `mali_mgm` como diferença provada: são multiobjeto ou com header condicional, então são indício forte, não prova; provados nos 108 são `tcpc_rt1711h` e `mali_prot_alloc`. Atribui a geração `bdb5aebb8ade` ao "ramdisk first-stage" sem dizer que é o do slot A. Diz que o `sgm41543` só existe em árvore Motorola de outro SoC; o lamu é MT6769, kernel 6.6 |
| `RELATORIO_FONTES_LAKE.md` | Útil no que mediu: o `810fd09a116c` e o `f597b3de5ef5` também não estão no histórico completo do `Xiaomi_Kernel_OpenSource`; `MTK_kernel_modules`, `kernel_build` e `kernel_devicetree` do MiCode só têm README; o Mali r49p1 está no `dew-v-oss` | Atribui 36 módulos ao AOSP só pelo nome no `Makefile`. Em 11 deles o próprio `.ko` registra `../kernel_device_modules-6.6/…` (por exemplo `8250_mtk`, `mediatek-drm`, `mtk_iommu`), e no `mtk_wdt` e no `snd-soc-mt6660` o `srcversion` do ACK diverge. Dá `wlan_drv_gen4m_6768` e `wmt_drv` como sem fonte, mas a árvore Samsung tem `vendor/mediatek/kernel_modules/connectivity` (não procurou nela). Diz que só 21 dos 108 têm `srcversion`; na verdade são 21 dos 370, e 9 dos 108. Diz que comparar `srcversion` exige rebuild; o `kernel/lake/tools/srcversion.py` faz isso sem compilar. O caminho `vendor/longcheer/mtk/kernel_modules` não aparece nos `.ko` (NÃO VERIFICADO); os autores Longcheer, sim |
| "Reconciliando o GKI Modificado…" | Não confiável | Diz que o Wi-Fi é Qualcomm, mas o `.ko` mostra `../vendor/mediatek/kernel_modules/connectivity/wlan/core/gen4m`. Associa `810fd09a116c` ao build `ab13771415`, o que é falso: esse commit não está no ACK. Cita as issues #41100 e #39022 como fonte do vermagic; elas só pedem a fonte. Cita #41101, que é do POCO X7 Pro (`rodin`). A lista de módulos do seu pedido GPL tem 80 entradas e só 77 distintas. Ela omite 32 dos 108 e escreve `mtk-wdt` no lugar de `mtk_wdt`. E-mails `opensource@`/`legalqa@` e páginas `dev.mi.com/…a05m_oss` e Reddit: NÃO VERIFICADO |

## Reproduzir

Os clones ficam fora do repo, um diretório por árvore, sem rodar nada delas: `git clone --depth 1 --filter=blob:none --no-checkout https://github.com/mt6768-mainline/android_kernel_samsung_mt6768`, depois `git sparse-checkout set --no-cone <diretórios>` e `git checkout HEAD`. Exemplo:

```
python3 -I kernel/lake/tools/srcversion.py <árvore>/kernel/kernel_device_modules-6.6/drivers/watchdog/mtk_wdt.c
9669582EAC146E40FA39B58
```

## NÃO VERIFICADO

- A ordem real dos headers nos casos com header local e com vários objetos. Sem `.o.cmd` do build do vendor, uma diferença nesses casos não é prova.
- `srcversion` do `mali_kbase_mt6768_r49` (centenas de objetos).
- Os 99 módulos dos 108 sem `srcversion` no aparelho (só 21 dos 370 têm, e 9 deles estão nos 108). Para eles só existe prova de nível 2 ou 3 (símbolos, `modinfo`), e isso exige compilar.
- O `srcversion` contra o `dew-v-oss` foi calculado num clone feito pelo Antigravity, sem alterações locais (`git status` vazio, HEAD `cc23c64`, igual ao da API).
- A origem Samsung OSRC dos espelhos `mt6768-mainline`, `x1cen`, `Theman-Fromfar` e `rsuntk` (nenhum declara licença no GitHub).
- O endereço oficial de e-mail para pedidos GPL da Xiaomi.
