package dev.shaaf.keycloak.mcp.server.commands.organization;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.organization.OrganizationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetOrganizationsCommand extends AbstractCommand {

    @Inject
    OrganizationService organizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_ORGANIZATIONS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List all organizations in the realm (Keycloak 24+ organizations feature).";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(organizationService.getOrganizations(requireString(params, "realm")));
    }
}
