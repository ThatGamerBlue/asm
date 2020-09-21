package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

open class IntValue(private val data: Int) : AbstractValue(data) {

    override val type = Type.INT_TYPE

    override val value = data

    override val intValue = data

    override fun copy(): IntValue {
        return IntValue(data).apply { this.copySource = this@IntValue }
    }

    override fun toString(): String {
        return "INT[value=$data]"
    }
}