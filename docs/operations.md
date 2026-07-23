# Operations reference

This page documents the unified MCP tool `executeKeycloakOperation`.
Each example below was generated against a live Keycloak **26** instance
importing `deploy/quarkus-realm.json` unless marked as schema-only.

**96** operations validated live; remaining ops are documented with required params only
(feature-specific, nested IDs, or environment-dependent).

Regenerate (Keycloak on `127.0.0.1:8180` with admin/admin):

```bash
export KEYCLOAK_ADMIN=admin KEYCLOAK_ADMIN_PASSWORD=admin
./bin/kc.sh start-dev --http-port=8180 --import-realm   # Keycloak distro

mvn -Dtest=OperationsDocGeneratorTest -Dgenerate.operations.docs=true test
```

## Tool signature

```text
executeKeycloakOperation(
  operation: KeycloakOperation,   # e.g. GET_USERS
  params: string                  # JSON object
) -> string                       # JSON or status message
```

Generated: 2026-07-23T22:58:33.315076Z
Validated realm: `quarkus`
Operations registered: **178**

## Categories

- [Realm](#realm) (10)
- [User](#user) (22)
- [Sessions](#sessions) (9)
- [Credentials](#credentials) (1)
- [Client](#client) (17)
- [User client roles](#user-client-roles) (2)
- [Client scope](#client-scope) (19)
- [Scope mappings](#scope-mappings) (3)
- [Role](#role) (8)
- [Group](#group) (11)
- [Group client roles](#group-client-roles) (2)
- [Identity provider](#identity-provider) (7)
- [Authentication](#authentication) (6)
- [Events](#events) (4)
- [Component](#component) (6)
- [Keys](#keys) (1)
- [Required actions](#required-actions) (4)
- [Realm policies](#realm-policies) (4)
- [Localization](#localization) (6)
- [User profile](#user-profile) (2)
- [Organizations](#organizations) (8)
- [Discourse](#discourse) (1)
- [Authorization (UMA)](#authorization-uma) (26)

## Realm

### `GET_REALMS`

List all realms accessible to the user

**Required params:** _(none)_

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{ }
```

**Response**

```json
[ {
  "id" : "395848f7-9446-411e-97e3-3fda77f05ad0",
  "realm" : "master",
  "displayName" : "Keycloak",
  "displayNameHtml" : "<div class=\"kc-logo-text\"><span>Keycloak</span></div>",
  "notBefore" : 0,
  "defaultSignatureAlgorithm" : "RS256",
  "revokeRefreshToken" : false,
  "refreshTokenMaxReuse" : 0,
  "accessTokenLifespan" : 60,
  "accessTokenLifespanForImplicitFlow" : 900,
  "ssoSessionIdleTimeout" : 1800,
  "ssoSessionMaxLifespan" : 36000,
  "ssoSessionIdleTimeoutRememberMe" : 0,
  "ssoSessionMaxLifespanRememberMe" : 0,
  "offlineSessionIdleTimeout" : 2592000,
  "offlineSessionMaxLifespanEnabled" : false,
  "offlineSessionMaxLifespan" : 5184000,
  "clientSessionIdleTimeout" : 0,
  "clientSessionMaxLifespan" : 0,
  "clientOfflineSessionIdleTimeout" : 0,
  "clientOfflineSessionMaxLifespan" : 0,
  "accessCodeLifespan" : 60,
  "accessCodeLifespanUserAction" : 300,
  "accessCodeLifespanLogin" : 1800,
  "actionTokenGeneratedByAdminLifespan" : 43200,
  "actionTokenGeneratedByUserLifespan" : 300,
  "oauth2DeviceCodeLifespan" : 600,
  "oauth2DevicePollingInterval" : 5,
  "enabled" : true,
  "sslRequired" : "external",
  "passwordCredentialGrantAllowed" : null,
  "registrationAllowed" : false,
  "registrationEmailAsUsername" : false,
  "rememberMe" : false,
  "verifyEmail" : false,
  "loginWithEmailAllowed" : true,
  "duplicateEmailsAllowed" : false,
  "resetPasswordAllowed" : false,
  "editUsernameAllowed" : false,
  "bruteForceProtected" : false,
  "permanentLockout" : false,
  "maxTemporaryLockouts" : 0,
  "bruteForceStrategy" : null,
  "maxFailureWaitSeconds" : 900,
  "minimumQuickLoginWaitSeconds" : 60,
  "waitIncrementSeconds" : 60,
  "quickLoginCheckMilliSeconds" : 1000,
  "maxDeltaTimeSeconds" : 43200,
  "failureFactor" : 30,
  "maxSecondaryAuthFailures" : null,

… truncated …
```

### `GET_REALM`

Get details of a specific realm

**Required params:** `realmName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "quarkus"
}
```

**Response**

```json
{
  "id" : "quarkus",
  "realm" : "quarkus",
  "displayName" : null,
  "displayNameHtml" : null,
  "notBefore" : 0,
  "defaultSignatureAlgorithm" : "RS256",
  "revokeRefreshToken" : false,
  "refreshTokenMaxReuse" : 0,
  "accessTokenLifespan" : 300,
  "accessTokenLifespanForImplicitFlow" : 900,
  "ssoSessionIdleTimeout" : 1800,
  "ssoSessionMaxLifespan" : 36000,
  "ssoSessionIdleTimeoutRememberMe" : 0,
  "ssoSessionMaxLifespanRememberMe" : 0,
  "offlineSessionIdleTimeout" : 2592000,
  "offlineSessionMaxLifespanEnabled" : false,
  "offlineSessionMaxLifespan" : 5184000,
  "clientSessionIdleTimeout" : 0,
  "clientSessionMaxLifespan" : 0,
  "clientOfflineSessionIdleTimeout" : 0,
  "clientOfflineSessionMaxLifespan" : 0,
  "accessCodeLifespan" : 60,
  "accessCodeLifespanUserAction" : 300,
  "accessCodeLifespanLogin" : 1800,
  "actionTokenGeneratedByAdminLifespan" : 43200,
  "actionTokenGeneratedByUserLifespan" : 300,
  "oauth2DeviceCodeLifespan" : 600,
  "oauth2DevicePollingInterval" : 5,
  "enabled" : true,
  "sslRequired" : "external",
  "passwordCredentialGrantAllowed" : null,
  "registrationAllowed" : false,
  "registrationEmailAsUsername" : false,
  "rememberMe" : false,
  "verifyEmail" : false,
  "loginWithEmailAllowed" : true,
  "duplicateEmailsAllowed" : false,
  "resetPasswordAllowed" : false,
  "editUsernameAllowed" : false,
  "bruteForceProtected" : false,
  "permanentLockout" : false,
  "maxTemporaryLockouts" : 0,
  "bruteForceStrategy" : null,
  "maxFailureWaitSeconds" : 900,
  "minimumQuickLoginWaitSeconds" : 60,
  "waitIncrementSeconds" : 60,
  "quickLoginCheckMilliSeconds" : 1000,
  "maxDeltaTimeSeconds" : 43200,
  "failureFactor" : 30,
  "maxSecondaryAuthFailures" : null,
  "privateKey" : null,
  "publicKey" : null,
  "certificate" : null,
  "codeSecret" : nu
… truncated …
```

### `GET_REALM_EVENTS_CONFIG`

Get realm events configuration

**Required params:** `realmName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "quarkus"
}
```

**Response**

```json
{
  "eventsEnabled" : false,
  "eventsExpiration" : null,
  "eventsListeners" : [ "jboss-logging" ],
  "enabledEventTypes" : [ "LOGIN", "LOGIN_ERROR", "REGISTER", "REGISTER_ERROR", "LOGOUT", "LOGOUT_ERROR", "CODE_TO_TOKEN", "CODE_TO_TOKEN_ERROR", "CLIENT_LOGIN", "CLIENT_LOGIN_ERROR", "FEDERATED_IDENTITY_LINK", "FEDERATED_IDENTITY_LINK_ERROR", "REMOVE_FEDERATED_IDENTITY", "REMOVE_FEDERATED_IDENTITY_ERROR", "UPDATE_EMAIL", "UPDATE_EMAIL_ERROR", "UPDATE_PROFILE", "UPDATE_PROFILE_ERROR", "UPDATE_PASSWORD", "UPDATE_PASSWORD_ERROR", "UPDATE_TOTP", "UPDATE_TOTP_ERROR", "VERIFY_EMAIL", "VERIFY_EMAIL_ERROR", "VERIFY_PROFILE", "VERIFY_PROFILE_ERROR", "REMOVE_TOTP", "REMOVE_TOTP_ERROR", "GRANT_CONSENT", "GRANT_CONSENT_ERROR", "UPDATE_CONSENT", "UPDATE_CONSENT_ERROR", "REVOKE_GRANT", "REVOKE_GRANT_ERROR", "SEND_VERIFY_EMAIL", "SEND_VERIFY_EMAIL_ERROR", "SEND_RESET_PASSWORD", "SEND_RESET_PASSWORD_ERROR", "SEND_IDENTITY_PROVIDER_LINK", "SEND_IDENTITY_PROVIDER_LINK_ERROR", "RESET_PASSWORD", "RESET_PASSWORD_ERROR", "RESTART_AUTHENTICATION", "RESTART_AUTHENTICATION_ERROR", "IDENTITY_PROVIDER_LINK_ACCOUNT", "IDENTITY_PROVIDER_LINK_ACCOUNT_ERROR", "IDENTITY_PROVIDER_FIRST_LOGIN", "IDENTITY_PROVIDER_FIRST_LOGIN_ERROR", "IDENTITY_PROVIDER_POST_LOGIN", "IDENTITY_PROVIDER_POST_LOGIN_ERROR", "IMPERSONATE", "IMPERSONATE_ERROR", "CUSTOM_REQUIRED_ACTION", "CUSTOM_REQUIRED_ACTION_ERROR", "EXECUTE_ACTIONS", "EXECUTE_ACTIONS_ERROR", "EXECUTE_ACTION_TOKEN", "EXECUTE_ACTION_TOKEN_ERROR", "CLIENT_REGISTER", "CLIENT_REGISTER_ERROR", "CLIENT_UPDATE", "CLIENT_UPDATE_ERROR", "CLIENT_DELETE", "CLIENT_DELETE_ERROR", "CLIENT_INITIATED_ACCOUNT_LINKING", "CLIENT_INITIATED_ACCOUNT_LINKING_ERROR", "TOKEN_EXCHANGE", "TOKEN_EXCHANGE_ERROR", "OAUTH2_DEVICE_AUTH", "OAUTH2_DEVICE_AUTH_ERROR", "OAUTH2_DEVICE_VERIFY_USE
… truncated …
```

### `CREATE_REALM`

Create a new realm

**Required params:** `realmName`, `displayName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "docs-demo-realm",
  "displayName" : "Docs Demo Realm",
  "enabled" : true
}
```

**Response**

```json
Successfully created realm: docs-demo-realm
```

### `SET_REALM_ENABLED`

Enable or disable a realm

**Required params:** `realmName`, `enabled`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "docs-demo-realm",
  "enabled" : false
}
```

**Response**

```json
Successfully disabled realm: docs-demo-realm
```

### `SET_REALM_ENABLED`

Enable or disable a realm

**Required params:** `realmName`, `enabled`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "docs-demo-realm",
  "enabled" : true
}
```

**Response**

```json
Successfully enabled realm: docs-demo-realm
```

### `DELETE_REALM`

Delete a realm

**Required params:** `realmName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realmName" : "docs-demo-realm"
}
```

**Response**

```json
Successfully deleted realm: docs-demo-realm
```

### `UPDATE_REALM`

Update an existing realm

**Required params:** `realmName`, `realmRepresentation`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realmName": "quarkus", "realmRepresentation": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `TEST_LDAP_CONNECTION`

Test an LDAP or user federation connection (Keycloak TestLdapConnectionRepresentation JSON in test)

**Required params:** `realm`, `test`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "test": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_REALM_CLIENT_POLICIES`

Update realm client policies JSON (ClientPoliciesRepresentation in policies)

**Required params:** `realm`, `policies`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "policies": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## User

### `GET_USERS`

List all users in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "af134cab-f41c-4675-b141-205f975db679",
  "username" : "admin",
  "firstName" : null,
  "lastName" : null,
  "email" : null,
  "emailVerified" : false,
  "attributes" : null,
  "userProfileMetadata" : null,
  "enabled" : true,
  "self" : null,
  "origin" : null,
  "createdTimestamp" : null,
  "totp" : false,
  "federationLink" : null,
  "serviceAccountClientId" : null,
  "credentials" : null,
  "disableableCredentialTypes" : [ ],
  "requiredActions" : [ ],
  "federatedIdentities" : null,
  "realmRoles" : null,
  "clientRoles" : null,
  "clientConsents" : null,
  "notBefore" : 0,
  "applicationRoles" : null,
  "socialLinks" : null,
  "groups" : null,
  "access" : {
    "manageGroupMembership" : true,
    "view" : true,
    "mapRoles" : true,
    "impersonate" : true,
    "manage" : true
  }
}, {
  "id" : "eb4123a3-b722-4798-9af5-8957f823657a",
  "username" : "alice",
  "firstName" : null,
  "lastName" : null,
  "email" : null,
  "emailVerified" : false,
  "attributes" : null,
  "userProfileMetadata" : null,
  "enabled" : true,
  "self" : null,
  "origin" : null,
  "createdTimestamp" : null,
  "totp" : false,
  "federationLink" : null,
  "serviceAccountClientId" : null,
  "credentials" : null,
  "disableableCredentialTypes" : [ ],
  "requiredActions" : [ ],
  "federatedIdentities" : null,
  "realmRoles" : null,
  "clientRoles" : null,
  "clientConsents" : null,
  "notBefore" : 0,
  "applicationRoles" : null,
  "socialLinks" : null,
  "groups" : null,
  "access" : {
    "manageGroupMembership" : true,
    "view" : true,
    "mapRoles" : true,
    "impersonate" : true,
    "manage" : true
  }
}, {
  "id" : "1eed6a8e-a853-4597-b4c6-c4c2533546a0",
  "username" : "jdoe",
  "firstName" : null,
  "lastName" : null,
  "email" : null,
  "emailVerified" : false,
  "att
… truncated …
```

### `GET_USER_BY_USERNAME`

Get a user by their username

**Required params:** `realm`, `username`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "username" : "admin"
}
```

**Response**

```json
{
  "id" : "af134cab-f41c-4675-b141-205f975db679",
  "username" : "admin",
  "firstName" : null,
  "lastName" : null,
  "email" : null,
  "emailVerified" : false,
  "attributes" : null,
  "userProfileMetadata" : null,
  "enabled" : true,
  "self" : null,
  "origin" : null,
  "createdTimestamp" : null,
  "totp" : false,
  "federationLink" : null,
  "serviceAccountClientId" : null,
  "credentials" : null,
  "disableableCredentialTypes" : [ ],
  "requiredActions" : [ ],
  "federatedIdentities" : null,
  "realmRoles" : null,
  "clientRoles" : null,
  "clientConsents" : null,
  "notBefore" : 0,
  "applicationRoles" : null,
  "socialLinks" : null,
  "groups" : null,
  "access" : {
    "manageGroupMembership" : true,
    "view" : true,
    "mapRoles" : true,
    "impersonate" : true,
    "manage" : true
  }
}
```

### `GET_USER_BY_ID`

Get a user by their ID

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
{
  "id" : "af134cab-f41c-4675-b141-205f975db679",
  "username" : "admin",
  "firstName" : null,
  "lastName" : null,
  "email" : null,
  "emailVerified" : false,
  "attributes" : null,
  "userProfileMetadata" : null,
  "enabled" : true,
  "self" : null,
  "origin" : null,
  "createdTimestamp" : null,
  "totp" : false,
  "federationLink" : null,
  "serviceAccountClientId" : null,
  "credentials" : null,
  "disableableCredentialTypes" : [ ],
  "requiredActions" : [ ],
  "federatedIdentities" : null,
  "realmRoles" : null,
  "clientRoles" : null,
  "clientConsents" : null,
  "notBefore" : 0,
  "applicationRoles" : null,
  "socialLinks" : null,
  "groups" : null,
  "access" : {
    "manageGroupMembership" : true,
    "view" : true,
    "mapRoles" : true,
    "impersonate" : true,
    "manage" : true
  }
}
```

### `COUNT_USERS`

Count users in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
3
```

### `GET_USER_GROUPS`

Get groups a user belongs to

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
[ ]
```

### `GET_USER_ROLES`

Get roles assigned to a user

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
[ {
  "id" : "3ce83241-464b-4ca0-8f0f-17002a797aab",
  "name" : "admin",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "d3246456-8f5d-4722-8364-a46a8d25dc7c",
  "name" : "user",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
} ]
```

### `CREATE_USER`

Create a new user in a realm

**Required params:** `realm`, `username`, `email`, `password`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "username" : "docs-demo-user",
  "firstName" : "Docs",
  "lastName" : "Demo",
  "email" : "docs-demo@example.com",
  "password" : "DocsDemoPass1!"
}
```

**Response**

```json
Successfully created user: docs-demo-user
```

### `RESET_PASSWORD`

Reset a user's password

**Required params:** `realm`, `userId`, `newPassword`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "newPassword" : "DocsDemoPass2!",
  "temporary" : false
}
```

**Response**

```json
Successfully reset password for user: e2b390a2-c08c-4458-a8f6-56bd92bebafd
```

### `ADD_USER_TO_GROUP`

Add a user to a group

**Required params:** `realm`, `userId`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
Successfully added user to group: e2b390a2-c08c-4458-a8f6-56bd92bebafd -> e61b59d7-4092-4b67-8cc4-6b91491ce829
```

### `REMOVE_USER_FROM_GROUP`

Remove a user from a group

**Required params:** `realm`, `userId`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
Successfully removed user from group: e2b390a2-c08c-4458-a8f6-56bd92bebafd -> e61b59d7-4092-4b67-8cc4-6b91491ce829
```

### `ADD_ROLE_TO_USER`

Add a realm role to a user

**Required params:** `realm`, `userId`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "roleName" : "user"
}
```

**Response**

```json
Successfully added role to user: e2b390a2-c08c-4458-a8f6-56bd92bebafd -> user
```

### `REMOVE_ROLE_FROM_USER`

Remove a realm role from a user

**Required params:** `realm`, `userId`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "roleName" : "user"
}
```

**Response**

```json
Successfully removed role from user: e2b390a2-c08c-4458-a8f6-56bd92bebafd -> user
```

### `UPDATE_USER`

Update an existing user

**Required params:** `realm`, `userId`, `userRepresentation`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd",
  "userRepresentation" : {
    "firstName" : "DocsUpdated",
    "enabled" : true
  }
}
```

**Response**

```json
Successfully updated user: e2b390a2-c08c-4458-a8f6-56bd92bebafd
```

### `DELETE_USER`

Delete a user from a realm

**Required params:** `realm`, `username`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "username" : "docs-demo-user"
}
```

**Response**

```json
successfully deleted: e2b390a2-c08c-4458-a8f6-56bd92bebafd
```

### `SEND_VERIFICATION_EMAIL`

Send email verification to a user

**Required params:** `realm`, `userId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REVOKE_USER_CONSENT`

Revoke user consent for a client

**Required params:** `realm`, `userId`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CLEAR_USER_EVENTS`

Clear the user event store for the realm

**Required params:** `realm`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus"}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_USER_CREDENTIAL`

Delete a user credential by id

**Required params:** `realm`, `userId`, `credentialId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "credentialId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `MOVE_USER_CREDENTIAL`

Change credential order (0-based index; uses move-to-first/after on the server)

**Required params:** `realm`, `userId`, `credentialId`, `newPosition`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "credentialId": "...", "newPosition": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `SYNC_USER_STORAGE`

Trigger user storage / LDAP sync (e.g. action=triggerFullSync; see Keycloak admin API)

**Required params:** `realm`, `userStorageId`, `action`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userStorageId": "...", "action": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `LOGOUT_ALL_USERS`

Logout all users in the realm (stateful clients receive logout)

**Required params:** `realm`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus"}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_USER_PROFILE_CONFIG`

Update declarative user profile (UPConfig JSON in config)

**Required params:** `realm`, `config`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "config": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Sessions

### `GET_USER_SESSIONS`

Get user sessions

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
[ ]
```

### `GET_USER_CONSENTS`

Get user consents

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
[ ]
```

### `GET_USER_BRUTE_FORCE_STATUS`

Get brute-force / lockout status for a user (attack detection)

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
{
  "failedLoginNotBefore" : 0,
  "numFailures" : 0,
  "numTemporaryLockouts" : 0,
  "disabled" : false,
  "lastIPFailure" : "n/a",
  "lastFailure" : 0
}
```

### `GET_CLIENT_USER_SESSIONS`

Get user sessions for a client

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
[ ]
```

### `GET_CLIENT_OFFLINE_SESSIONS`

Get offline sessions for a client

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
[ ]
```

### `CLEAR_USER_LOGIN_FAILURES`

Clear login failures for a user

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd"
}
```

**Response**

```json
Successfully cleared login failures for user: e2b390a2-c08c-4458-a8f6-56bd92bebafd
```

### `LOGOUT_USER`

Logout a user from all sessions

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "e2b390a2-c08c-4458-a8f6-56bd92bebafd"
}
```

**Response**

```json
Successfully logged out user: e2b390a2-c08c-4458-a8f6-56bd92bebafd
```

### `CLEAR_ALL_LOGIN_FAILURES`

Clear all login failures in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
Successfully cleared all login failures for realm: quarkus
```

### `GET_OFFLINE_SESSIONS`

Get offline sessions for a user

**Required params:** `realm`, `userId`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Credentials

### `GET_USER_CREDENTIALS`

List credentials (password, OTP, webauthn, etc.) for a user

**Required params:** `realm`, `userId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679"
}
```

**Response**

```json
[ {
  "id" : "d65ccdb1-ae65-4ab8-a424-09819144ae64",
  "type" : "password",
  "userLabel" : null,
  "createdDate" : 1554245879354,
  "secretData" : null,
  "credentialData" : "{\"hashIterations\":27500,\"algorithm\":\"pbkdf2-sha256\",\"additionalParameters\":{}}",
  "priority" : null,
  "value" : null,
  "temporary" : null,
  "device" : null,
  "hashedSaltedValue" : null,
  "salt" : null,
  "hashIterations" : null,
  "counter" : null,
  "algorithm" : null,
  "digits" : null,
  "period" : null,
  "config" : null,
  "federationLink" : null
} ]
```

## Client

### `GET_CLIENTS`

List all clients in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "35b5a50f-a32a-4bd1-b4b3-50f0ade135c7",
  "clientId" : "account",
  "name" : "${client_account}",
  "description" : null,
  "type" : null,
  "rootUrl" : "${authBaseUrl}",
  "adminUrl" : null,
  "baseUrl" : "/realms/quarkus/account/",
  "surrogateAuthRequired" : false,
  "enabled" : true,
  "alwaysDisplayInConsole" : false,
  "clientAuthenticatorType" : "client-secret",
  "secret" : "0136c3ef-0dfd-4b13-a6d0-2c8b6358edec",
  "registrationAccessToken" : null,
  "defaultRoles" : null,
  "redirectUris" : [ "/realms/quarkus/account/*" ],
  "webOrigins" : [ ],
  "notBefore" : 0,
  "bearerOnly" : false,
  "consentRequired" : false,
  "standardFlowEnabled" : true,
  "implicitFlowEnabled" : false,
  "directAccessGrantsEnabled" : false,
  "serviceAccountsEnabled" : false,
  "authorizationServicesEnabled" : null,
  "directGrantsOnly" : null,
  "publicClient" : false,
  "frontchannelLogout" : false,
  "protocol" : "openid-connect",
  "attributes" : {
    "realm_client" : "false",
    "post.logout.redirect.uris" : "+"
  },
  "authenticationFlowBindingOverrides" : { },
  "fullScopeAllowed" : false,
  "nodeReRegistrationTimeout" : 0,
  "registeredNodes" : null,
  "protocolMappers" : null,
  "clientTemplate" : null,
  "useTemplateConfig" : null,
  "useTemplateScope" : null,
  "useTemplateMappers" : null,
  "defaultClientScopes" : [ "web-origins", "acr", "roles", "profile", "basic", "email" ],
  "optionalClientScopes" : [ "address", "phone", "offline_access", "microprofile-jwt" ],
  "authorizationSettings" : null,
  "access" : {
    "view" : true,
    "configure" : true,
    "manage" : true
  },
  "origin" : null
}, {
  "id" : "690942b4-75fe-492f-8ee8-58ce11f8042f",
  "clientId" : "account-console",
  "name" : "${client_account-console}",
  "description" : null,
  "type" : n
… truncated …
```

### `GET_CLIENT`

Get a specific client by client ID

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
{
  "id" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a",
  "clientId" : "backend-service",
  "name" : null,
  "description" : null,
  "type" : null,
  "rootUrl" : null,
  "adminUrl" : null,
  "baseUrl" : null,
  "surrogateAuthRequired" : false,
  "enabled" : true,
  "alwaysDisplayInConsole" : false,
  "clientAuthenticatorType" : "client-secret",
  "secret" : "secret",
  "registrationAccessToken" : null,
  "defaultRoles" : null,
  "redirectUris" : [ "*" ],
  "webOrigins" : [ ],
  "notBefore" : 0,
  "bearerOnly" : false,
  "consentRequired" : false,
  "standardFlowEnabled" : true,
  "implicitFlowEnabled" : false,
  "directAccessGrantsEnabled" : true,
  "serviceAccountsEnabled" : true,
  "authorizationServicesEnabled" : null,
  "directGrantsOnly" : null,
  "publicClient" : false,
  "frontchannelLogout" : false,
  "protocol" : "openid-connect",
  "attributes" : {
    "realm_client" : "false",
    "post.logout.redirect.uris" : "+"
  },
  "authenticationFlowBindingOverrides" : { },
  "fullScopeAllowed" : true,
  "nodeReRegistrationTimeout" : -1,
  "registeredNodes" : null,
  "protocolMappers" : [ {
    "id" : "1390addb-ba10-4455-a1ea-8455c3770cf1",
    "name" : "Client ID",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usersessionmodel-note-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : {
      "user.session.note" : "clientId",
      "id.token.claim" : "true",
      "access.token.claim" : "true",
      "claim.name" : "clientId",
      "jsonType.label" : "String",
      "userinfo.token.claim" : "true"
    }
  }, {
    "id" : "cdafda09-f6d9-41e3-87ef-6789e861689a",
    "name" : "Client Host",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usersessionmodel-note-mapper",
    "consentRequired" : false,
    "consentText
… truncated …
```

### `GET_CLIENT_SECRET`

Get client secret

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
Error getting client secret: backend-service - Received: 'HTTP 404 Not Found' when invoking REST Client method: 'org.keycloak.admin.client.resource.ClientResource#getSecret'
```

### `GET_CLIENT_ROLES`

Get roles defined for a client

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
[ {
  "id" : "5b9947c6-eb74-4de6-8623-0285720993f3",
  "name" : "uma_protection",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : true,
  "containerId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a",
  "attributes" : null
} ]
```

### `GET_CLIENT_PROTOCOL_MAPPERS`

Get protocol mappers for a client

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
[ ]
```

### `GET_SERVICE_ACCOUNT_USER`

Get service account user for a client

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "backend-service"
}
```

**Response**

```json
null
```

### `CREATE_CLIENT`

Create a new client in a realm

**Required params:** `realm`, `clientId`, `redirectUris`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "docs-demo-client",
  "redirectUris" : "http://localhost:9999/*"
}
```

**Response**

```json
Successfully created client: docs-demo-client
```

### `GENERATE_CLIENT_SECRET`

Generate a new client secret

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "docs-demo-client"
}
```

**Response**

```json
Error generating new client secret: docs-demo-client - Received: 'HTTP 404 Not Found' when invoking REST Client method: 'org.keycloak.admin.client.resource.ClientResource#generateNewSecret'
```

### `CREATE_CLIENT_ROLE`

Create a new role for a client

**Required params:** `realm`, `clientId`, `roleName`, `description`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "docs-demo-client",
  "roleName" : "docs-role",
  "description" : "Docs demo role"
}
```

**Response**

```json
Successfully created client role: docs-role
```

### `DELETE_CLIENT_ROLE`

Delete a role from a client

**Required params:** `realm`, `clientId`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "docs-demo-client",
  "roleName" : "docs-role"
}
```

**Response**

```json
Successfully deleted client role: docs-role
```

### `DELETE_CLIENT`

Delete a client from a realm

**Required params:** `realm`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientId" : "docs-demo-client"
}
```

**Response**

```json
Client not found: docs-demo-client
```

### `UPDATE_CLIENT`

Update an existing client

**Required params:** `realm`, `clientId`, `clientRepresentation`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "clientRepresentation": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_PROTOCOL_MAPPER_TO_CLIENT`

Add protocol mapper to a client

**Required params:** `realm`, `clientId`, `mapper`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "mapper": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_CLIENT_ROLE_TO_USER`

Add a client role to a user (adds a role of the target client, not a realm role)

**Required params:** `realm`, `userId`, `clientId`, `roleName`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "clientId": "...", "roleName": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_CLIENT_ROLE_FROM_USER`

Remove a client role from a user

**Required params:** `realm`, `userId`, `clientId`, `roleName`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "clientId": "...", "roleName": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_CLIENT_ROLE_TO_GROUP`

Add a client role to a group

**Required params:** `realm`, `groupId`, `clientId`, `roleName`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "groupId": "...", "clientId": "...", "roleName": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_CLIENT_ROLE_FROM_GROUP`

Remove a client role from a group

**Required params:** `realm`, `groupId`, `clientId`, `roleName`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "groupId": "...", "clientId": "...", "roleName": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## User client roles

### `GET_USER_CLIENT_ROLES`

List client roles assigned to a user (clientId = internal id from GET_CLIENT)

**Required params:** `realm`, `userId`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679",
  "clientId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a"
}
```

**Response**

```json
[ ]
```

### `GET_USER_AVAILABLE_CLIENT_ROLES`

List client roles the user can still be assigned (not yet assigned at client scope)

**Required params:** `realm`, `userId`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "userId" : "af134cab-f41c-4675-b141-205f975db679",
  "clientId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a"
}
```

**Response**

```json
[ {
  "id" : "5b9947c6-eb74-4de6-8623-0285720993f3",
  "name" : "uma_protection",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : true,
  "containerId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a",
  "attributes" : null
} ]
```

## Client scope

### `GET_CLIENT_SCOPES`

Get all client scopes in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "83e275f7-b171-45fa-99c7-7c04f91fbe41",
  "name" : "roles",
  "description" : "OpenID Connect scope for add user roles to the access token",
  "protocol" : "openid-connect",
  "attributes" : {
    "include.in.token.scope" : "false",
    "consent.screen.text" : "${rolesScopeConsentText}",
    "display.on.consent.screen" : "true"
  },
  "protocolMappers" : [ {
    "id" : "9eb470cc-8157-46f2-8233-8cae169c6591",
    "name" : "realm roles",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usermodel-realm-role-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : {
      "user.attribute" : "foo",
      "access.token.claim" : "true",
      "claim.name" : "realm_access.roles",
      "jsonType.label" : "String",
      "multivalued" : "true"
    }
  }, {
    "id" : "eebdefd0-c446-4bf3-b945-08db42f0ea92",
    "name" : "audience resolve",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-audience-resolve-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : { }
  }, {
    "id" : "37c62d93-c670-487c-8c3a-a6329a9924b0",
    "name" : "client roles",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usermodel-client-role-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : {
      "user.attribute" : "foo",
      "access.token.claim" : "true",
      "claim.name" : "resource_access.${client_id}.roles",
      "jsonType.label" : "String",
      "multivalued" : "true"
    }
  } ]
}, {
  "id" : "ac32b7a2-6818-4f8c-b3b8-21872eebce0a",
  "name" : "basic",
  "description" : "OpenID Connect scope for add all basic claims to the token",
  "protocol" : "openid-connect",
  "attributes" : {
    "include.in.token.scope" : "false",
    "display.on.consent.screen" : 
… truncated …
```

### `GET_CLIENT_SCOPE`

Get a specific client scope

**Required params:** `realm`, `clientScopeId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "83e275f7-b171-45fa-99c7-7c04f91fbe41"
}
```

**Response**

```json
{
  "id" : "83e275f7-b171-45fa-99c7-7c04f91fbe41",
  "name" : "roles",
  "description" : "OpenID Connect scope for add user roles to the access token",
  "protocol" : "openid-connect",
  "attributes" : {
    "include.in.token.scope" : "false",
    "consent.screen.text" : "${rolesScopeConsentText}",
    "display.on.consent.screen" : "true"
  },
  "protocolMappers" : [ {
    "id" : "9eb470cc-8157-46f2-8233-8cae169c6591",
    "name" : "realm roles",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usermodel-realm-role-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : {
      "user.attribute" : "foo",
      "access.token.claim" : "true",
      "claim.name" : "realm_access.roles",
      "jsonType.label" : "String",
      "multivalued" : "true"
    }
  }, {
    "id" : "eebdefd0-c446-4bf3-b945-08db42f0ea92",
    "name" : "audience resolve",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-audience-resolve-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : { }
  }, {
    "id" : "37c62d93-c670-487c-8c3a-a6329a9924b0",
    "name" : "client roles",
    "protocol" : "openid-connect",
    "protocolMapper" : "oidc-usermodel-client-role-mapper",
    "consentRequired" : false,
    "consentText" : null,
    "config" : {
      "user.attribute" : "foo",
      "access.token.claim" : "true",
      "claim.name" : "resource_access.${client_id}.roles",
      "jsonType.label" : "String",
      "multivalued" : "true"
    }
  } ]
}
```

### `GET_CLIENT_SCOPE_PROTOCOL_MAPPERS`

Get protocol mappers for a client scope

**Required params:** `realm`, `clientScopeId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "83e275f7-b171-45fa-99c7-7c04f91fbe41"
}
```

**Response**

```json
[ {
  "id" : "9eb470cc-8157-46f2-8233-8cae169c6591",
  "name" : "realm roles",
  "protocol" : "openid-connect",
  "protocolMapper" : "oidc-usermodel-realm-role-mapper",
  "consentRequired" : false,
  "consentText" : null,
  "config" : {
    "user.attribute" : "foo",
    "access.token.claim" : "true",
    "introspection.token.claim" : "true",
    "claim.name" : "realm_access.roles",
    "jsonType.label" : "String",
    "multivalued" : "true"
  }
}, {
  "id" : "eebdefd0-c446-4bf3-b945-08db42f0ea92",
  "name" : "audience resolve",
  "protocol" : "openid-connect",
  "protocolMapper" : "oidc-audience-resolve-mapper",
  "consentRequired" : false,
  "consentText" : null,
  "config" : {
    "introspection.token.claim" : "true",
    "access.token.claim" : "true"
  }
}, {
  "id" : "37c62d93-c670-487c-8c3a-a6329a9924b0",
  "name" : "client roles",
  "protocol" : "openid-connect",
  "protocolMapper" : "oidc-usermodel-client-role-mapper",
  "consentRequired" : false,
  "consentText" : null,
  "config" : {
    "user.attribute" : "foo",
    "access.token.claim" : "true",
    "introspection.token.claim" : "true",
    "claim.name" : "resource_access.${client_id}.roles",
    "jsonType.label" : "String",
    "multivalued" : "true"
  }
} ]
```

### `GET_DEFAULT_CLIENT_SCOPES`

Get all default client scopes for realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "58e57c6f-18bf-4347-9ab0-b8325ef522e0",
  "name" : "web-origins",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "7eaa8ede-9a92-487a-9444-60a5d7355542",
  "name" : "role_list",
  "description" : null,
  "protocol" : "saml",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "83e275f7-b171-45fa-99c7-7c04f91fbe41",
  "name" : "roles",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "d20498e8-4ec8-4496-9d8f-c09131dd5d15",
  "name" : "profile",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "eb0bdf87-6cda-4684-89a8-f7bd6f0c7bba",
  "name" : "email",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "def2aa93-13e0-4ff1-8815-6f0224a9c31d",
  "name" : "acr",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "ac32b7a2-6818-4f8c-b3b8-21872eebce0a",
  "name" : "basic",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
} ]
```

### `GET_OPTIONAL_CLIENT_SCOPES`

Get all optional client scopes for realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "35bfd94e-681f-456a-bca0-0d0d8d986a96",
  "name" : "address",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "541f2eae-d481-4d00-be30-89f4f60d169f",
  "name" : "phone",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "55621a1e-cd6b-45a7-9f06-a678e0801b9c",
  "name" : "microprofile-jwt",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
}, {
  "id" : "97aca0c9-7f14-4783-bb48-681de54f0b31",
  "name" : "offline_access",
  "description" : null,
  "protocol" : "openid-connect",
  "attributes" : null,
  "protocolMappers" : null
} ]
```

### `CREATE_CLIENT_SCOPE`

Create a new client scope

**Required params:** `realm`, `clientScope`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScope" : {
    "name" : "docs-demo-scope",
    "protocol" : "openid-connect",
    "description" : "Docs demo"
  }
}
```

**Response**

```json
Successfully created client scope: docs-demo-scope
```

### `UPDATE_CLIENT_SCOPE`

Update an existing client scope

**Required params:** `realm`, `clientScopeId`, `clientScope`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "664e30a9-352e-4af8-929c-17f8a4b50cdf",
  "clientScope" : {
    "name" : "docs-demo-scope",
    "protocol" : "openid-connect",
    "description" : "Updated docs demo"
  }
}
```

**Response**

```json
Successfully updated client scope: 664e30a9-352e-4af8-929c-17f8a4b50cdf
```

### `DELETE_CLIENT_SCOPE`

Delete a client scope

**Required params:** `realm`, `clientScopeId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "664e30a9-352e-4af8-929c-17f8a4b50cdf"
}
```

**Response**

```json
Successfully deleted client scope: 664e30a9-352e-4af8-929c-17f8a4b50cdf
```

### `ADD_DEFAULT_CLIENT_SCOPE`

Add a client scope as default for realm

**Required params:** `realm`, `clientScopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_OPTIONAL_CLIENT_SCOPE`

Add a client scope as optional for realm

**Required params:** `realm`, `clientScopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_DEFAULT_CLIENT_SCOPE`

Remove a default client scope from realm

**Required params:** `realm`, `clientScopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_OPTIONAL_CLIENT_SCOPE`

Remove an optional client scope from realm

**Required params:** `realm`, `clientScopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_PROTOCOL_MAPPER_TO_CLIENT_SCOPE`

Add a protocol mapper to a client scope

**Required params:** `realm`, `clientScopeId`, `mapper`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "mapper": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_CLIENT_SCOPE_PROTOCOL_MAPPER`

Update a protocol mapper in a client scope

**Required params:** `realm`, `clientScopeId`, `mapperId`, `mapper`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "mapperId": "...", "mapper": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_CLIENT_SCOPE_PROTOCOL_MAPPER`

Delete a protocol mapper from a client scope

**Required params:** `realm`, `clientScopeId`, `mapperId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "mapperId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_CLIENT_SCOPE_MAPPED_REALM_ROLES`

Add realm roles to a client scope's scope mapping

**Required params:** `realm`, `clientScopeId`, `roles`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "roles": []}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_CLIENT_SCOPE_MAPPED_REALM_ROLES`

Remove realm roles from a client scope's scope mapping

**Required params:** `realm`, `clientScopeId`, `roles`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "roles": []}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_CLIENT_SCOPE_MAPPED_CLIENT_ROLES`

Add target-client roles to a client scope's scope mapping

**Required params:** `realm`, `clientScopeId`, `targetClientId`, `roles`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "targetClientId": "...", "roles": []}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_CLIENT_SCOPE_MAPPED_CLIENT_ROLES`

Remove target-client roles from a client scope's scope mapping

**Required params:** `realm`, `clientScopeId`, `targetClientId`, `roles`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientScopeId": "...", "targetClientId": "...", "roles": []}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Scope mappings

### `GET_CLIENT_SCOPE_SCOPE_MAPPINGS`

Get combined client-scope scope mappings (realm and client role assignments)

**Required params:** `realm`, `clientScopeId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "83e275f7-b171-45fa-99c7-7c04f91fbe41"
}
```

**Response**

```json
{
  "realmMappings" : null,
  "clientMappings" : null
}
```

### `GET_CLIENT_SCOPE_MAPPED_REALM_ROLES`

Get realm role mappings for a client scope

**Required params:** `realm`, `clientScopeId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "83e275f7-b171-45fa-99c7-7c04f91fbe41"
}
```

**Response**

```json
[ ]
```

### `GET_CLIENT_SCOPE_MAPPED_CLIENT_ROLES`

Get target-client role mappings for a client scope (internal client id)

**Required params:** `realm`, `clientScopeId`, `targetClientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "clientScopeId" : "83e275f7-b171-45fa-99c7-7c04f91fbe41",
  "targetClientId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a"
}
```

**Response**

```json
[ ]
```

## Role

### `GET_REALM_ROLES`

Get all realm-level roles

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "3ce83241-464b-4ca0-8f0f-17002a797aab",
  "name" : "admin",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "68615956-51ca-49ca-865a-f9cb2571b027",
  "name" : "confidential",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "33e23f19-f424-4403-933f-1bcbe0db64e3",
  "name" : "default-roles-quarkus",
  "description" : "${role_default-roles}",
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "c6d57a00-eb97-460d-91b0-89e6a94a7aa5",
  "name" : "offline_access",
  "description" : "${role_offline-access}",
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "c50286f6-3562-473f-ad45-9767b982ff45",
  "name" : "uma_authorization",
  "description" : "${role_uma_authorization}",
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
}, {
  "id" : "d3246456-8f5d-4722-8364-a46a8d25dc7c",
  "name" : "user",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : null
} ]
```

### `GET_REALM_ROLE`

Get a specific realm role by name

**Required params:** `realm`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "admin"
}
```

**Response**

```json
{
  "id" : "3ce83241-464b-4ca0-8f0f-17002a797aab",
  "name" : "admin",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : false,
  "containerId" : "quarkus",
  "attributes" : { }
}
```

### `GET_ROLE_COMPOSITES`

Get composite roles for a realm role

**Required params:** `realm`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "admin"
}
```

**Response**

```json
[ ]
```

### `CREATE_REALM_ROLE`

Create a new realm role

**Required params:** `realm`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "docs-temp-role",
  "description" : "Temporary docs role"
}
```

**Response**

```json
Successfully created role: docs-temp-role
```

### `UPDATE_REALM_ROLE`

Update an existing realm role

**Required params:** `realm`, `roleName`, `roleRepresentation`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "docs-temp-role",
  "roleRepresentation" : {
    "name" : "docs-temp-role",
    "description" : "Updated temp role"
  }
}
```

**Response**

```json
Successfully updated role: docs-temp-role
```

### `ADD_COMPOSITE_TO_ROLE`

Add a composite role to a realm role

**Required params:** `realm`, `roleName`, `compositeRoleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "docs-temp-role",
  "compositeRoleName" : "user"
}
```

**Response**

```json
Successfully added composite role: docs-temp-role -> user
```

### `REMOVE_COMPOSITE_FROM_ROLE`

Remove a composite role from a realm role

**Required params:** `realm`, `roleName`, `compositeRoleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "docs-temp-role",
  "compositeRoleName" : "user"
}
```

**Response**

```json
Successfully removed composite role: docs-temp-role -> user
```

### `DELETE_REALM_ROLE`

Delete a realm role

**Required params:** `realm`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "roleName" : "docs-temp-role"
}
```

**Response**

```json
Successfully deleted role: docs-temp-role
```

## Group

### `GET_GROUPS`

Get all groups in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `GET_GROUP`

Get details of a specific group

**Required params:** `realm`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
{
  "id" : "e61b59d7-4092-4b67-8cc4-6b91491ce829",
  "name" : "docs-sample-group",
  "description" : null,
  "path" : "/docs-sample-group",
  "parentId" : null,
  "subGroupCount" : 0,
  "subGroups" : [ ],
  "attributes" : { },
  "realmRoles" : [ ],
  "clientRoles" : { },
  "access" : {
    "view" : true,
    "viewMembers" : true,
    "manageMembers" : true,
    "manage" : true,
    "manageMembership" : true
  }
}
```

### `GET_GROUP_MEMBERS`

Get members of a group

**Required params:** `realm`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
[ ]
```

### `GET_SUBGROUPS`

List direct subgroups of a group (one level)

**Required params:** `realm`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
[ ]
```

### `GET_GROUP_ROLES`

Get roles assigned to a group

**Required params:** `realm`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829"
}
```

**Response**

```json
[ ]
```

### `CREATE_GROUP`

Create a new group in a realm

**Required params:** `realm`, `groupName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupName" : "docs-parent-group"
}
```

**Response**

```json
Successfully created group: docs-parent-group
```

### `CREATE_SUBGROUP`

Create a subgroup under a parent group

**Required params:** `realm`, `parentGroupId`, `subGroupName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "parentGroupId" : "75ffb474-d246-4b0e-9e5b-9772a06c770d",
  "subGroupName" : "docs-child-group"
}
```

**Response**

```json
Successfully created subgroup: 75ffb474-d246-4b0e-9e5b-9772a06c770d -> docs-child-group
```

### `UPDATE_GROUP`

Update an existing group

**Required params:** `realm`, `groupId`, `groupRepresentation`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "75ffb474-d246-4b0e-9e5b-9772a06c770d",
  "groupRepresentation" : {
    "name" : "docs-parent-group",
    "path" : "/docs-parent-group"
  }
}
```

**Response**

```json
Successfully updated group: 75ffb474-d246-4b0e-9e5b-9772a06c770d
```

### `ADD_ROLE_TO_GROUP`

Add a role to a group

**Required params:** `realm`, `groupId`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "75ffb474-d246-4b0e-9e5b-9772a06c770d",
  "roleName" : "user"
}
```

**Response**

```json
Successfully added role to group: 75ffb474-d246-4b0e-9e5b-9772a06c770d -> user
```

### `REMOVE_ROLE_FROM_GROUP`

Remove a role from a group

**Required params:** `realm`, `groupId`, `roleName`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "75ffb474-d246-4b0e-9e5b-9772a06c770d",
  "roleName" : "user"
}
```

**Response**

```json
Successfully removed role from group: 75ffb474-d246-4b0e-9e5b-9772a06c770d -> user
```

### `DELETE_GROUP`

Delete a group from a realm

**Required params:** `realm`, `groupId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "75ffb474-d246-4b0e-9e5b-9772a06c770d"
}
```

**Response**

```json
Successfully deleted group: 75ffb474-d246-4b0e-9e5b-9772a06c770d
```

## Group client roles

### `GET_GROUP_CLIENT_ROLES`

List client roles assigned to a group

**Required params:** `realm`, `groupId`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829",
  "clientId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a"
}
```

**Response**

```json
[ ]
```

### `GET_GROUP_AVAILABLE_CLIENT_ROLES`

List client roles the group can still be assigned for the given client

**Required params:** `realm`, `groupId`, `clientId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "groupId" : "e61b59d7-4092-4b67-8cc4-6b91491ce829",
  "clientId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a"
}
```

**Response**

```json
[ {
  "id" : "5b9947c6-eb74-4de6-8623-0285720993f3",
  "name" : "uma_protection",
  "description" : null,
  "scopeParamRequired" : null,
  "composite" : false,
  "composites" : null,
  "clientRole" : true,
  "containerId" : "302430aa-3929-42cf-8ba2-2b9d2e71dc3a",
  "attributes" : null
} ]
```

## Identity provider

### `GET_IDENTITY_PROVIDERS`

Get all identity providers in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `GET_IDENTITY_PROVIDER`

Get a specific identity provider by alias

**Required params:** `realm`, `alias`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_IDENTITY_PROVIDER`

Create a new identity provider

**Required params:** `realm`, `identityProvider`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "identityProvider": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_IDENTITY_PROVIDER`

Update an existing identity provider

**Required params:** `realm`, `alias`, `identityProvider`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "...", "identityProvider": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_IDENTITY_PROVIDER`

Delete an identity provider

**Required params:** `realm`, `alias`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_IDENTITY_PROVIDER_MAPPERS`

Get mappers for an identity provider

**Required params:** `realm`, `alias`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_IDENTITY_PROVIDER_MAPPER`

Create an identity provider mapper

**Required params:** `realm`, `alias`, `mapper`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "...", "mapper": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Authentication

### `GET_AUTHENTICATION_FLOWS`

Get all authentication flows in a realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "4c5476fa-9aef-440b-bd14-25bf8cbfcd16",
  "alias" : "browser",
  "description" : "browser based authentication",
  "providerId" : "basic-flow",
  "topLevel" : true,
  "builtIn" : true,
  "authenticationExecutions" : [ {
    "authenticatorConfig" : null,
    "authenticator" : "auth-cookie",
    "authenticatorFlow" : false,
    "requirement" : "ALTERNATIVE",
    "priority" : 10,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : "auth-spnego",
    "authenticatorFlow" : false,
    "requirement" : "DISABLED",
    "priority" : 20,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : "identity-provider-redirector",
    "authenticatorFlow" : false,
    "requirement" : "ALTERNATIVE",
    "priority" : 25,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : null,
    "authenticatorFlow" : true,
    "requirement" : "ALTERNATIVE",
    "priority" : 30,
    "autheticatorFlow" : true,
    "flowAlias" : "forms",
    "userSetupAllowed" : false
  } ]
}, {
  "id" : "75d65771-3bfb-4def-a539-656de7d1af58",
  "alias" : "clients",
  "description" : "Base authentication for clients",
  "providerId" : "client-flow",
  "topLevel" : true,
  "builtIn" : true,
  "authenticationExecutions" : [ {
    "authenticatorConfig" : null,
    "authenticator" : "client-secret",
    "authenticatorFlow" : false,
    "requirement" : "ALTERNATIVE",
    "priority" : 10,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator
… truncated …
```

### `GET_FLOW_EXECUTIONS`

Get executions for an authentication flow

**Required params:** `realm`, `flowAlias`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "flowAlias" : "browser"
}
```

**Response**

```json
[ {
  "id" : "a8148521-acde-41d7-82eb-804ea5d8e033",
  "requirement" : "ALTERNATIVE",
  "displayName" : "Cookie",
  "alias" : null,
  "description" : null,
  "requirementChoices" : [ "REQUIRED", "ALTERNATIVE", "DISABLED" ],
  "configurable" : false,
  "authenticationFlow" : null,
  "providerId" : "auth-cookie",
  "authenticationConfig" : null,
  "flowId" : null,
  "level" : 0,
  "index" : 0,
  "priority" : 10
}, {
  "id" : "3a83b7a1-3b61-44ff-b44d-1041ec95f50f",
  "requirement" : "DISABLED",
  "displayName" : "Kerberos",
  "alias" : null,
  "description" : null,
  "requirementChoices" : [ "REQUIRED", "ALTERNATIVE", "DISABLED" ],
  "configurable" : false,
  "authenticationFlow" : null,
  "providerId" : "auth-spnego",
  "authenticationConfig" : null,
  "flowId" : null,
  "level" : 0,
  "index" : 1,
  "priority" : 20
}, {
  "id" : "7a69cdc3-96d4-44d0-8b56-33f77bc2cb3e",
  "requirement" : "ALTERNATIVE",
  "displayName" : "Identity Provider Redirector",
  "alias" : null,
  "description" : null,
  "requirementChoices" : [ "REQUIRED", "ALTERNATIVE", "DISABLED" ],
  "configurable" : true,
  "authenticationFlow" : null,
  "providerId" : "identity-provider-redirector",
  "authenticationConfig" : null,
  "flowId" : null,
  "level" : 0,
  "index" : 2,
  "priority" : 25
}, {
  "id" : "e6f62a57-3fe1-4aad-8a81-17c1375d44fa",
  "requirement" : "ALTERNATIVE",
  "displayName" : "forms",
  "alias" : null,
  "description" : "Username, password, otp and other auth forms.",
  "requirementChoices" : [ "REQUIRED", "ALTERNATIVE", "DISABLED", "CONDITIONAL" ],
  "configurable" : false,
  "authenticationFlow" : true,
  "providerId" : null,
  "authenticationConfig" : null,
  "flowId" : "fadc7c73-7fae-4c28-ad69-51bb03ba17bf",
  "level" : 0,
  "index" : 3,
  "priority" : 30
}, {
  "id" : "efbffd70-4a
… truncated …
```

### `GET_AUTHENTICATION_FLOW`

Get a specific authentication flow by ID

**Required params:** `realm`, `flowId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "flowId" : "4c5476fa-9aef-440b-bd14-25bf8cbfcd16"
}
```

**Response**

```json
{
  "id" : "4c5476fa-9aef-440b-bd14-25bf8cbfcd16",
  "alias" : "browser",
  "description" : "browser based authentication",
  "providerId" : "basic-flow",
  "topLevel" : true,
  "builtIn" : true,
  "authenticationExecutions" : [ {
    "authenticatorConfig" : null,
    "authenticator" : "auth-cookie",
    "authenticatorFlow" : false,
    "requirement" : "ALTERNATIVE",
    "priority" : 10,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : "auth-spnego",
    "authenticatorFlow" : false,
    "requirement" : "DISABLED",
    "priority" : 20,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : "identity-provider-redirector",
    "authenticatorFlow" : false,
    "requirement" : "ALTERNATIVE",
    "priority" : 25,
    "autheticatorFlow" : false,
    "flowAlias" : null,
    "userSetupAllowed" : false
  }, {
    "authenticatorConfig" : null,
    "authenticator" : null,
    "authenticatorFlow" : true,
    "requirement" : "ALTERNATIVE",
    "priority" : 30,
    "autheticatorFlow" : true,
    "flowAlias" : "forms",
    "userSetupAllowed" : false
  } ]
}
```

### `CREATE_AUTHENTICATION_FLOW`

Create a copy of an existing authentication flow

**Required params:** `realm`, `authFlowNameId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{
  "realm" : "quarkus",
  "authFlowNameId" : "docs-browser-copy"
}
```

**Response**

```json
Error: Failed to execute operation CREATE_AUTHENTICATION_FLOW: Cannot invoke "org.keycloak.representations.idm.AuthenticationFlowRepresentation.setId(String)" because "flowRep" is null
```

### `DELETE_AUTHENTICATION_FLOW`

Delete an authentication flow

**Required params:** `realm`, `flowId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "flowId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_FLOW_EXECUTION`

Update an execution in an authentication flow

**Required params:** `realm`, `flowAlias`, `executionRepresentation`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "flowAlias": "...", "executionRepresentation": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Events

### `GET_ADMIN_EVENTS`

List stored admin events for the realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `GET_USER_EVENTS`

List stored user login events for the realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `UPDATE_REALM_EVENTS_CONFIG`

Update realm events configuration

**Required params:** `realmName`, `eventsConfig`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realmName": "quarkus", "eventsConfig": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CLEAR_ADMIN_EVENTS`

Clear the admin event store for the realm

**Required params:** `realm`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus"}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Component

### `GET_COMPONENTS`

List all components in the realm (user storage, keys, etc.)

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "7b96e738-529f-4995-949d-d48b509af90a",
  "name" : null,
  "providerId" : "declarative-user-profile",
  "providerType" : "org.keycloak.userprofile.UserProfileProvider",
  "parentId" : "quarkus",
  "subType" : null,
  "config" : {
    "kc.user.profile.config" : [ "{\"attributes\":[{\"name\":\"username\",\"displayName\":\"${username}\",\"validations\":{\"length\":{\"min\":3,\"max\":255},\"username-prohibited-characters\":{},\"up-username-not-idn-homograph\":{}},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"email\",\"displayName\":\"${email}\",\"validations\":{\"email\":{},\"length\":{\"max\":255}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"firstName\",\"displayName\":\"${firstName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"lastName\",\"displayName\":\"${lastName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false}],\"groups\":[{\"name\":\"user-metadata\",\"displayHeader\":\"User metadata\",\"displayDescription\":\"Attributes, which refer to user metadata\"}],\"unmanagedAttributePolicy\":\"ENABLED\"}" ]
  }
}, {
  "id" : "7ebad719-3c5e-4880-a9f1-3242dd9dbe24",
  "name" : "Consent Required",
  "providerId" : "consent-required",
  "providerType" : "org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy",
  "parentId" 
… truncated …
```

### `GET_COMPONENT`

Get a component by id

**Required params:** `realm`, `componentId`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "componentId" : "7b96e738-529f-4995-949d-d48b509af90a"
}
```

**Response**

```json
{
  "id" : "7b96e738-529f-4995-949d-d48b509af90a",
  "name" : null,
  "providerId" : "declarative-user-profile",
  "providerType" : "org.keycloak.userprofile.UserProfileProvider",
  "parentId" : "quarkus",
  "subType" : null,
  "config" : {
    "kc.user.profile.config" : [ "{\"attributes\":[{\"name\":\"username\",\"displayName\":\"${username}\",\"validations\":{\"length\":{\"min\":3,\"max\":255},\"username-prohibited-characters\":{},\"up-username-not-idn-homograph\":{}},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"email\",\"displayName\":\"${email}\",\"validations\":{\"email\":{},\"length\":{\"max\":255}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"firstName\",\"displayName\":\"${firstName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false},{\"name\":\"lastName\",\"displayName\":\"${lastName}\",\"validations\":{\"length\":{\"max\":255},\"person-name-prohibited-characters\":{}},\"required\":{\"roles\":[\"user\"]},\"permissions\":{\"view\":[\"admin\",\"user\"],\"edit\":[\"admin\",\"user\"]},\"multivalued\":false}],\"groups\":[{\"name\":\"user-metadata\",\"displayHeader\":\"User metadata\",\"displayDescription\":\"Attributes, which refer to user metadata\"}],\"unmanagedAttributePolicy\":\"ENABLED\"}" ]
  }
}
```

### `CREATE_COMPONENT`

Create a realm component (e.g. LDAP user federation)

**Required params:** `realm`, `component`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "component": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_COMPONENT`

Update a realm component

**Required params:** `realm`, `componentId`, `component`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "componentId": "...", "component": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_COMPONENT`

Delete a realm component

**Required params:** `realm`, `componentId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "componentId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_SUB_COMPONENTS`

List sub-components of a parent component and provider type

**Required params:** `realm`, `parentId`, `type`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "parentId": "...", "type": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Keys

### `GET_REALM_KEYS`

Get realm key metadata (signing, encryption, etc.)

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
{
  "active" : {
    "HS256" : "1e092ed5-579a-4dff-a79a-10ba5fd018f0",
    "HS512" : "be16206d-a13b-4b7f-9730-8a821e57b83d",
    "RS256" : "cfIADN_xxCJmVkWyN-PNXEEvMUWs2r68CxtmhEDNzXU",
    "AES" : "b04473d3-8395-4016-b455-19a9e951106b"
  },
  "keys" : [ {
    "providerId" : "066f8625-06ba-4463-995f-93a058d2d800",
    "providerPriority" : 100,
    "kid" : "cfIADN_xxCJmVkWyN-PNXEEvMUWs2r68CxtmhEDNzXU",
    "status" : "ACTIVE",
    "type" : "RSA",
    "algorithm" : "RS256",
    "publicKey" : "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn5T13suF8mlS+pJXp0U1bto41nW55wpcs+Rps8ZVCRyJKWqzwSCYnI7lm0rB2wBpAAO4OPoj1zlmVoFmBPsDU9Xf7rjsJb5LIzIQDCZY44aSDZt6RR+gakPiQvlzHyW/RozYpngDJF7TsTD7rdRF1xQ4RprfBF8fwK/xsU7pxbeom5xDHZhz3fiw8s+7UdbmnazDHfAjU58aUrLGgVRfUsuoHjtsptYlOIXEifaeMetXZE+HhqLYRHQPDap5fbBJl773Trosn7N9nmzN4x1xxGj9So21WC5UboQs9sAIVgizc4omjZ5Y4RN9HLH7G4YwJctNntzmnJhDui9zAO+zSQIDAQAB",
    "certificate" : "MIICnTCCAYUCBgFp4EYIrjANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdwcm90ZWFuMB4XDTE5MDQwMjIyNTYxOVoXDTI5MDQwMjIyNTc1OVowEjEQMA4GA1UEAwwHcHJvdGVhbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJ+U9d7LhfJpUvqSV6dFNW7aONZ1uecKXLPkabPGVQkciSlqs8EgmJyO5ZtKwdsAaQADuDj6I9c5ZlaBZgT7A1PV3+647CW+SyMyEAwmWOOGkg2bekUfoGpD4kL5cx8lv0aM2KZ4AyRe07Ew+63URdcUOEaa3wRfH8Cv8bFO6cW3qJucQx2Yc934sPLPu1HW5p2swx3wI1OfGlKyxoFUX1LLqB47bKbWJTiFxIn2njHrV2RPh4ai2ER0Dw2qeX2wSZe+9066LJ+zfZ5szeMdccRo/UqNtVguVG6ELPbACFYIs3OKJo2eWOETfRyx+xuGMCXLTZ7c5pyYQ7ovcwDvs0kCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAVtmRKDb4OK5iSA46tagMBkp6L7WuPpCWuHGWwobEP+BecYsShW7zP3s12oA8SNSwbhvu0CRqgzxhuypgf3hKQFVU153Erv4hzkj+8S0s5LR/ZE7tDNY2lzJ3yQKXy3Md7EkuzzvOZ50MTrcSKAanWq/ZW1OTnrtGymj5zGJnTg7mMnJzEIGePxkvPu/QdchiPBLqxfZYm1jsFGY25djOC3N/KmVcRVmPRGuu6D8tBFHlKoPfZYPdbMvsvs24aupHKRcZ+ofTCpK+2Qo8c0pSSqeEYHGmuGqC6lC6ozxtxSABPO9Q1R1tZBU7Kg5HvXUwwmoVS3EGub46
… truncated …
```

## Required actions

### `GET_REQUIRED_ACTIONS`

List required actions for the realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "alias" : "CONFIGURE_TOTP",
  "name" : "Configure OTP",
  "providerId" : "CONFIGURE_TOTP",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 10,
  "config" : { }
}, {
  "alias" : "TERMS_AND_CONDITIONS",
  "name" : "Terms and Conditions",
  "providerId" : "TERMS_AND_CONDITIONS",
  "enabled" : false,
  "defaultAction" : false,
  "priority" : 20,
  "config" : { }
}, {
  "alias" : "UPDATE_PASSWORD",
  "name" : "Update Password",
  "providerId" : "UPDATE_PASSWORD",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 30,
  "config" : { }
}, {
  "alias" : "UPDATE_PROFILE",
  "name" : "Update Profile",
  "providerId" : "UPDATE_PROFILE",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 40,
  "config" : { }
}, {
  "alias" : "VERIFY_EMAIL",
  "name" : "Verify Email",
  "providerId" : "VERIFY_EMAIL",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 50,
  "config" : { }
}, {
  "alias" : "delete_account",
  "name" : "Delete Account",
  "providerId" : "delete_account",
  "enabled" : false,
  "defaultAction" : false,
  "priority" : 60,
  "config" : { }
}, {
  "alias" : "delete_credential",
  "name" : "Delete Credential",
  "providerId" : "delete_credential",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 100,
  "config" : { }
}, {
  "alias" : "update_user_locale",
  "name" : "Update User Locale",
  "providerId" : "update_user_locale",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 1000,
  "config" : { }
} ]
```

### `GET_REQUIRED_ACTION`

Get a required action by provider alias

**Required params:** `realm`, `alias`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "alias" : "VERIFY_EMAIL"
}
```

**Response**

```json
{
  "alias" : "VERIFY_EMAIL",
  "name" : "Verify Email",
  "providerId" : "VERIFY_EMAIL",
  "enabled" : true,
  "defaultAction" : false,
  "priority" : 50,
  "config" : { }
}
```

### `UPDATE_REQUIRED_ACTION`

Update a required action (enabled, name, etc.)

**Required params:** `realm`, `alias`, `requiredAction`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "alias": "...", "requiredAction": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `EXECUTE_ACTIONS_EMAIL`

Send email to the user to execute the listed required actions

**Required params:** `realm`, `userId`, `actions`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "userId": "...", "actions": ["UPDATE_PASSWORD"]}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Realm policies

### `GET_REALM_CLIENT_POLICIES`

Get realm client policies (includeGlobal optional, default true)

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
{
  "policies" : [ ],
  "globalPolicies" : [ ]
}
```

### `GET_REALM_CLIENT_PROFILES`

Get realm client profiles (includeGlobal optional, default true)

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
{
  "profiles" : [ ],
  "globalProfiles" : [ {
    "name" : "fapi-1-baseline",
    "description" : "Client profile, which enforce clients to conform 'Financial-grade API Security Profile 1.0 - Part 1: Baseline' specification.",
    "executors" : [ {
      "executor" : "secure-session",
      "configuration" : { }
    }, {
      "executor" : "pkce-enforcer",
      "configuration" : {
        "auto-configure" : true
      }
    }, {
      "executor" : "secure-client-authenticator",
      "configuration" : {
        "allowed-client-authenticators" : [ "client-jwt", "client-secret-jwt", "client-x509" ],
        "default-client-authenticator" : "client-jwt"
      }
    }, {
      "executor" : "secure-client-uris",
      "configuration" : { }
    }, {
      "executor" : "consent-required",
      "configuration" : {
        "auto-configure" : true
      }
    }, {
      "executor" : "full-scope-disabled",
      "configuration" : {
        "auto-configure" : true
      }
    } ]
  }, {
    "name" : "fapi-1-advanced",
    "description" : "Client profile, which enforce clients to conform 'Financial-grade API Security Profile 1.0 - Part 2: Advanced' specification.",
    "executors" : [ {
      "executor" : "secure-session",
      "configuration" : { }
    }, {
      "executor" : "confidential-client",
      "configuration" : { }
    }, {
      "executor" : "secure-client-authenticator",
      "configuration" : {
        "allowed-client-authenticators" : [ "client-jwt", "client-x509" ],
        "default-client-authenticator" : "client-jwt"
      }
    }, {
      "executor" : "secure-client-uris",
      "configuration" : { }
    }, {
      "executor" : "secure-request-object",
      "configuration" : {
        "available-period" : "3600",
        "verify-nbf" : true
      }
    }, {
… truncated …
```

### `GET_CLIENT_REGISTRATION_PROVIDERS`

List client registration policy component types (providers) for the realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ {
  "id" : "allowed-client-templates",
  "helpText" : "When present, it allows to specify whitelist of client scopes, which will be allowed in representation of registered (or updated) client",
  "properties" : [ {
    "name" : "allowed-client-scopes",
    "label" : "allowed-client-scopes.label",
    "helpText" : "allowed-client-scopes.tooltip",
    "type" : "MultivaluedList",
    "defaultValue" : null,
    "options" : [ "roles", "basic", "role_list", "address", "email", "web-origins", "acr", "offline_access", "microprofile-jwt", "profile", "phone" ],
    "secret" : false,
    "required" : false,
    "readOnly" : false
  }, {
    "name" : "allow-default-scopes",
    "label" : "allow-default-scopes.label",
    "helpText" : "allow-default-scopes.tooltip",
    "type" : "boolean",
    "defaultValue" : true,
    "options" : null,
    "secret" : false,
    "required" : false,
    "readOnly" : false
  } ],
  "clientProperties" : null,
  "metadata" : { }
}, {
  "id" : "client-disabled",
  "helpText" : "When present, then newly registered client will be disabled and admin needs to manually enable them",
  "properties" : [ ],
  "clientProperties" : null,
  "metadata" : { }
}, {
  "id" : "scope",
  "helpText" : "When present, then newly registered client won't have full scope allowed",
  "properties" : [ ],
  "clientProperties" : null,
  "metadata" : { }
}, {
  "id" : "max-clients",
  "helpText" : "When present, then it won't be allowed to register new client if count of existing clients in realm is same or bigger than configured limit",
  "properties" : [ {
    "name" : "max-clients",
    "label" : "max-clients.label",
    "helpText" : "max-clients.tooltip",
    "type" : "String",
    "defaultValue" : "200",
    "options" : null,
    "secret" : false,
    "required" : false,
  
… truncated …
```

### `PUSH_REALM_REVOCATION`

Push a notBefore revocation to all cluster nodes (notBefore policy refresh)

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
{
  "successRequests" : null,
  "failedRequests" : null
}
```

## Localization

### `GET_REALM_LOCALES`

List custom locales configured for the realm (realm-specific overrides).

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `SAVE_LOCALIZATION_TEXT`

Create or update a single message key for a locale

**Required params:** `realm`, `locale`, `key`, `text`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "locale" : "en",
  "key" : "docsDemoKey",
  "text" : "Docs demo value"
}
```

**Response**

```json
OK
```

### `GET_LOCALIZATION_TEXTS`

Get all translation key/value for a locale (useRealmFallback optional).

**Required params:** `realm`, `locale`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "locale" : "en"
}
```

**Response**

```json
{
  "docsDemoKey" : "Docs demo value"
}
```

### `DELETE_LOCALIZATION_TEXT`

Delete one translation key for a locale

**Required params:** `realm`, `locale`, `key`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus",
  "locale" : "en",
  "key" : "docsDemoKey"
}
```

**Response**

```json
OK
```

### `DELETE_LOCALIZATION_TEXTS`

Delete all custom translations for a locale in the realm

**Required params:** `realm`, `locale`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "locale": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_OR_UPDATE_LOCALIZATION_TEXTS`

Replace or merge bulk translations (texts: JSON object of key to string).

**Required params:** `realm`, `locale`, `texts`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "locale": "...", "texts": {"key":"value"}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## User profile

### `GET_USER_PROFILE_CONFIG`

Get declarative user profile (UPConfig) for the realm

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
{
  "attributes" : [ {
    "name" : "username",
    "displayName" : "${username}",
    "validations" : {
      "length" : {
        "min" : 3,
        "max" : 255
      },
      "username-prohibited-characters" : { },
      "up-username-not-idn-homograph" : { }
    },
    "annotations" : null,
    "required" : null,
    "permissions" : {
      "view" : [ "admin", "user" ],
      "edit" : [ "admin", "user" ]
    },
    "selector" : null,
    "group" : null,
    "multivalued" : false,
    "defaultValue" : null
  }, {
    "name" : "email",
    "displayName" : "${email}",
    "validations" : {
      "email" : { },
      "length" : {
        "max" : 255
      }
    },
    "annotations" : null,
    "required" : {
      "roles" : [ "user" ],
      "scopes" : null
    },
    "permissions" : {
      "view" : [ "admin", "user" ],
      "edit" : [ "admin", "user" ]
    },
    "selector" : null,
    "group" : null,
    "multivalued" : false,
    "defaultValue" : null
  }, {
    "name" : "firstName",
    "displayName" : "${firstName}",
    "validations" : {
      "length" : {
        "max" : 255
      },
      "person-name-prohibited-characters" : { }
    },
    "annotations" : null,
    "required" : {
      "roles" : [ "user" ],
      "scopes" : null
    },
    "permissions" : {
      "view" : [ "admin", "user" ],
      "edit" : [ "admin", "user" ]
    },
    "selector" : null,
    "group" : null,
    "multivalued" : false,
    "defaultValue" : null
  }, {
    "name" : "lastName",
    "displayName" : "${lastName}",
    "validations" : {
      "length" : {
        "max" : 255
      },
      "person-name-prohibited-characters" : { }
    },
    "annotations" : null,
    "required" : {
      "roles" : [ "user" ],
      "scopes" : null
    },
    "permissions" : {
      "view" : [ "admi
… truncated …
```

### `UPDATE_REALM_CLIENT_PROFILES`

Update realm client profiles (ClientProfilesRepresentation in profiles)

**Required params:** `realm`, `profiles`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "profiles": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Organizations

### `GET_ORGANIZATIONS`

List all organizations in the realm (Keycloak 24+ organizations feature).

**Required params:** `realm`

!!! success "Validated against local Keycloak"
    Live request/response captured below.

**Request**

```json
{
  "realm" : "quarkus"
}
```

**Response**

```json
[ ]
```

### `GET_ORGANIZATION`

Get an organization by id

**Required params:** `realm`, `orgId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_ORGANIZATION`

Create an organization (organization: OrganizationRepresentation JSON).

**Required params:** `realm`, `organization`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "organization": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_ORGANIZATION`

Update an organization

**Required params:** `realm`, `orgId`, `organization`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "...", "organization": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_ORGANIZATION`

Delete an organization

**Required params:** `realm`, `orgId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_ORGANIZATION_MEMBERS`

List members of an organization

**Required params:** `realm`, `orgId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `ADD_ORGANIZATION_MEMBER`

Add a user to an organization

**Required params:** `realm`, `orgId`, `userId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "...", "userId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `REMOVE_ORGANIZATION_MEMBER`

Remove a user from an organization

**Required params:** `realm`, `orgId`, `userId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "orgId": "...", "userId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Discourse

### `SEARCH_DISCOURSE`

Search Keycloak Discourse forum

**Required params:** `query`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{
  "query" : "admin rest api"
}
```

**Response**

```json
Error: Failed to execute operation SEARCH_DISCOURSE: Received: 'Not Found, status code 404' when invoking REST Client method: 'dev.shaaf.keycloak.mcp.server.discourse.DiscourseService#search'
```

## Authorization (UMA)

### `GET_AUTHZ_RESOURCE_SERVER`

Get UMA/authorization server settings for a client (client id = internal UUID).

**Required params:** `realm`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_RESOURCE_SERVER`

Update authorization service settings for a client (resourceServer object)

**Required params:** `realm`, `clientId`, `resourceServer`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "resourceServer": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `LIST_AUTHZ_RESOURCES`

List UMA protected resources for a client

**Required params:** `realm`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_AUTHZ_RESOURCE`

Get an authorization resource by id

**Required params:** `realm`, `clientId`, `resourceId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "resourceId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_AUTHZ_RESOURCE`

Create a protected resource (body in resource)

**Required params:** `realm`, `clientId`, `resource`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "resource": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_RESOURCE`

Update a protected resource

**Required params:** `realm`, `clientId`, `resourceId`, `resource`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "resourceId": "...", "resource": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_AUTHZ_RESOURCE`

Delete a protected resource

**Required params:** `realm`, `clientId`, `resourceId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "resourceId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `LIST_AUTHZ_SCOPES`

List authorization scopes for a client

**Required params:** `realm`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_AUTHZ_SCOPE`

Get an authorization scope by id

**Required params:** `realm`, `clientId`, `scopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "scopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_AUTHZ_SCOPE`

Create an authorization scope

**Required params:** `realm`, `clientId`, `scope`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "scope": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_SCOPE`

Update an authorization scope

**Required params:** `realm`, `clientId`, `scopeId`, `scope`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "scopeId": "...", "scope": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_AUTHZ_SCOPE`

Delete an authorization scope

**Required params:** `realm`, `clientId`, `scopeId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "scopeId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `LIST_AUTHZ_POLICIES`

List policies for a client's authorization service

**Required params:** `realm`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_AUTHZ_POLICY`

Get a policy by id

**Required params:** `realm`, `clientId`, `policyId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "policyId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_AUTHZ_POLICY`

Create a policy (type, name, config depend on policy provider)

**Required params:** `realm`, `clientId`, `policy`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "policy": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_POLICY`

Update a policy by id

**Required params:** `realm`, `clientId`, `policyId`, `policy`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "policyId": "...", "policy": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_AUTHZ_POLICY`

Delete a policy by id

**Required params:** `realm`, `clientId`, `policyId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "policyId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_AUTHZ_RESOURCE_PERMISSION`

Get a resource permission by id

**Required params:** `realm`, `clientId`, `permissionId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_AUTHZ_RESOURCE_PERMISSION`

Create a resource-based permission (permission object)

**Required params:** `realm`, `clientId`, `permission`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permission": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_RESOURCE_PERMISSION`

Update a resource permission by id

**Required params:** `realm`, `clientId`, `permissionId`, `permission`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "...", "permission": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_AUTHZ_RESOURCE_PERMISSION`

Delete a resource permission by id

**Required params:** `realm`, `clientId`, `permissionId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `LIST_AUTHZ_SCOPE_PERMISSIONS`

List scope permissions; optional: name, resourceId, scopeId, first, max (pagination).

**Required params:** `realm`, `clientId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `GET_AUTHZ_SCOPE_PERMISSION`

Get a scope-based permission by id

**Required params:** `realm`, `clientId`, `permissionId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `CREATE_AUTHZ_SCOPE_PERMISSION`

Create a scope-based permission

**Required params:** `realm`, `clientId`, `permission`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permission": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `UPDATE_AUTHZ_SCOPE_PERMISSION`

Update a scope-based permission by id

**Required params:** `realm`, `clientId`, `permissionId`, `permission`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "...", "permission": {}}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

### `DELETE_AUTHZ_SCOPE_PERMISSION`

Delete a scope-based permission by id

**Required params:** `realm`, `clientId`, `permissionId`

!!! note "Schema only"
    Not executed in the live docs run; params come from the command implementation.

**Request**

```json
{"realm": "quarkus", "clientId": "...", "permissionId": "..."}
```

**Response**

```json
_Not executed in the live docs run_ (needs feature flags, nested IDs, or is destructive/environment-specific). Required params and description are from the command implementation.
```

## Tips

- Prefer `realm` for realm-scoped admin ops; use `realmName` for realm CRUD (`GET_REALM`, `CREATE_REALM`, …).
- `clientId` is usually the public client id string (e.g. `backend-service`). Some role-mapping
  ops expect the client's **internal UUID** from `GET_CLIENT`.
- Nested `*Representation` bodies follow the Keycloak Admin REST API shapes.
- Destructive ops (`DELETE_*`, `CLEAR_*`, `LOGOUT_ALL_USERS`) should be disabled in production
  via `keycloak.mcp.commands.disabled`.
