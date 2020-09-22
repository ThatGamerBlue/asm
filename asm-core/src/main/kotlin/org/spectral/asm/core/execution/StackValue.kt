package org.spectral.asm.core.execution

import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Represents a value of a stack operand or LV created or accessed by a frame.
 *
 * @property value AbstractValue
 * @constructor
 */
class StackValue(val value: AbstractValue) {

    /**
     * The state which this value was pushed to the stack.
     */
    lateinit var pusher: ExecutionState

    /**
     * The states which this value was popped from the stack.
     */
    val poppers = mutableListOf<ExecutionState>()

    override fun toString(): String {
        return value.toString()
    }
}