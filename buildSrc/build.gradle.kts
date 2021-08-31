// val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
// compileKotlin.kotlinOptions.jvmTarget = "11"
//
// //buildscript {
// //    repositories {
// //        mavenCentral()
// //    }
// ////
// ////    dependencies {
// ////        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
// ////    }
// //
// //}
//
// plugins {
//    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
//    `kotlin-dsl`
// }
//
repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}
//
//
// dependencies {
// //    implementation(gradleApi())
// //    implementation(gradleKotlinDsl())
//    implementation(kotlin("gradle-plugin"))
//    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
// }

dependencies {
//    implementation(kotlin("jvm"))
//    implementation(gradleApi())
//    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin"))
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:4.24.16")
}

plugins {
    kotlin("jvm") version "1.5.21"
    `kotlin-dsl`
}
