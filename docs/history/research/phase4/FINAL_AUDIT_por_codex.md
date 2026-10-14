# Auditoria adversarial final — C0.3 antes do flash

Data: 2026-10-11. Escopo: host-only e read-only. Nenhum build, install, fastboot ou acesso ao device foi executado nesta auditoria.

## Veredito

**CORRIGIR antes do dono flashar.** Não encontrei um gatilho novo de bootloop introduzido pelo delta do C0.3; o delta declarado é de versão e identidade do manager. Porém, o `guard_hashes.txt` presente ainda autoriza C0.1, e o rollback documentado não recupera de forma garantida o estado `slot-unbootable:b=yes`. O teste `su -c id` também não distingue KSU do Magisk já instalado. Consolidar um plano C0.3 único, corrigir o arquivo de hashes, fechar a recuperação do slot B e tornar o aceite de root específico do KSU.

## Achados numerados

### 1. [BLOQUEADOR — rollback pode parar com B não inicializável]

**Evidência:** `[fora do repo] docs/DEVICE-TEST-PROTOCOL.md:259-262` registra A como `slot-unbootable=yes`, B com `slot-retry-count=1`; `:241-242,270-274` dizem que o contador/reação a boot falho ainda não foi isolado e que uma falha pode consumir o retry. O próprio protocolo diz que ambos os slots inválidos chegam a `fastboot_init`, mas não validou o comportamento de um boot B ruim. `phase4/C01_device_plan.md:129-137` manda reflashear o backup e só reiniciar se `slot-unbootable:b=no`. O wrapper `[fora do repo] tools/fastboot_guard.sh:107-109` recusa `set_active` (não está na allowlist); para reboot, `:150-159` verifica B e fastboot bootloader, mas não reabilita um slot. A especificação A/B do AOSP determina que `set_active <slot>` limpa o estado unbootable e reinicia o retry count ([Implement A/B updates](https://source.android.com/docs/core/ota/ab/ab_implement?authuser=56)); o bootloader também pode marcar um slot sem boot bem-sucedido como unbootable e escolher outro slot ([A/B seamless updates](https://source.android.com/docs/core/ota/ab?authuser=758840678)).

**Risco concreto:** se o boot de C0.3 consumir o único retry e B ficar unbootable, A já está registrado como unbootable. Regravar os bytes do backup em `boot_b` não tem no procedimento um passo que limpe o estado do slot; o plano manda parar quando o estado continua `yes`. Se `current-slot` não for `b`, o wrapper também recusa até o flash do backup. Não afirmo que isso certamente acontecerá com o LK deste aparelho; afirmo que o caso de falha previsto não tem uma recuperação autorizada e validada no plano atual.

**Correção:** antes do flash, documentar e proteger um caminho de recuperação que grave o backup verificado em `boot_b` e reative **somente B** com o comando/guard adequado, confirmando depois `current-slot=b`, `slot-unbootable:b=no` e o estado do retry antes de reiniciar. Não liberar um bypass genérico de fastboot nem ativar A. Se esse caminho não for incluído, o plano não está pronto para um teste cujo critério inclui rollback.

### 2. [ALTO — plano e allowlist existentes apontam para C0.1]

**Evidência:** o `[fora do repo] kernel/lake/guard_hashes.txt` atual contém backup `3890fb96…829fc7` e SHA `74090569…78a1120` de `C01_ksu_boot_b.img`; não contém SHA de C0.3 `2977e1dc…2d6304fe`. O guard compara SHA exato e recusa imagem fora da lista (`[fora do repo] tools/fastboot_guard.sh:100-105`). `phase4/C01_device_plan.md:9,102,125` ainda manda flashar C0.1 e descreve `KSU_VERSION=16`. Já `research/MASTER_PLAN.md:61-70` diz explicitamente usar C0.3, traz o SHA C0.3 e inclui um `printf` que geraria a lista correta — mas o arquivo presente não foi atualizado por esse comando. O C0.1 kernel mantém a identidade de manager anterior (versão fallback 16 e cert esperado padrão), enquanto o APK release atual tem cert `efb8…`; veja `phase4/C01_build_result.md:51-55`, `phase4/MANAGER_build.md:51-56` e `phase4/C03_stablekey_result.md:17,42-47`.

**Efeito:** com a allowlist atual, o flash de C0.3 será recusado antes da escrita; isso é fail-closed. Se o operador seguir o plano antigo e escolher C0.1, o kernel pode bootar, mas não reconhecerá o APK release C0.3 como manager, portanto não entregará root ao manager pretendido.

**Correção:** substituir/rotular `C01_device_plan.md` como obsoleto e atualizar o `guard_hashes.txt` real para conter exatamente o backup e o SHA C0.3. Conferir o hash do arquivo de imagem e a lista final antes de exportar `FASTBOOT_GUARD_HASHES`. Manter o bloco final do `MASTER_PLAN.md` como única instrução operacional e incorporar nele os gates detalhados de Z0 e de rollback.

### 3. [ALTO — o aceite `su -c id` pode atribuir root ao provedor errado]

**Evidência:** `research/MASTER_PLAN.md:8` registra Magisk como root atual; `:69` aceita `su -c id` como prova de contexto KSU. `id` sem outra consulta prova `uid=0`, mas não identifica quem atendeu `su`. No pin KSU, `KsuCli.createRootShell()` tenta primeiro `libksud.so debug su` e cai para o `su` genérico se falhar (`manager/app/src/main/java/me/weishu/kernelsu/ui/util/KsuCli.kt:78-110`), portanto até um teste genérico do shell pode ser satisfeito pelo Magisk.

**Efeito:** uma resposta `uid=0` pode virar falso passe de KSU, deixando sem detecção um manager não coroado ou um KSU `su` quebrado.

**Correção:** aceite o manager só após confirmar `Natives.isManager` e UAPI 5 no APK/kernel, e execute a verificação privilegiada pela rota explícita do KSU (`libksud.so debug su`/`ksud`), não por `su` resolvido pelo PATH. Se a validação usar UID shell via ADB, trate o grant desse UID separadamente. Não use apenas `uid=0` como atribuição ao KSU.

### 4. [MÉDIO — APK chamado release está marcado debuggable]

**Evidência:** inspeção do APK exato `SuperRoot-manager-release.apk` (SHA-256 `c8b66244…69f73956`) com `aapt2 dump badging` retornou `application-debuggable`. O mesmo APK passa no `apksigner`: um signer, V2=true, package `me.weishu.kernelsu`, versionCode `32673`, cert SHA-256 `efb8d49f…8d99ad9`; assim a identidade que o kernel aceita está correta, mas o flag de depuração permanece ligado. O APK também contém `lib/arm64-v8a/libksud.so`, cujo hash é `8c600dc9…b19f410`, igual ao binário registrado em `phase4/MANAGER_build.md:38-49`.

**Risco:** superfície de depuração desnecessária num manager de root assinado pela chave estável, além de não corresponder a um artefato release endurecido.

**Correção:** gerar um APK release com `debuggable=false`, assinado pela mesma chave fixa, e confirmar novamente package, V2, signer, cert e presença/hash da `libksud.so`. Como o cert continua o mesmo, isso não exige trocar a identidade esperada no C0.3; não use um APK recompilado com outro certificado.

## Lista de caça do contrato

1. **BOOTLOOP:** nenhum gatilho novo identificado no delta C0.1→C0.3. O C0.3 define `KSU_VERSION=32673`, `KSU_EXPECTED_SIZE=818` e `KSU_EXPECTED_HASH=efb8…`; `CONFIG_KSU=y` e `CONFIG_KSU_DISABLE_MANAGER` está desligado (`scratch/grok/c01-build/src/common/drivers/kernelsu/Kbuild:121,128-146`; `.config:6700,6702`; `phase4/C03_stablekey_result.md:42-49`). O artefato medido é o SHA previsto, 67.108.864 bytes, `ANDROID!`, header v4 e vermagic esperado (`:76-91`). O C0.1 já bootou no alvo com o mesmo modo de integração/repack, conforme o contrato e `research/MASTER_PLAN.md:8`; o C0.3 não muda o caminho de hooks/configuração, só os dados de versão/cert. O footer `NONE` também já estava no C0.1 (`phase4/C01_device_plan.md:40-44,142-144`). Isso é evidência estática e histórica, não prova de boot do C0.3.
2. **ROOT NÃO CONCEDIDO:** package, versão de manager, cert V2 de signer único, cert esperado do kernel, UAPI 5 e `CONFIG_KSU_DISABLE_MANAGER` desligado batem. Não falta um `ksud` avulso: `SuperRoot-manager-release.apk` embute `libksud.so`; `MainActivity.onCreate()` chama `install()` quando `Natives.isManager` e as UAPIs coincidem (`MainActivity.kt:122-256`); `KsuCli.install()` executa o `libksud.so` embutido (`KsuCli.kt:39-41,132-137`), e `userspace/ksud/src/utils.rs:232-276` copia o executável para `/data/adb/ksud`, instala assets e cria o link `/data/adb/ksu/bin/ksud`. Fluxo: flash do C0.3 → `adb install` do APK release → abrir o manager uma vez (isso dispara a instalação se ele foi coroado e UAPI=5) → verificar pela rota KSU específica. `adb push ksud` separado é desnecessário. O passo que falta no plano é tornar o critério final específico ao KSU, achado 3.
3. **MAGISK HERDADO:** nenhum conflito de boot novo identificado; o C0.1 integrou/bootou com Magisk herdado, segundo o estado do projeto. O ponto concreto é a ambiguidade de shell do achado 3, não evidência de que Magisk impeça a coroação do C0.3.
4. **FLASH/ROLLBACK:** o bloco final do `MASTER_PLAN.md:61-70` manda C0.3, não C0.2; o arquivo operacional de hashes e o `C01_device_plan.md` continuam em C0.1 (achado 2). O guard restringe escrita a `boot_b`, tamanho, magic, hash e partição corretos e exige `current-slot=b`/fastboot bootloader (`fastboot_guard.sh:83-105,121-145`). O rollback após B não inicializável ainda está incompleto (achado 1).
5. **ESTADO SEM BOOT/ROLLBACK:** existe um caminho condicional sem rollback dentro do procedimento atual: B fica unbootable após falha e A já está unbootable; a recuperação por teclas pode chegar ao fastboot, mas não há comando permitido para reativar B. Não há prova de hard brick irreversível; o risco é ficar sem Android e sem um rollback utilizável pelo procedimento aprovado. Ver achado 1.
6. **SEGREDO:** nenhum conteúdo de chave privada nem senha/atribuição de senha apareceu nos relatórios pesquisados. O keystore está fora do repositório, com modo `0400`; `C03_stablekey_result.md:7,15-18` registra que `[fora do repo]` não é repositório Git e que `keys/` está ignorado. O APK contém o certificado público, não o keystore. Nenhum achado de vazamento identificado no escopo inspecionado.

## Verificações de identidade feitas no host

- C0.3 imagem: SHA-256 atual `2977e1dc002d1a9d9ebb9338b60fae3e9df6cc31d50c8adb8be9b2352d6304fe`, bate com o relatório C0.3.
- Manager: SHA-256 atual `c8b66244d91dabac62cd1dc95363e6aa458a203cc8dbbd3bd2635f7b69f73956`; package e cert V2 batem com os macros do kernel.
- APK inclui `libksud.so` de arm64 com SHA `8c600dc9a063e91d45d8ab556d8822e1f768bf274a367de450eeec8b0b19f410`.
- `guard_hashes.txt` atual ainda tem o hash C0.1, não C0.3.

**Nenhum comando adb/fastboot, build, install ou teste foi executado.**
