package project

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/* -----------------------------------------------------
 * Project Types
 * ----------------------------------------------------- */

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
		val buildInfo: BuildInfoSpec,
		@JsonNames("publications")
		val publications: List<PublicationInfoSpec>?)

@Suppress("OPT_IN_USAGE")
@Serializable
data class BuildInfoSpec(
		@JsonNames("package", "package-out", "out-package")
		val outPackage: String,
		@JsonNames("file-name", "file", "name")
		val filename: String
)

/* -----------------------------------------------------
 * Publication Types
 * ----------------------------------------------------- */

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

@Suppress("OPT_IN_USAGE")
@Serializable
data class ArtifactInfoSpec(
		@JsonNames("task-name", "name", "task")
		val taskName: String,
		@JsonNames("required")
		val required: Boolean = false)

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
		val licenses: List<LicenseInfoSpec>,
		
		@JsonNames("developers")
		val developers: Set<DeveloperInfoSpec>?,
		@JsonNames("developers-ref")
		val developersRef: String?,
		
		@JsonNames("scm")
		val scm: ScmConnectionInfoSpec?)

@Suppress("OPT_IN_USAGE")
@Serializable
data class LicenseInfoSpec(
		@JsonNames("name")
		val name: String,
		@JsonNames("value")
		val value: String)

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