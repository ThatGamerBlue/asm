package org.spectral.asm.executor

import me.coley.analysis.SimAnalyzer
import me.coley.analysis.SimInterpreter
import me.coley.analysis.value.AbstractValue
import org.objectweb.asm.tree.analysis.Frame
import org.spectral.asm.core.Method
import java.util.*

class MethodExecutor(private val method: Method) {

    private val simulator = SimAnalyzer(SimInterpreter())
    private val executionFrames: Array<Frame<AbstractValue>>
    private var currentIndex = 0

    private val stack = Stack<ExecutionValue>()

    val frames = mutableListOf<ExecutionFrame>()

    init {
        simulator.setSkipDeadCodeBlocks(false)
        simulator.setThrowUnresolvedAnalyzerErrors(false)
        executionFrames = simulator.analyze(method.owner.name, method.node)
    }

    fun step() {
        val insn = method.instructions[currentIndex]
        val executionFrame = executionFrames[currentIndex]

        val frame = ExecutionFrame(insn, executionFrame)
        frame.pops.addAll(this.getPops(frame))
        frame.pushes.addAll(this.getPushes(frame))

        frames.add(frame)

        currentIndex++
    }

    fun run() {
        while(currentIndex < method.instructions.size()) {
            step()
        }
    }

    private fun getPushes(frame: ExecutionFrame): List<ExecutionValue> {
        if(currentIndex >= executionFrames.size - 1) return emptyList()

        val nextFrame = executionFrames[currentIndex + 1]
        val nextStack = mutableListOf<AbstractValue>()
        val curStack = mutableListOf<AbstractValue>()

        for(i in 0 until nextFrame.stackSize) {
            nextStack.add(nextFrame.getStack(i))
        }

        for(i in 0 until frame.stack.size) {
            curStack.add(frame.stack[i])
        }

        nextStack.removeAll(curStack)
        return nextStack.map { ExecutionValue(it) }.apply {
            this.forEach { it.pusher = frame }
            this.forEach { stack.push(it) }
        }
    }

    private fun getPops(frame: ExecutionFrame): List<ExecutionValue> {
        if(currentIndex == 0) return emptyList()

        val lastFrame = frames[currentIndex - 1]
        val lastStack = mutableListOf<AbstractValue>()
        val curStack = mutableListOf<AbstractValue>()

        for(i in 0 until lastFrame.stack.size) {
            lastStack.add(lastFrame.stack[i])
        }

        for(i in 0 until frame.stack.size) {
            curStack.add(frame.stack[i])
        }

        lastStack.removeAll(curStack)
        return lastStack.map {
            if(stack.isNotEmpty()) stack.pop()
            else ExecutionValue(it)
        }.apply {
            this.forEach { it.poppers.add(frame) }
        }
    }

}