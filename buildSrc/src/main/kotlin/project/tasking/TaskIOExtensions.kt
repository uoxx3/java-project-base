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

fun Task.writeToFile(file: File, opts: Array<out OpenOption>, action: (OutputStream) -> Unit) {
	// Convert the file in the Path object
	val filePath = file.toPath()
	try {
		Files.newOutputStream(filePath, *opts).use(action)
	} catch (_: IOException) {
	}
}