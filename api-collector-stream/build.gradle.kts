import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.71"
    kotlin("plugin.serialization") version kotlinVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

group = "com.stuko.stream"
version = "0.9.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude( "org.springframework.boot", "spring-boot-starter-logging")
    }
}

allprojects {

    apply(plugin = "idea")
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-kapt")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    repositories {
        jcenter()
        mavenCentral()
        maven {
            url = uri("${rootDir}/.m2/repository")
        }
        // mavenLocal()
    }

    dependencies {

        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-jdbc")

        implementation ("org.mybatis:mybatis:3.5.5")
        implementation("org.mybatis:mybatis-spring:2.0.5")
        implementation ("com.google.code.gson:gson:2.8.6")
        // Rhea
        //implementation("com.kcb.rhea:rhea-core:0.9.0-SNAPSHOT")
        //implementation("com.kcb.rhea:rhea-starter:0.9.0-SNAPSHOT")
        //implementation("com.kcb.rhea:rhea-domain:0.9.0-SNAPSHOT")
        //implementation("com.kcb.rhea:rhea-feature-business:0.9.0-SNAPSHOT")

        // Kafka
        // implementation("org.springframework.kafka:spring-kafka")
        // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
        implementation("org.apache.kafka:kafka-clients:3.5.1")

        implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

        // Spring
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
        implementation("org.apache.storm:storm-core:2.2.0")
        // runtimeOnly("com.codahale.metrics:metrics-core:3.0.0")
        implementation("io.dropwizard.metrics:metrics-core:3.2.2")
        // runtimeOnly("com.codahale.metrics:metrics-jvm:3.0.2")

        // runtimeOnly("com.h2database:h2")

        // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
        // implementation("org.apache.httpcomponents:httpclient:4.5.12")
        // https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
        implementation("org.apache.httpcomponents:httpclient:4.5.13")

        implementation("com.squareup.okhttp3:okhttp:4.7.2")

        implementation ("org.mongodb:mongodb-driver:3.11.2")
        implementation ("org.mongodb:bson:4.0.4")
        implementation ("com.google.code.gson:gson:2.8.6")

        // https://mvnrepository.com/artifact/org.postgresql/postgresql
        implementation("org.postgresql:postgresql:42.2.14")

        // implementation ("org.hdrhistogram:HdrHistogram")
        //implementation ("org.testng:testng:6.8.5")
        //implementation ("org.mockito:mockito-core")
        //implementation ("org.hamcrest:java-hamcrest")
        //implementation ("org.easytesting:fest-assert-core:2.0M8")
        //implementation ("org.jmock:jmock:2.6.0")
        //implementation ("org.apache.storm:storm-clojure:")
        //implementation ("org.apache.storm:storm-clojure-test:")
        //implementation ("org.apache.storm:storm-client:")
        implementation ("org.apache.storm:storm-client:2.2.0")
        //implementation ("org.apache.storm:multilang-javascript:")
        //implementation ("org.apache.storm:multilang-ruby:")
        //implementation ("org.apache.storm:multilang-python:")
        //implementation ("commons-collections:commons-collections")
        //implementation ("com.google.guava:guava")
        // implementation ("org.apache.storm:storm-metrics:2.2.0")
        //implementation ("org.apache.storm:storm-hdfs:")
        //implementation ("org.apache.storm:storm-hbase:")
        //implementation ("org.apache.storm:storm-redis:")

        // implementation("org.springframework.kafka:spring-kafka:2.5.5.RELEASE")

        implementation ("com.thoughtworks.xstream:xstream:1.4.13")
        // implementataion("uy.kohesive.kovert:kovert-vertx:1.5.0")
        implementation ( "io.vertx:vertx-core:3.9.3" )
        implementation ("io.vertx:vertx-web:3.9.3")
        // https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2
        implementation("org.apache.commons:commons-dbcp2:2.11.0")
        // https://mvnrepository.com/artifact/org.apache.commons/commons-pool2
        implementation("org.apache.commons:commons-pool2:2.12.0")
        // https://mvnrepository.com/artifact/commons-logging/commons-logging
        implementation("commons-logging:commons-logging:1.3.0")


        // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
        implementation ("com.zaxxer:HikariCP:3.4.5")
        // https://mvnrepository.com/artifact/hikari-cp/hikari-cp
        // implementation ("hikari-cp:hikari-cp:3.0.1")

        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.1")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
        // implementation("org.mariadb.jdbc:mariadb-java-client:2.1.2")
        // https://mvnrepository.com/artifact/mysql/mysql-connector-java
        implementation("mysql:mysql-connector-java:8.0.33")

        // https://mvnrepository.com/artifact/cn.easyproject/ojdbc6
        // https://mvnrepository.com/artifact/oracle.jdbc.oracledriver/ojdbc6
        implementation("com.oracle.database.jdbc:ojdbc6:11.2.0.4")

        // jayway jsonpath
        implementation("com.jayway.jsonpath:json-path:2.6.0")


    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}

