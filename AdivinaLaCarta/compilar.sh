#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
if [ -d src/recursos ]; then
  cp -R src/recursos out/
fi
echo "Compilacion correcta. Clases generadas en out/"
