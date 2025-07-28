package dev.shaaf.keycloak.mcp.server.testcontainers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for Keycloak integration tests using Testcontainers.
 * This class provides a shared Keycloak container and client for tests.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractKeycloakTest {

    /**
     * The shared Keycloak container.
     */
    @Container
    protected static final KeycloakTestContainer keycloakContainer = new KeycloakTestContainer();

    /**
     * The Keycloak admin client.
     */
    protected Keycloak keycloakClient;

    /**
     * Sets up the Keycloak client before all tests.
     */
    @BeforeAll
    public void setupKeycloakClient() {
        // Start the container if it's not already running
        if (!keycloakContainer.isRunning()) {
            keycloakContainer.start();
        }

        // Set the keycloak.url system property to the URL of the Testcontainers instance
        String keycloakUrl = keycloakContainer.getKeycloakServerUrl();
        System.setProperty("keycloak.url", keycloakUrl);
        System.out.println("Keycloak URL: " + keycloakUrl);

        // Create a Keycloak client for the container
        keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm("master")  // The master realm is the default administrative realm
                .username(keycloakContainer.getAdminUsername())
                .password(keycloakContainer.getAdminPassword())
                .clientId("admin-cli")  // The admin-cli is the default client for the admin console
                .build();
    }

    /**
     * Closes the Keycloak client after all tests.
     */
    @AfterAll
    public void closeKeycloakClient() {
        if (keycloakClient != null) {
            keycloakClient.close();
        }
    }

    /**
     * Gets the Keycloak server URL.
     * 
     * @return The Keycloak server URL
     */
    protected String getKeycloakServerUrl() {
        return keycloakContainer.getKeycloakServerUrl();
    }
}