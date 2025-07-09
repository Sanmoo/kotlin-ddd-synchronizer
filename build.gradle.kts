plugins {
    kotlin("jvm") version "2.1.10"
    application
}

group = "com.github.sanmoo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:amazon-kinesis-client:1.8.8")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.sanmoo.MainKt") // This assumes Main.kt is in the root package
}