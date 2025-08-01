package dev.shaaf.keycloak.mcp.server.tools;

import dev.shaaf.keycloak.mcp.server.service.DocsService;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.inject.Inject;

public class DocsTool {

    @Inject
    DocsService docsService;

    @Tool(description = "This tool is your go-to source for all Keycloak-related knowledge, ensuring you have the information needed to effectively use and integrate Keycloak into your projects.")
    public String getDocs(@ToolArg(description = "Question about Keycloak") String text) {
        return docsService.askAQuestion(text);
    }


}
