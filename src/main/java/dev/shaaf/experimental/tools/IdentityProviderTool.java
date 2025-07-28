package dev.shaaf.experimental.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.experimental.service.IdentityProviderService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;

import java.util.List;

public class IdentityProviderTool {

    @Inject
    IdentityProviderService identityProviderService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all identity providers in a realm")
    String getIdentityProviders(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProviders(realm));
        } catch (Exception e) {
            Log.error("Failed to get identity providers: " + realm, e);
            throw new ToolCallException("Failed to get identity providers: " + realm);
        }
    }

    @Tool(description = "Get a specific identity provider")
    String getIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                              @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProvider(realm, alias));
        } catch (Exception e) {
            Log.error("Failed to get identity provider: " + alias, e);
            throw new ToolCallException("Failed to get identity provider: " + alias);
        }
    }

    @Tool(description = "Create an identity provider")
    String createIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                 @ToolArg(description = "A String denoting the identity provider representation in JSON format") String idpJson) {
        try {
            IdentityProviderRepresentation identityProvider = mapper.readValue(idpJson, IdentityProviderRepresentation.class);
            return identityProviderService.createIdentityProvider(realm, identityProvider);
        } catch (Exception e) {
            Log.error("Failed to create identity provider", e);
            throw new ToolCallException("Failed to create identity provider - " + e.getMessage());
        }
    }

    @Tool(description = "Update an identity provider")
    String updateIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                 @ToolArg(description = "A String denoting the alias of the identity provider") String alias,
                                 @ToolArg(description = "A String denoting the updated identity provider representation in JSON format") String idpJson) {
        try {
            IdentityProviderRepresentation identityProvider = mapper.readValue(idpJson, IdentityProviderRepresentation.class);
            return identityProviderService.updateIdentityProvider(realm, alias, identityProvider);
        } catch (Exception e) {
            Log.error("Failed to update identity provider: " + alias, e);
            throw new ToolCallException("Failed to update identity provider: " + alias + " - " + e.getMessage());
        }
    }

    @Tool(description = "Delete an identity provider")
    String deleteIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                 @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
        return identityProviderService.deleteIdentityProvider(realm, alias);
    }

    @Tool(description = "Get identity provider mappers")
    String getIdentityProviderMappers(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                     @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProviderMappers(realm, alias));
        } catch (Exception e) {
            Log.error("Failed to get identity provider mappers: " + alias, e);
            throw new ToolCallException("Failed to get identity provider mappers: " + alias);
        }
    }

    @Tool(description = "Create identity provider mapper")
    String createIdentityProviderMapper(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                       @ToolArg(description = "A String denoting the alias of the identity provider") String alias,
                                       @ToolArg(description = "A String denoting the identity provider mapper representation in JSON format") String mapperJson) {
        try {
            IdentityProviderMapperRepresentation mapperRepresentation = mapper.readValue(mapperJson, IdentityProviderMapperRepresentation.class);
            return identityProviderService.createIdentityProviderMapper(realm, alias, mapperRepresentation);
        } catch (Exception e) {
            Log.error("Failed to create identity provider mapper", e);
            throw new ToolCallException("Failed to create identity provider mapper - " + e.getMessage());
        }
    }
}