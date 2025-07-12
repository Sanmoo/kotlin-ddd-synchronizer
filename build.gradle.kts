plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "com.github.sanmoo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // AWS SDK
    implementation(platform("software.amazon.awssdk:bom:2.31.78"))
    implementation("software.amazon.awssdk:kinesis")
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.kinesis:amazon-kinesis-client:3.1.1")

    // Json Serialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.4")
    testImplementation("com.diffplug.selfie:selfie-runner-junit5:2.5.3")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.sanmoo.MainKt") // Update this to match your package
}

kover {
    reports {
        filters {
            excludes {
                classes("com.github.sanmoo.MainKt")
            }
        }

        verify {
            rule {
                minBound(100)
            }
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.github.sanmoo.MainKt"
    }
    
    // For building a fat JAR
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}