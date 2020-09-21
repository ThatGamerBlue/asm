package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class DoubleValue(private var data: Double) : AbstractValue(data), WideValue {

    override val type = Type.DOUBLE_TYPE

    override val value = data

    override val doubleValue = data

    override fun copy(): DoubleValue {
        return DoubleValue(data).apply { this.copySource = this@DoubleValue }
    }

    override fun toString(): String {
        return "DOUBLE[value=$data]"
    }
}