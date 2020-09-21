package org.spectral.asm.core.execution

import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Represents a value in the LVT or that has been pushed or popped to/from the stack.
 * Holds references to the [ExecutionFrame]s which have pushed or poppled this value.
 *
 * @property pusher ExecutionFrame
 * @property poppers List<ExecutionFrame>
 */
interface ExecutionValue {

    /**
     * The associated stack data value
     */
    val data: AbstractValue

    /**
     * The frame which pushed or created this value.
     */
    val pusher: ExecutionFrame

    /**
     * The frames which have popped this value.
     */
    val poppers: List<ExecutionFrame>

}