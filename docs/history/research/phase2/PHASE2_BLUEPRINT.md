# SuperRoot — blueprint do APK único (Fase 2)

Data: 2026-10-10  
Estado: blueprint de implementação; esta entrega é somente leitura.  
Core decidido: KernelSU LKM, um único motor de kernel, confiança média conforme Fase 1.

## Decisão de arquitetura

SuperRoot será um único APK de manager. O APK usa a UI Compose existente, um backend de domínio próprio e um transporte versionado para o daemon do KernelSU. O KernelSU LKM e seu userspace compatível formam o único core de root. Não embarcar nem iniciar MagiskSU, magiskd, KernelPatch/kpimg ou outro core em paralelo.

As features acopladas serão reimplementadas como políticas e extensões do SuperRoot sobre o core KSU. Só #14, #15 e #17 entram pela compatibilidade de contrato indicada como portável na matriz. Esse recorte tem confiança alta na matriz local; a compatibilidade efetiva de um módulo específico continua dependendo de ensaio.

O repositório local [fora do repo] já separa app/core, app/apk e app/apk-legacy. A escolha é manter app/apk como o único frontend Compose e aproveitar app/core como domínio compartilhado. O app/apk-legacy não é publicado como outro manager. O APK distribui o frontend e um conjunto versionado de payloads do core; cada payload declara versão, arquitetura, origem e hash, e deve corresponder ao build de kernel validado. A compatibilidade dessa divisão com o instalador atual ainda precisa de desenho de migração. Confiança média.

## Diagrama de camadas

    ┌───────────────────────────────────────────────────────────────┐
    │ L4 — UI única Compose: Início, Superusuário, Módulos, Logs,  │
    │      Configurações e popup de pedido su                       │
    └──────────────────────────────┬────────────────────────────────┘
                                   │ ações tipadas / estado observado
    ┌──────────────────────────────▼────────────────────────────────┐
    │ L3 — Manager APK + CoreRepository + transporte SRIP/1         │
    │      um único backend privilegiado; sem DB de policy paralelo │
    └──────────────────────────────┬────────────────────────────────┘
                                   │ API negociada por versão/capacidade
    ┌──────────────────────────────▼────────────────────────────────┐
    │ L2 — Compatibilidade de módulos Magisk/KSU                    │
    │      module.prop, estados, webroot, action.sh, metamount.sh   │
    │      runner e provedor de mounts pertencentes ao SuperRoot    │
    └──────────────────────────────┬────────────────────────────────┘
                                   │ operações de core aprovadas
    ┌──────────────────────────────▼────────────────────────────────┐
    │ L1 — Único core: KernelSU-LKM + userspace ksud correspondente │
    │      hooks/UID, autorização root e interface de kernel        │
    └───────────────────────────────────────────────────────────────┘

    L5 — plano transversal de política:
         identidade do manager, concessão su, SELinux, perfis por app,
         isolamento de mounts por UID e ocultação explícita por UID.
         Aplicado pelo daemon e pelo core; nunca é um segundo core.

A configuração e os exports locais citados na decisão da Fase 1 confirmam pré-requisitos-base de Kprobe, módulos e EXT4. Não confirmam o KMI integral do .ko nem a ABI exata do kernel em execução. O diretório common verificado não contém drivers/kernelsu nem Module.symvers na raiz; o gate deve usar o DDK KSU pinado e os artefatos de build exatos da árvore lake-build. Confiança alta para a ausência nesses caminhos e para o limite do KMI.

## Contratos entre camadas

### L1 — KernelSU LKM

- Fixar um commit do KernelSU/DDK e compilar kernelsu.ko para a arquitetura do aparelho contra a configuração, headers, Module.symvers e KMI do build alvo. Não usar kpimg nem substituir o kernel com APatch.
- L1 é responsável por conceder a autoridade root e expor a API que ksud consome. O APK não chama símbolos de kernel diretamente.
- O instalador deve tratar o par [fora do repo] kernel/ramdisk e o .ko como uma unidade versionada. Bloquear a instalação se versão, arquitetura, KMI ou hash não corresponderem.
- Reutilizar o mecanismo LKM do KernelSU; não portar o antigo caminho manual non-GKI como se fosse a API suportada para GKI 6.6.89. Confiança alta na decisão da Fase 1.

### L2 — Compatibilidade de módulos

- Um parser cria um ModuleRecord canônico a partir de module.prop e do diretório em /data/adb/modules. Preservar os estados disable, remove e update e a semântica de atualização/rollback.
- Um único runner, pertencente ao SuperRoot, agenda os hooks compatíveis de boot e os scripts webroot/action.sh. Registrar saída, duração, código de retorno e módulo responsável para diagnóstico.
- Tratar metamount.sh como contrato delegado: o runner chama o script do módulo/metamodule e não presume que o nome do arquivo garante determinado efeito. O provedor de mounts valida o escopo da operação e registra o resultado.
- Oferecer compatibilidade seletiva com o formato Magisk + KSU. Um instalador que dependa de recovery, de APIs Magisk internas, de .replace ou de side effects fora do contrato não passa automaticamente.
- Scripts de módulos são código privilegiado. O produto deve declarar o nível de confiança exigido para instalação; a decisão sobre assinatura/allowlist de ZIP fica com o dono. Não há compatibilidade binária implícita.

### L3 — Manager e IPC

- Uma interface CoreRepository desacopla ViewModels e UI do motor. Operações mínimas: handshake/status; listar e decidir pedidos su; listar/alterar políticas; listar/operar módulos; ler logs; consultar capacidades; solicitar reinício.
- Definir SRIP/1 como protocolo local tipado e versionado, com handshake de versão e capacidades. Não expor shell arbitrário à UI nem copiar o protocolo interno de ioctl de outro core para o app.
- ksud é o único dono privilegiado da policy e do estado de runtime. Implementar o facade SRIP no próprio ksud ou em seu processo substituto único; nunca deixar magiskd e ksud competindo por sockets, banco ou hooks.
- A chamada Activity/ViewModel → serviço Android local usa Binder não exportado. A fronteira privilegiada verifica UID/PID, pacote e certificado de assinatura do Manager. Se a comunicação com ksud usar AF_UNIX, restringir permissões do socket e verificar credenciais do peer; o transporte concreto deve ser fechado após inspeção do ABI KSU pinado.
- O handshake retorna versão do core, versão do protocolo, capabilities e compatibilidade KMI. O Manager esconde controles sem capability e recusa mutações se o handshake ou a identidade assinada falhar.
- Fluxo de pedido su: core/daemon cria pedido com nonce e prazo; Manager mostra o popup; resposta é allow/deny e prazo; daemon valida identidade, nonce, política e eventual SuperKey antes de persistir e conceder. Timeout, dado inválido ou falha IPC resulta em deny. Persistência acontece após validação, evitando o tipo de bypass por gravação antecipada documentado no fork atual.
- A policy canônica fica no daemon. O banco Android pode cachear nomes/ícones e estado para a UI, mas não conceder root. Migrar regras existentes do magisk.db uma vez, com backup, auditoria e conversão conservadora; não manter duas fontes de verdade.
- A Fase 1 não confirmou a superfície IPC externa exata do commit KSU escolhido. O nome SRIP/1 é uma proposta interna do SuperRoot, não uma API upstream afirmada. Confirmar o ponto de extensão antes de implementação. Confiança média.

### L4 — UI única

Manter o app/apk Compose como aplicação única, com o app/core como domínio. Reutilizar as telas existentes de início, superuser, módulos, logs, settings e popup como apresentação; trocar o acesso Magisk por CoreRepository. Manter um único applicationId, certificado de distribuição e rota de upgrade. O app/apk-legacy não entra no artefato entregue.

A tela exibe explicitamente estado do core, handshake/capabilities, estado de cada módulo e resultado das operações. Estado desconhecido não aparece como sucesso. Alterações irreversíveis de boot mostram imagem, slot e hash antes da confirmação. Confiança média, baseada na estrutura local do app.

## Mapa de origem por feature

PORT significa reimplementar a semântica necessária sob o core KSU do SuperRoot, sem copiar o acoplamento de outro motor. ADAPT significa executar uma convenção/CLI estável por contrato. CORE significa parte do motor KSU escolhido. EXCLUIR significa fora do baseline. A confiança qualifica a evidência de origem da matriz, não a compatibilidade futura.

| # | Feature | Melhor origem da matriz | Modo | Decisão e confiança |
|---|---|---|---|---|
| 1 | Tracepoint sys_enter + kretprobe | KSU; acoplada | CORE | Vem somente com o core KSU pinado; não extrair hook isolado. Alta |
| 2 | Troca manual de sys_call_table | fork backslashxx; acoplada | EXCLUIR | A matriz diz não extrair; fora do core escolhido. Alta |
| 3 | Hook LSM task_fix_setuid | rsuntk; bsxx não verificável | EXCLUIR | Sem justificativa para importar; corpo de bsxx depende do fechamento das lacunas. Média |
| 4 | LKM e late load | KSU; KP tem .ko próprio, sem o mesmo comportamento | CORE | Adotar o ciclo LKM KSU para GKI; não combinar loaders. Alta |
| 5 | kpimg / patch estático | APatch/KernelPatch | EXCLUIR | Acoplado ao KP e incompatível com a decisão KSU única. Alta |
| 6 | magiskinit / proxy de init | Magisk | EXCLUIR | Não iniciar segunda cadeia Magisk; funções necessárias entram em adaptadores próprios. Alta |
| 7 | ksuinit + kernelsu.ko no cpio | KSU | CORE | Usar apenas a integração KSU versionada para LKM. Alta |
| 8 | Redirect de /system/bin/su | KSU, Magisk e KP têm desenhos acoplados | PORT | Um único entrypoint SuperRoot encaminha ao backend ksud e aplica política. Não copiar redirect de outro core. Média |
| 9 | Allowlist de UID no kernel | KSU; equivalentes acoplados em APatch/forks | PORT | Implementar a fonte única de identidade/policy que alimenta a decisão KSU. Média |
| 10 | Policy su em SQLite magisk.db | Magisk | PORT | Migrar dados úteis para o store de ksud; não conservar magisk.db como autoridade. Alta |
| 11 | Daemon por socket Unix | Magisk | PORT | Criar facade local versionado; sem protocolo Magisk nem magiskd. Alta |
| 12 | IPC reboot(magic) + ioctl K | KSU | CORE | ksud encapsula comandos KSU; APK usa SRIP, não ioctl bruto. Alta |
| 13 | SuperKey/auth_superkey | KernelPatch | PORT | Preservar, se o dono quiser, o desafio SuperKey do SuperRoot na camada de policy; não importar SuperCall. Alta |
| 14 | module.prop + disable/remove/update | Contrato de arquivos KSU defs.rs e APatch defs.rs | ADAPT | Base de compatibilidade de módulos. Alta |
| 15 | webroot + action.sh | Nomes compartilhados KSU/APatch | ADAPT | Executar pela API de módulo do SuperRoot e relatar resultado. Alta |
| 16 | Magic mount, tmpfs/bind/.replace | Magisk | PORT | Novo mount planner sob o ciclo L2/KSU; suporte a .replace só após teste explícito. O algoritmo Magisk é acoplado. Média |
| 17 | Mount delegado por metamount.sh | KSU/APatch e outros que expõem o nome | ADAPT | Executar o contrato delegado; o efeito pertence ao script externo. Alta para contrato, baixa para cada módulo |
| 18 | Overlayfs do repo | Nenhuma implementação confirmada na matriz | EXCLUIR | Não chamar CONFIG_OVERLAY_FS=y de implementação pronta; avaliar como backend de mount separado. Média |
| 19 | Loader KPM | KernelPatch | PORT | Não hospedar binário/formato KPM. Traduzir apenas comportamentos selecionados para extensões first-party compiladas e revisadas junto ao LKM; sem loader dinâmico genérico. Média para estratégia, baixa para cobertura |
| 20 | Zygisk/JNI do zygote | Magisk | PORT | Separar um host de módulos e lifecycle, ligado ao CoreRepository/policy KSU. Compatibilidade deve ser decidida por módulo-alvo; não prometer ABI universal. Baixa, depende do fechamento das lacunas |
| 21 | Umount de módulo por UID | KSU/Suki e outros motores KSU | PORT | O core recebe uma policy por UID e aplica uma visão de mounts isolada; falha não desmonta globalmente. Média |
| 22 | DenyList no daemon | Magisk | PORT | Reimplementar como opt-in explícito por UID/app. Sem heurística ou inclusão automática. Alta |
| 23 | uid-exclude de módulo | APatch/KernelPatch | PORT | Modelar como regra do provider de mounts/policy; sem API APatch. Média |
| 24 | SuList/MagiskHide vivo | Nenhuma; marcado morto | EXCLUIR | Não reanimar feature sem requisito e evidência. Alta |
| 25 | Cliente userspace susfs | Next/Suki | PORT | Implementar somente os comportamentos priorizados contra API KSU própria; não copiar protocolo SUSFS. Baixa, depende do fechamento das lacunas |
| 26 | susfs dentro do kernel do repo | Nenhum dos clones verificados | EXCLUIR | Não existe suporte atestado nesta árvore; qualquer exigência requer nova decisão de kernel. Alta para a matriz, cobertura futura não avaliada |
| 27 | selinux_hide/domínio SELinux do motor | KSU, Magisk e KP | PORT | Regras explícitas e mínimas para daemon, su e módulos; SELinux permanece enforcing. Não prometer ocultação integral. Média |
| 28 | Checagem de assinatura do Manager no kernel | KSU | PORT | Fixar identidade de Manager assinada e verificar antes de operação privilegiada; rotação de certificado exige migração planejada. Média |
| 29 | resetprop applet/CLI | Magisk resetprop/cli.rs; KSU tem CLI compatível | ADAPT | Preservar CLI compatível como utilitário userspace, fora do daemon e do core. Alta |
| 30 | Patcher boot.img como ferramenta | libmagiskboot.so, ferramenta isolada | ADAPT | Usar apenas como utilitário de unpack/repack após validação do formato; instalação/injeção continua específica do KSU e dos gates. Média |

Para features explicitamente classificadas como não verificáveis, e para compatibilidade Zygisk/KPM/SUSFS que dependa da lista completa de módulos, o detalhamento depende do fechamento das lacunas da matriz. As linhas da matriz demonstram origem e acoplamento, não equivalência de ABI.

## Reimplementação dos acoplamentos

- Su redirect: estabelecer um único caminho suportado para resolver su e encaminhar a requisição ao daemon KSU. O daemon autentica UID/PID/pacote, faz lookup da policy e retorna resultado; remover shims concorrentes Magisk/KP. O caminho final do executável é uma decisão de integração do commit KSU pinado. Confiança média.
- Allowlist: usar UID Android como chave canônica e registrar pacote, certificado, usuário Android e regra. A UI não altera diretamente estado de kernel. Mudanças de regra são transação do daemon e propagadas ao KSU core; negar em caso de identidade ambígua ou IPC indisponível. Confiança média.
- Umount: construir uma MountView por UID em cima de namespaces/mount operations que o KSU LKM e o kernel GKI suportem. Remover apenas mounts de módulos sob controle do SuperRoot daquela visão. Testar processos incluídos e excluídos; não desmontar globalmente. A técnica concreta depende das APIs disponíveis no build alvo. Média.
- Zygisk: tratar o runtime como subsistema posterior e separado do LKM: catálogo de módulos nativos, seleção de processos por policy, broker de ciclo de vida ligado ao processo zygote e isolamento por app. Reimplementar a integração da policy e do manager; auditar se o host de módulos-alvo pode ser adaptado ou se requer runtime próprio. A matriz não prova que qualquer módulo Magisk Zygisk funcione com KSU. Baixa.
- KPM: não criar parser/loader de KPM nem aceitar módulos kernel opacos. Cada comportamento necessário vira um componente first-party revisado, integrado ao LKM por uma interface interna estática e restrita (callbacks/capabilities aprovadas no build). Ciclo de enable/disable pertence ao daemon e a um manifesto versionado. Se o módulo depende de hook impossível ou não aceitável no KSU, excluí-lo ou pedir ao dono para reabrir a escolha do core. Média para a fronteira; baixa para cobertura.
- SUSFS: não simular que o cliente userspace basta. Traduzir cada necessidade observável em uma capability própria do core/namespace, somente após confirmar que a árvore a suporta. Sem capability KSU correspondente, marcar não suportado, sem esconder a falha. O core escolhido não tem implementação SUSFS confirmada nesta matriz. Baixa.
- SELinux e ocultação: criar um conjunto pequeno de regras explícitas para o serviço, su e scripts de módulos; manter enforcing. Ocultação é policy opt-in por UID aplicada por mount view e regras de visibilidade que tenham suporte demonstrado. Não afirmar invisibilidade geral, integridade remota ou ocultação do bootloader. Confiança média.

## Sequência e gates

Os gates abaixo são critérios para marcos futuros. Nesta entrega não houve compilação, alteração de imagem nem acesso ao device.

### G0 — Congelar entradas de build

Fixar commit de KernelSU/DDK, toolchain, branch/config do kernel alvo, imagem de referência, versão do kernel e hashes de headers, Module.symvers e artefato do kernel.

PASS: todos os inputs pertencem ao mesmo build do alvo Android15-6.6 / 6.6.89; arquitetura é a do POCO C75; KPROBES, EXT4_FS e MODULES estão habilitados; a lista de símbolos necessária foi extraída do build exato; manifest de build reproduzível e completo. Se a origem de qualquer artefato não puder ser provada, gate falha.

### G1 — Primeiro build: KernelSU LKM + KMI/símbolos

Construir kernelsu.ko pelo DDK KSU pinado contra os artefatos de G0 e aplicar o estilo de verificação de gate_kmi_crc.

PASS: build/modpost conclui; não há símbolo indefinido; cada import do .ko existe no Module.symvers do mesmo kernel e seu CRC/KMI bate exatamente; vermagic/versão/arquitetura correspondem; dependências KPROBES, EXT4_FS e MODULES satisfazem; assinatura/trust exigida pelo alvo está resolvida; hash do .ko e relatório KMI são guardados junto ao manifest. Export de register_kprobe/unregister_kprobe sozinho não passa o gate. Ainda não prova carregamento nem boot no C75.

### G2 — Pacote offline e integração do manager

Empacotar APK Compose, payload KSU compatível, manifest, certificados e facade IPC sem instalar no device.

PASS: um único APK/Manager publicado; um core KSU; nenhum payload magiskinit/magiskd/KPM; handshake aceita apenas versões/capabilities declaradas; policy não é gravada antes da confirmação validada; hashes e assinatura conferem. Inventário estático de payload e testes offline poderão ser especificados na implementação; não executados aqui.

### G3 — Pré-condições para device/flash

Antes de qualquer operação que escreva em partição: backup completo e restauração verificada das imagens/partições alvo; fastboot_guard passa para o device identificado; slot B é o destino autorizado; caminho para restaurar e imagem original têm hashes registrados. Recusar slot A em qualquer circunstância.

PASS: checklist e artefatos de backup verificados, guard confirma device/partição/slot B, nenhum comando de escrita executado sem o dono responder literalmente sim para essa operação. Qualquer falha aborta antes de write/flash. Um backup não substitui autorização.

### G4 — Boot de validação em B

Somente após G3 e autorização explícita do dono, instalar no slot B e validar o boot sem tocar no slot A.

PASS: Android inicia em B; KSU LKM carrega uma única vez e a versão/KMI reportados batem com G1; daemon fica disponível; não há panic, bootloop ou perda de dados; caminho de rollback restaura a imagem original. O dono aprova separadamente qualquer troca de slot/ativação ou próximo flash.

### G5 — Policy e pedidos su

Validar handshake, identidade do Manager, ALLOW/DENY/ASK, expiração, popup, concorrência, timeout, SuperKey se habilitada, migração do banco e reinício do daemon.

PASS: identidade válida com allow obtém somente a autoridade pedida; deny, timeout, assinatura inválida, nonce repetido e falha IPC negam; policy persistida só depois de validação; nenhuma linha antiga desconhecida vira grant automático. Exige autorização do dono para conceder root durante o ensaio no device.

### G6 — Contrato de módulos

Testar módulos de referência KSU e Magisk por #14, #15 e #17: manifesto, estados de ciclo de vida, action.sh/webroot e chamada delegada metamount.sh. Cobrir também atualização, remoção, módulo que falha no boot e rollback.

PASS: estado canônico reproduzível; scripts rodam na fase declarada e somente uma vez; erro fica associado ao módulo; disable/remove/rollback impedem reexecução indevida; mount effect é observado e limitado ao escopo da policy. Não declarar Magisk compatibility total por passar esses casos.

### G7 — Isolamento, SELinux e recuperação

Validar App Profile, regras SELinux, mount view por UID, DenyList opt-in e escada de recuperação/quarentena sobre o novo store de módulos.

PASS: SELinux continua enforcing; UID opt-in recebe apenas a visão configurada; demais UIDs mantêm a visão padrão; erro de policy não causa unmount global; boot ruim permite recuperar por B e desativar somente o módulo suspeito antes do disable-all. A ocultação fica documentada como best-effort, sem promessa de invisibilidade.

### G8 — Zygisk/KPM/SUSFS por módulo priorizado

Executar somente depois de o dono aprovar a lista de módulos indispensáveis e os comportamentos requeridos.

PASS: cada módulo-alvo tem caso reproduzível e feature/capability explícita sob KSU; módulos não cobertos são recusados com motivo. Se o custo de reimplementação ou ausência de hooks tornar um requisito essencial inviável, parar e reabrir a decisão KSU/APatch com o dono; não instalar segundo core como fallback.

## Riscos e decisões do dono

1. KMI e carregamento no C75 não foram verificados. O G1 pode falhar mesmo com os exports-base presentes. Confiança alta quanto a essa lacuna.
2. KSU daemon IPC e verificação da assinatura do Manager variam por versão/commit. Fixar e inspecionar a API do pin escolhido antes de congelar SRIP/1. Confiança média.
3. O inventário de módulos indispensáveis e o conteúdo completo das células não verificáveis ainda podem mudar escopo de Zygisk, KPM e SUSFS. Depende do fechamento das lacunas da matriz.
4. Compatibilidade do formato de módulo não garante compatibilidade de instalador, hooks de boot, mounts ou efeitos laterais. Cada módulo suportado precisa de teste e lista de capabilities.
5. Código de módulo executa com privilégio elevado e pode causar bootloop. O dono decide a política de confiança para ZIPs, módulos permitidos e execução de scripts.
6. Um mount/SELinux policy incorreto pode quebrar app ou boot. O dono escolhe quais UIDs entram em isolamento/ocultação e se SuperKey é obrigatória; padrão recomendado é deny por falha e opt-in explícito.
7. App Profile, SuperKey e recovery ladder existem no fork Magisk atual. A semântica pode ser preservada, mas o armazenamento e o daemon precisam migrar para ksud sem duplicar fonte de verdade. Confiança média com base nos documentos locais do fork.
8. A instalação exige decidir como payload KSU, initramfs e APK são versionados e recuperados juntos. Nenhuma imagem foi alterada nesta fase.
9. Exigem confirmação literal sim do dono: qualquer flash/write, qualquer alteração do slot B, ativação/reboot de teste que altere o slot, teste de concessão real de root em device e cada operação de rollback que escreva partição. O backup, fastboot_guard, slot B e bloqueio absoluto do slot A são pré-condições adicionais, nunca substitutos do sim.
10. Também ficam para decisão do dono: lista mínima de módulos; inclusão e prioridade de Zygisk/KPM/SUSFS; política de assinatura de módulos; padrão de SuperKey e de ocultação por app; migração de dados e identidade/certificado de atualização do Manager.

## Evidência local e confiança

- Fase 1: PHASE1_SYNTHESIS.md e CODEX_core_decision.md registram KernelSU como core recomendado com confiança média, opções/símbolos-base e KMI ainda não verificado.
- GROK_matriz_features.md registra as 30 features, origens e células PORTÁVEL/ACOPLADA/AUSENTE/NÃO-VERIFICÁVEL. Somente #14/#15/#17 são contratos portáveis para a arquitetura proposta.
- SuperRoot/Magisk/README.md identifica o fork atual como userspace Magisk e documenta App Profile, SuperKey, hide-hardening e recovery ladder. app/apk tem MainActivity/Compose; app/apk-legacy é uma variante separada.
- native/src/core/module.rs contém o ciclo de módulos/estados disable/remove/update; native/src/core/su/connect.rs e su/daemon.rs contêm a fronteira atual de pedido su; app/core tem PolicyDao, SuRequestHandler e RootUtils. Estes componentes servem como referência de comportamento e migração, não como daemon/core a manter em paralelo.
- A checagem direta da árvore common não encontrou drivers/kernelsu nem Module.symvers na raiz common. A Fase 1 localiza o .config e Module.symvers gerados dentro do output de build e registra os símbolos Kprobe base. Não se leu nem usou conteúdo de CLAUDE2_*.md; não se executou build, teste, flash ou operação no device.

## Resumo para decisão

Arquitetura-alvo: APK Compose único → CoreRepository/SRIP versionado → ksud como único backend privilegiado → KernelSU LKM único. Compatibilidade de módulos entra por #14/#15/#17; policy, su, allowlist, mount por UID, SELinux e comportamentos de Zygisk/KPM/SUSFS são implementados como capacidades próprias, com escopo condicionado ao inventário e aos gates. O primeiro PASS é G1: .ko para o build exato, sem import indefinido e com CRC/KMI, vermagic e assinatura/trust correspondentes. Nenhum gate foi executado nesta entrega.
