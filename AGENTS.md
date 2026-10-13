# AGENTS.md: regras de trabalho para agentes de IA

Este arquivo vale para qualquer agente que trabalhe neste repositório. Você não tem acesso ao computador do dono, só a este repositório e ao que ele contém. Por isso, caminhos são sempre relativos à raiz do repo.

## Regras do dono (valem sem exceção)

- "device SÓ sob 'sim, <passo>' presencial", inclusive leitura. Você não tem acesso a aparelho. Qualquer passo de aparelho (adb, fastboot, flash, install, leitura) vai para `docs/PENDENCIAS-DO-DONO.md`, marcado como pendente.
- "boot safety = slot B, NUNCA A".
- "legítimo (sem evasão/ocultação indetectável)": nada de DenyList, hide ou umount por perfil.
- "Cross-review obrigatório (autor ≠ revisor)": nenhuma entrega é aceita sem revisão de outro agente ou de humano.
- "fix all findings, no severity triage": todo achado de revisão vira correção. Não existe achado "baixo" para deixar de lado.
- Chaves: nunca abrir, imprimir ou registrar senha. Keystores e `.pass` ficam fora do repo (`.gitignore`).
- Lake: commit só com "sim, commitar"; push só com "sim, push". Isso vale para o repo do lake, não para este.
- Commits e PRs não levam linha de atribuição de IA (skill `no-ai-attribution` do dono): nada de `Co-Authored-By`, `Generated with` ou equivalente.

## Prova obrigatória

Toda afirmação sobre o estado do código tem uma destas provas: caminho e linha (`arquivo:linha`), comando e saída, ou hash. Sem prova, escreva **NÃO VERIFICADO**. Não escreva "parece correto" nem "o restante é rotineiro".

## Contrato para pedir trabalho a outro agente

Todo pedido de trabalho tem estes campos: OBJETIVO, ESCOPO (caminhos de escrita e de leitura), DEFINIÇÕES, PREDICADO DE SUCESSO (com quantificadores), TAREFA (passos numerados), PROVA (sabotagem quando for código), PROIBIDO e TETO de chamadas de ferramenta.

## Fluxo de trabalho

1. Trabalhe em branch nova a partir de `main`.
2. Abra PR. Não faça merge direto.
3. Outro agente (ou o humano) revisa o PR. O autor não revisa o próprio trabalho.
4. Corrija todo achado da revisão.
5. Só o dono faz o merge.

## Caminhos e idioma

- Caminhos sempre relativos à raiz do repo, sem `./` e sem `../`. Nada de `/home/`, `~/superroot` ou `~/Documentos`.
- Documentos em português do Brasil, com acentuação completa.
- Código, identificadores, comandos e nomes de arquivo ficam no original (inglês).
- Dado pessoal (e-mail, serial de aparelho, IP privado, MAC, IMEI) não entra no repo. Se achar, redija e registre o caminho em `docs/history/REDACOES.txt`, sem o valor.

## Como retomar

Leia nesta ordem:

1. `README.md`
2. `docs/ESTADO.md`
3. `docs/ultraroot/plan.md`, seções §1 (regras fixas) e §9 (estado de execução)
4. `docs/ultraroot/TODOs.md`, seção §1 (SP1)
5. `docs/PENDENCIAS-DO-DONO.md`

Antes de qualquer alteração, rode `tools/verify_repo.sh` e confirme que sai com código 0.
