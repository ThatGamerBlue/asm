package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type

class ObjectValue(private val data: Any, override val type: Type) : AbstractValue(data) {

    override val value = data

    override fun copy(): ObjectValue {
        this.copySource = this
        return this
    }

    override fun toString(): String {
        return "OBJECT@${Integer.toHexString(System.identityHashCode(this))}[value=$data, type=$type]"
    }
}