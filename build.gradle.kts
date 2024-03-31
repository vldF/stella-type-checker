plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
    id("antlr")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.8")
    implementation("com.google.guava:guava:32.1.1-jre")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("MainKt")
}

tasks.named<Test>("test") {
    description = "Run only supported Stella Type Checker tests. You can configure supported extension list in " +
            "StellaTestRunner.kt"
    group = "verification"

    outputs.upToDateWhen {false}

    environment("ENABLE_ONLY_SUPPORTED_TESTS", true)
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

tasks.generateGrammarSource {
    arguments = listOf("-visitor", "-long-messages")
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateGrammarSource"))
}

tasks.named("compileTestKotlin") {
    dependsOn(tasks.named("generateTestGrammarSource"))
}

task<Test>("runAllTests") {
    description = "Run all tests for Stella Type Checker. You can run only supported tests via task runSupportedTests"
    group = "verification"

    outputs.upToDateWhen {false}

    environment("ENABLE_ONLY_SUPPORTED_TESTS", false)
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}
