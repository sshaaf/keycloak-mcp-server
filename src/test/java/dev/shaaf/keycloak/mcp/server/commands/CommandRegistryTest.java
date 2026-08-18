package dev.shaaf.keycloak.mcp.server.commands;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CommandRegistry functionality.
 * Verifies that commands are properly discovered and registered.
 */
@QuarkusTest
public class CommandRegistryTest {

    @Inject
    CommandRegistry registry;

    @Test
    public void testRegistryIsInjected() {
        assertNotNull(registry);
    }

    @Test
    public void testCommandsAreDiscovered() {
        assertTrue(registry.getCommandCount() > 0, "Should have discovered at least one command");
    }

    @Test
    public void testGetUsersCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USERS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_USERS));
    }

    @Test
    public void testGetRealmsCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_REALMS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_REALMS));
    }

    @Test
    public void testGetClientsCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_CLIENTS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_CLIENTS));
    }

    @Test
    public void testGetGroupsCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_GROUPS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_GROUPS));
    }

    @Test
    public void testGetRealmRolesCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_REALM_ROLES));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_REALM_ROLES));
    }

    @Test
    public void testGetIdentityProvidersCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_IDENTITY_PROVIDERS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_IDENTITY_PROVIDERS));
    }

    @Test
    public void testGetAuthenticationFlowsCommandIsAvailable() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_AUTHENTICATION_FLOWS));
        assertNotNull(registry.getCommand(KeycloakOperation.GET_AUTHENTICATION_FLOWS));
    }

    @Test
    public void testAvailableOperationsString() {
        String ops = registry.getAvailableOperationsString();
        assertNotNull(ops);
        assertFalse(ops.isEmpty());
        assertTrue(ops.contains("GET_USERS"));
        assertTrue(ops.contains("GET_REALMS"));
    }

    @Test
    public void testAllUserCommandsRegistered() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USERS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USER_BY_USERNAME));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_USER));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USER_BY_ID));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USER_GROUPS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_USER_ROLES));
        assertTrue(registry.isAvailable(KeycloakOperation.COUNT_USERS));
    }

    @Test
    public void testAllRealmCommandsRegistered() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_REALMS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_REALM));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_REALM));
    }

    @Test
    public void testAllClientCommandsRegistered() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_CLIENTS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_CLIENT));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_CLIENT));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_CLIENT_ROLES));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_CLIENT_ROLE));
        assertTrue(registry.isAvailable(KeycloakOperation.GENERATE_CLIENT_SECRET));
    }

    @Test
    public void testAllGroupCommandsRegistered() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_GROUPS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_GROUP_MEMBERS));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_GROUP));
        assertTrue(registry.isAvailable(KeycloakOperation.UPDATE_GROUP));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_SUBGROUP));
    }

    @Test
    public void testAllAuthCommandsRegistered() {
        assertTrue(registry.isAvailable(KeycloakOperation.GET_AUTHENTICATION_FLOWS));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_AUTHENTICATION_FLOW));
        assertTrue(registry.isAvailable(KeycloakOperation.CREATE_AUTHENTICATION_FLOW));
        assertTrue(registry.isAvailable(KeycloakOperation.GET_FLOW_EXECUTIONS));
        assertTrue(registry.isAvailable(KeycloakOperation.UPDATE_FLOW_EXECUTION));
    }
}

