package dev.shaaf.keycloak.mcp.server.commands.component;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.component.ComponentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class DeleteComponentCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_COMPONENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "componentId"};
    }

    @Override
    public String getDescription() {
        return "Delete a realm component";
    }

    @Override
    public String execute(JsonNode params) {
        return componentService.deleteComponent(
                requireString(params, "realm"), requireString(params, "componentId"));
    }
}
