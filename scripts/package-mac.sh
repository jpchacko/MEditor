#!/usr/bin/env bash
# Builds target/dist/MEditor.app (bundles its own Java runtime). Run from anywhere.
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q package

JAR=$(basename "$(ls target/MEditor-*.jar | head -1)")
rm -rf target/jpkg-input target/dist
mkdir -p target/jpkg-input
cp "target/$JAR" target/jpkg-input/

# macOS requires a purely numeric app version, so it is not derived from the POM's -SNAPSHOT.
jpackage --type app-image --name MEditor --app-version 1.0.0 \
  --input target/jpkg-input --main-jar "$JAR" --main-class org.example.Main \
  --dest target/dist --icon packaging/MEditor.icns \
  --java-options "-Dapple.awt.application.name=MEditor"

echo "Built: target/dist/MEditor.app"
