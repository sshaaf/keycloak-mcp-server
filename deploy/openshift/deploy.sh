#!/bin/bash
# OpenShift Deployment Script for Keycloak MCP Server
# This script helps deploy the MCP server with proper HTTPS Keycloak configuration

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Keycloak MCP Server OpenShift Deployment ===${NC}"
echo ""

# Configuration
PROJECT_NAME=${PROJECT_NAME:-"keycloak-mcp"}
KEYCLOAK_NAMESPACE=${KEYCLOAK_NAMESPACE:-"rhbk"}
KEYCLOAK_TLS_SECRET=${KEYCLOAK_TLS_SECRET:-"example-tls-secret"}
KEYCLOAK_URL=${KEYCLOAK_URL:-""}
KEYCLOAK_USER=${KEYCLOAK_USER:-"admin"}
KEYCLOAK_PASSWORD=${KEYCLOAK_PASSWORD:-""}
USE_CA_CERT=${USE_CA_CERT:-"false"}

# Check if oc is installed
if ! command -v oc &> /dev/null; then
    echo -e "${RED}Error: oc CLI not found. Please install OpenShift CLI.${NC}"
    exit 1
fi

# Check if logged in
if ! oc whoami &> /dev/null; then
    echo -e "${RED}Error: Not logged in to OpenShift. Please run 'oc login' first.${NC}"
    exit 1
fi

echo -e "${YELLOW}Configuration:${NC}"
echo "  Project: $PROJECT_NAME"
echo "  Keycloak Namespace: $KEYCLOAK_NAMESPACE"
echo ""

# Create or switch to project
echo -e "${GREEN}1. Creating/switching to project...${NC}"
if oc get project $PROJECT_NAME &> /dev/null; then
    oc project $PROJECT_NAME
    echo "  ✓ Using existing project: $PROJECT_NAME"
else
    oc new-project $PROJECT_NAME
    echo "  ✓ Created new project: $PROJECT_NAME"
fi
echo ""

# Get Keycloak URL if not provided
if [ -z "$KEYCLOAK_URL" ]; then
    echo -e "${YELLOW}2. Detecting Keycloak URL...${NC}"
    if oc get keycloak -n $KEYCLOAK_NAMESPACE &> /dev/null; then
        KEYCLOAK_HOSTNAME=$(oc get keycloak -n $KEYCLOAK_NAMESPACE -o jsonpath='{.items[0].spec.hostname.hostname}' 2>/dev/null || echo "")
        if [ -n "$KEYCLOAK_HOSTNAME" ]; then
            KEYCLOAK_URL="https://$KEYCLOAK_HOSTNAME"
            echo "  ✓ Detected Keycloak URL: $KEYCLOAK_URL"
            USE_CA_CERT="true"
        else
            echo -e "${RED}  ✗ Could not detect Keycloak URL from Keycloak CR${NC}"
            read -p "  Please enter Keycloak HTTPS URL: " KEYCLOAK_URL
        fi
    else
        read -p "  Please enter Keycloak HTTPS URL: " KEYCLOAK_URL
    fi
else
    echo -e "${GREEN}2. Using provided Keycloak URL: $KEYCLOAK_URL${NC}"
fi

# Validate HTTPS
if [[ ! "$KEYCLOAK_URL" =~ ^https:// ]]; then
    echo -e "${YELLOW}  ⚠ Warning: Keycloak URL should use HTTPS in production!${NC}"
    echo -e "${YELLOW}     Current URL: $KEYCLOAK_URL${NC}"
    read -p "  Continue anyway? (y/N): " continue
    if [[ ! "$continue" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi
echo ""

# Extract CA certificate if needed
if [[ "$USE_CA_CERT" == "true" && "$KEYCLOAK_URL" =~ ^https:// ]]; then
    echo -e "${GREEN}3. Extracting Keycloak CA certificate...${NC}"
    if oc get secret $KEYCLOAK_TLS_SECRET -n $KEYCLOAK_NAMESPACE &> /dev/null; then
        oc get secret $KEYCLOAK_TLS_SECRET -n $KEYCLOAK_NAMESPACE \
          -o jsonpath='{.data.tls\.crt}' | base64 -d > /tmp/keycloak-ca.crt
        
        # Create CA ConfigMap
        oc create configmap keycloak-ca-bundle \
          --from-file=ca.crt=/tmp/keycloak-ca.crt \
          --dry-run=client -o yaml | oc apply -f -
        
        echo "  ✓ CA certificate extracted and configured"
        rm -f /tmp/keycloak-ca.crt
    else
        echo -e "${YELLOW}  ⚠ CA certificate secret not found: $KEYCLOAK_TLS_SECRET${NC}"
        echo "  ⚠ If using self-signed certificates, configure CA manually"
        echo "  📖 See: deploy/openshift/KEYCLOAK_TLS_SETUP.md"
    fi
else
    echo -e "${GREEN}3. Skipping CA certificate extraction${NC}"
    echo "  (Using trusted CA or HTTP)"
fi
echo ""

# Get Keycloak password if not provided
if [ -z "$KEYCLOAK_PASSWORD" ]; then
    echo -e "${YELLOW}4. Getting Keycloak credentials...${NC}"
    read -sp "  Enter Keycloak admin password: " KEYCLOAK_PASSWORD
    echo ""
else
    echo -e "${GREEN}4. Using provided Keycloak credentials${NC}"
fi
echo ""

# Create ConfigMap
echo -e "${GREEN}5. Creating ConfigMap...${NC}"
cat <<EOF | oc apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: keycloak-mcp-config
  labels:
    app: keycloak-mcp-server
data:
  keycloak-url: "$KEYCLOAK_URL"
EOF
echo "  ✓ ConfigMap created/updated"
echo ""

# Create Secret
echo -e "${GREEN}6. Creating Secret...${NC}"
oc create secret generic keycloak-mcp-secret \
  --from-literal=username=$KEYCLOAK_USER \
  --from-literal=password="$KEYCLOAK_PASSWORD" \
  --dry-run=client -o yaml | oc apply -f -
echo "  ✓ Secret created/updated"
echo ""

# Apply manifests
echo -e "${GREEN}7. Deploying application...${NC}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Apply in order
oc apply -f "$SCRIPT_DIR/deployment.yaml"
oc apply -f "$SCRIPT_DIR/service.yaml"
oc apply -f "$SCRIPT_DIR/route.yaml"

echo "  ✓ Application deployed"
echo ""

# Wait for deployment
echo -e "${GREEN}8. Waiting for deployment to be ready...${NC}"
oc rollout status deployment/keycloak-mcp-server --timeout=5m
echo "  ✓ Deployment ready"
echo ""

# Get route
ROUTE_URL=$(oc get route keycloak-mcp-server -o jsonpath='{.spec.host}' 2>/dev/null || echo "")
if [ -n "$ROUTE_URL" ]; then
    echo -e "${GREEN}=== Deployment Complete! ===${NC}"
    echo ""
    echo -e "${GREEN}Application URL:${NC} https://$ROUTE_URL"
    echo -e "${GREEN}SSE Endpoint:${NC} https://$ROUTE_URL/mcp/sse"
    echo ""
    echo -e "${YELLOW}Test connection:${NC}"
    echo "  curl https://$ROUTE_URL/q/health"
    echo ""
    echo -e "${YELLOW}View logs:${NC}"
    echo "  oc logs -f deployment/keycloak-mcp-server"
    echo ""
else
    echo -e "${YELLOW}⚠ Warning: Could not get route URL${NC}"
    echo "  Run: oc get route keycloak-mcp-server"
fi

echo -e "${YELLOW}Configuration:${NC}"
echo "  Keycloak URL: $KEYCLOAK_URL"
echo "  Keycloak User: $KEYCLOAK_USER"
echo "  Project: $PROJECT_NAME"
echo ""

if [[ "$USE_CA_CERT" == "true" ]]; then
    echo -e "${YELLOW}TLS Configuration:${NC}"
    echo "  CA Certificate: Configured"
    echo "  📖 For more TLS options, see: deploy/openshift/KEYCLOAK_TLS_SETUP.md"
    echo ""
fi

echo -e "${GREEN}✓ Deployment successful!${NC}"

