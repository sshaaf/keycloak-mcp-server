package dev.shaaf.keycloak.mcp.server.commands.auth;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authentication.AuthenticationService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class DeleteAuthenticationFlowCommand extends AbstractCommand {

    @Inject
    AuthenticationService authenticationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_AUTHENTICATION_FLOW;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "flowId"};
    }

    @Override
    public String getDescription() {
        return "Delete an authentication flow";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return authenticationService.deleteAuthenticationFlow(
                requireString(params, "realm"),
                requireString(params, "flowId")
        );
    }
}

