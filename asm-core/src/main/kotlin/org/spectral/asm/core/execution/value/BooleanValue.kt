package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class BooleanValue(private val data: Boolean) : IntValue(if(data) 1 else 0) {

    override val type = Type.BOOLEAN_TYPE

    override val booleanValue = data

    override fun copy(): BooleanValue {
        return BooleanValue(data).apply { this.copySource = this@BooleanValue }
    }

    override fun toString(): String {
        return "BOOLEAN[value=$data]"
    }
}