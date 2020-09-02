package org.spectral.asm.core

/**
 * An ASM framework node element.
 */
interface Node<T> {

    /**
     * The pool this element belongs to.
     */
    val pool: Pool<T>

    /**
     * Name of the node element.
     */
    val name: String

    /**
     * ASM Node element.
     */
    val node: Any

}