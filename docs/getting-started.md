# Getting Started with Keycloak MCP Server

This guide will help you get started with the Keycloak MCP Server using different deployment methods.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Deployment Options](#deployment-options)
 - [Option 1: Docker Container (Local)](#option-1-docker-container-local)
 - [Option 2: OpenShift Deployment](#option-2-openshift-deployment)
 - [Option 3: Native Binary](#option-3-native-binary)
 - [Option 4: Development Mode (Username/Password)](#option-4-development-mode-usernamepassword)
3. [Configuring Cursor IDE](#configuring-cursor-ide)
4. [Testing Your Setup](#testing-your-setup)
5. [Available Operations](#available-operations)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### For All Deployment Methods

- **Keycloak Instance**: A running Keycloak server (v22.0+)
 - Local: `http://localhost:8180` or `https://localhost:8543`
 - Production: `https://keycloak.example.com`
- **Cursor IDE**: Latest version with MCP support

### For Authentication

- Keycloak instance running (v20.0 or later)
- Keycloak user account with appropriate permissions
- Access to obtain JWT tokens from Keycloak

### For Development Mode (Legacy)

- Keycloak admin username and password

---

## Deployment Options

## Option 1: Docker Container (Local)

### Step 1: Pull the Container Image

```bash
docker pull quay.io/sshaaf/keycloak-mcp-server:latest
```

### Step 2: Configure Keycloak Client

**Note**: The MCP server validates JWT tokens using OIDC discovery. No client secret is required.

1. (Optional) Create a client in Keycloak for OIDC validation:
   - Client ID: `mcp-server`
   - Client authentication: **OFF** (public client)
   - Standard flow: **ON**
   - Direct access grants: **ON**

2. Users authenticate with their own Keycloak credentials to obtain JWT tokens

### Step 3: Run the Container

**Production/Remote Keycloak:**

```bash
docker run -d \
 --name keycloak-mcp-server \
 -p 8080:8080 \
 -e KC_URL=https://keycloak.example.com \
 -e KC_REALM=master \
 -e OIDC_CLIENT_ID=mcp-server \
 quay.io/sshaaf/keycloak-mcp-server:latest
```

**Local Keycloak (macOS/Windows):**

```bash
docker run -d \
 --name keycloak-mcp-server \
 -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 -e KC_REALM=master \
 -e OIDC_CLIENT_ID=mcp-server \
 quay.io/sshaaf/keycloak-mcp-server:latest
```

**Note:** Use `host.docker.internal` instead of `localhost` to access services running on your host

**Local Keycloak (Linux):**

```bash
docker run -d \
 --name keycloak-mcp-server \
 --network host \
 -e KC_URL=http://localhost:8180 \
 -e KC_REALM=master \
 -e OIDC_CLIENT_ID=mcp-server \
 quay.io/sshaaf/keycloak-mcp-server:latest
```

### Step 4: Verify the Container is Running

```bash
# Check container status
docker ps | grep keycloak-mcp-server

# Check logs
docker logs keycloak-mcp-server

# Test health endpoint
curl http://localhost:8080/q/health
```

### Step 5: Configure Cursor MCP

Edit `~/.cursor/mcp.json`:

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 }
 }
}
```

### Step 6: Reload MCP Servers in Cursor

1. Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: **"Reload MCP Servers"**
3. Select it

### Step 7: Test the Connection

In Cursor chat, ask:

```
List all Keycloak realms
```

**Expected result:** You should see a list of all realms from your Keycloak instance.

---

## Option 2: OpenShift Deployment

### Step 1: Prerequisites

- OpenShift cluster access (v4.x or later)
- `oc` CLI installed and configured
- Keycloak instance deployed in OpenShift with HTTPS enabled

./setup-service-account.sh \
 --keycloak-url https://keycloak.apps.example.com \
 --admin-user admin \
 --admin-password your-admin-password \
 --client-id mcp-server \
 --realm master
```

Or follow the manual steps in [authentication.md](authentication.md).

### Step 3: Deploy MCP Server

1. **Create a new project (or use existing):**

```bash
oc new-project keycloak-mcp-server
# Or use existing project
# oc project your-project
```

2. **Create ConfigMap:**

```bash
oc create configmap keycloak-mcp-config \
 --from-literal=keycloak-url=https://keycloak.apps.example.com \
 --from-literal=keycloak-realm=master \
 --from-literal=client-id=mcp-server
```

3. **Create Secret with Client Credentials:**

```bash
# Replace with your actual client secret from Step 2
oc create secret generic keycloak-mcp-secret \
 --from-literal=client-secret=your-client-secret-from-keycloak
```

4. **Extract Keycloak CA Certificate (if using self-signed certs):**

```bash
# Find the TLS secret name
KC_TLS_SECRET=$(oc get keycloak -o jsonpath='{.items[0].spec.http.tlsSecret}' -n keycloak)

# Extract and create CA bundle
oc get secret $KC_TLS_SECRET -n keycloak -o jsonpath='{.data.tls\.crt}' | \
 base64 -d > /tmp/keycloak-ca.crt

oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=/tmp/keycloak-ca.crt

rm /tmp/keycloak-ca.crt
```

5. **Deploy the MCP Server:**

```bash
# Clone the repository
git clone https://github.com/sshaaf/keycloak-mcp-server.git
cd keycloak-mcp-server

# Apply deployment manifests
oc apply -f deploy/openshift/deployment.yaml
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml
```

Or use the automated deployment script:

```bash
cd keycloak-mcp-server/deploy/openshift
chmod +x deploy.sh
./deploy.sh
```

### Step 4: Get the Route URL

```bash
oc get route keycloak-mcp-server -o jsonpath='{.spec.host}'
# Example output: keycloak-mcp-server-your-project.apps.example.com
```

### Step 5: Verify Deployment

```bash
# Check pod status
oc get pods -l app=keycloak-mcp-server

# Check logs
oc logs -f deployment/keycloak-mcp-server

# Test health endpoint
ROUTE_URL=$(oc get route keycloak-mcp-server -o jsonpath='{.spec.host}')
curl https://$ROUTE_URL/q/health
```

### Step 6: Configure Cursor MCP

Edit `~/.cursor/mcp.json`:

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "https://keycloak-mcp-server-your-project.apps.example.com/mcp/sse"
 }
 }
}
```

**Replace** `keycloak-mcp-server-your-project.apps.example.com` with your actual route URL from Step 4.

### Step 7: Reload MCP Servers in Cursor

1. Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: **"Reload MCP Servers"**
3. Select it

### Step 8: Test the Connection

In Cursor chat, ask:

```
List all users from the master realm
```

**Expected result:** You should see a list of users from your Keycloak master realm.

---

## Option 3: Native Binary

### Step 1: Download the Native Binary

Download the latest native binary for your platform from the [releases page](https://github.com/sshaaf/keycloak-mcp-server/releases):

**Linux (x64):**
```bash
wget https://github.com/sshaaf/keycloak-mcp-server/releases/download/v0.3.0/keycloak-mcp-server-linux-x64
chmod +x keycloak-mcp-server-linux-x64
```

**macOS (x64):**
```bash
wget https://github.com/sshaaf/keycloak-mcp-server/releases/download/v0.3.0/keycloak-mcp-server-darwin-x64
chmod +x keycloak-mcp-server-darwin-x64
```

**macOS (ARM64):**
```bash
wget https://github.com/sshaaf/keycloak-mcp-server/releases/download/v0.3.0/keycloak-mcp-server-darwin-arm64
chmod +x keycloak-mcp-server-darwin-arm64
```

Or build from source:

```bash
git clone https://github.com/sshaaf/keycloak-mcp-server.git
cd keycloak-mcp-server
mvn clean package -Dnative -DskipTests
```

### Step 2: Set Up Keycloak Service Account

Follow the steps in [authentication.md](authentication.md) to create a OIDC client in Keycloak.

### Step 3: Create Environment Configuration

Create a `.env` file:

```bash
cat > keycloak-mcp.env << EOF
KC_URL=https://keycloak.example.com
KC_REALM=master
OIDC_CLIENT_ID=mcp-server
QUARKUS_HTTP_PORT=8080
EOF
```

### Step 4: Run the Native Binary

```bash
# Load environment variables
export $(cat keycloak-mcp.env | xargs)

# Run the native binary
./keycloak-mcp-server-linux-x64
# Or for macOS: ./keycloak-mcp-server-darwin-arm64
```

**Run as Background Service (Linux/macOS):**

```bash
# Create systemd service (Linux)
sudo tee /etc/systemd/system/keycloak-mcp-server.service > /dev/null <<EOF
[Unit]
Description=Keycloak MCP Server
After=network.target

[Service]
Type=simple
User=mcp-server
EnvironmentFile=/opt/keycloak-mcp/keycloak-mcp.env
ExecStart=/opt/keycloak-mcp/keycloak-mcp-server-linux-x64
Restart=always

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable keycloak-mcp-server
sudo systemctl start keycloak-mcp-server
```

### Step 5: Verify the Service is Running

```bash
# Check if port is listening
lsof -i :8080
# Or: netstat -tuln | grep 8080

# Test health endpoint
curl http://localhost:8080/q/health
```

### Step 6: Configure Cursor MCP

Edit `~/.cursor/mcp.json`:

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 }
 }
}
```

### Step 7: Reload MCP Servers in Cursor

1. Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: **"Reload MCP Servers"**
3. Select it

### Step 8: Test the Connection

In Cursor chat, ask:

```
List all authentication flows from the master realm
```

**Expected result:** You should see a list of authentication flows from your Keycloak master realm.

---

## Option 4: Development Mode (Username/Password)

> **Warning**: This method is for **development only**. Use user authentication authentication for production.

### Step 1: Clone and Build the Project

```bash
git clone https://github.com/sshaaf/keycloak-mcp-server.git
cd keycloak-mcp-server
```

### Step 2: Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Keycloak Connection (Development Mode)
quarkus.keycloak.admin-client.server-url=http://localhost:8180
quarkus.keycloak.admin-client.username=admin
quarkus.keycloak.admin-client.password=admin

# Dev services disabled (using external Keycloak)
quarkus.keycloak.devservices.enabled=false

# HTTP Configuration
quarkus.http.port=8080
quarkus.http.host=0.0.0.0
```

Or use environment variables:

```bash
export KC_URL=http://localhost:8180
export KC_REALM=master
export OIDC_CLIENT_ID=mcp-server
```

**Note**: Development mode disables authentication for convenience. No credentials are required.

### Step 3: Run in Development Mode

```bash
mvn quarkus:dev
```

The server will start on `http://localhost:8080` with hot-reload enabled.

**Features in Dev Mode:**
- Hot reload (code changes apply automatically)
- Dev UI available at `http://localhost:8080/q/dev`
- Detailed error messages
- Continuous testing mode

### Step 4: Verify the Server is Running

```bash
# Test health endpoint
curl http://localhost:8080/q/health

# Access Dev UI
open http://localhost:8080/q/dev
```

### Step 5: Configure Cursor MCP

Edit `~/.cursor/mcp.json`:

```json
{
 "mcpServers": {
 "keycloak-dev": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 }
 }
}
```

### Step 6: Reload MCP Servers in Cursor

1. Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: **"Reload MCP Servers"**
3. Select it

### Step 7: Test the Connection

In Cursor chat, ask:

```
Count all users in Keycloak
```

**Expected result:** You should see the total count of users across all realms.

### Step 8: Making Changes (Optional)

Since you are in development mode, you can make code changes:

1. Edit any Java file in `src/main/java/`
2. Save the file
3. Quarkus will automatically reload
4. Reload MCP servers in Cursor to see changes

---

## Configuring Cursor IDE

### MCP Configuration File Location

**macOS/Linux:**
```bash
~/.cursor/mcp.json
```

**Windows:**
```bash
%USERPROFILE%\.cursor\mcp.json
```

### Configuration Examples

#### Single MCP Server

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 }
 }
}
```

#### Multiple MCP Servers

```json
{
 "mcpServers": {
 "keycloak-prod": {
 "transport": "sse",
 "url": "https://keycloak-mcp-server.apps.example.com/mcp/sse"
 },
 "keycloak-dev": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 }
 }
}
```

#### With Additional MCP Servers

```json
{
 "mcpServers": {
 "keycloak": {
 "transport": "sse",
 "url": "http://localhost:8080/mcp/sse"
 },
 "context7": {
 "url": "https://mcp.context7.com/mcp",
 "headers": {}
 }
 }
}
```

### Reloading MCP Configuration

After editing `mcp.json`:

1. **Command Palette Method:**
 - Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
 - Type: "Reload MCP Servers"
 - Press Enter

2. **Restart Cursor:**
 - Close and reopen Cursor

---

## Testing Your Setup

### Quick Test Commands

Try these commands in Cursor chat to verify everything is working:

#### Test 1: List Realms
```
List all Keycloak realms
```

**Expected Output:**
```
Found 3 realms:
1. master - Red Hat build of Keycloak (Enabled)
2. quarkus (Enabled)
3. test (Enabled)
```

#### Test 2: Count Users
```
How many users are in the master realm?
```

**Expected Output:**
```
There are X users in the master realm.
```

#### Test 3: List Clients
```
Show me all clients in the master realm
```

**Expected Output:**
```
Found X clients:
- account
- admin-cli
- broker
- master-realm
- mcp-server
- ...
```

#### Test 4: Search Discourse
```
Search Keycloak Discourse for "LDAP integration"
```

**Expected Output:**
```
Found X topics related to "LDAP integration":
1. [Topic Title] - https://keycloak.discourse.group/...
2. ...
```

### Health Check Endpoints

Test the MCP server directly:

```bash
# Health check
curl http://localhost:8080/q/health

# Liveness probe
curl http://localhost:8080/q/health/live

# Readiness probe
curl http://localhost:8080/q/health/ready

# MCP SSE endpoint
curl -N http://localhost:8080/mcp/sse
```

---

## Available Operations

The Keycloak MCP Server supports the following operations:

### User Operations
- `GET_USERS` - List all users
- `GET_USER_BY_USERNAME` - Get user by username
- `GET_USER_BY_ID` - Get user by ID
- `CREATE_USER` - Create a new user
- `DELETE_USER` - Delete a user
- `UPDATE_USER` - Update user details
- `GET_USER_GROUPS` - Get user's groups
- `GET_USER_ROLES` - Get user's roles
- `ADD_USER_TO_GROUP` - Add user to group
- `REMOVE_USER_FROM_GROUP` - Remove user from group
- `ADD_ROLE_TO_USER` - Assign role to user
- `REMOVE_ROLE_FROM_USER` - Remove role from user
- `RESET_PASSWORD` - Reset user password
- `SEND_VERIFICATION_EMAIL` - Send verification email
- `COUNT_USERS` - Count users in realm

### Realm Operations
- `GET_REALMS` - List all realms
- `GET_REALM` - Get realm details
- `CREATE_REALM` - Create a new realm

### Client Operations
- `GET_CLIENTS` - List all clients
- `GET_CLIENT` - Get client details
- `CREATE_CLIENT` - Create a new client
- `DELETE_CLIENT` - Delete a client
- `GENERATE_CLIENT_SECRET` - Generate new client secret
- `GET_CLIENT_ROLES` - Get client roles
- `CREATE_CLIENT_ROLE` - Create client role
- `DELETE_CLIENT_ROLE` - Delete client role

### Role Operations
- `GET_REALM_ROLES` - List realm roles
- `GET_REALM_ROLE` - Get realm role details

### Group Operations
- `GET_GROUPS` - List all groups
- `GET_GROUP_MEMBERS` - Get group members
- `CREATE_GROUP` - Create a group
- `UPDATE_GROUP` - Update group details
- `DELETE_GROUP` - Delete a group
- `CREATE_SUBGROUP` - Create a subgroup

### Identity Provider Operations
- `GET_IDENTITY_PROVIDERS` - List identity providers
- `GET_IDENTITY_PROVIDER` - Get identity provider details
- `GET_IDENTITY_PROVIDER_MAPPERS` - Get identity provider mappers

### Authentication Flow Operations
- `GET_AUTHENTICATION_FLOWS` - List authentication flows
- `GET_AUTHENTICATION_FLOW` - Get flow details
- `CREATE_AUTHENTICATION_FLOW` - Create a flow
- `DELETE_AUTHENTICATION_FLOW` - Delete a flow
- `GET_FLOW_EXECUTIONS` - Get flow executions
- `UPDATE_FLOW_EXECUTION` - Update flow execution

### Discourse Operations
- `SEARCH_DISCOURSE` - Search Keycloak community forum

### Example Usage in Cursor Chat

```
# User management
"Create a user named john.doe with email john@example.com in the master realm"
"List all users in the quarkus realm"
"Add user alice to the admin group"

# Client management
"Show me all clients in the master realm"
"Create a new confidential client named my-app"
"Generate a new secret for the my-app client"

# Realm management
"List all realms"
"Show me the details of the quarkus realm"
"Create a new realm named production"

# Authentication flows
"List all authentication flows in the master realm"
"Show me the browser authentication flow"

# Discourse search
"Search Keycloak Discourse for 'SAML configuration'"
"Find topics about user federation"
```

---

## Troubleshooting

### Issue: MCP Server Not Connecting

**Symptoms:**
- Cursor shows "MCP server not responding"
- Tools not available in chat

**Solutions:**

1. **Check if the server is running:**
 ```bash
 # For Docker
 docker ps | grep keycloak-mcp-server
 docker logs keycloak-mcp-server

 # For OpenShift
 oc get pods -l app=keycloak-mcp-server
 oc logs -f deployment/keycloak-mcp-server

 # For native binary
 ps aux | grep keycloak-mcp-server
 ```

2. **Test the health endpoint:**
 ```bash
 curl http://localhost:8080/q/health
 ```

3. **Check the MCP SSE endpoint:**
 ```bash
 curl -N http://localhost:8080/mcp/sse
 ```

4. **Verify mcp.json configuration:**
 - Check the URL is correct
 - Ensure no trailing slashes
 - Verify the port number

5. **Reload MCP servers in Cursor:**
 - `Cmd+Shift+P` → "Reload MCP Servers"

---

### Issue: SSL/TLS Certificate Errors

**Symptoms:**
- `SSLHandshakeException: Failed to create SSL connection`
- Certificate validation errors in logs

**Solutions:**

1. **For Docker (Development):**
 ```bash
 docker run -d \
 -e QUARKUS_TLS_TRUST_ALL=true \
 -e QUARKUS_REST_CLIENT_TRUST_ALL=true \
 ...
 ```

2. **For OpenShift (Production):**
 - Ensure CA certificate ConfigMap is created
 - Verify volume mount in deployment
 - See [keycloak-tls-setup.md](keycloak-tls-setup.md)

3. **For Native Binary:**
 ```bash
 export QUARKUS_TLS_TRUST_ALL=true
 ./keycloak-mcp-server-linux-x64
 ```

---

### Issue: Authentication Failures

**Symptoms:**
- `401 Unauthorized` errors
- `403 Forbidden` errors
- "Invalid credentials" in logs

**Solutions:**

1. **Verify Keycloak URL:**
 ```bash
 curl -I https://keycloak.example.com
 ```

2. **For JWT Authentication:**
 - Verify OIDC client exists in Keycloak (mcp-server)
 - Ensure user has proper roles and permissions
 - Test user token:
 ```bash
 curl -X POST https://keycloak.example.com/realms/master/protocol/openid-connect/token \
 -d 'grant_type=password' \
 -d 'client_id=admin-cli' \
 -d 'username=your-username' \
 -d 'client_secret=your-secret'
 ```

3. **For Username/Password:**
 - Verify admin username and password
 - Check user has admin privileges
 - Test login:
 ```bash
 curl -X POST https://keycloak.example.com/realms/master/protocol/openid-connect/token \
 -d 'grant_type=password' \
 -d 'client_id=admin-cli' \
 -d 'username=admin' \
 -d 'password=admin'
 ```

---

### Issue: Connection Refused / Network Errors

**Symptoms:**
- `Connection refused` errors
- `No route to host` errors

**Solutions:**

1. **Check Keycloak is running:**
 ```bash
 curl http://localhost:8180
 # Or for HTTPS
 curl -k https://localhost:8543
 ```

2. **For Docker on macOS/Windows:**
 - Use `host.docker.internal` instead of `localhost`

3. **For Docker on Linux:**
 - Use `--network host` or configure bridge networking

4. **Check firewall rules:**
 ```bash
 # Linux
 sudo firewall-cmd --list-all

 # macOS
 sudo pfctl -sr
 ```

5. **Verify port is not in use:**
 ```bash
 lsof -i :8080
 ```

---

### Issue: MCP Tools Not Appearing in Cursor

**Symptoms:**
- No Keycloak operations available
- Can't execute Keycloak commands

**Solutions:**

1. **Verify MCP server is initialized:**
 - Look for initialization message in server logs
 - Check Cursor's developer console (Help → Toggle Developer Tools)

2. **Reload MCP servers:**
 - `Cmd+Shift+P` → "Reload MCP Servers"

3. **Restart Cursor completely**

4. **Check mcp.json syntax:**
 ```bash
 # Validate JSON
 cat ~/.cursor/mcp.json | python -m json.tool
 ```

---

### Issue: Slow Response Times

**Symptoms:**
- Operations take a long time
- Timeouts in Cursor

**Solutions:**

1. **Check Keycloak performance:**
 - Database connections
 - Memory usage
 - Network latency

2. **Increase container resources:**
 ```bash
 # Docker
 docker update --memory 1g --cpus 2 keycloak-mcp-server
 ```

3. **For OpenShift, adjust resource limits:**
 ```yaml
 resources:
 requests:
 memory: "512Mi"
 cpu: "250m"
 limits:
 memory: "1Gi"
 cpu: "1000m"
 ```

---

### Getting Help

If you are still experiencing issues:

1. **Check the logs:**
 ```bash
 # Docker
 docker logs keycloak-mcp-server --tail 100

 # OpenShift
 oc logs deployment/keycloak-mcp-server --tail 100

 # Development mode
 # Logs appear in terminal
 ```

2. **Enable debug logging:**
 ```bash
 # Add to environment
 export QUARKUS_LOG_LEVEL=DEBUG
 ```

3. **Check documentation:**
 - [index.md](index.md)
 - [authentication.md](authentication.md)
 - [openshift-deployment.md](openshift-deployment.md)
 - [keycloak-tls-setup.md](keycloak-tls-setup.md)

4. **Open an issue:**
 - [GitHub Issues](https://github.com/sshaaf/keycloak-mcp-server/issues)
 - Include logs, configuration, and error messages

5. **Community support:**
 - [Keycloak Discourse](https://keycloak.discourse.group)
 - [Quarkus Discord](https://quarkus.io/community/)

---

## Next Steps

Once you have the MCP server running:

1. **Explore Available Operations**: Try different Keycloak management commands
2. **Automate Tasks**: Use the MCP server to automate user provisioning, client setup, etc.
3. **Secure Your Setup**: Migrate to user authentication authentication if using username/password
4. **Monitor Performance**: Set up logging and monitoring
5. **Deploy to Production**: Follow the OpenShift deployment guide for production setup

## Additional Resources

- **Documentation**: [index.md](index.md)
- **Authentication Guide**: [authentication.md](authentication.md)
- **OpenShift Deployment**: [openshift-deployment.md](openshift-deployment.md)
- **TLS Configuration**: [keycloak-tls-setup.md](keycloak-tls-setup.md)
- **GitHub Repository**: [https://github.com/sshaaf/keycloak-mcp-server](https://github.com/sshaaf/keycloak-mcp-server)
- **Keycloak Documentation**: [https://www.keycloak.org/documentation](https://www.keycloak.org/documentation)

---

**Happy Keycloak management with MCP! **

