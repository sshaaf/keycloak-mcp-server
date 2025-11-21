# OpenShift Deployment

Kubernetes manifests for deploying Keycloak MCP Server on OpenShift.

## Files

* `configmap.yaml` - Configuration settings
* `deployment.yaml` - Main deployment specification
* `service.yaml` - Kubernetes service
* `route.yaml` - OpenShift route (ingress)
* `ca-configmap.yaml` - CA certificate bundle for Keycloak TLS
* `kustomization.yaml` - Kustomize configuration
* `deploy.sh` - Automated deployment script

## Quick Deployment

```bash
./deploy.sh
```

## Manual Deployment

```bash
oc apply -f .
```

## Documentation

For complete deployment instructions, see:

* [OpenShift Deployment Guide](../../docs/openshift-deployment.md)
* [Keycloak TLS Setup Guide](../../docs/keycloak-tls-setup.md)

