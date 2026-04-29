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
public class GetLocalizationTextsCommand extends AbstractCommand {

    @Inject
    LocalizationService localizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.GET_LOCALIZATION_TEXTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "locale"};
    }

    @Override
    public String getDescription() {
        return "Get all translation key/value for a locale (useRealmFallback optional).";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        if (params.has("useRealmFallback") && !params.get("useRealmFallback").isNull()) {
            return toJson(localizationService.getRealmLocalizationTexts(
                    requireString(params, "realm"),
                    requireString(params, "locale"),
                    params.get("useRealmFallback").asBoolean()));
        }
        return toJson(localizationService.getRealmLocalizationTexts(
                requireString(params, "realm"), requireString(params, "locale")));
    }
}
