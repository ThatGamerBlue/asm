package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.util.concurrent.ConcurrentHashMap

class Class private constructor(
        val pool: ClassPool,
        override val node: ClassNode,
        override val type: Type,
        override val real: Boolean
): ClassVisitor(ASM8, node), Node<Class> {

    constructor(pool: ClassPool, node: ClassNode) : this(pool, node, Type.getObjectType(node.name), true)

    constructor(pool: ClassPool, name: String) : this(pool, classnode(name.replace(".", "/")), Type.getObjectType(name.replace(".", "/")), false)

    constructor(pool: ClassPool, type: Type) : this(pool, classnode(type.className.replace(".", "/")), type, false)

    val name get() = node.name

    var elementClass: Class? = null

    val access get() = node.access

    var parent: Class? = null
        private set

    val children = hashSetOf<Class>()

    var interfaces = hashSetOf<Class>()

    val implementers = hashSetOf<Class>()

    val isArray: Boolean get() = type.sort == Type.ARRAY

    private val methodMap = ConcurrentHashMap<Type, Method>()

    val methods: List<Method> get() = methodMap.values.toList()

    private val fieldMap = ConcurrentHashMap<Type, Field>()

    val fields: List<Field> get() = fieldMap.values.toList()

    fun accept(classVisitor: ClassVisitor) {
        node.accept(classVisitor)

        parent = pool.getOrCreate(node.superName)
        parent?.children?.add(this)

        interfaces.clear()
        interfaces.addAll(node.interfaces.mapNotNull { pool.getOrCreate(it) })
        interfaces.forEach { it.implementers.add(this) }

        if(isArray) {
            elementClass = pool.getOrCreate(name.replace("[]", ""))
        }

        node.methods.forEach { methodMap[Type.getMethodType(it.desc)] = Method(pool, this, it) }
        node.fields.forEach { fieldMap[Type.getType(it.desc)] = Field(pool, this, it) }

        methods.forEach { it.init() }
        fields.forEach { it.init() }
    }

    override fun toString(): String {
        return name
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