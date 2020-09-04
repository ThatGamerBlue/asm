package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.analysis.Value
import org.spectral.asm.core.ext.slotSize

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
     * The instructions which pushed this value to the stack.
     */
    val pushed = insns

    /**
     * The instructions which have popped this value off the stack.
     */
    val pops = mutableListOf<AbstractInsnNode>()

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
    val isNull: Boolean get() = value == null

    /**
     * Gets the size of the value on the stack.
     *
     * @return Int
     */
    override fun getSize(): Int {
        return type.slotSize
    }
}