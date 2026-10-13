# Revisão adversarial — blueprint e decisão de core

Revisor: Grok. Data: 2026-10-10. Alvos lidos: `PHASE2_BLUEPRINT.md`, `CODEX_core_decision.md`. Evidência cruzada: `GROK_matriz_features.md` (rodada 2, 0 células NÃO-VERIFICÁVEL) e `tools/gate_kmi_crc.sh` de `kernel/lake/tools/gate_kmi_crc.sh`. Não alterei os alvos. Não compilei e não falei com o aparelho.

Veredito: o core KernelSU se sustenta para o baseline escrito. O plano não está pronto para rodar o G1 como gate. O PASS do G1, do jeito que está escrito, aceita um `.ko` que não encaixa no 6.6.89 que o C75 carrega.

## Caça 1 — KernelSU × APatch

Nenhuma feature do baseline é exclusiva do APatch/KPM a ponto de inviabilizar o KernelSU.

O baseline que o blueprint trata como contrato portável é #14, #15 e #17. Na matriz as três existem no KSU oficial. O que só o KernelPatch entrega, e o KSU marca AUSENTE, é #5 (`kpimg`), #13 (`auth_superkey`), #19 (loader KPM real) e #23 (uid-exclude). O blueprint exclui #5, não importa o SuperCall de #13, e manda reabrir a escolha do core se um comportamento KPM for essencial e não couber no LKM (#19, G8). Essa condição é o ponto certo de inversão. Ela não está ativa: o baseline não pede binário KPM.

#25 e #26 não empurram para o APatch. O cliente susfs está em Next/Suki e o efeito depende do patch externo `https://gitlab.com/simonpunk/susfs4ksu`. O KSU oficial e o KernelPatch estão AUSENTE na #26. Se ocultação susfs for requisito, falta patch de kernel, não troca para APatch.

## Achados

### 1. O PASS do G1 não é o `gate_kmi_crc.sh` — severidade alta

Evidência. O script recebe o symvers de um kernel novo e compara o CRC que os 557 `.ko` de vendor/GKI exigem do vmlinux. Uso na linha 16: `gate_kmi_crc.sh <symvers of the NEW build>`. O PASS nas linhas 99–101 exige `mismatches=0`, `missing_exports=0`, `conflicting_crcs=0`, `export_type_mismatches=0`, `namespace_mismatches=0`, `unresolved=0`. Não há argumento para um `.ko` novo. `vermagic` aparece só como coluna do inventário, no comentário da linha 12, e não entra no PASS.

O blueprint, linhas 140–142, manda “aplicar o estilo” desse gate ao `kernelsu.ko` e trata CRC/KMI do `.ko` como o PASS. Um PASS do script real diz que o vmlinux ainda alimenta os módulos que já estão no aparelho. Diz zero sobre os indefinidos do `kernelsu.ko`.

Correção. Separar os dois testes. Manter o script como gate do kernel, se um dia o kernel for reconstruído. Para o G1, extrair os indefinidos do `.ko` (`nm -u` / `__versions`) e comparar cada um com o `Module.symvers` do mesmo artefato pinado em G0: CRC, `export_type` e namespace. Falhar se o symvers usado não for o do vmlinux cujo `vermagic` será o do boot. Proibir registrar PASS do script de vendor como PASS do `.ko`.

### 2. O G1 deixa passar o hook que o core realmente usa — severidade alta

Evidência. A matriz marca #1 ACOPLADA no KSU: tracepoint `sys_enter` + kretprobe. O corpo está em `KernelSU/kernel/hook/syscall_hook_manager.c:134-135` (`init_kretprobe("syscall_regfunc"` e `"syscall_unregfunc"`) e `:146` `register_trace_prio_sys_enter(...)`. Os alvos do kretprobe são strings resolvidas em runtime. Não são imports do `.ko`. O `Kconfig:5` do mesmo tree só exige `KPROBES && EXT4_FS`. O PASS do G1 (blueprint linha 142) repete `KPROBES`, `EXT4_FS` e `MODULES`.

Um `.ko` com modpost limpo e CRC certo passa com `syscall_regfunc` ausente, na blacklist de kprobe, ou com o tracepoint de syscall desligado. O módulo “encaixa” no link e o su não ganha o hook.

Correção. Incluir no PASS do G1 a lista de nomes que o `.ko` pinado passa a `register_kretprobe` / tracepoint, cada um presente no `kallsyms` ou `System.map` do mesmo vmlinux, e os configs que esse commit realmente usa para esse caminho (`KRETPROBES` e o tracepoint de syscall), além do `depends on` do Kconfig. `register_kprobe` exportado continua insuficiente, como o próprio G1 já diz.

### 3. vermagic e trust não amarram o `.ko` ao kernel do aparelho — severidade alta

Evidência. O G0/G1 comparam artefatos do build local. A decisão de core (`CODEX_core_decision.md:31`) já diz que isso não prova que o artefato local é o kernel do telefone. O gate de CRC do projeto também não compara vermagic. O blueprint linha 142 pede “vermagic/versão/arquitetura correspondem” e “assinatura/trust exigida pelo alvo está resolvida”, sem dizer qual string e qual chave.

Isso deixa passar um `.ko` linkado contra o symvers do lake-build quando a imagem do C75 tem outro banner de vermagic, e um `.ko` assinado por uma chave que não está no keyring do kernel que vai dar `insmod` (`MODULE_SIG_FORCE` / `CONFIG_SYSTEM_TRUSTED_KEYS`). “Trust resolvida” cabe numa assinatura qualquer.

Correção. O PASS só fecha se `vermagic` do `.ko` for igual ao vermagic do vmlinux/boot de referência do C75 (ou do artefato que G4 vai gravar, se a decisão for trocar o kernel — e hoje a decisão é não trocar). A chave que assina o `.ko` tem de ser uma chave que esse vmlinux confia. Guardar os dois no manifest do G0.

### 4. Dois donos do su — severidade média

Evidência. L1 (blueprint linha 50) “é responsável por conceder a autoridade root”. L3 (linha 66) diz que o `ksud` é o único dono privilegiado da policy. L5 aplica a mesma policy “pelo daemon e pelo core”. A matriz marca #8 e #9 ACOPLADAS: o redirect e a allowlist estão no kernel (`sucompat.c` do KSU). O blueprint linha 92 marca #8 como PORT e fala em “um único entrypoint SuperRoot”.

Quem nega o `su` no aparelho é o kernel, com a allowlist. Um segundo redirect no userspace, ou uma policy da UI que discorde do que foi propagado ao `.ko`, cria dois vereditos. A UI já está proibida de escrever o kernel (linha 121); o desenho das camadas não escolhe o único escritor.

Correção. Um escritor: o daemon. O kernel só aplica a allowlist que o daemon commitou. Marcar #8 como CORE do commit KSU pinado. Não acrescentar outro redirect. Se a propagação ao kernel falhar, o resultado é deny, e a UI mostra a policy que o kernel confirmou, não a que o app gravou por último.

### 5. Dois donos do mount, e o sucesso vazio — severidade média

Evidência. L2 (linhas 29–30 e 55–59) põe o provedor de mounts no SuperRoot e trata `metamount.sh` como delegação. A matriz marca #16 AUSENTE no KSU e ACOPLADA só no Magisk (tmpfs, bind, `.replace`). #17 é PORTÁVEL como nome de script. #21 é ACOPLADA: o umount por uid é do kernel. No KSU pinado, `userspace/ksud/src/metamodule.rs:254-256` faz `exec_mount_script` devolver `Ok(())` quando `metamount.sh` não existe.

Um módulo Magisk cujo efeito é magic mount, e que não traz `metamount.sh`, passa em #14/#17 com mount nenhum. O umount do kernel (#21) ainda pode desmontar o que o planner novo da #16 montar para o mesmo uid. L2 e L1 mandam no mesmo diretório.

Correção. Um dono do efeito de mount: o planner do SuperRoot. `metamount.sh` só roda se o módulo declara esse contrato; ausência não é sucesso de mount. `.replace` fica fora até o planner ter teste próprio (#16). O umount por uid só lista mounts que esse planner registrou. G6 falha se o módulo prometeu mount e a visão do uid não mudou.

### 6. #15 promete webroot ao Magisk — severidade média

Evidência. A coluna “Melhor origem” da matriz, feature 15: webroot e `action.sh` na família KSU e no APatch; Magisk e Alpha só `action.sh`. A busca por `webroot` nos trees Magisk e Alpha deu zero. O blueprint linha 99 marca #15 ADAPT com confiança alta, “nomes compartilhados KSU/APatch”, e a linha 29 mete `webroot` na compatibilidade Magisk/KSU. A linha 202 resume #14/#15/#17 como os contratos portáveis, no singular.

`action.sh` é realista nos dois. WebUI via `webroot` é contrato KSU/APatch. Um módulo Magisk que só tenha WebUI nesse diretório não é servido pelo código Magisk que a matriz leu. Instalação também diverge: #6 e #7 são cadeias de boot diferentes; #16 não existe no KSU. A frase da L2 (linha 59) que recusa recovery, API interna e `.replace` está certa, e convive com o resumo que trata a superfície como uma só.

Correção. Partir #15: `action.sh` entra na compat Magisk; `webroot` entra só para módulo que declara o contrato KSU/APatch, com servidor no manager, não como herança Magisk. O resumo da linha 202 deixa de dizer que as três linhas cobrem Magisk.

### 7. Write irreversível sem “sim”, e o slot B pode ser o slot vivo — severidade alta

Evidência. O item 9 (linha 196) exige `sim` para flash/write, alteração do slot B, reboot que mude o slot, concessão real de root e rollback que escreva partição. G3 (linhas 150–154) fixa o destino em B e recusa A sempre. G4 (linhas 156–160) instala em B. Não há teste de que B está inativo. Se o C75 já bootou de B, o `sim` grava o slot que está rodando e o A “intocado” pode não ser o boot bom.

Fora da lista, com efeito irreversível no aparelho: `insmod` do `kernelsu.ko` no kernel vivo; a primeira execução de `action.sh`, `post-fs-data.sh` ou `metamount.sh` (código de root, capaz de escrever partição); a migração única do `magisk.db` (linha 70), que grava a policy autoritativa. O item 10 chama a migração de decisão do dono e não de `sim` literal.

Correção. O destino do flash é o slot inativo, recusado se for o slot atual, com A ou B pelo nome que o bootloader reportar. `sim` separado, na mesma frase da operação, para: gravar esse slot, `insmod` no kernel vivo, executar script de módulo no device, migrar o `magisk.db`, e qualquer rollback. Backup e `fastboot_guard` continuam pré-condição, não substituto.

### 8. O blueprint ainda espera lacunas que já fecharam — severidade baixa

Evidência. Linha 87: “bsxx não verificável”. Linhas 109, 116 e 191: Zygisk/KPM/SUSFS e “células não verificáveis” ainda dependeriam do fechamento. Linha 201 repete a tag NÃO-VERIFICÁVEL como estado da matriz. A grade depois da rodada 2 tem 0 células nessa tag (270 = 86 ACOPLADA, 148 AUSENTE, 36 PORTÁVEL). #3 no KSU oficial segue AUSENTE; bsxx e rsuntk têm hook de setuid acoplado ao kernel deles, e o EXCLUIR desse hook no core tiann continua correto. #25 é ACOPLADA ao kernel externo; #26 é AUSENTE nos clones. #20 Zygisk segue AUSENTE fora do Magisk.

Correção. Trocar “aguarda lacunas” por esses vereditos. G8 não fica bloqueado à espera de uma matriz. Fica bloqueado à lista do dono, com susfs explícito como patch de kernel externo e KPM explícito como “reabre o core se for essencial”.

## O que não é achado

A escolha KernelSU LKM para este baseline, com a árvore 6.6.89 já buildada, não é contradita pela matriz. Zygisk (#20) e magic mount (#16) são do Magisk e o blueprint já os tira do core. O G1, como experimento de compilação local, não escreve no aparelho. O problema é usar o PASS escrito como prova de encaixe.

## Veredito

Ajustar antes de rodar o G1 como gate: achados 1, 2 e 3 (o critério de PASS). Sem esse ajuste, um PASS não garante que o `kernelsu.ko` encaixa no 6.6.89 do C75.

Antes de G3/G4/G6, e sem bloquear a compilação local: achados 4, 5, 6 e 7. O achado 8 é correção de texto; não inverte o core.

Pronto para G1: não, até o PASS ser o teste do `.ko` contra o vmlinux de referência, com alvos de hook, vermagic e chave. Pronto para escolher KernelSU como core do baseline: sim, com a ressalva já escrita no G8 para KPM e com susfs fora desse baseline.
