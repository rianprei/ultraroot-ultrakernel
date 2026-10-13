# Revisão Codex — spec UltraRoot rev.5

**Escopo:** leitura integral das 1001 linhas de `docs/superpowers/specs/2026-10-11-ultraroot-design.md`, revisão estática e read-only. Consultei também os catálogos referenciados e os documentos SP1 de paridade/recuperação para conferir o estado real da decomposição. Nenhum arquivo de implementação foi alterado; não rodei build, testes ou comandos de device.

## Veredito

**AJUSTAR antes de usar como base direta das tasks do Manager.** A arquitetura é coerente como direção: control plane comum, adapters por backend, runtime de módulos, performance, boot/recovery e diagnóstico. O spec evita afirmar suporte universal de device e rejeita a fusão dos runtimes de root (linhas 8-17, 22-34, 49, 59, 788-802). Contudo, não existe mapa item-a-item do inventário para esses blocos, e há três conflitos/ambiguidades que afetam gate, segurança de boot e status de validação.

## Mapeamento para arquitetura

Tomando como seis blocos o control plane, SDK/API, adapters de root, Module Runtime, Performance Engine e o conjunto Boot/Recovery/Diag/Automation/Monitor (linhas 22-34), há um mapeamento **grosso**, mas não limpo nem verificável:

- Root e autorização Magisk/KSU/APatch cabem nos adapters; Zygisk e módulos cabem no runtime/adapters; patchers e imagens cabem em Boot/Recovery; controle de hardware cabe na Performance Engine.
- Dashboard, Root Access Center, Module Center, Boot Image Center, System Toolkit, Performance, Recovery e Extensions (linhas 624-782) são boas áreas de UI/control plane. A seção não liga cada função de origem a uma dessas áreas e a um adapter concreto.
- O diagrama descreve o runtime como “KSU+Magisk fmt” com adapter Zygisk (linhas 27-29), mas não reserva caminho para APM. O inventário APatch inclui formato, scripts e ciclo de vida próprios (linhas 486-512). O guia oficial APM documenta, entre outros, `post-mount.sh` e `boot-completed.sh`; isso precisa de runtime/adapter identificado, não de compatibilidade presumida com o runtime KSU/Magisk. [Guia APM do APatch](https://apatch.dev/pt_BR/apm-guide.html). KernelSU também tem sua própria restrição: um metamódulo ativo por vez e montagem delegada a ele. [Guia de metamódulos KernelSU](https://kernelsu.org/guide/metamodule.html).

Os arquivos chamados pelo spec ainda não preenchem esse mapa: `CAPABILITIES.md` se identifica como “seed SP1” e contém KSU-core, manager-grant e o grupo agregado F01–F30 (`docs/ultraroot/CAPABILITIES.md:1-15`); `COMPAT_MATRIX.md` também é seed, com algumas linhas de SP1 e grupos F01–F30 (`docs/ultraroot/COMPAT_MATRIX.md:1-16`). O restante do inventário de 1001 linhas não tem IDs estáveis, destino arquitetural, adapter responsável, estado por item ou critério de aceite. Portanto, dá para derivar épicos; ainda não dá para gerar tasks rastreáveis por capacidade.

## Contradições e ambiguidades

1. **Gate aponta para revisão errada.** O cabeçalho identifica rev.5 e descreve correções da rev.5 (linhas 1-5), mas o gate manda o dono aprovar rev.4 (linha 63). Atualizar a revisão e o critério de aprovação antes de qualquer plano derivado.
2. **`boot_b` não está marcado como cenário exclusivo do lake.** A regra dura manda detectar o slot ativo e diz que B-nunca-A é exceção POCO C75, não regra universal (linha 16). A cobertura de recovery usa “rollback boot_b” sem qualificar dispositivo (linha 54). Escrever “lake/POCO C75” nesse cenário e definir a operação genérica por detecção do slot e procedimento validado por device. A suite SP1 atual realmente fixa B para o lake, o que reforça a necessidade de escopo explícito (`RECOVERY_SUITE.md:8-17`, `DEVICE_PLAN_SP1.md:16-30`).
3. **“Fundação validada” não carrega os campos de evidência exigidos pelo próprio spec.** A regra pede path exato, hash, comando, versão, data e resultado para toda alegação (linhas 17 e 20), mas a afirmação agregada C0.1/C0.3 em §2 não aponta os artefatos nem separa precisamente o que foi validado. Os registros SP1 classificam `manager-grant` como `NOT_TESTED`/`IMPLEMENTED_UNVALIDATED` e dizem que Task 7 não rodou (`CAPABILITIES.md:10`, `COMPAT_MATRIX.md:10`). O relatório C0.3 prova checks de host para artefatos/certificado/kernel e declara que não houve install/device (`research/phase4/C03_stablekey_result.md:3,20,22-36`); não prova que o Manager recebeu root no aparelho. Especificar esse limite na frase de §2 e ligar cada claim ao relatório reproduzível; até lá, não usar “HOST_TESTED” como status do grant.

Não achei uma promessa explícita de compatibilidade com todo Android, nem uma proposta de executar MagiskSU, KSU e KernelPatch como um único root runtime. §1, §3, §5 e §11 são claros quanto a validação por device, UNKNOWN e compatibilidade modular por etapas. “Universal Module Center” (linha 660) ainda é um rótulo arriscado: o conteúdo qualifica a compatibilidade por backend, então “Unified Module Center” ou um subtítulo explícito reduziria a leitura de compatibilidade universal.

## Lacunas que bloqueiam o detalhamento do Manager

1. **Falta um mapa de rastreio completo.** Dar ID a cada capacidade do inventário, associar origem/proveniência, bloco de UI, adapter/runtime, deps, IMPL/VALID, risco, teste/evidência e aceite. Os campos requeridos já aparecem em §4 (linhas 36-40), mas não são aplicados ao inventário completo nem estão preenchidos nos arquivos seed.
2. **APM não tem dono no runtime da arquitetura.** Adicionar explicitamente o runtime/adapter e as diferenças de hooks/lifecycle APM, ou registrar quais itens serão EXCLUDED e por quê. Não converter suporte ao formato ZIP em alegação de compatibilidade de scripts/mounts.
3. **Funções do próprio manager Magisk sem decisão de produto.** Ocultar/reempacotar a identidade do app está no inventário (linhas 183-203), mas não tem seção correspondente no UltraRoot nem status de exclusão. Definir se há equivalente, se fica fora por ser um único APK, ou se é EXCLUDED com justificativa.
4. **Atualização do APK UltraRoot ausente.** O inventário Magisk cita instalação/atualização do próprio manager (linha 186), enquanto a seção unificada trata atualização de módulos e de extensões (linhas 662-688, 781), não ciclo de atualização do APK. Acrescentar ou excluir explicitamente, incluindo como o manager recupera uma atualização inválida.
5. **Backup e logs estão presentes, mas com escopo repetido.** Boot Image Center trata backups de imagens (linhas 690-704), System Toolkit inclui backup/restore genérico (linha 722), e Recovery Center volta a listar backups e rollback (linhas 754-764). Logs aparecem no Dashboard, Root Access Center, Toolkit, Recovery e Extensions (linhas 641-658, 718, 760, 778). Definir o proprietário dos dados, o que pode ser restaurado, retenção, fluxo de rollback e modelo de eventos/redação de segredos. O spec já proíbe registrar SuperKey (linhas 465-466), mas essa regra precisa pertencer ao contrato central de logs.

**Resposta aos exemplos de lacuna:** boot-image management, backup/restore e logs não estão totalmente esquecidos; aparecem em várias seções. A lacuna é escopo e responsabilidade duplicados. A ausência mais nítida para o Manager é sua própria atualização.

## Usabilidade / próxima forma de decomposição

Como visão e lista de épicos, o documento é utilizável. Como origem direta de tickets, ainda é ambíguo e redundante: as listas por produto (linhas 98-615) repetem as áreas unificadas (linhas 617-782), não têm IDs comuns e misturam capacidade técnica, operação de UI, dependência externa e requisito de segurança. §8 define só a regra “sem teste = UNKNOWN”, sem tabela de paridade ou aceite por backend; os detalhes atuais de módulo cobrem apenas fixtures KSU/Magisk e marcam boot e mount como NOT_TESTED (`MODULE_PARITY.md:3-23`).

Antes dos planos do Manager, criar o mapa item→tela→adapter/runtime→dependências→in/out-of-scope→aceite e dividir backups, logs, módulo/APM, boot e self-update em operações pequenas. Manter teste, validação e compatibilidade por backend/device como eixos separados, como o próprio §4 pede.

```text
STATUS: DONE
MAPEIA_ARQUITETURA: não
CONTRADICOES: 3
LACUNAS: 5
VEREDITO: AJUSTAR
```
