package org.spectral.asm.execution.value

import org.objectweb.asm.Type

class ArrayValue(private val data: Array<*>) : AbstractValue(data) {

    override val type = Type.getObjectType("java/lang/Object")

    override val value = data

    override fun copy(): ArrayValue {
        return ArrayValue(data).apply { this.copySource = this@ArrayValue }
    }

    override fun toString(): String {
        return "ARRAY[size=${data.size}]"
    }
}