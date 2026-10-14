#!/bin/bash
# check_code_paths.sh: confere que os caminhos de código citados num documento existem no repo.
# Uso: bash tools/check_code_paths.sh [arquivo.md]   (padrão: docs/ultraroot/TODOs.md)
# Caminho de código: trecho entre crases com "/" e extensão de código, com ":linhas" opcional.
# Resolução: raiz do repo, depois ultraroot/. Depois de "Origem: KernelSU", resolve em kernel/upstream/KernelSU/.
# Depois de "Origem: Magisk" ou "Origem: APatch", o caminho é de repo externo não versionado: EXTERNO, não conta como falha.
# Caminho com "..." é elidido: ELIDIDO, não conta como falha.
# Sai com 1 se algum caminho não existir (FALTA).
set -u
cd "$(dirname "$0")/.." || exit 2
DOC="${1:-docs/ultraroot/TODOs.md}"
[ -f "$DOC" ] || { echo "arquivo não existe: $DOC"; exit 2; }
EXT='kt|kts|java|cc|cpp|c|h|sh|py|cmake|gradle|xml'
ok=0; falta=0; externo=0; elidido=0
while IFS= read -r line; do
  n="${line%%:*}"; text="${line#*:}"
  origem=""
  case "$text" in
    *"Origem: KernelSU"*) origem=KernelSU ;;
    *"Origem: Magisk"*) origem=Magisk ;;
    *"Origem: APatch"*) origem=APatch ;;
  esac
  after="${text#*Origem:}"
  while IFS= read -r tok; do
    [ -n "$tok" ] || continue
    p="${tok//\`/}"; p="${p%%:*}"
    if [[ "$p" == *...* ]]; then echo "ELIDIDO $DOC:$n $p"; elidido=$((elidido+1)); continue; fi
    up=0
    if [ -n "$origem" ] && [ "$after" != "$text" ] && printf '%s' "$after" | grep -qF "$tok"; then up=1; fi
    if [ "$up" = 1 ] && [ "$origem" = KernelSU ]; then
      if [ -e "kernel/upstream/KernelSU/$p" ] || [ -e "kernel/upstream/KernelSU/kernel/include/$p" ]; then
        echo "OK $DOC:$n $p (kernel/upstream/KernelSU)"; ok=$((ok+1))
      else echo "FALTA $DOC:$n $p (KernelSU)"; falta=$((falta+1)); fi
      continue
    fi
    if [ "$up" = 1 ]; then echo "EXTERNO $DOC:$n $p ($origem, não versionado)"; externo=$((externo+1)); continue; fi
    if [ -e "$p" ] || [ -e "ultraroot/$p" ]; then echo "OK $DOC:$n $p"; ok=$((ok+1))
    else echo "FALTA $DOC:$n $p"; falta=$((falta+1)); fi
  done < <(printf '%s\n' "$text" | grep -oE "\`[^\` ]*/[^\` ]*\.($EXT)(:[0-9-]+)?\`")
done < <(grep -n '' "$DOC")
echo "CODE-PATHS: ok=$ok falta=$falta externo=$externo elidido=$elidido"
[ "$falta" = 0 ]
