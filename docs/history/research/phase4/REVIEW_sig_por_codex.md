# Ultrareview — autorização do manager KernelSU

Data: 2026-10-11. Source: KernelSU `6e3860443f8fea7c5fc226277e53888033796488`, em `kernel/upstream/KernelSU`. Escopo read-only; sem instalação ou device.

## Veredito

**Assinatura + `KSU_VERSION=32673` não são, sozinhos, a regra geral de autorização.** O KernelSU também exige que o package name encontrado no Android seja exatamente o `KSU_PACKAGE_NAME` compilado no kernel. Para o APK debug medido e o C0.1 descrito em `MANAGER_build.md`, esse requisito já casa: ambos anunciam `me.weishu.kernelsu`. Portanto, se C0.2 mantiver esse nome, o manager não estiver desabilitado e a UAPI continuar compatível, trocar `KSU_EXPECTED_SIZE/HASH` para o certificado desse APK é a mudança funcional que falta para reconhecê-lo. Corrigir a versão `16` para `32673` é correto para identificação/telemetria, mas **não é uma comparação de autorização**.

Ainda falta provar em boot que o tracker coroa o UID do APK e que manager/`ksud` operam com a UAPI do kernel. Nada foi instalado ou testado no device nesta revisão.

## Achados

1. **A identidade do manager tem duas condições no caminho real.** `track_throne()` lê `/data/system/packages.list`, extraindo pacote e UID (`kernel/manager/throne_tracker.c:231-289`). O scanner procura APKs em `/data/app`; `maybe_manager_apk_dir()` extrai o package name do caminho e exige igualdade com `KSU_PACKAGE_NAME` (`kernel/manager/throne_tracker.c:74-85`; extração em `kernel/manager/apk_sign.c:300-324`). Após a checagem da assinatura passar, `crown_manager()` encontra a entrada daquele nome em `packages.list` e registra seu UID como manager (`throne_tracker.c:35-47,196-206,298-346`). Nome errado impede a checagem/coroação mesmo com certificado correto. **Confiança alta.**

2. **`EXPECTED_SIZE` e `EXPECTED_HASH` são o filtro de certificado V2, não a identidade inteira.** `is_manager_apk()` encaminha os dois macros para `check_v2_signature()` (`kernel/manager/apk_sign.c:326-329`). O parser lê o primeiro signer/certificado V2, compara o tamanho DER e o SHA-256 do certificado (`apk_sign.c:97-142`). O scanner só coroa o UID depois de `is_manager_apk()` retornar verdadeiro (`throne_tracker.c:196-206`). O código não compara `KSU_VERSION` nesse fluxo. **Confiança alta.**

3. **O parser tem requisitos de esquema e de contagem.** Ele aceita o par V2 `0x7109871a` e padding; um ID desconhecido causa rejeição, e exige exatamente um par V2 (`apk_sign.c:230-265`). `check_block()` percorre o primeiro signer e o primeiro certificado, sem iterar todos os signers (`apk_sign.c:100-112`). O APK medido passa esse formato: `apksigner verify --verbose --print-certs` deu V2=true, V1/V3/V3.1/V3.2/V4=false, um signer e SHA-256 `b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f`. O DER medido tem 744 bytes (`MANAGER_build.md:51-56`). Para builds futuros, manter a assinatura V2 compatível e um signer conhecido; uma assinatura V3 adicional pode ser rejeitada por este parser. **Confiança alta no source e no APK inspecionado.**

4. **O nome atual bate; `.debug` só quebra se alterar o `applicationId`.** O Kbuild usa `me.weishu.kernelsu` quando `KSU_IS_PR_BUILD=0`, e `.pr` quando é 1; os macros são compilados no KSU (`kernel/Kbuild:132-149`). O Gradle escolhe o `applicationId` por `IS_PR_BUILD` ou pelo override `KSU_PACKAGE_NAME` (`manager/app/build.gradle.kts:25-29,144-151`); o bloco `debug` não declara `applicationIdSuffix` (`build.gradle.kts:65-73`). Nesta revisão, `aapt dump badging` confirmou `package: name='me.weishu.kernelsu'`, `versionCode='32673'` e `application-debuggable`. O log C0.1 registrado também contém `package name me.weishu.kernelsu` (`MANAGER_build.md:26,48`). Se o APK receber `applicationIdSuffix ".debug"`, `IS_PR_BUILD=true` ou override de nome, configure o kernel com exatamente o mesmo ID. Também confirme `CONFIG_KSU_DISABLE_MANAGER` desligado: quando ativo, `is_manager()` só reconhece UID 0 e o tracking normal não dá identidade especial ao APK (`kernel/Kconfig:21-27`; `manager_identity.h:10-38`). **Confiança alta para o APK atual; configuração final de C0.2 pendente.**

5. **`32673` e UAPI `5` são coisas diferentes.** O Kbuild deriva `KSU_VERSION` de `30000 + git commit count`, mas usa `16` se não encontra o Git (`kernel/Kbuild:117-127`). O manager versionCode também é `30000 + git commit count` (`manager/build.gradle.kts:18-35`), daí `32673` no APK deste pin (`MANAGER_build.md:24,48`). O kernel devolve a versão de release e `KERNEL_SU_UAPI_VERSION` em campos distintos (`kernel/supercall/dispatch.c:40-62`); neste pin a UAPI é `5` (`uapi/supercall.h:10-13`). O manager decide compatibilidade comparando as UAPIs (`HomeViewModel.kt:65-68`) e só chama `install()` quando é manager e as UAPIs são iguais (`MainActivity.kt:131-132`; `Natives.kt:123-131`). `ksud` também falha se `get_info().uapi_version != uapi_version()` (`userspace/ksud/src/ksucalls.rs:202-211`). Assim, fallback de release `16` não impede por si só a coroação; divergência de UAPI impede recursos/integração do userspace. Para o APK e kernel compilados deste mesmo pin, a constante esperada é UAPI 5. **Confiança alta no comportamento do source; valor realmente reportado pelo C0.1 não foi consultado no device nesta revisão.**

6. **Uma vez coroado, o UID do manager tem caminho especial para `su`; os demais apps dependem da allowlist.** `allowed_for_su()` permite o manager ou um UID autorizado pela política (`kernel/supercall/perm.c:27-30`); a ação de grant usa essa autorização antes de elevar a credencial (`kernel/supercall/dispatch.c:25-37`). Logo, “APK reconhecido como manager” não significa que todo outro APK ganhou root: cada app precisa do perfil/grant apropriado. **Confiança alta.**

7. **O certificado debug não é necessariamente diferente a cada rebuild.** Enquanto o mesmo debug keystore for mantido, builds usam o mesmo certificado; outro host, remoção ou regeneração do keystore pode trocar o DER e, portanto, tamanho/hash. O par atual `744 + b830cb…` só é válido para o certificado medido (`MANAGER_build.md:43,51-56`). Para produção, prefira uma chave de assinatura fixa e controlada, fora do repositório; atualize o par do kernel sempre que a chave mudar. A instalação/atualização Android também precisa aceitar a assinatura do APK existente para esse package name; o estado instalado no aparelho não foi verificado. **Confiança média na estabilidade operacional do debug keystore; alta no risco de incompatibilidade de certificado.**

## Condições para considerar C0.2 pronto para o manager

- No vmlinux final, confirmar `KSU_VERSION=32673`, `KSU_EXPECTED_SIZE=744`, `KSU_EXPECTED_HASH=b830cb5194e9bdb0f2a7c2cb295cd6e4123aaecf9b262f5adac7d90ce1d0620f`, `KSU_PACKAGE_NAME=me.weishu.kernelsu`, `KSU_IS_PR_BUILD=0` e `CONFIG_KSU_DISABLE_MANAGER` ausente/desligado.
- Confirmar no APK exato o `applicationId`, V2 signer e certificado; os números usados no kernel têm de vir desse mesmo APK.
- Confirmar kernel e manager/`ksud` com UAPI `5`; versão code `32673` não substitui esse gate.
- Após instalação/boot autorizados, exigir log de coroação do UID, manager reportando `isManager`, UAPI compatível e um fluxo `su` do manager. Para apps comuns, verificar também o grant/perfil na allowlist.

Nenhum desses passos de build ou device foi executado nesta ultrareview. O resultado é uma revisão estática do pin e uma inspeção read-only do APK já produzido.

## Resumo de confiança

| Afirmação | Confiança |
|---|---|
| O KSU exige package name compatível e certificado esperado para coroar o manager | Alta |
| APK debug atual é `me.weishu.kernelsu`, V2 e um signer | Alta; `aapt`/`apksigner` inspecionados |
| Release version `16` vs `32673` não é a comparação de autorização | Alta; o gate de compatibilidade é UAPI |
| UAPI do kernel executando agora é 5 | Não verificado no device; source pin declara 5 |
| C0.2 vai coroar o APK e conceder root após os macros serem corrigidos | Não verificado; depende do build, config e boot |
