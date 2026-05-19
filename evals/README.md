# Evaluations for Keycloak MCP prompts

Two complementary approaches cover `prompts.txt` (178 operations) without duplicating business logic in ad hoc strings.

## 1. Contract coverage (CI, no LLM)

**Goal:** The documented prompts file stays aligned with the Java enum and command wiring.

- **Test:** `PromptsFileCoverageTest` (`src/test/java/.../evals/PromptsFileCoverageTest.java`) — **plain JUnit 5** (no `@QuarkusTest`, no port/Docker).
  - Parses `## OPERATION` lines from the repo root `prompts.txt`.
  - Asserts every name is a valid `KeycloakOperation` and that the set of names matches `KeycloakOperation.values()` exactly (order may follow the file).
  - **Registry wiring** is not repeated here: use `AllKeycloakOperationsRegisteredTest` for “every enum has a command when enabled.”

**Run:** from the `keycloak-mcp-server` module (directory containing `pom.xml`):

```bash
mvn -q test -Dtest=PromptsFileCoverageTest
```

**Regenerate `prompts.txt` after enum/command changes:**

```bash
python3 scripts/generate-prompts-txt.py
```

This is the right default “eval” for a library: **full coverage of every line in `prompts.txt` on every `mvn test`**.

## 2. LLM / agent evals (optional, API keys)

**Goal:** Measure whether a model (or an agent) maps **natural language** to the right **operation** and **parameters**—for example when users do not type enum names.

**Data:** Generate a machine-readable dataset from the same `prompts.txt` single source of truth:

```bash
python3 scripts/prompts-to-eval-dataset.py -o evals/dataset.jsonl
```

Each JSONL line looks like:

```json
{"operation": "GET_USERS", "prompt": "List all users in a realm", "requiredKeys": ["realm"]}
```

**Ways to run real LLM evals:**

| Approach | Notes |
|----------|--------|
| **Promptfoo** | Install with npm; point tests at `evals/dataset.jsonl` and a prompt that simulates the user, then assert the model (or tool call) returns the expected `operation` / param keys. Good for regression across model versions. |
| **OpenAI / Anthropic evals** | Use their Evals SDK or a small script: for each row, call the model with the `prompt` field, compare structured output to `operation` and `requiredKeys`. |
| **E2E against MCP** | Start the server (or `curl` the MCP tool), run an agent with natural language, assert the `executeKeycloakOperation` `operation` argument matches the golden `operation` from the dataset. Slower, highest fidelity. |

**Suggested metric:** **operation accuracy** (exact match on `KeycloakOperation` name) first; add **param-key F1** or required-key presence if you have golden JSON for each op.

**Cost / CI:** LLM evals are usually **nightly** or **manual** because of cost and flakiness; keep **PromptsFileCoverageTest** in every PR.

## 3. Dokimos (Java, optional Maven profile)

**Goal:** In-repo regression that a **live LLM** selects the right Keycloak admin operation and JSON parameters for natural-language prompts, using the same admin semantics as production (`KeycloakTool`). Dokimos also runs an optional **LLM-as-judge** scorer on the assistant’s final text.

**Constraints:**

- **Not part of default CI:** plain `mvn test` excludes tests tagged `dokimos` and does **not** compile Dokimos-specific sources (they live under `src/dokimos/` and activate only with the profile below).
- **Requires API keys:** tests are skipped unless `OPENAI_API_KEY` is set (JUnit `@EnabledIfEnvironmentVariable`).
- **LangChain4j tool name:** the eval uses `invokeKeycloakOperation` as the LangChain4j `@Tool` method name so it does not collide with the MCP-registered `executeKeycloakOperation` tool from `KeycloakTool` at Quarkus build time.

**Run:**

```bash
cd keycloak-mcp-server   # directory containing pom.xml
export OPENAI_API_KEY=...   # required
# optional: export DOKIMOS_OPENAI_MODEL=gpt-4o-mini

mvn test -Pdokimos
```

**Dataset:** [`src/dokimos/resources/dokimos/keycloak-tool-use.json`](../src/dokimos/resources/dokimos/keycloak-tool-use.json) mirrors **[`evals/dataset.jsonl`](dataset.jsonl)** for a **representative subset** of operations (natural-language `input`, expected `invokeKeycloakOperation` + params, optional `metadata.operation` / `requiredKeys`). Realm-scoped rows use **`master`** unless you customize JSON. The JSONL file lists **all** operations; the Dokimos JSON intentionally stays small for cost and stability.

**Implementation sketch:** [`src/dokimos/java/.../eval/dokimos/`](../src/dokimos/java/dev/shaaf/keycloak/mcp/server/eval/dokimos/) (`KeycloakToolInvoker`, `KeycloakToolUseDokimosTest`).

## 4. Relationship to `AllKeycloakOperationsRegisteredTest`

`AllKeycloakOperationsRegisteredTest` already ensures every `KeycloakOperation` has a command. `PromptsFileCoverageTest` adds: **the human-facing `prompts.txt` must stay complete and in sync** with the enum, which those tests do not check.

## Files

| File | Role |
|------|------|
| `prompts.txt` | Human-readable; regenerate with `scripts/generate-prompts-txt.py` |
| `scripts/prompts-to-eval-dataset.py` | `prompts.txt` → `evals/dataset.jsonl` |
| `evals/dataset.jsonl` | **Generated** — add to `.gitignore` or commit, team choice |
| `src/test/.../PromptsFileCoverageTest.java` | CI coverage of all prompts |
| `src/dokimos/` | Opt-in Dokimos + LangChain4j sources and datasets (`mvn test -Pdokimos`) |
