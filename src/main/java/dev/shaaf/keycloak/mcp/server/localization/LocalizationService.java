package dev.shaaf.keycloak.mcp.server.localization;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.admin.client.Keycloak;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LocalizationService {

    @Inject
    KeycloakClientFactory clientFactory;

    public List<String> getRealmSpecificLocales(String realm) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).localization().getRealmSpecificLocales();
        } catch (Exception e) {
            Log.error("getRealmSpecificLocales " + realm, e);
            return Collections.emptyList();
        }
    }

    public Map<String, String> getRealmLocalizationTexts(String realm, String locale) {
        return getRealmLocalizationTexts(realm, locale, null);
    }

    public Map<String, String> getRealmLocalizationTexts(String realm, String locale, Boolean useRealmFallback) {
        try {
            Keycloak k = clientFactory.createClient();
            if (useRealmFallback == null) {
                return k.realm(realm).localization().getRealmLocalizationTexts(locale);
            }
            return k.realm(realm).localization().getRealmLocalizationTexts(locale, useRealmFallback);
        } catch (Exception e) {
            Log.error("getRealmLocalizationTexts " + realm, e);
            return Collections.emptyMap();
        }
    }

    public String getRealmLocalizationText(String realm, String locale, String key) {
        try {
            Keycloak k = clientFactory.createClient();
            return k.realm(realm).localization().getRealmLocalizationText(locale, key);
        } catch (Exception e) {
            Log.error("getRealmLocalizationText " + realm, e);
            return null;
        }
    }

    public void deleteRealmLocalizationTexts(String realm, String locale) {
        Keycloak k = clientFactory.createClient();
        k.realm(realm).localization().deleteRealmLocalizationTexts(locale);
    }

    public void deleteRealmLocalizationText(String realm, String locale, String key) {
        Keycloak k = clientFactory.createClient();
        k.realm(realm).localization().deleteRealmLocalizationText(locale, key);
    }

    public void saveRealmLocalizationText(String realm, String locale, String key, String text) {
        Keycloak k = clientFactory.createClient();
        k.realm(realm).localization().saveRealmLocalizationText(locale, key, text);
    }

    public void createOrUpdateRealmLocalizationTexts(String realm, String locale, Map<String, String> texts) {
        Keycloak k = clientFactory.createClient();
        k.realm(realm).localization().createOrUpdateRealmLocalizationTexts(locale, texts);
    }
}
