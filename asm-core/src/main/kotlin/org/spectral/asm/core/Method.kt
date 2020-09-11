package org.spectral.asm.core

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.tree.MethodNode
import org.spectral.asm.core.util.ObservableList
import kotlin.properties.Delegates

class Method(val owner: Class, private val node: MethodNode) : Node {

    override var access = node.access

    override var name = node.name

    var desc = node.desc
        private set

    override val type get() = Type.getMethodType(desc)

    val argumentsTypes = ObservableList(type.argumentTypes.toMutableList()) {
        visitDesc()
    }

    var returnType by Delegates.observable(type.returnType) { _, _, _ ->
        visitDesc()
    }

    private fun visitDesc() {
        val desc = StringBuilder()
        if(argumentsTypes.isNotEmpty()) {
            desc.append('(')
            argumentsTypes.forEach { desc.append(it.toString()) }
            desc.append(')')
        }
        desc.append(returnType.toString())
        this.desc = desc.toString()
    }

    fun accept(visitor: MethodVisitor) {

    }

    override fun toString(): String {
        return "$owner.$name$desc"
    }
}