import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.3.72"
	val springVersion = "2.3.2.RELEASE"
	id("org.springframework.boot") version springVersion
	id("org.jetbrains.kotlin.jvm") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
	id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

group = "com.stuko.data.api"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	jcenter()
	google()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.session:spring-session-hazelcast")
	implementation("org.springframework.session:spring-session-core:2.2.2.RELEASE")
	implementation("org.springframework.session:spring-session-hazelcast:2.2.2.RELEASE")

	implementation ("org.mongodb:mongo-java-driver:3.12.7")
	implementation ("org.apache.httpcomponents:httpclient:4.3.3")
	implementation ("org.apache.httpcomponents:httpmime:4.3.3")
	implementation ("org.mongodb:bson:4.0.4")

	implementation ("com.google.code.gson:gson:2.8.6")
	implementation ("org.mybatis:mybatis:3.5.5")
	implementation("org.mybatis:mybatis-spring:2.0.5")
	implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
	implementation ("com.google.code.gson:gson:2.8.6")
	implementation("org.postgresql:postgresql:42.2.14")
	// Kafka
	implementation("org.springframework.kafka:spring-kafka")

	implementation(kotlin("script-runtime"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
