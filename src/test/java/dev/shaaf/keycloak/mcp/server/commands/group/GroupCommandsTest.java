package dev.shaaf.keycloak.mcp.server.commands.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Group commands: Phase 1 adds GET_GROUP, GET_GROUP_ROLES, role mapping;
 * subgroups (GET_SUBGROUPS, CREATE_SUBGROUP) from issue #14.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupCommandsTest {

    private static final String REALM = "quarkus";
    private static final String GROUP_NAME = "test-group";

    @Inject
    KeycloakTool keycloakTool;

    @Inject
    ObjectMapper objectMapper;

    private String findGroupIdByName(String groupsJson, String name) throws Exception {
        for (JsonNode n : objectMapper.readTree(groupsJson)) {
            if (name.equals(n.path("name").asText(null))) {
                return n.path("id").asText(null);
            }
        }
        return null;
    }

    @Test
    @Order(1)
    public void testGetGroups() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );
        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    @Order(2)
    public void testCreateGroup() {
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_GROUP,
                "{\"realm\": \"" + REALM + "\", \"groupName\": \"" + GROUP_NAME + "\"}"
        );
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("created") || result.toLowerCase().contains("success"));
    }

    @Test
    @Order(3)
    public void testGetGroupPhase1() throws Exception {
        String groupsJson = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUPS,
                "{\"realm\": \"" + REALM + "\"}"
        );
        String groupId = findGroupIdByName(groupsJson, GROUP_NAME);
        assertNotNull(groupId, groupsJson);

        String one = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUP,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + groupId + "\"}"
        );
        assertNotNull(one);
        assertTrue(one.contains(GROUP_NAME) || one.contains("null"), one);
    }

    @Test
    @Order(4)
    public void testGetSubGroups() throws Exception {
        String groupId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(groupId);
        String subs = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_SUBGROUPS,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + groupId + "\"}"
        );
        assertNotNull(subs);
        assertTrue(subs.startsWith("["));
    }

    @Test
    @Order(5)
    public void testCreateSubgroupAndListSubGroups() throws Exception {
        String groupId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(groupId);
        String created = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.CREATE_SUBGROUP,
                """
                {
                    "realm": "%s",
                    "parentGroupId": "%s",
                    "subGroupName": "test-sub"
                }
                """.formatted(REALM, groupId)
        );
        assertNotNull(created);
        String subs = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_SUBGROUPS,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + groupId + "\"}"
        );
        assertNotNull(subs);
        assertTrue(subs.contains("test-sub") || subs.contains("sub"), subs);
    }

    @Test
    @Order(6)
    public void testGetGroupRolesPhase1() throws Exception {
        String groupId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(groupId);
        String roles = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUP_ROLES,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + groupId + "\"}"
        );
        assertNotNull(roles);
        assertTrue(roles.startsWith("["));
    }

    @Test
    @Order(7)
    public void testGetGroupMembers() throws Exception {
        String groupId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(groupId);
        String members = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_GROUP_MEMBERS,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + groupId + "\"}"
        );
        assertNotNull(members);
    }

    @Test
    @Order(8)
    public void testUpdateGroup() throws Exception {
        String groupId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(groupId);
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.UPDATE_GROUP,
                """
                {
                    "realm": "%s",
                    "groupId": "%s",
                    "groupRepresentation": {
                        "id": "%s",
                        "name": "%s",
                        "path": "/%s"
                    }
                }
                """.formatted(REALM, groupId, groupId, GROUP_NAME, GROUP_NAME)
        );
        assertNotNull(result);
    }

    @Test
    @Order(100)
    public void testDeleteGroup() throws Exception {
        String parentId = findGroupIdByName(
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.GET_GROUPS,
                        "{\"realm\": \"" + REALM + "\"}"
                ),
                GROUP_NAME
        );
        assertNotNull(parentId, "parent group to delete not found");
        // Delete child subgroup first (Keycloak may block deleting a parent with children)
        String subsJson = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.GET_SUBGROUPS,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + parentId + "\"}"
        );
        for (JsonNode n : objectMapper.readTree(subsJson)) {
            String subId = n.path("id").asText(null);
            if (subId != null) {
                keycloakTool.executeKeycloakOperation(
                        KeycloakOperation.DELETE_GROUP,
                        "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + subId + "\"}"
                );
            }
        }
        String result = keycloakTool.executeKeycloakOperation(
                KeycloakOperation.DELETE_GROUP,
                "{\"realm\": \"" + REALM + "\", \"groupId\": \"" + parentId + "\"}"
        );
        assertNotNull(result);
    }
}
