package org.spectral.asm.core.execution

import org.objectweb.asm.Type
import org.spectral.asm.core.Method
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.exception.StackUnderflowException
import org.spectral.asm.core.execution.value.*

/**
 * Represents a JVM execution frame as per Oracle JVM Spec.
 * @see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6
 *
 * @property execution The [Execution] instance this frame belongs to.
 * @constructor
 */
class Frame private constructor(private val execution: Execution, private val method: Method) {

    /**
     * The stack for this frame.
     */
    val stack = mutableListOf<AbstractValue>()

    /**
     * The local variable table of this frame.
     */
    val lvt = mutableListOf<AbstractValue>()

    /**
     * The execution frame states after execution of each instruction.
     */
    val states = mutableListOf<ExecutionState>()

    /**
     * Whether this frame terminated with a returning value.
     */
    var returnValue: AbstractValue? = null

    fun push(value: AbstractValue) {
        push(0, value)
    }

    fun pushWide(value: AbstractValue) {
        push(value)
        push(TopValue())
    }

    /**
     * Pushes a value to the stack.
     *
     * @param index Index to push to.
     * @param value AbstractValue
     */
    fun push(index: Int, value: AbstractValue) {

    }

    fun pop(): AbstractValue {
        return pop(0)
    }

    fun popWide(): AbstractValue {
        pop()
        return pop()
    }

    fun pop(index: Int): AbstractValue {
        throw IllegalStateException()
    }


}