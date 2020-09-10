package org.spectral.asm.core

import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.FieldNode

class Field(override val owner: Class, private val node: FieldNode) : Member {

    override var name = node.name

    override var access = node.access

    override var desc = Descriptor(mutableListOf(), Type.getType(node.desc))

    override val type get() = Type.getType(desc.toString())

    var value: Any? = node.value

    override val annotations = mutableListOf<Annotation>()

    override fun initialize() {
        annotations.clear()

        node.visibleAnnotations?.forEach {
            annotations.add(Annotation(it))
        }
    }

    fun accept(visitor: FieldVisitor) {
        annotations.forEach {
            it.accept(visitor.visitAnnotation(it.type.toString(), true))
        }

        visitor.visitEnd()
    }

    override fun toString(): String {
        return "$owner.$name"
    }
}