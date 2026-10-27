# Prompt de deep research: o que ainda falta da fonte dos módulos de vendor do lake (v2)

Copie tudo a partir de "INÍCIO DO PROMPT". Funciona em IA web (só navegação) e em IA de linha de comando (com shell e git). As seções marcadas [CLI] valem só para quem tem shell. Versão 2 (2026-10-27): refeita depois da auditoria de seis pesquisas (`docs/ultrakernel/LAKE-OSS-AUDITORIA.md`). Pede só o que ainda falta e lista o que já foi verificado, para a IA não repetir.

---

## INÍCIO DO PROMPT

Você é um pesquisador de kernel Android e de licenciamento GPL. Trabalhe com rigor de auditoria: toda afirmação tem fonte verificável. O que não tiver fonte é marcado como NÃO VERIFICADO. Não invente URL, commit, branch, tag ou nome de arquivo. Se não abriu a página nem rodou o comando, não cite.

Seja exaustivo. Seis pesquisas anteriores já cobriram o óbvio, então o valor da sua resposta está só no que for **novo** e **provado**.

### OBJETIVO

Achar o código-fonte público dos módulos de kernel do POCO C75 (`lake`) que ainda não têm fonte idêntica, e provar a identidade de cada um pelo `srcversion`. O dono quer recompilar todos os módulos de vendor contra um GKI com `CONFIG_MPTCP=y` e `CONFIG_IP_ROUTE_MULTIPATH=y` sem perder nenhuma função.

### JÁ PROVADO (não refaça; use)

- Aparelho: POCO C75, modelo `2410FPCC5G`, `lake`, MT6768, Android 15. Firmware `OS3.0.306.0.WGTMIXM`. GKI `6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k`. ODM: Longcheer.
- Os módulos de vendor foram compilados contra árvores privadas: vermagic `g810fd09a116c` (slot B), `gbdb5aebb8ade` (slot A) e `gf597b3de5ef5` (`millet_*`, `binder_gki`). Nenhum dos três commits está no AOSP `kernel/common`, no histórico completo do `MiCode/Xiaomi_Kernel_OpenSource` nem em outro repositório MiCode.
- A Xiaomi monta esses módulos com `kernel_device_modules-6.6`, `vendor/mediatek/kernel_modules` e uma árvore do ODM: `EXT_MODULES` inclui `../vendor/longcheer/mtk/kernel_modules` (`build.config.mtk_kernel_device_modules:22` do `MiCode/MTK_kernel_device_modules@dew-v-oss`). A árvore Longcheer não está publicada.
- O código MediaTek comum do lake é público. Os `srcversion` de `mtk_wdt`, `snd-soc-mt6660`, `barohub`, `maghub`, `mtk_charger_algorithm_class`, `usb_meta` e `c2k_usb` são idênticos aos de `MiCode/MTK_kernel_device_modules` (`dew-v-oss`, `spark-w-oss`) e aos dos espelhos Samsung Galaxy A05 (`mt6768-mainline/android_kernel_samsung_mt6768`, `rsuntk/android_kernel_device_modules-6.6`, `x1cen/kernel-a055f`, `Theman-Fromfar/a05m-kernel-6.6`).
- Fontes já varridas sem achar os alvos abaixo:
  - os 16 branches de `MiCode/MTK_kernel_device_modules`;
  - os 267 branches de `MiCode/Xiaomi_Kernel_OpenSource`;
  - `MiCode/mtkcam-kernel_device_modules`;
  - `MiCode/MTK_kernel_modules` (72 branches com código; o branch padrão só tem README): Mali e conectividade;
  - os espelhos Samsung A05;
  - Motorola lamu (`OWLXS/android_kernel_motorola_lamu-modules`, `-mediatek_modules`);
  - Motorola MT6853 (`nift4`);
  - `vendor-mtk-sources`;
  - `kernel/mediatek` do AOSP;
  - árvores comunitárias MT6768 4.14/4.19/5.10.
  Não liste de novo essas fontes como achado.

### ALVOS

**Grupo A: sem nenhuma fonte pública (20 módulos).** Chaves de busca tiradas dos próprios `.ko`:

| Módulo | Chaves de busca |
|---|---|
| 13 painéis: `dsi_panel_c3n_35_02_0h_dsc_vdo`, `dsi_panel_c3n_35_0f_0e_dsc_vdo`, `dsi_panel_c3n_35_0f_0l_dsc_vdo`, `dsi_panel_c3n_37_02_0d_dsc_vdo`, `dsi_panel_c3n_43_02_0a_dsc_vdo`, `dsi_panel_c3n_43_02_0k_dsc_vdo`, `dsi_panel_c3n_43_02_0n_dsc_vdo`, `dsi_panel_c3n_44_03_0b_dsc_vdo`, `dsi_panel_c3n_44_03_0j_dsc_vdo`, `dsi_panel_c3n_46_03_0c_dsc_vdo`, `dsi_panel_c3n_46_03_0f_dsc_vdo`, `dsi_panel_c3n_46_03_0g_dsc_vdo`, `dsi_panel_c3n_null_dsc_vdo` | compatible `dsi_panel_c3n,lcm`; descrição "c3n lcm driver"; strings `lcm_hbm_control_lc`, `dsi_panel_c3n,bootargs`, `[LC_DEBUG] hfp = %d`, `[LCM]%s:bias_keep_on`. Painel ativo do aparelho: `dsi_panel_c3n_46_03_0c_dsc_vdo`. O sufixo `_lc` é convenção Longcheer: procure o mesmo framework em outros projetos Longcheer (`dsi_panel_<projeto>_…`) |
| `gc_gc6133c_ii`, `sc_sc080cs_i` | descrições "gc6133c yuv driver" e "sc080cs yuv driver"; string `zaki sc080 ret=%d buf=0x%x`. Revisões vizinhas públicas: `gc_gc6133c_i` e `sc_sc080cs_ii` em `dew-v-oss` e `gale-s-oss`, e `gc6133c_hlt_i` em `earth-s-oss`. Falta exatamente `gc6133c_ii` e `sc080cs_i` |
| `millet_core` | strings `millet_millet-core: up msg: %d`, `millet_millet-core: %s: create socket error!`; vermagic `6.6.30-android15-8-gf597b3de5ef5-4k`. É o "freezer" de processos da Xiaomi (HyperOS) |
| `snd-soc-fs15xx` | FourSemi; compatible `foursemi,fs15xx`; descrição "FourSemi Smart PA Driver"; **`srcversion` alvo `D8DF6AD5C6BF767B5D91205`**. Versões oficiais já testadas, todas diferentes: `MiCode/Xiaomi_Kernel_OpenSource` em `air-t-oss` (`A139488BA32CF5EC0C2DBBC`), `blue-u-oss` (`25A4675D1FE002A04EEAC42`), `earth-s-oss` (`AB51BDD95658976A4EDB000`) e `gale-s-oss` (`17082C1CF12098FD179FE20`). Procure versões mais novas, de 2025 e 2026 |
| `sgm41543_charger` | SGMicro; descrição "sgm4154x charger driver"; compatibles `sgm,sgm41541`, `sgm,sgm41513`, `sgm,sgm41516` (e variantes `A`/`D`); string `%s get mtk-charger info fail`. Família compatível já vista no lamu (`oem/tinno_charger/switch_charger/sgm41542/sgm415xx.c`): não serve como idêntica |
| `upm6920_charger` | Unisemi Power; descrição "SC UPM6920 Charger Driver"; compatible `upm,upm6920`; string `%s: force_detect_count: %d` |
| `tcpc_upm7610` | Unisemi Power; descrição "UPM7610 TCPC Driver"; aliases `i2c:upm7610`, `i2c:rt1715`, `i2c:rt1716`; **`srcversion` alvo `43983026A0014DD9AE03751`**. Pistas de busca de código (não testadas): `physwizz/A145r` (Galaxy A14, MT6769), `Kernels-by-ravindu644/samsung_kernel_a166p`, `victor-egg/RMX5010_Kernel`, Infinix X6855, Tecno KL5 |

**Grupo B: há fonte pública, mas a versão do lake é outra (o ODM mexeu).** O `srcversion` das cópias públicas diverge. `tcpc_rt1711h`, `tcpc_class` e os 3 do Mali estão entre os 108. `rt_pd_manager`, `tcpci_late_sync`, `charger_class` e `mtk_chg_type_det` não estão: entram como marcadores da mesma pilha (Type-C e carga do ODM), porque uma árvore que acerte um deles provavelmente acerta os outros:

| Módulo | `srcversion` alvo | Cópias públicas já testadas (todas diferentes) |
|---|---|---|
| `tcpc_rt1711h` | `93338420C6A6356E6D90B4D` | Samsung A05, `dew-v-oss`, `spark-w-oss` (`tcpc/` e `sc_tcpc/`) |
| `tcpc_class` | `575D16703AE3F264E2F098A` | Samsung A05 |
| `rt_pd_manager` | `B3937EA9C4B2F3EC9F9E549` | Samsung A05, `dew-v-oss`, `spark-w-oss` |
| `tcpci_late_sync` | `9C16E05401480B7354F6B27` | Samsung A05, `dew-v-oss`, `spark-w-oss` |
| `charger_class` | `4E620520A1BC3D30EF3C5E6` | Samsung A05, `dew-v-oss`, `spark-w-oss` (dois caminhos) |
| `mtk_chg_type_det` | `0B95E4F49E532F4572CCEEE` | Samsung A05, `dew-v-oss`, `spark-w-oss` |
| `mali_prot_alloc_mt6768_r49` | `0538CFDB83B3AC9A2F1CFB0` | RESOLVIDO: idêntico em `MiCode/MTK_kernel_modules@bsp-rodin-v-oss` (também `flare-w-oss`, `spark-w-oss`) |
| `mali_mgm_mt6768_r49` | `88415118DF4F3F9263EF15A` | RESOLVIDO: idêntico em `MiCode/MTK_kernel_modules@bsp-rodin-v-oss` |
| `mali_kbase_mt6768_r49` | `16BF4ECF240FA554E792B9E` | não calculado (centenas de objetos); candidato principal `MiCode/MTK_kernel_modules@bsp-rodin-v-oss`, onde os outros dois Mali são idênticos. Versão `r49p1-03bet0`; caminho no `.ko`: `vendor/mediatek/kernel_modules/gpu/mt6768/mali_avalon/mali-r49p1` |

Para o Grupo B, a chance está em árvores de **outros clientes da Longcheer ou da mesma geração MTK** (tag `lc-master-v-t-alps-release-v0.mp1.rc-V4` ou vizinhas). Também vale o mesmo SoC com Type-C Richtek/SouthChip e Mali r49p1 mais novo.

### ONDE PROCURAR (além do já varrido)

1. **Outros produtos Longcheer com fonte GPL publicada.** Procure quais aparelhos de 2024 a 2026 a Longcheer fez para outras marcas (Lenovo, Motorola, Samsung, Nokia/HMD, ZTE, TCL, realme e outras). Depois, procure a fonte desses aparelhos no portal de cada marca. Uma árvore `vendor/longcheer/...` publicada por qualquer marca é o achado mais valioso.
2. **Busca de código com as strings exatas da tabela.** Use GitHub code search, Sourcegraph, GitLab, Gitee, Codeberg, SourceForge e Bitbucket. Strings de `printk` e `compatible` são únicas e acham cópias que a busca por nome não acha.
3. **Portais open source de fabricantes.** Samsung OSRC (modelos MT6768/MT6769 de 2024 a 2026), Motorola, Lenovo, Transsion (Tecno, Infinix, itel), realme/OPPO, Nokia/HMD, ZTE/Nubia e TCL. Os tarballs de kernel desses portais costumam trazer `vendor/` inteiro.
4. **Fabricantes dos chips.** Unisemi Power (`upm6920`, `upm7610`), FourSemi (`fs15xx`), SGMicro (`sgm41543`), GalaxyCore (`gc6133c`) e SmartSens (`sc080cs`). Procure drivers de referência publicados e envios para o kernel Linux (`lore.kernel.org`, `patchwork.kernel.org`).
5. **Xiaomi.** Algum branch `lake-*-oss` ou `pond-*-oss` publicado depois de 2026-10-26, em qualquer repositório MiCode. Procure também resposta da Xiaomi nas issues #39022, #41071, #41100 e #41145 do `MiCode/Xiaomi_Kernel_OpenSource`.
6. **Atenção:** árvores de recovery e de device com `.ko` pré-compilado (por exemplo `SavedByLight/android_device_xiaomi_lake`, `lpxx50117/twrp_device_xiaomi_lake`, `Mayuri-Chan/recovery_device_xiaomi_pond`) não são fonte. Não as reporte como fonte.

### COMO PROVAR IDENTIDADE

- Prova aceita, por ordem: (1) `srcversion` calculado igual ao alvo; (2) mesmo `compatible`, descrição e strings de `printk`, mais o mesmo nome de arquivo (vale só como "candidato forte"); (3) mesmo nome de módulo (vale só como "candidato").
- [CLI] O `srcversion` se calcula sem compilar. É o MD4 do `modpost` (`scripts/mod/sumversion.c`) sobre o arquivo `.c` de cada objeto do módulo, na ordem do `Makefile`. Em cada objeto entram também os headers do **mesmo diretório**, na ordem da primeira inclusão. Antes do hash saem os espaços, as quebras `\` + nova linha e os comentários `/* */`; strings entram inteiras e comentários `//` ficam. O MD4 é o padrão, mas lê cada palavra de 32 bits do dado em big-endian (`ntohl`). As duas palavras de tamanho entram nativas e o resultado é impresso como `htonl` de cada palavra, em hexa maiúsculo, cortado em 23 caracteres.
  - Antes de usar, valide a sua implementação: `drivers/char/ttyprintk.c` do AOSP `kernel/common` no commit `5a0ffb447c1d` dá `DD5AC3162BF38EEBEE12295`, e `mm/zsmalloc.c` dá `7159363137F692924211B69`.
  - Interpretação: um arquivo único sem header local que diverge prova fonte diferente. Com header local ou vários objetos, a divergência não prova sozinha, porque a ordem dos headers importa.
- Web sem shell: entregue o arquivo e a URL "raw" de cada candidato, para o dono calcular.

### ENTREGA

1. Uma linha por alvo dos grupos A e B: alvo; fonte nova (URL, repositório, branch e commit); caminho do arquivo e do `Makefile`; `srcversion` calculado (ou "não calculado"); nível de prova; evidência (MEASURED, FACT, INFERRED).
2. Só achados novos. O que já está em "JÁ PROVADO" não entra.
3. A lista de clientes Longcheer encontrados, com a fonte que mostra que o aparelho é Longcheer.
4. Todas as consultas sem resultado (texto exato e onde).
5. A lista NÃO VERIFICADO.

Critério de sucesso: para cada um dos 29 alvos, há uma linha com fonte nova ou com "sem fonte nova", e todo `srcversion` declarado igual foi calculado.

### LIMITES

- Vale qualquer fonte publicada abertamente, oficial ou de terceiros, em qualquer país e idioma: espelhos, tarballs de portais OEM, fóruns e repositórios pessoais. Marque a origem de cada uma.
- Não use código obtido por invasão, credencial alheia, portal de parceiro sem acesso próprio ou vazamento interno. Também não use firmware de teste interno apresentado como fonte. Isso não é fonte GPL do aparelho, não dá para publicar nem citar, e a prova por `srcversion` resolve a identidade de qualquer fonte pública.
- Não dê instruções de gravar partição, desbloquear, mexer em preloader, `lk`, `seccfg` ou no slot A.
- Não ponha dado pessoal na resposta (e-mail, serial, IMEI, IP), nem e-mails de autores tirados dos `.ko` ou de commits.
- Não invente números, datas, versões ou URLs.

### TETO

Até 200 consultas ou chamadas de ferramenta. Pare antes se todos os 29 alvos tiverem resposta.

## FIM DO PROMPT
