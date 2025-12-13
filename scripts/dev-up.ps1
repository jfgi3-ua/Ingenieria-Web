# SOLO WINDOWS POWERSHELL

$ErrorActionPreference = "Stop"

# Obtiene el Escritorio del usuario (funciona aunque esté en OneDrive o tenga otro nombre)
$desktop = [Environment]::GetFolderPath("Desktop")
$dataDir = Join-Path $desktop "FitGymData"

# Crea la carpeta y subcarpeta pgdata
New-Item -ItemType Directory -Force -Path $dataDir | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $dataDir "pgdata") | Out-Null

# Exporta variable para docker compose (solo para esta sesión)
$env:FITGYM_DATA_DIR = $dataDir

Write-Host "FITGYM_DATA_DIR=$($env:FITGYM_DATA_DIR)"
docker compose up -d
docker ps
