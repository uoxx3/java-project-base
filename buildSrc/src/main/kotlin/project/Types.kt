package project

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/* -----------------------------------------------------
 * Project Types
 * ----------------------------------------------------- */

/**
 * Data class representing project information.
 *
 * @property name the name of the project
 * @property version the version of the project
 * @property group the group of the project
 * @property packageName the package name of the project
 * @property buildInfo the build information of the project
 * @property publications the list of publications of the project
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class ProjectInfoSpec(
  @JsonNames("name")
  val name: String,
  @JsonNames("version")
  val version: String,
  @JsonNames("group")
  val group: String,
  @JsonNames("package")
  val packageName: String,
  @JsonNames("build-info", "info-build")
  val buildInfo: BuildInfoSpec?,
  @JsonNames("publications")
  val publications: List<PublicationInfoSpec>?)

/**
 * Data class representing build information.
 *
 * @property outPackage the output package of the build
 * @property filename the filename of the build
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class BuildInfoSpec(
  @JsonNames("package", "package-out", "out-package")
  val outPackage: String,
  @JsonNames("file-name", "file", "name")
  val filename: String?)

/* -----------------------------------------------------
 * Publication Types
 * ----------------------------------------------------- */

/**
 * Data class representing publication information.
 *
 * @property type the type of the publication
 * @property name the name of the publication
 * @property component the component of the publication
 * @property artifacts the list of artifacts of the publication
 * @property pom the POM information of the publication
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class PublicationInfoSpec(
  @JsonNames("type")
  val type: String,
  @JsonNames("name")
  val name: String,
  @JsonNames("component")
  val component: String,
  @JsonNames("artifacts")
  val artifacts: List<ArtifactInfoSpec>?,
  @JsonNames("pom")
  val pom: PomInfoSpec?)

/**
 * Data class representing artifact information.
 *
 * @property taskName the name of the task
 * @property required whether the artifact is required
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class ArtifactInfoSpec(
  @JsonNames("task-name", "name", "task")
  val taskName: String,
  @JsonNames("required")
  val required: Boolean = false)

/**
 * Data class representing POM information.
 *
 * @property url the URL of the POM
 * @property description the description of the POM
 * @property artifactId the artifact ID of the POM
 * @property licenses the list of licenses of the POM
 * @property developers the set of developers of the POM
 * @property developersRef the reference to developers of the POM
 * @property scm the SCM connection information of the POM
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class PomInfoSpec(
  @JsonNames("url")
  val url: String,
  @JsonNames("description")
  val description: String,
  @JsonNames("artifact-id", "artifact-name", "artifact")
  val artifactId: String,
  @JsonNames("licenses")
  val licenses: List<LicenseInfoSpec>?,
  
  @JsonNames("developers")
  val developers: Set<DeveloperInfoSpec>?,
  @JsonNames("developers-ref")
  val developersRef: String?,
  
  @JsonNames("scm")
  val scm: ScmConnectionInfoSpec?)

/**
 * Data class representing license information.
 *
 * @property name the name of the license
 * @property value the value of the license
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class LicenseInfoSpec(
  @JsonNames("name")
  val name: String,
  @JsonNames("value")
  val value: String)

/**
 * Data class representing developer information.
 *
 * @property id the ID of the developer
 * @property name the name of the developer
 * @property email the email of the developer
 * @property organization the organization of the developer
 * @property organizationUrl the organization URL of the developer
 * @property roles the roles of the developer
 * @property timezone the timezone of the developer
 * @property url the URL of the developer
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class DeveloperInfoSpec(
  @JsonNames("id")
  val id: String,
  @JsonNames("name")
  val name: String?,
  @JsonNames("email")
  val email: String?,
  @JsonNames("organization")
  val organization: String?,
  @JsonNames("organization-url", "url-organization")
  val organizationUrl: String?,
  @JsonNames("roles")
  val roles: List<String>?,
  @JsonNames("timezone", "time-zone")
  val timezone: String?,
  @JsonNames("url")
  val url: String?)

/**
 * Data class representing SCM connection information.
 *
 * @property url the URL of the SCM
 * @property branch the branch of the SCM
 * @property connection the connection of the SCM
 * @property developerConnection the developer connection of the SCM
 */
@Suppress("OPT_IN_USAGE")
@Serializable
data class ScmConnectionInfoSpec(
  @JsonNames("url")
  val url: String,
  @JsonNames("branch")
  val branch: String,
  @JsonNames("connection")
  val connection: String?,
  @JsonNames("connection-dev", "dev-connection")
  val developerConnection: String?)