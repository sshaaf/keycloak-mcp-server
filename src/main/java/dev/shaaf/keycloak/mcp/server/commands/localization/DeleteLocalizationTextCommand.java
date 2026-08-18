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
public class DeleteLocalizationTextCommand extends AbstractCommand {

    @Inject
    LocalizationService localizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.DELETE_LOCALIZATION_TEXT;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "locale", "key"};
    }

    @Override
    public String getDescription() {
        return "Delete one translation key for a locale";
    }

    @Override
    public String execute(JsonNode params) {
        localizationService.deleteRealmLocalizationText(
                requireString(params, "realm"),
                requireString(params, "locale"),
                requireString(params, "key"));
        return "OK";
    }
}
