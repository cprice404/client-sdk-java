ext["grpcVersion"] = "1.39.0"
ext["protobufVersion"] = "3.17.3"
ext["opentelemetryVersion"] = "1.4.1"

val currentVersion: String by project

allprojects {
    group = "momento.sandbox"
    version = currentVersion
}
