# Keycloak MCP Server Scripts

This directory contains helper scripts for managing the Keycloak MCP Server.

## Available Scripts

### `get-mcp-token.sh`

Helper script for obtaining JWT tokens from Keycloak and generating Cursor MCP configuration.

**Features:**
- Authenticates with Keycloak
- Retrieves JWT token for the user
- Generates Cursor MCP configuration
- Shows token expiration time
- Displays user roles and permissions

**Prerequisites:**
- `curl` command installed
- `jq` command installed (JSON processor)
- Keycloak user credentials

**Usage:**

```bash
# Basic usage
./get-mcp-token.sh \
 --keycloak-url https://keycloak.example.com \
 --username your-username \
 --password your-password

# With custom realm and MCP URL
./get-mcp-token.sh \
 --keycloak-url https://keycloak.example.com \
 --realm master \
 --username your-username \
 --password your-password \
 --mcp-url https://mcp-server.example.com/mcp/sse

# Show help
./get-mcp-token.sh --help
```

**Options:**

| Option | Description | Required | Default |
|--------|-------------|----------|---------|
| `--keycloak-url` | Keycloak server URL | Yes | - |
| `--username` | Your Keycloak username | Yes | - |
| `--password` | Your Keycloak password | Yes | - |
| `--realm` | Keycloak realm | No | `master` |
| `--mcp-url` | MCP server URL | No | `http://localhost:8080/mcp/sse` |
| `--help` | Display help message | No | - |

**Output:**

The script will:
1. Authenticate with Keycloak using your credentials
2. Retrieve your JWT token
3. Generate Cursor MCP configuration
4. Show token expiration time
5. Display your realm roles

**Example Output:**

```
 Token obtained successfully!

Token expires in: 300 seconds (5 minutes)

 Cursor MCP Configuration:
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

 To use:
1. Copy the configuration above
2. Update ~/.cursor/mcp.json
3. Reload MCP servers in Cursor (Cmd+Shift+P → "Reload MCP Servers")
```

## Installing Dependencies

### macOS (Homebrew)

```bash
brew install curl jq
```

### Ubuntu/Debian

```bash
sudo apt-get install curl jq
```

### RHEL/CentOS/Fedora

```bash
sudo dnf install curl jq
```

### Windows (WSL)

```bash
sudo apt-get install curl jq
```

## Troubleshooting

### `jq: command not found`

Install `jq` using your package manager (see above).

### `curl: command not found`

Install `curl` using your package manager (see above).

### Authentication Failed

- Verify your Keycloak URL is correct and accessible
- Check admin username and password
- Ensure your admin user has sufficient permissions

### Client Already Exists

The script will update the existing client configuration. This is safe and ensures the client has the correct settings.

### Network Timeout

- Check your network connection
- Verify Keycloak server is accessible
- Try increasing timeout by modifying the curl commands in the script

## Security Considerations

 **Important Security Notes:**

1. **Never commit JWT tokens** to version control
2. **Tokens expire** - Refresh them regularly (typically 5-60 minutes)
3. **Use strong passwords** for your Keycloak account
4. **Each user has their own token** - Don't share tokens between users
5. **Monitor your account activity** in Keycloak audit logs

## Contributing

If you find issues or have suggestions for improving these scripts, please:

1. Open an issue on GitHub
2. Submit a pull request
3. Discuss in the Keycloak Discourse

## License

These scripts are part of the Keycloak MCP Server project and are licensed under the same license (MIT).

