#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
./compilar.sh
java -cp out Main
