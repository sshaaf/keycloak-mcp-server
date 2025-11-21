# GitHub Actions Setup Guide

## Overview

This project uses GitHub Actions to automatically build and push container images to Quay.io. This guide explains how to set up the required secrets and understand the workflows.

## Required GitHub Secrets

You need to configure two secrets in your GitHub repository for automatic container image pushing to Quay.io.

### 1. Navigate to Repository Settings

1. Go to your GitHub repository
2. Click **Settings** (top right)
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. Click **New repository secret**

### 2. Create QUAY_USERNAME Secret

- **Name**: `QUAY_USERNAME`
- **Value**: Your Quay.io username (e.g., `sshaaf`)

### 3. Create QUAY_PASSWORD Secret

- **Name**: `QUAY_PASSWORD`
- **Value**: Your Quay.io password or robot token (recommended)

**Recommended: Use Quay.io Robot Token**

For better security, use a robot account instead of your personal credentials:

1. Go to [quay.io/repository/sshaaf/keycloak-mcp-server](https://quay.io/repository/sshaaf/keycloak-mcp-server)
2. Click **Settings** → **Robot Accounts**
3. Click **Create Robot Account**
4. Give it a name (e.g., `github_actions`)
5. Grant **Write** permissions to the repository
6. Copy the generated token
7. Use the robot account name as `QUAY_USERNAME` (e.g., `sshaaf+github_actions`)
8. Use the token as `QUAY_PASSWORD`

## Workflows

### 1. Build and Push Workflow (`build-artifacts.yml`)

**Triggers:**
- Push to `main` branch
- Pull requests to `main` branch
- Manual workflow dispatch

**Jobs:**

#### a) `build-jar`
- Builds Uber JAR
- Uploads artifact
- **Runs on**: All triggers

#### b) `build-native-linux`
- Builds native Linux binary with GraalVM
- Uploads artifact
- **Runs on**: All triggers

#### c) `build-native-macos`
- Builds native macOS binary with GraalVM
- Uploads artifact
- **Runs on**: All triggers

#### d) `build-native-windows`
- Builds native Windows binary with GraalVM
- Uploads artifact
- **Runs on**: All triggers

#### e) `build-and-push-container` **New**
- Builds container image using Jib
- Pushes to Quay.io with automatic git SHA tagging
- **Runs on**: **Only push to `main` branch** (not on PRs)
- **Tags created**:
 - `<git-commit-sha>` (e.g., `49ff54e`) - primary tag
 - `latest` - always updated

**Why only on main?**
- Prevents pushing unnecessary images on every PR
- Ensures only tested and merged code gets published
- Saves CI/CD resources and Quay.io storage

### 2. Release Workflow (`release.yml`)

**Triggers:**
- Manual workflow dispatch only

**Version Management:**
- **Automatic version extraction** from `pom.xml`
- No hardcoded versions - always in sync with Maven project

**Jobs:**

All build jobs from above, plus:

#### `build-and-push-container` (Release version)
- Builds container image using Jib
- Pushes to Quay.io with version tagging
- **Tags created**:
 - `<git-commit-sha>` (e.g., `49ff54e`) - primary tag
 - `<version>` (e.g., `0.3.0`) - semantic version
 - `latest` - always updated

#### `release`
- Creates GitHub release with semantic-release
- Uploads all build artifacts
- Runs after all builds complete successfully

## Container Image Tags

### Automatic Tagging Strategy

| Event | Git SHA Tag | Version Tag | Latest Tag |
|-------|-------------|-------------|------------|
| **Push to main** | `49ff54e` | | `latest` |
| **Release** | `49ff54e` | `0.3.0` | `latest` |

### Tag Breakdown

1. **Git Commit SHA** (always created)
 - Format: Short SHA (7 characters)
 - Example: `49ff54e`
 - Purpose: Perfect traceability to source code
 - Usage: Production deployments

2. **Version Tag** (release only)
 - Format: Semantic version from `pom.xml`
 - Example: `0.3.0`
 - Purpose: Mark stable releases
 - Usage: Official releases

3. **Latest Tag** (always updated)
 - Format: `latest`
 - Purpose: Points to most recent build
 - Usage: Development/testing only

## Workflow Examples

### Example 1: Regular Development Push

```bash
# Developer pushes to main
git push origin main

# GitHub Actions:
# 1. Builds JAR, native binaries (all platforms)
# 2. Builds and pushes container image to Quay.io
#
# Result:
# - quay.io/sshaaf/keycloak-mcp-server:49ff54e
# - quay.io/sshaaf/keycloak-mcp-server:latest
```

### Example 2: Pull Request

```bash
# Developer creates PR
git push origin feature-branch

# GitHub Actions:
# 1. Builds JAR, native binaries (all platforms)
# 2. Does NOT push container image
#
# Result:
# - Artifacts built and tested
# - No container images pushed (saves resources)
```

### Example 3: Manual Release

```bash
# Maintainer triggers release workflow manually

# GitHub Actions:
# 1. Builds JAR, native binaries (all platforms)
# 2. Builds and pushes container image with version tag
# 3. Creates GitHub release
#
# Result:
# - quay.io/sshaaf/keycloak-mcp-server:49ff54e
# - quay.io/sshaaf/keycloak-mcp-server:0.3.0
# - quay.io/sshaaf/keycloak-mcp-server:latest
# - GitHub release with all artifacts
```

## Verifying Container Push

After a push to main or a release, you can verify the images:

### 1. Check GitHub Actions Summary

Go to the workflow run and check the **Image Details** section in the summary.

### 2. Pull the Image

```bash
# Pull by commit SHA (recommended)
docker pull quay.io/sshaaf/keycloak-mcp-server:49ff54e

# Pull latest
docker pull quay.io/sshaaf/keycloak-mcp-server:latest

# Pull by version (releases only)
docker pull quay.io/sshaaf/keycloak-mcp-server:0.3.0
```

### 3. Check Quay.io Repository

Visit: [https://quay.io/repository/sshaaf/keycloak-mcp-server?tab=tags](https://quay.io/repository/sshaaf/keycloak-mcp-server?tab=tags)

## Troubleshooting

### Issue: Container Push Fails with Authentication Error

**Error:**
```
Error: unauthorized: access to the requested resource is not authorized
```

**Solution:**
1. Verify `QUAY_USERNAME` and `QUAY_PASSWORD` secrets are set correctly
2. If using robot account, ensure it has **Write** permissions
3. Check the robot token hasn't expired

### Issue: Container Push Fails with "Repository Not Found"

**Error:**
```
Error: repository not found
```

**Solution:**
1. Create the repository on Quay.io first
2. Make sure the repository name matches: `quay.io/sshaaf/keycloak-mcp-server`
3. Ensure the repository is set to **Public** or the credentials have access

### Issue: Git Commit SHA Not Resolved

**Error:**
```
Tag contains @git.commit.id.abbrev@
```

**Solution:**
1. Ensure `fetch-depth: 0` is set in checkout step (already configured)
2. Verify the git-commit-id-maven-plugin is working
3. Check Maven build logs for git plugin execution

### Issue: Container Build Succeeds but Not Pushed

**Check:**
1. Verify you pushed to `main` branch (not a PR)
2. Check the workflow condition: `if: github.event_name == 'push' && github.ref == 'refs/heads/main'`
3. Review GitHub Actions logs for the `build-and-push-container` job

## Local Testing

You can test container building locally without pushing:

```bash
# Build without pushing
mvn clean package -Dquarkus.container-image.build=true

# Build and push manually (requires local Docker)
mvn clean package \
 -Dquarkus.container-image.build=true \
 -Dquarkus.container-image.push=true \
 -Dquarkus.container-image.username=$QUAY_USERNAME \
 -Dquarkus.container-image.password=$QUAY_PASSWORD
```

## Security Best Practices

### 1. Use Robot Accounts
- Create dedicated robot account for CI/CD
- Grant only necessary permissions (Write to specific repo)
- Easier to rotate credentials
- Better audit trail

### 2. Keep Secrets Secure
- Never commit credentials to git
- Never log credentials in workflow output
- Use GitHub Secrets for all sensitive data
- Rotate credentials periodically

### 3. Repository Settings
- Make Quay.io repository **Public** for open-source projects
- Use **Private** for proprietary code
- Enable vulnerability scanning on Quay.io
- Set up image expiration policies for old tags

## Monitoring and Maintenance

### Regular Checks

1. **Weekly**: Review pushed images on Quay.io
2. **Monthly**: Verify security scan results
3. **Quarterly**: Rotate robot account tokens
4. **As needed**: Update base image versions

### Image Cleanup

Old images accumulate over time. Consider:

1. **Quay.io Expiration Policy**:
 - Keep `latest` forever
 - Keep version tags (`0.3.0`) forever
 - Auto-expire git SHA tags after 90 days

2. **Manual Cleanup**:
 ```bash
 # List all tags
 curl https://quay.io/api/v1/repository/sshaaf/keycloak-mcp-server/tag/

 # Delete specific tag (via Quay.io UI or API)
 ```

## Summary

 **Setup**: Configure `QUAY_USERNAME` and `QUAY_PASSWORD` secrets
 **Push to main**: Automatic container build and push with git SHA
 **Pull requests**: Build only, no push (saves resources)
 **Releases**: Build and push with git SHA + version tag
 **Tags**: Git commit SHA (primary) + latest (always) + version (releases)
 **Security**: Use robot accounts with minimal permissions

---

**Your GitHub Actions are now configured to automatically push container images to Quay.io!**

For any issues, check the [GitHub Actions logs](https://github.com/sshaaf/keycloak-mcp-server/actions) or [Quay.io repository](https://quay.io/repository/sshaaf/keycloak-mcp-server).

