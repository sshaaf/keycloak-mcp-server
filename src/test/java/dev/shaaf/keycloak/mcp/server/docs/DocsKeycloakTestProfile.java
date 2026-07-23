package dev.shaaf.keycloak.mcp.server.docs;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Uses a locally running Keycloak (see docker run on :8180) instead of Dev Services.
 */
public class DocsKeycloakTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.keycloak.devservices.enabled", "false",
                "quarkus.keycloak.admin-client.server-url", "http://127.0.0.1:8180",
                "kc.dev.user", "admin",
                "kc.dev.password", "admin",
                "kc.dev.realm", "master",
                "quarkus.oidc.enabled", "false",
                "quarkus.http.auth.permission.mcp.policy", "permit"
        );
    }
}
