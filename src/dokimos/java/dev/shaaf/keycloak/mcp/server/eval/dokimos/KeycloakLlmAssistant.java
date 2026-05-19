package dev.shaaf.keycloak.mcp.server.eval.dokimos;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * LangChain4j AI Service used only by opt-in Dokimos evals ({@code mvn test -Pdokimos}).
 */
public interface KeycloakLlmAssistant {

    @SystemMessage(
            """
                    You help administrators manage Keycloak via tools.
                    When the user asks for information or changes in Keycloak, call invokeKeycloakOperation (the only tool).
                    The operation argument must be an exact KeycloakOperation enum constant (e.g. GET_REALMS, GET_REALM).
                    The params argument must be one JSON object serialized as a string; use {} when no parameters are required.
                    Prefer a single correct tool call.""")
    String chat(@UserMessage String userMessage);
}
