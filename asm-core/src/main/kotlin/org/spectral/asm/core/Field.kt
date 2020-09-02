package org.spectral.asm.core

import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldNode
import java.lang.reflect.Modifier

/**
 * Represents an ASM FieldNode object.
 *
 * @property pool ClassPool
 * @property owner Class
 * @property node FieldNode
 * @constructor
 */
class Field(
        override val pool: ClassPool,
        override val owner: Class,
        override val node: FieldNode,
        override val real: Boolean
) : ClassMember {

    /**
     * Creates a virtual or fake field.
     *
     * @param pool ClassPool
     * @param owner Class
     * @param name String
     * @param desc String
     * @constructor
     */
    constructor(pool: ClassPool, owner: Class, name: String, desc: String)
            : this(pool, owner, createVirtualFieldNode(name, desc), false)

    override val name = node.name

    override val desc = node.desc

    override val access = node.access

    override val type = Type.getType(desc)

    val value: Any? = node.value

    override fun isStatic(): Boolean = Modifier.isStatic(access)

    override fun isPrivate(): Boolean = Modifier.isPrivate(access)

    override fun toString(): String {
        return "$owner.$name"
    }

    companion object {
        /**
         * Creates a virtual [FieldNode] object.
         *
         * @param name String
         * @param desc String
         * @return FieldNode
         */
        private fun createVirtualFieldNode(name: String, desc: String): FieldNode {
            return FieldNode(0, name, desc, null, null)
        }
    }
}