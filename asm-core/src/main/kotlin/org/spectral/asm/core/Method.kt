package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Modifier

class Method private constructor(
        val pool: ClassPool,
        val owner: Class,
        override val node: MethodNode,
        override val real: Boolean
) : MethodVisitor(ASM8, node), ClassMember<Method> {

    constructor(pool: ClassPool, owner: Class, node: MethodNode) : this(pool, owner, node, true)

    constructor(pool: ClassPool, owner: Class, name: String, desc: String) : this(pool, owner, methodnode(name, desc), false)

    constructor(pool: ClassPool, owner: Class, type: Type) : this(pool, owner, methodnode(type.className, type.descriptor), false)

    private fun init() {
        node.accept(this)
    }

    override val name get() = node.name

    override val desc get() = node.desc

    override val access get() = node.access

    override val type: Type = Type.getMethodType(desc)

    override val isStatic: Boolean get() = Modifier.isStatic(access)

    override val isPrivate: Boolean get() = Modifier.isPrivate(access)

    val isConstructor: Boolean get() = name == "<init>"

    val isInitializer: Boolean get() = name == "<clinit>"

    fun accept(visitor: ClassVisitor) {
        node.accept(visitor)
        init()
    }

    fun accept(visitor: MethodVisitor) {
        node.accept(visitor)
        init()
    }

    companion object {
        private fun methodnode(name: String, desc: String): MethodNode {
            return MethodNode(ASM8).apply {
                this.name = name
                this.desc = desc
            }
        }
    }
}