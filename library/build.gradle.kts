import generator.TaskBuildInfoGenerator
import project.tasking.fromSourceSets
import project.tasking.fromTasks
import publish.createFromSpec
import publish.getProjectInfo
import kotlin.jvm.optionals.getOrNull

plugins {
  `java-library`
}

/* -----------------------------------------------------
 * Project configuration
 * ----------------------------------------------------- */

val projectSpec = getProjectInfo()

group = projectSpec.group
version = projectSpec.version

/* -----------------------------------------------------
 * Java configuration
 * ----------------------------------------------------- */

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = sourceCompatibility
}

/* -----------------------------------------------------
 * Publishing configuration
 * ----------------------------------------------------- */

afterEvaluate {
  publishing.publications {
    // Generate maven publications
    createFromSpec(projectSpec, project)
  }
  
  signing {
    // Sign all publications
    useInMemoryPgpKeys(
      projectEnv["SIGNING_USER_ID"].getOrNull(),
      projectEnv["SIGNING_PASSWORD"].getOrNull(),
      projectEnv["SIGNING_PGP_KEY"].getOrNull())
    sign(publishing.publications)
  }
}

/* -----------------------------------------------------
 * Task configurations
 * ----------------------------------------------------- */

val buildInfoGen by tasks.registering(TaskBuildInfoGenerator::class) {
  projectName = "<library-name>"
  groupName = project.group as String?
  moduleName = "${project.group}.build"
}

val javadocJar by tasks.registering(Jar::class) {
  group = "publishing"
  archiveClassifier.set("javadoc")
  
  fromTasks("javadoc")
}

val sourceJar by tasks.registering(Jar::class) {
  group = "publishing"
  archiveClassifier.set("sources")
  
  fromSourceSets("main") {
    return@fromSourceSets it.java.srcDirs
  }
}

tasks.named("processResources") {
  dependsOn(buildInfoGen)
}

tasks.test {
  useJUnitPlatform()
}

/* -----------------------------------------------------
 * Dependencies
 * ----------------------------------------------------- */

dependencies {
  compileOnly(libs.org.jetbrains.annotations)
  // Testing libraries
  testImplementation(platform(libs.org.junit.junitJupiterBom))
  testImplementation(libs.org.junit.junitJupiter)
  testImplementation(libs.org.junit.junitEngine)
  // Testing Runtime/Compile
  testCompileOnly(libs.org.jetbrains.annotations)
  testRuntimeOnly(libs.org.junit.junitRuntime)
}