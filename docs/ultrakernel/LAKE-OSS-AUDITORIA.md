# Fonte dos módulos de vendor do lake: auditoria das pesquisas (2026-10-26)

Pergunta: dá para ligar `CONFIG_MPTCP` e `CONFIG_IP_ROUTE_MULTIPATH` sem perder nenhum dos 108 módulos que quebram o KMI (`docs/ultrakernel/UK-ULTRA-RELATORIO.md`)? Para isso, cada um dos 108 precisa de fonte idêntica à usada pela Xiaomi. Entrada: três relatórios de deep research produzidos com `docs/ultrakernel/PROMPT-DEEP-RESEARCH-LAKE-OSS.md` (fora do repo). Cada afirmação deles foi conferida aqui por comando.

## Veredito

**Não dá hoje.** Prova:

1. 25 dos 108 módulos não têm nenhum objeto com o mesmo nome em nenhuma fonte pública encontrada. São eles: 13 painéis `dsi_panel_c3n_*`; os sensores de câmera `gc_gc6133c_ii`, `sc_sc080cs_i` e `sp_sp0821_iii`; `gf_fingerprint`, `millet_core`, `simtray`, `wifi_ant_check`, `mtk_spi`; e `snd-soc-fs15xx`, `sgm41543_charger`, `upm6920_charger` e `tcpc_upm7610` (`docs/ultrakernel/data/lake-108-origem.tsv`, coluna `objeto_na_arvore_samsung` = `AUSENTE`). Sem eles, o kernel com MPTCP fica sem tela, carga, áudio do alto-falante e câmera.
2. Onde há fonte, ela nem sempre é a do lake. O `srcversion` dá o mesmo valor em `mtk_wdt` e `snd-soc-mt6660`, e dá valor diferente em `tcpc_rt1711h` e `mali_prot_alloc_mt6768_r49` (arquivo único sem header local, então a diferença está provada). Tabela abaixo.
3. Um módulo recompilado com fonte diferente pode até carregar, mas comportamento igual não está provado. Isso contraria o critério "0 erros".

Caminho que resolve: a fonte do lake pela GPL (pedido pronto em `docs/ultrakernel/PEDIDO-GPL-LAKE.md`). Até lá, MPTCP e multipath continuam fora (`docs/ultraroot/ULTRAROOT-PLANO-ULTRAKERNEL.md`, R4.2).

## Fatos novos medidos

| Fato | Prova |
|---|---|
| Os commits `810fd09a116c` e `f597b3de5ef5` (vermagic dos módulos de vendor) não existem no ACK | `curl https://android.googlesource.com/kernel/common/+/<hash>?format=JSON` → `NOT_FOUND` nos dois; o commit do GKI do aparelho, `5a0ffb447c1d`, resolve no mesmo endpoint |
| O branch `dew-v-oss` do MiCode é do Redmi 15C / POCO C85, kernel 6.6.58, só a árvore comum (sem `drivers/misc/mediatek`, sem `kernel_device_modules`) | API do GitHub: commit `e91fa1498975`, mensagem "Xiaomi kernel changes for REDMI 15C / REDMI 15C / POCO C85 Android V … MTK release TAG: lc-master-v-t-alps-release-v0.mp1.rc-V4"; `Makefile` `SUBLEVEL = 58`; `contents/drivers/misc/mediatek` → 404 |
| Os `.ko` do aparelho trazem o caminho do fonte: `../kernel_device_modules-6.6/…` (30 dos 108), `../vendor/mediatek/kernel_modules/…` (5) e árvore do kernel (7) | `strings <mod>.ko`; coluna `caminho_embutido_no_ko` da tabela |
| Mali do lake: `../vendor/mediatek/kernel_modules/gpu/mt6768/mali_avalon/mali-r49p1`, versão `r49p1-03bet0` | `strings mali_mgm_mt6768_r49.ko`, `strings mali_kbase_mt6768_r49.ko` |
| A árvore Samsung do Galaxy A05/A05s (`mt6768-mainline/android_kernel_samsung_mt6768`) tem o mesmo layout (`kernel/kernel_device_modules-6.6`, `vendor/mediatek/kernel_modules/gpu/mt6768` → `gpu_mali`, `MALI_RELEASE_NAME ?= '"r49p1-03bet0"'`) e declara objeto para 83 dos 108 | `git ls-tree`, `grep` nos 4298 `Makefile`/`Kbuild`/`*.mk` |
| `CONFIG_MPTCP` não pode ser módulo | `kernel/common/net/mptcp/Kconfig:3` (`bool`) |

## `srcversion`: aparelho contra a árvore Samsung

Ferramenta: `kernel/lake/tools/srcversion.py`, que recalcula o hash do `modpost` sem compilar. Validação: 8 de 8 módulos do build v6 conferem com `modinfo -F srcversion`, e um `.o.cmd` real dá o mesmo valor. Teste: `kernel/lake/tests/test_srcversion.py`, rodado pelo V65. Sabotagens: com a ordem de bytes do MD4 trocada, 3 falhas; sem o filtro de diretório, 1 falha.

| Módulo | Está nos 108 | `srcversion` do aparelho | Árvore Samsung |
|---|---|---|---|
| `mtk_wdt` | sim | `9669582EAC146E40FA39B58` | IDÊNTICO (as 4 cópias publicadas do A05) |
| `snd-soc-mt6660` | sim | `00343172686151BE18DC96F` | IDÊNTICO |
| `barohub` | não | `542FCD5C5660DB8C9FC0ADA` | IDÊNTICO |
| `maghub` | não | `80C12C0122AE406D99B9CE6` | IDÊNTICO |
| `mtk_charger_algorithm_class` | não | `E974FAEF007AC1DA0CEFFAC` | IDÊNTICO |
| `usb_meta` | não | `C976D6E4EA1629FEAED5551` | IDÊNTICO |
| `c2k_usb` (3 objetos) | não | `7EE82CA52CE2FF379AA6DC0` | IDÊNTICO |
| `tcpc_rt1711h` | sim | `93338420C6A6356E6D90B4D` | DIFERENTE (prova: arquivo único, sem header local) |
| `rt_pd_manager` | não | `B3937EA9C4B2F3EC9F9E549` | DIFERENTE (prova: arquivo único, sem header local) |
| `mali_prot_alloc_mt6768_r49` | sim | `0538CFDB83B3AC9A2F1CFB0` | DIFERENTE (prova: arquivo único, sem header local) |
| `mali_mgm_mt6768_r49` | sim | `88415118DF4F3F9263EF15A` | DIFERENTE (testadas as variantes com e sem o header condicional) |
| `charger_class` | não | `4E620520A1BC3D30EF3C5E6` | DIFERENTE (testadas as variantes `c`, `c+h`, `h+c`) |
| `mtk_chg_type_det` | não | `0B95E4F49E532F4572CCEEE` | DIFERENTE (7 headers locais; ordem inferida) |
| `tcpci_late_sync` | não | `9C16E05401480B7354F6B27` | DIFERENTE (1 header local) |
| `tcpc_class` (vários objetos) | sim | `575D16703AE3F264E2F098A` | DIFERENTE (com e sem os objetos de PD; ordem inferida) |
| `tcpc_upm7610`, `snd-soc-fs15xx` | sim | na tabela de dados | SEM FONTE |
| `mali_kbase_mt6768_r49`, `isee`, `icnl9916_spi`, `mtk_fpsgo` | só `mali_kbase` | na tabela de dados | NÃO CALCULADO |

Leitura: o código MediaTek comum do lake é o mesmo snapshot do A05; 7 módulos idênticos provam isso. As diferenças aparecem justamente onde o ODM mexe: carga, Type-C/PD e o Mali. A árvore `rsuntk/android_kernel_device_modules-6.6` dá os mesmos 5 casamentos de arquivo único e valores próprios no Type-C (outra variante do A05).

## Avaliação dos três relatórios

| Relatório | Situação | Erros encontrados |
|---|---|---|
| `deep-research-report(1).md` | Inútil: a IA respondeu "tema não especificado" e não recebeu o prompt | não se aplica |
| "auditoria das fontes dos módulos de vendor…" | Bom, com ressalvas. Confirmados: issues #41071 (2026-08-02) e #41145 (2026-09-17), `dew-v-oss` existe, árvores Samsung existem | Supôs que `dew-v-oss` poderia ter os módulos de vendor: não tem (prova acima). Deixou os commits como UNKNOWN: agora está medido que não estão no ACK. Não achou as issues #39022 (2025-02-01, aberta), #40912 (fechada em 2026-03-31) e #41100 (2026-08-23, aberta), todas pedindo a fonte do lake. A versão em inglês do pedido GPL deixou a lista dos 108 como marcador |
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
- Os 70 módulos dos 108 sem `srcversion` no aparelho. Para eles só existe prova de nível 2 ou 3 (símbolos, `modinfo`), e isso exige compilar.
- A origem Samsung OSRC dos espelhos `mt6768-mainline`, `x1cen`, `Theman-Fromfar` e `rsuntk` (nenhum declara licença no GitHub).
- O endereço oficial de e-mail para pedidos GPL da Xiaomi.
