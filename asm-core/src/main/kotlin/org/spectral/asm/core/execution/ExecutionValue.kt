package org.spectral.asm.core.execution

/**
 * Represents a value in the LVT or that has been pushed or popped to/from the stack.
 * Holds references to the [ExecutionFrame]s which have pushed or poppled this value.
 *
 * @property pusher ExecutionFrame
 * @property poppers List<ExecutionFrame>
 */
interface ExecutionValue {

    /**
     * The frame which pushed or created this value.
     */
    val pusher: ExecutionFrame

    /**
     * The frames which have popped this value.
     */
    val poppers: List<ExecutionFrame>

    /**
     * The original frame which this frame was dupped from.
     */
    val copySource: ExecutionFrame?

    /**
     * Gets the value casted to [T] of this object.
     *
     * @return T
     */
    fun <T> value(): T

}