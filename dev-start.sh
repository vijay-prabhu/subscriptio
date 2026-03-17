#!/usr/bin/env bash
set -e

LOG_DIR="/tmp/subscriptio-logs"
mkdir -p "$LOG_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
PID_FILE="$LOG_DIR/pids"

log() { echo -e "${BLUE}[subscriptio]${NC} $1"; }
ok()  { echo -e "${GREEN}[✓]${NC} $1"; }
err() { echo -e "${RED}[✗]${NC} $1"; }
warn() { echo -e "${YELLOW}[!]${NC} $1"; }

# Clean up old PIDs
> "$PID_FILE"

# ─── 1. Docker Compose (PostgreSQL, Redis, Mailhog) ──────────────────
log "Starting Docker Compose services..."
cd "$PROJECT_ROOT/infra"
docker compose up -d > "$LOG_DIR/docker-compose.log" 2>&1

# Wait for PostgreSQL
for i in $(seq 1 15); do
  if docker exec subscriptio-postgres pg_isready -U subscriptio > /dev/null 2>&1; then
    ok "PostgreSQL ready"
    break
  fi
  sleep 1
done

if ! docker exec subscriptio-postgres pg_isready -U subscriptio > /dev/null 2>&1; then
  err "PostgreSQL failed to start. Check $LOG_DIR/docker-compose.log"
  exit 1
fi

# ─── 2. Spring Boot Backend (port 8080) ──────────────────────────────
log "Starting Spring Boot backend..."
cd "$PROJECT_ROOT/backend"
mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
echo "$!" >> "$PID_FILE"

for i in $(seq 1 30); do
  if curl -sf http://localhost:8080/api/v1/health > /dev/null 2>&1; then
    ok "Backend ready → http://localhost:8080"
    break
  fi
  sleep 2
done

if ! curl -sf http://localhost:8080/api/v1/health > /dev/null 2>&1; then
  err "Backend failed to start. Check $LOG_DIR/backend.log"
  exit 1
fi

# ─── 3. Admin Dashboard — Next.js (port 3000) ────────────────────────
log "Starting Admin Dashboard (Next.js)..."
cd "$PROJECT_ROOT/admin-dashboard"
npm run dev > "$LOG_DIR/admin-dashboard.log" 2>&1 &
echo "$!" >> "$PID_FILE"

for i in $(seq 1 15); do
  if curl -sf http://localhost:3000 > /dev/null 2>&1; then
    ok "Admin Dashboard ready → http://localhost:3000"
    break
  fi
  sleep 2
done

# ─── 4. Customer Portal — Nuxt.js (port 3001) ────────────────────────
log "Starting Customer Portal (Nuxt.js)..."
cd "$PROJECT_ROOT/customer-portal"
npx nuxi@3.14.0 dev --port 3001 > "$LOG_DIR/customer-portal.log" 2>&1 &
echo "$!" >> "$PID_FILE"

for i in $(seq 1 15); do
  if curl -sf http://localhost:3001 > /dev/null 2>&1; then
    ok "Customer Portal ready → http://localhost:3001"
    break
  fi
  sleep 2
done

# ─── Summary ─────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN} All services running${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "  PostgreSQL        → localhost:5432"
echo "  Redis             → localhost:6379"
echo "  Mailhog UI        → http://localhost:8025"
echo "  Backend API       → http://localhost:8080"
echo "  Admin Dashboard   → http://localhost:3000"
echo "  Customer Portal   → http://localhost:3001"
echo ""
echo "  Logs:  $LOG_DIR/"
echo "    backend.log, admin-dashboard.log, customer-portal.log"
echo ""
echo -e "  Stop all:  ${YELLOW}./dev-stop.sh${NC}"
echo ""
