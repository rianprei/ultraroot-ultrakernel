# Review do inc1-fix + inc2 — UltraRoot Manager

**Veredito: CORRIGIR — 1 achado.** Revisão read-only do snapshot `[artefato local, fora do repo]/grok/ultraroot-mgr/`. Não compilei, não rodei testes e não toquei em device.

## Achado

1. **Perfil de uma UID da allowlist pode ser mostrado como falso/zerado quando `GET_APP_PROFILE` falha.** Em `KernelSU/manager/app/src/main/java/me/weishu/kernelsu/core/backend/KsuBackend.kt:43-55`, cada UID recebida da allowlist é enriquecida com `Natives.getAppProfile("", uid)` e convertida em `AllowGrant`, sem validar se a leitura do perfil falhou. `KernelSU/manager/app/src/main/cpp/ksu.cc:165-169` preserva o retorno do ioctl `KSU_IOCTL_GET_APP_PROFILE`, mas `KernelSU/manager/app/src/main/cpp/jni.cc:203,229-239` transforma qualquer retorno diferente de zero em um objeto de perfil padrão (`allowSu=false`, demais campos nos defaults). O kernel confirma que essa chamada pode falhar (`-EOPNOTSUPP`, `-EFAULT` ou `-ENOENT`) em `KernelSU/kernel/supercall/dispatch.c:342-370`. Como a tela mostra esses campos como estado lido em `KernelSU/manager/app/src/main/java/me/weishu/kernelsu/ui/screen/rootaccess/RootAccessScreen.kt:42-58`, uma falha ou corrida entre a leitura da lista e a leitura individual pode aparecer como “sem root”/perfil zerado em vez de indisponível ou lista desatualizada.

   A conversão para perfil padrão já existia no JNI, mas o inc2 passa a consumi-la para apresentar o estado da allowlist. Recomendo expor o erro da leitura individual até `KsuBackend` e renderizar indisponível/atualizar a lista, sem apresentá-lo como um perfil confirmado. É falha de integridade da informação exibida; o caminho revisado não grava perfil nem concede/revoga root.

## Verificações solicitadas

- **Wrapper JNI — confirmado no código.** `KsuBackend.kt:59-80` trata `UnsatisfiedLinkError`, `ExceptionInInitializerError` cujo `cause` seja `UnsatisfiedLinkError`, e `NoClassDefFoundError` associado a `Natives`/`kernelsu`, retornando `KsuRead.Unavailable`. Outros erros de inicialização não relacionados à biblioteca são repropagados. `KsuBackendHostTest.kt:7-21` é um teste JUnit real: chama os métodos de produção e confere o estado e a mensagem de indisponibilidade; não há mock nem assert sempre verdadeiro. Não executei o teste por causa do escopo read-only. Ele cobre a condição host sem biblioteca, mas não injeta/asserta separadamente cada classe de erro — em particular, não prova sozinho o ramo `ExceptionInInitializerError`.
- **Allowlist — caminho real e somente leitura.** `KsuBackend.kt:37-42` chama `Natives.getAllowListUids()`. A declaração é nativa em `Natives.kt:108`; `jni.cc:55-72` chama `get_allow_list`, e `ksu.cc:104-106` implementa essa chamada com `KSU_IOCTL_NEW_GET_ALLOW_LIST`. Falha do ioctl retorna `null` em `jni.cc:58-60`, convertida em `Unavailable` em `KsuBackend.kt:41`; `RootAccessScreen.kt:39-45` apresenta esse estado sem crash no próprio componente. Nesse caminho não há `setAppProfile`, grant ou revoke. A ressalva sobre a leitura individual do perfil está no achado acima.
- **Host versus app inteiro.** O teste é de backend, não uma execução da tela Compose. A composição principal ainda acessa `Natives.isFullFeatured()` sem wrapper em `KernelSU/manager/app/src/main/java/me/weishu/kernelsu/ui/MainActivity.kt:286`; portanto o teste host não garante que o app inteiro inicia numa JVM sem `libkernelsu`. Isso é preexistente. O APK Android inspecionado inclui bibliotecas para `arm64-v8a`, `riscv64` e `x86_64`; num device suportado sem KernelSU, a biblioteca existe e a falha de ioctl da allowlist segue o caminho `Unavailable` descrito acima. Não validei esse comportamento em device.
- **applicationId e certificado — confirmados no artefato inc2.** `KernelSU/manager/app/build.gradle.kts:25-35,149` mantém o padrão `me.weishu.kernelsu` e a configuração de assinatura por propriedades. A inspeção read-only de `out/UltraRoot-mgr-inc2.apk` confirmou pacote `me.weishu.kernelsu` (`aapt dump badging`) e SHA-256 do certificado `efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9` (`apksigner verify --print-certs`).

## Estado final

- `APPID_CERT_OK`: sim.
- `ACHADOS`: 1 — erro de `GET_APP_PROFILE` pode ser apresentado como perfil padrão/sem root.
- Não encontrei escrita de estado, concessão/revogação de root ou risco de bootloop introduzido pela tela Root Access revisada.
