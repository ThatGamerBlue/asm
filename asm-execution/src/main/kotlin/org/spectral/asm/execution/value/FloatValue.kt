package org.spectral.asm.execution.value

import org.objectweb.asm.Type

class FloatValue(private val data: Float) : AbstractValue(data) {

    override val type = Type.FLOAT_TYPE

    override val value = data

    override val floatValue = data

    override fun copy(): FloatValue {
        return FloatValue(data).apply { this.copySource = this@FloatValue }
    }

    override fun toString(): String {
        return "FLOAT[value=$data]"
    }
}