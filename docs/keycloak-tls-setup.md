# Keycloak HTTPS/TLS Configuration Guide

## Overview

In production, Keycloak **always uses HTTPS**. This guide explains how to configure the Keycloak MCP Server to connect to HTTPS-enabled Keycloak instances.

## Prerequisites

- Keycloak deployed with HTTPS enabled
- Knowledge of your Keycloak TLS certificate type:
 - Trusted CA-signed certificate
 - Self-signed certificate
 - OpenShift service CA

## Scenarios

### Scenario 1: Keycloak with Trusted CA Certificate

If your Keycloak uses a certificate from a trusted CA (We will Encrypt, DigiCert, etc.), **no additional configuration is needed**.

```yaml
# configmap.yaml
data:
 keycloak-url: "https://keycloak.example.com"
```

The Java runtime will trust these certificates by default.

### Scenario 2: Keycloak with Self-Signed Certificate

When Keycloak uses self-signed certificates (common in development/test), you need to configure trust.

#### Step 1: Extract Keycloak CA Certificate

If Keycloak was deployed using the Keycloak Operator (as shown in [your tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)):

```bash
# Get the TLS secret name from Keycloak CR
KEYCLOAK_NAMESPACE=rhbk # Your Keycloak namespace
KEYCLOAK_TLS_SECRET=example-tls-secret # From Keycloak CR spec.http.tlsSecret

# Extract CA certificate
oc get secret ${KEYCLOAK_TLS_SECRET} -n ${KEYCLOAK_NAMESPACE} \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt
```

#### Step 2: Create ConfigMap with CA Certificate

```bash
# Create ConfigMap in your namespace
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt \
 -n keycloak-mcp
```

#### Step 3: Update Deployment

Edit `deploy/openshift/deployment.yaml`:

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
 # Add JVM trust store configuration
 - name: JAVA_OPTS
 value: "-Djavax.net.ssl.trustStore=/certs/cacerts -Djavax.net.ssl.trustStorePassword=changeit"
 volumes:
 - name: keycloak-ca-cert
 configMap:
 name: keycloak-ca-bundle
```

#### Step 4: Build Custom Truststore (Alternative)

For more control, create a custom Java truststore:

```bash
# Import CA certificate into truststore
keytool -import -trustcacerts \
 -alias keycloak-ca \
 -file keycloak-ca.crt \
 -keystore truststore.jks \
 -storepass changeit \
 -noprompt

# Create secret with truststore
oc create secret generic keycloak-truststore \
 --from-file=truststore.jks=truststore.jks
```

Update deployment:

```yaml
spec:
 template:
 spec:
 containers:
 - name: keycloak-mcp-server
 volumeMounts:
 - name: truststore
 mountPath: /certs
 readOnly: true
 env:
 - name: JAVAX_NET_SSL_TRUSTSTORE
 value: /certs/truststore.jks
 - name: JAVAX_NET_SSL_TRUSTSTOREPASSWORD
 value: changeit
 volumes:
 - name: truststore
 secret:
 secretName: keycloak-truststore
```

### Scenario 3: Keycloak with OpenShift Service CA

When Keycloak is deployed on OpenShift and uses the platform's service CA:

#### Step 1: Create ConfigMap with Injection Annotation

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

#### Step 2: Mount in Deployment

```yaml
spec:
 template:
 spec:
 containers:
 - name: keycloak-mcp-server
 volumeMounts:
 - name: keycloak-ca-cert
 mountPath: /etc/ssl/certs/openshift-ca.crt
 subPath: service-ca.crt
 readOnly: true
 env:
 - name: JAVA_OPTS
 value: "-Djavax.net.ssl.trustStore=/etc/ssl/certs/openshift-ca.crt"
 volumes:
 - name: keycloak-ca-cert
 configMap:
 name: keycloak-ca-bundle
```

### Scenario 4: Keycloak Operator Deployment

When using the Keycloak Operator (recommended approach from [your tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)):

#### Keycloak CR Example

```yaml
apiVersion: k8s.keycloak.org/v2alpha1
kind: Keycloak
metadata:
 name: example-kc
 namespace: rhbk
spec:
 instances: 1
 db:
 vendor: postgres
 host: postgres-db
 usernameSecret:
 name: keycloak-db-secret
 key: username
 passwordSecret:
 name: keycloak-db-secret
 key: password
 http:
 tlsSecret: example-tls-secret # TLS certificate
 hostname:
 hostname: keycloak.rhbk.apps.example.com
```

#### MCP Server Configuration

```yaml
# configmap.yaml
data:
 # Use the hostname from Keycloak CR with HTTPS
 keycloak-url: "https://keycloak.rhbk.apps.example.com"
```

#### Extract and Use CA Certificate

```bash
# Extract CA from Keycloak's TLS secret
oc get secret example-tls-secret -n rhbk \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Create ConfigMap in MCP namespace
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt \
 -n keycloak-mcp
```

## Complete Deployment Example

### With Self-Signed Certificate

```bash
# 1. Create namespace
oc new-project keycloak-mcp

# 2. Extract Keycloak CA certificate
oc get secret example-tls-secret -n rhbk \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# 3. Create CA ConfigMap
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt

# 4. Create secrets
oc create secret generic keycloak-mcp-secret \
 --from-literal=username=admin \
 --from-literal=password='YourPassword'

# 5. Create ConfigMap with HTTPS URL
cat <<EOF | oc apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
 name: keycloak-mcp-config
data:
 keycloak-url: "https://keycloak.rhbk.apps.example.com"
EOF

# 6. Deploy with CA certificate mounting
# (Use updated deployment.yaml with uncommented volumes)
oc apply -f deploy/openshift/deployment.yaml
oc apply -f deploy/openshift/service.yaml
oc apply -f deploy/openshift/route.yaml
```

## Testing Connection

### Test from Pod

```bash
# Execute into the pod
oc exec -it deployment/keycloak-mcp-server -- /bin/sh

# Test HTTPS connection
curl -v https://keycloak.rhbk.apps.example.com/realms/master

# If using custom CA, test with certificate
curl --cacert /certs/ca.crt https://keycloak.rhbk.apps.example.com/realms/master
```

### Check Application Logs

```bash
# View logs for connection errors
oc logs -f deployment/keycloak-mcp-server

# Look for SSL/TLS errors like:
# - "PKIX path building failed"
# - "unable to find valid certification path"
# - "SSLHandshakeException"
```

## Troubleshooting

### Error: "PKIX path building failed"

**Cause**: Java doesn't trust the Keycloak certificate.

**Solution**:
1. Verify CA certificate is mounted: `oc exec deployment/keycloak-mcp-server -- ls -la /certs`
2. Check Java trust store configuration
3. Ensure certificate is valid and not expired

### Error: "unable to find valid certification path"

**Cause**: Certificate chain is incomplete or CA certificate is missing.

**Solution**:
1. Extract the full certificate chain from Keycloak
2. Include intermediate certificates
3. Verify with: `openssl s_client -connect keycloak.example.com:443 -showcerts`

### Error: "SSLHandshakeException: Received fatal alert: handshake_failure"

**Cause**: TLS version mismatch or cipher suite incompatibility.

**Solution**:
1. Check Keycloak TLS configuration
2. Enable TLS 1.2+ in Java:
 ```yaml
 env:
 - name: JAVA_OPTS
 value: "-Dhttps.protocols=TLSv1.2,TLSv1.3"
 ```

### Error: "Connection refused" on HTTPS port

**Cause**: Keycloak not listening on HTTPS or firewall blocking.

**Solution**:
1. Verify Keycloak route: `oc get route -n rhbk`
2. Test connectivity: `curl -v https://keycloak.example.com`
3. Check if Keycloak pods are running: `oc get pods -n rhbk`

## Security Best Practices

### 1. Never Disable Certificate Verification

 **Don't do this**:
```yaml
env:
- name: JAVA_OPTS
 value: "-Djavax.net.ssl.trustAll=true" # INSECURE!
```

 **Do this**: Always configure proper certificate trust.

### 2. Rotate Certificates Regularly

```bash
# Update CA certificate
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=new-keycloak-ca.crt \
 --dry-run=client -o yaml | oc replace -f -

# Restart pods to pick up new certificate
oc rollout restart deployment/keycloak-mcp-server
```

### 3. Use Secrets for Truststore Passwords

```yaml
env:
- name: JAVAX_NET_SSL_TRUSTSTOREPASSWORD
 valueFrom:
 secretKeyRef:
 name: keycloak-mcp-secret
 key: truststore-password
```

### 4. Monitor Certificate Expiration

```bash
# Check certificate expiration
openssl s_client -connect keycloak.example.com:443 2>/dev/null | \
 openssl x509 -noout -dates
```

## Reference: Keycloak Operator Installation

Based on [your tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/), here is the complete Keycloak setup:

### 1. Install Keycloak Operator

```bash
# Via OpenShift Console or CLI
oc create -f https://operatorhub.io/install/keycloak-operator.yaml
```

### 2. Deploy PostgreSQL

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
 name: postgresql-db
 namespace: rhbk
spec:
 serviceName: postgresql-db-service
 selector:
 matchLabels:
 app: postgresql-db
 replicas: 1
 template:
 metadata:
 labels:
 app: postgresql-db
 spec:
 containers:
 - name: postgresql-db
 image: postgres:latest
 env:
 - name: POSTGRES_PASSWORD
 value: testpassword
 - name: POSTGRES_DB
 value: keycloak
```

### 3. Create TLS Certificate

```bash
# Create self-signed certificate
openssl req -subj '/CN=keycloak.rhbk.apps.example.com/O=Example Org/C=US' \
 -newkey rsa:2048 -nodes -keyout key.pem \
 -x509 -days 365 -out certificate.pem

# Create secret
oc create secret tls example-tls-secret \
 --cert certificate.pem \
 --key key.pem \
 -n rhbk
```

### 4. Deploy Keycloak

```yaml
apiVersion: k8s.keycloak.org/v2alpha1
kind: Keycloak
metadata:
 name: example-kc
 namespace: rhbk
spec:
 instances: 1
 db:
 vendor: postgres
 host: postgres-db
 usernameSecret:
 name: keycloak-db-secret
 key: username
 passwordSecret:
 name: keycloak-db-secret
 key: password
 http:
 tlsSecret: example-tls-secret
 hostname:
 hostname: keycloak.rhbk.apps.example.com
```

### 5. Configure MCP Server

```bash
# Extract Keycloak CA
oc get secret example-tls-secret -n rhbk \
 -o jsonpath='{.data.tls\.crt}' | base64 -d > keycloak-ca.crt

# Deploy MCP Server with CA trust
oc create configmap keycloak-ca-bundle \
 --from-file=ca.crt=keycloak-ca.crt \
 -n keycloak-mcp

# Update configmap with HTTPS URL
oc patch configmap keycloak-mcp-config \
 -p '{"data":{"keycloak-url":"https://keycloak.rhbk.apps.example.com"}}'
```

## Summary

 **Production Keycloak always uses HTTPS**
 **Configure certificate trust based on CA type**
 **Use Keycloak Operator for managed deployments**
 **Never disable certificate verification**
 **Test connections before deploying**
 **Monitor certificate expiration**

---

**For more details on Keycloak Operator deployment, see:**
[Keycloak Operator for Kubernetes - a Basic Tutorial](https://shaaf.dev/post/2023-09-08-install-keycloak-operator-kubernetes-basic-tutorial/)

