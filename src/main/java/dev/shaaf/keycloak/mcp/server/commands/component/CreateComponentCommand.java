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
public class CreateComponentCommand extends AbstractCommand {

    @Inject
    ComponentService componentService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_COMPONENT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "component"};
    }

    @Override
    public String getDescription() {
        return "Create a realm component (e.g. LDAP user federation)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return componentService.createComponent(
                requireString(params, "realm"),
                extractObject(params, "component", ComponentRepresentation.class));
    }
}
