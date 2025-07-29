# Release Workflow Changes

## Changes Made

The GitHub Actions release workflow has been modified to prevent releases from happening on the main branch. Instead, releases can only be triggered from branches with the prefix "release-".

### Specific Changes:

1. Modified the `release.yml` workflow file to change the branch trigger from `main` to a pattern that matches branches starting with `release-`:

```diff
on:
  push:
    branches:
-      - main
+      - 'release-**'
  workflow_dispatch: # Allows you to run this workflow manually from the Actions tab
```

## Rationale

This change ensures that releases are only made from dedicated release branches, which is a best practice for CI/CD workflows. It provides several benefits:

1. **Separation of Concerns**: Development work continues on the main branch without affecting release processes.
2. **Release Preparation**: Dedicated release branches allow for final testing and preparation before a release.
3. **Hotfix Support**: Hotfixes can be applied to release branches without affecting ongoing development.
4. **Release Tracking**: Each release branch clearly represents a specific release version.

## How to Create a Release

To create a release:

1. Create a new branch from main with the prefix "release-", e.g., `release-v1.2.3`
2. Make any final adjustments needed for the release
3. Push the branch to GitHub, which will automatically trigger the release workflow
4. Alternatively, you can manually trigger the workflow from the Actions tab

## Note on Manual Triggers

The workflow can still be manually triggered using the workflow_dispatch event. When manually triggering the workflow, make sure you're on a branch with the "release-" prefix to maintain the intended release process.