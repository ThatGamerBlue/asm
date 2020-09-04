package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.simulator.util.combine

/**
 * Represents a null constant value on the stack.
 *
 * @constructor
 */
class NullConstantValue(insns: List<AbstractInsnNode>) : AbstractValue(insns, NULL_VALUE_TYPE, null) {

    constructor(insn: AbstractInsnNode) : this(listOf(insn))

    override val isPrimitive = false

    override val isReference = true

    override val isValueResolved = true

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        return onCopy(NullConstantValue(combine(insns, insn)))
    }

    override fun canMerge(other: AbstractValue): Boolean {
        return other == this
    }

    override fun equals(other: Any?): Boolean {
        return other is NullConstantValue
    }

    companion object {
        /**
         * Null value type
         */
        val NULL_VALUE_TYPE = Type.getObjectType("null")

        /**
         * Creates a new null constant value
         */
        fun newNull(insn: AbstractInsnNode): NullConstantValue {
            return NullConstantValue(insn)
        }
    }
}