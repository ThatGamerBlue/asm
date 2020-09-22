package org.spectral.asm.core.execution.value

import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.ext.UNKNOWN_TYPE

class AddressValue(private val data: Instruction) : AbstractValue(data) {

    override val type = UNKNOWN_TYPE

    override val value = data

    override fun copy(): AddressValue {
        return AddressValue(data).apply { this.copySource = this@AddressValue }
    }

    override fun toString(): String {
        return "ADDRESS[value=$data]"
    }
}