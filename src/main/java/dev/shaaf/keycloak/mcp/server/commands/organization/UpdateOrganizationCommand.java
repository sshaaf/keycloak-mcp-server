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
public class UpdateOrganizationCommand extends AbstractCommand {

    @Inject
    OrganizationService organizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_ORGANIZATION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "orgId", "organization"};
    }

    @Override
    public String getDescription() {
        return "Update an organization";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return organizationService.updateOrganization(
                requireString(params, "realm"),
                requireString(params, "orgId"),
                extractObject(params, "organization", OrganizationRepresentation.class));
    }
}
