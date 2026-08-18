package dev.shaaf.keycloak.mcp.server.commands.component;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.component.ComponentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.ComponentRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetComponentCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_COMPONENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "componentId"};
    }

    @Override
    public String getDescription() {
        return "Get a component by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        ComponentRepresentation c = componentService.getComponent(
                requireString(params, "realm"), requireString(params, "componentId"));
        if (c == null) {
            return "null";
        }
        return toJson(c);
    }
}
