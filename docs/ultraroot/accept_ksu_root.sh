#!/usr/bin/env bash
# accept_ksu_root.sh <serial>
# Exit 0 só com as três juntas:
#   a saída de su -c id contém uid=0(
#   ksud ou /data/adb/ksu, e o prctl/supercall UAPI é 5
#   o contexto não contém magisk
# ACCEPT_KSU_FIXTURE substitui a coleta. Coleta via adb exige
# ACCEPT_KSU_ALLOW_ADB=1; sem fixture nem opt-in, sai 2 sem chamar comandos.
set -euo pipefail

serial="${1:-}"
[ -n "$serial" ] || { echo "uso: accept_ksu_root.sh <serial>" >&2; exit 2; }

if [ -n "${ACCEPT_KSU_FIXTURE:-}" ]; then
  # shellcheck disable=SC1090
  . "$ACCEPT_KSU_FIXTURE"
  ctx="${KSU_SU_CONTEXT:-}"
  uapi="${KSU_UAPI:-}"
  ksud="${KSU_HAS_KSUD:-no}"
  ksudir="${KSU_HAS_DIR:-no}"
  id_out="${KSU_SU_ID:-}"
else
  if [ "${ACCEPT_KSU_ALLOW_ADB:-0}" != 1 ]; then
    echo "sem fixture: coleta adb exige ACCEPT_KSU_ALLOW_ADB=1" >&2
    exit 2
  fi
  if ! command -v adb >/dev/null 2>&1; then
    echo "sem fixture: coleta adb fica na Task 7 e não roda neste host" >&2
    exit 2
  fi
  # O rc de cada coleta é checado: saída parcial de comando que falhou não é aceite.
  set +e
  id_raw="$(adb -s "$serial" shell su -c id 2>&1)"
  id_rc=$?
  probe_raw="$(adb -s "$serial" shell "su -c 'if [ -e /data/adb/ksud ]; then echo HAS_KSUD; fi; if [ -d /data/adb/ksu ]; then echo HAS_KSUDIR; fi; /data/adb/ksud debug info'" 2>&1)"
  probe_rc=$?
  set -e
  id_out="$(printf '%s' "$id_raw" | tr -d '\r')"
  probe="$(printf '%s' "$probe_raw" | tr -d '\r')"
  if [ "$id_rc" -ne 0 ]; then
    echo "REPROVA su -c id falhou (rc=$id_rc): ${id_out:-vazio}" >&2
    exit 1
  fi
  if [ "$probe_rc" -ne 0 ]; then
    echo "REPROVA coleta do ksud falhou (rc=$probe_rc)" >&2
    exit 1
  fi
  ctx="$id_out"
  if printf '%s\n' "$probe" | grep -qx 'HAS_KSUD'; then
    ksud=yes
  else
    ksud=no
  fi
  if printf '%s\n' "$probe" | grep -qx 'HAS_KSUDIR'; then
    ksudir=yes
  else
    ksudir=no
  fi
  uapi="$(printf '%s\n' "$probe" | sed -n 's/^uapi_version:[[:space:]]*//p' | sed -n '1p')"
fi

case "$ctx" in
  *magisk*)
    echo "REPROVA contexto magisk: $ctx" >&2
    exit 1
    ;;
esac
case "$id_out" in
  *magisk*)
    echo "REPROVA su -c id com magisk: $id_out" >&2
    exit 1
    ;;
esac

if [ "$ksud" != "yes" ] && [ "$ksudir" != "yes" ]; then
  echo "REPROVA sem ksud e sem /data/adb/ksu" >&2
  exit 1
fi

if [ "$uapi" != "5" ]; then
  echo "REPROVA uapi='$uapi' (esperado 5)" >&2
  exit 1
fi

case "$id_out" in
  *'uid=0('*) ;;
  *)
    echo "REPROVA su -c id sem uid=0: ${id_out:-vazio}" >&2
    exit 1
    ;;
esac

if [ -z "$ctx" ]; then
  echo "REPROVA su sem contexto" >&2
  exit 1
fi

echo "APROVA ksu serial=$serial uid=0 uapi=5"
exit 0
