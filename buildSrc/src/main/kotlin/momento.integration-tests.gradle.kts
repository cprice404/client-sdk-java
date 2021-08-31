import gradle.kotlin.dsl.accessors._1fe27f2da650fe3a413b302e2d070f59.*
import gradle.kotlin.dsl.accessors._1fe27f2da650fe3a413b302e2d070f59.main
import gradle.kotlin.dsl.accessors._1fe27f2da650fe3a413b302e2d070f59.sourceSets
import gradle.kotlin.dsl.accessors._1fe27f2da650fe3a413b302e2d070f59.testImplementation
import gradle.kotlin.dsl.accessors._1fe27f2da650fe3a413b302e2d070f59.testRuntimeClasspath
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Set up integration testing -------

// https://docs.gradle.org/current/userguide/java_testing.html#sec:configuring_java_integration_tests

// Define independent source set so we can run separately then unit tests
sourceSets {
    create("intTest") {
        compileClasspath += main.get().output + configurations.testRuntimeClasspath
        runtimeClasspath += output + compileClasspath
    }
}

// Extend base project run time and test configuration then add integration specific deps
val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// Define separate task for running int tests
val integrationTest = task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    useJUnitPlatform()
    testLogging {
//         showStandardStreams = true  // Un comment this if need full integration test output for stdout & stderr
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
    outputs.upToDateWhen { false }
}