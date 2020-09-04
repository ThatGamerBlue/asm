package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.simulator.util.combine

/**
 * Represents a method return result value on the stack.
 *
 * @constructor
 */
class ReturnAddressValue(insns: List<AbstractInsnNode>) : AbstractValue(insns, Type.VOID_TYPE, null) {

    /**
     * Creates a return address value with a single instruction.
     *
     * @param insn AbstractInsnNode
     * @constructor
     */
    constructor(insn: AbstractInsnNode) : this(listOf(insn))

    override val isPrimitive = false

    override val isReference = false

    override val isValueResolved = true

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        return onCopy(ReturnAddressValue(combine(insns, insn)))
    }

    override fun canMerge(other: AbstractValue): Boolean {
        return other == this
    }

    override fun equals(other: Any?): Boolean {
        return other == this
    }
}