# Jib Container Image Guide

## Overview

The Keycloak MCP Server now supports building container images using **Jib** (Java Image Builder), a fast and efficient container image builder that doesn't require Docker to be installed.

## What is Jib?

Jib is a containerization tool from Google that builds optimized Docker and OCI images for Java applications without needing:
- Docker daemon
- Dockerfile
- Complex build scripts

### Benefits

 **No Docker Required** - Build images without Docker daemon
 **Fast Builds** - Only rebuilds changed layers
 **Reproducible** - Same source = same image
 **Optimized Layers** - Separates dependencies from classes
 **Multi-Platform** - Build for linux/amd64 and linux/arm64
 **CI/CD Friendly** - Perfect for pipelines

## Configuration

### Base Configuration

The project is configured in `application.properties`:

```properties
# Container Image Configuration
quarkus.container-image.build=false
quarkus.container-image.registry=quay.io
quarkus.container-image.group=sshaaf
quarkus.container-image.name=keycloak-mcp-server
quarkus.container-image.tag=@git.commit.id.abbrev@ # Automatic: Git SHA
quarkus.container-image.additional-tags=latest # Automatic: Latest

# Jib specific
quarkus.jib.base-jvm-image=registry.access.redhat.com/ubi9/openjdk-21-runtime:1.20
quarkus.jib.platforms=linux/amd64,linux/arm64
```

**Note**: The `@git.commit.id.abbrev@` is automatically replaced with the current git commit SHA during the build.

### Image Details

- **Registry**: [Quay.io](https://quay.io/repository/sshaaf/keycloak-mcp-server)
- **Base Image**: Red Hat UBI 9 with OpenJDK 21
- **Platforms**: AMD64 (x86_64) and ARM64 (Apple Silicon, AWS Graviton)
- **Full Image**: `quay.io/sshaaf/keycloak-mcp-server`
- **Primary Tag**: Git commit SHA (e.g., `49ff54e`) - **automatically generated**
- **Additional Tags**: Version (`0.3.0`) + Latest (`latest`)

### Automatic Git SHA Tagging

Images are **automatically tagged with the git commit SHA** for perfect traceability:

```bash
# Every build automatically creates:
quay.io/sshaaf/keycloak-mcp-server:49ff54e # Git commit SHA (primary)
quay.io/sshaaf/keycloak-mcp-server:latest # Latest build

# Semantic versions are manually tagged when ready for release:
docker tag quay.io/sshaaf/keycloak-mcp-server:49ff54e \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

**Benefits**:
- Every image traceable to exact source code
- Reproducible builds (same commit = same tag)
- Easy rollbacks (use previous commit SHA)
- No automatic SHA management needed
- Manual control over semantic versions

See [Git Commit Tagging Guide](git-commit-tagging.md) for complete details.

## Building Container Images

### 1. Build and Push to Quay.io

```bash
# Set Quay.io credentials
export QUAY_USERNAME=your-username
export QUAY_PASSWORD=your-password # or robot token

# Build and push
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.username=${QUAY_USERNAME} \
 -Dquarkus.container-image.password=${QUAY_PASSWORD}
```

**Result**: `quay.io/sshaaf/keycloak-mcp-server:0.3.0` and `:latest` pushed to [Quay.io](https://quay.io/repository/sshaaf/keycloak-mcp-server)

### 2. Build and Push to Custom Registry

```bash
# For GitHub Container Registry
./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=ghcr.io \
 -Dquarkus.container-image.group=your-username \
 -Dquarkus.container-image.username=${GITHUB_USERNAME} \
 -Dquarkus.container-image.password=${GITHUB_TOKEN}
```

**Result**: `ghcr.io/your-username/keycloak-mcp-server:0.3.0`

### 3. Build and Load to Docker Daemon

```bash
# Build and load into local Docker
./mvnw package -Dquarkus.jib.docker-executable-name=docker

# Verify
docker images | grep keycloak-mcp-server
```

**Result**: Image available locally in Docker

### 4. Build to Tarball (Offline Distribution)

```bash
# Build to tar file
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Djib.outputPaths.tar=target/keycloak-mcp-server.tar

# Load into Docker
docker load < target/keycloak-mcp-server.tar
```

**Result**: `target/keycloak-mcp-server.tar` file created

### 5. Build for Specific Platform

```bash
# Build only for ARM64 (Apple Silicon)
./mvnw package -Dquarkus.jib.platforms=linux/arm64

# Build only for AMD64
./mvnw package -Dquarkus.jib.platforms=linux/amd64

# Build for both (default)
./mvnw package -Dquarkus.jib.platforms=linux/amd64,linux/arm64
```

## Running the Container

### Using Docker

```bash
# Pull and run from Quay.io
docker run -d \
 -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 --name keycloak-mcp \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Check logs
docker logs -f keycloak-mcp

# Test health
curl http://localhost:8080/q/health
```

### Using Podman

```bash
# Same commands work with Podman
podman run -d \
 -p 8080:8080 \
 -e KC_URL=http://host.containers.internal:8180 \
 --name keycloak-mcp \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

### Using Docker Compose

```yaml
version: '3.8'

services:
 keycloak-mcp-server:
 image: quay.io/sshaaf/keycloak-mcp-server:0.3.0
 ports:
 - "8080:8080"
 environment:
 KC_URL: http://keycloak:8080 depends_on:
 - keycloak
 networks:
 - keycloak-net

 keycloak:
 image: quay.io/keycloak/keycloak:23.0
 ports:
 - "8180:8080"
 environment:
 KEYCLOAK_ADMIN: admin
 KEYCLOAK_ADMIN_PASSWORD: admin
 command: start-dev
 networks:
 - keycloak-net

networks:
 keycloak-net:
 driver: bridge
```

Run with:
```bash
docker compose up -d
```

## Kubernetes/OpenShift Deployment

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
 name: keycloak-mcp-server
 labels:
 app: keycloak-mcp-server
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
 image: quay.io/sshaaf/keycloak-mcp-server:0.3.0
 ports:
 - containerPort: 8080
 name: http
 protocol: TCP
 env:
 - name: KC_URL
 value: "http://keycloak:8080" secretKeyRef:
 name: keycloak-admin
 key: password
 livenessProbe:
 httpGet:
 path: /q/health/live
 port: 8080
 initialDelaySeconds: 30
 periodSeconds: 10
 readinessProbe:
 httpGet:
 path: /q/health/ready
 port: 8080
 initialDelaySeconds: 10
 periodSeconds: 5
 resources:
 requests:
 memory: "256Mi"
 cpu: "250m"
 limits:
 memory: "512Mi"
 cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
 name: keycloak-mcp-server
spec:
 type: ClusterIP
 selector:
 app: keycloak-mcp-server
 ports:
 - port: 8080
 targetPort: 8080
 name: http
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
 name: keycloak-mcp-server
 annotations:
 nginx.ingress.kubernetes.io/rewrite-target: /
spec:
 rules:
 - host: mcp.example.com
 http:
 paths:
 - path: /
 pathType: Prefix
 backend:
 service:
 name: keycloak-mcp-server
 port:
 number: 8080
```

Deploy with:
```bash
kubectl apply -f k8s-deployment.yaml
```

## CI/CD Integration

### GitHub Actions

```yaml
name: Build and Push Container

on:
 push:
 branches: [ main ]
 tags: [ 'v*' ]

jobs:
 build:
 runs-on: ubuntu-latest
 steps:
 - uses: actions/checkout@v4

 - name: Set up JDK 21
 uses: actions/setup-java@v4
 with:
 java-version: '21'
 distribution: 'temurin'
 cache: maven

 - name: Build and Push Container Image
 run: |
 ./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=ghcr.io \
 -Dquarkus.container-image.group=${{ github.repository_owner }} \
 -Dquarkus.container-image.username=${{ github.actor }} \
 -Dquarkus.container-image.password=${{ secrets.GITHUB_TOKEN }}
```

### GitLab CI

```yaml
build-container:
 stage: build
 image: maven:3.9-eclipse-temurin-21
 script:
 - ./mvnw package
 -Dquarkus.container-image.push=true
 -Dquarkus.container-image.registry=$CI_REGISTRY
 -Dquarkus.container-image.group=$CI_PROJECT_NAMESPACE
 -Dquarkus.container-image.username=$CI_REGISTRY_USER
 -Dquarkus.container-image.password=$CI_REGISTRY_PASSWORD
 only:
 - main
 - tags
```

## Advanced Configuration

### Custom Base Image

```properties
# Use a different base image
quarkus.jib.base-jvm-image=eclipse-temurin:21-jre
```

### Additional Ports

```properties
# Expose additional ports
quarkus.jib.ports=8080,8443
```

### Custom Labels

```properties
# Add custom OCI labels
quarkus.jib.labels."com.example.team"=platform
quarkus.jib.labels."com.example.maintainer"=devops@example.com
```

### Working Directory

```properties
# Set working directory in container
quarkus.jib.working-directory=/app
```

### User Configuration

```properties
# Run as non-root user
quarkus.jib.user=1001:1001
```

### JVM Arguments

```properties
# Customize JVM arguments
quarkus.jib.jvm-arguments=-Xmx512m,-Xms256m,-XX:+UseG1GC
```

## Registry-Specific Configurations

### Quay.io (Default)

```bash
# Login to Quay.io (optional, for authentication)
docker login quay.io

# Build and push
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.username=${QUAY_USERNAME} \
 -Dquarkus.container-image.password=${QUAY_PASSWORD}

# Or use robot account
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.username=sshaaf+robot \
 -Dquarkus.container-image.password=${QUAY_ROBOT_TOKEN}
```

**Repository**: [https://quay.io/repository/sshaaf/keycloak-mcp-server](https://quay.io/repository/sshaaf/keycloak-mcp-server)

### Docker Hub

```bash
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=docker.io \
 -Dquarkus.container-image.username=${DOCKER_USERNAME} \
 -Dquarkus.container-image.password=${DOCKER_PASSWORD}
```

### GitHub Container Registry (ghcr.io)

```bash
./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=ghcr.io \
 -Dquarkus.container-image.username=${GITHUB_USERNAME} \
 -Dquarkus.container-image.password=${GITHUB_TOKEN}
```

### AWS ECR

```bash
# Login to ECR first
aws ecr get-login-password --region us-east-1 | \
 docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com

# Build and push
./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=123456789012.dkr.ecr.us-east-1.amazonaws.com \
 -Dquarkus.container-image.username=AWS \
 -Dquarkus.container-image.password=$(aws ecr get-login-password --region us-east-1)
```

### Azure Container Registry (ACR)

```bash
# Login to ACR first
az acr login --name myregistry

# Build and push
./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=myregistry.azurecr.io \
 -Dquarkus.container-image.username=${ACR_USERNAME} \
 -Dquarkus.container-image.password=${ACR_PASSWORD}
```

### Google Container Registry (GCR)

```bash
# Configure Docker to use gcloud as credential helper
gcloud auth configure-docker

# Build and push
./mvnw package \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.registry=gcr.io \
 -Dquarkus.container-image.group=${GCP_PROJECT_ID}
```

## Troubleshooting

### Issue: "unauthorized: authentication required"

**Solution**: Ensure you are logged in to the registry:
```bash
docker login
# or
echo $GITHUB_TOKEN | docker login ghcr.io -u $GITHUB_USERNAME --password-stdin
```

### Issue: "platform not supported"

**Solution**: Build for specific platform:
```bash
./mvnw package -Dquarkus.jib.platforms=linux/amd64
```

### Issue: Build is slow

**Solution**: Jib caches layers. First build is slow, subsequent builds are fast. To disable cache:
```bash
./mvnw package -Dquarkus.jib.use-current-timestamp=true
```

### Issue: Base image pull fails

**Solution**: Check network connectivity or use alternative base image:
```properties
quarkus.jib.base-jvm-image=eclipse-temurin:21-jre
```

## Comparison: Jib vs Docker

| Feature | Jib | Docker Build |
|---------|-----|--------------|
| **Docker Daemon Required** | No | Yes |
| **Dockerfile Required** | No | Yes |
| **Build Speed (incremental)** | Very Fast | Slower |
| **Layer Optimization** | Automatic | Manual |
| **Multi-Platform** | Native | buildx required |
| **CI/CD Friendly** | Excellent | Good |
| **Reproducibility** | Perfect | Good |
| **Maven/Gradle Integration** | Native | External |

## Best Practices

### 1. Use Specific Base Image Versions
```properties
# Good - pinned version
quarkus.jib.base-jvm-image=registry.access.redhat.com/ubi9/openjdk-21-runtime:1.20

# Avoid - floating tag
quarkus.jib.base-jvm-image=openjdk:21
```

### 2. Tag with Version and Commit SHA
```bash
./mvnw package \
 -Dquarkus.container-image.tag=0.3.0-${GIT_COMMIT_SHA}
```

### 3. Use Non-Root User
```properties
quarkus.jib.user=1001:1001
```

### 4. Optimize for Size
- Use minimal base images (UBI Micro, Alpine)
- Enable compression
- Remove unnecessary dependencies

### 5. Security Scanning
```bash
# Scan with Trivy
trivy image quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Scan with Snyk
snyk container test quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Quay.io also provides built-in security scanning
# View results at: https://quay.io/repository/sshaaf/keycloak-mcp-server?tab=tags
```

## Image Information

### Default Built Image

- **Registry**: [Quay.io](https://quay.io/repository/sshaaf/keycloak-mcp-server)
- **Full Name**: `quay.io/sshaaf/keycloak-mcp-server`
- **Automatic Tags**: `<git-sha>` (e.g., `49ff54e`), `latest`
- **Manual Tags**: `0.3.0`, `1.0.0`, etc. (when you decide to release)
- **Base**: Red Hat UBI 9 + OpenJDK 21
- **Size**: ~250-300 MB
- **Platforms**: linux/amd64, linux/arm64
- **Exposed Port**: 8080 (overridden from default port 0)
- **Endpoint**: `/mcp/sse`

### Port Configuration Strategy

The application uses **smart port assignment**:

| Deployment Mode | Port | Reason |
|----------------|------|--------|
| Local JAR/Native | `0` (random) | Avoids port conflicts in development |
| Container | `8080` (fixed) | Reliable networking for Docker/Kubernetes |

The container automatically overrides port 0 to 8080 via the `QUARKUS_HTTP_PORT` environment variable.

### Labels

All images include OCI-compliant labels:
- `org.opencontainers.image.title`
- `org.opencontainers.image.description`
- `org.opencontainers.image.version`
- `org.opencontainers.image.source`
- `org.opencontainers.image.licenses`

## Summary

 **Jib Extension Added**: `quarkus-container-image-jib`
 **Configuration Complete**: Ready to build images
 **Multi-Platform Support**: AMD64 & ARM64
 **CI/CD Ready**: No Docker daemon required
 **Optimized Layers**: Fast incremental builds

---

**Quick Start**:
```bash
# Pull and run from Quay.io
docker pull quay.io/sshaaf/keycloak-mcp-server:latest

# Run
docker run -d -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Or build locally and load to Docker
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.jib.docker-executable-name=docker
```

For more information, see:
- [Quarkus Container Images Guide](https://quarkus.io/guides/container-image)
- [Jib Documentation](https://github.com/GoogleContainerTools/jib)

