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
class Class(override val pool: ClassPool, override val node: ClassNode) : Node {

    /**
     * The name of the class element.
     */
    override val name = node.name

    /**
     * The ASM type of the class.
     */
    override val type: Type = Type.getObjectType(name)

    /**
     * The modifier flags of the class.
     */
    override val access = node.access

    /**
     * The super class of this object.
     */
    val parent: Class? = pool[node.superName]

    /**
     * The interface class of this object.
     */
    val interfaces: List<Class> = node.interfaces.mapNotNull { pool[it] }

    /**
     * The methods in this class object.
     */
    val methods: List<Method> = node.methods.map { Method(pool, this, it) }

    /**
     * The fields in this class object.
     */
    val fields: List<Field> = node.fields.map { Field(pool, this, it) }

    override fun toString(): String {
        return name
    }
}