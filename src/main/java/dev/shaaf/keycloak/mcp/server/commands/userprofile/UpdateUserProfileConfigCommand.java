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
public class UpdateUserProfileConfigCommand extends AbstractCommand {

    @Inject
    UserProfileConfigService userProfileConfigService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.UPDATE_USER_PROFILE_CONFIG;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "config"};
    }

    @Override
    public String getDescription() {
        return "Update declarative user profile (UPConfig JSON in config)";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return userProfileConfigService.updateConfiguration(
                requireString(params, "realm"),
                extractObject(params, "config", UPConfig.class));
    }
}
