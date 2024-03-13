package project.tasking

import org.gradle.api.provider.Property
import java.util.*

/**
 * If a value is present, apply the provided function to it, and if the result is non-null, return an Optional
 * describing the result. Otherwise, return an empty Optional.
 *
 * @param action the function to apply to the value, if present
 * @return an Optional describing the result of applying a function to the value of this Optional, if a value
 * is present; otherwise, an [Optional.empty]
 */
fun <T, V : Any> Optional<T>.ifPresentApply(action: T.() -> V?): Optional<V> = if (!isPresent) {
  Optional.empty()
} else {
  Optional.ofNullable(action(get()))
}

/**
 * If a value is present, performs the given action with the value, otherwise returns this Property.
 *
 * @param action the action to be performed, if a value is present
 * @return this Property
 */
fun <T> Property<T>.ifPresent(action: (T) -> Unit): Property<T> = if (!isPresent) {
  this
} else {
  also { action(it.get()) }
}