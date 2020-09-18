package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor

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
    val index: Int get() = code.indexOf(this)

    /**
     * Makes the given visitor visit this instruction.
     *
     * @param visitor MethodVisitor
     */
    abstract fun accept(visitor: MethodVisitor)

    open fun copy(clonedLabels: Map<Label, Label>): Instruction { throw UnsupportedOperationException() }

    abstract override fun toString(): String
}