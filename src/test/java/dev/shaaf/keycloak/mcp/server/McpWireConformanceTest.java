package dev.shaaf.keycloak.mcp.server;

import io.github.senor14.mcptestkit.McpAssertions;
import io.github.senor14.mcptestkit.client.HttpMcpTestClient;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Wire-level MCP conformance for the streamable endpoint: raw JSON-RPC over HTTP, no MCP SDK
 * on the client side, so it checks the bytes a non-SDK client actually receives — including
 * the error paths (unknown method, unknown tool) nothing else in the suite sends.
 * <p>
 * Runs without Docker: tool execution is not exercised (that needs Keycloak), so Dev Services
 * stay off and the class runs in the same CI leg that excludes the testcontainers-tagged tests.
 * What it pins is this surface — initialize, the tool list shape, and the error paths — so a
 * {@code quarkus-mcp-server-http} upgrade that changes any of it fails the build.
 */
@QuarkusTest
@TestProfile(McpWireConformanceTest.WireOnlyProfile.class)
class McpWireConformanceTest {

    /**
     * The {@code %test} profile stays active, so OIDC is already off and {@code /mcp} already
     * permits anonymous access (application.properties). These overrides only undo what
     * application-test.properties adds for the Keycloak-backed tests: Dev Services, and the
     * {@code ${keycloak.url}}-based admin-client URL that only resolves when they run.
     */
    public static class WireOnlyProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.keycloak.devservices.enabled", "false",
                    "quarkus.keycloak.admin-client.server-url", "http://localhost:9");
        }
    }

    @TestHTTPResource("/mcp")
    URL mcp;

    @Test
    void streamableEndpointConformsAtTheWireLevel() {
        try (HttpMcpTestClient client = HttpMcpTestClient.connect(
                URI.create(mcp.toString()), Map.of(), Duration.ofSeconds(30))) {
            McpAssertions.assertThat(client)
                    .initializesSuccessfully()
                    .negotiatedProtocolVersionIsOneOf("2025-11-25")
                    .declaresToolsCapability()
                    .hasTools()
                    .toolNamesAreUnique()
                    .toolsHaveDescriptions()
                    .toolSchemasAreValid()
                    .toolOutputSchemasAreValid()
                    .unknownMethodYieldsMethodNotFound()
                    .unknownToolHandledGracefully();
        }
    }
}
