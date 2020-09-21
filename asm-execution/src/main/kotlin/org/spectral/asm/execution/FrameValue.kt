package org.spectral.asm.execution

import org.spectral.asm.core.execution.ExecutionValue
import org.spectral.asm.execution.value.*

class FrameValue(val data: AbstractValue) : ExecutionValue {

    override lateinit var pusher: Frame

    override val poppers = mutableListOf<Frame>()

    @Suppress("UNCHECKED_CAST")
    override fun <T> value(): T {
        return when(data) {
            is BooleanValue -> data.booleanValue
            is ByteValue -> data.byteValue
            is CharValue -> data.charValue
            is DoubleValue -> data.doubleValue
            is FloatValue -> data.floatValue
            is IntValue -> data.intValue
            is LongValue -> data.longValue
            is ShortValue -> data.shortValue
            else -> data.value
        } as T
    }
}