package org.spectralpowered.asm.core

/**
 * An abstract implementation of a pool type.
 *
 * @param T
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
}