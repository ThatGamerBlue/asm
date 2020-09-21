package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.execution.ExecutionState
import org.spectral.asm.core.execution.exception.ExecutionException

/**
 * Represents a JVM bytecode instruction.
 *
 * @property opcode Int
 * @constructor
 */
abstract class Instruction(val opcode: Int) {

    /**
     * An optional reference to the code the method belongs in.
     */
    lateinit var code: Code internal set

    /**
     * Gets the index in  the instruction list this instruction is at.
     */
    val offset: Int get() = code.indexOf(this)

    /**
     * Makes the given visitor visit this instruction.
     *
     * @param visitor MethodVisitor
     */
    abstract fun accept(visitor: MethodVisitor)

    /**
     * Logic performed to performed to the stack and LVT during execution
     * by this instruction.
     *
     * @param frame ExecutionFrame
     */
    open fun execute(frame: ExecutionState) { throw ExecutionException(UnsupportedOperationException("Execution for opcode $opcode not implemented.")) }

    /**
     * Logic to copy the instruction and mapped labels.
     *
     * @param clonedLabels Map<Label, Label>
     * @return Instruction
     */
    open fun copy(clonedLabels: Map<Label, Label>): Instruction { throw UnsupportedOperationException() }

    abstract override fun toString(): String
}