package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode

/**
 * Represents an uninitialized value on the stack.
 *
 * @constructor
 */
class UninitializedValue(type: Type?, value: Any?) : AbstractValue(type ?: Type.getObjectType("null"), value) {

    override val isPrimitive = false

    override val isReference = false

    override val isValueResolved = true

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        throw IllegalStateException("Copying an uninitialized value is not prohibited.")
    }

    override fun canMerge(other: AbstractValue): Boolean {
        return other == this
    }

    override fun equals(other: Any?): Boolean {
        return other == this
    }

    companion object {
        /**
         * Base uninitialized value
         */
        val UNINITIALIZED_VALUE = UninitializedValue(null, null)
    }
}