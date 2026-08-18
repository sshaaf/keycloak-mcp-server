package dev.shaaf.keycloak.mcp.server.authz;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Fine-grained authorization (UMA) for a confidential client with authorization enabled.
 */
@ApplicationScoped
public class AuthorizationAdminService {

    @Inject
    KeycloakClientFactory clientFactory;

    private AuthorizationResource authz(String realm, String clientInternalId) {
        Keycloak k = clientFactory.createClient();
        return k.realm(realm).clients().get(clientInternalId).authorization();
    }

    public ResourceServerRepresentation getResourceServer(String realm, String clientInternalId) {
        try {
            return authz(realm, clientInternalId).getSettings();
        } catch (Exception e) {
            Log.error("getResourceServer " + clientInternalId, e);
            return null;
        }
    }

    public String updateResourceServer(String realm, String clientInternalId, ResourceServerRepresentation rep) {
        try {
            authz(realm, clientInternalId).update(rep);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<ResourceRepresentation> listResources(String realm, String clientInternalId) {
        try {
            return authz(realm, clientInternalId).resources().resources();
        } catch (Exception e) {
            Log.error("listResources " + clientInternalId, e);
            return Collections.emptyList();
        }
    }

    public ResourceRepresentation getResource(String realm, String clientInternalId, String resourceId) {
        try {
            return authz(realm, clientInternalId).resources().resource(resourceId).toRepresentation();
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String createResource(String realm, String clientInternalId, ResourceRepresentation r) {
        try (Response resp = authz(realm, clientInternalId).resources().create(r)) {
            if (resp.getStatus() / 100 == 2) {
                return "Created, HTTP " + resp.getStatus();
            }
            return "HTTP " + resp.getStatus() + (resp.hasEntity() ? " " + resp.readEntity(String.class) : "");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateResource(String realm, String clientInternalId, String resourceId, ResourceRepresentation r) {
        try {
            authz(realm, clientInternalId).resources().resource(resourceId).update(r);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteResource(String realm, String clientInternalId, String resourceId) {
        try {
            authz(realm, clientInternalId).resources().resource(resourceId).remove();
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<ScopeRepresentation> listScopes(String realm, String clientInternalId) {
        try {
            return authz(realm, clientInternalId).scopes().scopes();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public ScopeRepresentation getScope(String realm, String clientInternalId, String scopeId) {
        try {
            return authz(realm, clientInternalId).scopes().scope(scopeId).toRepresentation();
        } catch (NotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String createScope(String realm, String clientInternalId, ScopeRepresentation s) {
        try (Response resp = authz(realm, clientInternalId).scopes().create(s)) {
            if (resp.getStatus() / 100 == 2) {
                return "Created, HTTP " + resp.getStatus();
            }
            return "HTTP " + resp.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateScope(String realm, String clientInternalId, String scopeId, ScopeRepresentation s) {
        try {
            authz(realm, clientInternalId).scopes().scope(scopeId).update(s);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteScope(String realm, String clientInternalId, String scopeId) {
        try {
            authz(realm, clientInternalId).scopes().scope(scopeId).remove();
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<PolicyRepresentation> listPolicies(String realm, String clientInternalId) {
        try {
            return authz(realm, clientInternalId).policies().policies();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public PolicyRepresentation getPolicy(String realm, String clientInternalId, String policyId) {
        try {
            return authz(realm, clientInternalId).policies().policy(policyId).toRepresentation();
        } catch (Exception e) {
            return null;
        }
    }

    public String createPolicy(String realm, String clientInternalId, PolicyRepresentation p) {
        try (Response resp = authz(realm, clientInternalId).policies().create(p)) {
            if (resp.getStatus() / 100 == 2) {
                return "Created, HTTP " + resp.getStatus();
            }
            return "HTTP " + resp.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updatePolicy(String realm, String clientInternalId, String policyId, PolicyRepresentation p) {
        try {
            authz(realm, clientInternalId).policies().policy(policyId).update(p);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deletePolicy(String realm, String clientInternalId, String policyId) {
        try {
            authz(realm, clientInternalId).policies().policy(policyId).remove();
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public ResourcePermissionRepresentation getResourcePermission(
            String realm, String clientInternalId, String permissionId) {
        try {
            return authz(realm, clientInternalId).permissions().resource().findById(permissionId)
                    .toRepresentation();
        } catch (Exception e) {
            return null;
        }
    }

    public String createResourcePermission(
            String realm, String clientInternalId, ResourcePermissionRepresentation p) {
        try (Response resp = authz(realm, clientInternalId).permissions().resource().create(p)) {
            return "HTTP " + resp.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateResourcePermission(
            String realm, String clientInternalId, String permissionId, ResourcePermissionRepresentation p) {
        try {
            authz(realm, clientInternalId).permissions().resource().findById(permissionId).update(p);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteResourcePermission(String realm, String clientInternalId, String permissionId) {
        try {
            authz(realm, clientInternalId).permissions().resource().findById(permissionId).remove();
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<ScopePermissionRepresentation> listScopePermissions(
            String realm, String clientInternalId, String nameFilter, String resourceId, String scopeId,
            Integer first, Integer max) {
        try {
            return authz(realm, clientInternalId).permissions().scope()
                    .findAll(nameFilter, resourceId, scopeId, first, max);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public ScopePermissionRepresentation getScopePermission(
            String realm, String clientInternalId, String permissionId) {
        try {
            return authz(realm, clientInternalId).permissions().scope().findById(permissionId).toRepresentation();
        } catch (Exception e) {
            return null;
        }
    }

    public String createScopePermission(String realm, String clientInternalId, ScopePermissionRepresentation p) {
        try (Response resp = authz(realm, clientInternalId).permissions().scope().create(p)) {
            return "HTTP " + resp.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateScopePermission(
            String realm, String clientInternalId, String permissionId, ScopePermissionRepresentation p) {
        try {
            authz(realm, clientInternalId).permissions().scope().findById(permissionId).update(p);
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteScopePermission(String realm, String clientInternalId, String permissionId) {
        try {
            authz(realm, clientInternalId).permissions().scope().findById(permissionId).remove();
            return "OK";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
