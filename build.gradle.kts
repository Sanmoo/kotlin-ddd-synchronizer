plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
    jacoco
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

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
    
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.4")
    testImplementation("com.diffplug.selfie:selfie-runner-junit5:2.5.3")
}

tasks.test {
    useJUnitPlatform()
    environment(properties.filter { it.key == "selfie" }) // optional, see "Overwrite everything" below
    inputs.files(fileTree("src/test") { // optional, improves up-to-date checking
        include("**/*.ss")
    })
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "1.00".toBigDecimal()
            }

            includes = listOf("com.github.sanmoo.*")
            excludes = listOf("com.github.sanmoo.MainKt")
        }
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.github.sanmoo.MainKt") // Update this to match your package

    // Skip IMDS check entirely (not suitable for production), change me
    applicationDefaultJvmArgs = listOf("-Dcom.amazonaws.sdk.disableEc2Metadata=true")
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