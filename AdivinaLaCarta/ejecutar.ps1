$ErrorActionPreference = "Stop"

& (Join-Path $PSScriptRoot "compilar.ps1")
java -cp (Join-Path $PSScriptRoot "out") Main
