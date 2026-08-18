package dev.shaaf.keycloak.mcp.server.commands.localization;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.localization.LocalizationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class GetRealmLocalesCommand extends AbstractCommand {

    @Inject
    LocalizationService localizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_REALM_LOCALES;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm"};
    }

    @Override
    public String getDescription() {
        return "List custom locales configured for the realm (realm-specific overrides).";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        return toJson(localizationService.getRealmSpecificLocales(requireString(params, "realm")));
    }
}
