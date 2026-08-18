package dev.shaaf.keycloak.mcp.server.commands;

import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Registry for all Keycloak commands.
 * Discovers commands via CDI and filters them based on configuration.
 */
@ApplicationScoped
@Startup
public class CommandRegistry {

    private final Map<KeycloakOperation, KeycloakCommand> commands = new EnumMap<>(KeycloakOperation.class);
    private final Set<KeycloakOperation> availableOperations = EnumSet.noneOf(KeycloakOperation.class);

    @Inject
    @RegisteredCommand
    Instance<KeycloakCommand> discoveredCommands;

    @Inject
    CommandConfig config;

    @PostConstruct
    void initialize() {
        // Collect all discovered commands
        Map<KeycloakOperation, KeycloakCommand> discovered = new EnumMap<>(KeycloakOperation.class);
        for (KeycloakCommand command : discoveredCommands) {
            discovered.put(command.getOperation(), command);
        }

        Log.infof("Discovered %d commands", discovered.size());

        // Apply configuration filters
        Set<String> disabled = config.disabled()
                .map(HashSet::new)
                .orElseGet(HashSet::new);
        Optional<List<String>> explicitlyEnabled = config.enabled();

        for (var entry : discovered.entrySet()) {
            KeycloakOperation op = entry.getKey();
            String opName = op.name();

            // Check if disabled
            if (disabled.contains(opName)) {
                Log.debugf("Command %s is disabled via configuration", opName);
                continue;
            }

            // Check if explicitly enabled (if explicit list is provided)
            if (explicitlyEnabled.isPresent()) {
                if (!explicitlyEnabled.get().contains(opName)) {
                    Log.debugf("Command %s not in enabled list, skipping", opName);
                    continue;
                }
            } else if (!config.enableAllByDefault()) {
                // If enableAllByDefault is false and no explicit list, skip
                Log.debugf("Command %s skipped (enableAllByDefault=false)", opName);
                continue;
            }

            // Register the command
            commands.put(op, entry.getValue());
            availableOperations.add(op);
        }

        // Log available commands
        if (config.logOnStartup()) {
            logAvailableCommands();
        }
    }

    private void logAvailableCommands() {
        Log.info("═══════════════════════════════════════════════════════════");
        Log.info("  Keycloak MCP Server - Available Commands");
        Log.info("═══════════════════════════════════════════════════════════");

        // Group by category
        Map<String, List<KeycloakOperation>> byCategory = availableOperations.stream()
                .collect(Collectors.groupingBy(this::getCategory));

        byCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    Log.infof("  %s:", entry.getKey());
                    entry.getValue().stream()
                            .sorted()
                            .forEach(op -> Log.infof("    - %s", op.name()));
                });

        Log.infof("  Total: %d commands enabled", commands.size());
        Log.info("═══════════════════════════════════════════════════════════");
    }

    private String getCategory(KeycloakOperation op) {
        String name = op.name();
        if (name.contains("USER_PROFILE") || name.contains("PROFILE_CONFIG")) {
            return "User profile";
        }
        if (name.contains("LOCALIZATION") || name.equals("GET_REALM_LOCALES")
                || name.equals("GET_LOCALIZATION_TEXTS") || name.equals("DELETE_LOCALIZATION_TEXTS")
                || name.equals("DELETE_LOCALIZATION_TEXT") || name.equals("SAVE_LOCALIZATION_TEXT")
                || name.equals("CREATE_OR_UPDATE_LOCALIZATION_TEXTS")) {
            return "Localization";
        }
        if (name.contains("ORGANIZATION")) {
            return "Organization";
        }
        if (name.contains("AUTHZ")) {
            return "Authorization (UMA)";
        }
        if (name.contains("REQUIRED_") || name.equals("EXECUTE_ACTIONS_EMAIL")) {
            return "Required actions";
        }
        if (name.contains("EVENT") && !name.contains("EVENTS_CONFIG")) {
            return "Events";
        }
        if (name.contains("COMPONENT") || name.equals("GET_SUB_COMPONENTS")) {
            return "Component";
        }
        if (name.equals("GET_REALM_KEYS")) {
            return "Keys";
        }
        if (name.contains("CREDENTIAL")) {
            return "Credentials";
        }
        if (name.contains("USER") && (name.contains("BRUTE") || name.contains("SESSION")
                || name.contains("CONSENT") || name.equals("LOGOUT_USER"))) {
            return "Sessions";
        }
        if (name.equals("PUSH_REALM_REVOCATION") || name.equals("LOGOUT_ALL_USERS")
                || name.equals("SYNC_USER_STORAGE") || name.equals("TEST_LDAP_CONNECTION")
                || (name.contains("CLIENT_POLIC") || name.contains("CLIENT_PROFIL")
                || name.equals("GET_CLIENT_REGISTRATION_PROVIDERS"))) {
            return "Realm";
        }
        if (name.equals("ADD_CLIENT_ROLE_TO_USER") || name.equals("REMOVE_CLIENT_ROLE_FROM_USER")
                || name.contains("GET_USER_") && name.contains("CLIENT")) {
            return "User";
        }
        if (name.equals("ADD_CLIENT_ROLE_TO_GROUP") || name.equals("REMOVE_CLIENT_ROLE_FROM_GROUP")
                || (name.contains("GET_GROUP_") && name.contains("CLIENT"))) {
            return "Group";
        }
        if (name.contains("CLIENT_SCOPE_SCOPE") || name.contains("MAPPED_REALM")
                || name.contains("MAPPED_CLIENT")) {
            return "Client scope";
        }
        if (name.contains("USER") || name.contains("PASSWORD") || name.contains("EMAIL")) {
            return "User";
        }
        if (name.contains("REALM") || name.equals("PUSH_REALM_REVOCATION")) {
            return "Realm";
        }
        if (name.contains("CLIENT") && !name.contains("IDENTITY") && !name.contains("IDP")
                && !name.contains("CLIENT_SCOPE")) {
            return "Client";
        }
        if (name.contains("ROLE")) {
            return "Role";
        }
        if (name.contains("GROUP") || name.contains("SUBGROUP")) {
            return "Group";
        }
        if (name.contains("IDENTITY") || name.contains("IDP")) {
            return "Identity Provider";
        }
        if (name.contains("AUTH") || name.contains("FLOW")) {
            return "Authentication";
        }
        if (name.contains("DISCOURSE")) {
            return "Discourse";
        }
        if (name.contains("SESSION") || name.contains("CONSENT")
                || name.contains("LOGOUT_") || name.contains("OFFLINE")
                || name.contains("BRUTE") || name.contains("LOGIN_FAILURE")) {
            return "Sessions";
        }
        return "Other";
    }

    /**
     * Get command for an operation.
     *
     * @param operation The operation to look up
     * @return Command or null if not available
     */
    public KeycloakCommand getCommand(KeycloakOperation operation) {
        return commands.get(operation);
    }

    /**
     * Check if an operation is available.
     *
     * @param operation The operation to check
     * @return true if the operation is enabled
     */
    public boolean isAvailable(KeycloakOperation operation) {
        return commands.containsKey(operation);
    }

    /**
     * Get all available operations.
     *
     * @return Unmodifiable set of available operations
     */
    public Set<KeycloakOperation> getAvailableOperations() {
        return Collections.unmodifiableSet(availableOperations);
    }

    /**
     * Get available operations as comma-separated string (for tool description).
     *
     * @return Comma-separated list of operation names
     */
    public String getAvailableOperationsString() {
        return availableOperations.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get the count of available commands.
     *
     * @return Number of enabled commands
     */
    public int getCommandCount() {
        return commands.size();
    }
}

