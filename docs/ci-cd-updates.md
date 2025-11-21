# CI/CD Updates - Automatic Container Push to Quay.io

## Summary

The GitHub Actions workflows have been updated to automatically build and push container images to Quay.io on every push to the `main` branch and during releases.

## What Changed

### 1. Updated `.github/workflows/build-artifacts.yml`

**Added new job**: `build-and-push-container`

- **Triggers**: Only on push to `main` branch (not on PRs)
- **Purpose**: Build and push container images automatically
- **Images pushed**:
 - `quay.io/sshaaf/keycloak-mcp-server:<git-sha>` (e.g., `49ff54e`)
 - `quay.io/sshaaf/keycloak-mcp-server:latest`

**Features**:
- Full git history checkout for git SHA tagging
- Maven caching for faster builds
- Automatic git commit SHA extraction
- GitHub Actions summary with image details
- No Docker daemon required (uses Jib)

### 2. Updated `.github/workflows/release.yml`

**Added new job**: `build-and-push-container`

- **Triggers**: Manual workflow dispatch (releases)
- **Purpose**: Build and push container images with version tagging
- **Images pushed**:
 - `quay.io/sshaaf/keycloak-mcp-server:<git-sha>` (e.g., `49ff54e`)
 - `quay.io/sshaaf/keycloak-mcp-server:<version>` (e.g., `0.3.0`)
 - `quay.io/sshaaf/keycloak-mcp-server:latest`

**Features**:
- **Automatic version extraction** from `pom.xml` (no hardcoded versions!)
- Creates semantic version tag
- Integrated with release workflow
- Runs before GitHub release creation

**Version Management**:
The workflow automatically extracts the version from `pom.xml` using Maven:
```bash
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```
This ensures the GitHub Actions version always matches your Maven project version - no manual updates needed!

### 3. Created `github-actions-setup.md`

Comprehensive guide covering:
- How to set up GitHub Secrets (`QUAY_USERNAME`, `QUAY_PASSWORD`)
- Robot account setup on Quay.io
- Workflow triggers and conditions
- Tagging strategy explained
- Troubleshooting guide
- Security best practices

### 4. Updated `README.md`

- Added "CI/CD and Container Images" section
- Linked to GitHub Actions Setup guide
- Highlighted automatic container builds

## Required Setup

### GitHub Secrets

You must configure these secrets in your GitHub repository:

1. **`QUAY_USERNAME`**: Your Quay.io username or robot account
2. **`QUAY_PASSWORD`**: Your Quay.io password or robot token

**How to add secrets**:
1. Go to repository → **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Add `QUAY_USERNAME` and `QUAY_PASSWORD`

**Recommended**: Use a Quay.io robot account for better security:
- Create robot account at: https://quay.io/repository/sshaaf/keycloak-mcp-server
- Use robot name as username (e.g., `sshaaf+github_actions`)
- Use robot token as password

## How It Works

### Workflow 1: Regular Development (Push to main)

```bash
# Developer pushes to main
git commit -m "Add new feature"
git push origin main

# GitHub Actions automatically:
# 1. Builds JAR and native binaries
# 2. Builds container image with Jib
# 3. Tags with git commit SHA + latest
# 4. Pushes to quay.io/sshaaf/keycloak-mcp-server

# Result:
# - quay.io/sshaaf/keycloak-mcp-server:49ff54e
# - quay.io/sshaaf/keycloak-mcp-server:latest
```

### Workflow 2: Pull Request

```bash
# Developer creates PR
git push origin feature-branch

# GitHub Actions:
# 1. Builds JAR and native binaries
# 2. Does NOT push container image
#
# Why? Saves resources and prevents cluttering the registry
```

### Workflow 3: Release

```bash
# Maintainer triggers release workflow

# GitHub Actions:
# 1. Builds JAR and native binaries
# 2. Builds container image with Jib
# 3. Tags with git SHA + version + latest
# 4. Pushes to Quay.io
# 5. Creates GitHub release

# Result:
# - quay.io/sshaaf/keycloak-mcp-server:49ff54e
# - quay.io/sshaaf/keycloak-mcp-server:0.3.0
# - quay.io/sshaaf/keycloak-mcp-server:latest
```

## Tag Strategy

| Event | Git SHA Tag | Version Tag | Latest Tag |
|-------|-------------|-------------|------------|
| **Push to main** | Yes | No | Yes |
| **Pull Request** | No build | No build | No build |
| **Release** | Yes | Yes | Yes |

### Why This Strategy?

1. **Git SHA Tags**: Perfect traceability
 - Every commit gets a unique tag
 - Production deployments can reference exact code
 - Easy rollbacks

2. **Version Tags**: Semantic releases
 - Only created during official releases
 - Manual control over versioning
 - Clear release history

3. **Latest Tag**: Development convenience
 - Always points to most recent build
 - Good for testing
 - Not recommended for production

## Using the Images

### Pull Latest

```bash
docker pull quay.io/sshaaf/keycloak-mcp-server:latest
```

### Pull Specific Commit

```bash
docker pull quay.io/sshaaf/keycloak-mcp-server:49ff54e
```

### Pull Release Version

```bash
docker pull quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

### Run Container

```bash
docker run -d \
 -p 8080:8080 \
 -e KC_URL=http://host.docker.internal:8180 \
 -e KC_USER=admin \
 -e KC_PASSWORD=admin \
 --name keycloak-mcp \
 quay.io/sshaaf/keycloak-mcp-server:latest
```

## Benefits

### 1. Automation
- No manual container builds
- No manual tagging
- No manual pushing
- Consistent process every time

### 2. Traceability
- Every image has git SHA
- Can trace back to exact source code
- Easy to find what changed

### 3. Efficiency
- PRs don't push images (saves resources)
- Only tested code gets published
- Multi-architecture builds (AMD64 + ARM64)

### 4. Security
- Robot accounts with minimal permissions
- Secrets never exposed in logs
- Automatic vulnerability scanning on Quay.io

### 5. Flexibility
- Manual releases still available
- Can override tags when needed
- Local builds work the same way

## Verification

### 1. Check GitHub Actions

Go to: https://github.com/sshaaf/keycloak-mcp-server/actions

Look for the "Build and Push" workflow and check the summary.

### 2. Check Quay.io

Go to: https://quay.io/repository/sshaaf/keycloak-mcp-server?tab=tags

You should see:
- `latest` tag
- Git commit SHA tags (e.g., `49ff54e`)
- Version tags (e.g., `0.3.0`) for releases

### 3. Pull and Test

```bash
# Pull the image
docker pull quay.io/sshaaf/keycloak-mcp-server:latest

# Check image labels
docker inspect quay.io/sshaaf/keycloak-mcp-server:latest | grep -A5 Labels

# Run it
docker run --rm quay.io/sshaaf/keycloak-mcp-server:latest
```

## Troubleshooting

### Issue: Container Push Fails

**Check**:
1. Are secrets configured correctly?
2. Is the Quay.io repository created?
3. Does the robot account have write permissions?

**Solution**: See [github-actions-setup.md](github-actions-setup.md#troubleshooting)

### Issue: Images Not Pushed on Main

**Check**:
1. Did you push to `main` branch?
2. Check workflow condition in YAML
3. Review GitHub Actions logs

### Issue: Git SHA Shows as Placeholder

**Check**:
1. Ensure `fetch-depth: 0` in checkout
2. Verify git-commit-id-maven-plugin is working
3. Check Maven build logs

## Next Steps

1. **Set up secrets**: Configure `QUAY_USERNAME` and `QUAY_PASSWORD`
2. **Test push**: Push a commit to `main` and watch the workflow
3. **Verify images**: Check Quay.io for the pushed images
4. **Pull and test**: Try pulling and running the image

## Documentation

- [github-actions-setup.md](github-actions-setup.md) - Complete setup guide
- [git-commit-tagging.md](git-commit-tagging.md) - Git SHA tagging details
- [jib-container-guide.md](jib-container-guide.md) - Jib configuration
- [README.md](README.md) - Main project documentation

---

## Summary

 **Automatic Builds**: Every push to `main` builds and pushes images
 **Git SHA Tagging**: Perfect traceability with commit SHAs
 **Release Versioning**: Semantic versions on releases
 **Multi-Architecture**: AMD64 and ARM64 support
 **No Docker Required**: Jib handles everything
 **Secure**: Robot accounts and GitHub Secrets

**Your CI/CD pipeline is now fully automated!**

Next push to `main` will automatically build and publish your container images to Quay.io.

