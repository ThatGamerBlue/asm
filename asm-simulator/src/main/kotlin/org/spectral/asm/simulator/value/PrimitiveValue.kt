package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.simulator.Unresolved
import org.spectral.asm.simulator.util.combine
import org.spectral.asm.simulator.util.promotionIndex
import kotlin.math.max

/**
 * Represents a primitive data type value on the stack.
 *
 * @constructor
 */
class PrimitiveValue(insns: List<AbstractInsnNode>, type: Type, value: Any?) : AbstractValue(insns, type, value) {

    /**
     * Creates a primitive data type value with multiple instructions and null data values.
     *
     * @param insns List<AbstractInsnNode>
     * @param type Type
     * @constructor
     */
    constructor(insns: List<AbstractInsnNode>, type: Type) : this(insns, type, null)

    /**
     * Creates a primitive data type value with a null value.
     *
     * @param insn AbstractInsnNode
     * @param type Type
     * @constructor
     */
    constructor(insn: AbstractInsnNode, type: Type) : this(listOf(insn), type, null)

    /**
     * Creates a primitive data type value with a single instruction
     *
     * @param insn AbstractInsnNode
     * @param type Type
     * @param value Any?
     * @constructor
     */
    constructor(insn: AbstractInsnNode, type: Type, value: Any?) : this(listOf(insn), type, value)

    override val isPrimitive: Boolean get() = true

    override val isReference: Boolean get() = false

    override val isValueResolved: Boolean get() = value?.let { it is Unresolved } ?: false

    override val isArray: Boolean get() = false

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        return PrimitiveValue(combine(insns, insn), type, value)
    }

    override fun canMerge(other: AbstractValue): Boolean {
        if(other == this) return true
        else if(!other.isPrimitive) return false
        return type == other.type
    }

    /**
     * Value getter methods
     */

    fun getBooleanValue(): Boolean {
        return (value as Number).toInt() > 1
    }

    fun getIntValue(): Int {
        return (value as Number).toInt()
    }

    fun getFloatValue(): Float {
        return (value as Number).toFloat()
    }

    fun getDoubleValue(): Double {
        return (value as Number).toDouble()
    }

    fun getLongValue(): Long {
        return (value as Number).toLong()
    }

    /**
     * Mathematical Operation Methods
     */

    companion object {

        /**
         * Creation methods
         */

        fun ofInt(insn: AbstractInsnNode, value: Int): AbstractValue {
            return PrimitiveValue(insn, Type.INT_TYPE, value)
        }

        fun ofChar(insn: AbstractInsnNode, value: Char): AbstractValue {
            return PrimitiveValue(insn, Type.CHAR_TYPE, value)
        }

        fun ofByte(insn: AbstractInsnNode, value: Byte): AbstractValue {
            return PrimitiveValue(insn, Type.BYTE_TYPE, value)
        }

        fun ofShort(insn: AbstractInsnNode, value: Short): AbstractValue {
            return PrimitiveValue(insn, Type.SHORT_TYPE, value)
        }

        fun ofBool(insn: AbstractInsnNode, value: Boolean): AbstractValue {
            return PrimitiveValue(insn, Type.BOOLEAN_TYPE, value)
        }

        fun ofLong(insn: AbstractInsnNode, value: Long): AbstractValue {
            return PrimitiveValue(insn, Type.LONG_TYPE, value)
        }

        fun ofFloat(insn: AbstractInsnNode, value: Float): AbstractValue {
            return PrimitiveValue(insn, Type.FLOAT_TYPE, value)
        }

        fun ofDouble(insn: AbstractInsnNode, value: Double): AbstractValue {
            return PrimitiveValue(insn, Type.DOUBLE_TYPE, value)
        }

        /**
         * Gets a common primitive data type for calculations of two data
         * types.
         *
         * @param a Type
         * @param b Type
         * @return Type
         */
        private fun commonMathType(a: Type, b: Type): Type {
            val i1 = a.promotionIndex
            val i2 = b.promotionIndex
            val max = max(i1, i2)
            if(max <= Type.DOUBLE) {
                return if(max == i1) a else b
            }

            throw IllegalStateException("Cannot do math on non-primitive types: ${a.descriptor} & ${b.descriptor}")
        }
    }
}