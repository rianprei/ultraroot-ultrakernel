# Review de cobertura do TODOs.md — rodada 2

**VEREDITO: REJEITADO.** A cobertura é `515 / 517 = 99,61%`, acima de 99%, mas há violações verificadas nas categorias (d) e (e). O critério de aceite exige cobertura ≥99% **e zero violações**.

## Denominador e medição

| Fonte | Unidades obrigatórias | Coberto | Parcial | Ausente |
|---|---:|---:|---:|---:|
| Bullets do inventário §1–§4, spec:151–853 | 433 | 431 | 2 | 0 |
| IDs CAPABILITIES OBRIG + OBRIG-PROPOSTO | 52 | 52 | 0 | 0 |
| Requisitos de produto §§0–13 | 32 | 32 | 0 | 0 |
| **Total** | **517** | **515** | **2** | **0** |

A contagem do inventário veio de 440 bullets no intervalo spec:151–853, menos 7 itens classificados fora do obrigatório: spec:244, 426–428 e 492–494. O catálogo tem 44 OBRIG e 8 OBRIG-PROPOSTO (CAPABILITIES.md:33–84 e 85–92). As 32 unidades §§0–13 foram contadas por parágrafo normativo, bullet ou linha de tabela; o diagrama §3 conta como entregável (spec:42–116). A matriz item a item, com linha-fonte, linha TODO e classificação, está em `docs/history/reports/TODOs_coverage_round2.tsv` (517 linhas de itens).

## Itens parciais

- `spec:525 — “Configurar SuperKey durante o processo de patch.”` — TODOs.md:351 pede gerar/configurar SuperKey e TODOs.md:361 pede patch automático, mas nenhuma tarefa conecta a configuração da chave ao fluxo de patch da imagem. A cobertura específica do fluxo fica parcial.
- `spec:656 — “requisitos de patch e símbolos necessários.”` — TODOs.md:366–368 pede verificar configuração e pré-requisitos genéricos do kernel antes da instalação; não exige verificar os símbolos necessários ao patch.

**AUSENTES:** nenhum. Os dois itens acima têm tarefa relacionada, mas falta o detalhe indicado.

## Comparação com a rodada 1

**95,48% (486/509) → 99,61% (515/517).** A base de comparação está em `REVIEW_TODOs_codex4.md:17–22`; os 23 itens antes parciais/ausentes estão listados em `REVIEW_TODOs_codex4.md:552–577`. Nesta rodada, 23 agora estão cobertos: inventário spec:353, 529, 571, 576, 599, 622, 661, 715 e 841; ID `MG-SU-GRANT`; requisitos gerais spec:42, 54, 55, 57, 65, 70, 74, 85, 91, 92, 93, 96 e 99. Os dois antes ausentes, spec:622 e 661, agora têm tarefas em TODOs.md:404 e 613.

Oito IDs novos passaram a compor o denominador e estão cobertos: `UR-DASH`, `UR-CLI`, `UR-MOD`, `UR-BOOT`, `UR-SYS`, `UR-REC`, `UR-EXT` e `MG-APP-MGMT` (CAPABILITIES.md:85–92; TODOs.md:493–565).

**Perdas de classificação:** `spec:525` e `spec:656` eram COBERTO na rodada 1 e ficam PARCIAL nesta medição, porque as tarefas atuais não especificam o vínculo com o fluxo de patch nem a verificação dos símbolos. O texto atual do TODOs é o evidenciado em TODOs.md:351, 361, 366 e 368; não os trato como regressões introduzidas pela revisão do autor.

## Violações verificadas

- **(a) NÃO-REAL/FORA/EXCL como obrigatório:** nenhuma encontrada. TODOs.md:4 manda esses itens para a seção 10; TODOs.md:645–656 lista os itens excluídos e as razões.
- **(b) `[x]` sem evidência:** nenhuma após resolver a referência interna. Há 9 linhas `[x]` (TODOs.md:25–30, 34, 234 e 241); os caminhos citados existem. TODOs.md:241 aponta explicitamente para a seção 1.1, onde TODOs.md:25 cita `C03_stablekey_result.md`, existente. A evidência de instalação em TODOs.md:30 aponta para `evidence/2026-10-11_install_vc40000.txt`, existente.
- **(c) ID do TODOs ausente do catálogo:** nenhum. A varredura dos IDs `MG-*`, `KSU-*`, `AP-*` e `UR-*` não encontrou ID concreto fora do catálogo; os oito IDs propostos estão em CAPABILITIES.md:85–92. `MG-MOD-*` em TODOs.md:521 é um padrão de grupo, não um ID singular.
- **(d) Divergência de status de ID:** TODOs.md:521 marca `[~]`, que a legenda em TODOs.md:7 define como IN_PROGRESS, e na mesma linha afirma “IDs existentes, ainda NOT_STARTED” para `KSU-META`, `AP-APM` e `MG-MOD-*`. CAPABILITIES.md:38–43, 58, 72 e 87 mantém esses IDs como `IMPL NOT_STARTED`. Há contradição entre o marcador e os status declarados.
- **(e) Tarefa de aparelho sem `[DEVICE]`:** TODOs.md:27 registra “Boot C0.1 no aparelho” sem tag; TODOs.md:234 repete o boot no aparelho sem tag; TODOs.md:31 registra C0.3 “no aparelho” e o handoff de flash/boot sem tag. Em contraste, a instalação está marcada em TODOs.md:30, a escrita de imagem em TODOs.md:219 e 222, e os seis cenários de recuperação em TODOs.md:570–575.
- **(f) Evidência citada inexistente no disco:** nenhuma. A checagem dos 9 `[x]` não encontrou caminho ausente; o resultado literal está resumido abaixo.

**Inconsistência adicional:** TODOs.md:587 diz “Falta incluir os IDs propostos da seção 6”, mas os oito IDs já constam em CAPABILITIES.md:85–92 e TODOs.md:491 diz que foram adicionados. A afirmação está desatualizada, embora não altere a cobertura das 52 linhas.

## Não obrigatórios fora do denominador

- `spec:244 MG-APP-STUB` — EXCLUDED porque reempacotar a identidade quebra a autorização C0.3; CAPABILITIES.md:53.
- `spec:426–427 KSU-META-OVFS` — NÃO-REAL no pin `6e386044`; overlayfs/meta-overlayfs não está implementado; CAPABILITIES.md:68.
- `spec:428` — magic mount não é função do KSU deste pin; o requisito correspondente pertence ao Magisk `MG-MOD-MOUNT`; spec:428, CAPABILITIES.md:42.
- `spec:492 KSU-HWBP` — NÃO-REAL no pin; CAPABILITIES.md:65.
- `spec:493 KSU-PHYSMEM` — NÃO-REAL e FORA-DO-PRODUTO por envolver ocultação; CAPABILITIES.md:66.
- `spec:494 KSU-SYSCALL-API` — API genérica não existe; o hook interno de `su` permanece coberto por `KSU-ROOT`; CAPABILITIES.md:67.
- `spec:430 KSU-DETECT-SURFACE` — FORA por motivo de reduzir superfície de detecção; CAPABILITIES.md:69.
- `spec:516 AP-SEL-BYPASS` — FORA por bypass de AVC; CAPABILITIES.md:75.
- `spec:580 AP-KPM-PART` — NÃO-REAL no pin; CAPABILITIES.md:74.

## Evidências e sabotagem

Contagem executada com `python3 docs/history/reports/generate_round2_matrix.py`; verificação de paths/IDs/tags em `docs/history/reports/round2_verification.txt`:

```text
matrix=docs/history/reports/TODOs_coverage_round2.tsv rows=517 inventory=433 req=32 capabilities=52
inventory_classes {'COBERTO': 431, 'PARCIAL': 2, 'AUSENTE': 0, 'UNKNOWN': 0}
req_classes {'COBERTO': 32, 'PARCIAL': 0, 'AUSENTE': 0, 'UNKNOWN': 0}
cap_classes {'COBERTO': 52, 'PARCIAL': 0, 'AUSENTE': 0, 'UNKNOWN': 0}
```

Verificação dos `[x]` e paths: `checked_x_rows=9 missing_evidence_paths=[]`; cada path e seu resultado `exists=True` estão em `docs/history/reports/round2_verification.txt`. Varredura de IDs: `unknown_ids=[]`. Comparação de campos IMPL/VALID explícitos: 0 divergências; o achado (d) acima é o marcador `[~]` de TODOs.md:521 contra os estados expressos na mesma linha e no catálogo.

Sabotagem executada: cópia do TODOs para `docs/history/reports/todos-rodada2-sabotado.md`, remoção da única linha que contém “Corpus de pesquisa e metodologia reprodutível” (TODOs.md:594), e execução de `round2_sabotage_check.py` contra a fonte e a cópia. Saída literal:

```text
removed_line=594; matches=1; text=- [ ] Corpus de pesquisa e metodologia reprodutível: domínios root/privilégio; módulos; injeção (Zygisk, LSPosed, hooking); admin Android; boot e recovery; performance e hardware; monitoramento e benchmark; ferramentas. Inviáveis, redundantes, experimentais e externos ficam no catálogo
docs/ultraroot/TODOs.md: COBERTO TODO:594
docs/history/reports/todos-rodada2-sabotado.md: AUSENTE — item Corpus de pesquisa e metodologia reprodutível
```

Auto-revisão concluída: denominador independente contado como 433 + 52 + 32 = 517; a matriz contém 517 itens; a sabotagem foi executada e a checagem acusou a ausência. Não usei adb.
