# UltraRoot — app próprio, incremento 1

Projeto novo em `ultraroot/`. Não é fork nem reskin do manager KernelSU. Não foi instalado.

## Arquitetura

| Módulo | Papel |
|---|---|
| `:app` | UI. Namespace `dev.superroot.ultraroot`. Compose, Material 3, gaveta com as 6 seções. |
| `:core-backend` | API do engine. Namespace `dev.superroot.core`. `KsuBackend` + `libultraroot.so`. Sem Compose. |

`Kernel & Performance` chama `KsuBackend.readCapability()` (ioctl `GET_INFO`: versão, UAPI do kernel, UAPI 5 deste binário). Sem a lib, a tela recebe `backend indisponível` e não estoura. As outras cinco seções são nossas e dizem que este incremento não lê o aparelho. Não há número falso.

A allowlist e o perfil também estão na API (`readAllowList`, `readProfile`). Ioctl de perfil com falha devolve null e vira `perfil indisponível`, não `allowSu=false`. A tela dessas seções ainda não lista isso.

## Tema

Material 3 `darkColorScheme`, base preta. Sem dynamic color neste incremento.

| Token | Cor |
|---|---|
| background | `#0B0C0E` |
| surface | `#14161A` |
| primary | `#D6B25E` |
| secondary | `#8AA4B5` |
| texto | `#E8E6E3` |

Navegação: `ModalNavigationDrawer` + `NavigationDrawerItem`. Seis destinos não cabem numa barra inferior do Material 3 (o guia pede 3 a 5). Marca: losango em `res/drawable/ic_mark.xml`.

## O que é nosso e o que é reusado

Nosso: módulos Gradle, tema, gaveta, telas, `engine.cc`, `KsuBackend`, `EngineNatives`.

Reusado, só o contrato do engine: cabeçalhos UAPI copiados de `KernelSU/uapi/` (`supercall.h`, `app_profile.h` e os includes deles). O `.so` usa o fd `[ksu_driver]` que já está aberto no processo (`engine.cc:22-55` só procura o fd; não o instala) e fala os ioctls `GET_INFO`, `NEW_GET_ALLOW_LIST` e `GET_APP_PROFILE`. Nenhuma tela, ícone, string ou tema do manager KSU entrou neste projeto.

## Decisão de package

Opção (a). `applicationId` = `me.weishu.kernelsu`. O namespace do código continua `dev.superroot.ultraroot`.

O C0.3 não autoriza “pelo cert” sozinho. Em `drivers/kernelsu/Kbuild` do build C0.3: pacote `me.weishu.kernelsu`, `EXPECTED_SIZE` 818, `EXPECTED_HASH` `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9`, comentário “v2 only”. `throne_tracker.c` faz `strcmp` com `KSU_PACKAGE_NAME` em `packages.list` e só então `is_manager_apk` lê o bloco V2 (`apk_sign.c`, id `0x7109871a`). Outro id no signing block, inclusive V3, invalida a coroa.

`dev.superroot.ultraroot` fica para uma decisão do dono. Trocar o pacote exige mudar `KSU_PACKAGE_NAME` e reflashear o kernel. Não basta reassinatura.

O APK deste incremento está assinado só com V2, um signer, cert DER de 818 bytes, SHA-256 igual ao `EXPECTED_HASH`. versionCode 1, versionName `0.1.0-inc1`, rótulo UltraRoot. ABI nativa: `arm64-v8a` apenas.

## Verificação neste host

`KsuBackendHostTest` 3/3, failures=0: capability sem lib, allowlist sem lib, perfil null vira `perfil indisponível` e `allowSu=false` lido continua confirmado.

APK: `ultraroot/out/UltraRoot-own-inc1.apk`  
sha256 `58cb70b93a4c4f8367b499de0d856bf00c68d1f56cefcb5a00d2d63e620340f4`

`libultraroot.so` exporta `kernelVersion`, `kernelUapi`, `managerUapi`, `allowListUids`, `readProfile`. O strip do Gradle não rodou; `libc++_shared.so` foi no APK sem strip (~9 MB). O ioctl não foi executado no aparelho.
