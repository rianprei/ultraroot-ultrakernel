# Prompt de deep research: código-fonte dos módulos de vendor do POCO C75 (lake)

Copie tudo a partir de "INÍCIO DO PROMPT". Funciona em IA web (só navegação) e em IA de linha de comando (com shell e git). As seções marcadas [CLI] valem só para quem tem shell. Dados de entrada levantados em 2026-10-26 a partir dos módulos do aparelho do dono; a tabela completa fica em `docs/ultrakernel/data/lake-vendor-modules-fingerprint.tsv`.

---

## INÍCIO DO PROMPT

Você é um pesquisador de kernel Android e de licenciamento GPL. Trabalhe com rigor de auditoria: toda afirmação tem fonte verificável, e o que não tiver fonte é marcado como NÃO VERIFICADO. Não invente URL, commit, branch, tag ou nome de arquivo. Se não abriu a página ou não rodou o comando, não cite.

### OBJETIVO

Encontrar, montar e provar um código-fonte público e legítimo que recompile os módulos de kernel de vendor do POCO C75 (codename `lake`), para que o dono possa recompilar esses módulos contra um kernel GKI modificado (com `CONFIG_MPTCP=y` e `CONFIG_IP_ROUTE_MULTIPATH=y`) sem perder nenhuma função do aparelho. Se não houver fonte pública suficiente, entregar o caminho legal para obtê-la (pedido GPL) pronto para enviar.

### CONTEXTO (fatos já medidos; não refaça, use)

- Aparelho: POCO C75, modelo `2410FPCC5G`, codename `lake`, SoC MediaTek MT6768 (família Helio G8x), GPU Mali (`mali_kbase_mt6768_r49`), firmware `OS3.0.306.0.WGTMIXM`, slot ativo b, bootloader desbloqueado.
- Kernel: GKI `android15-6.6`, `uname -r` stock `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k` (Google build `ab13771415`, commit do common kernel `5a0ffb4`). Toolchain do GKI: clang `r510928`.
- Os módulos de vendor NÃO foram compilados contra a árvore do Google: 363 deles têm vermagic `6.6.89-android15-8-g810fd09a116c-4k` e 7 têm `6.6.30-android15-8-gf597b3de5ef5-4k` (`binder_gki`, `millet_binder`, `millet_core`, `millet_hs`, `millet_oem_cgroup`, `millet_pkg`, `millet_sig`). Os commits `810fd09a116c` e `f597b3de5ef5` identificam a árvore de build do vendor.
- O aparelho tem 370 módulos únicos: 366 com licença GPL (GPL, GPL v2, Dual BSD/GPL, GPL and additional rights) e 4 `Proprietary` (`cpuqos_ext`, `eas_ext`, `fpsgo`, `mtk_em`).
- Ligar `CONFIG_MPTCP` ou `CONFIG_IP_ROUTE_MULTIPATH` muda o CRC (MODVERSIONS) de 223 e 203 símbolos do kernel, e 108 módulos de vendor deixam de carregar. Entre eles: armazenamento (`ufs-mediatek-mod`), watchdog (`mtk_wdt`), tela (`mediatek-drm`), GPU (Mali), Wi-Fi e Bluetooth (`wlan_drv_gen4m_6768`, `wmt_drv`), modem (`ccci_md_all`, `ccmni`), bateria e carga. Os 108 são todos GPL.
- Já verificado (não repita como descoberta): o repositório `github.com/MiCode/Xiaomi_Kernel_OpenSource` tem 267 branches e nenhum para `lake` (há `fire-t-oss`, `flame-u-oss` e `warm-u-oss`, que são outros aparelhos). Uma busca no GitHub por "lake kernel", "redmi 14c kernel" e "poco c75 kernel" não achou fonte da Xiaomi. Existem repositórios da comunidade (`mt6768-dev/android_kernel_xiaomi_mt6768`, `_earth`, `_fire`, `sergiofalconp24-hub/dew-kernel`) de outros aparelhos ou outras versões; nenhum provado compatível.

Os 108 módulos que precisam de fonte (prioridade máxima):

```
8250_mtk arm_dsu_pmu bootprof cache-parity ccci_md_all ccmni ccu_isp4 cfg80211 clk-common clk-mt6768 cqhci drm_display_helper dsi_panel_c3n_35_02_0h_dsc_vdo dsi_panel_c3n_35_0f_0e_dsc_vdo dsi_panel_c3n_35_0f_0l_dsc_vdo dsi_panel_c3n_37_02_0d_dsc_vdo dsi_panel_c3n_43_02_0a_dsc_vdo dsi_panel_c3n_43_02_0k_dsc_vdo dsi_panel_c3n_43_02_0n_dsc_vdo dsi_panel_c3n_44_03_0b_dsc_vdo dsi_panel_c3n_44_03_0j_dsc_vdo dsi_panel_c3n_46_03_0c_dsc_vdo dsi_panel_c3n_46_03_0f_dsc_vdo dsi_panel_c3n_46_03_0g_dsc_vdo dsi_panel_c3n_null_dsc_vdo extcon-mtk-usb gc_gc6133c_ii gf_fingerprint i2c-mt65xx iommu_gz leds-mt6370-flash mac80211 mali_kbase_mt6768_r49 mali_mgm_mt6768_r49 mali_prot_alloc_mt6768_r49 mcDrvModule mediatek-drm millet_core monitor_hang mt6358-regulator mt6358_battery mt635x-auxadc mt6370 mt6370-adc mt6370-charger mt6370-regulator mt6397 mt6577_auxadc mt6768-mt6358 mtk-btcvsd mtk-cqdma mtk-dvfsrc mtk-dvfsrc-devfreq mtk-dvfsrc-regulator mtk-emi mtk-icc-core mtk-mmc mtk-mml mtk-pmic-keys mtk-pwm mtk-scpsys mtk-scpsys-bringup mtk-scpsys-mt6768 mtk-smi mtk-sp-spk-amp mtk-uart-apdma mtk_battery_manager mtk_battery_oc_throttling mtk_iommu mtk_irtx_pwm mtk_jpeg mtk_spi mtk_u_ether mtk_usb_f_rndis mtk_wdt nvmem-mt635x-efuse phy-generic phy-mtk-tphy pinctrl-mtk-v2 pwm-mtk-disp reboot-mode rtc-mt6397 sc_sc080cs_i scheduler scp sec-rng sensorHub sgm41543_charger simtray snd-soc-fs15xx snd-soc-mt6358 snd-soc-mt6660 snd-soc-mt6768-afe snd-soc-mtk-common sp_sp0821_iii spi-mt65xx syscon-reboot-mode t-base-tui tcpc_class tcpc_rt1711h tcpc_upm7610 thermal_monitor ufs-mediatek-dbg ufs-mediatek-mod upm6920_charger wifi_ant_check wlan_drv_gen4m_6768 wmt_drv
```

Impressões digitais (`srcversion` do `modinfo`, hash do código-fonte do módulo; igualdade prova fonte idêntica):

- `barohub` `542FCD5C5660DB8C9FC0ADA`; `c2k_usb` `7EE82CA52CE2FF379AA6DC0`; `charger_class` `4E620520A1BC3D30EF3C5E6`; `icnl9916_spi` `DB6CAC09EBA320EC299F83F`; `isee` `939C531EAE068E894C5191E`; `maghub` `80C12C0122AE406D99B9CE6`
- `mali_kbase_mt6768_r49` `16BF4ECF240FA554E792B9E`; `mali_mgm_mt6768_r49` `88415118DF4F3F9263EF15A`; `mali_prot_alloc_mt6768_r49` `0538CFDB83B3AC9A2F1CFB0`
- `mtk_charger_algorithm_class` `E974FAEF007AC1DA0CEFFAC`; `mtk_chg_type_det` `0B95E4F49E532F4572CCEEE`; `mtk_fpsgo` `DED32693FF7871B94738552`; `mtk_wdt` `9669582EAC146E40FA39B58`; `rt_pd_manager` `B3937EA9C4B2F3EC9F9E549`
- `snd-soc-fs15xx` `D8DF6AD5C6BF767B5D91205`; `snd-soc-mt6660` `00343172686151BE18DC96F`; `tcpc_class` `575D16703AE3F264E2F098A`; `tcpc_rt1711h` `93338420C6A6356E6D90B4D`; `tcpc_upm7610` `43983026A0014DD9AE03751`; `tcpci_late_sync` `9C16E05401480B7354F6B27`; `usb_meta` `C976D6E4EA1629FEAED5551`

### DEFINIÇÕES

- **Fonte oficial:** publicada pela Xiaomi (MiCode, portal open source da Xiaomi), pela MediaTek, pelo Google (AOSP, `android.googlesource.com`), ou por outro fabricante em canal oficial (por exemplo `opensource.samsung.com`, portal da Transsion, Motorola, OPPO e realme, Lenovo, Nokia).
- **Fonte compatível:** árvore MediaTek de módulos (em geral `kernel_device_modules-6.6`, `vendor/mediatek/kernel_modules`, ou a árvore `alps` do vendor) da mesma geração (kernel 6.6, Android 15, MT6768 ou MT6769), de qualquer fabricante, que compile módulos com as mesmas funções.
- **Prova de identidade (do mais forte ao mais fraco):** (1) `srcversion` igual; (2) mesma lista de símbolos importados e exportados (`nm -u` e `__ksymtab`); (3) mesmas strings de `modinfo` (`description`, `author`, `alias`, `firmware`); (4) mesmos nomes de arquivo e de Kconfig na árvore.
- **Níveis de evidência:** FACT (fonte aberta e citada), MEASURED (comando rodado, com saída), INFERRED (dedução explícita a partir de FACT ou MEASURED), UNKNOWN.

### TAREFA (passos numerados)

1. **Árvore base do vendor.** Descubra o que são os commits `810fd09a116c` e `f597b3de5ef5`. Procure no AOSP common kernel (`android15-6.6`, tags e branches de release) e em forks de vendor. Se achar, dê a URL do commit, a data e o branch. [CLI] `git ls-remote` e um fetch raso do commit, se o servidor permitir.
2. **Fonte oficial do lake.** Procure no portal open source da Xiaomi, em todos os repositórios da organização MiCode (não só no `Xiaomi_Kernel_OpenSource`), em tags e releases, por `lake`, `2410FPCC`, `Redmi 14C`, `POCO C75`, `OS3.0` e nomes de aparelhos-irmãos com o mesmo SoC e Android 15. Liste cada lugar consultado, mesmo sem resultado.
3. **Fontes compatíveis de outros fabricantes.** Ache releases oficiais de kernel 6.6 com módulos MediaTek de vendor para MT6768 ou MT6769 e Android 15 (Samsung, Transsion/Tecno/Infinix/itel, Motorola, OPPO/realme, Lenovo, Nokia/HMD e outros). Para cada uma: URL, aparelho, versão do kernel, se inclui a árvore de módulos de vendor (não só o kernel GKI), e a licença.
4. **Matriz de cobertura.** Para cada um dos 108 módulos: em qual fonte o código aparece (caminho do arquivo e do Makefile), o nível de prova de identidade alcançado e o nível de evidência. [CLI] Clone raso e busque por nome do módulo (`obj-m`, `MODULE_DESCRIPTION`, nome do arquivo `.c`). Se a máquina tiver os `.ko` do aparelho, calcule o `srcversion` de um build e compare.
5. **Componentes fora de árvore.** Mali (`r49`, driver ARM, publicado pela ARM em `developer.arm.com` como "Mali GPU Kernel Device Drivers"), Wi-Fi e conectividade (`wlan_drv_gen4m`, `wmt_drv`, conectividade MediaTek), TEE (`mcDrvModule`, `t-base-tui`, Trustonic), sensores (`sensorHub`, `barohub`, `maghub`), painéis (`dsi_panel_c3n_*`), carregadores (`sgm41543_charger`, `upm6920_charger`), fingerprint (`gf_fingerprint`, Goodix), módulos Xiaomi (`millet_*`). Para cada família, a melhor fonte oficial e a versão exata.
6. **Receita de montagem.** Com o melhor conjunto de fontes: estrutura de diretórios, ordem de build (Kleaf ou `make M=`), toolchain (clang `r510928`), configs, e como apontar para o kernel GKI (`KERNEL_SRC`, `Module.symvers`). Liste o que fica faltando.
7. **Plano de verificação sem aparelho.** Como provar no host que cada módulo recompilado é equivalente ao do aparelho: `srcversion`, diferença de símbolos importados e exportados, `modinfo`, e o gate de CRC contra o kernel modificado.
8. **Caminho legal (se faltar fonte).** Redija o pedido de código-fonte com base na GPLv2, seção 3: destinatários (portal e e-mail open source da Xiaomi; e a MediaTek, se aplicável), modelo e firmware exatos, lista dos módulos GPL pedidos (os 108 primeiro), e prazo razoável. Português e inglês. Sem dado pessoal: deixe campos `<nome>` e `<e-mail>`.

### PROVA (o que entregar)

1. **Resumo** em até 10 linhas: dá ou não dá para montar a fonte hoje, e o que falta.
2. **Tabela de fontes:** fonte | URL | tipo (oficial ou compatível) | kernel | aparelho | inclui módulos de vendor | licença | evidência.
3. **Matriz dos 108 módulos:** módulo | fonte | caminho no código | prova de identidade | evidência.
4. **Commits `810fd09a116c` e `f597b3de5ef5`:** o que são, com URL, ou UNKNOWN com a lista do que foi consultado.
5. **Receita de montagem** e **plano de verificação** (passos 6 e 7).
6. **Pedido GPL** pronto (passo 8), em português e em inglês.
7. **Lista de consultas sem resultado:** cada busca e URL consultada que não deu em nada.
8. **Lista de NÃO VERIFICADO.**

Critério de sucesso: para todo módulo da lista de 108, existe uma linha na matriz com fonte ou com "sem fonte pública" e o nível de evidência; toda URL da resposta foi aberta; nenhum `srcversion` é declarado igual sem cálculo.

### PROIBIDO

- Código vazado, ROM de teste interna, dump não oficial apresentado como fonte, ou qualquer material obtido contornando acesso. Só fontes públicas e oficiais, ou de comunidade com licença clara (e marcadas como tal).
- Afirmar compatibilidade sem prova de identidade. "Parece o mesmo" é UNKNOWN.
- Instruções que mandem gravar partição, desbloquear ou mexer em preloader, `lk`, `seccfg` ou no slot A.
- Dado pessoal (e-mail, serial, IMEI, IP) na resposta.
- Inventar números, datas, versões ou URLs.

### TETO

- IA web: até 80 páginas abertas. Priorize os passos 1, 2, 3 e 8.
- IA de linha de comando: até 150 comandos e 20 GB de disco. Só clones rasos (`--depth 1`) ou `--filter=blob:none`. Nenhum comando de aparelho (`adb`, `fastboot`).

## FIM DO PROMPT
