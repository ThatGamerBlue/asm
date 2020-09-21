package org.spectral.asm.core.execution

import org.spectral.asm.core.code.Instruction

/**
 * Represents a snapshot in time of a [Frame] after a given
 * instruction has been executed.
 *
 * @property insn The instruction executed during this state.
 * @property frame The live [Frame] this state belongs to.
 * @constructor
 */
class ExecutionState(val insn: Instruction, private val frame: Frame) {

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

    /**
     * The values loaded from the LVT during this execution state.
     */
    val loads = mutableListOf<StackValue>()

}