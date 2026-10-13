# Revisão de cobertura do TODOs.md — rodada 3

**VEREDITO: REJEITADO.** A cobertura medida é 517/517 = 100%, mas há violações (e): operações de concessão/revogação de root aparecem sem `[DEVICE]` sob capacidades marcadas `Device-gate: sim`. O critério exige cobertura ≥99% e zero violações.

## Denominador e medição

| Fonte | Regra contada | Total obrigatório | Coberto | Parcial | Ausente |
|---|---|---:|---:|---:|---:|
| Inventário §§1–4, spec:151–853 | 440 bullets `- ` menos os 7 marcados fora do obrigatório em spec:244, 426–428 e 492–494 | 433 | 433 | 0 | 0 |
| CAPABILITIES.md | 44 `OBRIG` + 8 `OBRIG-PROPOSTO` (capability table, linhas 33–92) | 52 | 52 | 0 | 0 |
| Requisitos de produto §§0–13 | 32 unidades normativas; âncoras spec:42,44,46,48,51–57,65–68,70,74,85,87,90–93,96,99,102,104,107,109,112,114,116 | 32 | 32 | 0 | 0 |
| **Total** |  | **517** | **517** | **0** | **0** |

Comando executado para a matriz item a item:

```text
$ python3 docs/history/reports/round3_coverage.py
todo=docs/ultraroot/TODOs.md inventory_raw=440 excluded_bullets=7 inventory=433 capabilities=52 product_requirements=32 total=517
CAPABILITY_COBERTO=52
INVENTORY_COBERTO=433
SPEC_REQ_COBERTO=32
coverage=517/517=100.00%
matrix=docs/history/reports/TODOs_coverage_rodada3.tsv
```

A matriz contém para cada uma das 517 unidades a linha-fonte, a classificação e a linha/trecho do TODO. Linhas: inventário spec:151–853; capacidades CAPABILITIES.md:33–92; requisitos gerais spec:42–116. Resultado conferido na matriz: 518 linhas incluindo o cabeçalho.

Os dois itens parciais da rodada 2 agora estão cobertos: spec:525 “Configurar SuperKey durante o processo de patch” → TODOs:362 “Configurar a SuperKey durante o patch da imagem”; spec:656 “requisitos de patch e símbolos necessários” → TODOs:370 “requisitos de patch e aos símbolos necessários”. Ambos aparecem como `COBERTO` na matriz.

## Comparação com a rodada 2

**99,61% (515/517) → 100% (517/517).** Ganhos: spec:525 e spec:656 passaram de PARCIAL para COBERTO, respectivamente em TODOs:362 e TODOs:370. Perdas: nenhuma.

## Sete correções pedidas

| # | Resultado | Evidência atual |
|---:|---|---|
| 1 | OK | Uma ocorrência na seção 1.1: TODOs:27 “`[DEVICE] Boot C0.1 no aparelho, recorte`”. |
| 2 | OK | Uma ocorrência na seção 1.1: TODOs:31 “`[DEVICE] C0.3 no aparelho`”. |
| 3 | OK | Uma ocorrência no seed KSU-core: TODOs:234 “`[DEVICE] KSU no boot C0.1`”. |
| 4 | OK | Uma ocorrência em AP-BOOT: TODOs:362 “Configurar a SuperKey durante o patch da imagem, no fluxo automático e no manual”. Cobre spec:525. |
| 5 | OK | Uma ocorrência em AP-BOOT: TODOs:370 “requisitos de patch e aos símbolos necessários”. Cobre spec:656. |
| 6 | OK | TODOs:523 usa `[ ]` para “KSU-META, AP-APM e MG-MOD-*”; CAPABILITIES.md:38–43,58,72 registra esses IDs como `NOT_STARTED`. |
| 7 | OK | Uma ocorrência em 8.2: TODOs:589 “CAPABILITIES.md: existe, com os IDs da seção 6 (OBRIG-PROPOSTO)”. A frase está dentro de código Markdown (`CAPABILITIES.md`), sem alterar o sentido. |

## Violações (a)–(f)

- **(a) Nenhuma.** TODOs:4 direciona EXCLUDED/NÃO-REAL/FORA à seção 10; TODOs:651–658 os lista por classe. Nenhuma linha de checklist fora da seção 10 cita esses IDs como tarefa obrigatória. A menção em TODOs:299 diz que `meta-overlayfs` não está no pin; não pede sua implementação.
- **(b) Nenhuma.** Há 9 linhas `[x]` (TODOs:25–30,34,234,241); cada uma cita evidência ou aponta para a seção 1.1, que a cita em TODOs:25. O comando `test -e` confirmou os 8 caminhos distintos citados nas linhas 25–30, 34 e 234:

  ```text
  EXISTS docs/history/research/phase4/C03_stablekey_result.md
  EXISTS docs/history/research/phase4/C01_device_plan.md
  EXISTS [artefato local, fora do repo]/grok/c01-build/C01_ksu_boot_b.img
  EXISTS docs/history/research/phase4/c01_dmesg.txt
  EXISTS docs/ultraroot/REVIEW_vc40000_codex4.md
  EXISTS [fora do repo]/ultraroot/reskin-inc2b-vc32673.apk
  EXISTS docs/history/evidence/2026-10-11_install_vc40000.txt
  EXISTS docs/ultraroot/SP1_AUDIT_FIXES_codex4.md
  ```

- **(c) Nenhuma.** A varredura encontrou 60 IDs concretos no catálogo e 60 no TODO; `unknown=[]`. O token `MG-MOD-*` em TODOs:523 é padrão de grupo, não ID concreto.
- **(d) Nenhuma divergência de IMPL/VALID.** Comparação dos 44 pares explicitamente escritos no TODO com CAPABILITIES: `explicit_status_pairs=44 mismatches=[]`. Os oito OBRIG-PROPOSTO têm tarefas `[ ]`; TODOs:523 também usa `[ ]` para os três grupos citados, coerente com NOT_STARTED em CAPABILITIES.md:38–43,58,72. A legenda em TODOs:7–11 concorda com os marcadores `[x]`, `[~]` e `[!]` observados.
- **(e) Encontrada.** A legenda diz que `[DEVICE]` é necessário para tarefas presenciais (TODOs:10), mas há tarefas de mudança de autorização root sem a tag em capabilities que declaram `Device-gate: sim`: TODOs:64–69 marca MG-SU-GRANT como device-gated e TODOs:65 “Conceder root a app autorizado”, TODOs:67 “Revogar root” e TODOs:69 “Controlar root oferecido ao shell e ao ADB” não têm `[DEVICE]`; TODOs:245 declara o mesmo gate para KSU-ROOT, mas TODOs:247–248 pedem conceder/revogar root sem a tag; TODOs:347 declara `Device-gate: sim` para AP-ROOT, mas TODOs:348 pede “Conceder e revogar root a apps autorizados” sem a tag. Os passos explícitos de manager-grant em TODOs:36 e 242 já estão marcados `[DEVICE]`; as seis linhas acima mantêm o mesmo tipo de mudança de acesso sem o marcador.
- **(f) Nenhuma outra inconsistência interna encontrada.** A linha 8.2 corrigida em TODOs:589 já não diz que os IDs propostos faltam; as três linhas `[DEVICE]` de boot em TODOs:27,31,234 distinguem C0.1, C0.3 e o recorte KSU sem conflitar com o estado incompleto do log.

## Fora do denominador

Bullets excluídos do denominador: spec:244 — `MG-APP-STUB`, EXCLUDED porque reempacotar a identidade quebra a autorização pelo cert fixo C0.3; spec:426–427 — `KSU-META-OVFS`, NÃO-REAL porque OverlayFS/meta-overlayfs não está no pin; spec:428 — magic mount não é recurso do KSU deste pin, pertence a MG-MOD-MOUNT; spec:492 — `KSU-HWBP`, NÃO-REAL; spec:493 — `KSU-PHYSMEM`, NÃO-REAL e FORA por ocultação; spec:494 — `KSU-SYSCALL-API`, NÃO-REAL como API genérica (o hook interno de `su` continua em KSU-ROOT).

Itens fora do denominador que não são bullets incluídos na contagem: spec:430 — `KSU-DETECT-SURFACE`, FORA pelo motivo de reduzir detecção; spec:516 — `AP-SEL-BYPASS`, FORA por bypass de AVC; spec:580 — `AP-KPM-PART`, NÃO-REAL porque não há fonte no pin.

## Prova de sabotagem

Comandos executados e saída literal:

```text
$ python3 - <<'PY'
from pathlib import Path
source=Path('docs/ultraroot/TODOs.md')
dest=Path('docs/history/reports/todos-rodada3-sabotado.md')
phrase='Matriz de recuperação por device e por domínio'
lines=source.read_text(encoding='utf-8').splitlines(keepends=True)
hits=[i for i,line in enumerate(lines,1) if phrase in line]
if len(hits)!=1: raise SystemExit(f'expected one source line, got {hits}')
dest.write_text(''.join(line for line in lines if phrase not in line),encoding='utf-8')
print(f'copied={source} to={dest}; removed source_line={hits[0]}; matches=1')
PY
copied=docs/ultraroot/TODOs.md to=docs/history/reports/todos-rodada3-sabotado.md; removed source_line=577; matches=1

$ python3 docs/history/reports/round3_coverage.py
todo=docs/ultraroot/TODOs.md inventory_raw=440 excluded_bullets=7 inventory=433 capabilities=52 product_requirements=32 total=517
CAPABILITY_COBERTO=52
INVENTORY_COBERTO=433
SPEC_REQ_COBERTO=32
coverage=517/517=100.00%
matrix=docs/history/reports/TODOs_coverage_rodada3.tsv

$ python3 docs/history/reports/round3_coverage.py docs/history/reports/todos-rodada3-sabotado.md
todo=docs/history/reports/todos-rodada3-sabotado.md inventory_raw=440 excluded_bullets=7 inventory=433 capabilities=52 product_requirements=32 total=517
CAPABILITY_COBERTO=52
INVENTORY_COBERTO=433
SPEC_REQ_AUSENTE=1
SPEC_REQ_COBERTO=31
coverage=516/517=99.81%
matrix=docs/history/reports/TODOs_coverage_rodada3_sabotado.tsv
SABOTAGE_CHECK: spec:107 AUSENTE — Matriz de recuperação por device e por domínio; accusations=1
```

O check acusa a falta exata em spec:107 quando a linha TODOs:577 é removida.
