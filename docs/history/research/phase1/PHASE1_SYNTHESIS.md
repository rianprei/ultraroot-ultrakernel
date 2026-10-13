# SuperRoot — Fase 1: síntese correlacionada (maestro)

Data: 2026-10-10. Fontes independentes: `GROK_matriz_features.md` + `GROK_dossie.md` (RE com REA, evidência file:line), `CODEX_core_decision.md` (decisão aterrada no nosso build), `CLAUDE2_conflito_arquitetura.md` (DESCARTADO — 4 versões contraditórias, instável). Grok e Codex NÃO leram um ao outro: convergência é independente.

## 1. Convergência (os dois, por caminhos diferentes, apontam o mesmo)
- **A linha "combinável vs chassi" é real e nítida.** Grok (por RE) e Codex (por análise) concordam: o valor portável vive na camada de **contrato de módulo em userspace**; o **core de kernel é escolha única e exclusiva** — não se extrai o core de um motor e cola no outro.
- **Core recomendado por ambos: KernelSU.** Codex explícito (confiança Média). Grok implícito: só KSU/APatch têm os primitivos de core, e a superfície portável que sobra é a de módulo compartilhada KSU↔APatch.

## 2. Camada PORTÁVEL (o que o engine único hospeda sem brigar) — matriz do grok
Apenas 3 de 30 features são `PORTÁVEL`, e são o contrato de módulo:
- **#14** contrato `module.prop` + `/data/adb/modules` + estados disable/remove/update — igual em KSU `defs.rs` e APatch `defs.rs`.
- **#15** `webroot` + `action.sh` — mesmos nomes KSU e APatch.
- **#17** mount delegado a `metamount.sh`/`metainstall.sh` — nomes de script; efeito no script externo.
Conclusão: **o "SuperRoot" combinável = 1 core + 1 camada de compatibilidade de MÓDULOS** (roda módulos no formato Magisk/KSU). Não é fundir kernels.

## 3. Camada ACOPLADA (presa ao core; "não extrair" na matriz)
Tudo que é core — tracepoint/kretprobe, LKM+late-load, kpimg, ramdisk init, redirect de su, allowlist no kernel, daemon por socket, superkey, loader KPM, magic mount, zygisk, denylist/sulist, susfs — está marcado `ACOPLADA`/`AUSENTE`. São 25+ de 30. Ou seja: isso você **implementa/porta sob o core escolhido**, não copia.

## 4. Decisão do core — trade-off (Codex)
| Eixo | KernelSU | APatch/KernelPatch |
|---|---|---|
| Integração dado que TEMOS a árvore | **Baixo-médio** (LKM GKI; nosso `.config` já tem `KPROBES/EXT4_FS/MODULES`, `Module.symvers` exporta `register_kprobe`) | Baixo p/ 1ª aplicação se `kptools` aceitar a imagem; não precisa de árvore |
| Superfície p/ módulos estrangeiros | Boa p/ módulos Magisk/KSU; sem runtime KPM | APM (tipo-Magisk) + KPM (hooks kernel), mas Install persistente de KPM **não implementado** upstream |
| Risco de brick | Baixo-médio (LKM preserva o kernel) | Médio (altera o kernel na boot.img; 6.6 testado em Xiaomi/OnePlus, não no C75) |
| Zygisk-compat | Mais claro (ZygiskNext especifica KSU+ksud) | Mais incerto |

## 5. GATE antes de ser definitivo (não pular)
A recomendação é Média, não Alta, por 1 motivo concreto: **ainda não compilamos um KernelSU LKM contra a nossa árvore nem conferimos o KMI/ABID do `.ko` contra o kernel que roda no C75.** Codex confirma os exports-base, mas deixa o KMI completo como NÃO-VERIFICADO. Portanto o passo que fecha a decisão: build do LKM + checagem de KMI + boot-test — sob as mesmas travas do kernel (nunca slot A, backup, guard).

## 6. O que inverteria p/ APatch (da matriz do grok)
Se os módulos-alvo dependerem de hooks KPM difíceis de reimplementar, ou se o LKM do KSU falhar no KMI do C75, ou se ZygiskNext não cobrir os módulos prioritários no KSU — aí APatch vira o melhor candidato.

## 7. Descartado
`CLAUDE2_*`: 4 vereditos contraditórios (origem = workflow ultracode de 11 estágios reescrevendo o arquivo). Não entra na decisão.

## DECISÃO DO DONO (maestro não decide)
Core único: **KernelSU (recomendado, Média)** vs **APatch** vs **rodar o GATE do item 5 antes de cravar**.

---
## ADENDO — lacunas fechadas (grok rodada 2, 2026-10-10)
- **Magisk Alpha** (vvb2060 96221b69, vc 31000): decompilou, células preenchidas.
- **53/53** células NÃO-VERIFICÁVEL resolvidas → matriz exaustiva.
- **susfs**: só cliente userspace nos repos (Next `susfsd.rs`, Suki `cli.rs`); o lado-kernel é dependência externa (`susfs4ksu`, gitlab/simonpunk) e **NÃO é portável sem aplicar esse patch de kernel** — fica como opção futura, fora do core inicial.
- Ressalvas factuais: `webroot` ausente no Magisk/Alpha; feature KP #28 é uid, não assinatura; hash do rsuntk não extraído literal; bsxx #3 não chama `security_add_hooks`.
- **Conclusão inalterada:** core = KernelSU; camada combinável = contrato de módulo; resto reimplementa sob o core.
