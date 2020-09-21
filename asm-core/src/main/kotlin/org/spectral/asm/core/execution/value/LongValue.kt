package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class LongValue(private val data: Long) : AbstractValue(data), WideValue {

    override val type = Type.LONG_TYPE

    override val value = data

    override val longValue = data

    override fun copy(): LongValue {
        return LongValue(data).apply { this.copySource = this@LongValue }
    }

    override fun toString(): String {
        return "LONG[value=$data]"
    }
}