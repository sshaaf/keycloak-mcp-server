package dev.shaaf.keycloak.mcp.server.commands.userprofile;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.userprofile.UserProfileConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.representations.userprofile.config.UPConfig;

@ApplicationScoped
@RegisteredCommand
public class GetUserProfileConfigCommand extends AbstractCommand {

    @Inject
    UserProfileConfigService userProfileConfigService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_USER_PROFILE_CONFIG;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "Get declarative user profile (UPConfig) for the realm";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        UPConfig c = userProfileConfigService.getConfiguration(requireString(params, "realm"));
        if (c == null) {
            return "null";
        }
        return toJson(c);
    }
}
