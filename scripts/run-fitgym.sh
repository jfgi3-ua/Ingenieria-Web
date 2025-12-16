#!/usr/bin/env bash
set -e

# Detect Desktop location (local or iCloud)
DESKTOP="$HOME/Desktop"
ICLOUD_DESKTOP="$HOME/Library/Mobile Documents/com~apple~CloudDocs/Desktop"
[ -d "$ICLOUD_DESKTOP" ] && DESKTOP="$ICLOUD_DESKTOP"

DATA_DIR="$DESKTOP/FitGymData"

# Create folders
mkdir -p "$DATA_DIR/pgdata"

# Export env var for this session
export FITGYM_DATA_DIR="$DATA_DIR"

echo "FITGYM_DATA_DIR=$FITGYM_DATA_DIR"

# Start containers
docker compose up -d
docker ps