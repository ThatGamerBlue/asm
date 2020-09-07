package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldNode
import java.lang.reflect.Modifier

class Field private constructor(
        val pool: ClassPool,
        val owner: Class,
        override val node: FieldNode,
        override val real: Boolean
) : FieldVisitor(ASM8), ClassMember<Field> {

    constructor(pool: ClassPool, owner: Class, node: FieldNode) : this(pool, owner, node, true)

    constructor(pool: ClassPool, owner: Class, name: String, desc: String) : this(pool, owner, fieldnode(name, desc), false)

    internal fun init() {
        typeClass = pool.getOrCreate(type)
    }

    override val name get() = node.name

    override val desc get() = node.desc

    override val access get() = node.access

    override val type = Type.getType(desc)

    lateinit var typeClass: Class

    override val isStatic: Boolean get() = Modifier.isStatic(access)

    override val isPrivate: Boolean get() = Modifier.isPrivate(access)

    val value: Any? get() = node.value

    fun accept(visitor: ClassVisitor) {
        node.accept(visitor)
        init()
    }

    override fun toString(): String {
        return "$owner.$name"
    }

    companion object {
        private fun fieldnode(name: String, desc: String): FieldNode {
            return FieldNode(0, name, desc, null, null)
        }
    }
}