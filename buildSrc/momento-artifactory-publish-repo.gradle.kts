buildscript {
    dependencies {
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
    }
}

plugins {
    `maven-publish`
    id("com.jfrog.artifactory")
}

configure<org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention> {
    clientConfig.isIncludeEnvVars = true

    setContextUrl("https://momento.jfrog.io/artifactory")
    publish {
        repository {
            val artifactoryUsername = System.getenv("ARTIFACTORY_USERNAME")
            val artifactoryPassword = System.getenv("ARTIFACTORY_PASSWORD")
            setRepoKey("maven-public") // The Artifactory repository key to publish to
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

/*
buildscript {
    repositories {
        maven {
            url 'https://momento.jfrog.io/artifactory/maven-public'
            credentials {
                username = "${artifactory_user}"
                password = "${artifactory_password}"
            }
        }

    }
    dependencies {
        //Check for the latest version here: http://plugins.gradle.org/plugin/com.jfrog.artifactory
        classpath "org.jfrog.buildinfo:build-info-extractor-gradle:4+"
    }
}

allprojects {
    apply plugin: "com.jfrog.artifactory"
}

artifactory {
    contextUrl = "${artifactory_contextUrl}"   //The base Artifactory URL if not overridden by the publisher/resolver
    publish {
        repository {
            repoKey = 'maven-local'
            username = "${artifactory_user}"
            password = "${artifactory_password}"
            maven = true

        }
    }
    resolve {
        repository {
            repoKey = 'maven-public'
            username = "${artifactory_user}"
            password = "${artifactory_password}"
            maven = true

        }
    }
}
 */