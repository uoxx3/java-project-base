package project.tasking

import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import java.util.*

/**
 * Gets a SourceSet by name from the project's SourceSetContainer.
 *
 * @param name the name of the SourceSet to retrieve
 * @return an Optional containing the SourceSet if found, otherwise an [Optional.empty]
 */
fun Task.getSourceSetByName(name: String): Optional<SourceSet> =
  project.extensions.findByType(SourceSetContainer::class.java)?.let {
    return@let Optional.ofNullable(it.findByName(name))
  } ?: Optional.empty()

/**
 * Configures SourceSets with the given names using the provided action.
 *
 * @param names the names of the SourceSets to configure
 * @param action the action to perform on each SourceSet
 */
fun Task.configureSourceSets(names: Iterable<String>, action: (SourceSet) -> Unit) =
  names.forEach {
    getSourceSetByName(it).ifPresent(action)
  }

/**
 * Creates a Jar from SourceSets with the specified names using the provided action.
 *
 * @param names the names of the SourceSets to include in the Jar
 * @param action the action to perform on each SourceSet
 * @param T the type of the result of applying the action
 */
fun <T> Jar.fromSourceSets(vararg names: String, action: (SourceSet) -> T) =
  names.map(this::getSourceSetByName)
    .filter { it.isPresent }
    .map { it.get() }
    .map(action)
    .also { from(it) }

/**
 * Creates a Jar from Tasks with the specified names.
 *
 * @param names the names of the Tasks to include in the Jar
 */
fun Jar.fromTasks(vararg names: String) =
  names.mapNotNull { project.tasks.findByName(it) }
    .also { from(it) }
    .forEach { dependsOn(it) }