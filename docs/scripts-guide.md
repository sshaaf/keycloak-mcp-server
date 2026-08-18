# Scripts

Helper scripts for working with the Keycloak MCP Server.

## get-mcp-token.sh

Obtains JWT tokens from Keycloak and generates MCP client configuration.

### Prerequisites

- `curl` and `jq` installed
- Keycloak user credentials

### Usage

```bash
./scripts/get-mcp-token.sh \
  --keycloak-url https://keycloak.example.com \
  --username your-username \
  --password your-password
```

### Options

| Option | Description | Required | Default |
|--------|-------------|----------|---------|
| `--keycloak-url` | Keycloak server URL | Yes | - |
| `--username` | Keycloak username | Yes | - |
| `--password` | Keycloak password | Yes | - |
| `--realm` | Keycloak realm | No | `master` |
| `--mcp-url` | MCP server URL | No | `http://localhost:8080/mcp/sse` |
| `--help` | Show help | No | - |

### Example Output

```
✅ Token obtained successfully!

Token expires in: 300 seconds (5 minutes)

📋 Cursor MCP Configuration:
{
  "mcpServers": {
    "keycloak": {
      "transport": "sse",
      "url": "https://mcp-server.example.com/mcp/sse",
      "headers": {
        "Authorization": "Bearer eyJhbGciOiJS..."
      }
    }
  }
}

📌 To use:
1. Copy the configuration above
2. Update ~/.cursor/mcp.json
3. Reload MCP servers in Cursor (Cmd+Shift+P → "Reload MCP Servers")
```

### Installing Dependencies

**macOS:**
```bash
brew install curl jq
```

**Ubuntu/Debian:**
```bash
sudo apt-get install curl jq
```

**RHEL/Fedora:**
```bash
sudo dnf install curl jq
```

## Security Notes

- Never commit JWT tokens to version control
- Tokens expire (typically 5-60 minutes) - refresh as needed
- Each user should use their own token
