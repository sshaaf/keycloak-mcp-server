package dev.shaaf.keycloak.mcp.server.commands.requiredaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import dev.shaaf.keycloak.mcp.server.KeycloakOperation;
import dev.shaaf.keycloak.mcp.server.commands.AbstractCommand;
import dev.shaaf.keycloak.mcp.server.commands.RegisteredCommand;
import dev.shaaf.keycloak.mcp.server.requiredaction.RequiredActionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
@RegisteredCommand
public class ExecuteActionsEmailCommand extends AbstractCommand {

    @Inject
    RequiredActionService requiredActionService;

    @Override
    public KeycloakOperation getOperation() {
        return KeycloakOperation.EXECUTE_ACTIONS_EMAIL;
    }

    @Override
    public String[] getRequiredParams() {
        return new String[]{"realm", "userId", "actions"};
    }

    @Override
    public String getDescription() {
        return "Send email to the user to execute the listed required actions";
    }

    @Override
    public String execute(JsonNode params) throws Exception {
        JsonNode node = params.get("actions");
        if (node == null || !node.isArray()) {
            throw new io.quarkiverse.mcp.server.ToolCallException("actions must be a JSON array of strings");
        }
        List<String> actions = mapper.convertValue(node, new TypeReference<List<String>>() { });
        return requiredActionService.executeActionsEmail(
                requireString(params, "realm"), requireString(params, "userId"), actions);
    }
}
