plugins {
	id("org.springframework.boot") version "3.4.0-M1"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("plugin.jpa") version "2.0.0"
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.spring") version "2.0.0"
}

group = "com.aniable"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.h2database:h2")
	implementation("org.postgresql:postgresql")
	implementation("software.amazon.awssdk:s3:2.26.21")
	implementation("software.amazon.awssdk:sso:2.26.21")
	implementation("software.amazon.awssdk:ssooidc:2.26.21")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
