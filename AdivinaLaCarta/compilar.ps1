$ErrorActionPreference = "Stop"

$salida = Join-Path $PSScriptRoot "out"
New-Item -ItemType Directory -Path $salida -Force | Out-Null
$fuentes = Get-ChildItem -Path (Join-Path $PSScriptRoot "src") -Recurse -Filter "*.java"

javac -encoding UTF-8 -d $salida $fuentes.FullName
$recursos = Join-Path $PSScriptRoot "src\recursos"
if (Test-Path $recursos) {
    Copy-Item -Path $recursos -Destination $salida -Recurse -Force
}
Write-Output "Compilacion correcta. Clases generadas en $salida"
