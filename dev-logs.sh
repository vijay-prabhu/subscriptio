#!/usr/bin/env bash

LOG_DIR="/tmp/subscriptio-logs"
SERVICE="${1:-all}"

case "$SERVICE" in
  backend)
    tail -f "$LOG_DIR/backend.log"
    ;;
  admin)
    tail -f "$LOG_DIR/admin-dashboard.log"
    ;;
  portal)
    tail -f "$LOG_DIR/customer-portal.log"
    ;;
  analytics)
    tail -f "$LOG_DIR/analytics.log"
    ;;
  docker)
    tail -f "$LOG_DIR/docker-compose.log"
    ;;
  all)
    echo "Usage: ./dev-logs.sh [backend|admin|portal|analytics|docker]"
    echo ""
    echo "Log files:"
    ls -la "$LOG_DIR/"*.log 2>/dev/null || echo "  No logs yet. Run ./dev-start.sh first."
    ;;
  *)
    echo "Unknown service: $SERVICE"
    echo "Usage: ./dev-logs.sh [backend|admin|portal|docker]"
    ;;
esac
