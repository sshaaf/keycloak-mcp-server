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
public class GetComponentsCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_COMPONENTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List all components in the realm (user storage, keys, etc.)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(componentService.getComponents(requireString(params, "realm")));
    }
}
