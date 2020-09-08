package org.spectral.asm.executor

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.analysis.Frame

class ExecutionFrame : Frame<StackValue> {

    var insn: AbstractInsnNode? = null

    constructor(numLocals: Int, numStack: Int) : super(numLocals, numStack)

    constructor(frame: ExecutionFrame) : super(frame)

    val pops = mutableListOf<StackValue>()

    val pushes = mutableListOf<StackValue>()

    override fun push(value: StackValue) {
        insn = value.insn

        super.push(value)

        value.frame = this
        pushes.add(value)
    }

    override fun pop(): StackValue {
        val value = super.pop()
        insn = value.insn

        value.popped.add(this)
        pops.add(value)

        return value
    }
}