package project.tasking

import org.gradle.api.Task
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import java.util.*

/* -----------------------------------------------------
 * Task group info
 * ----------------------------------------------------- */

/**
 * Get the group name as a file string. Replaces special characters with slashes.
 *
 * @param groupName the group name to convert
 * @param file the file name to append
 * @return the group name converted to a file string with the file name appended
 */
fun Task.getGroupAsFileStr(groupName: String?, file: String): String {
  // Check if the group name is valid
  var result = (groupName ?: "").trim()
  if (result.isBlank() && file.isBlank()) return ""
  
  // Replace all special characters
  val regexReplace = Regex("([.\\-_])")
  result = result.replace(regexReplace, "/")
  
  if (!result.endsWith("/")) {
    result += "/"
  }
  
  return (result + file)
}

/* -----------------------------------------------------
 * File generator
 * ----------------------------------------------------- */

/**
 * Generate a directory if it does not already exist.
 *
 * @param file the directory to generate
 * @param recursive whether to create parent directories if necessary
 * @return an Optional containing the directory if it was generated successfully, otherwise an [Optional.empty]
 */
fun Task.generateDirectory(file: File, recursive: Boolean = true): Optional<File> {
  // Check if the directory already exists
  if (file.exists() && file.isDirectory) return Optional.of(file)
  
  // Convert the file in the Path object
  val filePath = file.toPath()
  
  // Try to create recursively
  return try {
    if (recursive) {
      Files.createDirectories(filePath)
    } else {
      Files.createDirectory(filePath)
    }
    Optional.of(file)
  } catch (e: IOException) {
    Optional.empty()
  }
}

/**
 * Generate a file if it does not already exist.
 *
 * @param file the file to generate
 * @param recursive whether to create parent directories if necessary
 * @param opts the options to use when opening the file
 * @return an Optional containing the file if it was generated successfully, otherwise an [Optional.empty]
 */
fun Task.generateFile(
  file: File,
  recursive: Boolean = true,
  vararg opts: OpenOption = arrayOf(StandardOpenOption.CREATE)
): Optional<File> {
  // Check if the directory already exists
  if (file.exists()) return Optional.of(file)
  
  // Try to create parent directory
  return generateDirectory(file.parentFile, recursive).ifPresentApply {
    return@ifPresentApply try {
      writeToFile(file, opts) { /*Do nothing*/ }
      file
    } catch (e: IOException) {
      null
    }
  }
}

/**
 * Write to a file.
 *
 * @param file the file to write to
 * @param opts the options to use when opening the file
 * @param action the action to perform on the output stream
 */
fun Task.writeToFile(file: File, opts: Array<out OpenOption>, action: (OutputStream) -> Unit) {
  // Convert the file in the Path object
  val filePath = file.toPath()
  try {
    Files.newOutputStream(filePath, *opts).use(action)
  } catch (_: IOException) {
  }
}