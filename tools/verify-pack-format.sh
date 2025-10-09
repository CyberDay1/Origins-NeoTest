#!/usr/bin/env bash
set -euo pipefail

declare -A EXPECTED
EXPECTED["1.21.10-neoforge"]=88
EXPECTED["1.21.9-neoforge"]=88
EXPECTED["1.21.8-neoforge"]=81
EXPECTED["1.21.7-neoforge"]=81
EXPECTED["1.21.6-neoforge"]=80
EXPECTED["1.21.5-neoforge"]=71
EXPECTED["1.21.4-neoforge"]=61
EXPECTED["1.21.3-neoforge"]=57
EXPECTED["1.21.2-neoforge"]=57
EXPECTED["1.21.1-neoforge"]=48

failures=0

for variant in "${!EXPECTED[@]}"; do
  echo "==> Switching to $variant"
  ./gradlew --no-daemon stonecutter use "$variant" >/dev/null

  echo "==> Building"
  ./gradlew --no-daemon -q clean build >/dev/null || true

  PACK_FILE="build/resources/main/pack.mcmeta"
  if [[ ! -f "$PACK_FILE" ]]; then
    echo "::error ::Missing $PACK_FILE for $variant"
    failures=$((failures+1))
    continue
  fi

  got=$(grep -Eo '"pack_format"\s*:\s*[0-9]+' "$PACK_FILE" | grep -Eo '[0-9]+' | head -n1 || echo "NA")
  want=${EXPECTED[$variant]}

  if [[ "$got" != "$want" ]]; then
    echo "::error ::pack_format mismatch for $variant: got=$got want=$want"
    failures=$((failures+1))
  else
    echo "::notice ::pack_format OK for $variant -> $got"
  fi

done

if [[ $failures -gt 0 ]]; then
  echo "::error ::Pack format verification failed for $failures variant(s)"
  exit 1
fi

echo "All pack_format values match expectations."
