package org.spectral.asm.core

/**
 * Abstract pool
 *
 * @param T
 * @constructor Create empty Abstract pool
 */
abstract class AbstractPool<T> : Pool<T> {

    private val elements = mutableListOf<T>()

    override val size: Int get() = elements.size

    override fun add(element: T): Boolean {
        return elements.add(element)
    }

    override fun remove(element: T): Boolean {
        return elements.remove(element)
    }

    override fun clear() {
        elements.clear()
    }

    override fun contains(element: T): Boolean {
        return elements.contains(element)
    }

    override fun forEach(action: (T) -> Unit) {
        elements.forEach(action)
    }

    override fun firstOrNull(predicate: (T) -> Boolean): T? {
        return elements.firstOrNull(predicate)
    }

    /**
     * Iterate and run an action for each element in the pool
     * Provides a [ListIterator] instance to make concurrent modifications to the
     * backing list.
     *
     * @param action
     * @receiver [T] and [MutableIterator]
     */
    fun iterate(action: (T, MutableIterator<T>) -> Unit) {
        val it = elements.listIterator()
        while(it.hasNext()) {
            action(it.next(), it)
        }
    }
}