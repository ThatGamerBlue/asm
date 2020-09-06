package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

class Class private constructor(
        val pool: ClassPool,
        override val node: ClassNode,
        override val type: Type,
        override val real: Boolean
): ClassVisitor(ASM8, node), Node<Class> {

    constructor(pool: ClassPool, node: ClassNode) : this(pool, node, Type.getObjectType(node.name), true)

    constructor(pool: ClassPool, name: String) : this(pool, classnode(name), Type.getObjectType(name), false)

    constructor(pool: ClassPool, type: Type) : this(pool, classnode(type.className), type, false)

    init {
        node.accept(this)
    }

    val name get() = node.name

    val access get() = node.access

    var parent: Class? = null
        private set

    val children = hashSetOf<Class>()

    var interfaces = hashSetOf<Class>()

    val implementers = hashSetOf<Class>()

    fun accept(classVisitor: ClassVisitor) {
        node.accept(classVisitor)

        parent = pool[node.superName]
        parent?.children?.add(this)

        interfaces.clear()
        interfaces.addAll(node.interfaces.mapNotNull { pool[it] })
        interfaces.forEach { it.implementers.add(this) }
    }

    companion object {
        private fun classnode(name: String): ClassNode {
            return ClassNode(ASM8).apply {
                this.name = name
                this.superName = "java/lang/Object"
            }
        }
    }
}