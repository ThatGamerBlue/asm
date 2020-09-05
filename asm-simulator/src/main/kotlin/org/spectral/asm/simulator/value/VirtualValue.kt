package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.simulator.Unresolved
import org.spectral.asm.simulator.util.combine

/**
 * Represents a abstract virtual value reference on the stack.
 *
 * @constructor
 */
open class VirtualValue(insns: List<AbstractInsnNode>, type: Type, value: Any?) : AbstractValue(insns, type, value) {

    /**
     * Creates a virtual value with a single instruction.
     *
     * @param insn AbstractInsnNode
     * @param type Type
     * @param value Any?
     * @constructor
     */
    constructor(insn: AbstractInsnNode, type: Type, value: Any?) : this(listOf(insn), type, value)

    override val isPrimitive = false

    override val isReference = true

    override val isValueResolved = true

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        return onCopy(VirtualValue(combine(insns, insn), type, value))
    }

    override fun canMerge(other: AbstractValue): Boolean {
        if(other == this) return true
        else if(other is NullConstantValue
                || other == UninitializedValue.UNINITIALIZED_VALUE) {
            return false
        }
        return type == other.type
    }


    /**
     * Creates a virtual value of a method reference
     */
    fun ofMethodRef(insn: AbstractInsnNode, desc: Type): AbstractValue {
        if(value == null || value == Type.VOID_TYPE) {
            throw IllegalStateException("Cannot act on null reference value.")
        }

        if(!isReference) {
            throw IllegalStateException("Cannot act on reference on a non-reference value.")
        }

        val retType = desc.returnType
        if(retType == Type.VOID_TYPE) {
            return VirtualValue(listOf(), Type.VOID_TYPE, null)
        }

        if(retType.sort <= Type.DOUBLE) {
            return PrimitiveValue(insn, retType)
        }

        return ofVirtual(insn, retType)
    }

    companion object {
        /**
         * Creates a virtual value
         */
        fun ofVirtual(insn: AbstractInsnNode, type: Type): VirtualValue {
            return VirtualValue(insn, type, Unresolved(type))
        }

        fun ofVirtual(insns: List<AbstractInsnNode>, type: Type): VirtualValue {
            return VirtualValue(insns, type, Unresolved(type))
        }

        /**
         * Create a virtual value of a class type
         */
        fun ofClass(insn: AbstractInsnNode, value: Type): VirtualValue {
            return VirtualValue(insn, Type.getObjectType("java/lang/Class"), value)
        }
    }
}