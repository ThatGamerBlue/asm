package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

class Class(val pool: ClassPool, private val node: ClassNode) : Node {

    override var name = node.name

    override var access = node.access

    override val type get() = Type.getObjectType(name)

    var superName = node.superName

    val interfaces = node.interfaces.toMutableList()

    val children = mutableListOf<Class>()

    val parent: Class? get() = pool[superName]

    val methods = node.methods.map { Method(this, it) }

    fun accept(visitor: ClassVisitor) {

    }

    override fun toString(): String {
        return name
    }
}