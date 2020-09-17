package org.spectral.asm.core

/**
 * Represents a collection of [Class] java bytecode objects.
 *
 * @property classMap HashMap<String, Class>
 */
class ClassPool : MutableList<Class> {

    /**
     * Backing storage of classes to track order.
     */
    private val classes = mutableListOf<Class>()

    /**
     * Backing storage of classes by their name -> instance
     */
    private var classMap = mutableMapOf<String, Class>()

    /**
     * The number of classes in the pool.
     */
    override val size get() = classes.size

    override fun contains(element: Class): Boolean = classes.contains(element)

    override fun containsAll(elements: Collection<Class>): Boolean = classes.containsAll(elements)

    override operator fun get(index: Int): Class = classes[index]

    operator fun get(name: String): Class? = classMap[name]

    override fun indexOf(element: Class): Int = classes.indexOf(element)

    override fun isEmpty(): Boolean = classes.isEmpty()

    override fun iterator(): MutableIterator<Class> = classes.listIterator()

    override fun lastIndexOf(element: Class): Int = classes.lastIndexOf(element)

    override fun add(element: Class): Boolean = classes.add(element).apply { rebuildClassMap() }

    override fun add(index: Int, element: Class) = classes.add(index, element).apply { rebuildClassMap() }

    override fun addAll(index: Int, elements: Collection<Class>): Boolean = classes.addAll(index, elements).apply { rebuildClassMap() }

    override fun addAll(elements: Collection<Class>): Boolean = classes.addAll(elements)

    override fun clear() = classes.clear().apply { rebuildClassMap() }

    override fun listIterator(): MutableListIterator<Class> = classes.listIterator()

    override fun listIterator(index: Int): MutableListIterator<Class> = classes.listIterator(index)

    override fun remove(element: Class): Boolean = classes.remove(element).apply { rebuildClassMap() }

    override fun removeAll(elements: Collection<Class>): Boolean = classes.removeAll(elements).apply { rebuildClassMap() }

    override fun removeAt(index: Int): Class = classes.removeAt(index).apply { rebuildClassMap() }

    override fun retainAll(elements: Collection<Class>): Boolean = classes.retainAll(elements)

    override fun set(index: Int, element: Class): Class = classes.set(index, element).apply { rebuildClassMap() }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Class> = classes.subList(fromIndex, toIndex)

    private fun rebuildClassMap() {
        classMap = classes.associate { it.name to it }.toMutableMap()
    }
}