package project.tasking

import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import java.util.*

fun Task.getSourceSetByName(name: String): Optional<SourceSet> {
	// Get sourceSet container object
	val container = project.extensions.findByType(SourceSetContainer::class.java) ?: return Optional.empty()
	
	return Optional.ofNullable(container.findByName(name))
}

fun Task.configureSourceSets(names: Iterable<String>, action: (SourceSet) -> Unit) =
		names.forEach {
			getSourceSetByName(it).ifPresent(action)
		}

fun <T> Jar.fromSourceSets(vararg names: String, action: (SourceSet) -> T) {
	val sourceObjects = names.map(this::getSourceSetByName)
			.filter { it.isPresent }
			.map { it.get() }
			.map { action(it) }
	
	from(sourceObjects)
}

fun Jar.fromTasks(vararg names: String) {
	val tasks = names.mapNotNull { project.tasks.findByName(it) }
	
	// Configure Tasks
	from(tasks)
	tasks.forEach { dependsOn(it) }
}