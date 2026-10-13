# SuperRoot — decisão do core (Fase 1)

**Recomendação: KernelSU como core único — confiança média.** O POCO C75 4G usa um GKI 6.6.89 custom cujo build local já tem as opções e os símbolos Kprobe exigidos pelo caminho atual do KernelSU. Ter a árvore e o build reproduzível remove a principal vantagem de APatch (dispensar a source tree) e permite controlar a integração e o ABI do core. KernelSU também oferece um caminho LKM para GKI que preserva o kernel atual e tem suporte mais claro no upstream para ZygiskNext. APatch continua sendo a referência mais completa para hooks de kernel e KPM; se esses módulos forem a prioridade principal do produto, esse peso pode inverter a escolha.

## Escopo e método

- Análise somente leitura. Não compilei, não alterei o kernel nem interagi com o aparelho.
- Não consultei arquivos `GROK_*.md` nem `CLAUDE2_*.md`. A seção “O que mudaria” lista evidências que poderiam ser verificadas na matriz de RE, sem usar resultados dela.
- Verifiquei a versão e a configuração compilada em `~/lake-build/src/common` e `~/lake-build/src/out/.../common/`. As observações sobre o build são locais; compatibilidade no aparelho ainda exige validar o módulo e a imagem exata.
- Documentação upstream consultada em 2026-10-10. Onde a documentação e a compatibilidade do aparelho não bastam para concluir, isso está indicado.

## Tabela de trade-off

| Eixo | KernelSU | APatch / KernelPatch | Leitura para este aparelho |
|---|---|---|---|
| **Esforço de integração com a árvore disponível** | **Baixo–médio.** O modo LKM para GKI evita substituir o kernel. A configuração local cumpre `KPROBES`, `EXT4_FS` e `MODULES`; `Module.symvers` contém `register_kprobe` e `unregister_kprobe`. O DDK deve ser alinhado ao KMI/build exatos. **[Alta para os dados locais; média para o módulo ainda não compilado]** | **Baixo para a primeira aplicação se `kptools` aceitar a imagem; médio para integrar o SuperRoot.** A source tree não é requisito: APatch injeta KernelPatch em `boot.img`. A configuração local atende `KALLSYMS` e `KALLSYMS_ALL`, mas o patch da imagem C75 não foi validado. **[Alta para requisitos; média para resultado no C75]** | A árvore favorece KernelSU porque torna possível construir e manter um core compatível sem depender de descoberta de símbolos a partir de uma imagem opaca. |
| **Superfície de adaptadores para módulos estrangeiros** | **Boa para módulos Magisk/KSU; limitada para KPM.** Compartilha formato ZIP e várias convenções Magisk, com diferenças conhecidas de montagem e instalação. Não fornece runtime KPM. **[Alta]** | **Mais ampla para kernel hooks:** APM cobre módulos do tipo Magisk e KPM oferece hooks em kernelspace. Porém a instalação persistente de KPM ainda não está implementada upstream: há Embed no kernel/boot ou Load até o próximo reboot. **[Alta]** | Para os módulos Magisk/KSU comuns, KernelSU é um ponto de partida mais direto. Para portar código KPM, APatch expõe mais primitivas, mas ainda será preciso criar a semântica de instalação/lifecycle do SuperRoot. Como o requisito é adaptar/reimplementar, nenhum lado implica compatibilidade binária automática. |
| **Risco de brick/boot** | **Baixo–médio no modo LKM; médio se substituir o kernel.** O LKM preserva o kernel original, mas ainda altera a imagem de ramdisk e precisa do par boot/init_boot correto. **[Média]** | **Médio.** Altera o próprio kernel dentro de `boot.img`; depende de resolução de símbolos/relocações e do formato da imagem exata. APatch declara suporte a 6.6, com teste limitado a Xiaomi e OnePlus; isso ajuda por POCO ser Xiaomi, mas não valida o C75. **[Média]** | KernelSU LKM tem o rollback mais simples para a Fase 1. Nenhuma rota foi testada neste aparelho nesta análise. |
| **Manutenção** | **Menor para a base escolhida.** KSU tem uma fronteira de módulos Magisk-like documentada, perfis por app e caminho LKM atual. A implementação de KPM seria trabalho próprio. **[Média; avaliação arquitetural]** | **Maior para o SuperRoot se precisar hospedar KPM de forma persistente.** KernelPatch oferece hooks fortes, mas o próprio guia diz que “Install” de KPM não está implementado; seria preciso acompanhar a API KP e implementar a gestão que falta. **[Alta para a lacuna; média para o custo]** | A source tree permite implementar e testar a superfície KernelSU sob um ABI do SuperRoot, em vez de herdar o formato de patch binário do KernelPatch. |
| **Maturidade de zygisk-compat** | **Mais clara hoje.** O README upstream de ZygiskNext especifica versões mínimas de KernelSU e `ksud`. **[Alta]** | **Mais incerta.** A FAQ do APatch descreve ZygiskNext/APMs de terceiros e diz que APatch não garante estabilidade. A FAQ registra compatibilidade de versões de ZygiskNext, mas o README upstream atual lista KernelSU e Magisk como requisitos, não APatch. **[Média; documentação não está totalmente alinhada]** | Vantagem de integração para KernelSU; nenhum dos dois inclui Zygisk no core. |
| **Ocultação e concessões de segurança** | **Política mais explícita por app.** App Profiles controlam privilégios/SELinux do processo `su` e podem desmontar módulos para apps selecionados. Isso não torna o sistema indetectável. **[Alta]** | **Controle de kernel mais flexível.** APatch limita quais apps veem/obtêm `su`; KernelPatch pode conceder root no contexto da thread por hook. SuperKey/SuperCall adiciona um segredo de autoridade elevada. Isso também não garante invisibilidade. **[Alta para o modelo documentado]** | Ambos deixam sinais de modificação do boot/kernel e exigem que SuperRoot trate política, daemon e isolamento de forma própria. |

## Evidência técnica

### 1. KernelSU no GKI 6.6.89 local

**O que o upstream exige.** O Kconfig atual do KernelSU define `CONFIG_KSU` como tristate, com dependências `KPROBES && EXT4_FS`; permite compilar `kernelsu` como módulo. A configuração de build encontrada no caminho local `~/lake-build/src/out/bazel/output_user_root/b816944b4a8db05c78f8f829a3c594da/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64_config/out_dir/.config` contém `CONFIG_KPROBES=y`, `CONFIG_HAVE_KPROBES=y`, `CONFIG_KPROBE_EVENTS=y`, `CONFIG_MODULES=y`, `CONFIG_EXT4_FS=y`, `CONFIG_OVERLAY_FS=y`, `CONFIG_KALLSYMS=y` e `CONFIG_KALLSYMS_ALL=y`. A versão em `~/lake-build/src/common/Makefile` é 6.6.89. **[Alta; configuração do build local, não uma leitura direta do aparelho]**

- `CONFIG_KPROBE_EVENTS` está habilitado, mas não é uma dependência declarada no Kconfig atual do KernelSU; as dependências declaradas são `KPROBES` e `EXT4_FS`.
- A `Module.symvers` desse build exporta `register_kprobe` e `unregister_kprobe` como `EXPORT_SYMBOL_GPL`. Isso confirma a presença dos símbolos básicos para o caminho Kprobe do LKM no ABI gerado. **[Alta]**
- Isso **não** é prova completa de compatibilidade KMI do módulo KernelSU: não comparei a lista de indefinidos de um `.ko` construído pelo DDK com a ABI final do kernel, nem confirmei que o artefato local corresponde bit a bit ao kernel que está no telefone. O ponto verificável é que as opções e exports-base estão presentes. **[Alta sobre o limite]**

**Caminho de integração e hooks.** Para aparelhos GKI, o KernelSU documenta o modo LKM carregado no kernel existente, sem substituí-lo. O guia atual recomenda LKM para telefone; a documentação de build antiga avisa que, desde KernelSU 3.0, o modo de imagem GKI deixou de ser oficialmente mantido e aponta o DDK para construir LKM. O código atual registra, entre outros, um kprobe para `input_event` em `kernel/runtime/ksud_integration.c`. **[Alta]**

Há exemplos de chamadas manuais inseridas em `fs/exec.c` (por exemplo, `do_execveat_common` → `ksu_handle_execveat`), mas o guia que os documenta é o de integração non-GKI com KernelSU v0.9.5 e diz que KernelSU 1.0+ não suporta esse caminho non-GKI. Portanto, não trataria esse exemplo legado como a rota suportada para o GKI 6.6 atual. Se o SuperRoot decidir embutir hooks no source próprio, isso será uma integração controlada pelo projeto, não evidência de que o fluxo manual antigo seja a API upstream atual. **[Alta]**

**KMI.** O kernel local é o branch de build `android15-6.6`, versão base 6.6.89. O KMI do módulo deve corresponder ao kernel de destino; a árvore e `Module.symvers` locais oferecem a base para compilar e conferir isso. Confirmo os exports Kprobe acima, mas deixo o KMI completo como **não verificado** até existir um build do módulo e uma comparação de ABI. **[Alta]**

### 2. APatch / KernelPatch no mesmo kernel

APatch aplica KernelPatch ao kernel extraído de `boot.img`, usando `kpimg`/`kptools`; não exige compilar a árvore. Os requisitos oficiais são ARM64 e `CONFIG_KALLSYMS=y`; `CONFIG_KALLSYMS_ALL=y` é recomendado, com suporte inicial documentado quando fica `n`. APatch cobre kernels Android 3.18–6.12, mas a nota específica de 6.6 diz que os testes foram em Xiaomi e OnePlus. O build local tem `KALLSYMS` e `KALLSYMS_ALL` habilitados e o POCO pertence à família Xiaomi; **o C75 4G e sua imagem concreta continuam sem teste**. **[Alta para docs/config; média para aplicabilidade no aparelho]**

KPM executa código no kernelspace e oferece hooks inline e da tabela de syscalls, expondo funções KernelPatch como `kallsyms_lookup_name` e `hook_wrap`. O guia atual do APatch permite Embed no kernel/boot ou Load imediato; declara explicitamente que a opção Install de KPM ainda não foi implementada. Isso é relevante para SuperRoot: há uma API de baixo nível útil para portar comportamentos, mas falta o lifecycle persistente que o produto teria de fornecer. **[Alta]**

APatch adiciona também SuperCall, uma syscall autenticada por SuperKey. O próprio projeto alerta que a chave tem privilégios acima de root. É uma concessão de segurança e gestão de segredo que precisaria ser refletida no modelo de permissões do SuperRoot. **[Alta]**

### 3. Superfície para adaptar módulos estrangeiros

KernelSU documenta o formato ZIP e várias convenções Magisk compartilhadas: `post-fs-data.sh`, `service.sh`, `system.prop` e `sepolicy.rule`. As diferenças concretas incluem instalação em recovery, montagem via metamodule e substituição de arquivos (`.replace` não funciona da mesma maneira). É uma base clara para um adaptador seletivo de módulos Magisk/KSU, mas não significa que todos os instaladores e módulos funcionem sem tradução. **[Alta]**

APatch oferece APM para módulos do tipo Magisk e KPM para código de kernel. Sua FAQ confirma compatibilidade de WebUI com módulos KernelSU, mas isso não prova compatibilidade integral de seus scripts, instaladores ou efeitos. Para ambos, SuperRoot deve traduzir manifestos, hooks, política e ciclo de vida para uma API própria; para KPM, APatch fornece a referência mais rica de hooks, mas a gestão persistente ainda precisa ser construída. **[Alta para limites documentados; média para a comparação]**

A escolha por KernelSU prioriza uma base LKM ligada ao KMI que temos, o formato de módulos Magisk/KSU mais diretamente adaptável, a maturidade upstream de ZygiskNext e uma rota de rollback que não substitui o kernel. O custo assumido é implementar uma interface de kernel do SuperRoot inspirada no que precisamos dos KPMs. Se a RE mostrar que os módulos KPM-alvo dependem de hooks que não são implementáveis com custo razoável no nosso source, APatch passa a ser o melhor candidato.

### 4. Concessões que permanecem

- **`su` e daemon:** KernelSU mantém `ksud` e o Manager; APatch mantém AndroidPatch/`apd` e SuperCall/SuperKey. SuperRoot ainda precisa definir autorização, IPC, recuperação do serviço e persistência própria.
- **SELinux:** KernelSU permite configurar contexto/regras do processo `su` por App Profile e instalar `sepolicy.rule`. APatch/KernelPatch documenta root no contexto da thread via hook sem trocar esse contexto, além de suporte por `magiskpolicy`. Nenhuma abordagem elimina o trabalho de política para módulos e serviços.
- **Ocultação:** KernelSU pode desmontar módulos para UIDs/apps selecionados; APatch controla quais apps recebem acesso/visibilidade de `su` e oferece hooks de kernel. São políticas de isolamento e redução de superfície observável, não uma garantia geral de invisibilidade ou de aprovação por verificadores externos.

## O que mudaria minha recomendação

Não consultei a matriz do Grok. Estes são os dados dela que poderiam mudar a decisão:

1. **Inventário dos KPMs-alvo:** se a RE confirmar que os módulos essenciais dependem de hooks KernelPatch/KPM difíceis de portar para hooks definidos pelo SuperRoot, recomendaria APatch. Se forem comportamentos pequenos e reproduzíveis a partir de source/API documentada, KernelSU continua preferível.
2. **Compatibilidade do KPM no produto:** se a matriz demonstrar que Embed/Load cobre o lifecycle exigido sem a função Install persistente, isso reduziria o principal custo de APatch. Se a instalação/atualização em runtime for requisito, essa lacuna pesa contra APatch.
3. **Validação do alvo:** um resultado reproduzível de `kptools` no kernel exato 6.6.89 do C75, com resolução de símbolos/relocações e boot test validado, reduziria substancialmente o risco de APatch. Falha de símbolo ou incompatibilidade no kernel exato reforçaria KernelSU.
4. **KMI do KernelSU:** se um build de KSU LKM contra esta árvore falhar por símbolos/CRC fora do KMI ou por incompatibilidade com o kernel rodando, APatch ganha. Se os símbolos necessários forem todos resolvidos pelo ABI local, confirma-se KernelSU.
5. **Zygisk e módulos Magisk realmente necessários:** se os módulos prioritários exigirem integração que a versão atual de ZygiskNext não suporte com KernelSU, mas o APatch mantenha uma implementação suportada e estável, reavaliar a maturidade de compatibilidade pode inverter a escolha.

## Fontes

**Árvore e build locais:**

- `~/lake-build/src/common/Makefile:2-4` — `VERSION=6`, `PATCHLEVEL=6`, `SUBLEVEL=89`.
- `~/lake-build/src/out/bazel/output_user_root/b816944b4a8db05c78f8f829a3c594da/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64_config/out_dir/.config:261-263,681,688,810,6708,6765,7686` — configuração citada acima.
- `~/lake-build/src/out/bazel/output_user_root/b816944b4a8db05c78f8f829a3c594da/execroot/_main/bazel-out/k8-fastbuild/bin/common/kernel_aarch64/Module.symvers:1123-1124` — exports de `register_kprobe` e `unregister_kprobe`.

**Documentação upstream:**

- [KernelSU: instalação, KMI e modos GKI/LKM](https://github.com/tiann/KernelSU/blob/main/website/docs/guide/installation.md)
- [KernelSU: configuração Kconfig](https://github.com/tiann/KernelSU/blob/main/kernel/Kconfig)
- [KernelSU: build e nota sobre KernelSU 3.0/DDK](https://github.com/tiann/KernelSU/blob/main/website/docs/guide/how-to-build.md)
- [KernelSU: compatibilidade e diferenças de módulos Magisk](https://github.com/tiann/KernelSU/blob/main/website/docs/guide/difference-with-magisk.md)
- [KernelSU: hooks runtime (`ksud_integration.c`)](https://github.com/tiann/KernelSU/blob/main/kernel/runtime/ksud_integration.c)
- [KernelSU: integração manual legado](https://github.com/tiann/KernelSU/blob/main/website/docs/guide/how-to-integrate-for-non-gki.md)
- [KernelSU: App Profiles e desmontagem de módulos por app](https://github.com/tiann/KernelSU/blob/main/website/docs/guide/app-profile.md)
- [APatch: requisitos e instalação](https://apatch.dev/install.html)
- [APatch: FAQ, SuperKey, SELinux e Zygisk](https://apatch.dev/faq.html)
- [APatch: uso e limitações de KPM](https://apatch.dev/kpm-usage-guide)
- [APatch: repositório e suporte de versões/configuração](https://github.com/bmax121/APatch)
- [KernelPatch: guia de API KPM](https://github.com/bmax121/KernelPatch/blob/main/doc/zh-CN/module.md)
- [ZygiskNext: requisitos upstream atuais](https://github.com/LSPosed/ZygiskNext/blob/main/README.md)
