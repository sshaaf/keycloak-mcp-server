package dev.shaaf.keycloak.mcp.server.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;

import java.util.Collections;
import java.util.List;

/**
 * Service for managing Keycloak identity providers
 */
@ApplicationScoped
public class IdentityProviderService {

    @Inject
    Keycloak keycloak;

    /**
     * Get all identity providers
     * @param realm The realm to get identity providers from
     * @return List of all identity providers
     */
    public List<IdentityProviderRepresentation> getIdentityProviders(String realm) {
        try {
            return keycloak.realm(realm).identityProviders().findAll();
        } catch (Exception e) {
            Log.error("Failed to get identity providers: " + realm, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get a specific identity provider
     * @param realm The realm where the identity provider resides
     * @param alias The alias of the identity provider
     * @return The identity provider representation or null if not found
     */
    public IdentityProviderRepresentation getIdentityProvider(String realm, String alias) {
        try {
            return keycloak.realm(realm).identityProviders().get(alias).toRepresentation();
        } catch (NotFoundException e) {
            Log.error("Identity provider not found: " + alias, e);
            return null;
        } catch (Exception e) {
            Log.error("Failed to get identity provider: " + alias, e);
            return null;
        }
    }

    /**
     * Create an identity provider
     * @param realm The realm where the identity provider will be created
     * @param identityProvider The identity provider representation
     * @return Success or error message
     */
    public String createIdentityProvider(String realm, IdentityProviderRepresentation identityProvider) {
        try {
            Response response = keycloak.realm(realm).identityProviders().create(identityProvider);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created identity provider: " + identityProvider.getAlias();
            } else {
                Log.error("Failed to create identity provider. Status: " + response.getStatus());
                response.close();
                return "Error creating identity provider: " + identityProvider.getAlias();
            }
        } catch (Exception e) {
            Log.error("Failed to create identity provider: " + identityProvider.getAlias(), e);
            return "Error creating identity provider: " + identityProvider.getAlias() + " - " + e.getMessage();
        }
    }

    /**
     * Update an identity provider
     * @param realm The realm where the identity provider resides
     * @param alias The alias of the identity provider
     * @param identityProvider The updated identity provider representation
     * @return Success or error message
     */
    public String updateIdentityProvider(String realm, String alias, IdentityProviderRepresentation identityProvider) {
        try {
            IdentityProviderResource idpResource = keycloak.realm(realm).identityProviders().get(alias);
            idpResource.update(identityProvider);
            return "Successfully updated identity provider: " + alias;
        } catch (NotFoundException e) {
            return "Identity provider not found: " + alias;
        } catch (Exception e) {
            Log.error("Failed to update identity provider: " + alias, e);
            return "Error updating identity provider: " + alias + " - " + e.getMessage();
        }
    }

    /**
     * Delete an identity provider
     * @param realm The realm where the identity provider resides
     * @param alias The alias of the identity provider
     * @return Success or error message
     */
    public String deleteIdentityProvider(String realm, String alias) {
        try {
            // Check if identity provider exists before attempting to delete
            IdentityProviderRepresentation idp = getIdentityProvider(realm, alias);
            if (idp == null) {
                return "Identity provider not found: " + alias;
            }
            
            keycloak.realm(realm).identityProviders().get(alias).remove();
            return "Successfully deleted identity provider: " + alias;
        } catch (NotFoundException e) {
            return "Identity provider not found: " + alias;
        } catch (Exception e) {
            Log.error("Failed to delete identity provider: " + alias, e);
            return "Error deleting identity provider: " + alias + " - " + e.getMessage();
        }
    }

    /**
     * Get identity provider mappers
     * @param realm The realm where the identity provider resides
     * @param alias The alias of the identity provider
     * @return List of identity provider mappers or empty list if not found
     */
    public List<IdentityProviderMapperRepresentation> getIdentityProviderMappers(String realm, String alias) {
        try {
            return keycloak.realm(realm).identityProviders().get(alias).getMappers();
        } catch (NotFoundException e) {
            Log.error("Identity provider not found: " + alias, e);
            return Collections.emptyList();
        } catch (Exception e) {
            Log.error("Failed to get identity provider mappers: " + alias, e);
            return Collections.emptyList();
        }
    }

    /**
     * Create identity provider mapper
     * @param realm The realm where the identity provider resides
     * @param alias The alias of the identity provider
     * @param mapper The identity provider mapper representation
     * @return Success or error message
     */
    public String createIdentityProviderMapper(String realm, String alias, IdentityProviderMapperRepresentation mapper) {
        try {
            Response response = keycloak.realm(realm).identityProviders().get(alias).addMapper(mapper);
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return "Successfully created identity provider mapper: " + mapper.getName();
            } else {
                Log.error("Failed to create identity provider mapper. Status: " + response.getStatus());
                response.close();
                return "Error creating identity provider mapper: " + mapper.getName();
            }
        } catch (NotFoundException e) {
            return "Identity provider not found: " + alias;
        } catch (Exception e) {
            Log.error("Failed to create identity provider mapper: " + mapper.getName(), e);
            return "Error creating identity provider mapper: " + mapper.getName() + " - " + e.getMessage();
        }
    }
}