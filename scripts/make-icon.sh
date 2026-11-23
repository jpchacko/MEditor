#!/usr/bin/env bash
# Regenerates packaging/MEditor.icns from scripts/IconGen.java.
set -euo pipefail
cd "$(dirname "$0")/.."
TMP=$(mktemp -d)
SET="$TMP/MEditor.iconset"
mkdir -p "$SET" packaging
java scripts/IconGen.java "$TMP/base.png"
for s in 16 32 128 256 512; do
  sips -z $s $s "$TMP/base.png" --out "$SET/icon_${s}x${s}.png" >/dev/null
  sips -z $((s*2)) $((s*2)) "$TMP/base.png" --out "$SET/icon_${s}x${s}@2x.png" >/dev/null
done
iconutil -c icns "$SET" -o packaging/MEditor.icns
rm -rf "$TMP"
echo "Wrote packaging/MEditor.icns"
