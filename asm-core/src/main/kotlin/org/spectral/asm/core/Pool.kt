package org.spectral.asm.core

/**
 * A collection or pool of elements.
 *
 * @param T
 */
interface Pool<T> {

    /**
     * The number of elements in the pool.
     */
    val size: Int

    /**
     * Adds an element to the pool collection.
     *
     * @param element T
     * @return Boolean
     */
    fun add(element: T): Boolean

    /**
     * Removes an element from the pool collection.
     *
     * @param element T
     * @return Boolean
     */
    fun remove(element: T): Boolean

    /**
     * Gets whether a given element is in the pool collection or not.
     *
     * @param element T
     * @return Boolean
     */
    fun contains(element: T): Boolean

    /**
     * Invoke an action for each element in the pool
     *
     * @param action Function1<T, Unit>
     */
    fun forEach(action: (T) -> Unit)

    /**
     * Gets the first element in the pool which the given predicate returns true.
     *
     * @param predicate Function1<T, Boolean>
     * @return T?
     */
    fun firstOrNull(predicate: (T) -> Boolean): T?

    /**
     * Removes all the elements from the pool.
     */
    fun clear()

}