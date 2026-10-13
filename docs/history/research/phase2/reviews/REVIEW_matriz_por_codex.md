# Revisão adversarial da matriz do Grok

## Escopo

Confrontei `GROK_matriz_features.md`, `GROK_dossie.md` e `GROK_gaps_closed.md` da Fase 1. O parecer revisa esses artefatos do Grok; não usa a decisão anterior do Codex nem arquivos `CLAUDE2_*.md`. A revisão foi documental e read-only: não reexecutei REA, não compilei e não acessei dispositivo.

## Achados

1. **Média — item #4 mistura late-load do KSU com o artefato do KernelPatch.** A linha da matriz chama a feature de “LKM + late load (`pid != 1`)” e classifica APatch/KernelPatch como `ACOPLADA` (`GROK_matriz_features.md:20`). Porém, o texto explicativo diz que o `kernelpatch.ko` da release 0.13.9 não foi rastreado e que o flag `pid != 1` do KSU não se aplica ao KernelPatch (`GROK_matriz_features.md:64-66`). A célula afirma mais do que a evidência: não demonstra late-load nem o comportamento desse `.ko`. **Correção:** separar “módulo KernelSU com late-load `pid != 1`” de “artefato `.ko` do KernelPatch”; deixar a segunda capacidade como não verificada até examinar a release, e não agregá-la à primeira.

2. **Média — item #8 fecha como verificado o redirect do KernelPatch sem ler o corpo decisivo.** A matriz classifica o redirect de `/system/bin/su` como `ACOPLADA` em todos os motores (`GROK_matriz_features.md:24`). Para KP, ela própria diz que só confirmou as macros `SU_PATH`, `LEGACY_SU_PATH`, `APD_PATH` e o supercall; o corpo que reescreve o exec não foi citado (`GROK_matriz_features.md:86`). As macros estabelecem paths, não provam o redirecionamento. Pela definição da tabela — `NÃO-VERIFICÁVEL` quando o corpo que fecha o veredito não foi lido (`GROK_matriz_features.md:3`) — a célula KP não deveria estar fechada como `ACOPLADA`. **Correção:** marcar o redirect KP como `NÃO-VERIFICÁVEL` até ler esse fluxo, ou restringir a célula ao contrato de paths comprovado.

3. **Média — item #14 estende evidência de nomes de arquivo a compatibilidade do ciclo de vida.** A matriz marca `module.prop + disable/remove/update` como `PORTÁVEL` nos nove motores (`GROK_matriz_features.md:30`). Para KSU e APatch, a evidência citada registra diretórios e nomes em `defs.rs`; para Magisk, registra o diretório de módulos (`GROK_matriz_features.md:108-116`, `GROK_dossie.md:201-213`). O corpo do parser `read_module_prop` não foi citado, e os artefatos não demonstram que leitura de metadados e efeitos de ativar, atualizar ou remover tenham o mesmo contrato operacional em todos. **Correção:** limitar `PORTÁVEL` ao layout e aos marcadores de arquivo; separar parsing e execução do ciclo de vida, ou juntar evidência de cada consumidor antes de chamá-los compatíveis.

4. **Média — item #15 dá um único rótulo a duas capacidades com cobertura diferente.** `webroot + action.sh` aparece como `PORTÁVEL` em todas as células (`GROK_matriz_features.md:31`), mas a evidência e o resumo confirmam que Magisk e Alpha têm `action.sh` e não têm `webroot` (`GROK_matriz_features.md:114,190`; `GROK_gaps_closed.md:22-23,77,125`). **Correção:** dividir a linha em `webroot` e execução de `action.sh`, ou identificar explicitamente as células Magisk/Alpha como parciais; `action.sh` pode continuar portátil nelas.

5. **Média — item #30 e sua origem preferida estão provisórios, não demonstrados.** A grade marca `libmagiskboot.so` como origem e `PORTÁVEL` para Magisk/Alpha (`GROK_matriz_features.md:46,176-178`). O suporte descrito são strings `ANDROID!` e `truncate`; a análise no Ghidra expirou sem xref. O próprio dossiê chama o binário de candidato a utilitário e registra o timeout (`GROK_dossie.md:315,321-323`). Isso não prova quais operações de imagem são oferecidas nem que a ferramenta seja separável para o uso proposto. **Correção:** preservar `libmagiskboot.so` como candidato de origem, não vencedor confirmado; rotular as células como provisórias/não verificadas até rastrear operações de unpack/patch/repack. Não encontrei outra origem comprovadamente melhor nos artefatos revisados.

6. **Alta para qualquer promessa de “root capaz de tudo” — faltam capacidades de OTA/A-B e persistência no mapa.** A grade cobre ramdisk, `ksuinit`/`.ko` e ferramentas de boot image (#6, #7 e #30), mas não modela cobertura de boot stages, troca de slot, sobrevivência a OTA nem rollback (`GROK_matriz_features.md:22-23,46`; `GROK_dossie.md:227,306-317`). Play Integrity/atestado remoto também não é coberto e não pode ser inferido de um APK de root. **Correção:** adicionar uma seção explícita de cobertura e limites para OTA/A-B/persistência e declarar Play Integrity fora das garantias do core; sem essa delimitação, a matriz não sustenta a alegação ampla de “tudo”.

## Itens de caça do contrato

1. **Classificação PORTÁVEL incorreta:** achados #4 e #5 acima; #15 é parcial. **#17: nenhum achado** na classificação do contrato de chamada. A matriz e o dossiê dizem expressamente que só o chamador `metamount.sh` é portátil e que o efeito depende de script externo; sem script, o chamador não monta nada (`GROK_matriz_features.md:122-124`). A ressalva deve permanecer visível no produto.
2. **Células fechadas sem corpo suficiente:** achados #1, #2, #3 e #5. A declaração “53/53 fechadas” não deve impedir reabrir as células específicas cuja própria evidência permanece indireta (`GROK_gaps_closed.md:3`).
3. **Capacidade ausente necessária para “1 APK capaz de tudo”:** achado #6, especialmente OTA/A-B e persistência; Play Integrity deve ser declarado como fora das garantias.
4. **Coluna “melhor origem”:** nenhum caso comprovado de motor alternativo superior nos artefatos. Para #30, `libmagiskboot.so` é apenas candidato, conforme achado #5.
5. **Muda a decisão do core KernelSU? Não.** Nenhum achado mostra incompatibilidade estrutural do core escolhido. Os problemas alteram a fronteira de portabilidade, a confiança em algumas células e o escopo de capacidades, não a escolha do mecanismo de core.

## Veredito

A matriz **não deve seguir para a Fase 2 sem ajustes**: corrigir/separar #4, reabrir a evidência de KP em #8, delimitar #14, separar as duas capacidades em #15, rebaixar #30 a candidato e explicitar a cobertura OTA/A-B/persistência e o limite de Play Integrity. Com esses ajustes, os achados não exigem trocar o core KernelSU.

## Bloco final

STATUS: DONE
ACHADOS: 6 concretos
MUDA_CORE: não — nenhum achado demonstra incompatibilidade estrutural do core KernelSU escolhido
AJUSTE_NECESSARIO: corrigir/separar #4; reabrir #8 KP; limitar #14 ao contrato de arquivos; dividir #15; tratar #30 como candidato; delimitar OTA/A-B/persistência e Play Integrity
