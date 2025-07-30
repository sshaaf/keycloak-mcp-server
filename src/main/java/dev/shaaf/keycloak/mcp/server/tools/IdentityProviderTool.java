package dev.shaaf.keycloak.mcp.server.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shaaf.keycloak.mcp.server.service.IdentityProviderService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class IdentityProviderTool {

    @Inject
    IdentityProviderService identityProviderService;

    @Inject
    ObjectMapper mapper;

    @Tool(description = "Get all identity providers from a keycloak realm")
    String getIdentityProviders(@ToolArg(description = "A String denoting the name of the realm") String realm) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProviders(realm));
        } catch (Exception e) {
            Log.error("Failed to get identity providers: " + realm, e);
            throw new ToolCallException("Failed to get identity providers: " + realm);
        }
    }

    @Tool(description = "Get a specific identity provider from a keycloak realm")
    String getIdentityProvider(@ToolArg(description = "A String denoting the name of the realm") String realm,
                              @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProvider(realm, alias));
        } catch (Exception e) {
            Log.error("Failed to get identity provider: " + alias, e);
            throw new ToolCallException("Failed to get identity provider: " + alias);
        }
    }

    @Tool(description = "Get identity provider mappers from a keycloak realm")
    String getIdentityProviderMappers(@ToolArg(description = "A String denoting the name of the realm") String realm,
                                     @ToolArg(description = "A String denoting the alias of the identity provider") String alias) {
        try {
            return mapper.writeValueAsString(identityProviderService.getIdentityProviderMappers(realm, alias));
        } catch (Exception e) {
            Log.error("Failed to get identity provider mappers: " + alias, e);
            throw new ToolCallException("Failed to get identity provider mappers: " + alias);
        }
    }
}