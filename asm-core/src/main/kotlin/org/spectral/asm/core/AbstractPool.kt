package org.spectral.asm.core

import java.util.concurrent.ConcurrentHashMap

/**
 * Abstract pool
 *
 * @param T
 * @constructor Create empty Abstract pool
 */
abstract class AbstractPool<T : Node> : Pool<T> {

    private val elements = ConcurrentHashMap<String, T>()

    override val size: Int get() = elements.size

    override fun add(element: T): Boolean {
        if(elements.containsKey(element.name)) {
            return false
        }

        elements[element.name] = element

        return true
    }

    override fun remove(element: T): Boolean {
        if(!elements.containsKey(element.name)) {
            return false
        }

        elements.remove(element.name)

        return true
    }

    override fun clear() {
        elements.clear()
    }

    override fun contains(element: T): Boolean {
        return elements.contains(element)
    }

    override fun forEach(action: (T) -> Unit) {
        elements.values.forEach(action)
    }

    override fun firstOrNull(predicate: (T) -> Boolean): T? {
        return elements.values.firstOrNull(predicate)
    }

    fun filter(predicate: (T) -> Boolean): List<T> {
        return elements.filterValues(predicate).values.toList()
    }

    fun removeIf(predicate: (T) -> Boolean) {
        this.elements.forEach { (name: String, cls: T) ->
            if(predicate(cls)) {
                this.elements.remove(name)
            }
        }
    }

    /**
     * Gets a element from the pool with a given name.
     *
     * @param name String
     * @return T?
     */
    operator fun get(name: String): T? {
        return elements[name]
    }

}