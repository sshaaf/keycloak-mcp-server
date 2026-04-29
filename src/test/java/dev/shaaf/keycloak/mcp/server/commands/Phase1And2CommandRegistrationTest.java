package dev.shaaf.keycloak.mcp.server.commands;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that every operation from Phase 1 (24) and Phase 2 (15) of the
 * API expansion plan is registered in {@link CommandRegistry}, plus
 * {@link KeycloakOperation#GET_SUBGROUPS} (issue #14) added with group tooling.
 * <p>
 * Phase docs reference the old monolithic {@code KeycloakTool} switch; the
 * implementation uses one {@link dev.shaaf.keycloak.mcp.server.KeycloakTool}
 * method and CDI-registered commands instead.
 */
@QuarkusTest
class Phase1And2CommandRegistrationTest {

    @Inject
    CommandRegistry registry;

    /** Phase 1 — 24 operations (see PHASE1_IMPLEMENTATION_SUMMARY.md). */
    private static final KeycloakOperation[] PHASE1 = {
            // Realm
            KeycloakOperation.UPDATE_REALM,
            KeycloakOperation.DELETE_REALM,
            KeycloakOperation.SET_REALM_ENABLED,
            KeycloakOperation.GET_REALM_EVENTS_CONFIG,
            KeycloakOperation.UPDATE_REALM_EVENTS_CONFIG,
            // Client
            KeycloakOperation.UPDATE_CLIENT,
            KeycloakOperation.GET_CLIENT_SECRET,
            KeycloakOperation.GET_SERVICE_ACCOUNT_USER,
            KeycloakOperation.GET_CLIENT_PROTOCOL_MAPPERS,
            KeycloakOperation.ADD_PROTOCOL_MAPPER_TO_CLIENT,
            // Role
            KeycloakOperation.CREATE_REALM_ROLE,
            KeycloakOperation.UPDATE_REALM_ROLE,
            KeycloakOperation.DELETE_REALM_ROLE,
            KeycloakOperation.GET_ROLE_COMPOSITES,
            KeycloakOperation.ADD_COMPOSITE_TO_ROLE,
            KeycloakOperation.REMOVE_COMPOSITE_FROM_ROLE,
            // Group
            KeycloakOperation.GET_GROUP,
            KeycloakOperation.GET_GROUP_ROLES,
            KeycloakOperation.ADD_ROLE_TO_GROUP,
            KeycloakOperation.REMOVE_ROLE_FROM_GROUP,
            // Identity provider
            KeycloakOperation.CREATE_IDENTITY_PROVIDER,
            KeycloakOperation.UPDATE_IDENTITY_PROVIDER,
            KeycloakOperation.DELETE_IDENTITY_PROVIDER,
            KeycloakOperation.CREATE_IDENTITY_PROVIDER_MAPPER,
    };

    /** Phase 2 — 15 client-scope operations (see PHASE2_IMPLEMENTATION_SUMMARY.md). */
    private static final KeycloakOperation[] PHASE2 = {
            KeycloakOperation.GET_CLIENT_SCOPES,
            KeycloakOperation.GET_CLIENT_SCOPE,
            KeycloakOperation.CREATE_CLIENT_SCOPE,
            KeycloakOperation.UPDATE_CLIENT_SCOPE,
            KeycloakOperation.DELETE_CLIENT_SCOPE,
            KeycloakOperation.GET_CLIENT_SCOPE_PROTOCOL_MAPPERS,
            KeycloakOperation.ADD_PROTOCOL_MAPPER_TO_CLIENT_SCOPE,
            KeycloakOperation.UPDATE_CLIENT_SCOPE_PROTOCOL_MAPPER,
            KeycloakOperation.DELETE_CLIENT_SCOPE_PROTOCOL_MAPPER,
            KeycloakOperation.ADD_DEFAULT_CLIENT_SCOPE,
            KeycloakOperation.REMOVE_DEFAULT_CLIENT_SCOPE,
            KeycloakOperation.ADD_OPTIONAL_CLIENT_SCOPE,
            KeycloakOperation.REMOVE_OPTIONAL_CLIENT_SCOPE,
            KeycloakOperation.GET_DEFAULT_CLIENT_SCOPES,
            KeycloakOperation.GET_OPTIONAL_CLIENT_SCOPES,
    };

    private static final KeycloakOperation[] EXTRAS = {
            KeycloakOperation.GET_SUBGROUPS,
    };

    @Test
    void allPhase1OperationsRegistered() {
        for (KeycloakOperation op : PHASE1) {
            assertTrue(registry.isAvailable(op), "Phase 1 command missing: " + op);
            assertNotNull(registry.getCommand(op), "Phase 1 command null: " + op);
        }
    }

    @Test
    void allPhase2OperationsRegistered() {
        for (KeycloakOperation op : PHASE2) {
            assertTrue(registry.isAvailable(op), "Phase 2 command missing: " + op);
            assertNotNull(registry.getCommand(op), "Phase 2 command null: " + op);
        }
    }

    @Test
    void subgroupCommandRegistered() {
        for (KeycloakOperation op : EXTRAS) {
            assertTrue(registry.isAvailable(op), "Command missing: " + op);
            assertNotNull(registry.getCommand(op), "Command null: " + op);
        }
    }
}
