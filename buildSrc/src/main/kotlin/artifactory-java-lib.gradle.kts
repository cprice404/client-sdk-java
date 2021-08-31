import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.task

//
// // buildscript {
// //    repositories {
// //        mavenCentral()
// //    }
// //
// //    dependencies {
// //        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
// //    }
// // }
// //
// // build
// // dependencies {
// //    classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
// // }
//
plugins {
    `java-library`
    `maven-publish`
    id("com.jfrog.artifactory")
}

repositories {
    mavenCentral()
}

configure<JavaPluginExtension> {
// java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components.getByName("java"))
        }
    }
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

//
// configure<org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention> {
//    clientConfig.isIncludeEnvVars = true
//
//    setContextUrl("https://momento.jfrog.io/artifactory")
//    publish {
//        repository {
//            val artifactoryUsername = System.getenv("ARTIFACTORY_USERNAME")
//            val artifactoryPassword = System.getenv("ARTIFACTORY_PASSWORD")
//            setRepoKey("maven-public") // The Artifactory repository key to publish to
//            setUsername(artifactoryUsername)
//            setPassword(artifactoryPassword)
//        }
//
//        defaults {
//            // Reference to Gradle publications defined in the build script.
//            // This is how we tell the Artifactory Plugin which artifacts should be
//            // published to Artifactory.
//            publications("mavenJava")
//            setPublishArtifacts(true)
//            setPublishPom(true) // Publish generated POM files to Artifactory (true by default)
//        }
//    }
// }
//
// /*
// buildscript {
//    repositories {
//        maven {
//            url 'https://momento.jfrog.io/artifactory/maven-public'
//            credentials {
//                username = "${artifactory_user}"
//                password = "${artifactory_password}"
//            }
//        }
//
//    }
//    dependencies {
//        //Check for the latest version here: http://plugins.gradle.org/plugin/com.jfrog.artifactory
//        classpath "org.jfrog.buildinfo:build-info-extractor-gradle:4+"
//    }
// }
//
// allprojects {
//    apply plugin: "com.jfrog.artifactory"
// }
//
// artifactory {
//    contextUrl = "${artifactory_contextUrl}"   //The base Artifactory URL if not overridden by the publisher/resolver
//    publish {
//        repository {
//            repoKey = 'maven-local'
//            username = "${artifactory_user}"
//            password = "${artifactory_password}"
//            maven = true
//
//        }
//    }
//    resolve {
//        repository {
//            repoKey = 'maven-public'
//            username = "${artifactory_user}"
//            password = "${artifactory_password}"
//            maven = true
//
//        }
//    }
// }
// */


