#!/bin/bash

# Root directory
ROOT="/Users/moises/Desktop/projects/saas"

# Microservicios con Prisma (excluye luminia-gateway y luminia-sflbilling)
PRISMA_SERVICES=(
  "luminia-authentications"
  "luminia-billers"
  "luminia-bookings"
  "luminia-businesses"
  "luminia-catalogs"
  "luminia-files"
  "luminia-fitness"
  "luminia-inventories"
  "luminia-notifiers"
  "luminia-orders"
  "luminia-payments"
  "luminia-remittances"
  "luminia-reviews"
  "luminia-suscriptions"
  "luminia-transports"
  "luminia-users"
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
  do script "cd '$dir' && echo '>>> Resetting $name...' && source ~/.nvm/nvm.sh && nvm use 22.16.0 && $cmd" in front window
end tell
EOF
}

echo "Reseteando bases de datos..."

for service in "${PRISMA_SERVICES[@]}"; do
  dir="$ROOT/$service"
  if [ -f "$dir/prisma/schema.prisma" ]; then
    echo "  → $service"

    # Construir comando: siempre borra migrations, reset, migrate dev, generate
    # y seed solo si existe prisma/seed.ts
    if [ -f "$dir/prisma/seed.ts" ]; then
      cmd="rm -rf prisma/migrations && npx prisma migrate reset --force && npx prisma migrate dev --name init && npx prisma generate && npx prisma db seed"
    else
      cmd="rm -rf prisma/migrations && npx prisma migrate reset --force && npx prisma migrate dev --name init && npx prisma generate"
    fi

    open_tab "$dir" "$cmd" "$service"
    sleep 0.5
  else
    echo "  ✗ $service (sin prisma)"
  fi
done

echo ""
echo "Todos los resets iniciados."
