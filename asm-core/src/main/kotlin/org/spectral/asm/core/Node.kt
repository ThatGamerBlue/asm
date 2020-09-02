package org.spectral.asm.core

import org.objectweb.asm.Type

/**
 * An ASM framework node element.
 */
interface Node {

    /**
     * The pool this element belongs to.
     */
    val pool: ClassPool

    /**
     * Name of the node element.
     */
    val name: String

    /**
     * ASM Node element.
     */
    val node: Any

    /**
     * The bit-packed visibility and modifier flags of the node.
     */
    val access: Int

    /**
     * The ASM [Type] of this object.
     */
    val type: Type

}