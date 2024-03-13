package publish

import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.Project
import project.DeveloperInfoSpec
import project.PomInfoSpec
import project.ProjectInfoSpec
import project.PublicationInfoSpec
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.name

/* -----------------------------------------------------
 * ProjectInfoSpec Extensions
 * ----------------------------------------------------- */

/**
 * Retrieves the project information specification from a file named "project.gradle.json" located in the project directory.
 *
 * @return The project information specification.
 * @throws IOException if the file is not found or cannot be read.
 */
@Suppress("OPT_IN_USAGE")
fun Project.getProjectInfo(): ProjectInfoSpec {
  // Get file "developers.maven.json" from project
  val developerFile = Files.walk(projectDir.toPath(), 1)
    .filter(Files::isRegularFile)
    .filter { it.name == "project.gradle.json" }
    .findFirst()
    .orElseThrow { IOException("\"project.gradle.json\" not found in \"$this\"") }
  
  // Get the file content
  return Files.newInputStream(developerFile).use { stream ->
    // Decode json information
    val spec = jsonSerializer.decodeFromStream<ProjectInfoSpec>(stream)
    return@use spec.updateRefs(this)
  }
}

/**
 * Updates the references in the project information specification using the specified project.
 *
 * @param project The Gradle project.
 * @return The updated project information specification.
 */
fun ProjectInfoSpec.updateRefs(project: Project): ProjectInfoSpec {
  // Check all publications
  if (publications == null) return this
  
  // Update publication info
  val resultPublications = publications.map {
    it.updatePublicationsRef(project)
  }
  
  // Generate a ProjectInfoSpec copy
  return copy(publications = resultPublications)
}

/**
 * Updates the references in the publication information specification using the specified project.
 *
 * @param project The Gradle project.
 * @return The updated publication information specification.
 */
fun PublicationInfoSpec.updatePublicationsRef(project: Project): PublicationInfoSpec {
  // Get project developers reference
  if (pom == null) return this
  if (pom.developers == null) {
    if (pom.developersRef == null) return this
  }
  
  // Extract Pom developers
  val developersResult = pom.getDevelopersSpec(project)
  val pomResult = pom.copy(developers = developersResult)
  
  return copy(pom = pomResult)
}

/**
 * Retrieves the developers' information specification from a file referenced in the [PomInfoSpec].
 *
 * @param project The Gradle project.
 * @return The developers' information specification.
 */
@Suppress("OPT_IN_USAGE")
fun PomInfoSpec.getDevelopersSpec(project: Project): Set<DeveloperInfoSpec>? = try {
  // Get file from project
  val developerFile = Files.walk(project.projectDir.toPath(), 1)
    .filter(Files::isRegularFile)
    .filter { it.name == developersRef!! }
    .findFirst()
    .orElseThrow { IOException("\"$developersRef\" not found in \"$project\"") }
  
  // Get the file content
  Files.newInputStream(developerFile).use { stream ->
    // Decode json information
    return@use jsonSerializer.decodeFromStream<Set<DeveloperInfoSpec>>(stream)
  }
} catch (e: Exception) {
  System.err.println("$this: ${e.message}")
  null
}