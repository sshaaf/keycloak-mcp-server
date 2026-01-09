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
public class GetFlowExecutionsCommand extends AbstractCommand {

    @Inject
    AuthenticationService authenticationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_FLOW_EXECUTIONS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "flowAlias"};
    }

    @Override
    public String getDescription() {
        return "Get executions for an authentication flow";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String flowAlias = requireString(params, "flowAlias");
        return toJson(authenticationService.getFlowExecutions(realm, flowAlias));
    }
}

