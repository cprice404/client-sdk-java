import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// tasks().test {
//    useJUnitPlatform()
//
//    testLogging {
//        // showStandardStreams = true  // Un comment this if need full integration test output for stdout & stderr
//        exceptionFormat = TestExceptionFormat.FULL
//        events("passed", "skipped", "failed")
//    }
// }

tasks.withType<Test> { // get("test") { //  get<Test>("test") { //}   <Test>  ("test") {
    useJUnitPlatform()

    testLogging {
        // showStandardStreams = true  // Un comment this if need full integration test output for stdout & stderr
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}
