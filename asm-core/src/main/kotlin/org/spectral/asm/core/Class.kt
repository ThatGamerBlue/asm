package org.spectral.asm.core

import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

/**
 * Represents an ASM ClassNode class.
 *
 * @property pool
 * @property node
 * @constructor Create empty Class
 */
class Class(override val pool: Pool<Class>, override val node: ClassNode) : Node<Class> {

    /**
     * The name of the class element.
     */
    override val name = node.name

    /**
     * The ASM type of the class.
     */
    val type: Type = Type.getObjectType(name)
}