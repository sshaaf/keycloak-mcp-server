// Defines the plugins required for the project.
// 'java' provides core Java compilation capabilities.
// 'io.quarkus' integrates the Quarkus framework, handling dependency management and build tasks.
plugins {
    java
    id("io.quarkus")
}

// Specifies the repositories where dependencies are located.
repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

// Configures the Java toolchain.
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Defines the project dependencies.
dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-jsonb")

    // MCP

    implementation("io.quarkiverse.mcp:quarkus-mcp-server-stdio:1.4.0")

    // MCP SSE
    //implementation("io.quarkiverse.mcp:quarkus-mcp-server-sse")

    implementation("io.quarkus:quarkus-rest-qute")

    // Keycloak admin client
    implementation("io.quarkus:quarkus-keycloak-admin-rest-client")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito:mockito-core")
}

group = "dev.shaaf.keycloak.mcp.server"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}