plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.kotlin.jpa)
	alias(libs.plugins.kotlin.spring)
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
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
}

dependencies {
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.mail)
	implementation(libs.spring.boot.starter.web)
	developmentOnly(libs.spring.boot.devtools)
	testImplementation(libs.spring.boot.starter.test)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.kotlin.reflect)
	runtimeOnly(libs.h2)
	testImplementation(libs.kotlin.test.junit5)
	testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
