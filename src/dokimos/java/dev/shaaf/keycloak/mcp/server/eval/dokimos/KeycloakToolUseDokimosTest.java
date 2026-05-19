package dev.shaaf.keycloak.mcp.server.eval.dokimos;

import dev.dokimos.core.Dataset;
import dev.dokimos.core.EvalTestCaseParam;
import dev.dokimos.core.Experiment;
import dev.dokimos.core.ExperimentResult;
import dev.dokimos.core.evaluators.LLMJudgeEvaluator;
import dev.dokimos.core.evaluators.agents.ToolCorrectnessEvaluator;
import dev.dokimos.langchain4j.LangChain4jSupport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.shaaf.keycloak.mcp.server.KeycloakTool;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Opt-in Dokimos eval: a live LLM must invoke {@link KeycloakTool} correctly for natural-language prompts.
 * <p>
 * Run: {@code mvn test -Pdokimos} with {@code OPENAI_API_KEY} set. Without the key, this class is skipped.
 * <p>
 * Class name ends with {@code Test} so Maven Surefire default includes match ( {@code *IT} is not included by default).
 */
@QuarkusTest
@Tag("dokimos")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class KeycloakToolUseDokimosTest {

    @Inject
    KeycloakTool keycloakTool;

    @Test
    void naturalLanguageMapsToKeycloakTool() throws Exception {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(System.getenv().getOrDefault("DOKIMOS_OPENAI_MODEL", "gpt-4o-mini"))
                .temperature(0.0)
                .build();

        KeycloakToolInvoker invoker = new KeycloakToolInvoker(keycloakTool);
        KeycloakLlmAssistant assistant = AiServices.builder(KeycloakLlmAssistant.class)
                .chatModel(model)
                .tools(invoker)
                .build();

        Dataset dataset;
        try (InputStream in = KeycloakToolUseDokimosTest.class.getResourceAsStream("/dokimos/keycloak-tool-use.json")) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource dokimos/keycloak-tool-use.json");
            }
            dataset = Dataset.fromJson(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }

        ToolCorrectnessEvaluator toolCorrectness = ToolCorrectnessEvaluator.builder()
                .name("KeycloakTool correctness")
                .matchMode(ToolCorrectnessEvaluator.MatchMode.NAMES_AND_ARGS)
                .threshold(1.0)
                .build();

        LLMJudgeEvaluator responseJudge = LLMJudgeEvaluator.builder()
                .name("Final response quality")
                .criteria(
                        "The Actual Output reasonably addresses the user request (it may summarize JSON). "
                                + "Score 1.0 if acceptable, 0.0 if misleading, wrong domain, or empty where content was expected.")
                .evaluationParams(List.of(EvalTestCaseParam.INPUT, EvalTestCaseParam.ACTUAL_OUTPUT))
                .judge(LangChain4jSupport.asJudge(model))
                .threshold(0.65)
                .build();

        Experiment experiment = Experiment.builder()
                .name("Keycloak MCP LLM tool use")
                .dataset(dataset)
                .task(example -> {
                    invoker.clear();
                    String out = assistant.chat(example.input());
                    return Map.of(
                            "output", out != null ? out : "",
                            "toolCalls", invoker.getRecordedToolCalls());
                })
                .evaluators(List.of(toolCorrectness, responseJudge))
                .build();

        ExperimentResult result = experiment.run();
        assertEquals(1.0, result.passRate(), 0.0001, () -> result.toJson());
    }
}
