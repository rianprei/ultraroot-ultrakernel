# Fase 2 — ajustes pós-revisão cruzada (maestro, 2026-10-10)

Reviews: `reviews/REVIEW_blueprint_por_grok.md` (8 achados), `reviews/REVIEW_matriz_por_codex.md` (6 achados). **Core KernelSU CONFIRMADO pelos dois** — nenhum achado troca o core.

## A. G1 REDEFINIDO (o PASS antigo era inválido — grok 1,2,3, severidade alta)
O G1 antigo reusava `gate_kmi_crc.sh` (valida vmlinux↔corpus de 557 .ko), que NÃO inspeciona o .ko novo. G1 passa a ser, em duas etapas:

**G1a — viabilidade ESTÁTICA (sem build, sem device):** contra a árvore pinada `~/lake-build/src/common` + `Module.symvers` + `System.map`/vermagic de referência do C75:
1. Todo símbolo de kernel que o SOURCE do KernelSU (commit pinado) chama resolve no nosso `Module.symvers` (CRC/export_type/namespace batendo).
2. Todo nome de alvo de kretprobe/tracepoint que o KSU registra (ex.: `syscall_regfunc`/`syscall_unregfunc`, `sys_enter`) existe no `System.map`/kallsyms do nosso vmlinux, e os CONFIG que esse caminho usa (`KPROBES`,`KRETPROBES`, tracepoint de syscall, `EXT4_FS`,`MODULES`) estão `=y` no nosso `.config`.
3. A string de vermagic que o KSU produziria == vermagic do vmlinux/boot de referência do C75.
4. Postura de assinatura: registrar `MODULE_SIG_FORCE`/`CONFIG_SYSTEM_TRUSTED_KEYS` do nosso kernel e qual chave o `.ko` precisaria pra dar `insmod`.
PASS G1a = os 4 batem. FALHA = lista o que falta (então nem compila).

**G1b — build + inspeção do .ko (só se G1a PASS):** compilar `kernelsu.ko` via DDK pinado; `nm -u`/`__versions` do .ko resolvem todos no `Module.symvers`; vermagic do .ko == referência; chave confiada. Local, sem device, sem `insmod`.

## B. Catálogo da matriz (codex 1-6 / grok 8) — corrigir antes de usar como fonte
- #4: separar "late-load pid!=1 do KSU" de "artefato .ko do KernelPatch" (este = NÃO-VERIFICADO).
- #8: coluna KP volta a NÃO-VERIFICÁVEL (só macros de path lidas, não o corpo do redirect) — e, no core, #8 é CORE do commit KSU, não PORT.
- #14: PORTÁVEL só o layout/marcadores de arquivo; parsing+ciclo de vida separados.
- #15: dividir — `action.sh` = compat Magisk; `webroot` = só contrato KSU/APatch (ausente no Magisk/Alpha).
- #30 (`libmagiskboot.so`): candidato, não vencedor (Ghidra deu timeout).
- Lacunas de CAPACIDADE a declarar explícito: OTA/A-B, persistência, e **Play Integrity fora das garantias do core**.

## C. Donos únicos (grok 4,5 — p/ G3+, não bloqueia G1)
- **su:** um escritor = o daemon; kernel só aplica a allowlist commitada; falha de propagação = deny. Sem segundo redirect em userspace.
- **mount:** um dono = o planner do SuperRoot; `metamount.sh` só roda se o módulo declara; ausência ≠ sucesso; umount por uid só lista o que o planner registrou.

## D. Autorização do dono — EXPANDIR (grok 7, aceito em parte)
Exigir "sim" literal, na frase da operação, para cada um: gravar slot, **`insmod` no kernel vivo**, **executar script de módulo no device**, **migrar `magisk.db`**, qualquer rollback. Backup + `fastboot_guard` continuam pré-condição.

## E. REJEITADO (grok 7, parte do slot)
"Flashar o slot inativo" — **NÃO** neste device. Invariante mantida: **slot B (ativo), NUNCA slot A** (A = OS antigo OS3.0.20.0; trocar pra A boota OS velho sobre dados novos — RE4 D3). Nosso modelo T3 grava boot_b no lugar, sob backup+guard+recuperação por teclas. O risco residual (sem fallback em A) é aceito e coberto pelo backup, não por tocar o A.

## F. Texto (grok 8, baixa): blueprint ainda diz "aguarda lacunas" — já fecharam (0 NÃO-VERIFICÁVEL após rodada 2). Trocar por vereditos; G8 bloqueia só na lista do dono (KPM reabre core se essencial; susfs = patch de kernel externo, fora do baseline).

## G. RESULTADO DO G1a (2026-10-10): BLOQUEADO — LKM morto no stock
KernelSU como LKM out-of-tree NÃO encaixa: 6 símbolos (kallsyms_lookup_name, kallsyms_lookup, kallsyms_lookup_size_offset, filp_open, commit_creds, __tracepoint_sys_enter) presentes no System.map mas NÃO exportados (TRIM_UNUSED_KSYMS=y); kernel_read/kernel_write em namespace protegido + MODULE_SIG_PROTECT=y rejeitam unsigned; vermagic diverge (UTS_RELEASE local != referência). Config e alvos de hook: PASS. Relatório: G1a_feasibility.md.
**Consequência:** o modo LKM está descartado. Reabre integração:
 (1) KernelSU SOURCE-INTEGRATED (rebuild do kernel com KSU in-tree; símbolos diretos; nossas travas) — vira "kernel custom + app";
 (2) APatch (patcha boot.img stock; resolve símbolos via kpimg/KALLSYMS_ALL; feito pra GKI stock) — patch de imagem, lacuna KPM.
Decisão do DONO pendente. Core KernelSU permanece válido SE for no modo integrado.

## H. DECISÃO FECHADA (2026-10-10, maestro + grok review + codex adversarial)
**Core = KernelSU, modo SOURCE-INTEGRATED (CONFIG_KSU=y, in-tree).** LKM descartado (G1a). APatch descartado (só ganharia se byte-identidade/KPM fossem requisito duro; o requisito do dono é features+inbrickável → controle do build vence).
Caveats aceitos: (a) "plausível, não validado" até o build Kleaf linkar; (b) perde-se "GKI byte-idêntico" — vira kernel custom derivado, esperado.
**Gate NOVO p/ vmlinux modificado** (codex): CONFIG_KSU=y presente + KSU no vmlinux; gate_kmi_crc dos 557 ainda PASS; diff de ABI/KMI+config vs baseline justificado; artefato testado == empacotado; dmesg-accept cobre init do KSU + registro real dos hooks. gate_kmi_crc agora prova "compat de imports do corpus", não "GKI intacto".
**Próximo passo = G1b-integrado** (build de validação, local, sem device): worktree descartável, fiar KSU no Kconfig/Kbuild, forçar CONFIG_KSU=y, Kleaf até linkar vmlinux, confirmar KSU no vmlinux, gate_kmi_crc vs 557. PASS = integração validada; BLOCKED = achou o catch do Kleaf.
