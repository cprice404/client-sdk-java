import com.google.protobuf.gradle.*

plugins {
    id("com.google.protobuf") version "0.8.16"
//    id("momento.artifactory-java-lib")
    id("momento.maven-central-java-lib")
    idea
}

dependencies {

    api("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
    api("io.grpc:grpc-stub:${rootProject.ext["grpcVersion"]}")
    api("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53") // necessary for Java 9+

    protobuf(files("src/client_protos/proto/"))
}

protobuf {
    var systemOverride = ""
    if (System.getProperty("os.name") == "Mac OS X") {
        println("overriding protobuf artifacts classifier to osx-x86_64 so M1 Macs can find lib")
        systemOverride = ":osx-x86_64"
    }

    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}$systemOverride"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}$systemOverride"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}
