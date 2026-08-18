package dev.shaaf.keycloak.mcp.server.commands.requiredaction;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.requiredaction.RequiredActionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetRequiredActionCommand extends AbstractCommand {

    @Inject
    RequiredActionService requiredActionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REQUIRED_ACTION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias"};
    }

    @Override
    public String getDescription() {
        return "Get a required action by provider alias";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        RequiredActionProviderRepresentation r = requiredActionService.getRequiredAction(
                requireString(params, "realm"), requireString(params, "alias"));
        return r == null ? "null" : toJson(r);
    }
}
