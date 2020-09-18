package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

/**
 * Represents a Java annotation.
 *
 * @property type Type
 * @constructor
 */
class Annotation(val type: Type) : AnnotationVisitor(ASM9) {

    private val data = hashMapOf<String, Any>()

    constructor(type: Type, value: Any) : this(type) {
        data["value"] = value
    }

    val size: Int get() = data.size

    operator fun set(name: String, value: Any) {
        data[name] = value
    }

    operator fun get(name: String): Any = data[name] ?: throw IllegalStateException("No annotation data value named '$name'")

    override fun visit(name: String, value: Any) {
        data[name] = value
    }

    override fun visitAnnotation(name: String, descriptor: String): AnnotationVisitor {
        val annotation = Annotation(Type.getType(descriptor))
        data[name] = annotation
        return annotation
    }

    override fun visitArray(name: String): AnnotationVisitor {
        val array = mutableListOf<Any>()
        data[name] = array

        return object : AnnotationVisitor(ASM9) {
            override fun visit(name: String, value: Any) { array.add(value) }
            override fun visitAnnotation(name: String, descriptor: String): AnnotationVisitor {
                val annotation = Annotation(Type.getType(descriptor))
                array.add(annotation)
                return annotation
            }
        }
    }

    fun accept(visitor: AnnotationVisitor) {
        data.entries.forEach { entry ->
            accept(visitor, entry.key, entry.value)
        }

        visitor.visitEnd()
    }

    private fun accept(visitor: AnnotationVisitor, name: String?, value: Any?) {
        when (value) {
            is Annotation -> {
                value.accept(visitor.visitAnnotation(name, type.descriptor))
            }
            is MutableList<*> -> {
                val arr = visitor.visitArray(name)

                value.forEach {
                    accept(arr, null, it)
                }

                arr.visitEnd()
            }
            else -> {
                visitor.visit(name, value)
            }
        }
    }
}