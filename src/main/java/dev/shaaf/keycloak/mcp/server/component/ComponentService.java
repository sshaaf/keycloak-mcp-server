package dev.shaaf.keycloak.mcp.server.component;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ComponentRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class ComponentService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<ComponentRepresentation> getComponents(String realm) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).components().query();
        } catch (Exception e) {
            Log.error("Failed to get components: " + realm, e);
            return Collections.emptyList();
        }
    }

    public ComponentRepresentation getComponent(String realm, String componentId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).components().component(componentId).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Component not found: " + componentId, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get component: " + componentId, e);
            return null;
        }
    }

    public String createComponent(String realm, ComponentRepresentation component) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            Response response = keycloak.realm(realm).components().add(component);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created component: " + component.getName();
            } else {
                response.close();
                return "Error creating component: " + component.getName();
            }
        } catch (Exception e) {
            Log.error("Failed to create component: " + component.getName(), e);
            return "Error creating component: " + component.getName() + " - " + e.getMessage();
        }
    }

    public String updateComponent(String realm, String componentId, ComponentRepresentation component) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).components().component(componentId).update(component);
            return "Successfully updated component: " + componentId;
        } catch (NotFoundException e) {
            return "Component not found: " + componentId;
        } catch (Exception e) {
            Log.error("Failed to update component: " + componentId, e);
            return "Error updating component: " + componentId + " - " + e.getMessage();
        }
    }

    public String deleteComponent(String realm, String componentId) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            keycloak.realm(realm).components().component(componentId).remove();
            return "Successfully deleted component: " + componentId;
        } catch (NotFoundException e) {
            return "Component not found: " + componentId;
        } catch (Exception e) {
            Log.error("Failed to delete component: " + componentId, e);
            return "Error deleting component: " + componentId + " - " + e.getMessage();
        }
    }

    public List<ComponentRepresentation> getSubComponents(String realm, String parentId, String type) {
        Keycloak keycloak = clientFactory.createClient();
        try {
            return keycloak.realm(realm).components().query(parentId, type);
        } catch (Exception e) {
            Log.error("Failed to get sub-components: " + parentId, e);
            return Collections.emptyList();
        }
    }
}
