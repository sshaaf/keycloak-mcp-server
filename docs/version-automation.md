# Automatic Version Management from pom.xml

## Overview

The GitHub Actions workflows now **automatically extract the version** from `pom.xml` instead of using hardcoded values. This ensures the release version is always in sync with your Maven project.

## What Changed

### Before (Hardcoded)

```yaml
env:
 APP_VERSION: '0.2.0' # Manual update required
```

**Problems**:
- Had to update workflow file manually when version changes
- Could get out of sync with pom.xml
- Easy to forget to update
- Version existed in two places

### After (Automatic)

```yaml
env:
 # APP_VERSION is automatically extracted from pom.xml in each job

steps:
 - name: Extract Version from POM
 id: get-version
 run: |
 VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
 echo "version=$VERSION" >> $GITHUB_OUTPUT
 echo "Extracted version from pom.xml: $VERSION"

 - name: Prepare release assets
 env:
 APP_VERSION: ${{ steps.get-version.outputs.version }}
 run: |
 # Uses dynamic version from pom.xml
 mv artifacts/runner-jar/${{ env.APP_NAME }}-${{ env.APP_VERSION }}-runner.jar ...
```

**Benefits**:
- Single source of truth (pom.xml)
- Always in sync
- No manual updates needed
- Version bumps automatically flow to CI/CD

## How It Works

### Maven Command

The workflow uses Maven's `help:evaluate` goal to extract the version:

```bash
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

**Flags explained**:
- `-Dexpression=project.version` - Extract the project version
- `-q` - Quiet mode (minimal output)
- `-DforceStdout` - Force output to stdout (no log formatting)

**Example output**:
```
0.3.0
```

### Workflow Steps

1. **Checkout code** - Get the repository including `pom.xml`
2. **Set up JDK** - Required to run Maven commands
3. **Extract version** - Run Maven command and save to output
4. **Use version** - Reference the extracted version in subsequent steps

### Where It's Used

#### `release.yml` - Release Workflow

The version is extracted in the `release` job and used to:
- Name release artifacts
- Tag container images
- Create GitHub releases

```yaml
mv artifacts/runner-jar/keycloak-mcp-server-0.3.0-runner.jar ...
 ↑ From pom.xml
```

#### `build-artifacts.yml` - Build and Push Workflow

The version is extracted in the `build-and-push-container` job and used to:
- Tag container images with semantic version

```yaml
-Dquarkus.container-image.additional-tags=0.3.0,latest
 ↑ From pom.xml
```

## Example: Version Bump Workflow

### 1. Update Version in pom.xml

```xml
<project>
 <groupId>dev.shaaf.keycloak.mcp.server</groupId>
 <artifactId>keycloak-mcp-server</artifactId>
 <version>0.4.0</version> <!-- Only place to update -->
 ...
</project>
```

### 2. Commit and Push

```bash
git add pom.xml
git commit -m "chore: bump version to 0.4.0"
git push origin main
```

### 3. Trigger Release

Manually trigger the release workflow from GitHub Actions UI.

### 4. Automatic Results

**Container images created**:
- `quay.io/sshaaf/keycloak-mcp-server:49ff54e` (git SHA)
- `quay.io/sshaaf/keycloak-mcp-server:0.4.0` (from pom.xml)
- `quay.io/sshaaf/keycloak-mcp-server:latest`

**Release artifacts named**:
- `keycloak-mcp-server-0.4.0-runner.jar` (from pom.xml)
- `keycloak-mcp-server-0.4.0-linux` (from pom.xml)
- `keycloak-mcp-server-0.4.0-macos` (from pom.xml)
- `keycloak-mcp-server-0.4.0-windows.exe` (from pom.xml)

**GitHub Release**:
- Tag: `v0.4.0` (from pom.xml)
- Title: `Release v0.4.0` (from pom.xml)

## Benefits

### 1. Single Source of Truth

```
pom.xml
 ↓
 → GitHub Actions (extracts)
 ↓
 → Container Tags
 → Release Artifacts
 → GitHub Releases
```

**One place to update** = No synchronization issues

### 2. Reduced Maintenance

**Before**:
```bash
# Update version in 2 places
vim pom.xml # Change to 0.4.0
vim .github/workflows/release.yml # Change to 0.4.0
git add pom.xml .github/workflows/release.yml
```

**After**:
```bash
# Update version in 1 place
vim pom.xml # Change to 0.4.0
git add pom.xml
# Complete.
```

### 3. Prevents Drift

**Before**:
- pom.xml: `0.3.0`
- release.yml: `0.2.0`
- **Result**: Wrong version in releases!

**After**:
- pom.xml: `0.3.0`
- release.yml: Automatically uses `0.3.0`
- **Result**: Always correct!

### 4. Semantic Versioning Support

Works with any valid Maven version format:

- **Release**: `1.0.0`
- **Snapshot**: `1.0.1-SNAPSHOT`
- **Qualifier**: `1.0.0-RC1`
- **Date-based**: `2024.11.20`

All automatically extracted and used!

## Troubleshooting

### Issue: Version Shows as Empty

**Symptoms**:
```
Extracted version from pom.xml:
# Empty!
```

**Cause**: Maven command failed or pom.xml has issues

**Solution**:
1. Check pom.xml is valid:
 ```bash
 mvn validate
 ```
2. Check version is defined:
 ```bash
 mvn help:evaluate -Dexpression=project.version -q -DforceStdout
 ```
3. Check GitHub Actions logs for Maven errors

### Issue: Wrong Version Extracted

**Symptoms**:
```
Extracted version from pom.xml: 3.29.4
# Expected: 0.3.0
```

**Cause**: Maven is reading a parent POM version or dependency version

**Solution**:
1. Ensure `<version>` tag is in your project's `pom.xml`:
 ```xml
 <project>
 <version>0.3.0</version> <!-- Must be here -->
 ```
2. If using parent POM, ensure project has its own version
3. Check with local Maven:
 ```bash
 cd /path/to/project
 mvn help:evaluate -Dexpression=project.version -q -DforceStdout
 ```

### Issue: Artifacts Not Found

**Symptoms**:
```
mv: cannot stat 'artifacts/runner-jar/keycloak-mcp-server-0.3.0-runner.jar': No such file
```

**Cause**: Artifact filenames don't match the extracted version

**Solution**:
1. Ensure build jobs use the same version from pom.xml
2. Check artifact upload step uses wildcard: `keycloak-mcp-server-*-runner.jar`
3. Verify pom.xml version matches built artifact names

## Testing Locally

You can test the version extraction locally:

```bash
# Extract version
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Version: $VERSION"

# Test artifact naming
echo "Artifact would be: keycloak-mcp-server-${VERSION}-runner.jar"

# Test container tag
echo "Container tag would be: quay.io/sshaaf/keycloak-mcp-server:${VERSION}"
```

## Best Practices

### 1. Version Bumping

Use Maven versions plugin for consistency:

```bash
# Bump to next development version
mvn versions:set -DnewVersion=0.4.0-SNAPSHOT

# Bump to release version
mvn versions:set -DnewVersion=0.4.0

# Remove backup files
mvn versions:commit
```

### 2. Semantic Versioning

Follow semantic versioning in pom.xml:

- **Major** (breaking changes): `1.0.0` → `2.0.0`
- **Minor** (new features): `1.0.0` → `1.1.0`
- **Patch** (bug fixes): `1.0.0` → `1.0.1`

GitHub Actions will automatically use the correct version!

### 3. Pre-release Versions

Use qualifiers for pre-releases:

```xml
<version>1.0.0-RC1</version> <!-- Release Candidate -->
<version>1.0.0-beta.1</version> <!-- Beta -->
<version>1.0.0-alpha</version> <!-- Alpha -->
```

These will be extracted and used as-is in container tags and releases.

### 4. Snapshot Development

Use `-SNAPSHOT` for development:

```xml
<version>1.0.0-SNAPSHOT</version>
```

**Note**: Snapshots are typically not released, but the version will still be extracted correctly if you trigger a release workflow.

## Summary

 **Single source of truth**: Version only in `pom.xml`
 **Automatic extraction**: Maven reads version at runtime
 **Always in sync**: No drift between pom.xml and CI/CD
 **Less maintenance**: No manual workflow updates
 **Semantic versioning**: Works with any Maven version format
 **Transparent**: Logs show extracted version

---

**No more version drift!**

Your GitHub Actions will always use the version from `pom.xml` automatically.

## Related Documentation

- [github-actions-setup.md](github-actions-setup.md) - CI/CD setup guide
- [git-commit-tagging.md](git-commit-tagging.md) - Git SHA tagging
- [CI_CD_UPDATES.md](CI_CD_UPDATES.md) - All CI/CD changes

