# E1: copy e layout dos cartões de estado e da busca

Decisão pendente do dono (ver `docs/PENDENCIAS-DO-DONO.md`). Libera H7. As duas opções usam os mesmos três estados do backend.

## Fontes atuais

- Estados: `ultraroot/core-backend/src/main/java/dev/superroot/core/BackendState.kt`, linhas 4 a 6 (`Active`, `Absent`, `Incompatible`) e 16 a 20 (classificação).
- Filtro: `ultraroot/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessState.kt`, linha 14 (`RootFilter`), e linhas 19 a 21 (`NoMatch`).
- Tela: `ultraroot/app/src/main/java/dev/superroot/ultraroot/ui/RootAccessScreen.kt`, linhas 76 a 78.
- Textos atuais: `ultraroot/app/src/main/res/values/strings.xml`: `kp_unavailable_title` (linha 21), `kp_unavailable_hint` (linha 26), `root_unavailable_title` (linha 8), `root_nomatch_title` (linha 11), `root_nomatch_body` (linha 12), `root_grant_allowed` (linha 15), `root_grant_denied` (linha 16).

## Regras de copy (valem para as duas opções)

- Nunca afirmar que o root funciona. "su via KSU" não está provado (E4).
- "Permitido" significa só que a allowlist marca `allow_su`. Não significa que o su funciona.
- No estado incompatível, nada é concedido nem revogado.

## Opção A: informativa

Banner de estado fixo no topo. Busca e chips fixos logo abaixo. A lista rola por baixo. Sem botão.

| Estado | Título | Linha |
|---|---|---|
| Ativo (`Active`) | Backend respondeu | Kernel versão {version} · UAPI {uapi} |
| Sem resposta (`Absent`) | Sem resposta do kernel | Nenhum valor para mostrar. Verifique o backend de root e tente de novo. |
| Incompatível (`Incompatible`) | UAPI diferente do gerenciador | Kernel UAPI {kernelUapi}, gerenciador UAPI {managerUapi}. Nada é alterado enquanto as versões não batem. |

- Busca: placeholder "Buscar pacote ou UID".
- Chips: "Todos", "Permitidos", "Negados".
- Estado vazio: "Nenhum resultado" / "Nenhum app corresponde à busca e ao filtro."

Custo para H7: baixo. Não cria ação nova. Testes: texto de cada estado e busca fixa.

## Opção B: orientada à ação

Banner como primeiro item da lista, rolando junto. Busca e chips fixos.

| Estado | Título | Linha | Ação |
|---|---|---|---|
| Ativo | Backend pronto | Kernel {version} · UAPI {uapi}. A lista foi lida do kernel. | nenhuma |
| Sem resposta | Kernel não respondeu | A lista não pode ser lida agora. | botão "Tentar de novo" |
| Incompatível | Kernel e app em versões diferentes | UAPI {kernelUapi} no kernel e {managerUapi} no app. Nada é concedido nem revogado. | nenhuma |

- Busca: placeholder "Pacote ou UID".
- Chips: "Todos", "Permitido", "Negado".
- Estado vazio: "Nada encontrado" / "Tente outro nome ou UID, ou mude o filtro."

Custo para H7: médio. O botão precisa de uma nova leitura do backend (`KsuBackend.readMode`, que já existe) e de teste próprio para o clique.

## Recomendação

Opção A para H7 agora: não cria comportamento novo para testar. A opção B pode vir depois, quando a nova leitura estiver testada.
