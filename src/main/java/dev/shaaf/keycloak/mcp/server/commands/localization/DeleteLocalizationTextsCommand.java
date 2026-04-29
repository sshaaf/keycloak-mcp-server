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
public class DeleteLocalizationTextsCommand extends AbstractCommand {

    @Inject
    LocalizationService localizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_LOCALIZATION_TEXTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "locale"};
    }

    @Override
    public String getDescription() {
        return "Delete all custom translations for a locale in the realm";
    }

    @Override
    public String execute(JsonNode params) {
        localizationService.deleteRealmLocalizationTexts(
                requireString(params, "realm"), requireString(params, "locale"));
        return "OK";
    }
}
