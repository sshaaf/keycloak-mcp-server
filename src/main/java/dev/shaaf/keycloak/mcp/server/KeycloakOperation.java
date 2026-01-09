package dev.shaaf.keycloak.mcp.server;

/**
 * Enum defining all available Keycloak operations.
 * Operations are grouped by category for organization.
 */
public enum KeycloakOperation {
    // User Operations
    GET_USERS,
    GET_USER_BY_USERNAME,
    CREATE_USER,
    DELETE_USER,
    UPDATE_USER,
    GET_USER_BY_ID,
    GET_USER_GROUPS,
    ADD_USER_TO_GROUP,
    REMOVE_USER_FROM_GROUP,
    GET_USER_ROLES,
    ADD_ROLE_TO_USER,
    REMOVE_ROLE_FROM_USER,
    RESET_PASSWORD,
    SEND_VERIFICATION_EMAIL,
    COUNT_USERS,

    // Realm Operations
    GET_REALMS,
    GET_REALM,
    CREATE_REALM,

    // Client Operations
    GET_CLIENTS,
    GET_CLIENT,
    CREATE_CLIENT,
    DELETE_CLIENT,
    GENERATE_CLIENT_SECRET,
    GET_CLIENT_ROLES,
    CREATE_CLIENT_ROLE,
    DELETE_CLIENT_ROLE,

    // Role Operations
    GET_REALM_ROLES,
    GET_REALM_ROLE,

    // Group Operations
    GET_GROUPS,
    GET_GROUP_MEMBERS,
    CREATE_GROUP,
    UPDATE_GROUP,
    DELETE_GROUP,
    CREATE_SUBGROUP,

    // Identity Provider Operations
    GET_IDENTITY_PROVIDERS,
    GET_IDENTITY_PROVIDER,
    GET_IDENTITY_PROVIDER_MAPPERS,

    // Authentication Operations
    GET_AUTHENTICATION_FLOWS,
    GET_AUTHENTICATION_FLOW,
    CREATE_AUTHENTICATION_FLOW,
    DELETE_AUTHENTICATION_FLOW,
    GET_FLOW_EXECUTIONS,
    UPDATE_FLOW_EXECUTION,

    // Discourse Operations
    SEARCH_DISCOURSE
}

