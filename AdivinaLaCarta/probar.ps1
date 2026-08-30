$ErrorActionPreference = "Stop"

$salida = Join-Path $PSScriptRoot "out"
New-Item -ItemType Directory -Path $salida -Force | Out-Null
$fuentes = Get-ChildItem -Path (Join-Path $PSScriptRoot "src") -Recurse -Filter "*.java"
$pruebas = Get-ChildItem -Path (Join-Path $PSScriptRoot "test") -Recurse -Filter "*.java"

javac -encoding UTF-8 -d $salida $fuentes.FullName $pruebas.FullName
java -ea -cp $salida Pruebas
