#!/usr/bin/env bash
# Verificador do repo. Sai com 1 se qualquer checagem falhar. Imprime só arquivo:linha, nunca o valor achado.
cd "$(dirname "$0")/.." || exit 1
FAIL=0
fail() { echo "FALHA: $*"; FAIL=1; }

FILES=$(find . -path ./.git -prune -o -path ./build -prune -o -type f -print | sed 's#^\./##' | sort)
TEXT=$(for f in $FILES; do grep -Iq . "$f" 2>/dev/null && echo "$f"; done)

# V1: arquivos proibidos (chave, binário, saída de build, log, dado de aparelho)
for f in $FILES; do
  case "$f" in
    ultraroot/gradle/wrapper/gradle-wrapper.jar) ;;
    *.jks|*.keystore|*.p12|*.pem|*.key|*.pass|*.apk|*.aab|*.img|*.so|*.o|*.dex|*.ap_|*.jar|*.log) fail "V1 arquivo proibido: $f" ;;
    keys/*|backups/*|Magisk/*|out/*|*/build/*|*/.gradle/*|*/.kotlin/*|*/.cxx/*|*/captures/*|*/.idea/*) fail "V1 pasta proibida: $f" ;;
  esac
done

# V2: segredos (só file:line; o valor nunca é impresso)
SECRET='gh[pousr]_[A-Za-z0-9]{20,}|github_pat_[A-Za-z0-9_]{20,}|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|sk-[A-Za-z0-9]{20,}|-----BEGIN [A-Z ]*PRIVATE KEY-----|(storepass|keypass|password|senha)[[:space:]]*[:=][[:space:]]*[^[:space:]]{4,}'
for f in $TEXT; do
  [ "$f" = tools/verify_repo.sh ] && continue
  if grep -qE "$SECRET" "$f"; then fail "V2 segredo: $f:$(grep -nE "$SECRET" "$f" | head -n 1 | cut -d: -f1)"; fi
done

# V3: dado pessoal e caminho local. Fora da checagem: o próprio verificador, registros, regras escritas e pin upstream.
PII='[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[a-z]{2,}|(^|[^A-Za-z])(serial|Serial)[ :=]+[A-Za-z]*[0-9][A-Za-z0-9-]{4,}|adb -s [A-Za-z0-9]{8,}|\b192\.168\.[0-9]+\.[0-9]+|\b10\.[0-9]+\.[0-9]+\.[0-9]+\b|([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}|[Ii][Mm][Ee][Ii][^0-9]{0,12}[0-9]{15}'
LOCAL='/home/rianprei|~/superroot|~/Documentos|superroot/scratch|Documentos/mods|(^|[ "`(=:])/tmp/'
for f in $TEXT; do
  case "$f" in
    tools/verify_repo.sh|AGENTS.md|docs/history/PATHMAP.md|docs/history/EXCLUSOES.txt|docs/history/REDACOES.txt|kernel/upstream/*) continue ;;
  esac
  if grep -qE "$PII" "$f"; then fail "V3 dado pessoal: $f:$(grep -nE "$PII" "$f" | head -n 1 | cut -d: -f1)"; fi
  if grep -qE "$LOCAL" "$f"; then fail "V3 caminho local: $f:$(grep -nE "$LOCAL" "$f" | head -n 1 | cut -d: -f1)"; fi
done

# V4: referências internas. Estritas nos documentos de entrada; nos históricos, só contagem de aviso
# (citam APKs, árvore do kernel e planos que ficaram fora do repo; ver docs/history/EXCLUSOES.txt).
STRICT="README.md AGENTS.md CLAUDE.md docs/README.md docs/ESTADO.md docs/PENDENCIAS-DO-DONO.md docs/ultraroot/README.md docs/ultraroot/E1-OPCOES-COPY-LAYOUT.md docs/ultrakernel/README.md kernel/README.md docs/history/TIMELINE.md"
MISSING=""
WARN=0
for f in $TEXT; do
  case "$f" in *.md|*.txt|*.sh) ;; *) continue ;; esac
  case "$f" in tools/verify_repo.sh|kernel/upstream/*) continue ;; esac
  for p in $(grep -oE '(docs|ultraroot|kernel|tools)/[A-Za-z0-9_./-]+' "$f" | sed 's/[.,;:)]*$//' | sort -u); do
    [ -e "$p" ] && continue
    if echo " $STRICT " | grep -q " $f "; then
      MISSING="$MISSING
V4 link ausente: $f -> $p"
    else
      WARN=$((WARN+1))
    fi
  done
done
if [ -n "$MISSING" ]; then printf '%s
' "$MISSING" | sed '/^$/d'; FAIL=1; fi
echo "AVISO V4 (histórico, não bloqueia): $WARN referências a arquivos fora do repo"

# V5: pin do KernelSU: sha256 de cada arquivo = PIN.md; se o repo local do KernelSU existir, também = git show
PIN=kernel/upstream/KernelSU/PIN.md
if [ -f "$PIN" ]; then
  (cd kernel/upstream/KernelSU && grep -E '^\| [0-9a-f]{64} \|' PIN.md | awk -F'|' '{gsub(/ /,"",$2); gsub(/ /,"",$3); print $2"  "$3}' | sha256sum -c --quiet) || fail "V5 pin: sha256 não bate com PIN.md"
  K=/home/rianprei/superroot/scratch/grok/src/KernelSU; C=6e3860443f8fea7c5fc226277e53888033796488
  if [ -d "$K" ]; then
    for p in $(grep -E '^\| [0-9a-f]{64} \|' "$PIN" | awk -F'|' '{gsub(/ /,"",$3); print $3}'); do
      a=$(git -C "$K" show "$C:$p" 2>/dev/null | sha256sum | cut -d' ' -f1)
      b=$(sha256sum "kernel/upstream/KernelSU/$p" | cut -d' ' -f1)
      [ "$a" = "$b" ] || fail "V5 pin: $p difere do commit $C"
    done
  else
    echo "SKIP V5 (git show): repo local do KernelSU não existe nesta máquina"
  fi
fi

# V6: teste nativo host (12 casos PASS)
mkdir -p build
if g++ -std=c++17 -Wall -Werror -I ultraroot/core-backend/src/test/native/shim -I ultraroot/core-backend/src/main/cpp \
     ultraroot/core-backend/src/test/native/fill_test.cc -o build/fill_test 2>/dev/null; then
  n=$(./build/fill_test | grep -c ' PASS$')
  [ "$n" = 12 ] || fail "V6 teste nativo: $n PASS, esperado 12"
else
  fail "V6 teste nativo não compila"
fi

# V7: nenhum arquivo rastreado acima de 5 MB
if git rev-parse --verify -q HEAD >/dev/null; then
  git ls-files -z | xargs -0 -r du -k | awk '$1>5120{print "V7 acima de 5 MB: "$2; bad=1} END{exit bad}' || fail "V7 arquivo grande"
fi

# V8: manifesto de integridade (se existir)
if [ -f docs/history/MANIFEST.sha256 ]; then
  sha256sum -c --quiet docs/history/MANIFEST.sha256 >/dev/null 2>&1 || fail "V8 manifesto não confere"
fi

if [ "$FAIL" = 0 ]; then echo "VERIFICADOR OK"; else echo "VERIFICADOR FALHOU"; fi
exit $FAIL
