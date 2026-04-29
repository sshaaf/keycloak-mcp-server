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
public class RemoveOrganizationMemberCommand extends AbstractCommand {

    @Inject
    OrganizationService organizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.REMOVE_ORGANIZATION_MEMBER;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "orgId", "userId"};
    }

    @Override
    public String getDescription() {
        return "Remove a user from an organization";
    }

    @Override
    public String execute(JsonNode params) {
        return organizationService.removeOrganizationMember(
                requireString(params, "realm"),
                requireString(params, "orgId"),
                requireString(params, "userId"));
    }
}
