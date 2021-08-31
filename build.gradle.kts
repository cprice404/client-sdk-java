
ext["grpcVersion"] = "1.39.0"
ext["protobufVersion"] = "3.17.3"
ext["opentelemetryVersion"] = "1.4.1"

//
// buildscript {
//    dependencies {
//        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
//    }
// }
//
// allprojects {
//    group = "momento.sandbox"
//    version = "0.1.0"
//
// //    apply(from = "${project.rootDir}/buildSrc/momento-artifactory-publish-repo.gradle.kts")
//
//    apply(plugin = "com.jfrog.artifactory")
// }

//buildscript {
//    dependencies {
//        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
//    }
//}

plugins {
    `java-library`
    `maven-publish`
    id("momento-artifactory-publish-repo")
}

val currentVersion: String by project

allprojects {
//    apply(plugin = "com.jfrog.artifactory")

    group = "momento.sandbox"
    version = currentVersion
}

configure<org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention> {
    clientConfig.isIncludeEnvVars = true

    setContextUrl("https://momentochrissandbox.jfrog.io/artifactory")
    publish {
        repository {
            val artifactoryUsername = System.getenv("ARTIFACTORY_USERNAME")
            val artifactoryPassword = System.getenv("ARTIFACTORY_PASSWORD")
            setRepoKey("chris-sandbox-gradle-local") // The Artifactory repository key to publish to
            setUsername(artifactoryUsername)
            setPassword(artifactoryPassword)
        }

        defaults {
            // Reference to Gradle publications defined in the build script.
            // This is how we tell the Artifactory Plugin which artifacts should be
            // published to Artifactory.
            publications("mavenJava")
            setPublishArtifacts(true)
            setPublishPom(true) // Publish generated POM files to Artifactory (true by default)
        }
    }
}
