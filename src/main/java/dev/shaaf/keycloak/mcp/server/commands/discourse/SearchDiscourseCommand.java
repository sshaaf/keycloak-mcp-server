package dev.shaaf.keycloak.mcp.server.commands.discourse;

import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.discourse.SearchResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@RegisteredCommand
public class SearchDiscourseCommand extends AbstractCommand {

    @Inject
    SearchResource searchResource;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.SEARCH_DISCOURSE;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"query"};
    }

    @Override
    public String getDescription() {
        return "Search Keycloak Discourse forum";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        String query = requireString(params, "query");
        return toJson(searchResource.performSearch(query));
    }
}

