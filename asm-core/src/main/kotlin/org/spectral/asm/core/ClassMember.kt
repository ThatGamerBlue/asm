package org.spectral.asm.core

/**
 * Represents a type which is contained within a [Class] object.
 *
 * @property owner Class
 */
interface ClassMember : Node {

    /**
     * The owning class object.
     */
    val owner: Class

    /**
     * The descriptor of the member node.
     */
    val desc: String

    /**
     * Gets whether the member node is static or not.
     *
     * @return Boolean
     */
    fun isStatic(): Boolean

    /**
     * Gets whether the member node is private or not.
     *
     * @return Boolean
     */
    fun isPrivate(): Boolean
}