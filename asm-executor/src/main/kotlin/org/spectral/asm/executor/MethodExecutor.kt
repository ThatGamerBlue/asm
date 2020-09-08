package org.spectral.asm.executor

import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.Frame
import org.spectral.asm.core.Method

class MethodExecutor private constructor(
        val method: Method,
        private val interpreter: ExecutionInterpreter
) : Analyzer<StackValue>(interpreter){

    init {
        interpreter.analyzer = this
    }

    constructor(method: Method) : this(method, ExecutionInterpreter())

    fun run(): Array<Frame<StackValue>> = this.analyze(method.owner.name, method.node)

    override fun newFrame(numLocals: Int, numStack: Int): Frame<StackValue> {
        return ExecutionFrame(numLocals, numStack)
    }

    override fun newFrame(frame: Frame<out StackValue>): Frame<StackValue> {
        return ExecutionFrame(frame as ExecutionFrame)
    }
}