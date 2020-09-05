package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.analysis.Value
import org.spectral.asm.core.ext.slotSize
import org.spectral.asm.simulator.ExecFrame

/**
 * Represents an abstract implementation of a value of a local variable or stack element
 * in the LVT or stack at a given frame execution.
 *
 * @property insns List<AbstractInsnNode>
 * @property type Type
 * @property value Any?
 * @constructor
 */
abstract class AbstractValue(val insns: List<AbstractInsnNode>, val type: Type, val value: Any?) : Value {

    /**
     * Creates a value instance with a single instruction.
     *
     * @param insn AbstractInsnNode
     * @param type Type
     * @param value Any?
     * @constructor
     */
    constructor(insn: AbstractInsnNode, type: Type, value: Any?) : this(listOf(insn), type, value)

    /**
     * Creates a value instance witout any defined instrutions.
     *
     * @param type Type
     * @param value Any?
     * @constructor
     */
    constructor(type: Type, value: Any?) : this(listOf(), type, value)

    /**
     * Creates an empty null value instance.
     *
     * @constructor
     */
    constructor() : this(listOf(), Type.getObjectType("null"), null)

    var nullCheck: JumpInsnNode? = null
    private set

    var copySource: AbstractValue? = null
    private set

    /**
     * The frame at the moment this value was pushed onto
     * the stack.
     */
    lateinit var frame: ExecFrame

    /**
     * Whether the value type is primitive or not
     */
    abstract val isPrimitive: Boolean

    /**
     * Whether the value type is a reference type or not
     */
    abstract val isReference: Boolean

    /**
     * Whether the value is resolved or not.
     */
    abstract val isValueResolved: Boolean

    /**
     * Gets whether this value type is mergeable with the
     * provided [other] value object.
     *
     * @param other AbstractValue
     * @return Boolean
     */
    abstract fun canMerge(other: AbstractValue): Boolean

    /**
     * Copies this value given an instruction type.
     *
     * @param insn AbstractInsnNode
     * @return AbstractValue
     */
    abstract fun copy(insn: AbstractInsnNode): AbstractValue

    /**
     * Invoked when this value is duped on the stack.
     *
     * @param copy AbstractValue
     * @return AbstractValue
     */
    internal fun onCopy(copy: AbstractValue): AbstractValue {
        copy.setNullCheckedBy(nullCheck)
        copy.copySource = this
        return copy
    }

    /**
     * Sets the null check control flow jump
     *
     * @param nullCheck JumpInsnNode
     */
    fun setNullCheckedBy(nullCheck: JumpInsnNode?) {
        this.nullCheck = nullCheck
        copySource?.setNullCheckedBy(nullCheck)
    }

    /**
     * Gets whether the value is unresolved or not.
     */
    val isValueUnresolved: Boolean get() = !isValueResolved

    /**
     * Whether the data value is null or not.
     */
    open val isNull: Boolean get() = value == null

    /**
     * Whether the value is an array type or not.
     */
    open val isArray: Boolean get() = type.sort == Type.ARRAY

    /**
     * Gets the size of the value on the stack.
     *
     * @return Int
     */
    override fun getSize(): Int {
        return type.slotSize
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractValue

        if (insns != other.insns) return false
        if (type != other.type) return false
        if (value != other.value) return false
        if (nullCheck != other.nullCheck) return false
        if (frame != other.frame) return false
        if (copySource != other.copySource) return false
        if (isPrimitive != other.isPrimitive) return false
        if (isReference != other.isReference) return false
        if (isValueResolved != other.isValueResolved) return false

        return true
    }

    override fun hashCode(): Int {
        var result = insns.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (nullCheck?.hashCode() ?: 0)
        result = 31 * result + (copySource?.hashCode() ?: 0)
        result = 31 * result + frame.hashCode()
        result = 31 * result + isPrimitive.hashCode()
        result = 31 * result + isReference.hashCode()
        result = 31 * result + isValueResolved.hashCode()
        return result
    }

    override fun toString(): String {
        return when {
            this == UninitializedValue.UNINITIALIZED_VALUE -> "[UNINITIALIZED]"
            isNull -> "[$type:NULL]"
            else -> "[$type:$value]"
        }
    }

    companion object {
        /**
         * Create a default type value on the stack
         *
         * @param insn AbstractInsnNode
         * @param type Type
         * @return AbstractValue
         */
        fun ofDefault(insn: AbstractInsnNode, type: Type?): AbstractValue? {
            if(type == null) return UninitializedValue.UNINITIALIZED_VALUE
            return when(type.sort) {
                Type.VOID -> null
                Type.BOOLEAN, Type.CHAR, Type.BYTE,
                    Type.SHORT, Type.INT -> PrimitiveValue.ofInt(insn, 0)
                Type.FLOAT -> PrimitiveValue.ofFloat(insn, 0F)
                Type.LONG -> PrimitiveValue.ofLong(insn, 0L)
                Type.DOUBLE -> PrimitiveValue.ofDouble(insn, 0.0)
                Type.ARRAY, Type.OBJECT -> {
                    if(type == NullConstantValue.NULL_VALUE_TYPE) {
                        NullConstantValue.newNull(insn)
                    }
                    VirtualValue(insn, type, null)
                }
                else -> throw IllegalStateException("Unsupported type: $type")
            }
        }
    }
}