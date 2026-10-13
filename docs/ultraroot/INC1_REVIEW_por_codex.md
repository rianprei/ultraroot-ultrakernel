# UltraRoot Manager inc1 — revisão Codex

**Escopo:** revisão estática e read-only do incremento em `[artefato local, fora do repo]/grok/ultraroot-mgr/KernelSU`, sem build, testes, execução do app ou device. O checkout tinha alterações não commitadas de inc1 antes desta revisão; nenhum arquivo de implementação foi alterado.

## Verificações

1. **applicationId intacto: sim.** `manager/app/build.gradle.kts:25-29,149` continua derivando o pacote do `KSU_PACKAGE_NAME` ou do default `me.weishu.kernelsu` (exceto PR build). O diff de inc1 não altera esse arquivo nem define `KSU_PACKAGE_NAME`; `namespace` permanece `me.weishu.kernelsu` em `:64`. O manifesto referencia o `${applicationId}` para `taskAffinity` e `FileProvider` (`AndroidManifest.xml:72,77`). A configuração `apksign` continua independente, com propriedades de keystore/alias (`build.gradle.kts:31-35`), sem mudança no diff.

2. **KsuBackend só lê: confirmado.** `core/backend/KsuBackend.kt:12-17` constrói `KsuCapability` lendo somente `Natives.version`, `Natives.kernelUAPIVersion` e `Natives.managerUAPIVersion`. Não chama `KsuCli`, setters nem métodos de escrita. No JNI, esses acessores chegam a `get_version()`/`get_kernel_uapi_version()`/`get_manager_uapi_version()` (`manager/app/src/main/cpp/jni.cc:16-35`); os dois primeiros consultam `KSU_IOCTL_GET_INFO` e fallback `GET_INFO_LEGACY` (`ksu.cc:80-102`). Não encontrei operação de mutação no caminho novo.

3. **K&P usa dado real; ausência de KSU no Android não deve crashar.** `KernelPerformanceScreen.kt:27-45` carrega o resultado de `KsuBackend.readCapability()` e exibe os três campos recebidos, sem valores mock/hardcoded. Sem driver KSU, a busca de fd retorna `-1` (`ksu.cc:23-64`), `ioctl` falha normalmente (`:66-75`), o caminho de info/fallback conserva valores sentinela e o `prctl` legado inicializa versão em `-1` (`ksu.h:58-64`). Portanto, para APK rodando em Android com ABI e biblioteca suportados, a falta do driver produz valores sentinela/zero, não uma exceção Kotlin. Não há estado visual específico de “KSU indisponível”; a tela pode mostrar `-1`/`0`.

4. **Branding e identidade do APK.** `manager/gradle.properties:10` define `KSU_NAME=UltraRoot`; `build.gradle.kts:152` usa o valor como `app_name`, logo exibido no manifesto (`AndroidManifest.xml:22`). A identidade continua controlada por `managerPackageName` e `applicationId`, separada de `managerName`. O nome base do artefato APK também usa `managerName` (`build.gradle.kts:184-187`), efeito apenas de nome de arquivo. Não encontrei alteração em package ID, certificado ou configuração de assinatura.

## Achado

- **Médio — suporte a JVM host sem JNI não está protegido.** `Natives` carrega `libkernelsu` no inicializador (`manager/app/src/main/java/me/weishu/kernelsu/Natives.kt:34-35`). A nova chamada na tela (`KernelPerformanceScreen.kt:28-30`) não captura `UnsatisfiedLinkError` nem oferece backend/fallback substituível. Assim, a ausência do *kernel KSU* em um Android suportado é tolerada pelo código C nativo, mas executar esse caminho numa JVM host sem `libkernelsu` encerra a coroutine com erro não tratado e pode derrubar o teste/host. Se “host/sem device” inclui JVM host, é necessário tratar esse caso ou não invocar o JNI nesse ambiente. Não executei teste para reproduzir; a conclusão vem do fluxo estático acima.

## Veredito

**CORRIGIR** para cumprir a condição ampla de “host sem device”: proteger a chamada JNI ou representar a indisponibilidade do backend no host. Para Android sem KSU, os getters existentes falham de forma degradada e não indicam crash. `applicationId` intacto: **sim**. O backend novo é read-only. A branding não muda package/signature; também muda somente o basename do artefato de build.

```text
STATUS: DONE
VEREDITO: CORRIGIR
APPID_INTACTO: sim
ACHADOS: 1
```
