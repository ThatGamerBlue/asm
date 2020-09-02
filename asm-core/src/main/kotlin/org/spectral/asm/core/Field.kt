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
class Field(override val pool: ClassPool, override val owner: Class, override val node: FieldNode) : ClassMember {

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
}