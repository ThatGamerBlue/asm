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
     * The index value of this instruction.
     */
    var index = -1

    /**
     * The next instruction in the code.
     */
    var next: Instruction? = null

    /**
     * The previous instruction in the code.
     */
    var prev: Instruction? = null

    /**
     * Makes the given visitor visit this instruction.
     *
     * @param visitor MethodVisitor
     */
    abstract fun accept(visitor: MethodVisitor)
}