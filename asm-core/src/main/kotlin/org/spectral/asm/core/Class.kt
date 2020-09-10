package org.spectral.asm.core

import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

class Class(val pool: ClassPool, private val node: ClassNode) : Node {

    override var name = node.name

    override var access = node.access

    override val type get() = Type.getObjectType(name)

    var source = node.sourceFile

    var version = node.version

    var parentName = node.superName
        set(value) {
            field = value
            parent = pool.findClass(value)
        }

    var parent: Class? = null
        set(value) {
            field = value
            value?.let {
                if(it.name != parentName) {
                    parentName = it.name
                }
            }
        }

    val children = mutableListOf<Class>()

    val interfaces = mutableListOf<String>()

    val fields = mutableListOf<Field>()

    override val annotations = mutableListOf<Annotation>()

    override fun initialize() {
        interfaces.clear()
        children.clear()
        parent = null
        annotations.clear()
        fields.clear()

        pool.findClass(node.superName)?.let {
            parent = it
            it.children.add(this)
        }

        node.interfaces.forEach { itf ->
            pool.findClass(itf)?.children?.add(this)
            interfaces.add(itf)
        }

        node.visibleAnnotations?.forEach {
            annotations.add(Annotation(it))
        }

        node.fields.forEach {
            fields.add(Field(this, it).apply { this.initialize() })
        }
    }

    override fun toString(): String {
        return name
    }
}