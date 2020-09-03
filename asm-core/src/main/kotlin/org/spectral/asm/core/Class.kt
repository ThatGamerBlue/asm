package org.spectral.asm.core

import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

/**
 * Represents an ASM ClassNode class.
 *
 * @property pool
 * @property node
 * @property real
 * @constructor Create empty Class
 */
class Class(override val pool: ClassPool, override val node: ClassNode, override var type: Type, override val real: Boolean) : Node {

    /**
     * Creates a virtual or fake class
     *
     * @param pool ClassPool
     * @param name String
     * @constructor
     */
    constructor(pool: ClassPool, name: String) : this(pool, createVirtualClassNode(name), Type.getObjectType(name), false)

    /**
     * Creates a virtual or fake class of a primitive type.
     *
     * @param pool ClassPool
     * @param type Type
     * @constructor
     */
    constructor(pool: ClassPool, type: Type) : this(pool, createVirtualClassNode(type.className), type, false)

    /**
     * The name of the class element.
     */
    override val name = node.name

    /**
     * The modifier flags of the class.
     */
    override val access = node.access

    /**
     * Whether the class is an array class or not.
     */
    val isArray: Boolean = (type.sort == 9)

    /**
     * The element class type of the object is an array class.
     */
    var elementClass: Class? = null

    /**
     * The super class of this object.
     */
    var parent: Class? = null

    /**
     * The classes which extend this object.
     */
    val children = hashSetOf<Class>()

    /**
     * The interface classes of this object.
     */
    val interfaces = hashSetOf<Class>()

    /**
     * The classes which implement this object.
     */
    val implementers = hashSetOf<Class>()

    /**
     * The methods in this class object.
     */
    val methods = node.methods.map { Method(pool, this, it, true) }

    /**
     * The fields in this class object.
     */
    val fields = node.fields.map { Field(pool, this, it, true) }

    override fun toString(): String {
        return name
    }

    companion object {
        /**
         * Create a virtual or fake [ClassNode] object.
         *
         * @param name String
         * @return ClassNode
         */
        private fun createVirtualClassNode(name: String): ClassNode {
            return ClassNode(ASM8).apply {
                this.name = name.replace(".", "/")
                this.superName = "java/lang/Object"
            }
        }
    }
}