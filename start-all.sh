#!/bin/bash

# Root directory
ROOT="/Users/moises/Desktop/projects/saas"

# NestJS microservices
NEST_SERVICES=(
  "luminia-authentications"
  "luminia-billers"
  "luminia-business"
  "luminia-files"
  "luminia-gateway"
  "luminia-gyms"
  "luminia-notifiers"
  "luminia-orders"
  "luminia-payments"
  "luminia-store"
  "luminia-suscriptions"
  "luminia-users"
)

# React frontend
REACT_SERVICES=(
  "luminia-mall"
)

open_tab() {
  local dir="$1"
  local cmd="$2"
  local name="$3"

  osascript <<EOF
tell application "Terminal"
  activate
  tell application "System Events" to keystroke "t" using {command down}
  delay 0.3
  do script "cd '$dir' && echo '>>> Starting $name...' && source ~/.nvm/nvm.sh && nvm use 22.16.0 && $cmd" in front window
end tell
EOF
}

echo "Levantando todos los servicios..."

for service in "${NEST_SERVICES[@]}"; do
  dir="$ROOT/$service"
  if [ -f "$dir/package.json" ]; then
    echo "  → $service"
    if [ -f "$dir/prisma/seed.ts" ]; then
      open_tab "$dir" "npx prisma db seed && yarn start:dev" "$service"
    else
      open_tab "$dir" "yarn start:dev" "$service"
    fi
    sleep 0.5
  else
    echo "  ✗ $service (no encontrado)"
  fi
done

for service in "${REACT_SERVICES[@]}"; do
  dir="$ROOT/$service"
  if [ -f "$dir/package.json" ]; then
    echo "  → $service (React)"
    open_tab "$dir" "yarn dev" "$service"
    sleep 0.5
  else
    echo "  ✗ $service (no encontrado)"
  fi
done

echo ""
echo "Todos los servicios iniciados."
