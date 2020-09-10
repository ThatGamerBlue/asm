package org.spectral.asm.core

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode
import org.spectral.asm.core.code.Code

class Method(override val owner: Class, private val node: MethodNode) : Member {

    override var name = node.name

    override var desc = Descriptor(node.desc)

    override val type get() = Type.getMethodType(desc.toString())

    override val annotations = mutableListOf<Annotation>()

    override var access = node.access

    val exceptions = mutableListOf<String>()

    val code = Code(this)

    override fun initialize() {
        annotations.clear()
        exceptions.clear()

        node.visibleAnnotations?.forEach {
            annotations.add(Annotation(it))
        }

        node.exceptions?.forEach {
            exceptions.add(it)
        }
    }

    fun accept(visitor: MethodVisitor) {

    }

    override fun toString(): String {
        return "$owner.$name$desc"
    }
}