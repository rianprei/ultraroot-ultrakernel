# Revisão de cobertura do TODOs.md — rodada 4

**VEREDITO: ACEITO.** Recontagem em fontes atuais: 517/517 = 100%, e não encontrei violações (a)–(f). Para (e), apliquei o critério desta rodada: `[DEVICE]` é obrigatório quando a checklist manda executar uma mudança no aparelho; descrição de capacidade do produto não é, por si só, uma execução.

## Comparação

A rodada 3 também mediu 517/517 = 100%, mas rejeitou por concessão/revogação de root sem `[DEVICE]` (relatório da rodada 3:3). Agora, as seis linhas foram marcadas e o checker não acusa falta no TODO atual. A cobertura permaneceu em 100%; a violação (e) fechou.

## Denominador e cobertura

Fontes e contagem direta:

| Fonte | Unidade | Obrigatórios |
|---|---|---:|
| Spec, inventário §§1–4, linhas 151–853 | 440 bullets `- ` menos as sete linhas excluídas abaixo | 433 |
| `CAPABILITIES.md` | 44 `OBRIG` + 8 `OBRIG-PROPOSTO` | 52 |
| Spec, requisitos de produto §§0–13 | 32 âncoras normativas | 32 |
| **Total** |  | **517** |

Contagem do inventário: spec:151–853 contém 440 bullets; removi spec:244, 426–428 e 492–494, conforme o critério da rodada. Os motivos estão no próprio spec:244 marca `MG-APP-STUB` EXCLUDED; spec:426–427 marcam `KSU-META-OVFS` NÃO-REAL; spec:428 atribui magic mount ao ID Magisk `MG-MOD-MOUNT`; spec:492–494 marcam `KSU-HWBP`, `KSU-PHYSMEM` e a API genérica de syscall como NÃO-REAL, com ocultação FORA em spec:493.

Recontagem dos requisitos gerais: spec:42,44,46,48,51–57,65–68,70,74,85,87,90–93,96,99,102,104,107,109,112,114,116 = 32. No catálogo, `CAPABILITIES.md:33–92` contém 52 linhas `OBRIG`/`OBRIG-PROPOSTO` (44 + 8).

Comando e saída da medição atual:

```text
$ python3 [fora do repo] docs/history/reports/round4_coverage.py
todo=docs/ultraroot/TODOs.md inventory_raw=440 excluded_bullets=7 inventory=433 capabilities=52 product_requirements=32 total=517
CAPABILITY_COBERTO=52
INVENTORY_COBERTO=433
SPEC_REQ_COBERTO=32
coverage=517/517=100.00%
matrix=[fora do repo] docs/history/reports/TODOs_coverage_rodada4.tsv
```

A matriz atual tem 518 linhas contando o cabeçalho, uma por unidade, e classifica as 517 como COBERTO. As âncoras gerais incluem a regra e a exceção de obrigatório (TODOs:4,658), o gate de aprovação (TODOs:16), as evidências C0.1/C0.3 e o manager-grant (TODOs:25–36), as seções de produto (TODOs:495–567), recuperação (TODOs:571–578) e conclusão/processo (TODOs:580–643). As linhas específicas de SuperKey e patch continuam cobertas: spec:525 → TODOs:362, “Configurar a SuperKey durante o patch da imagem”; spec:656 → TODOs:370, “requisitos de patch e aos símbolos necessários”.

## Fechamento e varredura de (e)

As seis linhas exigidas têm `[DEVICE]`, cada uma uma vez: TODOs:65 “Conceder root a app autorizado”; TODOs:67 “Revogar root”; TODOs:69 “Controlar root oferecido ao shell e ao ADB”; TODOs:247 “Conceder root pelo manager (allowlist)”; TODOs:248 “Revogar root pelo manager”; TODOs:348 “Conceder e revogar root a apps autorizados”. Saída literal de `rg -n -F` no TODO atual:

```text
65:- [ ] [DEVICE] Conceder root a app autorizado
67:- [ ] [DEVICE] Revogar root
69:- [ ] [DEVICE] Controlar root oferecido ao shell e ao ADB
247:- [ ] [DEVICE] Conceder root pelo manager (allowlist)
248:- [ ] [DEVICE] Revogar root pelo manager
348:- [ ] [DEVICE] Conceder e revogar root a apps autorizados
```

A varredura percorreu checkboxes `[ ]`/`[x]` em todos os blocos `Device-gate: sim` e na seção de recuperação. Resultado: 82 linhas candidatas a ação; classificações abaixo cobrem todas. **Execução/mutação do aparelho — OK, todas com `[DEVICE]`:**

| TODOs | Trecho | Decisão |
|---|---|---|
| 65 | “Conceder root a app autorizado” | OK — `[DEVICE]` |
| 67 | “Revogar root” | OK — `[DEVICE]` |
| 69 | “Controlar root oferecido ao shell e ao ADB” | OK — `[DEVICE]` |
| 219 | “Instalar a imagem corrigida” | OK — `[DEVICE]` |
| 222 | “Gravar partição” com confirmação | OK — `[DEVICE]` |
| 247–248 | Conceder/revogar root pelo manager | OK — ambas `[DEVICE]` |
| 348 | “Conceder e revogar root a apps autorizados” | OK — `[DEVICE]` |
| 572–577 | flash falho; módulo defeituoso no boot; interrupção; boot-loop; rollback de performance; matriz de recuperação | OK — as seis linhas têm `[DEVICE]` |

**Capacidades do produto — OK sem `[DEVICE]` pela regra definida nesta rodada; são requisitos de comportamento, não uma ordem para executar agora uma mudança física:**

| TODOs | Trechos encontrados | Decisão |
|---|---|---|
| 68, 70–71 | gerenciar/listar root e registrar estado de autorização | OK — interface/gestão requerida |
| 99–101, 104–105, 111, 114, 119, 123 | instalar/desinstalar/ativar módulo; `updateJson`; scripts; `system.prop`; `sepolicy.rule` | OK — capacidades do runtime |
| 142, 149, 151, 153, 155–156 | gerenciar Zygisk/lista, namespaces e CLI | OK — capacidades do adapter |
| 164–166, 182, 188–189, 199, 208–210 | operações CPIO/ramdisk, patch/policy, propriedades, módulos e contextos SELinux | OK — capacidades do adapter/CLI |
| 250, 252, 254, 259–260, 265, 271 | políticas KSU, ioctl reboot magic, lista e perfil de UID/GID/exposição | OK — interfaces/capacidades; não pede reboot agora |
| 280, 285, 295, 310, 312, 314, 322–323 | ciclo ZIP, callbacks, persistência, desativação e restauração de módulo/imagem | OK — funcionalidades; não executa agora a restauração |
| 349–350, 356, 365–366, 368–369, 374, 376, 378, 387, 397, 412 | controlar/administrar root; atualizar/restaurar componentes; gerir módulos/KPM; pré-requisitos e diagnóstico | OK — capacidades do APatch; não executa agora concessão/restauração |
| 438–442, 444, 449, 454–456, 473, 475 | aplicar/reverter controles, persistir com autorização, benchmark, rollback, guardrails, backup/dry-run/slot, segurança de update | OK — comportamentos e salvaguardas do produto |

Também conferi as operações explícitas fora desses blocos: TODOs:27 boot C0.1, :30 instalação do APK, :31 C0.3, :36 manager-grant, :37 coroação, :40–43 migração dos módulos, :234 boot KSU e :242 grant provado levam `[DEVICE]`. As linhas de gates TODOs:16–18 usam `[!]` e determinam confirmação presencial antes de operação.

## Violações (a)–(f)

- **(a) Nenhuma.** TODOs:4 diz que EXCLUDED/NÃO-REAL/FORA ficam fora do obrigatório, com motivo. TODOs:647–658 lista os excluídos; a tabela de F01–F30 mantém F18 como NÃO-REAL em TODOs:689. Menções como “meta-overlayfs não está no pin” em TODOs:299 são bloqueio/limite, não tarefa para implementar. Os casos FORA/NÃO-REAL fora das bullets do denominador estão explicitamente rotulados no spec:430 (`KSU-DETECT-SURFACE`), spec:516 (`AP-SEL-BYPASS`) e spec:580 (`AP-KPM-PART`).
- **(b) Nenhuma.** Nove `[x]`: TODOs:25–30,34,234,241. Os caminhos citados nas linhas 25–30,34,234 existem; TODOs:241 remete à evidência C0.3 da seção 1.1. `test -e` retornou `EXISTS` para os oito caminhos distintos: `C03_stablekey_result.md`, `C01_ksu_boot_b.img`, `C01_device_plan.md`, `c01_dmesg.txt`, `REVIEW_vc40000_codex4.md`, `reskin-inc2b-vc32673.apk`, `2026-10-11_install_vc40000.txt` e `SP1_AUDIT_FIXES_codex4.md`. A legenda TODOs:7 e :11 exige evidência citada para `[x]`.
- **(c) Nenhuma.** A varredura comparou IDs concretos do TODO com a tabela do catálogo: zero desconhecidos. `MG-MOD-*` em TODOs:523 é padrão de grupo, não ID concreto. `CAPABILITIES.md:33–92` mantém os IDs citados, inclusive os que são deliberadamente NÃO-REAL/FORA/EXCL.
- **(d) Nenhuma.** Os 44 pares IMPL/VALID explicitados no TODO coincidem com o catálogo: `mismatches=[]`. Os seeds também coincidem: CAPABILITIES.md:9 `KSU-core` `IMPLEMENTED / DEVICE_TESTED` ↔ TODOs:233; CAPABILITIES.md:10 `manager-grant` `IMPLEMENTED / NOT_TESTED` ↔ TODOs:240. Os oito OBRIG-PROPOSTO têm status inicial `NOT_STARTED / NOT_TESTED` no catálogo (CAPABILITIES.md:85–94) e as checklists correspondentes usam somente `[ ]` (TODOs:495–567; `UR-CLI` está coberto por TODOs:509). TODOs:523 mantém `[ ]` para os grupos de metamódulos. A legenda TODOs:7 mapeia `[ ]` a NOT_STARTED, `[~]` a em andamento/sem validação, `[x]` a validado com evidência e `[!]` a bloqueio; não encontrei divergência de marcador.
- **(e) Nenhuma.** As seis linhas fechadas acima e as operações/recovery enumeradas na varredura estão marcadas `[DEVICE]`. Não considerei “Instalar módulo a partir de ZIP” ou outros verbos em requisitos funcionais como execução presente; isso segue o critério fornecido.
- **(f) Nenhuma contradição interna encontrada.** TODOs:26 chama C0.1 “obsoleto como alvo operacional”, enquanto TODOs:27 documenta o boot C0.1 como recorte histórico e mantém UNKNOWN explícito para 429 módulos/boot inteiro; não são afirmações concorrentes sobre o alvo atual. TODOs:31 mantém C0.3 `[~]` por falta de log. TODOs:25/34 registram evidência host; TODOs:36/242 mantêm o manager-grant no device como bloqueado/não provado. Os estados e ressalvas não conflitam.

## Prova de sabotagem

Copiei o TODO e removi apenas `[DEVICE]` da linha TODOs:248. O checker passa no original (6/6) e acusa a ausência exata na cópia:

```text
$ python3 - <<'PY'
from pathlib import Path
src=Path('docs/ultraroot/TODOs.md')
dst=Path('[fora do repo] docs/history/reports/todos-rodada4-sabotado.md')
phrase='Revogar root pelo manager'
lines=src.read_text(encoding='utf-8').splitlines(keepends=True)
hits=[i for i,line in enumerate(lines) if phrase in line]
if len(hits)!=1: raise SystemExit(f'expected one matching line, found {len(hits)}')
i=hits[0]
if '[DEVICE]' not in lines[i]: raise SystemExit(f'source row lacks DEVICE at line {i+1}')
lines[i]=lines[i].replace('[DEVICE] ', '', 1)
dst.write_text(''.join(lines),encoding='utf-8')
print(f'copied={src} to={dst}; removed=[DEVICE] from line={i+1}; matches=1')
PY
copied=docs/ultraroot/TODOs.md to=[fora do repo] docs/history/reports/todos-rodada4-sabotado.md; removed=[DEVICE] from line=248; matches=1

$ python3 [fora do repo] docs/history/reports/check_round4_device_tags.py docs/ultraroot/TODOs.md
CHECK TODOs:65 Conceder root a app autorizado: DEVICE=true
CHECK TODOs:67 Revogar root: DEVICE=true
CHECK TODOs:69 Controlar root oferecido ao shell e ao ADB: DEVICE=true
CHECK TODOs:247 Conceder root pelo manager (allowlist): DEVICE=true
CHECK TODOs:248 Revogar root pelo manager: DEVICE=true
CHECK TODOs:348 Conceder e revogar root a apps autorizados: DEVICE=true
EXPECTED_DEVICE_ROWS=6; missing=0

$ python3 [fora do repo] docs/history/reports/check_round4_device_tags.py [fora do repo] docs/history/reports/todos-rodada4-sabotado.md
CHECK TODOs:65 Conceder root a app autorizado: DEVICE=true
CHECK TODOs:67 Revogar root: DEVICE=true
CHECK TODOs:69 Controlar root oferecido ao shell e ao ADB: DEVICE=true
CHECK TODOs:247 Conceder root pelo manager (allowlist): DEVICE=true
CHECK TODOs:248 Revogar root pelo manager: DEVICE=false
CHECK TODOs:348 Conceder e revogar root a apps autorizados: DEVICE=true
VIOLATION (e): TODOs:248 "Revogar root pelo manager" sem [DEVICE] — - [ ] Revogar root pelo manager
EXPECTED_DEVICE_ROWS=6; missing=1
```

Sem adb, fastboot ou interação com aparelho nesta revisão.
