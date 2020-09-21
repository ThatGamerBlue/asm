package org.spectral.asm.core.execution

import org.spectral.asm.core.code.Instruction

/**
 * Represents a snapshot in time of a [Frame] after a given
 * instruction has been executed.
 *
 * @property insn The instruction executed during this state.
 * @constructor
 */
class ExecutionState(val insn: Instruction) {

    constructor(insn: Instruction, other: ExecutionState) : this(insn) {
        this.stack.addAll(other.stack)
        this.lvt.addAll(other.lvt)
    }

    /**
     * The stack after the [insn] was executed.
     */
    val stack = mutableListOf<StackValue>()

    /**
     * The LVT after the [insn] was executed.
     */
    val lvt = mutableListOf<StackValue>()

    /**
     * Values pushed to the stack during this execution state.
     */
    val pushes = mutableListOf<StackValue>()

    /**
     * The values popped from the stack during this execution state.
     */
    val pops = mutableListOf<StackValue>()

}