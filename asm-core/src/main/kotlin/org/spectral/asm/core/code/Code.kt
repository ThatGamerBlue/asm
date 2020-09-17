package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.Method

/**
 * Represents the code JVM instruction collection of a method.
 *
 * @property method Method
 * @property maxStack Int
 * @property maxLocals Int
 * @constructor
 */
class Code(val method: Method) {

    private val instructions = mutableListOf<Instruction>()

    var maxStack = 0

    var maxLocals = 0

    val size: Int get() = instructions.size

    lateinit var first: Instruction private set

    lateinit var last: Instruction private set

    operator fun get(index: Int): Instruction {
        if(index < 0 || index > size) {
            throw IndexOutOfBoundsException()
        }

        return instructions[index]
    }

    fun contains(insn: Instruction): Boolean {
        return instructions.contains(insn)
    }

    fun indexOf(insn: Instruction): Int {
        return instructions.indexOf(insn)
    }

    fun accept(visitor: MethodVisitor) {

    }

    fun add(insn: Instruction) {
        if(!::last.isInitialized) {
            first = insn
            last = insn
        } else {
            last.next = insn
            insn.prev = last
        }

        last = insn
        insn.index = 0

        instructions.add(insn)
    }

    fun insert(insn: Instruction) {
        if(!::first.isInitialized) {
            first = insn
            last = insn
        } else {
            first.prev = insn
            insn.next = first
        }
        first = insn
        insn.index = 0

        instructions.add(0, insn)
    }
}