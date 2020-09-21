package org.spectral.asm.execution.frame

import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.core.execution.ExecutionValue
import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Represents an execution frame.
 *
 * @property insn The instruction executed during this frame.
 * @property stack The JVM stack of this frame.
 * @property lvt the JVM local variable table of this frame.
 * @constructor
 */
class Frame internal constructor(
        override val insn: Instruction,
        val stack: MutableList<FrameValue>,
        val lvt: MutableList<FrameValue>
) : ExecutionFrame {

    /**
     * Creates a frame with a blank stack and lvt.
     *
     * @constructor
     */
    constructor(insn: Instruction) : this(insn, mutableListOf(), mutableListOf())

    /**
     * Reference collections
     */
    override val pushes = mutableListOf<FrameValue>()
    override val pops = mutableListOf<FrameValue>()
    override val loads = mutableListOf<FrameValue>()
    override val stores = mutableListOf<FrameValue>()

    /**
     * Push a value to the stack.
     *
     * @param value AbstractValue
     */
    override fun push(index: Int, value: AbstractValue) {
        val execValue = FrameValue(value)

        /*
         * Record the push
         */
        execValue.pusher = this
        pushes.add(execValue)

        /*
         * Push to the stack.
         */
        stack.add(index, execValue)
    }

    /**
     * Pop a value from [index] off the stack.
     *
     * @param index Int
     * @return ExecutionValue
     */
    override fun pop(index: Int): ExecutionValue {
        /*
         * Pop off the stack.
         */
        val execValue = stack.removeAt(index)

        /*
         * Record the pop
         */
        execValue.poppers.add(this)
        pops.add(execValue)

        return execValue
    }
}