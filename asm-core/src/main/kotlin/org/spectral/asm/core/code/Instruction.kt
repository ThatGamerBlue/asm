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
     * Makes the given visitor visit this instruction.
     *
     * @param visitor MethodVisitor
     */
    abstract fun accept(visitor: MethodVisitor)

    abstract fun copy(clonedLabels: Map<Label, Label>): Instruction

    abstract override fun toString(): String
}