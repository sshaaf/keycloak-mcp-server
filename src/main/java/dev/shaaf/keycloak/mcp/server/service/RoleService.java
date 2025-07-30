package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Service for managing Keycloak roles
 */
@ApplicationScoped
public class RoleService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all realm roles
     * @param realm The realm to get roles from
     * @return List of all realm roles
     */
    public List<RoleRepresentation> getRealmRoles(String realm) {
        try {
            return keycloak.realm(realm).roles().list();
        } catch (Exception e) {
            Log.error("Failed to get realm roles: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific realm role
     * @param realm The realm where the role resides
     * @param roleName The name of the role
     * @return The role representation or null if not found
     */
    public RoleRepresentation getRealmRole(String realm, String roleName) {
        try {
            return keycloak.realm(realm).roles().get(roleName).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Role not found: " + roleName, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get role: " + roleName, e);
            return null;
        }
    }

    /**
     * Create a realm role
     * @param realm The realm where the role will be created
     * @param roleName The name of the role
     * @param description The description of the role
     * @return Success or error message
     */
    public String createRealmRole(String realm, String roleName, String description) {
        try {
            RoleRepresentation role = new RoleRepresentation();
            role.setName(roleName);
            role.setDescription(description);
            
            RolesResource rolesResource = keycloak.realm(realm).roles();
            rolesResource.create(role);
            
            return "Successfully created role: " + roleName;
        } catch (Exception e) {
            Log.error("Failed to create role: " + roleName, e);
            return "Error creating role: " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Update a realm role
     * @param realm The realm where the role resides
     * @param roleName The name of the role
     * @param roleRepresentation The updated role representation
     * @return Success or error message
     */
    public String updateRealmRole(String realm, String roleName, RoleRepresentation roleRepresentation) {
        try {
            RoleResource roleResource = keycloak.realm(realm).roles().get(roleName);
            roleResource.update(roleRepresentation);
            return "Successfully updated role: " + roleName;
        } catch (NotFoundException e) {
            return "Role not found: " + roleName;
        } catch (Exception e) {
            Log.error("Failed to update role: " + roleName, e);
            return "Error updating role: " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Delete a realm role
     * @param realm The realm where the role resides
     * @param roleName The name of the role
     * @return Success or error message
     */
    public String deleteRealmRole(String realm, String roleName) {
        try {
            keycloak.realm(realm).roles().deleteRole(roleName);
            return "Successfully deleted role: " + roleName;
        } catch (NotFoundException e) {
            return "Role not found: " + roleName;
        } catch (Exception e) {
            Log.error("Failed to delete role: " + roleName, e);
            return "Error deleting role: " + roleName + " - " + e.getMessage();
        }
    }

    /**
     * Get role composites
     * @param realm The realm where the role resides
     * @param roleName The name of the role
     * @return List of composite roles or empty list if not found
     */
    public List<RoleRepresentation> getRoleComposites(String realm, String roleName) {
        try {
            Set<RoleRepresentation> composites = keycloak.realm(realm).roles().get(roleName).getRoleComposites();
            return composites != null ? new ArrayList<>(composites) : Collections.emptyList();
        } catch (NotFoundException e) {
            Log.error("Role not found: " + roleName, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get role composites: " + roleName, e);
            return Collections.emptyList();
        }
    }

    /**
     * Add composite to role
     * @param realm The realm where the roles reside
     * @param roleName The name of the role
     * @param compositeRoleName The name of the composite role to add
     * @return Success or error message
     */
    public String addCompositeToRole(String realm, String roleName, String compositeRoleName) {
        try {
            // Get the role to add as composite
            RoleRepresentation compositeRole = getRealmRole(realm, compositeRoleName);
            if (compositeRole == null) {
                return "Composite role not found: " + compositeRoleName;
            }
            
            // Add the composite role
            keycloak.realm(realm).roles().get(roleName).addComposites(List.of(compositeRole));
            
            return "Successfully added composite role: " + roleName + " -> " + compositeRoleName;
        } catch (NotFoundException e) {
            return "Role not found: " + roleName;
        } catch (Exception e) {
            Log.error("Failed to add composite role: " + roleName + " -> " + compositeRoleName, e);
            return "Error adding composite role: " + roleName + " -> " + compositeRoleName + " - " + e.getMessage();
        }
    }

    /**
     * Remove composite from role
     * @param realm The realm where the roles reside
     * @param roleName The name of the role
     * @param compositeRoleName The name of the composite role to remove
     * @return Success or error message
     */
    public String removeCompositeFromRole(String realm, String roleName, String compositeRoleName) {
        try {
            // Get the role to remove from composites
            RoleRepresentation compositeRole = getRealmRole(realm, compositeRoleName);
            if (compositeRole == null) {
                return "Composite role not found: " + compositeRoleName;
            }
            
            // Remove the composite role
            keycloak.realm(realm).roles().get(roleName).deleteComposites(List.of(compositeRole));
            
            return "Successfully removed composite role: " + roleName + " -> " + compositeRoleName;
        } catch (NotFoundException e) {
            return "Role not found: " + roleName;
        } catch (Exception e) {
            Log.error("Failed to remove composite role: " + roleName + " -> " + compositeRoleName, e);
            return "Error removing composite role: " + roleName + " -> " + compositeRoleName + " - " + e.getMessage();
        }
    }
}