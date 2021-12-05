plugins {
    kotlin("jvm") version "1.6.0"
    application
}

group = "com.github.3flex.aoc2021"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
    implementation("org.jetbrains.kotlinx:multik-default:0.1.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
