import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.9"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.7.21"
	kotlin("plugin.spring") version "1.7.21"
}

group = "com.demo.gateway"
version = "0.0.1"
description = "Auth Proxy using Spring Cloud Gateway"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Vault
	implementation("com.azure.spring:spring-cloud-azure-starter-keyvault-secrets")

	// Observability
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

	// Gateway
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")

	// Circuit Breaker
//	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
//	implementation("io.github.resilience4j:resilience4j-micrometer")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

	// Security
	implementation("org.springframework.session:spring-session-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

	implementation("com.google.guava:guava:31.1-jre")
	implementation("net.logstash.logback:logstash-logback-encoder:7.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("com.azure.spring:spring-cloud-azure-dependencies:4.6.0")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}
