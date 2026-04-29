package dev.shaaf.keycloak.mcp.server.commands.organization;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.organization.OrganizationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.OrganizationRepresentation;

@ApplicationScoped
@RegisteredCommand
public class GetOrganizationCommand extends AbstractCommand {

    @Inject
    OrganizationService organizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_ORGANIZATION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "orgId"};
    }

    @Override
    public String getDescription() {
        return "Get an organization by id";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        OrganizationRepresentation o = organizationService.getOrganization(
                requireString(params, "realm"), requireString(params, "orgId"));
        if (o == null) {
            return "null";
        }
        return toJson(o);
    }
}
