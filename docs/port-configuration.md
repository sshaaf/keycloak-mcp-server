# Port Configuration Guide

## Overview

The Keycloak MCP Server uses **smart port assignment** that automatically adapts to your deployment mode:

- **Local Development** (JAR/Native): Port `0` (random)
- **Container** (Docker/Kubernetes): Port `8080` (fixed)

## Why This Strategy?

### Port 0 for Local Development

**Benefits:**
- No port conflicts with other services
- Run multiple instances simultaneously
- No need to manually manage ports
- Perfect for CI/CD and automated tests

**How it works:**
When you set `quarkus.http.port=0`, the operating system automatically assigns an available port.

### Port 8080 for Containers

**Benefits:**
- Predictable networking for Docker/Kubernetes
- Easy port mapping (`-p 8080:8080`)
- Standard HTTP alternative port
- Works with load balancers and ingress

## Configuration

### application.properties

```properties
# SSE Server Configuration
# Port 0 = random port (avoids conflicts in local dev)
# Overridden to 8080 in containers via QUARKUS_HTTP_PORT env var
quarkus.http.port=0
quarkus.http.host=0.0.0.0

# Container configuration (Jib)
quarkus.jib.ports=8080
quarkus.jib.environment-variables.QUARKUS_HTTP_PORT=8080
```

## Usage Examples

### Local JAR Execution

```bash
# Build
mvn clean package

# Run with random port
java -jar target/quarkus-app/quarkus-run.jar

# Output shows assigned port:
# __ ____ __ _____ ___ __ ____ ______
# --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
# -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
# --\___\_\____/_/ |_/_/|_/_/|_|\____/___/
# Listening on: http://0.0.0.0:54321 ← Random port assigned
```

**Finding the assigned port:**
```bash
# From logs
grep "Listening on:" server.log

# Or use lsof
lsof -i -P | grep java
```

### Override Port Locally

If you need a specific port for local development:

```bash
# Option 1: System property
java -Dquarkus.http.port=8080 -jar target/quarkus-app/quarkus-run.jar

# Option 2: Environment variable
export QUARKUS_HTTP_PORT=8080
java -jar target/quarkus-app/quarkus-run.jar

# Option 3: Command line for Quarkus dev mode
mvn quarkus:dev -Dquarkus.http.port=8080
```

### Native Binary Execution

```bash
# Build native
mvn clean package -Pnative

# Run with random port
./target/keycloak-mcp-server-0.3.0-runner

# Override port
QUARKUS_HTTP_PORT=8080 ./target/keycloak-mcp-server-0.3.0-runner
```

### Container Execution

The container **automatically uses port 8080** via environment variable:

```bash
# Run container (port 8080 is automatic)
docker run -d \
 -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 -e KC_USER=admin \
 -e KC_PASSWORD=admin \
 quay.io/sshaaf/keycloak-mcp-server:latest

# Access SSE endpoint
curl http://localhost:8080/mcp/sse
```

### Override Container Port

You can override the container port if needed:

```bash
# Use different port in container
docker run -d \
 -p 9090:9090 \
 -e QUARKUS_HTTP_PORT=9090 \
 -e KC_URL=http://host.docker.internal:8180 \
 -e KC_USER=admin \
 -e KC_PASSWORD=admin \
 quay.io/sshaaf/keycloak-mcp-server:latest

# Access on custom port
curl http://localhost:9090/mcp/sse
```

## Kubernetes/OpenShift Deployment

### Standard Deployment (Port 8080)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: keycloak-mcp-server
spec:
 replicas: 1
 selector:
 matchLabels:
 app: keycloak-mcp-server
 template:
 metadata:
 labels:
 app: keycloak-mcp-server
 spec:
 containers:
 - name: server
 image: quay.io/sshaaf/keycloak-mcp-server:latest
 ports:
 - containerPort: 8080
 name: http
 env:
 - name: KC_URL
 value: "http://keycloak:8080"
 - name: KC_USER
 value: "admin"
 - name: KC_PASSWORD
 valueFrom:
 secretKeyRef:
 name: keycloak-admin
 key: password
---
apiVersion: v1
kind: Service
metadata:
 name: keycloak-mcp-server
spec:
 selector:
 app: keycloak-mcp-server
 ports:
 - name: http
 port: 80
 targetPort: 8080
 type: LoadBalancer
```

### Custom Port Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: keycloak-mcp-server
spec:
 template:
 spec:
 containers:
 - name: server
 image: quay.io/sshaaf/keycloak-mcp-server:latest
 ports:
 - containerPort: 9090
 name: http
 env:
 - name: QUARKUS_HTTP_PORT
 value: "9090"
 - name: KC_URL
 value: "http://keycloak:8080"
```

## Development Workflow

### Single Instance Development

```bash
# Just run - port assigned automatically
java -jar target/quarkus-app/quarkus-run.jar

# Check logs for actual port
# Listening on: http://0.0.0.0:54321
```

### Multiple Instances (Testing, Load Balancing)

```bash
# Terminal 1 - First instance
java -jar target/quarkus-app/quarkus-run.jar
# Listening on: http://0.0.0.0:54321

# Terminal 2 - Second instance (different port automatically)
java -jar target/quarkus-app/quarkus-run.jar
# Listening on: http://0.0.0.0:54322

# Terminal 3 - Third instance
java -jar target/quarkus-app/quarkus-run.jar
# Listening on: http://0.0.0.0:54323
```

No port conflicts! Each instance gets its own port automatically.

### CI/CD Testing

```bash
# In CI pipeline - no need to manage ports
for i in {1..10}; do
 java -jar target/quarkus-app/quarkus-run.jar &
done

# All 10 instances run simultaneously without conflicts
```

## Troubleshooting

### Issue: Can't Find the Assigned Port

**Solution:** Check the startup logs:

```bash
# Run and capture logs
java -jar target/quarkus-app/quarkus-run.jar 2>&1 | tee server.log

# Or grep for the port
java -jar target/quarkus-app/quarkus-run.jar 2>&1 | grep "Listening on"
```

### Issue: Want Fixed Port for Local Development

**Solution:** Override with environment variable:

```bash
# Add to your shell profile (~/.bashrc, ~/.zshrc, etc.)
export QUARKUS_HTTP_PORT=8080

# Or create a .env file
echo "QUARKUS_HTTP_PORT=8080" > .env
source .env
java -jar target/quarkus-app/quarkus-run.jar
```

### Issue: Container Not Responding on Port 8080

**Check:**
1. Verify port mapping: `docker ps` shows `0.0.0.0:8080->8080/tcp`
2. Check container logs: `docker logs <container-id>`
3. Verify environment variable: `docker exec <container-id> env | grep QUARKUS_HTTP_PORT`

**Solution:**
```bash
# Ensure port mapping is correct
docker run -p 8080:8080 quay.io/sshaaf/keycloak-mcp-server:latest

# Verify container is listening
docker exec <container-id> netstat -tlnp | grep 8080
```

### Issue: Port Already in Use (Local Override)

**Solution:**
```bash
# Find what is using the port
lsof -i :8080

# Use a different port
QUARKUS_HTTP_PORT=9090 java -jar target/quarkus-app/quarkus-run.jar

# Or use port 0 (default - random port)
unset QUARKUS_HTTP_PORT
java -jar target/quarkus-app/quarkus-run.jar
```

## Port Discovery in Code

If you need to programmatically discover the assigned port:

### Java Code

```java
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.logging.Log;

public class PortDiscovery {

 @ConfigProperty(name = "quarkus.http.port")
 int httpPort;

 void onStart(@Observes StartupEvent ev) {
 Log.info("Server started on port: " + httpPort);
 }
}
```

### Shell Script

```bash
#!/bin/bash

# Start server in background
java -jar target/quarkus-app/quarkus-run.jar > server.log 2>&1 &
PID=$!

# Wait for startup
sleep 5

# Extract port from logs
PORT=$(grep "Listening on:" server.log | grep -oP 'http://[^:]+:\K\d+')

echo "Server running on port: $PORT"
echo "SSE endpoint: http://localhost:$PORT/mcp/sse"

# Use the port
curl "http://localhost:$PORT/mcp/sse"
```

## Summary

| Scenario | Configuration | Port |
|----------|--------------|------|
| Local JAR/Native | Default | Random (0) |
| Local with override | `QUARKUS_HTTP_PORT=8080` | 8080 |
| Container (default) | Automatic via Jib | 8080 |
| Container (custom) | `-e QUARKUS_HTTP_PORT=9090` | 9090 |
| Kubernetes/OpenShift | Default | 8080 |
| Multiple local instances | Default | Random (each different) |

## Benefits Summary

 **No Port Conflicts**: Random ports eliminate conflicts in local development
 **Parallel Testing**: Run multiple instances simultaneously
 **Container-Friendly**: Fixed port 8080 for predictable networking
 **Flexible**: Easy to override when needed
 **CI/CD Ready**: No port management in automated pipelines
 **Production Ready**: Fixed ports for containers and cloud deployments

---

**Default Behavior:**
- **Local**: Port `0` (random) - perfect for development
- **Container**: Port `8080` (fixed) - perfect for deployment

**The best of both worlds!**

