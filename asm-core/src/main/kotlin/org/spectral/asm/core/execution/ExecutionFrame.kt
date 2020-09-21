package org.spectral.asm.core.execution

import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.value.AbstractValue
import org.spectral.asm.core.execution.value.TopValue

/**
 * Represents an execution frame as a snapshot in time after an instruction execution.
 */
interface ExecutionFrame {

    /**
     * The instruction which was execution during this frame.
     */
    val insn: Instruction

    /**
     * The values which were pushed to the stack during this execution frame.
     */
    val pushes: List<ExecutionValue>

    /**
     * The value which were popped from the stack during this execution frame.
     */
    val pops: List<ExecutionValue>

    /**
     * The values which were loaded from the LVT to the stack during this execution frame.
     */
    val loads: List<ExecutionValue>

    /**
     * The values which were saved to the LVT during this execution frame.
     */
    val stores: List<ExecutionValue>

    /**
     * Pushes a value to top of the stack.
     *
     * @param value AbstractValue
     */
    fun push(value: AbstractValue) {
        push(0, value)
    }

    /**
     * Pushes a 64bit (wide) value to the stack.
     *
     * @param value AbstractValue
     */
    fun pushWide(value: AbstractValue) {
        push(value)
        push(TopValue())
    }

    /**
     * Pushes / sets a value at [index] to the stack.
     *
     * @param index Int
     * @param value AbstractValue
     */
    fun push(index: Int, value: AbstractValue)

    /**
     * Pops a value from the top of the stack.
     *
     * @return ExecutionValue
     */
    fun pop(): ExecutionValue {
        return pop(0)
    }

    /**
     * Pops a 64bit (wide) value from the stack.
     *
     * @return ExecutionValue
     */
    fun popWide(): ExecutionValue {
        pop() // Pop the TOP value off the stack. Probably should assert this is correct.
        return pop()
    }

    /**
     * Pops a value at [index] from the stack.
     *
     * @param index Int
     * @return ExecutionValue
     */
    fun pop(index: Int): ExecutionValue

    /**
     * Loads a value from the LVT
     *
     * @param index Int
     * @return AbstractValue
     */
    fun load(index: Int): AbstractValue
}