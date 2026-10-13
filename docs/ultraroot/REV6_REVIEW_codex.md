# Cross-review Codex — UltraRoot rev.6

**Escopo:** leitura integral do spec rev.6 (1060 linhas) e de `docs/ultraroot/CAPABILITIES.md` (80 linhas), revisão estática e read-only. Conferi que os checkouts locais correspondem aos pins citados: Magisk `e8915d9d`, KernelSU `6e386044`, APatch `e78e7b3e` e KernelPatch `f36f3d13`. Não compilei, testei runtime nem toquei em device.

## Veredito

**CORRIGIR antes de aprovar o gate.** A limpeza dos recursos inventados/evasivos e a maior parte das correções da revisão anterior estão presentes. Restam três achados: proveniência incompleta no mapa, uma contradição entre “todo REAL é obrigatório” e uma exclusão real, e a evidência insuficiente para o status `DEVICE_TESTED` do boot C0.1.

## Confirmações pedidas

1. **As cinco capacidades inventadas saíram de `OBRIG` e estão marcadas `NÃO-REAL`.** São `KSU-HWBP`, `KSU-PHYSMEM`, `KSU-SYSCALL-API`, `KSU-META-OVFS` e `AP-KPM-PART` (spec §§2.5/2.10 e 3.4; catálogo linhas 59–62 e 68). Nenhuma dessas linhas pertence ao conjunto obrigatório. `KSU-PHYSMEM` também está `FORA`.

2. **As três capacidades evasivas estão fora, com o disfarce descrito.** `KSU-PHYSMEM` expõe a troca de “invisibly” por “interfaces do kernel” (spec linha 481); `KSU-DETECT-SURFACE` registra a justificativa “reduces detection surface” e a exclui (linha 418); `AP-SEL-BYPASS` identifica o eufemismo sobre manter o contexto e descreve o bypass de AVC (linha 504). Todas estão `FORA`/`EXCLUDED` no catálogo (linhas 60, 63 e 69), sem `OBRIG`.

3. **`manager-grant` não aparece como validado.** A linha seed está `IMPLEMENTED | NOT_TESTED` (catálogo linha 10); a fundação especifica que não há resultado de aceite com `uid=0` sem Magisk e proíbe `HOST_TESTED`/`DEVICE_TESTED` para o grant (spec linha 56). `KSU-ROOT` também mantém `VALID=NOT_TESTED` e diz que a cópia do `KsuBackend` só lê versão/UAPI (catálogo linha 48).

4. **A tabela tem exatamente 44 linhas `OBRIG`.** Todas têm ID, uma das seis seções, adapter/runtime, status IMPL e status VALID (catálogo linhas 27–46, 48–58, 64–67, 70–78). Não encontrei ID dos cinco `NÃO-REAL` ou das três capacidades `FORA` entre elas. Os status de validação das 44 capacidades são `NOT_TESTED`; isso não afirma que UltraRoot já as implementou ou que o comportamento foi provado em aparelho.

   A afirmação “44 são reais no pin” exige qualificação: 38 linhas descrevem capacidades dos pins de Magisk, KernelSU ou APatch/KernelPatch; seis IDs `UR-*` (`UR-PERF-READ`, `UR-PERF-WRITE`, `UR-BACKUP`, `UR-LOG`, `UR-SELFUPDATE`, `UR-EXT-SDK`) são requisitos criados para o próprio UltraRoot, não funções que o pin upstream precisa conter. Não são capacidades falsas ou evasivas, mas a definição de `REAL` em §0 (“presente no pin citado”) não as classifica. No estado atual, confirmo o rastreio formal de 44 itens obrigatórios e a ausência de inventadas/evasões nesse conjunto; não confirmo que os 44 sejam todos “presentes no pin” sob a definição literal.

## Achados restantes

1. **O mapa não cumpre o esquema de evidência que o próprio §4 exige.** O cabeçalho da tabela de rastreio (catálogo linha 25) traz descrição, seção, adapter/runtime, IMPL, VALID, dependência e teste pendente. Faltam por linha origem/proveniência reproduzível, alvo Android/kernel/ABI/device, risco/modo de falha, evidência atual e lacuna/próxima ação, todos exigidos no spec linha 78. Isso também deixa os seis IDs `UR-*` sem origem formalmente distinta dos itens “o que o pin tem” do cabeçalho. Acrescentar esses campos é necessário para auditar o “REAL” item a item; a lista seed das linhas 7–15 não supre isso para as 44 linhas.

2. **A regra de obrigatoriedade conflita com `MG-APP-STUB`.** O spec linha 36 define `REAL` como presente no pin e não marcado `NÃO-REAL`/`FORA`, e diz que toda capacidade REAL é obrigatória. Porém o próprio spec linha 232 afirma que ocultar/reempacotar a identidade do app Magisk existe no pin e marca `MG-APP-STUB` como `EXCLUDED`; o catálogo linha 47 mantém essa exclusão justificada. A razão do produto é explícita e tecnicamente compreensível, mas a regra não diz que `EXCL` justificado é exceção à obrigatoriedade. Declarar essa precedência (ou classificar explicitamente o item como fora do produto) elimina a contradição.

3. **O status `DEVICE_TESTED` de C0.1 ainda não atende ao padrão de evidência do spec.** Na linha 54, a evidência é uma frase em `research/MASTER_PLAN.md:8`; o mesmo registro diz que não há caminho para dmesg/log e que os números de módulos/erros não foram recontados. Isso não satisfaz a regra de §1 linha 45 (“claim sem base = UNKNOWN”). A ressalva transparente melhora o registro, mas `DEVICE_TESTED` continua mais forte que a evidência descrita. Marcar como relato/`UNKNOWN` até anexar evidência primária preserva a separação correta: o boot C0.1 relatado não valida `manager-grant`.

## Achados anteriores resolvidos

- Gate agora aprova rev.6 (spec linha 104).
- `boot_b` está limitado ao lake/POCO C75; o caminho geral exige detectar o slot e usar procedimento validado (linhas 44 e 95; catálogo linha 75).
- C0.3 e `manager-grant` têm escopo de evidência separado; grant continua `NOT_TESTED` (linha 56).
- `ApmRuntime` tem dono explícito para APM, separado de KSU/Magisk (linhas 73 e 742); a compatibilidade de ZIP não é tratada como prova de lifecycle.
- A identidade do app Magisk está decidida como excluída, com razão ligada ao certificado/pacote fixos (linha 232).
- Atualização do próprio APK ganhou ID, seção e regra de rejeição de certificado/falha (linhas 672–679).
- Backup e log têm dono único, com retenção e redação de segredos definidas (linhas 665–670); o System Toolkit não cria log próprio (linha 774).
- As seis seções de UI estão listadas (linha 21) e cada linha `OBRIG` aponta para uma delas.

```text
STATUS: DONE / REV6: CORRIGIR / SOBROU_INVENTADA_OU_EVASAO: não / ACHADOS: 3
```
