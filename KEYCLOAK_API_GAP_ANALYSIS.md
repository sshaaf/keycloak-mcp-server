# Keycloak Admin API - Gap Analysis

**Project**: Keycloak MCP Server
**Version**: 0.3.0
**Analysis Date**: 2026-03-09

## Executive Summary

This document provides a comprehensive gap analysis between the currently implemented Keycloak MCP Server operations and the full Keycloak Admin REST API. The server currently implements **46 operations** across 8 major categories. However, there are significant gaps in coverage, particularly around client scopes, user federation, events, sessions, and advanced security features.

---

## Current Implementation Overview

### Operations Currently Exposed via MCP Tool (46 operations)

#### 1. User Management (15 operations) ✅
- `GET_USERS` - List all users in a realm
- `GET_USER_BY_USERNAME` - Find user by username
- `GET_USER_BY_ID` - Get user by ID
- `CREATE_USER` - Create new user
- `UPDATE_USER` - Update user details
- `DELETE_USER` - Delete user
- `GET_USER_GROUPS` - Get user's groups
- `ADD_USER_TO_GROUP` - Add user to group
- `REMOVE_USER_FROM_GROUP` - Remove user from group
- `GET_USER_ROLES` - Get user's roles
- `ADD_ROLE_TO_USER` - Assign role to user
- `REMOVE_ROLE_FROM_USER` - Remove role from user
- `RESET_PASSWORD` - Reset user password
- `SEND_VERIFICATION_EMAIL` - Send email verification
- `COUNT_USERS` - Count users in realm

#### 2. Realm Management (3 operations) ⚠️ Partial
- `GET_REALMS` - List all realms
- `GET_REALM` - Get realm details
- `CREATE_REALM` - Create new realm

#### 3. Client Management (8 operations) ⚠️ Partial
- `GET_CLIENTS` - List all clients
- `GET_CLIENT` - Get client details
- `CREATE_CLIENT` - Create new client
- `DELETE_CLIENT` - Delete client
- `GENERATE_CLIENT_SECRET` - Generate new secret
- `GET_CLIENT_ROLES` - List client roles
- `CREATE_CLIENT_ROLE` - Create client role
- `DELETE_CLIENT_ROLE` - Delete client role

#### 4. Role Management (2 operations) ⚠️ Partial
- `GET_REALM_ROLES` - List realm roles
- `GET_REALM_ROLE` - Get specific role

#### 5. Group Management (6 operations) ✅
- `GET_GROUPS` - List all groups
- `GET_GROUP_MEMBERS` - List group members
- `CREATE_GROUP` - Create new group
- `UPDATE_GROUP` - Update group
- `DELETE_GROUP` - Delete group
- `CREATE_SUBGROUP` - Create subgroup

#### 6. Identity Provider Management (3 operations) ⚠️ Partial
- `GET_IDENTITY_PROVIDERS` - List IDPs
- `GET_IDENTITY_PROVIDER` - Get IDP details
- `GET_IDENTITY_PROVIDER_MAPPERS` - Get IDP mappers

#### 7. Authentication Flow Management (6 operations) ✅
- `GET_AUTHENTICATION_FLOWS` - List flows
- `GET_AUTHENTICATION_FLOW` - Get flow details
- `CREATE_AUTHENTICATION_FLOW` - Create flow
- `DELETE_AUTHENTICATION_FLOW` - Delete flow
- `GET_FLOW_EXECUTIONS` - List executions
- `UPDATE_FLOW_EXECUTION` - Update execution

#### 8. Discourse Integration (1 operation)
- `SEARCH_DISCOURSE` - Search Keycloak discourse

---

## Service Methods Not Exposed (26 methods)

The following methods exist in service classes but are **NOT** exposed via the MCP Tool enum:

### RealmService (5 methods not exposed)
- `updateRealm()` - Update realm configuration
- `deleteRealm()` - Delete a realm
- `setRealmEnabled()` - Enable/disable realm
- `getRealmEventsConfig()` - Get events configuration
- `updateRealmEventsConfig()` - Update events configuration

### ClientService (6 methods not exposed)
- `updateClient()` - Update client configuration
- `getClientSecret()` - Get current client secret
- `getServiceAccountUser()` - Get service account user
- `getClientProtocolMappers()` - Get protocol mappers
- `addProtocolMapperToClient()` - Add protocol mapper

### RoleService (6 methods not exposed)
- `createRealmRole()` - Create realm role
- `updateRealmRole()` - Update realm role
- `deleteRealmRole()` - Delete realm role
- `getRoleComposites()` - Get composite roles
- `addCompositeToRole()` - Add composite role
- `removeCompositeFromRole()` - Remove composite role

### GroupService (4 methods not exposed)
- `getGroup()` - Get specific group details
- `getGroupRoles()` - Get group's roles
- `addRoleToGroup()` - Assign role to group
- `removeRoleFromGroup()` - Remove role from group

### IdentityProviderService (5 methods not exposed)
- `createIdentityProvider()` - Create IDP
- `updateIdentityProvider()` - Update IDP
- `deleteIdentityProvider()` - Delete IDP
- `createIdentityProviderMapper()` - Create IDP mapper

---

## Major Missing API Categories

### 1. Client Scopes ❌ **MISSING**
**Priority**: HIGH
**Impact**: Critical for OIDC/OAuth2 configurations

Missing operations:
- List client scopes
- Get client scope details
- Create client scope
- Update client scope
- Delete client scope
- Get client scope protocol mappers
- Add/remove protocol mappers to client scope
- Add client scope to client (default/optional)
- Remove client scope from client

**Keycloak Admin Client Classes**:
- `ClientScopesResource`
- `ClientScopeResource`

---

### 2. User Sessions & Consents ❌ **MISSING**
**Priority**: HIGH
**Impact**: Essential for session management and security

Missing operations:
- Get user sessions
- Get offline sessions
- Revoke user sessions
- Logout user
- Get user consents
- Revoke user consent
- Get client user sessions
- Get client offline sessions

**Keycloak Admin Client Classes**:
- `UserResource.getUserSessions()`
- `UserResource.getOfflineSessions()`
- `UserResource.logout()`
- `UserResource.getConsents()`
- `ClientResource.getUserSessions()`

---

### 3. Attack Detection / Brute Force Protection ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Security monitoring

Missing operations:
- Clear login failures for user
- Clear all login failures in realm
- Get attack detection status

**Keycloak Admin Client Classes**:
- `AttackDetectionResource`

---

### 4. Events Management ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Audit logging and compliance

Missing operations:
- Get admin events
- Get user events
- Clear admin events
- Clear user events
- Update events configuration

**Keycloak Admin Client Classes**:
- `RealmResource.getAdminEvents()`
- `RealmResource.getEvents()`
- `RealmResource.clearEvents()`
- `RealmResource.clearAdminEvents()`

---

### 5. Components (User Storage, Keys) ❌ **MISSING**
**Priority**: HIGH
**Impact**: User federation and key management

Missing operations:
- List components
- Get component details
- Create component (LDAP, Kerberos, custom providers)
- Update component
- Delete component
- Test LDAP connection
- Sync users from user storage
- Get sub-components

**Keycloak Admin Client Classes**:
- `ComponentsResource`
- `ComponentResource`

---

### 6. Keys Management ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Cryptographic key management

Missing operations:
- Get realm keys
- Get active keys
- Get key metadata

**Keycloak Admin Client Classes**:
- `KeyResource`

---

### 7. Required Actions ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: User onboarding workflows

Missing operations:
- List required actions
- Get required action
- Update required action
- Register required action
- Unregister required action
- Execute actions email

**Keycloak Admin Client Classes**:
- `AuthenticationManagementResource.getRequiredActions()`
- `UserResource.executeActionsEmail()`

---

### 8. Scope Mappings ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Fine-grained authorization

Missing operations:
- Get client scope mappings
- Add client scope mapping
- Remove client scope mapping
- Get realm scope mappings
- Get available scope mappings

**Keycloak Admin Client Classes**:
- `ScopeMappingsResource`
- `RoleScopeResource`

---

### 9. Client Policies & Profiles ❌ **MISSING**
**Priority**: LOW
**Impact**: Advanced client security policies (Keycloak 12+)

Missing operations:
- List client policies
- Get client policy
- Create client policy
- Update client policy
- Delete client policy
- List client profiles
- Get client profile
- Create client profile
- Update client profile
- Delete client profile

**Keycloak Admin Client Classes**:
- `ClientPoliciesResource`
- `ClientProfilesResource`

---

### 10. Credential Management ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Password and credential operations

Missing operations:
- Get user credentials
- Delete user credential
- Move credential to position
- Get credential types
- Disable credential types
- Reset password with credential ID

**Keycloak Admin Client Classes**:
- `UserResource.credentials()`
- `CredentialResource`

---

### 11. Localization ❌ **MISSING**
**Priority**: LOW
**Impact**: Multi-language support

Missing operations:
- Get realm localization texts
- Get realm localization for locale
- Add realm localization text
- Update realm localization text
- Delete realm localization text

**Keycloak Admin Client Classes**:
- `RealmLocalizationResource`

---

### 12. Authorization Services (Fine-Grained) ❌ **MISSING**
**Priority**: HIGH
**Impact**: Authorization policies and permissions

Missing operations:
- List resources
- Create resource
- Update resource
- Delete resource
- List scopes
- Create scope
- Update scope
- Delete scope
- List policies
- Create policy
- Update policy
- Delete policy
- List permissions
- Create permission
- Update permission
- Delete permission

**Keycloak Admin Client Classes**:
- `AuthorizationResource`
- `ResourcesResource`
- `ScopesResource`
- `PoliciesResource`
- `PermissionsResource`

---

### 13. Role Mappings (Composite & Client Roles) ⚠️ **PARTIAL**
**Priority**: MEDIUM
**Impact**: Complex role hierarchies

Missing operations:
- Get user client-level role mappings
- Add client role to user
- Remove client role from user
- Get available client roles for user
- Get group client-level role mappings
- Add client role to group
- Remove client role from group

**Keycloak Admin Client Classes**:
- `RoleMappingResource`
- `ClientRoleMappingResource`

---

### 14. Partial Updates ❌ **MISSING**
**Priority**: LOW
**Impact**: Efficiency for large representations

Missing operations:
- Partial user update (PATCH-like)
- Partial client update
- Partial realm update

---

### 15. Organizations ❌ **MISSING**
**Priority**: LOW
**Impact**: Newer feature for multi-tenancy (Keycloak 24+)

Missing operations:
- List organizations
- Create organization
- Update organization
- Delete organization
- Get organization members
- Add member to organization

**Keycloak Admin Client Classes**:
- `OrganizationResource` (newer versions)

---

### 16. User Profile Configuration ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Declarative user attributes (Keycloak 15+)

Missing operations:
- Get user profile configuration
- Update user profile configuration

**Keycloak Admin Client Classes**:
- `UserProfileResource`

---

### 17. Client Registration Policies ❌ **MISSING**
**Priority**: LOW
**Impact**: Dynamic client registration

Missing operations:
- List client registration policies
- Get client registration policy
- Update client registration policy

---

### 18. Token Management ❌ **MISSING**
**Priority**: MEDIUM
**Impact**: Token revocation

Missing operations:
- Revoke token
- Introspect token
- Push revocation

---

## Implementation Priority Matrix

### High Priority (Critical for production use)
1. **Client Scopes** - Essential for OIDC configurations
2. **User Sessions Management** - Required for security operations
3. **Components/User Federation** - LDAP/Active Directory integration
4. **Authorization Services** - Fine-grained permissions
5. **Expose existing service methods** - Quick wins (26 methods)

### Medium Priority (Important for complete coverage)
1. **Events Management** - Audit logging
2. **Credential Management** - Enhanced security
3. **Required Actions** - User workflows
4. **Scope Mappings** - Authorization
5. **Role Mappings (Client-level)** - Complete role management
6. **User Profile Configuration** - Modern attribute management
7. **Attack Detection** - Security monitoring

### Low Priority (Nice to have)
1. **Client Policies & Profiles** - Advanced scenarios
2. **Localization** - Multi-language deployments
3. **Organizations** - Newer feature, limited adoption
4. **Partial Updates** - Optimization
5. **Client Registration Policies** - Dynamic registration scenarios

---

## Recommendations

### Phase 1: Quick Wins (Estimated: 1-2 weeks)
**Goal**: Expose all existing service methods

1. Add missing operations to `KeycloakOperation` enum:
   - UPDATE_REALM, DELETE_REALM, SET_REALM_ENABLED
   - UPDATE_CLIENT, GET_CLIENT_SECRET, GET_SERVICE_ACCOUNT_USER
   - GET_CLIENT_PROTOCOL_MAPPERS, ADD_PROTOCOL_MAPPER_TO_CLIENT
   - CREATE_REALM_ROLE, UPDATE_REALM_ROLE, DELETE_REALM_ROLE
   - GET_ROLE_COMPOSITES, ADD_COMPOSITE_TO_ROLE, REMOVE_COMPOSITE_FROM_ROLE
   - GET_GROUP, GET_GROUP_ROLES, ADD_ROLE_TO_GROUP, REMOVE_ROLE_FROM_GROUP
   - CREATE_IDP, UPDATE_IDP, DELETE_IDP, CREATE_IDP_MAPPER
   - GET_REALM_EVENTS_CONFIG, UPDATE_REALM_EVENTS_CONFIG

2. Add corresponding case statements in `KeycloakTool.executeKeycloakOperation()`

**Impact**: +26 operations (72 total)

### Phase 2: Client Scopes (Estimated: 1 week)
**Goal**: Complete OIDC/OAuth2 configuration support

1. Create `ClientScopeService.java`
2. Implement operations:
   - List, Get, Create, Update, Delete client scopes
   - Manage protocol mappers for scopes
   - Add/remove scopes to clients (default/optional)
3. Add to `KeycloakTool` enum and switch statement

**Impact**: +12-15 operations (~87 total)

### Phase 3: User Sessions & Security (Estimated: 2 weeks)
**Goal**: Session management and security monitoring

1. Create `SessionService.java` for:
   - User session management
   - Offline sessions
   - Logout operations
   - Consent management

2. Create `SecurityService.java` for:
   - Attack detection
   - Login failure management

**Impact**: +10-12 operations (~99 total)

### Phase 4: Components & User Federation (Estimated: 2-3 weeks)
**Goal**: LDAP/AD integration

1. Create `ComponentService.java`
2. Implement:
   - Component CRUD operations
   - User storage provider management
   - LDAP connection testing
   - User synchronization

**Impact**: +8-10 operations (~109 total)

### Phase 5: Events & Credentials (Estimated: 1-2 weeks)
**Goal**: Audit logging and credential management

1. Create `EventService.java`
2. Create `CredentialService.java`
3. Implement event queries and credential operations

**Impact**: +10-12 operations (~121 total)

### Phase 6: Authorization Services (Estimated: 3-4 weeks)
**Goal**: Fine-grained authorization

1. Create `AuthorizationService.java`
2. Implement resources, scopes, policies, permissions
3. This is a large API surface area

**Impact**: +20-25 operations (~146 total)

### Phase 7: Advanced Features (Estimated: 2-3 weeks)
**Goal**: Complete API coverage

1. Scope mappings
2. User profile configuration
3. Required actions
4. Token management
5. Localization

**Impact**: +15-20 operations (~166 total)

---

## Testing Considerations

For each new operation, ensure:
1. Unit tests for service methods
2. Integration tests with real Keycloak instance
3. Error handling for common scenarios (not found, unauthorized, etc.)
4. Documentation updates

---

## Architecture Improvements

### Consider implementing:

1. **Operation Factory Pattern** (as noted in KeycloakTool.java comments):
   ```java
   // Lines 154-160 of KeycloakTool.java suggest this improvement
   Map<KeycloakOperation, OperationExecutor> operations;
   interface OperationExecutor {
       String execute(JsonNode params);
   }
   ```
   This would reduce the large switch statement and improve maintainability.

2. **Parameter Validation Framework**:
   - Validate required parameters before execution
   - Provide better error messages for missing/invalid params

3. **Response Standardization**:
   - Consistent JSON response format
   - Error codes and messages
   - Success indicators

4. **Pagination Support**:
   - Many list operations should support pagination
   - Add `first`, `max` parameters

5. **Bulk Operations**:
   - Batch user creation
   - Bulk role assignments
   - Mass updates

---

## Metrics Summary

| Category | Current | Available (Service) | Total Needed | Coverage % |
|----------|---------|---------------------|--------------|------------|
| User Management | 15 | 15 | 20 | 75% |
| Realm Management | 3 | 8 | 12 | 25% |
| Client Management | 8 | 14 | 25 | 32% |
| Role Management | 2 | 8 | 12 | 17% |
| Group Management | 6 | 10 | 12 | 50% |
| Identity Providers | 3 | 8 | 15 | 20% |
| Authentication | 6 | 6 | 10 | 60% |
| Client Scopes | 0 | 0 | 15 | 0% |
| Sessions | 0 | 0 | 10 | 0% |
| Events | 0 | 0 | 8 | 0% |
| Components | 0 | 0 | 10 | 0% |
| Authorization | 0 | 0 | 25 | 0% |
| Credentials | 0 | 0 | 8 | 0% |
| Other | 1 | 1 | 18 | 6% |
| **TOTAL** | **46** | **72** | **200** | **23%** |

---

## Conclusion

The Keycloak MCP Server has solid foundational coverage (46 operations) with well-implemented user, group, and authentication flow management. However, to achieve comprehensive Keycloak Admin API coverage, approximately **154 additional operations** need to be implemented.

**Key findings:**
- 26 operations exist in service classes but aren't exposed (quick win)
- Major gaps in client scopes, sessions, events, and authorization services
- Current coverage: ~23% of full Keycloak Admin API
- With all phases: ~83% coverage (166/200 operations)

**Next steps:**
1. Start with Phase 1 (expose existing methods) - immediate value
2. Prioritize Client Scopes and Sessions (Phases 2-3) - high business value
3. Continue with Components/Federation (Phase 4) - enterprise requirement
4. Evaluate need for Phases 5-7 based on user feedback and use cases

---

**Document Version**: 1.0
**Last Updated**: 2026-03-09
**Author**: Gap Analysis Tool
