# C0.1 — receita de KernelSU in-tree e gate do vmlinux

Data: 2026-10-11. Pin: tiann/KernelSU 6e3860443f8fea7c5fc226277e53888033796488.

Raízes das citações: checkout KSU em kernel/upstream/KernelSU; árvore de kernel em ~/lake-build/src; scripts de gate em kernel/lake/tools e build em kernel/lake/scripts/build.sh; relatórios em docs/history/research/phase2. O trio baseline está detalhado em G1a_feasibility.md:7-11; a saída de config do G1b está em G1b_integrated_build.md:85-97. O .config baseline observado fica em ~/lake-build/src/out/bazel/output_user_root/b816944b4a8db05c78f8f829a3c594da/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64_config/out_dir/.config.

**Receita concluída; C0.1 ainda NÃO PASSOU.** G1b validou somente o alvo de configuração Kleaf. O alvo que compila e linka o vmlinux nem foi iniciado; nenhum boot de aceitação foi feito (PHASE2_ADJUSTMENTS.md:44-48).

## 1. Fiação

### Kernel

1. Usar o checkout pinado, sem executar KernelSU/kernel/setup.sh como está: o script pode clonar/atualizar o repositório (KernelSU/kernel/setup.sh:39-59). Copiar KernelSU/kernel/ para common/drivers/kernelsu/ dentro da cópia de trabalho. G1b copiou os 90 arquivos (696 KiB), em vez de apontar para fora de common/. O glob Kleaf inclui as fontes sob common/ (build/kernel/kleaf/common_kernels.bzl:526-540; G1b_integrated_build.md:51-57). **Confiança alta.**

2. Em common/drivers/Makefile, acrescentar a linha obj-$(CONFIG_KSU) += kernelsu/. Em common/drivers/Kconfig, acrescentar source "drivers/kernelsu/Kconfig" antes do endmenu. Esses dois edits foram aceitos pelo config target em G1b (G1b_integrated_build.md:53-61). No snapshot pristino, os pontos de inserção ficam perto de drivers/Makefile:201 e drivers/Kconfig:246.

3. Preservar o Kbuild do pin. Ele já lista os objetos e contém obj-$(CONFIG_KSU) += kernelsu.o (KernelSU/kernel/Kbuild:1-51,91). O Kconfig declara KSU como tristate, dependente de KPROBES && EXT4_FS, com default y (KernelSU/kernel/Kconfig:3-12). Com CONFIG_KSU=y, obj-y inclui os objetos no vmlinux; o KSU não é um kernelsu.ko.

4. Fixar a opção para o target GKI com um arquivo novo common/arch/arm64/configs/kernelsu.fragment contendo CONFIG_KSU=y. No mapa target_configs["kernel_aarch64"] em common/BUILD.bazel:149-160, acrescentar "defconfig_fragments": ["arch/arm64/configs/kernelsu.fragment"]. Kleaf aplica defconfig_fragments depois de make defconfig e valida os fragmentos contra o .config (build/kernel/kleaf/docs/kernel_config.md:31-46; [fora do repo] docs/api_reference/kernel.md:1209). Não escrever CONFIG_KSU=y diretamente em gki_defconfig: check_defconfig compara savedefconfig com esse arquivo, e o símbolo default y é omitido (G1b_integrated_build.md:59). A fragmentação proposta ainda precisa passar pelo config target; G1b provou somente o caminho via default. **Confiança alta na API Kleaf; média na mudança até executá-la.**

5. Não é necessário adicionar uma lista srcs para o kernel/ do KSU: common_kernel_sources inclui as fontes sob common/ com native.glob(["**"]). O edit Bazel necessário é acrescentar o fragmento à configuração de kernel_aarch64; os fontes têm de estar dentro de common/ (common/BUILD.bazel:149-160; common_kernels.bzl:526-540).

6. Conferir o .config associado ao mesmo output Bazel do kernel final, sem inferir pelo default. Exigir CONFIG_KSU=y, CONFIG_KPROBES=y e CONFIG_EXT4_FS=y. Falta, valor m ou configuração de output diferente reprova. G1b confirmou que o Kconfig foi carregado, que CONFIG_KSU=y apareceu no .config de //common:kernel_aarch64_config e que check_defconfig terminou sem erro (G1b_integrated_build.md:85-97). Isso prova configuração, não link.

### Userspace e APK

KernelSU/Cargo.toml:1-9 mostra userspace/ksud e userspace/ksuinit como crates do workspace. O justfile constrói ksud para aarch64-linux-android, copia-o para manager/app/src/main/jniLibs/arm64-v8a/libksud.so e constrói o manager APK via Gradle (KernelSU/justfile:4-9). O app busca libksud.so em applicationInfo.nativeLibraryDir (manager/app/.../KsuCli.kt:39-41). ksud embute assets da pasta AArch64 (userspace/ksud/src/assets.rs:43-50).

Portanto, o alvo Kleaf não entrega o APK único. A etapa Android precisa empacotar o userspace do mesmo pin e integrar a interface/app do SuperRoot. Isso não é feito pelo build do kernel. Para o caminho in-tree, ksud deve detectar o KSU já carregado e pular o fallback kernelsu.ko (userspace/ksud/src/late_load.rs:43-68). A integração dessa versão de APK ainda não foi compilada nem executada. **Confiança alta na separação [fora do repo] kernel/userspace; baixa para o APK integrado até validação.**

## 2. Assinatura e símbolos

G1a encontrou seis referências ausentes de Module.symvers no caminho LKM: kallsyms_lookup_name, kallsyms_lookup, kallsyms_lookup_size_offset, filp_open, commit_creds e __tracepoint_sys_enter (G1a_feasibility.md:42-57). Para o KSU in-tree, esse bloqueio de imports de módulo deixa de se aplicar: as referências entram no link do próprio kernel em vez de resolver imports de kernelsu.ko. O link real ainda precisa confirmar todas elas. **Confiança alta na diferença builtin/LKM; resultado do link não verificado.**

MODULE_SIG_PROTECT=y também não barra o KSU builtin: sig_ok e a lista de símbolos unprotected são avaliados para módulos em kernel/module/main.c:1157-1172; MODULE_SIG_PROTECT define sig_enforce=false no caminho GKI (kernel/module/signing.c:24-35,126-139). Manter essa proteção para módulos externos. O antigo problema com kernel_read/kernel_write e namespace protegido era do caminho de module load/modpost, não de assinatura de código builtin (G1a_feasibility.md:59-66,116-129).

O alvo declara system_trusted_key = "google_gki_ab13771415_modsign_cert.pem" (common/BUILD.bazel:158). O .config observado tem CONFIG_SYSTEM_TRUSTED_KEYRING=y e CONFIG_SYSTEM_TRUSTED_KEYS="google_gki_ab13771415_modsign_cert.pem" (baseline .config:7255-7256); CONFIG_SECONDARY_TRUSTED_KEYRING não está definido (:7258). certs/Makefile:29-34 gera a lista X.509 compilada a partir de CONFIG_SYSTEM_TRUSTED_KEYS e certs/system_keyring.c:279-296 a carrega em builtin_trusted_keys. O verificador de assinatura usa VERIFY_USE_SECONDARY_KEYRING, mas, com a keyring secundária desativada, cai explicitamente em builtin_trusted_keys (kernel/module/signing.c:71-74; certs/system_keyring.c:339-346). Portanto, sim: o certificado Google continua no caminho que valida módulos vendor, desde que config/certificado permaneçam no vmlinux. module_sig_check define sig_ok=true quando a assinatura valida (kernel/module/signing.c:89-99). O link e o estado sig_ok real dos módulos neste novo kernel não foram confirmados. **Confiança alta no caminho estático; runtime pendente.**

## 3. Gate novo para vmlinux modificado

gate_kmi_crc.sh agora é apenas uma etapa de compatibilidade dos imports do corpus: não prova KSU linkado, boot, nem ABI intacta (PHASE2_ADJUSTMENTS.md:5-15,47). Rodar sobre o vmlinux.symvers/Module.symvers produzido pelo mesmo //common:kernel_aarch64, nunca sobre o symvers de controle (G1b_integrated_build.md:99-109).

| ID | Critério PASS |
|---|---|
| C01-CFG | .config do build final tem exatamente CONFIG_KSU=y, CONFIG_KPROBES=y e CONFIG_EXT4_FS=y; keyring/cert Google preservados. Registrar caminho e hash dos artefatos. |
| C01-LINK | llvm-nm --defined-only vmlinux encontra pelo menos um símbolo definido que começa com ksu_; confirmar também ksu_syscall_hook_manager_init se presente. Guardar hash do vmlinux. |
| C01-KMI | [fora do repo] tools/gate_kmi_crc.sh recebe symvers do build KSU e retorna PASS/exit 0; exigir 557 arquivos, 370 módulos únicos e zero mismatches, missing_exports, conflicting_crcs, export_type_mismatches, namespace_mismatches e unresolved. Histórico de controle/cert: compared=2309, mismatches=0, missing_exports=0 (G1b_integrated_build.md:109). |
| C01-ABI | Produzir diff de config e ABI/KMI contra baseline e justificar cada diferença; sem drift sem explicação (PHASE2_ADJUSTMENTS.md:47). |
| C01-VM | Não há vermagic de kernelsu.ko para comparar. Extrair UTS_RELEASE e vermagic completo do kernel final; comparar módulo a módulo com o conjunto exato de system/vendor modules destinado a este boot. Falha qualquer incompatibilidade de release, SMP, preempt, unload, modversions ou arquitetura sem exceção justificada. O gate CRC atual não faz isso. |
| C01-SIG | Preservar configuração/certificado Google e confirmar lista X.509 no build; no boot, não aceitar rejeição/erro de assinatura de módulos vendor. Não deduzir sig_ok só do .config. |
| C01-PACK | Registrar hash do Image.lz4 testado; reextrair o payload do boot.img candidato e exigir hash idêntico. Registrar também hash do boot.img. |
| C01-DMESG | dmesg-accept combina ausência de erros novos com marcadores positivos de init e registro dos hooks, listados abaixo. |
| C01-DEVICE | Boot e checks T3 em B passam: dmesg, Wi-Fi, Bluetooth, su e módulos vendor; imagem testada == empacotada. |

O gate_kmi_crc.sh valida CRCs, providers, export_type e namespace para o corpus ([fora do repo] tools/gate_kmi_crc.sh:1-15,41-72,89-101). PASS nele não prova KSU no vmlinux nem que a imagem bootada é a mesma inspecionada.

Para a checagem build-side, CONFIG, VMLINUX e SYMVERS devem apontar para outputs da mesma execução de //common:kernel_aarch64. Executar no host de build futuro, com scratch controlado:

    set -euo pipefail
    export TMPDIR="[artefato local, fora do repo]/codex/c01"
    mkdir -p "$TMPDIR"
    grep -Fx 'CONFIG_KSU=y' "$CONFIG"
    grep -Fx 'CONFIG_KPROBES=y' "$CONFIG"
    grep -Fx 'CONFIG_EXT4_FS=y' "$CONFIG"
    grep -Fx 'CONFIG_SYSTEM_TRUSTED_KEYRING=y' "$CONFIG"
    grep -Fx 'CONFIG_SYSTEM_TRUSTED_KEYS="google_gki_ab13771415_modsign_cert.pem"' "$CONFIG"
    llvm-nm --defined-only "$VMLINUX" | awk '$3 ~ /^ksu_/ { found=1 } END { exit !found }'
    [fora do repo] tools/gate_kmi_crc.sh "$SYMVERS"

O exit code do gate KMI precisa ser zero e a saída precisa satisfazer todos os contadores de C01-KMI. As variáveis não devem misturar outputs de dois Bazel output roots.

### vermagic

G1a reportou UTS_RELEASE local 6.6.89-android15-8-4k e 6.6.89-android15-8-g5a0ffb447c1d-ab13771415-4k como referência (G1a_feasibility.md:7-12,106-114). Essa diferença deixa de ser o vermagic de um hipotético kernelsu.ko, mas permanece um risco para módulos vendor. O inventário existente tem mais de uma variante: entre elas 6.6.89-android15-8-g810fd09a116c-4k e 6.6.89-android15-8-gbdb5aebb8ade-4k ([fora do repo] tools/data/modules_inventory.tsv:1); primeiro selecionar os módulos da imagem C75 exata e então exigir compatibilidade para cada módulo que será carregado. Não reduzir os 557 arquivos a uma única string nem ignorar divergências ([fora do repo] tools/data/modules_inventory.tsv; cabeçalho em [fora do repo] tools/gate_kmi_crc.sh:10-15). O conjunto ativo e seu UTS esperado ainda precisam ser reconciliados. **Confiança alta na divergência reportada; conjunto ativo pendente.**

### dmesg-accept e hooks

[fora do repo] tools/dmesg_new_errors.sh encontra classes de erro novas contra baseline stock e retorna 0 se não encontrar, mas não exige sinais positivos de KSU (dmesg_new_errors.sh:1-16,76-115). O wrapper dmesg-accept deve exigir:

- [fora do repo] tools/dmesg_new_errors.sh <dmesg.txt> com exit 0;
- "welcome to KernelSU version" — começo de kernelsu_init (KernelSU/kernel/core/init.c:91-93);
- "hook_manager: ksu_hook_manager_init called" (kernel/hook/syscall_hook_manager.c:128-135);
- "hook_manager: register_syscall_regfunc kretprobe: 0" e "hook_manager: register_syscall_unregfunc kretprobe: 0": retorno zero de register_kretprobe (syscall_hook_manager.c:28-45,133-136);
- "hook_manager: sys_enter tracepoint registered" e ausência de "failed to register sys_enter tracepoint" (syscall_hook_manager.c:145-155);
- "Loading compiled-in X.509 certificates" indica que o carregador da lista compilada foi executado (common/certs/system_keyring.c:279-296);
- ausência de "late load mode, skipping kprobe hooks", pois o alvo é builtin (KernelSU/kernel/core/init.c:110-114,147-163,178-186).

A chamada do init sozinha não prova registro: o código imprime o resultado de cada kretprobe e do tracepoint. Retorno não zero reprova. **Confiança alta nos marcadores do source; logs de boot não verificados.**

Critério mínimo para a parte positiva do dmesg-accept, depois de salvar o dmesg do boot:

    set -euo pipefail
    [fora do repo] tools/dmesg_new_errors.sh "$DMESG"
    grep -Fq 'Loading compiled-in X.509 certificates' "$DMESG"
    grep -Fq 'welcome to KernelSU version' "$DMESG"
    grep -Fq 'hook_manager: ksu_hook_manager_init called' "$DMESG"
    grep -Fq 'hook_manager: register_syscall_regfunc kretprobe: 0' "$DMESG"
    grep -Fq 'hook_manager: register_syscall_unregfunc kretprobe: 0' "$DMESG"
    grep -Fq 'hook_manager: sys_enter tracepoint registered' "$DMESG"
    ! grep -Fq 'failed to register sys_enter tracepoint' "$DMESG"
    ! grep -Fq 'late load mode, skipping kprobe hooks' "$DMESG"

## 4. Sequência futura de device (não executada)

1. Fazer backup do boot_b original, guardar seu hash e o procedimento de recuperação por teclas. Fase 2 exige backup e fastboot_guard, autorização literal “sim” para gravar e proíbe slot A (PHASE2_ADJUSTMENTS.md:29-33).
2. Reempacotar boot.img a partir do boot C75 verificado, substituindo somente o payload do kernel pelo Image.lz4 que passou C01. Reextrair o payload e confirmar o hash. fastboot_guard exige arquivo regular de 67.108.864 bytes, magic ANDROID!, SHA-256 na allowlist e tamanho igual à partição boot_b ([fora do repo] tools/fastboot_guard.sh:17-29,83-105,116-130).
3. Só com “sim” literal para esta gravação, usar fastboot_guard.sh flash boot_b <boot.img>. O guard rejeita qualquer partição diferente, exige current-slot=b e is-userspace diferente de yes antes de enviar o flash (fastboot_guard.sh:83-105,133-145). Slot A nunca. O wrapper cria cópia temporária sob TMPDIR ou /tmp (fastboot_guard.sh:92); na execução futura, definir TMPDIR=[artefato local, fora do repo]/codex/c01/ para obedecer a regra de scratch.
4. Após boot em B, capturar dmesg e rodar C01-DMESG. Fazer checks T3: Wi-Fi conecta e trafega; Bluetooth liga, descobre e pareia; APK/daemon KSU funciona; app autorizado recebe su e confirma UID 0; app não autorizado continua sem root. Confirmar que módulos vendor necessários carregam sem erro de assinatura ou formato.
5. Aceitar só se todos os critérios passarem e a imagem empacotada corresponder ao kernel testado. Rollback requer outro “sim” literal; nunca gravar slot A. Nada disso foi executado nesta pesquisa.

**Confiança alta** nos bloqueios do fastboot_guard e na regra do slot; **média** no repack, pois a ferramenta não foi exercitada neste C01.

## 5. Plano de recursos

- O .config observado mostra CONFIG_LTO_NONE=y e full/thin LTO desativados (.config:738-740). Reconfirmar no config do link final.
- G1b usou --local_cpu_resources=2 --local_ram_resources=2560 para o alvo _config, que terminou em 31,258 s e 315 ações; isso não mediu nem iniciou o link (G1b_integrated_build.md:11-20,65-80). scripts/build.sh passa --local_cpu_resources e --local_ram_resources, mas por padrão pede nproc e 8000 MB (scripts/build.sh:8,17,129-132). Para execução futura, começar com 1 CPU, --jobs=1 e --local_ram_resources=4096. Esses limites orientam agendamento Bazel; não são teto de RSS do linker.

Com memória/espaço suficientes, o comando proposto dentro da cópia reflink é: cd [artefato local, fora do repo]/codex/c01/ksu-build; export TMPDIR="[artefato local, fora do repo]/codex/c01"; mkdir -p "$TMPDIR"; SOURCE_DATE_EPOCH=1752273969 [fora do repo] tools/bazel build --config=stamp --jobs=1 --local_cpu_resources=1 --local_ram_resources=4096 --sandbox_base="$TMPDIR/bazel-sandbox" //common:kernel_aarch64. Não executar nesta rodada; a captura atual falha nos pré-requisitos de RAM/disco acima.
- Pico do link não foi medido. Observação mais próxima: dois rustc somaram cerca de 3,44 GiB RSS durante config, quando MemAvailable era 1,5 GiB e SwapFree menor que 4 MiB (G1b_integrated_build.md:13-20). Reservar pelo menos 8 GiB de RAM disponível antes do link é orçamento prudencial, não medição nem garantia.
- Captura local revalidada em 2026-10-11: RAM total 15 GiB, 7,5 GiB disponíveis, 13 GiB de swap livre; /home tem 23 GiB livres (96%). O preflight de scripts/build.sh exige 80 GiB livres (scripts/build.sh:21-26). A RAM disponível está ligeiramente abaixo da reserva prudencial de 8 GiB; o bloqueio decisivo nesta captura é o disco. A captura anterior do G1b tinha 1,5 GiB disponíveis e menos de 4 MiB de swap livre.
- Worktree Git não se ajusta: Kleaf é repo com vários gitlinks. G1b usou cp -a --reflink=always na mesma Btrfs, excluiu out/ (5,9 GiB já existentes) e mediu 249,68 MiB de blocos exclusivos depois do config (G1b_integrated_build.md:22-40). Repetir isolamento em [artefato local, fora do repo]/codex/c01/ksu-build/ preserva a árvore comum e evita duplicar os dados compartilhados. O pico do novo output nunca foi medido; 5,9 GiB é tamanho observado preexistente, não máximo. Até medir uma execução, usar 80 GiB livres como requisito.
- O snapshot de G1b já tinha BUILD.bazel alterado e certificado Google não rastreado. scripts/build.sh clean executa git checkout em common e remove esse certificado (G1b_integrated_build.md:42-47; scripts/build.sh:118-125). Não usar clean sem preservar e revalidar o patch da chave.

**Conclusão de recurso: não viável nesta captura, principalmente pelo disco.** Esperar espaço livre suficiente e ao menos 8 GiB de RAM disponível conforme orçamento prudencial. Depois, criar a cópia reflink no scratch c01 e iniciar o alvo de link. **Confiança alta** nos recursos observados/preflight; **baixa** no pico exato do link.

## 6. Riscos abertos e não verificado

1. Link KSU→vmlinux não compilado; o CONFIG_KSU=y de G1b veio do default, não do fragmento proposto. Nenhum símbolo ksu_* confirmado.
2. gate_kmi_crc não rodou sobre symvers do build integrado. PASS histórico pertence ao controle/cert.
3. Diff de ABI/config do kernel modificado não existe.
4. UTS_RELEASE reportado diverge da referência; inventário mistura variantes. Falta identificar o conjunto ativo de módulos C75 e provar compatibilidade.
5. Certificado/keyring Google têm caminho estático presente, mas não houve vmlinux final/boot para verificar a carga e o estado sig_ok dos módulos.
6. Marcadores de init/hooks vêm do source; não há dmesg de boot para provar registro real.
7. ksud, ksuinit e APK não foram compilados ou testados com kernel builtin; verificar que o daemon não usa fallback LKM.
8. Boot, Wi-Fi, Bluetooth, su, módulos vendor e identidade do payload empacotado não verificados. Slot A permanece proibido; gravação/rollback requerem “sim” do dono.
9. Recursos atuais abaixo do preflight; pico de RAM/disco do link desconhecido.

## Confiança por afirmação

| Afirmação | Confiança |
|---|---|
| Pin e fiação Kconfig/Kbuild | Alta |
| G1b aceitou o config target e produziu CONFIG_KSU=y | Alta |
| Build final e símbolo ksu_* no vmlinux | Não verificado |
| Os seis símbolos e MODULE_SIG_PROTECT deixam de ser gate de módulo para KSU builtin | Alta no modelo; link não provado |
| Caminho do certificado Google continua configurado/compilável | Alta no source/config; runtime pendente |
| KMI/CRC, ABI diff e vermagic do kernel KSU | Não verificado |
| Recursos abaixo do preflight nesta captura | Alta |
| Pico de RAM/disco | Estimativa prudencial, não medido |
| Boot e aceitação T3 | Não verificado |

**C0.1 permanece aberto.** Este documento torna a integração uma sequência concreta, mas só o build, os gates PASS e o boot de aceitação concluem C0.1.

