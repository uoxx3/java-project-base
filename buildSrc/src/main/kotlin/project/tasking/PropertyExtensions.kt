package project.tasking

import org.gradle.api.provider.Property
import java.util.*

fun <T, V : Any> Optional<T>.ifPresentApply(action: T.() -> V?): Optional<V> {
	if (!isPresent) return Optional.empty()
	return Optional.ofNullable(action(get()))
}

fun <T> Property<T>.ifPresent(action: (T) -> Unit): Property<T> {
	if (!isPresent) return this
	
	return also {
		action(it.get())
	}
}