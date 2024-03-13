package generator

import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import project.tasking.generateFile
import project.tasking.ifPresent
import project.tasking.writeToFile
import java.io.File
import java.nio.file.StandardOpenOption
import java.util.*

/**
 * Abstract task for generating properties.
 */
abstract class TaskPropertyGenerator : DefaultTask(), ITaskGenerator {
  
  /**
   * The file property.
   */
  @get:Input
  abstract val file: Property<File>
  
  /**
   * The properties map.
   */
  @get:Input
  abstract val properties: MapProperty<String, String>
  
  /**
   * Method that will be executed when the task is called
   */
  @TaskAction
  override fun actionGenerator() {
    // Check file property exists
    file.ifPresent { ff ->
      // Check if the property object exists
      val propertyMap = properties.getOrElse(mutableMapOf())
      val resultProps = Properties().also { it.putAll(propertyMap) }
      
      val openOpts = arrayOf(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
      generateFile(ff, true, *openOpts).ifPresent { f ->
        writeToFile(f, openOpts) { o ->
          resultProps.store(o, null)
        }
      }
    }
  }
  
}