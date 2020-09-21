package org.spectral.asm.core.execution

import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Represents a value which is pushed, popped from the stack or loaded
 * or stored from the LVT.
 *
 * Holds references to which [ExecutionState] accessed or pushed this value to the stack.
 *
 * @property value The value object stored on the stack.
 * @constructor
 */
class StackValue(val value: AbstractValue) {

    /**
     * The state which this value was pushed to the stack in.
     */
    lateinit var pusher: ExecutionState

    /**
     * The states which popped this value off the stack in.
     */
    val poppers = mutableListOf<ExecutionState>()

}