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
