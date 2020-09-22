package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class CharValue(private val data: Char) : IntValue(data.toInt()) {

    override val type = Type.CHAR_TYPE

    val charValue = data

    override fun copy(): CharValue {
        return CharValue(data).apply { this.copySource = this@CharValue }
    }

    override fun toString(): String {
        return "CHAR[value=$data]"
    }
}