package dev.shaaf.keycloak.mcp.server.organization;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.MemberRepresentation;
import org.keycloak.representations.idm.OrganizationRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class OrganizationService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<OrganizationRepresentation> getOrganizations(String realm) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).organizations().getAll();
        } catch (Exception e) {
            Log.error("getOrganizations " + realm, e);
            return Collections.emptyList();
        }
    }

    public OrganizationRepresentation getOrganization(String realm, String orgId) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).organizations().get(orgId).toRepresentation();
        } catch (Exception e) {
            Log.error("getOrganization " + orgId, e);
            return null;
        }
    }

    public String createOrganization(String realm, OrganizationRepresentation org) {
        try (Response r = clientFactory.createClient().realm(realm).organizations().create(org)) {
            return "HTTP " + r.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String updateOrganization(String realm, String orgId, OrganizationRepresentation org) {
        try (Response r = clientFactory.createClient().realm(realm).organizations().get(orgId).update(org)) {
            if (r.getStatus() / 100 == 2) {
                return "OK, HTTP " + r.getStatus();
            }
            return "HTTP " + r.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String deleteOrganization(String realm, String orgId) {
        try (Response r = clientFactory.createClient().realm(realm).organizations().get(orgId).delete()) {
            if (r.getStatus() / 100 == 2) {
                return "OK, HTTP " + r.getStatus();
            }
            return "HTTP " + r.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public List<MemberRepresentation> getOrganizationMembers(String realm, String orgId) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).organizations().get(orgId).members().getAll();
        } catch (Exception e) {
            Log.error("getOrganizationMembers " + orgId, e);
            return Collections.emptyList();
        }
    }

    public String addOrganizationMember(String realm, String orgId, String userId) {
        try (Response r = clientFactory.createClient().realm(realm).organizations()
                .get(orgId).members().addMember(userId)) {
            return "HTTP " + r.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String removeOrganizationMember(String realm, String orgId, String userId) {
        try (Response r = clientFactory.createClient().realm(realm).organizations()
                .get(orgId).members().removeMember(userId)) {
            return "HTTP " + r.getStatus();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
