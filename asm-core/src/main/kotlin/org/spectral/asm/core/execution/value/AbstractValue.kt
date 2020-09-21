package org.spectral.asm.core.execution.value

import org.objectweb.asm.Type
import org.spectral.asm.core.execution.StackValue
import org.spectral.asm.core.execution.exception.ExecutionException

abstract class AbstractValue(private val data: Any?) {

    internal var stackValue: StackValue? = null

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