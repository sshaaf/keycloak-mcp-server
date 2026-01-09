package dev.shaaf.keycloak.mcp.server;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Basic integration tests for Keycloak MCP Server.
 * 
 * These tests run with the 'test' profile which uses Keycloak TestContainers.
 * A Keycloak instance is automatically started with the quarkus-realm.json configuration.
 * 
 * Run with: mvn test
 */
@QuarkusTest
public class KeycloakMcpServerTest {

    @Test
    public void testHealthEndpoint() {
        given()
            .when().get("/q/health")
            .then()
            .statusCode(200)
            .body(containsString("UP"));
    }

    @Test
    public void testLivenessEndpoint() {
        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(200)
            .body(containsString("UP"));
    }

    @Test
    public void testReadinessEndpoint() {
        given()
            .when().get("/q/health/ready")
            .then()
            .statusCode(200)
            .body(containsString("UP"));
    }

    @Test
    public void testServerRunning() {
        // Verify server is running and responding
        // The /mcp/sse endpoint is a long-lived SSE connection, so we verify
        // server responsiveness via health check instead
        given()
            .when().get("/q/health/live")
            .then()
            .statusCode(200)
            .body(containsString("UP"));
    }
}
