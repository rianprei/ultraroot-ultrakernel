# Pedido GPL da fonte do lake (pronto para o dono enviar)

Quem envia: o dono, com nome e e-mail próprios. Enviar publica o texto: a decisão é do dono. Base dos fatos: `docs/ultrakernel/LAKE-OSS-AUDITORIA.md` e `docs/ultrakernel/data/lake-vendor-modules-fingerprint.tsv`.

## Onde enviar

1. Comentário nas issues abertas do lake no `MiCode/Xiaomi_Kernel_OpenSource`: #39022 (2025-02-01), #40289, #40309, #41071, #41100, #41119 (pedido GPL formal, 2026-08-26) e #41145. A #40912 está fechada. Ou abrir uma issue nova.
2. E-mail de open source da Xiaomi: endereço NÃO VERIFICADO. Usar o que aparece em Configurações → Sobre o telefone → informações legais → licenças de código aberto do próprio aparelho (caminho NÃO VERIFICADO).
3. Precedente de escalada: a issue #41000 (Xiaomi 11T, `agate`) foi aberta em 2026-05-15. Em 2026-05-28 um comentário diz "yep branch agate-u-oss is here", depois de o autor ter tentado issues, suporte, o e-mail "oss-request" e a Software Freedom Conservancy. O branch `agate-u-oss` existe (API do GitHub). A relação de causa entre a denúncia e a publicação: NÃO VERIFICADO.

## Português

> **Assunto:** Pedido de código-fonte correspondente (GPLv2, seção 3): POCO C75 `2410FPCC5G` (`lake`), firmware OS3.0.306.0.WGTMIXM
>
> Prezada equipe de Open Source da Xiaomi,
>
> Sou proprietário de um POCO C75, modelo 2410FPCC5G (codinome `lake`), com o firmware oficial OS3.0.306.0.WGTMIXM (a Xiaomi já distribui também a OS3.0.307.0.WGTMIXM para o lake). Esse firmware distribui em formato binário o kernel Linux (GKI android15-6.6, `6.6.89-android15-8`) e 370 módulos de kernel de vendor. Destes, 366 declaram em `modinfo` licença GPL ("GPL", "GPL v2", "Dual BSD/GPL" ou "GPL and additional rights"). Os módulos foram compilados a partir de árvores identificadas pelos commits `810fd09a116c` (vermagic `6.6.89-android15-8-g810fd09a116c-4k`) e `f597b3de5ef5` (vermagic `6.6.30-android15-8-gf597b3de5ef5-4k`). Esses commits não estão no Android Common Kernel público. A fonte publicada para o aparelho-irmão (`dew-v-oss`) não serve para o lake: os módulos de Type-C dela têm `srcversion` diferente do lake, e os painéis `dsi_panel_c3n_*` do lake não estão nela.
>
> Nos termos da seção 3 da GNU General Public License versão 2, solicito o código-fonte completo e correspondente desses componentes, incluindo "todos os scripts usados para controlar a compilação e a instalação":
>
> 1. A árvore do kernel nos commits `810fd09a116c` e `f597b3de5ef5`, com o defconfig e os fragmentos usados.
> 2. As árvores `kernel_device_modules-6.6` e `vendor/mediatek/kernel_modules` usadas no lake (os próprios módulos registram esses caminhos), mais o device tree e o DTBO do lake.
> 3. Os scripts e configurações de build (Kleaf/`build.config.*`) e a versão da toolchain.
> 4. Com prioridade, a fonte destes 108 módulos GPL: 8250_mtk, arm_dsu_pmu, bootprof, cache-parity, ccci_md_all, ccmni, ccu_isp4, cfg80211, clk-common, clk-mt6768, cqhci, drm_display_helper, dsi_panel_c3n_35_02_0h_dsc_vdo, dsi_panel_c3n_35_0f_0e_dsc_vdo, dsi_panel_c3n_35_0f_0l_dsc_vdo, dsi_panel_c3n_37_02_0d_dsc_vdo, dsi_panel_c3n_43_02_0a_dsc_vdo, dsi_panel_c3n_43_02_0k_dsc_vdo, dsi_panel_c3n_43_02_0n_dsc_vdo, dsi_panel_c3n_44_03_0b_dsc_vdo, dsi_panel_c3n_44_03_0j_dsc_vdo, dsi_panel_c3n_46_03_0c_dsc_vdo, dsi_panel_c3n_46_03_0f_dsc_vdo, dsi_panel_c3n_46_03_0g_dsc_vdo, dsi_panel_c3n_null_dsc_vdo, extcon-mtk-usb, gc_gc6133c_ii, gf_fingerprint, i2c-mt65xx, iommu_gz, leds-mt6370-flash, mac80211, mali_kbase_mt6768_r49, mali_mgm_mt6768_r49, mali_prot_alloc_mt6768_r49, mcDrvModule, mediatek-drm, millet_core, monitor_hang, mt6358_battery, mt6358-regulator, mt635x-auxadc, mt6370, mt6370-adc, mt6370-charger, mt6370-regulator, mt6397, mt6577_auxadc, mt6768-mt6358, mtk_battery_manager, mtk_battery_oc_throttling, mtk-btcvsd, mtk-cqdma, mtk-dvfsrc, mtk-dvfsrc-devfreq, mtk-dvfsrc-regulator, mtk-emi, mtk-icc-core, mtk_iommu, mtk_irtx_pwm, mtk_jpeg, mtk-mmc, mtk-mml, mtk-pmic-keys, mtk-pwm, mtk-scpsys, mtk-scpsys-bringup, mtk-scpsys-mt6768, mtk-smi, mtk_spi, mtk-sp-spk-amp, mtk-uart-apdma, mtk_u_ether, mtk_usb_f_rndis, mtk_wdt, nvmem-mt635x-efuse, phy-generic, phy-mtk-tphy, pinctrl-mtk-v2, pwm-mtk-disp, reboot-mode, rtc-mt6397, scheduler, scp, sc_sc080cs_i, sec-rng, sensorHub, sgm41543_charger, simtray, snd-soc-fs15xx, snd-soc-mt6358, snd-soc-mt6660, snd-soc-mt6768-afe, snd-soc-mtk-common, spi-mt65xx, sp_sp0821_iii, syscon-reboot-mode, t-base-tui, tcpc_class, tcpc_rt1711h, tcpc_upm7610, thermal_monitor, ufs-mediatek-dbg, ufs-mediatek-mod, upm6920_charger, wifi_ant_check, wlan_drv_gen4m_6768, wmt_drv.
> 5. Depois, a fonte dos demais módulos GPL do firmware, para cada build publicada para o lake (OS1, OS2 e OS3).
>
> Peço a publicação no repositório MiCode/Xiaomi_Kernel_OpenSource (por exemplo, um branch `lake-v-oss`) ou o envio por meio eletrônico, em até 30 dias. Se a fonte já estiver publicada, peço o local exato. Não peço os 4 módulos declarados "Proprietary" (cpuqos_ext, eas_ext, fpsgo, mtk_em).
>
> Atenciosamente,
> `<nome>`, `<e-mail>`

## English

> **Subject:** Request for complete corresponding source code (GPLv2, Section 3): POCO C75 `2410FPCC5G` (`lake`), firmware OS3.0.306.0.WGTMIXM
>
> Dear Xiaomi Open Source team,
>
> I own a POCO C75, model 2410FPCC5G (codename `lake`), running official firmware OS3.0.306.0.WGTMIXM (Xiaomi also distributes OS3.0.307.0.WGTMIXM for lake). This firmware distributes, in binary form, the Linux kernel (GKI android15-6.6, `6.6.89-android15-8`) and 370 vendor kernel modules, of which 366 declare a GPL license in `modinfo` ("GPL", "GPL v2", "Dual BSD/GPL" or "GPL and additional rights"). The modules were built from trees identified by commits `810fd09a116c` (vermagic `6.6.89-android15-8-g810fd09a116c-4k`) and `f597b3de5ef5` (vermagic `6.6.30-android15-8-gf597b3de5ef5-4k`). Neither commit is in the public Android Common Kernel. The source published for the sibling device (`dew-v-oss`) does not cover lake: its Type-C modules have a different `srcversion` from lake's, and lake's `dsi_panel_c3n_*` panels are not in it.
>
> Under Section 3 of the GNU General Public License version 2, I request the complete corresponding source code for these components, including "all the scripts used to control compilation and installation":
>
> 1. The kernel tree at commits `810fd09a116c` and `f597b3de5ef5`, with the defconfig and fragments used.
> 2. The `kernel_device_modules-6.6` and `vendor/mediatek/kernel_modules` trees used for lake (the modules themselves record these paths), plus the lake device tree and DTBO.
> 3. The build scripts and configuration (Kleaf/`build.config.*`) and the toolchain version.
> 4. As a priority, the source of these 108 GPL modules: 8250_mtk, arm_dsu_pmu, bootprof, cache-parity, ccci_md_all, ccmni, ccu_isp4, cfg80211, clk-common, clk-mt6768, cqhci, drm_display_helper, dsi_panel_c3n_35_02_0h_dsc_vdo, dsi_panel_c3n_35_0f_0e_dsc_vdo, dsi_panel_c3n_35_0f_0l_dsc_vdo, dsi_panel_c3n_37_02_0d_dsc_vdo, dsi_panel_c3n_43_02_0a_dsc_vdo, dsi_panel_c3n_43_02_0k_dsc_vdo, dsi_panel_c3n_43_02_0n_dsc_vdo, dsi_panel_c3n_44_03_0b_dsc_vdo, dsi_panel_c3n_44_03_0j_dsc_vdo, dsi_panel_c3n_46_03_0c_dsc_vdo, dsi_panel_c3n_46_03_0f_dsc_vdo, dsi_panel_c3n_46_03_0g_dsc_vdo, dsi_panel_c3n_null_dsc_vdo, extcon-mtk-usb, gc_gc6133c_ii, gf_fingerprint, i2c-mt65xx, iommu_gz, leds-mt6370-flash, mac80211, mali_kbase_mt6768_r49, mali_mgm_mt6768_r49, mali_prot_alloc_mt6768_r49, mcDrvModule, mediatek-drm, millet_core, monitor_hang, mt6358_battery, mt6358-regulator, mt635x-auxadc, mt6370, mt6370-adc, mt6370-charger, mt6370-regulator, mt6397, mt6577_auxadc, mt6768-mt6358, mtk_battery_manager, mtk_battery_oc_throttling, mtk-btcvsd, mtk-cqdma, mtk-dvfsrc, mtk-dvfsrc-devfreq, mtk-dvfsrc-regulator, mtk-emi, mtk-icc-core, mtk_iommu, mtk_irtx_pwm, mtk_jpeg, mtk-mmc, mtk-mml, mtk-pmic-keys, mtk-pwm, mtk-scpsys, mtk-scpsys-bringup, mtk-scpsys-mt6768, mtk-smi, mtk_spi, mtk-sp-spk-amp, mtk-uart-apdma, mtk_u_ether, mtk_usb_f_rndis, mtk_wdt, nvmem-mt635x-efuse, phy-generic, phy-mtk-tphy, pinctrl-mtk-v2, pwm-mtk-disp, reboot-mode, rtc-mt6397, scheduler, scp, sc_sc080cs_i, sec-rng, sensorHub, sgm41543_charger, simtray, snd-soc-fs15xx, snd-soc-mt6358, snd-soc-mt6660, snd-soc-mt6768-afe, snd-soc-mtk-common, spi-mt65xx, sp_sp0821_iii, syscon-reboot-mode, t-base-tui, tcpc_class, tcpc_rt1711h, tcpc_upm7610, thermal_monitor, ufs-mediatek-dbg, ufs-mediatek-mod, upm6920_charger, wifi_ant_check, wlan_drv_gen4m_6768, wmt_drv.
> 5. Then, the source of the remaining GPL modules in the firmware, for every build published for lake (OS1, OS2 and OS3).
>
> Please publish it in MiCode/Xiaomi_Kernel_OpenSource (for example, a `lake-v-oss` branch) or send it electronically within 30 days. If it is already published, please tell me the exact location. I am not requesting the 4 modules declared "Proprietary" (cpuqos_ext, eas_ext, fpsgo, mtk_em).
>
> Sincerely,
> `<name>`, `<e-mail>`
