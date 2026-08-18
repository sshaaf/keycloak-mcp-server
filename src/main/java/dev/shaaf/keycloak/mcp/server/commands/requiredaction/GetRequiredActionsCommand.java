package dev.shaaf.keycloak.mcp.server.commands.requiredaction;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.requiredaction.RequiredActionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetRequiredActionsCommand extends AbstractCommand {

    @Inject
    RequiredActionService requiredActionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REQUIRED_ACTIONS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List required actions for the realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(requiredActionService.getRequiredActions(requireString(params, "realm")));
    }
}
