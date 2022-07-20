import gradle.kotlin.dsl.accessors._44a203dd12b20a1d2c1d5b1a3c4d1b7a.java
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.`java-library`
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.repositories

/***********************************************************************************************************************
 * Re-usable gradle plugin to configure a module as a java/maven library that publishes to maven central
 **********************************************************************************************************************/

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
