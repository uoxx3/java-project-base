package generator

import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import project.tasking.configureSourceSets
import project.tasking.generateFile
import project.tasking.getGroupAsFileStr
import project.tasking.writeToFile
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.*

/**
 * Abstract task for generating build information.
 */
abstract class TaskBuildInfoGenerator : DefaultTask(), ITaskGenerator {
  
  /**
   * The module name property.
   */
  @get:Optional
  @get:Input
  abstract val moduleName: Property<String>
  
  /**
   * The group name property.
   */
  @get:Optional
  @get:Input
  abstract val groupName: Property<String>
  
  /**
   * The project name property.
   */
  @get:Optional
  @get:Input
  abstract val projectName: Property<String>
  
  /**
   * The extra properties map.
   */
  @get:Input
  abstract val extraProperties: MapProperty<String, String>
  
  /**
   * Property file name
   */
  @get:Input
  abstract val filename: Property<String>
  
  private val conventionFilename: Property<String>
    get() = filename.convention("build.properties")
  
  /**
   * Method that will be executed when the task is called
   */
  @TaskAction
  override fun actionGenerator() = configureSourceSets(listOf("main")) { set ->
    // Generate resource file location
    val sourceDir = set.resources.srcDirs.firstOrNull() ?: return@configureSourceSets
    val location = Path.of(
      sourceDir.toString(),
      getGroupAsFileStr(moduleName.orNull, conventionFilename.get())).toFile()
    
    // Combine the default properties with the extra properties
    val extraMap = extraProperties.getOrElse(mutableMapOf())
    val properties = Properties().also { it.putAll(extraMap) }
    
    properties.run {
      put("build.name", projectName.getOrElse("<null>"))
      put("build.group", groupName.getOrElse("<null>"))
      put("build.version.name", (project.version as String?) ?: "<null>")
      put("build.timestamp", System.currentTimeMillis().toString())
      
      put("build.java.vendor", System.getProperty("java.vendor"))
      put("build.java.version", System.getProperty("java.version"))
    }
    
    // Generate file
    val openOpts = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    generateFile(location, true, *openOpts).ifPresent { f ->
      writeToFile(f, openOpts) { o ->
        properties.store(o, null)
      }
    }
  }
  
}