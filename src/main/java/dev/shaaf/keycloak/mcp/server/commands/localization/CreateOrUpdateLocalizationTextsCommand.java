package dev.shaaf.keycloak.mcp.server.commands.localization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.localization.LocalizationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
@RegisteredCommand
public class CreateOrUpdateLocalizationTextsCommand extends AbstractCommand {

    @Inject
    LocalizationService localizationService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.CREATE_OR_UPDATE_LOCALIZATION_TEXTS;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "locale", "texts"};
    }

    @Override
    public String getDescription() {
        return "Replace or merge bulk translations (texts: JSON object of key to string).";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        Map<String, String> texts = mapper.convertValue(
                params.get("texts"), new TypeReference<Map<String, String>>() { });
        localizationService.createOrUpdateRealmLocalizationTexts(
                requireString(params, "realm"),
                requireString(params, "locale"),
                texts);
        return "OK";
    }
}
