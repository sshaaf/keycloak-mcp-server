package dev.shaaf.keycloak.mcp.server.commands.realm;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.realm.RealmService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.TestLdapConnectionRepresentation;

@ApplicationScoped
@RegisteredCommand
public class TestLdapConnectionCommand extends AbstractCommand {

    @Inject
    RealmService realmService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.TEST_LDAP_CONNECTION;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "test"};
    }

    @Override
    public String getDescription() {
        return "Test an LDAP or user federation connection (Keycloak TestLdapConnectionRepresentation JSON in test)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return realmService.testLdapConnection(
                requireString(params, "realm"),
                extractObject(params, "test", TestLdapConnectionRepresentation.class));
    }
}
