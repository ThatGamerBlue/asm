package org.spectral.asm.core.execution

import org.spectral.asm.core.code.Instruction

/**
 * Represents an execution state of a given instruction.
 * Holds a snapshot in time of the JVM stack frame after [insn] executed.
 */
class ExecutionState(val insn: Instruction) {

    constructor(insn: Instruction, other: ExecutionState) : this(insn) {
        stack.addAll(other.stack)
        lvt = other.lvt
    }

    val stack = mutableListOf<StackValue>()

    var lvt = arrayOfNulls<StackValue>(insn.code.maxStack)

    val pushes = mutableListOf<StackValue>()

    val pops = mutableListOf<StackValue>()

    val writes = mutableListOf<StackValue>()

    val reads = mutableListOf<StackValue>()

    override fun toString(): String {
        return insn.toString()
    }
}