plugins {
    id("java")
    application
}

val supportedPlatforms = listOf("linux", "mac", "win")

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.java-native:jssc:2.9.4")
    implementation("io.vertx:vertx-core:4.2.6")
    implementation("io.vertx:vertx-web:4.2.6")
    implementation("io.vertx:vertx-web-client:4.2.6")
    implementation("io.vertx:vertx-mqtt:4.2.6")
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.107.Final:osx-x86_64")
}

application {
    // Define the main class for the application.
    mainClass.set("org.example.Main")
}