package dev.shaaf.keycloak.mcp.server.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Testcontainers implementation for Keycloak.
 * This class provides a reusable Keycloak container configuration for integration tests.
 */
public class KeycloakTestContainer extends GenericContainer<KeycloakTestContainer> {

    private static final DockerImageName KEYCLOAK_IMAGE = DockerImageName.parse("quay.io/keycloak/keycloak:latest");
    private static final int KEYCLOAK_PORT = 8080;
    private static final String KEYCLOAK_ADMIN = "admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "admin";
    private static final String QUARKUS_REALM_JSON = "deploy/quarkus-realm.json";
    private static final String CONTAINER_IMPORT_PATH = "/opt/keycloak/data/import";

    /**
     * Creates a new Keycloak container with default configuration.
     * Imports the quarkus-realm.json file for tests.
     */
    public KeycloakTestContainer() {
        super(KEYCLOAK_IMAGE);
        
        // Get the absolute path to the quarkus-realm.json file
        Path realmFilePath = Paths.get(QUARKUS_REALM_JSON).toAbsolutePath();
        
        withExposedPorts(KEYCLOAK_PORT)
            .withEnv("KEYCLOAK_ADMIN", KEYCLOAK_ADMIN)
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", KEYCLOAK_ADMIN_PASSWORD)
            // Copy the realm file into the container
            .withCopyFileToContainer(MountableFile.forHostPath(realmFilePath), CONTAINER_IMPORT_PATH + "/quarkus-realm.json")
            // Start Keycloak with the import-realm option
            .withCommand("start-dev", "--import-realm")
            .waitingFor(Wait.forHttp("/").forStatusCode(200));
    }

    /**
     * Gets the URL for the Keycloak server.
     * 
     * @return The URL for the Keycloak server
     */
    public String getKeycloakServerUrl() {
        return String.format("http://%s:%d", getHost(), getMappedPort(KEYCLOAK_PORT));
    }

    /**
     * Gets the admin username for the Keycloak server.
     * 
     * @return The admin username
     */
    public String getAdminUsername() {
        return KEYCLOAK_ADMIN;
    }

    /**
     * Gets the admin password for the Keycloak server.
     * 
     * @return The admin password
     */
    public String getAdminPassword() {
        return KEYCLOAK_ADMIN_PASSWORD;
    }
}