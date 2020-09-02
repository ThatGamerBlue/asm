package org.spectral.asm.core

/**
 * Represents a collection of [Class] objects from
 * a common classpath source.
 */
class ClassPool : AbstractPool<Class>() {

    /**
     * Gets a [Class] from the pool that matches a
     * given name.
     *
     * @param name String
     * @return Class?
     */
    operator fun get(name: String): Class? {
        return firstOrNull { it.name == name }
    }
}