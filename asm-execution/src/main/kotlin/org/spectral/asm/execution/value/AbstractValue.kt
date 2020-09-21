package org.spectral.asm.execution.value

import org.objectweb.asm.Type
import org.spectral.asm.execution.exception.ExecutionException

abstract class AbstractValue(private val data: Any?) {

    abstract val type: Type

    open val value: Any? = data

    open val intValue: Int get() = throw ExecutionException(UnsupportedOperationException())

    open val booleanValue: Boolean get() = throw ExecutionException(UnsupportedOperationException())

    open val doubleValue: Double get() = throw ExecutionException(UnsupportedOperationException())

    open val floatValue: Float get() = throw ExecutionException(UnsupportedOperationException())

    open val longValue: Long get() = throw ExecutionException(UnsupportedOperationException())

    var copySource: AbstractValue? = null

    abstract fun copy(): AbstractValue

    override fun toString(): String {
        return "VALUE[value=$value]"
    }
}