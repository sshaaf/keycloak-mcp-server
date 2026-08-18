package dev.shaaf.keycloak.mcp.server.commands.auth;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authentication.AuthenticationService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;

@ApplicationScoped
@RegisteredCommand
public class UpdateFlowExecutionCommand extends AbstractCommand {

    @Inject
    AuthenticationService authenticationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_FLOW_EXECUTION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "flowAlias", "executionRepresentation"};
    }

    @Override
    public String getDescription() {
        return "Update an execution in an authentication flow";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String flowAlias = requireString(params, "flowAlias");
        AuthenticationExecutionInfoRepresentation execution = extractObject(
                params, "executionRepresentation", AuthenticationExecutionInfoRepresentation.class);
        return authenticationService.updateFlowExecution(realm, flowAlias, execution);
    }
}

