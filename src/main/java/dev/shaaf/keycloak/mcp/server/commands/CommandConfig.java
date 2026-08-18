package dev.shaaf.keycloak.mcp.server.commands;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.List;
import java.util.Optional;

/**
 * Configuration for command loading and filtering.
 * Allows enabling/disabling commands via application.properties.
 */
@ConfigMapping(prefix = "keycloak.mcp.commands")
public interface CommandConfig {

    /**
     * Explicitly enabled commands. If set, ONLY these commands are available.
     * Example: GET_USERS,GET_REALMS,CREATE_USER
     */
    Optional<List<String>> enabled();

    /**
     * Explicitly disabled commands. These are excluded even if discovered.
     * Example: DELETE_USER,DELETE_REALM
     */
    Optional<List<String>> disabled();

    /**
     * Enable all discovered commands by default.
     * Set to false to require explicit enablement via the 'enabled' list.
     */
    @WithDefault("true")
    boolean enableAllByDefault();

    /**
     * Log available commands on startup.
     */
    @WithDefault("true")
    boolean logOnStartup();
}

