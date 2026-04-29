# Keycloak Admin API – MCP coverage and gaps

**Project:** Keycloak MCP Server  
**Version:** 0.4.0  
**Last updated:** 2026-04-29  

## Summary

The MCP tool exposes one operation per `KeycloakOperation` enum value. Each value has a registered `KeycloakCommand` (CDI), unless disabled via `keycloak.mcp.commands.*` in `application.properties`.

As of 0.4.0, **all enum operations are implemented** and covered by `AllKeycloakOperationsRegisteredTest` (every `KeycloakOperation` is registered when `enable-all-by-default` is true, which is the test default).

There are **178** `KeycloakOperation` values. The full list is the enum in `KeycloakOperation.java` and the startup log from `CommandRegistry` when `keycloak.mcp.commands.log-on-startup=true`.

## Coverage by area (0.4.0)

| Area | Notes |
|------|--------|
| **Users** | CRUD, groups, **realm** roles, **client** roles, count, email, password, credentials (list, delete, reorder) |
| **Realms** | List/get/create/update/delete, enable, events **config** |
| **Clients** | CRUD, secret, service account, protocol mappers, client roles |
| **Client scopes** | Full CRUD, default/optional realm linkage, protocol mappers, **scope → role** mappings (realm and target-client roles) |
| **Realm roles** | CRUD, composites |
| **Groups** | CRUD, members, subgroups, **realm** roles, **client** roles |
| **Identity providers** | List/get/create/update/delete, mappers |
| **Authentication** | Flows, executions, CRUD on flows (where API allows) |
| **Sessions & attack detection** | User sessions, consents, revoke, logout, offline / client-scoped sessions, clear login failures, **brute-force user status** |
| **Event stores** | Get/clear **admin** and **user** event **streams** (separate from realm **events config**) |
| **Components** | List/get/create/update/delete, sub-components (user federation, etc.) |
| **Keys** | Realm key metadata |
| **Required actions** | List/get/update, **execute-actions email** |
| **UMA / Authorization** | Resource server, resources, scopes, policies, resource & scope permissions (client = internal id, authorization must be enabled on client) |
| **Realm admin extras** | LDAP test, user-storage **sync**, **push revocation**, **logout all users**, **client policies/profiles** (Keycloak 12+ JSON), client registration **provider** types list |
| **Localization** | Locales, get/save/bulk update/delete of realm override messages |
| **Organizations** | Keycloak 24+ org API: CRUD, members, add/remove member |
| **User profile (declarative)** | Get/update `UPConfig` for the realm |
| **Discourse** | `SEARCH_DISCOURSE` (integration) |

## Intentional / remaining gaps vs “entire” Keycloak

The [Keycloak Admin REST API](https://www.keycloak.org/docs-api/latest/rest-api/) is large and evolving. The following are **not** modeled as separate MCP operations in 0.4.0 (or are only partially representable). Use the Admin console or raw REST if you need them.

1. **Partial / PATCH-style updates** – MCP commands pass full JSON representations (or the fields the Keycloak client expects). The Admin API’s true PATCH endpoints for some resources are not exposed as first-class `PARTIAL_*` operations.

2. **Token introspection / ROPC / well-known** – `PUSH_REALM_REVOCATION` and `LOGOUT_ALL_USERS` use `RealmResource` helpers. **OAuth2 token introspection** and **token-revocation-by-token-value** are typically not the same as admin “push not-before”; implement via OAuth/OIDC endpoints, not the subset wrapped here.

3. **List resource permissions (aggregate)** – The Java admin client’s resource-permission sub-resource is oriented around **get/create/update/delete by id**; discovery is usually via **policies** or the export flows. `LIST_AUTHZ_POLICIES` and resource CRUD cover most admin workflows.

4. **User-storage edge APIs** – `SYNC_USER_STORAGE` wraps `userStorage().syncUsers(storageId, action)`; `removeImportedUsers` / `unlink` and related calls are not separate enum values.

5. **LDAP** – `TEST_LDAP_CONNECTION` maps to `testLDAPConnection(TestLdapConnectionRepresentation)`; `ldapServerCapabilities` is a separate admin call and is not exposed.

6. **Organizations** – Exposed: core org CRUD and members. **Invites**, org-level IdP linking, and other newer sub-resources may need additional operations in a future version.

7. **Workflows / client types (preview)** – Realm exposes `workflows`, `clientTypes` in recent Keycloak; not yet in the enum.

8. **CORS / test SMTP** – `testSMTPConnection` exists on `RealmResource` but has no dedicated MCP op.

## Historical note

Documents written for **0.3.x** that described “46 operations”, “client scopes missing”, or “26 hidden service methods” are **obsolete**. The current source of truth is `KeycloakOperation` + the `commands` package and `AllKeycloakOperationsRegisteredTest`.

## How to verify locally

- Run tests: `mvn test` (includes `AllKeycloakOperationsRegisteredTest`).
- Run the server with `quarkus.mcp.log-prompt` / default logging: enabled commands are printed on startup if `keycloak.mcp.commands.log-on-startup=true`.
