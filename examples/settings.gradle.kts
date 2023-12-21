/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/7.2/userguide/multi_project_builds.html
 */

rootProject.name = "java-gradle"

/*
 * Uncomment the code below if you would like to develop the examples against your local
 * copy of the SDK code. This is useful if you are making changes to the SDK and want to
 * test them in the examples before committing them.
 */
includeBuild("..") {
    dependencySubstitution {
        substitute(module("software.momento.java:sdk")).using(project(":momento-sdk"))
    }
}

include("cache")
include("cache-with-aws")
include("lambda:docker")
include("token")
include("topic")
