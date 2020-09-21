package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class ByteValue(private val data: Byte) : IntValue(data.toInt()) {

    override val type = Type.BYTE_TYPE

    val byteValue = data

    override fun copy(): ByteValue {
        return ByteValue(data).apply { this.copySource = this@ByteValue }
    }

    override fun toString(): String {
        return "BYTE[value=$data]"
    }
}