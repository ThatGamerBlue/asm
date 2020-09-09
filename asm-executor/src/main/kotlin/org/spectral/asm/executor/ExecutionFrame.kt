package org.spectral.asm.executor

import me.coley.analysis.value.AbstractValue
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.analysis.Frame
import java.util.*

class ExecutionFrame(val insn: AbstractInsnNode, frame: Frame<AbstractValue>) {

    val pushes = mutableListOf<ExecutionValue>()
    val pops = mutableListOf<ExecutionValue>()

    val stack = Stack<AbstractValue>()

    init {
        /*
         * Update the stack
         */
        for(i in 0 until frame.stackSize) {
            stack.push(frame.getStack(i))
        }
    }
}