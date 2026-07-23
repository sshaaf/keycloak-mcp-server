package dev.shaaf.keycloak.mcp.server.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import dev.shaaf.keycloak.mcp.server.commands.CommandRegistry;
import dev.shaaf.keycloak.mcp.server.commands.KeycloakCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generates {@code docs/operations.md} with live request/response samples
 * against Keycloak Dev Services (quarkus realm).
 * <p>
 * Run:
 * {@code mvn -Dtest=OperationsDocGeneratorTest -Dgenerate.operations.docs=true test}
 */
@QuarkusTest
@TestProfile(DocsKeycloakTestProfile.class)
public class OperationsDocGeneratorTest {

    private static final String REALM = "quarkus";
    private static final Path DOCS_OUT = Path.of("docs/operations.md");
    private static final int MAX_OUTPUT_CHARS = 1800;

    @Inject
    KeycloakTool tool;

    @Inject
    CommandRegistry registry;

    @Inject
    ObjectMapper mapper;

    private final Map<String, String> ids = new LinkedHashMap<>();
    private final ObjectMapper pretty = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @EnabledIfSystemProperty(named = "generate.operations.docs", matches = "true")
    public void generateOperationsDocumentation() throws Exception {
        assertTrue(registry.getCommandCount() > 0);

        List<Section> sections = buildAndExecuteExamples();
        String markdown = renderMarkdown(sections);
        Files.createDirectories(DOCS_OUT.getParent());
        Files.writeString(DOCS_OUT, markdown, StandardCharsets.UTF_8);
        System.out.println("Wrote " + DOCS_OUT.toAbsolutePath() + " (" + sections.size() + " examples)");
    }

    private List<Section> buildAndExecuteExamples() throws Exception {
        List<Section> out = new ArrayList<>();

        // --- seed context from existing realm ---
        capture("GET_REALMS", "{}", "Realm", out);
        capture("GET_REALM", obj("realmName", REALM), "Realm", out);
        capture("GET_USERS", obj("realm", REALM), "User", out);
        String adminUser = safeExec(KeycloakOperation.GET_USER_BY_USERNAME,
                obj("realm", REALM, "username", "admin"));
        ids.put("adminUserId", firstId(adminUser));
        if (adminUser != null) {
            captureRaw("GET_USER_BY_USERNAME", obj("realm", REALM, "username", "admin"), adminUser, "User", out);
        }
        if (ids.get("adminUserId") == null) {
            throw new IllegalStateException("Could not resolve admin user id from Keycloak Dev Services");
        }
        capture("GET_USER_BY_ID", obj("realm", REALM, "userId", ids.get("adminUserId")), "User", out);
        capture("COUNT_USERS", obj("realm", REALM), "User", out);
        capture("GET_USER_GROUPS", obj("realm", REALM, "userId", ids.get("adminUserId")), "User", out);
        capture("GET_USER_ROLES", obj("realm", REALM, "userId", ids.get("adminUserId")), "User", out);
        capture("GET_USER_SESSIONS", obj("realm", REALM, "userId", ids.get("adminUserId")), "Sessions", out);
        capture("GET_USER_CONSENTS", obj("realm", REALM, "userId", ids.get("adminUserId")), "Sessions", out);
        capture("GET_USER_CREDENTIALS", obj("realm", REALM, "userId", ids.get("adminUserId")), "Credentials", out);
        capture("GET_USER_BRUTE_FORCE_STATUS", obj("realm", REALM, "userId", ids.get("adminUserId")), "Sessions", out);

        capture("GET_CLIENTS", obj("realm", REALM), "Client", out);
        String backend = tool.executeKeycloakOperation(
                KeycloakOperation.GET_CLIENT,
                obj("realm", REALM, "clientId", "backend-service"));
        ids.put("backendInternalId", firstId(backend));
        captureRaw("GET_CLIENT", obj("realm", REALM, "clientId", "backend-service"), backend, "Client", out);
        capture("GET_CLIENT_SECRET", obj("realm", REALM, "clientId", "backend-service"), "Client", out);
        capture("GET_CLIENT_ROLES", obj("realm", REALM, "clientId", "backend-service"), "Client", out);
        capture("GET_CLIENT_PROTOCOL_MAPPERS", obj("realm", REALM, "clientId", "backend-service"), "Client", out);
        capture("GET_CLIENT_USER_SESSIONS", obj("realm", REALM, "clientId", "backend-service"), "Sessions", out);
        capture("GET_CLIENT_OFFLINE_SESSIONS", obj("realm", REALM, "clientId", "backend-service"), "Sessions", out);
        capture("GET_SERVICE_ACCOUNT_USER", obj("realm", REALM, "clientId", "backend-service"), "Client", out);
        capture("GET_USER_CLIENT_ROLES",
                obj("realm", REALM, "userId", ids.get("adminUserId"), "clientId", ids.get("backendInternalId")),
                "User client roles", out);
        capture("GET_USER_AVAILABLE_CLIENT_ROLES",
                obj("realm", REALM, "userId", ids.get("adminUserId"), "clientId", ids.get("backendInternalId")),
                "User client roles", out);

        capture("GET_CLIENT_SCOPES", obj("realm", REALM), "Client scope", out);
        String scopes = tool.executeKeycloakOperation(KeycloakOperation.GET_CLIENT_SCOPES, obj("realm", REALM));
        ids.put("clientScopeId", firstId(scopes));
        if (ids.get("clientScopeId") != null) {
            capture("GET_CLIENT_SCOPE", obj("realm", REALM, "clientScopeId", ids.get("clientScopeId")), "Client scope", out);
            capture("GET_CLIENT_SCOPE_PROTOCOL_MAPPERS",
                    obj("realm", REALM, "clientScopeId", ids.get("clientScopeId")), "Client scope", out);
            capture("GET_CLIENT_SCOPE_SCOPE_MAPPINGS",
                    obj("realm", REALM, "clientScopeId", ids.get("clientScopeId")), "Scope mappings", out);
            capture("GET_CLIENT_SCOPE_MAPPED_REALM_ROLES",
                    obj("realm", REALM, "clientScopeId", ids.get("clientScopeId")), "Scope mappings", out);
            capture("GET_CLIENT_SCOPE_MAPPED_CLIENT_ROLES",
                    obj("realm", REALM, "clientScopeId", ids.get("clientScopeId"),
                            "targetClientId", ids.get("backendInternalId")),
                    "Scope mappings", out);
        }
        capture("GET_DEFAULT_CLIENT_SCOPES", obj("realm", REALM), "Client scope", out);
        capture("GET_OPTIONAL_CLIENT_SCOPES", obj("realm", REALM), "Client scope", out);

        capture("GET_REALM_ROLES", obj("realm", REALM), "Role", out);
        capture("GET_REALM_ROLE", obj("realm", REALM, "roleName", "admin"), "Role", out);
        capture("GET_ROLE_COMPOSITES", obj("realm", REALM, "roleName", "admin"), "Role", out);

        capture("GET_GROUPS", obj("realm", REALM), "Group", out);
        String groups = tool.executeKeycloakOperation(KeycloakOperation.GET_GROUPS, obj("realm", REALM));
        String groupId = firstId(groups);
        if (groupId == null) {
            tool.executeKeycloakOperation(KeycloakOperation.CREATE_GROUP,
                    obj("realm", REALM, "groupName", "docs-sample-group"));
            groups = tool.executeKeycloakOperation(KeycloakOperation.GET_GROUPS, obj("realm", REALM));
            groupId = findIdByName(groups, "docs-sample-group");
        }
        ids.put("groupId", groupId);
        if (groupId != null) {
            capture("GET_GROUP", obj("realm", REALM, "groupId", groupId), "Group", out);
            capture("GET_GROUP_MEMBERS", obj("realm", REALM, "groupId", groupId), "Group", out);
            capture("GET_SUBGROUPS", obj("realm", REALM, "groupId", groupId), "Group", out);
            capture("GET_GROUP_ROLES", obj("realm", REALM, "groupId", groupId), "Group", out);
            capture("GET_GROUP_CLIENT_ROLES",
                    obj("realm", REALM, "groupId", groupId, "clientId", ids.get("backendInternalId")),
                    "Group client roles", out);
            capture("GET_GROUP_AVAILABLE_CLIENT_ROLES",
                    obj("realm", REALM, "groupId", groupId, "clientId", ids.get("backendInternalId")),
                    "Group client roles", out);
        }

        capture("GET_IDENTITY_PROVIDERS", obj("realm", REALM), "Identity provider", out);
        capture("GET_AUTHENTICATION_FLOWS", obj("realm", REALM), "Authentication", out);
        capture("GET_FLOW_EXECUTIONS", obj("realm", REALM, "flowAlias", "browser"), "Authentication", out);
        String flows = tool.executeKeycloakOperation(KeycloakOperation.GET_AUTHENTICATION_FLOWS, obj("realm", REALM));
        String flowId = firstId(flows);
        if (flowId != null) {
            capture("GET_AUTHENTICATION_FLOW", obj("realm", REALM, "flowId", flowId), "Authentication", out);
        }

        capture("GET_ADMIN_EVENTS", obj("realm", REALM), "Events", out);
        capture("GET_USER_EVENTS", obj("realm", REALM), "Events", out);
        capture("GET_REALM_EVENTS_CONFIG", obj("realmName", REALM), "Realm", out);
        capture("GET_COMPONENTS", obj("realm", REALM), "Component", out);
        String components = tool.executeKeycloakOperation(KeycloakOperation.GET_COMPONENTS, obj("realm", REALM));
        String componentId = firstId(components);
        if (componentId != null) {
            capture("GET_COMPONENT", obj("realm", REALM, "componentId", componentId), "Component", out);
        }
        capture("GET_REALM_KEYS", obj("realm", REALM), "Keys", out);
        capture("GET_REQUIRED_ACTIONS", obj("realm", REALM), "Required actions", out);
        capture("GET_REQUIRED_ACTION", obj("realm", REALM, "alias", "VERIFY_EMAIL"), "Required actions", out);
        capture("GET_REALM_CLIENT_POLICIES", obj("realm", REALM), "Realm policies", out);
        capture("GET_REALM_CLIENT_PROFILES", obj("realm", REALM), "Realm policies", out);
        capture("GET_CLIENT_REGISTRATION_PROVIDERS", obj("realm", REALM), "Realm policies", out);
        capture("GET_REALM_LOCALES", obj("realm", REALM), "Localization", out);
        capture("GET_USER_PROFILE_CONFIG", obj("realm", REALM), "User profile", out);
        capture("GET_ORGANIZATIONS", obj("realm", REALM), "Organizations", out);
        capture("SEARCH_DISCOURSE", obj("query", "admin rest api"), "Discourse", out);

        // --- mutating examples with cleanup ---
        capture("CREATE_USER", """
                {"realm":"%s","username":"docs-demo-user","firstName":"Docs","lastName":"Demo","email":"docs-demo@example.com","password":"DocsDemoPass1!"}
                """.formatted(REALM), "User", out);
        String demoUser = tool.executeKeycloakOperation(KeycloakOperation.GET_USER_BY_USERNAME,
                obj("realm", REALM, "username", "docs-demo-user"));
        ids.put("demoUserId", firstId(demoUser));
        capture("RESET_PASSWORD",
                obj("realm", REALM, "userId", ids.get("demoUserId"), "newPassword", "DocsDemoPass2!", "temporary", false),
                "User", out);
        if (ids.get("groupId") != null) {
            capture("ADD_USER_TO_GROUP",
                    obj("realm", REALM, "userId", ids.get("demoUserId"), "groupId", ids.get("groupId")),
                    "User", out);
            capture("REMOVE_USER_FROM_GROUP",
                    obj("realm", REALM, "userId", ids.get("demoUserId"), "groupId", ids.get("groupId")),
                    "User", out);
        }
        capture("ADD_ROLE_TO_USER",
                obj("realm", REALM, "userId", ids.get("demoUserId"), "roleName", "user"),
                "User", out);
        capture("REMOVE_ROLE_FROM_USER",
                obj("realm", REALM, "userId", ids.get("demoUserId"), "roleName", "user"),
                "User", out);
        capture("UPDATE_USER", """
                {"realm":"%s","userId":"%s","userRepresentation":{"firstName":"DocsUpdated","enabled":true}}
                """.formatted(REALM, ids.get("demoUserId")), "User", out);
        capture("CLEAR_USER_LOGIN_FAILURES",
                obj("realm", REALM, "userId", ids.get("demoUserId")), "Sessions", out);
        capture("LOGOUT_USER", obj("realm", REALM, "userId", ids.get("demoUserId")), "Sessions", out);
        capture("DELETE_USER", obj("realm", REALM, "username", "docs-demo-user"), "User", out);

        capture("CREATE_CLIENT", """
                {"realm":"%s","clientId":"docs-demo-client","redirectUris":"http://localhost:9999/*"}
                """.formatted(REALM), "Client", out);
        capture("GENERATE_CLIENT_SECRET", obj("realm", REALM, "clientId", "docs-demo-client"), "Client", out);
        capture("CREATE_CLIENT_ROLE",
                obj("realm", REALM, "clientId", "docs-demo-client", "roleName", "docs-role", "description", "Docs demo role"),
                "Client", out);
        capture("DELETE_CLIENT_ROLE",
                obj("realm", REALM, "clientId", "docs-demo-client", "roleName", "docs-role"),
                "Client", out);
        capture("DELETE_CLIENT", obj("realm", REALM, "clientId", "docs-demo-client"), "Client", out);

        capture("CREATE_CLIENT_SCOPE", """
                {"realm":"%s","clientScope":{"name":"docs-demo-scope","protocol":"openid-connect","description":"Docs demo"}}
                """.formatted(REALM), "Client scope", out);
        String scopes2 = tool.executeKeycloakOperation(KeycloakOperation.GET_CLIENT_SCOPES, obj("realm", REALM));
        ids.put("docsScopeId", findIdByName(scopes2, "docs-demo-scope"));
        if (ids.get("docsScopeId") != null) {
            capture("UPDATE_CLIENT_SCOPE", """
                    {"realm":"%s","clientScopeId":"%s","clientScope":{"name":"docs-demo-scope","protocol":"openid-connect","description":"Updated docs demo"}}
                    """.formatted(REALM, ids.get("docsScopeId")), "Client scope", out);
            capture("DELETE_CLIENT_SCOPE",
                    obj("realm", REALM, "clientScopeId", ids.get("docsScopeId")), "Client scope", out);
        }

        capture("CREATE_REALM_ROLE",
                obj("realm", REALM, "roleName", "docs-temp-role", "description", "Temporary docs role"),
                "Role", out);
        capture("UPDATE_REALM_ROLE", """
                {"realm":"%s","roleName":"docs-temp-role","roleRepresentation":{"name":"docs-temp-role","description":"Updated temp role"}}
                """.formatted(REALM), "Role", out);
        capture("ADD_COMPOSITE_TO_ROLE",
                obj("realm", REALM, "roleName", "docs-temp-role", "compositeRoleName", "user"),
                "Role", out);
        capture("REMOVE_COMPOSITE_FROM_ROLE",
                obj("realm", REALM, "roleName", "docs-temp-role", "compositeRoleName", "user"),
                "Role", out);
        capture("DELETE_REALM_ROLE", obj("realm", REALM, "roleName", "docs-temp-role"), "Role", out);

        capture("CREATE_GROUP", obj("realm", REALM, "groupName", "docs-parent-group"), "Group", out);
        String g2 = tool.executeKeycloakOperation(KeycloakOperation.GET_GROUPS, obj("realm", REALM));
        ids.put("parentGroupId", findIdByName(g2, "docs-parent-group"));
        if (ids.get("parentGroupId") != null) {
            capture("CREATE_SUBGROUP",
                    obj("realm", REALM, "parentGroupId", ids.get("parentGroupId"), "subGroupName", "docs-child-group"),
                    "Group", out);
            capture("UPDATE_GROUP", """
                    {"realm":"%s","groupId":"%s","groupRepresentation":{"name":"docs-parent-group","path":"/docs-parent-group"}}
                    """.formatted(REALM, ids.get("parentGroupId")), "Group", out);
            capture("ADD_ROLE_TO_GROUP",
                    obj("realm", REALM, "groupId", ids.get("parentGroupId"), "roleName", "user"),
                    "Group", out);
            capture("REMOVE_ROLE_FROM_GROUP",
                    obj("realm", REALM, "groupId", ids.get("parentGroupId"), "roleName", "user"),
                    "Group", out);
            capture("DELETE_GROUP", obj("realm", REALM, "groupId", ids.get("parentGroupId")), "Group", out);
        }

        capture("CREATE_REALM",
                obj("realmName", "docs-demo-realm", "displayName", "Docs Demo Realm", "enabled", true),
                "Realm", out);
        capture("SET_REALM_ENABLED", obj("realmName", "docs-demo-realm", "enabled", false), "Realm", out);
        capture("SET_REALM_ENABLED", obj("realmName", "docs-demo-realm", "enabled", true), "Realm", out);
        capture("DELETE_REALM", obj("realmName", "docs-demo-realm"), "Realm", out);

        capture("CREATE_AUTHENTICATION_FLOW",
                obj("realm", REALM, "authFlowNameId", "docs-browser-copy"),
                "Authentication", out);
        String flows2 = tool.executeKeycloakOperation(KeycloakOperation.GET_AUTHENTICATION_FLOWS, obj("realm", REALM));
        String docsFlowId = findIdByAlias(flows2, "docs-browser-copy");
        if (docsFlowId != null) {
            capture("DELETE_AUTHENTICATION_FLOW", obj("realm", REALM, "flowId", docsFlowId), "Authentication", out);
        }

        capture("PUSH_REALM_REVOCATION", obj("realm", REALM), "Realm policies", out);
        capture("CLEAR_ALL_LOGIN_FAILURES", obj("realm", REALM), "Sessions", out);

        capture("SAVE_LOCALIZATION_TEXT",
                obj("realm", REALM, "locale", "en", "key", "docsDemoKey", "text", "Docs demo value"),
                "Localization", out);
        capture("GET_LOCALIZATION_TEXTS", obj("realm", REALM, "locale", "en"), "Localization", out);
        capture("DELETE_LOCALIZATION_TEXT",
                obj("realm", REALM, "locale", "en", "key", "docsDemoKey"),
                "Localization", out);

        // Document remaining ops that were not executed live (with schema only)
        appendSchemaOnlySections(out);

        return out;
    }

    private void appendSchemaOnlySections(List<Section> out) {
        for (KeycloakOperation op : KeycloakOperation.values()) {
            boolean already = out.stream().anyMatch(s -> s.operation.equals(op.name()));
            if (already) {
                continue;
            }
            KeycloakCommand cmd = registry.getCommand(op);
            String desc = cmd != null ? cmd.getDescription() : ("Execute " + op.name());
            String[] required = cmd != null ? cmd.getRequiredParams() : new String[0];
            String exampleParams = schemaExample(required);
            out.add(new Section(categoryFor(op), op.name(), desc, required, exampleParams,
                    "_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/"
                            + "environment-specific). Required params and description are from the command implementation.",
                    false));
        }
    }

    private static String schemaExample(String[] required) {
        if (required == null || required.length == 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < required.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            String k = required[i];
            sb.append('"').append(k).append("\": ");
            sb.append(switch (k) {
                case "realm", "realmName" -> "\"quarkus\"";
                case "enabled", "temporary", "includeGlobal", "useRealmFallback" -> "true";
                case "redirectUris" -> "\"http://localhost:8080/*\"";
                case "actions" -> "[\"UPDATE_PASSWORD\"]";
                case "roles" -> "[]";
                case "texts" -> "{\"key\":\"value\"}";
                default -> k.toLowerCase().contains("representation")
                        || k.equals("clientScope") || k.equals("mapper") || k.equals("identityProvider")
                        || k.equals("component") || k.equals("organization") || k.equals("resource")
                        || k.equals("scope") || k.equals("policy") || k.equals("permission")
                        || k.equals("resourceServer") || k.equals("config") || k.equals("test")
                        || k.equals("policies") || k.equals("profiles") || k.equals("eventsConfig")
                        || k.equals("requiredAction") || k.equals("executionRepresentation")
                        ? "{}"
                        : "\"...\"";
            });
        }
        sb.append('}');
        return sb.toString();
    }

    private String safeExec(KeycloakOperation op, String params) {
        try {
            return tool.executeKeycloakOperation(op, params);
        } catch (Exception e) {
            return null;
        }
    }

    private void capture(String operation, String params, String category, List<Section> out) {
        KeycloakOperation op = KeycloakOperation.valueOf(operation);
        KeycloakCommand cmd = registry.getCommand(op);
        String desc = cmd != null ? cmd.getDescription() : operation;
        String[] required = cmd != null ? cmd.getRequiredParams() : new String[0];
        try {
            String result = tool.executeKeycloakOperation(op, params);
            captureRaw(operation, params, result, category, out);
        } catch (Exception e) {
            out.add(new Section(category, operation, desc, required, prettyJson(params),
                    "Error: " + e.getMessage(), false));
        }
    }

    private void captureRaw(String operation, String params, String result, String category, List<Section> out) {
        KeycloakOperation op = KeycloakOperation.valueOf(operation);
        KeycloakCommand cmd = registry.getCommand(op);
        String desc = cmd != null ? cmd.getDescription() : operation;
        String[] required = cmd != null ? cmd.getRequiredParams() : new String[0];
        out.add(new Section(category, operation, desc, required, prettyJson(params),
                truncate(prettyJson(result)), true));
    }

    private String renderMarkdown(List<Section> sections) {
        StringBuilder md = new StringBuilder();
        md.append("""
                # Operations reference

                This page documents the unified MCP tool `executeKeycloakOperation`.
                Each example below was generated against a live Keycloak **26** instance
                importing `deploy/quarkus-realm.json` (admin credentials via `kc.dev.user` /
                `kc.dev.password`) unless marked as schema-only.

                Regenerate after command changes (Keycloak must be listening on `127.0.0.1:8180`):

                ```bash
                # Terminal 1 — Keycloak 26 with quarkus realm
                export KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin
                ./bin/kc.sh start-dev --http-port=8180 --import-realm

                # Terminal 2 — from this repo
                mvn -Dtest=OperationsDocGeneratorTest -Dgenerate.operations.docs=true test
                ```

                ## Tool signature

                ```text
                executeKeycloakOperation(
                  operation: KeycloakOperation,   # e.g. GET_USERS
                  params: string                  # JSON object
                ) -> string                       # JSON or status message
                ```

                Generated: %s  
                Validated realm: `%s`  
                Operations registered: **%d**

                ## Categories

                """.formatted(Instant.now(), REALM, registry.getCommandCount()));

        Map<String, List<Section>> byCat = new LinkedHashMap<>();
        for (Section s : sections) {
            byCat.computeIfAbsent(s.category, k -> new ArrayList<>()).add(s);
        }
        for (String cat : byCat.keySet()) {
            md.append("- [").append(cat).append("](#")
                    .append(slug(cat)).append(") (").append(byCat.get(cat).size()).append(")\n");
        }
        md.append('\n');

        for (Map.Entry<String, List<Section>> e : byCat.entrySet()) {
            md.append("## ").append(e.getKey()).append("\n\n");
            for (Section s : e.getValue()) {
                md.append("### `").append(s.operation).append("`\n\n");
                md.append(s.description).append("\n\n");
                if (s.required.length > 0) {
                    md.append("**Required params:** ");
                    md.append(String.join(", ", java.util.Arrays.stream(s.required).map(p -> "`" + p + "`").toList()));
                    md.append("\n\n");
                } else {
                    md.append("**Required params:** _(none)_\n\n");
                }
                if (s.live) {
                    md.append("!!! success \"Validated against local Keycloak\"\n\n");
                } else {
                    md.append("!!! note \"Schema only\"\n\n");
                }
                md.append("**Request**\n\n```json\n");
                md.append(s.requestJson.trim()).append("\n```\n\n");
                md.append("**Response**\n\n```json\n");
                md.append(s.response.trim()).append("\n```\n\n");
            }
        }

        md.append("""
                ## Tips

                - Prefer `realm` for realm-scoped admin ops; use `realmName` for realm CRUD (`GET_REALM`, `CREATE_REALM`, …).
                - `clientId` is usually the public client id string (e.g. `backend-service`). Some role-mapping
                  ops expect the client's **internal UUID** from `GET_CLIENT`.
                - Nested `*Representation` bodies follow the Keycloak Admin REST API shapes.
                - Destructive ops (`DELETE_*`, `CLEAR_*`, `LOGOUT_ALL_USERS`) should be disabled in production
                  via `keycloak.mcp.commands.disabled`.
                """);
        return md.toString();
    }

    private String obj(Object... kv) {
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) {
                m.put(String.valueOf(kv[i]), kv[i + 1]);
            }
            return mapper.writeValueAsString(m);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String prettyJson(String raw) {
        try {
            JsonNode n = mapper.readTree(raw);
            return pretty.writeValueAsString(n);
        } catch (Exception e) {
            return raw;
        }
    }

    private String truncate(String s) {
        if (s.length() <= MAX_OUTPUT_CHARS) {
            return s;
        }
        return s.substring(0, MAX_OUTPUT_CHARS) + "\n… truncated …";
    }

    private String firstId(String json) {
        try {
            JsonNode n = mapper.readTree(json);
            if (n.isArray() && !n.isEmpty()) {
                JsonNode id = n.get(0).get("id");
                return id != null ? id.asText() : null;
            }
            if (n.isObject() && n.has("id")) {
                return n.get("id").asText();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String findIdByName(String json, String name) {
        try {
            JsonNode n = mapper.readTree(json);
            if (n.isArray()) {
                for (JsonNode item : n) {
                    if (name.equals(text(item, "name")) || name.equals(text(item, "username"))
                            || name.equals(text(item, "clientId"))) {
                        return text(item, "id");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String findIdByAlias(String json, String alias) {
        try {
            JsonNode n = mapper.readTree(json);
            if (n.isArray()) {
                for (JsonNode item : n) {
                    if (alias.equals(text(item, "alias"))) {
                        return text(item, "id");
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return v == null || v.isNull() ? null : v.asText();
    }

    private static String slug(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private static String categoryFor(KeycloakOperation op) {
        String n = op.name();
        if (n.contains("AUTHZ")) return "Authorization (UMA)";
        if (n.contains("ORGANIZATION")) return "Organizations";
        if (n.contains("LOCALIZATION") || n.contains("LOCALES")) return "Localization";
        if (n.contains("CLIENT_SCOPE") || n.contains("CLIENT_SCOPE")) return "Client scope";
        if (n.startsWith("GET_CLIENT") || n.contains("CLIENT_ROLE") || n.contains("CLIENT_SECRET")
                || n.contains("PROTOCOL_MAPPER") || n.equals("CREATE_CLIENT") || n.equals("UPDATE_CLIENT")
                || n.equals("DELETE_CLIENT") || n.equals("GENERATE_CLIENT_SECRET")
                || n.equals("GET_SERVICE_ACCOUNT_USER") || n.equals("ADD_PROTOCOL_MAPPER_TO_CLIENT"))
            return "Client";
        if (n.contains("GROUP")) return "Group";
        if (n.contains("USER") || n.equals("COUNT_USERS") || n.equals("RESET_PASSWORD")
                || n.equals("SEND_VERIFICATION_EMAIL")) return "User";
        if (n.contains("ROLE") || n.contains("COMPOSITE")) return "Role";
        if (n.contains("IDENTITY_PROVIDER") || n.contains("IDP")) return "Identity provider";
        if (n.contains("AUTHENTICATION") || n.contains("FLOW")) return "Authentication";
        if (n.contains("SESSION") || n.contains("CONSENT") || n.contains("LOGOUT") || n.contains("BRUTE")
                || n.contains("LOGIN_FAILURE") || n.contains("OFFLINE")) return "Sessions";
        if (n.contains("EVENT")) return "Events";
        if (n.contains("COMPONENT")) return "Component";
        if (n.contains("KEY")) return "Keys";
        if (n.contains("REQUIRED_ACTION") || n.equals("EXECUTE_ACTIONS_EMAIL")) return "Required actions";
        if (n.contains("CREDENTIAL")) return "Credentials";
        if (n.contains("PROFILE")) return "User profile";
        if (n.contains("DISCOURSE")) return "Discourse";
        if (n.contains("REALM") || n.contains("LDAP") || n.contains("STORAGE") || n.contains("REVOCATION")
                || n.contains("REGISTRATION") || n.contains("POLICIES") || n.contains("PROFILES"))
            return "Realm";
        return "Other";
    }

    private record Section(
            String category,
            String operation,
            String description,
            String[] required,
            String requestJson,
            String response,
            boolean live
    ) {
    }
}
