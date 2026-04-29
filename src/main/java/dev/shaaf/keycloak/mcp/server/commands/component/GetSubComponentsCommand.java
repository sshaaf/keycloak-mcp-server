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
public class GetSubComponentsCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_SUB_COMPONENTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "parentId", "type"};
    }

    @Override
    public String getDescription() {
        return "List sub-components of a parent component and provider type";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(componentService.getSubComponents(
                requireString(params, "realm"),
                requireString(params, "parentId"),
                requireString(params, "type")));
    }
}
