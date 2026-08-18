package dev.shaaf.keycloak.mcp.server.commands.auth;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.authentication.AuthenticationService;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;

@ApplicationScoped
@RegisteredCommand
public class CreateAuthenticationFlowCommand extends AbstractCommand {

    @Inject
    AuthenticationService authenticationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_AUTHENTICATION_FLOW;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "authFlowNameId"};
    }

    @Override
    public String getDescription() {
        return "Create a copy of an existing authentication flow";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String realm = requireString(params, "realm");
        String authFlowNameId = requireString(params, "authFlowNameId");
        
        // Get existing flow and create a copy
        AuthenticationFlowRepresentation flowRep = authenticationService.getAuthenticationFlow(realm, authFlowNameId);
        flowRep.setId(null);
        flowRep.setAlias(authFlowNameId + "-copy");
        
        return authenticationService.createAuthenticationFlow(realm, flowRep);
    }
}

