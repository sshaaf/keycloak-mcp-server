# OpenShift Deployment Guide

## Overview

This guide explains how to deploy the Keycloak MCP Server container to OpenShift using various methods.

## Prerequisites

- OpenShift cluster access (v4.x or later)
- `oc` CLI installed and configured
- Appropriate permissions to create resources in a namespace
- **Keycloak instance with HTTPS enabled** (production requirement)
- Keycloak CA certificate (if using self-signed certificates)

## Important: User-Based Authentication (v0.4.0+)

** NEW: The MCP Server uses JWT Bearer tokens - each user authenticates with their OWN credentials!**

 **See [AUTHENTICATION.md](AUTHENTICATION.md) for complete authentication guide**

**How it works:**
```
User → Gets JWT token from Keycloak
 ↓
User → MCP Server (with JWT in headers)
 ↓
MCP Server → Validates token (OIDC)
 ↓
MCP Server → Keycloak API (with user's token)
 ↓
Keycloak → Enforces user's permissions
```

**Benefits:**
- Each user has their own permissions (no shared admin access)
- Keycloak enforces its existing permission system
- Realm isolation works automatically
- Role-based access control (admin/manager/viewer)
- Full audit trail by user
- **No service account needed!**

**Getting your token:**
```bash
./scripts/get-mcp-token.sh \
 --keycloak-url https://keycloak.example.com \
 --username your-username \
 --password your-password
```


## Important: HTTPS Keycloak Configuration

** Production Keycloak always uses HTTPS.** Before deploying, configure TLS trust:

 **See [keycloak-tls-setup.md](deploy/openshift/keycloak-tls-setup.md) for complete TLS configuration guide**

Quick summary:
- **Trusted CA**: No additional configuration needed
- **Self-signed**: Extract and mount CA certificate
- **OpenShift Service CA**: Use ConfigMap annotation for auto-injection
- **Keycloak Operator**: Extract certificate from Keycloak TLS secret

Reference: [Keycloak Operator Installation Tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)

## Quick Start

### Method 1: Using oc CLI (Recommended)

```bash
# 1. Login to OpenShift
oc login --server=https://your-openshift-cluster:6443

# 2. Create a new project
oc new-project keycloak-mcp

# 3. Deploy using manifests
oc apply -f deploy/openshift/

# 4. Get the route URL
oc get route keycloak-mcp-server -o jsonpath='{.spec.host}'
```

### Method 2: Using Kustomize

```bash
# Deploy with kustomize
oc apply -k deploy/openshift/

# Check deployment status
oc get pods -l app=keycloak-mcp-server
```

### Method 3: Using OpenShift Web Console

1. Log in to OpenShift Web Console
2. Navigate to **Developer** perspective
3. Click **+Add** → **Import from Git** or **Container Image**
4. Use image: `quay.io/sshaaf/keycloak-mcp-server:latest`
5. Configure environment variables and create

## Detailed Deployment

### Step 1: Create Project/Namespace

```bash
# Create a dedicated project
oc new-project keycloak-mcp

# Or use existing project
oc project keycloak-mcp
```

### Step 2: Configure Keycloak Connection

** Important: Use HTTPS URL for production Keycloak**

**Update ConfigMap** (`deploy/openshift/configmap.yaml`):

```yaml
data:
 # Production Keycloak HTTPS URL
 keycloak-url: "https://keycloak.rhbk.apps.example.com"
 # OR if using internal service (with custom CA)
 # keycloak-url: "https://keycloak.keycloak-system.svc.cluster.local"
```

**Note**: No secret is required. The MCP server validates JWT tokens using public OIDC discovery.

Previous versions required a secret, but with JWT authentication, no secrets are needed for deployment.

**Environment Variables** (in `deploy/openshift/deployment.yaml`):

```yaml
stringData:
 username: "your-keycloak-admin"
 password: "your-keycloak-password"
```

**Or create Secret from command line**:

```bash
oc create secret generic keycloak-mcp-secret \
 --from-literal=username=admin \
 --from-literal=password='YourSecurePassword123!'
```

**If using self-signed certificates, configure CA trust:**

```bash
# Extract CA certificate from Keycloak TLS secret
oc get secret example-tls-secret -n rhbk \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Create CA ConfigMap
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt

# See keycloak-tls-setup.md for complete configuration
```

### Step 3: Apply Manifests

```bash
# Apply all resources
oc apply -f deploy/openshift/configmap.yaml
oc apply -f deploy/openshift/deployment.yaml
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml
```

**Or apply all at once**:

```bash
oc apply -f deploy/openshift/
```

### Step 4: Verify Deployment

```bash
# Check pods
oc get pods -l app=keycloak-mcp-server

# Check deployment
oc get deployment keycloak-mcp-server

# Check service
oc get svc keycloak-mcp-server

# Check route
oc get route keycloak-mcp-server

# View logs
oc logs -f deployment/keycloak-mcp-server
```

### Step 5: Access the Application

```bash
# Get the route URL
ROUTE_URL=$(oc get route keycloak-mcp-server -o jsonpath='{.spec.host}')
echo "Application URL: https://${ROUTE_URL}"

# Test SSE endpoint
curl https://${ROUTE_URL}/mcp/sse
```

## Deployment Options

### Option 1: Using Specific Image Tag

Deploy with a specific git commit SHA for production:

```bash
# Edit deployment.yaml or use kustomize
oc set image deployment/keycloak-mcp-server \
 keycloak-mcp-server=quay.io/sshaaf/keycloak-mcp-server:49ff54e
```

### Option 2: Using Helm (if available)

Create a Helm chart for more flexibility:

```bash
# Install using Helm
helm install keycloak-mcp-server ./charts/keycloak-mcp-server \
 --set image.tag=0.3.0 \
 --set keycloak.url=http://keycloak:8080 \
 --set keycloak.username=admin \
 --set keycloak.password=admin
```

### Option 3: Using OpenShift Template

Create and process an OpenShift template:

```bash
oc process -f deploy/openshift/template.yaml \
 -p KEYCLOAK_URL=http://keycloak:8080 \
 -p KEYCLOAK_USERNAME=admin \
 -p KEYCLOAK_PASSWORD=admin \
 | oc apply -f -
```

## Configuration

### Environment Variables

The deployment supports the following environment variables:

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `KC_URL` | Keycloak server URL | - | Yes |
| `KC_USER` | Keycloak admin username | - | Yes |
| `KC_PASSWORD` | Keycloak admin password | - | Yes |
| `QUARKUS_HTTP_PORT` | HTTP port | `8080` | No |
| `QUARKUS_LOG_LEVEL` | Log level | `INFO` | No |

### Resource Limits

Default resource configuration:

```yaml
resources:
 requests:
 memory: "256Mi"
 cpu: "100m"
 limits:
 memory: "512Mi"
 cpu: "500m"
```

**Adjust for your workload**:

```bash
oc set resources deployment/keycloak-mcp-server \
 --requests=cpu=200m,memory=512Mi \
 --limits=cpu=1000m,memory=1Gi
```

### Scaling

```bash
# Scale up
oc scale deployment/keycloak-mcp-server --replicas=3

# Enable autoscaling
oc autoscale deployment/keycloak-mcp-server \
 --min=1 --max=5 --cpu-percent=80
```

## Security

### Running as Non-Root

The container runs as non-root by default. To enforce:

```yaml
spec:
 template:
 spec:
 securityContext:
 runAsNonRoot: true
 seccompProfile:
 type: RuntimeDefault
 containers:
 - name: keycloak-mcp-server
 securityContext:
 allowPrivilegeEscalation: false
 capabilities:
 drop:
 - ALL
```

### Using Sealed Secrets

For production, use Sealed Secrets or External Secrets Operator:

```bash
# Create sealed secret
echo -n 'admin' | \
 kubectl create secret generic keycloak-mcp-secret \
 --dry-run=client \
 -o yaml | \
 kubeseal -o yaml > sealed-configmap.yaml

oc apply -f sealed-configmap.yaml
```

### Network Policies

Restrict network access:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
 name: keycloak-mcp-server-netpol
spec:
 podSelector:
 matchLabels:
 app: keycloak-mcp-server
 policyTypes:
 - Ingress
 - Egress
 ingress:
 - from:
 - namespaceSelector:
 matchLabels:
 name: openshift-ingress
 ports:
 - protocol: TCP
 port: 8080
 egress:
 - to:
 - namespaceSelector:
 matchLabels:
 name: keycloak-system
 ports:
 - protocol: TCP
 port: 8080
```

## Health Checks

The deployment includes comprehensive health checks:

### Liveness Probe
- Path: `/q/health/live`
- Checks if the application is alive
- Restarts pod if failing

### Readiness Probe
- Path: `/q/health/ready`
- Checks if application can accept traffic
- Removes from service if failing

### Startup Probe
- Path: `/q/health/started`
- Checks if application has started
- Gives more time for slow startup

## Monitoring

### Viewing Logs

```bash
# Stream logs
oc logs -f deployment/keycloak-mcp-server

# Last 100 lines
oc logs --tail=100 deployment/keycloak-mcp-server

# Previous container (if crashed)
oc logs deployment/keycloak-mcp-server --previous
```

### Metrics (if Prometheus is installed)

```bash
# Check metrics endpoint
oc exec deployment/keycloak-mcp-server -- \
 curl http://localhost:8080/q/metrics

# Create ServiceMonitor for Prometheus
cat <<EOF | oc apply -f -
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
 name: keycloak-mcp-server
 labels:
 app: keycloak-mcp-server
spec:
 selector:
 matchLabels:
 app: keycloak-mcp-server
 endpoints:
 - port: http
 path: /q/metrics
 interval: 30s
EOF
```

## Troubleshooting

### Pod Not Starting

```bash
# Check pod status
oc get pods -l app=keycloak-mcp-server

# Describe pod for events
oc describe pod -l app=keycloak-mcp-server

# Check logs
oc logs -l app=keycloak-mcp-server
```

### Connection to Keycloak Fails

```bash
# Test from pod
oc exec deployment/keycloak-mcp-server -- \
 curl -v $KC_URL

# Check DNS resolution
oc exec deployment/keycloak-mcp-server -- \
 nslookup keycloak.keycloak-system.svc.cluster.local

# Check network policy
oc get networkpolicy
```

### Image Pull Errors

```bash
# Check image pull secret (if using private registry)
oc get secret -n keycloak-mcp

# Create image pull secret
oc create secret docker-registry quay-pull-secret \
 --docker-server=quay.io \
 --docker-username=sshaaf \
 --docker-password=<token>

# Link to service account
oc secrets link default quay-pull-secret --for=pull
```

### Route Not Accessible

```bash
# Check route
oc get route keycloak-mcp-server

# Check if TLS is configured
oc describe route keycloak-mcp-server

# Test from inside cluster
oc run test --rm -it --image=curlimages/curl -- \
 curl http://keycloak-mcp-server/mcp/sse
```

## Advanced Configurations

### Using External Keycloak

If Keycloak is outside OpenShift (e.g., managed service, different cluster):

```yaml
# configmap.yaml
data:
 # External Keycloak with trusted CA certificate
 keycloak-url: "https://keycloak.example.com"
```

**If using custom CA or self-signed certificate:**

1. Get the CA certificate from your Keycloak administrator
2. Create ConfigMap:
 ```bash
 oc create configmap keycloak-ca-bundle --from-file=ca.crt=external-keycloak-ca.crt
 ```
3. Update deployment to mount the certificate (see [keycloak-tls-setup.md](deploy/openshift/keycloak-tls-setup.md))

**If using Keycloak Operator** (recommended approach from [this tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)):

```bash
# Keycloak is deployed with operator in 'rhbk' namespace
# Extract the TLS certificate
oc get secret example-tls-secret -n rhbk \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Create CA bundle in MCP namespace
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt \
 -n keycloak-mcp
```

### Blue-Green Deployment

```bash
# Create blue deployment
oc apply -f deploy/openshift/ -l version=blue

# Test blue
oc get route keycloak-mcp-server-blue

# Deploy green
oc apply -f deploy/openshift/ -l version=green

# Switch traffic
oc patch route keycloak-mcp-server \
 -p '{"spec":{"to":{"name":"keycloak-mcp-server-green"}}}'
```

### Using S2I (Source-to-Image)

Build from source using OpenShift S2I:

```bash
oc new-app quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21~https://github.com/sshaaf/keycloak-mcp-server \
 --name=keycloak-mcp-server \
 --env KC_URL=http://keycloak:8080 \
 --env KC_USER=admin \
 --env KC_PASSWORD=admin
```

## CI/CD Integration

### Using Tekton Pipelines

Create a Tekton pipeline for automated deployment:

```yaml
apiVersion: tekton.dev/v1beta1
kind: Pipeline
metadata:
 name: keycloak-mcp-deploy
spec:
 params:
 - name: image-tag
 description: Image tag to deploy
 default: latest
 tasks:
 - name: deploy
 taskRef:
 name: openshift-client
 params:
 - name: SCRIPT
 value: |
 oc set image deployment/keycloak-mcp-server \
 keycloak-mcp-server=quay.io/sshaaf/keycloak-mcp-server:$(params.image-tag)
 oc rollout status deployment/keycloak-mcp-server
```

### Using ArgoCD

Deploy using GitOps with ArgoCD:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
 name: keycloak-mcp-server
 namespace: argocd
spec:
 project: default
 source:
 repoURL: https://github.com/sshaaf/keycloak-mcp-server
 targetRevision: main
 path: deploy/openshift
 destination:
 server: https://kubernetes.default.svc
 namespace: keycloak-mcp
 syncPolicy:
 automated:
 prune: true
 selfHeal: true
```

## Backup and Restore

### Backup Resources

```bash
# Backup all resources
oc get all,configmap,secret -l app=keycloak-mcp-server -o yaml > backup.yaml

# Backup specific resources
oc get deployment,service,route keycloak-mcp-server -o yaml > resources-backup.yaml
```

### Restore

```bash
# Restore from backup
oc apply -f backup.yaml
```

## Cleanup

```bash
# Delete all resources
oc delete -f deploy/openshift/

# Or delete by label
oc delete all -l app=keycloak-mcp-server

# Delete project
oc delete project keycloak-mcp
```

## Best Practices

### 1. Use Specific Image Tags

 **Don't use** `latest` in production:
```yaml
image: quay.io/sshaaf/keycloak-mcp-server:latest
```

 **Use** specific git commit SHA or version:
```yaml
image: quay.io/sshaaf/keycloak-mcp-server:49ff54e
```

### 2. Resource Limits

Always set resource limits to prevent resource exhaustion:

```yaml
resources:
 requests:
 memory: "256Mi"
 cpu: "100m"
 limits:
 memory: "512Mi"
 cpu: "500m"
```

### 3. Health Checks

Include comprehensive health checks for reliability:

```yaml
livenessProbe: ...
readinessProbe: ...
startupProbe: ...
```

### 4. Security

- Run as non-root user
- Drop all capabilities
- Use read-only root filesystem where possible
- Implement network policies
- Use secrets for sensitive data

### 5. High Availability

- Use multiple replicas (2-3 minimum)
- Configure pod disruption budgets
- Distribute across availability zones

```yaml
spec:
 replicas: 3
 template:
 spec:
 affinity:
 podAntiAffinity:
 preferredDuringSchedulingIgnoredDuringExecution:
 - weight: 100
 podAffinityTerm:
 labelSelector:
 matchLabels:
 app: keycloak-mcp-server
 topologyKey: kubernetes.io/hostname
```

## Summary

 **Quick deployment** with pre-configured manifests
 **Secure by default** with non-root containers
 **Production-ready** with health checks and resource limits
 **HTTPS configured** for secure Keycloak communication
 **OpenShift native** using Routes and SecurityContextConstraints
 **Flexible** supporting multiple deployment methods
 **Scalable** with horizontal pod autoscaling support

## Key Configuration Files

| File | Purpose |
|------|---------|
| `deployment.yaml` | Main application deployment with TLS support |
| `service.yaml` | ClusterIP service definition |
| `route.yaml` | OpenShift route for external access |
| `configmap.yaml` | Keycloak URL and OIDC client configuration |
| `ca-configmap.yaml` | CA certificate for self-signed Keycloak TLS |
| `kustomization.yaml` | Kustomize configuration |
| **[keycloak-tls-setup.md](deploy/openshift/keycloak-tls-setup.md)** | **Complete TLS/HTTPS configuration guide** |

## Important Notes

 **Production Keycloak uses HTTPS only** - Configure TLS trust before deploying
 **Read [keycloak-tls-setup.md](deploy/openshift/keycloak-tls-setup.md)** for TLS configuration
 **Keycloak Operator**: See [installation tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)
 **Use specific image tags** in production (not `latest`)

---

**Your Keycloak MCP Server is now ready for OpenShift!**

For issues or questions:
- Check logs: `oc logs -f deployment/keycloak-mcp-server`
- TLS issues: See [keycloak-tls-setup.md](deploy/openshift/keycloak-tls-setup.md)
- Keycloak setup: [Tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)

