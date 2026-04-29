# Testing Strategy - Keycloak MCP Server

**Project**: Keycloak MCP Server
**Version**: 0.3.0
**Analysis Date**: 2026-03-09

## Current State

### ❌ No Tests Exist
The project currently has **ZERO tests** despite the documentation claiming otherwise:

- **No test directory**: `src/test/java` does not exist
- **No test resources**: `src/test/resources` does not exist
- **No test configuration**: No test-specific `application.properties`
- **CI skips tests**: All GitHub Actions workflows use `-DskipTests`
- **Dev Services disabled**: `quarkus.keycloak.devservices.enabled=false`

### Existing Test Dependencies
The `pom.xml` includes minimal test dependencies:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>
```

### Documentation Discrepancy
`docs/developers.md` lines 49-65 claim:
> "Keycloak MCP Server includes a comprehensive test suite..."

**This is incorrect** - there are no tests in the codebase.

---

## Recommended Testing Strategy

### Phase 1: Test Infrastructure Setup (Week 1)

#### 1.1 Add Required Dependencies

Update `pom.xml` to add comprehensive test dependencies:

```xml
<!-- Test dependencies -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5-mockito</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers for Keycloak integration tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.7</version>
    <scope>test</scope>
</dependency>

<!-- Keycloak Testcontainer -->
<dependency>
    <groupId>com.github.dasniko</groupId>
    <artifactId>testcontainers-keycloak</artifactId>
    <version>3.3.0</version>
    <scope>test</scope>
</dependency>

<!-- Hamcrest matchers -->
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>hamcrest</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ for fluent assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.3</version>
    <scope>test</scope>
</dependency>
```

#### 1.2 Create Test Directory Structure

```
src/test/
├── java/
│   └── dev/
│       └── shaaf/
│           └── keycloak/
│               └── mcp/
│                   └── server/
│                       ├── user/
│                       │   ├── UserServiceTest.java
│                       │   └── UserServiceIntegrationTest.java
│                       ├── realm/
│                       │   ├── RealmServiceTest.java
│                       │   └── RealmServiceIntegrationTest.java
│                       ├── client/
│                       │   ├── ClientServiceTest.java
│                       │   └── ClientServiceIntegrationTest.java
│                       ├── role/
│                       │   ├── RoleServiceTest.java
│                       │   └── RoleServiceIntegrationTest.java
│                       ├── group/
│                       │   ├── GroupServiceTest.java
│                       │   └── GroupServiceIntegrationTest.java
│                       ├── idp/
│                       │   ├── IdentityProviderServiceTest.java
│                       │   └── IdentityProviderServiceIntegrationTest.java
│                       ├── authentication/
│                       │   ├── AuthenticationServiceTest.java
│                       │   └── AuthenticationServiceIntegrationTest.java
│                       ├── KeycloakToolTest.java
│                       ├── KeycloakToolIntegrationTest.java
│                       └── AbstractKeycloakTest.java
└── resources/
    ├── application-test.properties
    ├── test-realm.json
    └── import/
        └── test-data.json
```

#### 1.3 Create Base Test Configuration

**src/test/resources/application-test.properties**:
```properties
# Test profile configuration
quarkus.http.test-port=0

# Disable authentication for tests
quarkus.http.auth.permission.mcp.policy=permit
quarkus.oidc.enabled=false

# Keycloak Testcontainer will provide the URL
# Override in tests using @QuarkusTest and @TestProfile
quarkus.keycloak.admin-client.server-url=${keycloak.url:http://localhost:8180}

# Dev services disabled - using Testcontainers instead
quarkus.keycloak.devservices.enabled=false

# Test realm
test.keycloak.realm=test-realm
test.keycloak.admin.username=admin
test.keycloak.admin.password=admin
```

---

## Testing Layers

### Layer 1: Unit Tests (Service Layer)

**Purpose**: Test business logic in isolation without external dependencies

**Approach**: Mock Keycloak Admin Client

**Example**: `UserServiceTest.java`
```java
package dev.shaaf.keycloak.mcp.server.user;

import dev.shaaf.keycloak.mcp.server.KeycloakClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private KeycloakClientFactory clientFactory;

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.clientFactory = clientFactory;
    }

    @Test
    void testGetUsers_ReturnsUserList() {
        // Given
        String realmName = "test-realm";
        UserRepresentation user1 = new UserRepresentation();
        user1.setUsername("user1");
        UserRepresentation user2 = new UserRepresentation();
        user2.setUsername("user2");
        List<UserRepresentation> expectedUsers = Arrays.asList(user1, user2);

        when(clientFactory.createClient()).thenReturn(keycloak);
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.list()).thenReturn(expectedUsers);

        // When
        List<UserRepresentation> result = userService.getUsers(realmName);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("username")
            .containsExactly("user1", "user2");
        verify(clientFactory).createClient();
        verify(keycloak).realm(realmName);
    }

    @Test
    void testGetUserByUsername_Found() {
        // Given
        String realmName = "test-realm";
        String username = "testuser";
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);

        when(clientFactory.createClient()).thenReturn(keycloak);
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(username)).thenReturn(List.of(user));

        // When
        UserRepresentation result = userService.getUserByUsername(realmName, username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    void testGetUserByUsername_NotFound() {
        // Given
        String realmName = "test-realm";
        String username = "nonexistent";

        when(clientFactory.createClient()).thenReturn(keycloak);
        when(keycloak.realm(realmName)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(username)).thenReturn(List.of());

        // When
        UserRepresentation result = userService.getUserByUsername(realmName, username);

        // Then
        assertThat(result).isNull();
    }
}
```

**Coverage Target**: 80%+ for service classes

---

### Layer 2: Integration Tests (with Testcontainers)

**Purpose**: Test against a real Keycloak instance

**Approach**: Use Testcontainers to spin up Keycloak

**Base Test Class**: `AbstractKeycloakTest.java`
```java
package dev.shaaf.keycloak.mcp.server;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;

public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:24.0";
    private static final String REALM_IMPORT_FILE = "/test-realm.json";

    private KeycloakContainer keycloak;

    @Override
    public Map<String, String> start() {
        keycloak = new KeycloakContainer(DockerImageName.parse(KEYCLOAK_IMAGE))
                .withRealmImportFile(REALM_IMPORT_FILE)
                .withAdminUsername("admin")
                .withAdminPassword("admin");

        keycloak.start();

        String keycloakUrl = keycloak.getAuthServerUrl();

        return Map.of(
            "quarkus.keycloak.admin-client.server-url", keycloakUrl,
            "KC_URL", keycloakUrl,
            "KC_REALM", "test-realm",
            "keycloak.url", keycloakUrl
        );
    }

    @Override
    public void stop() {
        if (keycloak != null) {
            keycloak.stop();
        }
    }
}
```

**Integration Test Example**: `UserServiceIntegrationTest.java`
```java
package dev.shaaf.keycloak.mcp.server.user;

import dev.shaaf.keycloak.mcp.server.KeycloakTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
class UserServiceIntegrationTest {

    @Inject
    UserService userService;

    private static final String TEST_REALM = "test-realm";

    @Test
    void testCreateAndGetUser() {
        // Given
        String username = "integration-test-user";
        String email = "test@example.com";

        // When - Create user
        String createResult = userService.addUser(
            TEST_REALM,
            username,
            "Test",
            "User",
            email,
            "password123"
        );

        // Then
        assertThat(createResult).contains("Successfully created user");

        // When - Get user
        UserRepresentation user = userService.getUserByUsername(TEST_REALM, username);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
    }

    @Test
    void testGetUsers() {
        // When
        List<UserRepresentation> users = userService.getUsers(TEST_REALM);

        // Then
        assertThat(users).isNotEmpty();
    }

    @Test
    void testCountUsers() {
        // When
        int count = userService.countUsers(TEST_REALM);

        // Then
        assertThat(count).isGreaterThan(0);
    }
}
```

**Test Realm Configuration**: `src/test/resources/test-realm.json`
```json
{
  "realm": "test-realm",
  "enabled": true,
  "displayName": "Test Realm",
  "users": [
    {
      "username": "testuser",
      "enabled": true,
      "email": "testuser@example.com",
      "firstName": "Test",
      "lastName": "User",
      "credentials": [
        {
          "type": "password",
          "value": "password"
        }
      ]
    }
  ],
  "clients": [
    {
      "clientId": "test-client",
      "enabled": true,
      "protocol": "openid-connect",
      "publicClient": false,
      "directAccessGrantsEnabled": true
    }
  ],
  "roles": {
    "realm": [
      {
        "name": "test-role",
        "description": "Test role"
      }
    ]
  },
  "groups": [
    {
      "name": "test-group",
      "path": "/test-group"
    }
  ]
}
```

---

### Layer 3: MCP Tool Tests

**Purpose**: Test the KeycloakTool operations end-to-end

**Example**: `KeycloakToolIntegrationTest.java`
```java
package dev.shaaf.keycloak.mcp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
class KeycloakToolIntegrationTest {

    @Inject
    KeycloakTool keycloakTool;

    @Inject
    ObjectMapper mapper;

    @Test
    void testGetUsersOperation() throws Exception {
        // Given
        String params = """
            {
                "realm": "test-realm"
            }
            """;

        // When
        String result = keycloakTool.executeKeycloakOperation(
            KeycloakTool.KeycloakOperation.GET_USERS,
            params
        );

        // Then
        assertThat(result).isNotNull();
        var users = mapper.readTree(result);
        assertThat(users.isArray()).isTrue();
        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    void testCreateUserOperation() throws Exception {
        // Given
        String params = """
            {
                "realm": "test-realm",
                "username": "tool-test-user",
                "firstName": "Tool",
                "lastName": "Test",
                "email": "tool@example.com",
                "password": "password123"
            }
            """;

        // When
        String result = keycloakTool.executeKeycloakOperation(
            KeycloakTool.KeycloakOperation.CREATE_USER,
            params
        );

        // Then
        assertThat(result).contains("Successfully created user");
    }

    @Test
    void testGetRealmsOperation() throws Exception {
        // Given
        String params = "{}";

        // When
        String result = keycloakTool.executeKeycloakOperation(
            KeycloakTool.KeycloakOperation.GET_REALMS,
            params
        );

        // Then
        assertThat(result).isNotNull();
        var realms = mapper.readTree(result);
        assertThat(realms.isArray()).isTrue();
    }
}
```

---

### Layer 4: SSE/MCP Endpoint Tests

**Purpose**: Test the MCP server endpoints

**Example**: `McpEndpointTest.java`
```java
package dev.shaaf.keycloak.mcp.server;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(KeycloakTestResource.class)
class McpEndpointTest {

    @Test
    void testHealthEndpoint() {
        given()
            .when().get("/q/health")
            .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void testMcpSseEndpoint() {
        given()
            .when().get("/mcp/sse")
            .then()
                .statusCode(anyOf(is(200), is(405))); // Method may vary
    }
}
```

---

## Test Execution Strategy

### Local Development

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run integration tests only
mvn test -Dtest="*IntegrationTest"

# Run unit tests only (exclude integration)
mvn test -Dtest="*Test" -DexcludeTests="*IntegrationTest"

# Run with coverage
mvn clean test jacoco:report
```

### CI/CD Integration

**Update `.github/workflows/build-artifacts.yml`**:

```yaml
- name: Run Tests
  run: mvn -B test

- name: Upload Test Results
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: test-results
    path: target/surefire-reports/

- name: Publish Test Report
  uses: dorny/test-reporter@v1
  if: always()
  with:
    name: Maven Tests
    path: target/surefire-reports/*.xml
    reporter: java-junit
```

**Remove `-DskipTests` from all workflow files**:
- `.github/workflows/build-artifacts.yml`
- `.github/workflows/release.yml`

---

## Code Coverage Strategy

### Add JaCoCo Plugin

**pom.xml**:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Coverage Targets
- **Service Classes**: 80%+ line coverage
- **Tool Classes**: 70%+ line coverage
- **Overall Project**: 70%+ line coverage

---

## Test Data Management

### Test Realm Setup
Use `test-realm.json` for consistent test data:
- Predefined users
- Predefined clients
- Predefined roles
- Predefined groups

### Test Cleanup
Use `@AfterEach` and `@BeforeEach` to ensure test isolation:

```java
@AfterEach
void cleanup() {
    // Delete test users created during test
    List<UserRepresentation> users = userService.getUsers(TEST_REALM);
    users.stream()
        .filter(u -> u.getUsername().startsWith("test-"))
        .forEach(u -> userService.deleteUser(TEST_REALM, u.getUsername()));
}
```

---

## Performance Testing

### Load Testing with Gatling

Add Gatling for performance testing:

```xml
<dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>3.10.5</version>
    <scope>test</scope>
</dependency>
```

**Example Scenario**: `UserLoadTest.scala`
```scala
class UserLoadTest extends Simulation {
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")

  val scn = scenario("Get Users Load Test")
    .exec(
      http("get_users")
        .post("/mcp/sse")
        .body(StringBody("""{"operation":"GET_USERS","params":"{\"realm\":\"test-realm\"}"}"""))
    )

  setUp(
    scn.inject(rampUsers(100) during (10 seconds))
  ).protocols(httpProtocol)
}
```

---

## Contract Testing

### Consumer-Driven Contracts with Pact

For API stability:

```xml
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.7</version>
    <scope>test</scope>
</dependency>
```

---

## Mutation Testing

### PIT Mutation Testing

Ensure test quality:

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.15.8</version>
    <dependencies>
        <dependency>
            <groupId>org.pitest</groupId>
            <artifactId>pitest-junit5-plugin</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
    <configuration>
        <targetClasses>
            <param>dev.shaaf.keycloak.mcp.server.*</param>
        </targetClasses>
        <targetTests>
            <param>dev.shaaf.keycloak.mcp.server.*</param>
        </targetTests>
    </configuration>
</plugin>
```

Run with: `mvn org.pitest:pitest-maven:mutationCoverage`

---

## Implementation Roadmap

### Week 1: Foundation
- [ ] Add test dependencies to pom.xml
- [ ] Create test directory structure
- [ ] Create AbstractKeycloakTest base class
- [ ] Setup Testcontainers configuration
- [ ] Create test-realm.json
- [ ] Write first unit test (UserServiceTest)
- [ ] Write first integration test (UserServiceIntegrationTest)

### Week 2: Service Layer Tests
- [ ] UserService unit + integration tests
- [ ] RealmService unit + integration tests
- [ ] ClientService unit + integration tests
- [ ] RoleService unit + integration tests
- [ ] GroupService unit + integration tests

### Week 3: Tool Layer Tests
- [ ] KeycloakTool unit tests
- [ ] KeycloakTool integration tests
- [ ] MCP endpoint tests
- [ ] Error handling tests

### Week 4: Coverage & CI
- [ ] Add JaCoCo for coverage
- [ ] Achieve 70%+ coverage
- [ ] Update CI/CD to run tests
- [ ] Add test reporting to GitHub Actions
- [ ] Document testing guidelines

---

## Best Practices

### 1. Test Naming Convention
```
// Unit tests
<ClassName>Test.java
testMethodName_Scenario_ExpectedBehavior()

// Integration tests
<ClassName>IntegrationTest.java
testMethodName_Scenario()
```

### 2. Test Structure (Given-When-Then)
```java
@Test
void testName() {
    // Given - Setup
    // When - Execute
    // Then - Assert
}
```

### 3. Test Independence
- Each test should be independent
- Use @BeforeEach/@AfterEach for setup/cleanup
- Don't rely on test execution order

### 4. Use Test Fixtures
- Create builder pattern for test objects
- Reuse common test data

### 5. Mock External Dependencies
- Mock Keycloak client in unit tests
- Use real Keycloak in integration tests

---

## Documentation Updates Needed

### Fix `docs/developers.md`
Remove or update lines 49-65 which incorrectly claim tests exist.

### Add New Documentation
- `docs/testing-guide.md` - How to write and run tests
- `docs/testcontainers-setup.md` - Testcontainers configuration
- Update `docs/contributors.md` - Testing requirements for contributions

---

## Metrics & Reporting

### Test Metrics to Track
- Total test count
- Unit vs Integration test ratio
- Code coverage percentage
- Test execution time
- Flaky test rate
- Mutation score

### Reporting
- JaCoCo HTML reports
- Surefire test reports
- GitHub Actions test summary
- Coverage badges in README

---

## Benefits of This Strategy

1. **Confidence**: Catch bugs before production
2. **Refactoring**: Safe to improve code
3. **Documentation**: Tests serve as examples
4. **Quality**: Enforce standards via coverage
5. **CI/CD**: Automated quality gates
6. **Onboarding**: New developers understand behavior

---

## Estimated Effort

| Phase | Tasks | Effort | Priority |
|-------|-------|--------|----------|
| Setup | Infrastructure, base tests | 1 week | HIGH |
| Unit Tests | All service classes | 2 weeks | HIGH |
| Integration Tests | Testcontainers setup | 1 week | HIGH |
| Tool Tests | MCP layer testing | 1 week | MEDIUM |
| Coverage | JaCoCo, reporting | 3 days | MEDIUM |
| CI/CD | GitHub Actions update | 2 days | HIGH |
| Documentation | Testing guides | 3 days | MEDIUM |
| **TOTAL** | | **6-7 weeks** | |

---

## Conclusion

The project currently has **zero test coverage** despite documentation claims. Implementing this comprehensive testing strategy will:

1. Prevent regressions when adding new operations (154 planned)
2. Enable confident refactoring (e.g., Operation Factory pattern)
3. Ensure Keycloak Admin API operations work correctly
4. Provide safety net for the 7 implementation phases
5. Improve code quality and maintainability

**Immediate Action**: Start with Week 1 foundation to enable testing for the gap analysis implementation phases.

---

**Document Version**: 1.0
**Last Updated**: 2026-03-09
**Status**: Proposed Strategy
