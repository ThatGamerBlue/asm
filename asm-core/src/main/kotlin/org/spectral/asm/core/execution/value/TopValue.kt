package org.spectral.asm.core.execution.value

import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.ext.UNKNOWN_TYPE

class TopValue : AbstractValue(null) {

    override val type = UNKNOWN_TYPE

    override fun copy(): AbstractValue {
        throw ExecutionException("Copying a TOP stack value is not permitted.")
    }

    override fun toString(): String {
        return "TOP"
    }
}