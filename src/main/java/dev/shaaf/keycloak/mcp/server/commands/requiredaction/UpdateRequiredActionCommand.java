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
public class UpdateRequiredActionCommand extends AbstractCommand {

    @Inject
    RequiredActionService requiredActionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_REQUIRED_ACTION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "alias", "requiredAction"};
    }

    @Override
    public String getDescription() {
        return "Update a required action (enabled, name, etc.)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return requiredActionService.updateRequiredAction(
                requireString(params, "realm"),
                requireString(params, "alias"),
                extractObject(params, "requiredAction", RequiredActionProviderRepresentation.class));
    }
}
