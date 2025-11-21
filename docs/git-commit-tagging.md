# Git Commit SHA Tagging for Container Images

## Overview

The Keycloak MCP Server now automatically tags container images with the **git commit SHA** for perfect traceability and reproducibility.

## How It Works

### 1. Git Commit ID Plugin

The `git-commit-id-maven-plugin` captures git information during the Maven `initialize` phase:

```xml
<plugin>
 <groupId>io.github.git-commit-id</groupId>
 <artifactId>git-commit-id-maven-plugin</artifactId>
 <version>7.0.0</version>
</plugin>
```

### 2. Maven Resource Filtering

Maven resource filtering is enabled to substitute git properties in `application.properties`:

```xml
<resources>
 <resource>
 <directory>src/main/resources</directory>
 <filtering>true</filtering>
 </resource>
</resources>
```

### 3. Image Tag Configuration

The container image tag uses the git commit SHA (abbreviated):

```properties
# Primary tag: Always git commit SHA
quarkus.container-image.tag=@git.commit.id.abbrev@

# Additional tags: Latest only (versions are manually tagged)
quarkus.container-image.additional-tags=latest
```

## Result

When you build a container image, it will be automatically tagged with:

1. **Git Commit SHA** (primary tag): `49ff54e` - unique identifier for every build
2. **Latest**: `latest` - always points to the most recent build

### Example Image Names

```
quay.io/sshaaf/keycloak-mcp-server:49ff54e # Git commit SHA (primary)
quay.io/sshaaf/keycloak-mcp-server:latest # Latest build
```

### Manual Version Tagging

Semantic versions (like `0.3.0`, `1.0.0`, etc.) are **not automatically created**. You manually tag specific commits when you are ready to release:

```bash
# Build and tag a release version manually
docker tag quay.io/sshaaf/keycloak-mcp-server:49ff54e \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Push both tags
docker push quay.io/sshaaf/keycloak-mcp-server:49ff54e
docker push quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

Or use Maven properties for releases:

```bash
# Build with additional version tag for releases
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.additional-tags=0.3.0,latest
```

## Benefits

### 1. **Perfect Traceability**
Every container image can be traced back to the exact git commit that built it.

```bash
# Pull a specific commit
docker pull quay.io/sshaaf/keycloak-mcp-server:49ff54e

# Check what was in that commit
git show 49ff54e
```

### 2. **Reproducible Builds**
Same commit = same tag = guaranteed identical code.

### 3. **Easy Debugging**
When an issue occurs in production, you know exactly which commit to investigate:

```bash
# See what is running in production
docker inspect quay.io/sshaaf/keycloak-mcp-server:49ff54e

# Check that commit
git checkout 49ff54e
```

### 4. **CI/CD Friendly**
No automatic tag management - each commit gets its unique SHA tag automatically.

### 5. **Manual Release Control**
You decide when a commit is "release-worthy" and manually tag semantic versions (0.3.0, 1.0.0, etc.).

## Building Container Images

### Local Build

```bash
# Build with git commit SHA tag
./mvnw package -Dquarkus.container-image.build=true

# Result: Image tagged with your current commit SHA + 0.3.0 + latest
```

### CI/CD Build

```bash
# In CI/CD pipeline
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.username=$QUAY_USERNAME \
 -Dquarkus.container-image.password=$QUAY_PASSWORD

# Automatically uses the commit SHA from the git repository
```

## Git Properties Available

The plugin captures these properties:

| Property | Description | Example |
|----------|-------------|---------|
| `git.commit.id.abbrev` | Short commit SHA (7 chars) | `49ff54e` |
| `git.commit.id.full` | Full commit SHA | `49ff54e674e72d32832c555393a41f6eaef13bc2` |
| `git.commit.id.describe` | Git describe output | `49ff54e-dirty` |
| `git.branch` | Current branch name | `enhancements-v0.3` |

All properties are available in `target/classes/git.properties`.

## Viewing Git Info at Runtime

The git.properties file is included in the JAR:

```bash
# Extract and view git info from running container
docker exec keycloak-mcp cat /deployments/git.properties

# Output:
# git.branch=enhancements-v0.3
# git.commit.id.abbrev=49ff54e
# git.commit.id.full=49ff54e674e72d32832c555393a41f6eaef13bc2
```

## Using Specific Tags

### Pull by Commit SHA (Recommended for Production)
```bash
# Always get the exact version you tested
docker pull quay.io/sshaaf/keycloak-mcp-server:49ff54e
```

### Pull by Semantic Version (For Releases)
```bash
# Get a specific release version (manually tagged)
docker pull quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

**Note**: Semantic version tags like `0.3.0` are not automatically created. You must manually tag commits when you decide they're ready for release.

### Pull Latest (Development Only)
```bash
# Get the most recent build (not recommended for production)
docker pull quay.io/sshaaf/keycloak-mcp-server:latest
```

## Best Practices

### 1. **Production Deployments**
Always use commit SHA tags in production:

```yaml
# kubernetes/deployment.yaml
spec:
 containers:
 - name: keycloak-mcp
 image: quay.io/sshaaf/keycloak-mcp-server:49ff54e # Specific commit
 # NOT: latest or 0.3.0 (which can change)
```

### 2. **Development**
Use `latest` for development environments:

```bash
docker run -d quay.io/sshaaf/keycloak-mcp-server:latest
```

### 3. **Staging**
Use version tags for staging:

```bash
docker run -d quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

### 4. **Rollbacks**
Easy rollback to previous commits:

```bash
# Current: 49ff54e (has issues)
# Rollback to: a1b2c3d (previous working version)

kubectl set image deployment/keycloak-mcp \
 keycloak-mcp=quay.io/sshaaf/keycloak-mcp-server:a1b2c3d
```

## Dirty State Warning

If you have uncommitted changes, the plugin adds `-dirty` suffix:

```properties
git.commit.id.describe=49ff54e-dirty
```

**Recommendation**: Always commit changes before building production images.

## Creating Release Versions

When you are ready to tag a commit as an official release, you have several options:

### Option 1: Manual Docker Tag (Recommended)

```bash
# Build the image with git SHA
./mvnw package -Dquarkus.container-image.build=true

# Tag it with a semantic version
docker tag quay.io/sshaaf/keycloak-mcp-server:49ff54e \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0

# Push both tags
docker push quay.io/sshaaf/keycloak-mcp-server:49ff54e
docker push quay.io/sshaaf/keycloak-mcp-server:0.3.0
docker push quay.io/sshaaf/keycloak-mcp-server:latest
```

### Option 2: Override at Build Time

```bash
# Add version tag for this specific build
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.additional-tags=0.3.0,latest

# Creates: 49ff54e, 0.3.0, latest
```

### Option 3: Git Tag-Based Versioning

Create a git tag first:

```bash
# Tag the current commit
git tag -a v0.3.0 -m "Release version 0.3.0"
git push origin v0.3.0

# Build with git describe
./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.tag=$(git describe --tags --abbrev=0) \
 -Dquarkus.container-image.additional-tags=latest

# Creates: 0.3.0, latest
```

### Recommended Release Workflow

```bash
# 1. Ensure you are on a clean main/release branch
git checkout main
git pull origin main

# 2. Build and test
./mvnw clean package
# Run tests, verify everything works...

# 3. Build container with git SHA
./mvnw package -Dquarkus.container-image.build=true

# 4. Tag as release version
docker tag quay.io/sshaaf/keycloak-mcp-server:$(git rev-parse --short HEAD) \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0

# 5. Push all tags
docker push quay.io/sshaaf/keycloak-mcp-server:$(git rev-parse --short HEAD)
docker push quay.io/sshaaf/keycloak-mcp-server:0.3.0
docker push quay.io/sshaaf/keycloak-mcp-server:latest

# 6. Tag in git for reference
git tag -a v0.3.0 -m "Release version 0.3.0"
git push origin v0.3.0
```

## Customizing Tags

### Use Full Commit SHA

Edit `application.properties`:

```properties
# Use full SHA instead of abbreviated
quarkus.container-image.tag=@git.commit.id.full@
```

### Add Branch Name

```properties
# Include branch in tag
quarkus.container-image.tag=@git.branch@-@git.commit.id.abbrev@
# Result: enhancements-v0.3-49ff54e
```

### Add Custom Tags

```properties
# Add more tags
quarkus.container-image.additional-tags=0.3.0,latest,stable,@git.branch@
```

## Troubleshooting

### Issue: Tag shows @git.commit.id.abbrev@

**Cause**: Maven resource filtering not working.

**Solution**: Ensure `<filtering>true</filtering>` is set in `pom.xml`:

```xml
<build>
 <resources>
 <resource>
 <directory>src/main/resources</directory>
 <filtering>true</filtering>
 </resource>
 </resources>
</build>
```

### Issue: Build fails with "Not a git repository"

**Cause**: Building from a non-git source (e.g., downloaded zip).

**Solution**: The plugin will skip if not in a git repo. Tags will fall back to configured defaults.

### Issue: Wrong commit SHA

**Cause**: Building from wrong branch or detached HEAD.

**Solution**:

```bash
# Check current commit
git log -1 --oneline

# Ensure you are on the right branch
git checkout main

# Rebuild
./mvnw clean package
```

## Integration with CI/CD

### GitHub Actions

```yaml
- name: Build and Push Container
 run: |
 ./mvnw package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true
 env:
 QUAY_USERNAME: ${{ secrets.QUAY_USERNAME }}
 QUAY_PASSWORD: ${{ secrets.QUAY_PASSWORD }}

# Image automatically tagged with ${{ github.sha }} (abbreviated)
```

### GitLab CI

```yaml
build-container:
 script:
 - ./mvnw package
 -Dquarkus.container-image.build=true
 -Dquarkus.container-image.push=true

# Image automatically tagged with $CI_COMMIT_SHORT_SHA
```

## Summary

 **Automatic SHA Tagging**: Every build tagged with git commit SHA
 **Latest Tag**: Always updated with most recent build
 **Manual Versioning**: You control when commits become releases
 **Traceability**: Every image traceable to exact source code
 **Reproducibility**: Same commit = same image
 **CI/CD Ready**: No manual SHA management needed
 **Production Safe**: Use SHA tags for deployments

---

**Current Configuration**:
- **Primary Tag**: Git commit SHA (abbreviated, 7 characters) - **automatic**
- **Additional Tag**: `latest` - **automatic**
- **Semantic Versions**: `0.3.0`, `1.0.0`, etc. - **manual only**
- **Registry**: `quay.io/sshaaf/keycloak-mcp-server`

**Example Build**:
```bash
# Your current commit: 49ff54e
# Automatic images created:
quay.io/sshaaf/keycloak-mcp-server:49ff54e # Primary (SHA)
quay.io/sshaaf/keycloak-mcp-server:latest # Latest

# Manual release tagging (when you are ready):
docker tag quay.io/sshaaf/keycloak-mcp-server:49ff54e \
 quay.io/sshaaf/keycloak-mcp-server:0.3.0
docker push quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

