#!/usr/bin/env bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

LOG_DIR="/tmp/subscriptio-logs"
PID_FILE="$LOG_DIR/pids"
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"

log() { echo -e "${YELLOW}[subscriptio]${NC} $1"; }
ok()  { echo -e "${GREEN}[✓]${NC} $1"; }

# ─── Kill app processes from PID file ─────────────────────────────────
if [ -f "$PID_FILE" ]; then
  while read -r pid; do
    if kill -0 "$pid" 2>/dev/null; then
      kill "$pid" 2>/dev/null
    fi
  done < "$PID_FILE"
  rm -f "$PID_FILE"
fi

# ─── Kill by process pattern (fallback) ──────────────────────────────
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "next dev" 2>/dev/null || true
pkill -f "next-server" 2>/dev/null || true
pkill -f "nuxi" 2>/dev/null || true
pkill -f "nuxt" 2>/dev/null || true

ok "App processes stopped"

# ─── Stop Docker Compose ─────────────────────────────────────────────
log "Stopping Docker Compose services..."
cd "$PROJECT_ROOT/infra"
docker compose down > /dev/null 2>&1
ok "Docker Compose stopped"

echo ""
echo -e "${GREEN}All services stopped.${NC}"
echo "Logs preserved at: $LOG_DIR/"
echo ""
