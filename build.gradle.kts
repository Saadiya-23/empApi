plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation("io.dropwizard:dropwizard-core:4.0.5")
    implementation("io.dropwizard:dropwizard-assets:4.0.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("org.postgresql:postgresql:42.7.3") // <-- JDBC driver

    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
}

application {
    // If your main() is in src/main/kotlin/app/AttendanceApplication.kt
    mainClass.set("ApplicationKt")
}

tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(21) }

tasks.named<org.gradle.api.tasks.JavaExec>("run") {
    args = listOf("server", "src/main/resources/config.yml")
    jvmArgs = listOf("-Duser.timezone=Asia/Kolkata")
}
