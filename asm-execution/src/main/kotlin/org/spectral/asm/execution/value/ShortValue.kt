package org.spectral.asm.execution.value

import org.objectweb.asm.Type

class ShortValue(private val data: Short) : IntValue(data.toInt()) {

    override val type = Type.SHORT_TYPE

    val shortValue: Short = data

    override fun copy(): ShortValue {
        return ShortValue(data).apply { this.copySource = this@ShortValue }
    }

    override fun toString(): String {
        return "SHORT[value=$data]"
    }
}