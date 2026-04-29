#!/usr/bin/env bash
#
# Seed a small group hierarchy in Keycloak for local MCP / API testing.
# Idempotent: skips creation if a group with the same name already exists
# in the same place (top-level or under the parent).
#
# Base URL defaults to http://localhost:8080. Override for other hosts/ports, e.g.
# deploy/docker-compose.yml maps Keycloak to host port 8081:
#   KEYCLOAK_URL=http://localhost:8081 ./scripts/group-test.sh
#
# Usage:
#   ./scripts/group-test.sh
#   ./scripts/group-test.sh --keycloak-url http://keycloak.example.com:8443
#
set -euo pipefail

# host:port for local Keycloak unless KEYCLOAK_URL is set in the environment
KEYCLOAK_HOST="${KEYCLOAK_HOST:-localhost}"
KEYCLOAK_PORT="${KEYCLOAK_PORT:-8080}"
KEYCLOAK_URL="${KEYCLOAK_URL:-http://${KEYCLOAK_HOST}:${KEYCLOAK_PORT}}"

KEYCLOAK_REALM="${KEYCLOAK_REALM:-master}"
KEYCLOAK_USER="${KEYCLOAK_USER:-admin}"
KEYCLOAK_PASSWORD="${KEYCLOAK_PASSWORD:-admin}"
PREFIX="${PREFIX:-MCP-Test-}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info()    { echo -e "${BLUE}ℹ  $1${NC}"; }
print_ok()     { echo -e "${GREEN}✓ $1${NC}"; }
print_skip()   { echo -e "${YELLOW}· $1${NC}"; }
print_err()    { echo -e "${RED}✗ $1${NC}" >&2; }

usage() {
  cat << EOF
Seed test groups in Keycloak (hierarchy: Engineering → Backend, Frontend; Product → Mobile).

Environment (defaults in parentheses):
  KEYCLOAK_URL         Full base URL (default: http://\${KEYCLOAK_HOST}:\${KEYCLOAK_PORT})
  KEYCLOAK_HOST        Hostname when building URL (localhost)
  KEYCLOAK_PORT        Port when building URL (8080)
  KEYCLOAK_REALM       Realm (master)
  KEYCLOAK_USER        Admin user (admin)
  KEYCLOAK_PASSWORD    Admin password
  PREFIX               Name prefix for top-level groups (MCP-Test-)

Options:
  --keycloak-url URL   Override KEYCLOAK_URL
  --realm REALM        Override KEYCLOAK_REALM
  --username USER      Override KEYCLOAK_USER
  --password PASS      Override KEYCLOAK_PASSWORD
  --prefix PREFIX      Override PREFIX
  -h, --help           Show this help

Examples:
  $(basename "$0")
  KEYCLOAK_URL=http://localhost:8080 $(basename "$0")
EOF
}

for cmd in curl jq; do
  if ! command -v "$cmd" &>/dev/null; then
    print_err "Required command not found: $cmd"
    exit 1
  fi
done

while [[ $# -gt 0 ]]; do
  case "$1" in
    --keycloak-url) KEYCLOAK_URL="$2"; shift 2 ;;
    --realm)        KEYCLOAK_REALM="$2"; shift 2 ;;
    --username)     KEYCLOAK_USER="$2"; shift 2 ;;
    --password)     KEYCLOAK_PASSWORD="$2"; shift 2 ;;
    --prefix)       PREFIX="$2"; shift 2 ;;
    -h|--help)      usage; exit 0 ;;
    *)              print_err "Unknown option: $1"; usage; exit 1 ;;
  esac
done

# Trim trailing slash
KEYCLOAK_URL="${KEYCLOAK_URL%/}"
TOKEN=""

get_token() {
  local resp http
  resp=$(curl -sS -w "\n%{http_code}" -X POST \
    "${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "username=${KEYCLOAK_USER}" \
    --data-urlencode "password=${KEYCLOAK_PASSWORD}" \
    --data-urlencode "grant_type=password" \
    --data-urlencode "client_id=admin-cli")
  http=$(echo "$resp" | tail -n1)
  body=$(echo "$resp" | sed '$d')
  if [[ "$http" != "200" ]]; then
    print_err "Token request failed (HTTP $http): $body"
    exit 1
  fi
  TOKEN=$(echo "$body" | jq -r .access_token)
  if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
    print_err "No access_token in response"
    exit 1
  fi
}

# GET JSON helper
api_get() {
  local path="$1"
  curl -sS -H "Authorization: Bearer $TOKEN" "${KEYCLOAK_URL}/admin${path}"
}

# Returns group id for top-level name, or empty
find_top_id() {
  local name="$1"
  api_get "/realms/${KEYCLOAK_REALM}/groups" | jq -r --arg n "$name" '.[] | select(.name==$n) | .id' | head -1
}

# Returns child id for direct child name under parent, or empty
find_child_id() {
  local parent="$1" name="$2"
  api_get "/realms/${KEYCLOAK_REALM}/groups/${parent}/children" | jq -r --arg n "$name" '.[] | select(.name==$n) | .id' | head -1
}

# Creates top-level group; prints new id
create_top() {
  local name="$1" loc id http hdr
  hdr=$(mktemp)
  http=$(curl -sS -D "$hdr" -o /dev/null -w "%{http_code}" -X POST \
    "${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/groups" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg n "$name" '{name: $n}')")
  if [[ "$http" != "201" ]]; then
    rm -f "$hdr"
    print_err "Failed to create top-level group '$name' (HTTP $http)"
    exit 1
  fi
  loc=$(grep -i '^[Ll]ocation:' "$hdr" | tr -d '\r' | head -1)
  rm -f "$hdr"
  id="${loc##*/}"
  id="${id%%\?*}"
  echo "$id"
}

# Creates child; prints new id
create_child() {
  local parent="$1" name="$2" loc id http hdr
  hdr=$(mktemp)
  http=$(curl -sS -D "$hdr" -o /dev/null -w "%{http_code}" -X POST \
    "${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}/groups/${parent}/children" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$(jq -n --arg n "$name" '{name: $n}')")
  if [[ "$http" != "201" ]]; then
    rm -f "$hdr"
    print_err "Failed to create child '$name' under $parent (HTTP $http)"
    exit 1
  fi
  loc=$(grep -i '^[Ll]ocation:' "$hdr" | tr -d '\r' | head -1)
  rm -f "$hdr"
  id="${loc##*/}"
  id="${id%%\?*}"
  echo "$id"
}

ensure_top() {
  local name="$1" id
  id=$(find_top_id "$name")
  if [[ -n "$id" ]]; then
    print_skip "Top group exists: $name ($id)" >&2
  else
    id=$(create_top "$name")
    print_ok "Created top group: $name ($id)" >&2
  fi
  echo "$id"
}

ensure_child() {
  local parent="$1" name="$2" id
  id=$(find_child_id "$parent" "$name")
  if [[ -n "$id" ]]; then
    print_skip "Subgroup exists: $name under $parent ($id)" >&2
  else
    id=$(create_child "$parent" "$name")
    print_ok "Created subgroup: $name ($id)" >&2
  fi
  echo "$id"
}

# --- main ---
print_info "Keycloak: $KEYCLOAK_URL | realm: $KEYCLOAK_REALM | prefix: $PREFIX"
get_token
print_ok "Obtained admin access token"

ENG="${PREFIX}Engineering"
PROD="${PREFIX}Product"

eng_id=$(ensure_top "$ENG")
back_id=$(ensure_child "$eng_id" "Backend")
front_id=$(ensure_child "$eng_id" "Frontend")
prod_id=$(ensure_top "$PROD")
mobile_id=$(ensure_child "$prod_id" "Mobile")

echo ""
print_ok "Test groups ready. Summary (for GET_SUBGROUPS / Admin API):"
cat << EOF

  Realm:     $KEYCLOAK_REALM
  Top-level: $ENG, $PROD
  $ENG:      Backend, Frontend
  $PROD:     Mobile

  Group IDs:
    $ENG   $eng_id
    Backend   $back_id
    Frontend  $front_id
    $PROD  $prod_id
    Mobile    $mobile_id

EOF
