#!/bin/bash
set -e

# Keycloak MCP Token Generator
# This script helps users get their personal JWT token for accessing the MCP server

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
    echo ""
}

usage() {
    cat << EOF
Usage: $0 [OPTIONS]

Get a JWT token for accessing the Keycloak MCP Server.

OPTIONS:
    --keycloak-url URL      Keycloak server URL (required)
                           Example: https://keycloak.example.com

    --realm REALM          Keycloak realm (optional)
                           Default: master

    --username USER        Your Keycloak username (required)

    --password PASS        Your Keycloak password (required)

    --client-id ID         Client ID for token request (optional)
                           Default: admin-cli

    --mcp-url URL          MCP Server URL (optional)
                           Default: http://localhost:8080/mcp/sse

    --output FORMAT        Output format: json, env, or cursor (optional)
                           Default: cursor

    --help                 Display this help message

EXAMPLES:
    # Get token and generate Cursor config
    $0 --keycloak-url https://keycloak.example.com \\
       --username alice \\
       --password alice-password

    # Custom realm and MCP URL
    $0 --keycloak-url https://keycloak.example.com \\
       --realm production \\
       --username bob \\
       --password bob-password \\
       --mcp-url https://mcp-server.example.com/mcp/sse

    # Output as JSON
    $0 --keycloak-url https://keycloak.example.com \\
       --username charlie \\
       --password charlie-password \\
       --output json

EOF
    exit 1
}

# Default values
REALM="master"
CLIENT_ID="admin-cli"
MCP_URL="http://localhost:8080/mcp/sse"
OUTPUT_FORMAT="cursor"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --keycloak-url)
            KEYCLOAK_URL="$2"
            shift 2
            ;;
        --realm)
            REALM="$2"
            shift 2
            ;;
        --username)
            USERNAME="$2"
            shift 2
            ;;
        --password)
            PASSWORD="$2"
            shift 2
            ;;
        --client-id)
            CLIENT_ID="$2"
            shift 2
            ;;
        --mcp-url)
            MCP_URL="$2"
            shift 2
            ;;
        --output)
            OUTPUT_FORMAT="$2"
            shift 2
            ;;
        --help)
            usage
            ;;
        *)
            print_error "Unknown option: $1"
            usage
            ;;
    esac
done

# Validate required parameters
if [ -z "$KEYCLOAK_URL" ]; then
    print_error "Keycloak URL is required"
    usage
fi

if [ -z "$USERNAME" ]; then
    print_error "Username is required"
    usage
fi

if [ -z "$PASSWORD" ]; then
    print_error "Password is required"
    usage
fi

# Check required commands
for cmd in curl jq; do
    if ! command -v $cmd &> /dev/null; then
        print_error "$cmd is required but not installed. Please install it and try again."
        exit 1
    fi
done

print_header "Keycloak MCP Token Generator"

print_info "Configuration:"
echo "  Keycloak URL: $KEYCLOAK_URL"
echo "  Realm: $REALM"
echo "  Username: $USERNAME"
echo "  MCP Server URL: $MCP_URL"
echo ""

# Get access token
print_info "Requesting access token..."
TOKEN_RESPONSE=$(curl -sk -X POST \
  "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d "client_id=$CLIENT_ID" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD" 2>/dev/null)

ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token')
REFRESH_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.refresh_token')
EXPIRES_IN=$(echo "$TOKEN_RESPONSE" | jq -r '.expires_in')
REFRESH_EXPIRES_IN=$(echo "$TOKEN_RESPONSE" | jq -r '.refresh_expires_in')

if [ -z "$ACCESS_TOKEN" ] || [ "$ACCESS_TOKEN" = "null" ]; then
    print_error "Failed to get access token"
    ERROR_DESC=$(echo "$TOKEN_RESPONSE" | jq -r '.error_description // .error // "Unknown error"')
    echo "Error: $ERROR_DESC"
    exit 1
fi

print_success "Access token obtained"
echo "  Token expires in: $EXPIRES_IN seconds ($(($EXPIRES_IN / 60)) minutes)"
echo ""

# Decode token to show user info
print_info "Token information:"
TOKEN_PAYLOAD=$(echo "$ACCESS_TOKEN" | cut -d'.' -f2 | base64 -d 2>/dev/null | jq .)
PREFERRED_USERNAME=$(echo "$TOKEN_PAYLOAD" | jq -r '.preferred_username // .sub')
REALM_ACCESS=$(echo "$TOKEN_PAYLOAD" | jq -r '.realm_access.roles // [] | join(", ")')
RESOURCE_ACCESS=$(echo "$TOKEN_PAYLOAD" | jq -r '.resource_access | keys | join(", ")')

echo "  Username: $PREFERRED_USERNAME"
echo "  Realm Roles: $REALM_ACCESS"
if [ -n "$RESOURCE_ACCESS" ] && [ "$RESOURCE_ACCESS" != "" ]; then
    echo "  Resource Access: $RESOURCE_ACCESS"
fi
echo ""

# Output based on format
case $OUTPUT_FORMAT in
    json)
        print_header "Token (JSON)"
        echo "$TOKEN_RESPONSE" | jq .
        ;;
    
    env)
        print_header "Environment Variables"
        echo "export MCP_ACCESS_TOKEN='$ACCESS_TOKEN'"
        echo "export MCP_REFRESH_TOKEN='$REFRESH_TOKEN'"
        echo "export MCP_TOKEN_EXPIRES_IN='$EXPIRES_IN'"
        ;;
    
    cursor)
        print_header "Cursor MCP Configuration"
        print_info "Add this to your ~/.cursor/mcp.json file:"
        echo ""
        cat << EOF
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "$MCP_URL",
      "headers": {
        "Authorization": "Bearer $ACCESS_TOKEN"
      }
    }
  }
}
EOF
        echo ""
        print_warning "Note: This token will expire in $(($EXPIRES_IN / 60)) minutes"
        print_info "You'll need to regenerate it and update mcp.json when it expires"
        echo ""
        
        # Save to file
        CONFIG_FILE="/tmp/cursor-mcp-config-$(date +%s).json"
        cat > "$CONFIG_FILE" << EOF
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "$MCP_URL",
      "headers": {
        "Authorization": "Bearer $ACCESS_TOKEN"
      }
    }
  }
}
EOF
        print_success "Configuration saved to: $CONFIG_FILE"
        echo ""
        print_info "To use it, copy the contents to ~/.cursor/mcp.json or merge with existing config"
        ;;
    
    *)
        print_error "Unknown output format: $OUTPUT_FORMAT"
        echo "Valid formats: json, env, cursor"
        exit 1
        ;;
esac

print_header "Next Steps"

echo "1. Update your Cursor MCP configuration (~/.cursor/mcp.json)"
echo "2. Reload MCP servers in Cursor (Cmd+Shift+P → Reload MCP Servers)"
echo "3. Try a command in Cursor chat:"
echo "   ${GREEN}List all Keycloak realms${NC}"
echo ""

if [ -n "$REFRESH_TOKEN" ] && [ "$REFRESH_TOKEN" != "null" ]; then
    print_info "Refresh Token: Available (expires in $(($REFRESH_EXPIRES_IN / 3600)) hours)"
    echo "  To refresh your token before it expires, run:"
    echo "  curl -X POST '$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token' \\"
    echo "    -d 'grant_type=refresh_token' \\"
    echo "    -d 'client_id=$CLIENT_ID' \\"
    echo "    -d 'refresh_token=$REFRESH_TOKEN' | jq -r '.access_token'"
    echo ""
fi

print_success "Done! Happy Keycloak management! 🚀"
echo ""

