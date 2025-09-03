import de.schablinski.gradle.activejdbc.ActiveJDBCInstrumentation

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
  	id("de.schablinski.activejdbc-gradle-plugin") version "2.0.1"
	id("io.freefair.lombok") version "8.13.1"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "com.github.sanmoo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// AWS SDK
	implementation(platform("software.amazon.awssdk:bom:2.31.78"))
	implementation("software.amazon.awssdk:dynamodb")
	implementation("software.amazon.awssdk:sqs")

	// Kotlin Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

	// Json Serialization
	implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")

	// ActiveJDBC
	implementation("org.javalite:activejdbc:3.5-j11")
	implementation("org.javalite:activejdbc-kt:3.4-j11")

	implementation("ch.qos.logback:logback-classic:1.5.18")
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	compileOnly("org.projectlombok:lombok")
	// TODO: Investigate why this is conflicting with ActiveJDBC
	// developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.batch:spring-batch-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("com.diffplug.selfie:selfie-runner-junit5:2.5.3")
	testImplementation("io.mockk:mockk:1.14.5")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(kotlin("stdlib-jdk8"))
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	environment(properties.filter { it.key == "selfie "})
	inputs.files(fileTree("src/test") {
		include("**/*.ss")
	})
	dependsOn("instrumentKotlinModels")
}

tasks.register<ActiveJDBCInstrumentation>("instrumentKotlinModels") {
	group = "build"
	classesDir = sourceSets["main"].kotlin.classesDirectory.get().toString()
	outputDir = sourceSets["main"].kotlin.classesDirectory.get().toString()
}

tasks.getByName("bootJar") {

}

tasks.named("compileKotlin") {
	finalizedBy("instrumentKotlinModels")
}

tasks.named("resolveMainClassName") {
	dependsOn("instrumentKotlinModels")
}

kover {
	reports {
		verify {
			rule {
				minBound(100)
			}
		}

		filters {
			excludes {
				classes("com.github.sanmoo.ddd.synchronizer.Application*", "com.github.sanmoo.ddd.synchronizer.legacy" +
						".persistence.models.*")
			}
		}
	}
}