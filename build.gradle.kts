plugins {
    kotlin("jvm") version "2.1.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
    implementation("io.dropwizard:dropwizard-core:4.0.5")
    // Add this line to resolve the reflection error
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.21")
    implementation("io.dropwizard:dropwizard-assets:4.0.5")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("org.postgresql:postgresql:42.7.3") // <-- JDBC driver
    implementation("org.jdbi:jdbi3-core:3.39.1")
    implementation("org.jdbi:jdbi3-postgres:3.39.1")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.39.1") // for kotlin
    implementation("org.postgresql:postgresql:42.6.0")

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
