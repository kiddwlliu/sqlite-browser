import java.util.Date
import java.text.SimpleDateFormat

plugins {
    id("buildlogic.kotlin-springbootapp-conventions")
}

group = "idv.xcplay"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


	// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
	implementation("org.xerial:sqlite-jdbc:3.50.3.0")
	// https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-community-dialects
	// implementation("org.hibernate.orm:hibernate-community-dialects:6.6.22.Final")
	// https://mvnrepository.com/artifact/org.hibernate.validator/hibernate-validator
	// implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
}

application {
    mainClass.set("idv.xcplay.sqlitebrowser.SqliteBrowserApplicationKt")
}
springBoot {
    mainClass.set("idv.xcplay.sqlitebrowser.SqliteBrowserApplicationKt")
}
tasks.named<JavaExec>("run") {
	workingDir = file("$rootDir")
}
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    // workingDir = file("$projectDir")
	workingDir = file("$rootDir")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}
// jpa, plugin.spring
// allOpen {
// 	annotation("jakarta.persistence.Entity")
// 	annotation("jakarta.persistence.MappedSuperclass")
// 	annotation("jakarta.persistence.Embeddable")
// }

tasks.withType<Test> {
	useJUnitPlatform()
	workingDir = file("$rootDir")
}

tasks.getByName<Jar>("jar") {
	manifest {
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
		attributes["Build-Jdk"] = System.getProperty("java.specification.version") + " (" +
				System.getProperty("java.vendor") + " " +
				System.getProperty("java.vm.version") + ")" 
        //attributes["Build-OS"] = "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}"
		attributes["Build-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(Date())
		attributes["Implementation-Title"] = rootProject.name
		attributes["Implementation-Version"] = version
    }
}