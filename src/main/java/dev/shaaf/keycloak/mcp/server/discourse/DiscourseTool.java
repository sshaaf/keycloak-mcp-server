package dev.shaaf.keycloak.mcp.server.discourse;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DiscourseTool {

    @Inject
    SearchResource searchResource;

    @Tool(description = "Search keycloak community discourse for similar issues. When a user is having an issue they might paste a code snippet or want to search how similar people solved it. This tool provides the discourse search via query")
    String search(@ToolArg(description = "Search discource for similar discussions") String query) {
        return searchResource.performSearch(query).toString();
    }
}
