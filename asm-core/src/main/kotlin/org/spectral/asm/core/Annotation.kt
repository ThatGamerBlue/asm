package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import java.util.*

class Annotation(private val node: AnnotationNode) : AnnotationVisitor(ASM8), Comparable<Annotation> {

    private val data = TreeMap<String, Any>()

    val type: Type get() = Type.getType(node.desc)

    init {
        this.accept(node)
    }

    override fun visit(name: String, value: Any) {
        this[name] = value
    }

    operator fun set(name: String, value: Any) { data[name] = value }

    fun remove(name: String) { data.remove(name) }

    operator fun get(name: String): Any? = data[name]

    fun getValue(): Any? { return data["value"] }

    fun getValueString(): String? { return data["value"] as? String }

    fun accept(visitor: AnnotationVisitor) {
        data.entries.forEach {
            accept(visitor, it.key, it.value)
        }

        visitor.visitEnd()
    }

    private fun accept(visitor: AnnotationVisitor, name: String, value: Any) {
        visitor.visit(name, value)
    }

    override fun compareTo(other: Annotation): Int {
        return this.type.toString().compareTo(other.type.toString())
    }
}