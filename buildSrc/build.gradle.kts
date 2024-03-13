plugins {
  `kotlin-dsl`
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

repositories {
  mavenLocal() // Only for local testing purposes
  mavenCentral()
  google()
  maven {
    url = uri("https://plugins.gradle.org/m2/")
  }
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}