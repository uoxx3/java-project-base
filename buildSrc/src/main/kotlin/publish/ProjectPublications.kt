package publish

import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.ivy.IvyPublication
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import project.PomInfoSpec
import project.ProjectInfoSpec
import project.PublicationInfoSpec

/**
 * Creates publications based on the provided specification.
 *
 * @param spec The project information specification.
 * @param project The Gradle project.
 */
fun PublicationContainer.createFromSpec(spec: ProjectInfoSpec, project: Project) {
  if (spec.publications == null) return
  
  // Iterate all publications
  spec.publications.forEach { publication ->
    // Check the publication type
    when (publication.type.trim().lowercase()) {
      "maven" -> create(publication.name, MavenPublication::class.java) {
        configureMavenPublication(spec, publication, this, project)
      }
      
      "ivy" -> create(publication.name, IvyPublication::class.java) {
        configureIvyPublication(spec, publication, this, project)
      }
    }
  }
}

/* -----------------------------------------------------
 * Internal Maven methods
 * ----------------------------------------------------- */

/**
 * Configures a Maven publication.
 *
 * @param spec The project information specification.
 * @param cSpec The publication information specification.
 * @param publication The Maven publication.
 * @param project The Gradle project.
 */
private fun configureMavenPublication(
  spec: ProjectInfoSpec,
  cSpec: PublicationInfoSpec,
  publication: MavenPublication,
  project: Project
) {
  publication.groupId = spec.group
  publication.version = spec.version
  
  cSpec.pom?.let {
    publication.artifactId = it.artifactId
  }
  
  // Configure publication components
  project.components.findByName(cSpec.component)
    ?.let(publication::from)
  
  // Configure publication artifacts
  cSpec.artifacts?.let {
    it.forEach { artifact ->
      // Check the artifact type
      val artifactObj = project.tasks.findByName(artifact.taskName)
      if (artifactObj == null && artifact.required) {
        throw IllegalStateException("""
					The task "${artifact.taskName}" is not defined within "${project.path}" and is required for publication "${cSpec.name}"
				""".trimIndent())
      }
      
      // Attach the artifact
      if (artifactObj == null) {
        System.err.println("Project ${project.path} => Artifact \"${artifact.taskName}\" not found")
      } else {
        publication.artifact(artifactObj)
      }
    }
  }
  
  // Configure POM element
  cSpec.pom?.let { pomSpec ->
    publication.pom {
      configurePom(pomSpec, this)
    }
  }
  
}

/**
 * Configures a POM for a Maven publication.
 *
 * @param cPom The POM information specification.
 * @param pom The Maven POM.
 */
private fun configurePom(
  cPom: PomInfoSpec,
  pom: MavenPom,
) {
  // Base configuration
  pom.url.set(cPom.url)
  pom.name.set(cPom.artifactId)
  pom.description.set(cPom.description)
  
  // POM License configuration
  pom.licenses {
    cPom.licenses?.forEach { licenseSpec ->
      license {
        name.set(licenseSpec.name)
        url.set(licenseSpec.value)
      }
    }
  }
  
  // POM Developers configuration
  pom.developers {
    cPom.developers?.forEach { developerSpec ->
      developer {
        // Required information
        id.set(developerSpec.id)
        // Optional information
        developerSpec.name?.let(name::set)
        developerSpec.url?.let(url::set)
        developerSpec.email?.let(email::set)
        developerSpec.roles?.let(roles::set)
        developerSpec.organization?.let(organization::set)
        developerSpec.organizationUrl?.let(organizationUrl::set)
        developerSpec.timezone?.let(timezone::set)
      }
    }
  }
  
  // Configure SCM connection configuration
  if (cPom.scm == null) return
  pom.scm {
    url.set(cPom.scm.url)
    connection.set(cPom.scm.connection)
    developerConnection.set(cPom.scm.developerConnection)
  }
}

/* -----------------------------------------------------
 * Internal Ivy methods
 * ----------------------------------------------------- */

/**
 * Configures an Ivy publication.
 *
 * @param spec The project information specification.
 * @param cSpec The publication information specification.
 * @param publication The Ivy publication.
 * @param project The Gradle project.
 */
private fun configureIvyPublication(
  spec: ProjectInfoSpec,
  cSpec: PublicationInfoSpec,
  publication: IvyPublication,
  project: Project
) {
  // Configure Ivy component
  project.components.findByName(cSpec.component)
    ?.let(publication::from)
  
  // Configure Ivy publication metadata
  val publicationDesc = cSpec.pom ?: return
  
  publication.descriptor {
    // Project description
    description {
      text.set(publicationDesc.description)
      homepage.set(publicationDesc.url)
    }
    
    // License
    publicationDesc.licenses?.firstOrNull()?.let {
      license {
        name.set(it.name)
        url.set(it.value)
      }
    }
    
    val ivyDevelopers = publicationDesc.developers?.map { spec ->
      mutableMapOf<String, Any>().apply {
        // Required properties
        put("id", spec.id)
        // Optional properties
        spec.email?.let { put("email", it) }
        spec.name?.let { put("name", it) }
        spec.organization?.let { put("organization", it) }
        spec.organizationUrl?.let { put("organization-url", it) }
        spec.roles?.let { put("roles", it) }
        spec.timezone?.let { put("timezone", it) }
        spec.url?.let { put("url", it) }
      }
    } ?: emptySet<Any>()
    
    // Attach extra metadata
    withXml {
      asNode().appendNode("metadata", mutableMapOf<Any, Any>(
        "organization" to mapOf(
          "name" to spec.group,
          "url" to publicationDesc.url
        ),
        "developers" to ivyDevelopers
      ))
    }
  }
}