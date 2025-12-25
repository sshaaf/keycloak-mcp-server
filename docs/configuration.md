# Configuration

This guide covers environment variables, TLS configuration, and port settings.

## Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `KC_URL` | Keycloak server URL | - | Yes |
| `KC_REALM` | Default Keycloak realm | `master` | No |
| `OIDC_CLIENT_ID` | OIDC client ID for token validation | `mcp-server` | No |
| `QUARKUS_HTTP_PORT` | HTTP port | `0` (random) / `8080` (container) | No |
| `QUARKUS_LOG_LEVEL` | Log level | `INFO` | No |

## Port Configuration

The server uses smart port assignment:

| Mode | Port | Reason |
|------|------|--------|
| Local JAR/Native | `0` (random) | Avoids port conflicts in development |
| Container | `8080` (fixed) | Predictable networking for Docker/Kubernetes |

### Override the Port

```bash
# Environment variable
export QUARKUS_HTTP_PORT=8080
java -jar keycloak-mcp-server.jar

# Or system property
java -Dquarkus.http.port=8080 -jar keycloak-mcp-server.jar
```

## TLS Configuration

Production Keycloak uses HTTPS. Configure TLS trust based on your certificate type.

### Trusted CA Certificate

If Keycloak uses a certificate from a trusted CA (Let's Encrypt, DigiCert, etc.), no additional configuration is needed.

```yaml
# configmap.yaml
data:
  keycloak-url: "https://keycloak.example.com"
```

### Self-Signed Certificate

#### Step 1: Extract the CA Certificate

```bash
# From Keycloak Operator TLS secret
oc get secret example-tls-secret -n keycloak \
  -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Or from running Keycloak
openssl s_client -connect keycloak.example.com:443 -showcerts 2>/dev/null | \
  openssl x509 -outform PEM > keycloak-ca.crt
```

#### Step 2: Create ConfigMap

```bash
oc create configmap keycloak-ca-bundle \
  --from-file=ca.crt=keycloak-ca.crt
```

#### Step 3: Mount in Deployment

```yaml
spec:
  template:
    spec:
      containers:
      - name: keycloak-mcp-server
        volumeMounts:
        - name: keycloak-ca-cert
          mountPath: /certs
          readOnly: true
        env:
        - name: JAVA_OPTS
          value: "-Djavax.net.ssl.trustStore=/certs/cacerts -Djavax.net.ssl.trustStorePassword=changeit"
      volumes:
      - name: keycloak-ca-cert
        configMap:
          name: keycloak-ca-bundle
```

### OpenShift Service CA

When Keycloak uses OpenShift's service CA:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: keycloak-ca-bundle
  annotations:
    service.beta.openshift.io/inject-cabundle: "true"
data: {}
```

OpenShift automatically injects the service CA certificate.

### Development Only: Disable TLS Verification

**Never use in production!**

```bash
docker run -d \
  -e QUARKUS_TLS_TRUST_ALL=true \
  -e QUARKUS_REST_CLIENT_TRUST_ALL=true \
  ...
```

## Application Properties

Key settings in `application.properties`:

```properties
# OIDC Configuration
quarkus.oidc.auth-server-url=${KC_URL}/realms/${KC_REALM:master}
quarkus.oidc.client-id=${OIDC_CLIENT_ID:mcp-server}
quarkus.oidc.application-type=service

# Security - Require auth for MCP endpoints
quarkus.http.auth.permission.mcp.paths=/mcp/*
quarkus.http.auth.permission.mcp.policy=authenticated

# Public endpoints
quarkus.http.auth.permission.public.paths=/q/*
quarkus.http.auth.permission.public.policy=permit

# Development mode - disable auth
%dev.quarkus.http.auth.permission.mcp.policy=permit
%dev.quarkus.oidc.enabled=false

# HTTP Server
quarkus.http.port=0
quarkus.http.host=0.0.0.0

# CORS
quarkus.http.cors=true
quarkus.http.cors.origins=*
```

## Container Image Configuration

The container is built using Jib with these settings:

```properties
quarkus.container-image.registry=quay.io
quarkus.container-image.group=sshaaf
quarkus.container-image.name=keycloak-mcp-server

# Automatic git SHA tagging
quarkus.container-image.tag=@git.commit.id.abbrev@
quarkus.container-image.additional-tags=latest

# Base image and platforms
quarkus.jib.base-jvm-image=registry.access.redhat.com/ubi9/openjdk-21-runtime:1.20
quarkus.jib.platforms=linux/amd64,linux/arm64

# Container port override
quarkus.jib.environment-variables.QUARKUS_HTTP_PORT=8080
```

## Resource Limits (Kubernetes)

Recommended settings:

```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "100m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```

Adjust based on workload:

```bash
oc set resources deployment/keycloak-mcp-server \
  --requests=cpu=200m,memory=512Mi \
  --limits=cpu=1000m,memory=1Gi
```

## Health Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/q/health` | Combined health check |
| `/q/health/live` | Liveness probe |
| `/q/health/ready` | Readiness probe |
| `/q/health/started` | Startup probe |
| `/q/metrics` | Prometheus metrics |

## Troubleshooting

### "PKIX path building failed"

Java doesn't trust the Keycloak certificate.

```bash
# Verify CA is mounted
oc exec deployment/keycloak-mcp-server -- ls -la /certs

# Check certificate validity
openssl s_client -connect keycloak.example.com:443 -showcerts
```

### "Connection refused"

```bash
# Check Keycloak is accessible
curl -v https://keycloak.example.com

# From inside the pod
oc exec deployment/keycloak-mcp-server -- curl -v $KC_URL
```

### Port already in use

```bash
# Find what's using the port
lsof -i :8080

# Use a different port
QUARKUS_HTTP_PORT=9090 java -jar keycloak-mcp-server.jar
```

