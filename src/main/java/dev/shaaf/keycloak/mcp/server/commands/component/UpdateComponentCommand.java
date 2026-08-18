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
public class UpdateComponentCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_COMPONENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "componentId", "component"};
    }

    @Override
    public String getDescription() {
        return "Update a realm component";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return componentService.updateComponent(
                requireString(params, "realm"),
                requireString(params, "componentId"),
                extractObject(params, "component", ComponentRepresentation.class));
    }
}
