package org.spectral.asm.core

import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode

class Method(override val owner: Class, private val node: MethodNode) : Member {

    override var name = node.name

    override var desc = node.desc

    override val type get() = Type.getMethodType(desc)

    override val annotations = mutableListOf<Annotation>()

    override var access = node.access

    override fun initialize() {

    }

    override fun toString(): String {
        return "$owner.$name$desc"
    }
}