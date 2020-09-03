package org.spectral.asm.simulator.value

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.analysis.Value
import org.spectral.asm.core.Class
import org.spectral.asm.core.ext.slotSize

/**
 * Represents an abstract value at a given frame.
 *
 * @property insns MutableList<AbstractInsnNode>
 * @property element Class
 * @property value Any?
 * @constructor
 */
abstract class AbstractValue(val insns: MutableList<AbstractInsnNode>, val element: Class?, val value: Any?) : Value {

    /**
     * Creates a value with an optional instruction
     *
     * @param insn AbstractInsnNode?
     * @param element Class
     * @param value Any?
     * @constructor
     */
    constructor(insn: AbstractInsnNode?, element: Class?, value: Any?) : this(
            if(insn == null) mutableListOf() else mutableListOf(insn),
            element,
            value
    )

    var nullCheck: JumpInsnNode? = null
        set(value) {
            field = value
            if(copySource != null) {
                copySource!!.nullCheck = value
            }
        }

    /**
     * The source of the copy action for this value.
     */
    private var copySource: AbstractValue? = null

    /**
     * Copies the instruction from the top of the stack.
     *
     * @param insn AbstractInsnNode
     * @return AbstractValue
     */
    abstract fun copy(insn: AbstractInsnNode): AbstractValue

    /**
     * Invoked when the value is copied
     *
     * @param copy AbstractValue
     * @return AbstractValue
     */
    fun onCopy(copy: AbstractValue): AbstractValue {
        copy.nullCheck = nullCheck
        copy.copySource = this
        return copy
    }

    /**
     * Whether the given [other] can be merged with the current value.
     *
     * @param other AbstractValue
     * @return Boolean
     */
    abstract fun canMerge(other: AbstractValue): Boolean

    /**
     * Whether the current value is a primitive data type.
     */
    abstract val isPrimitive: Boolean

    /**
     * Whether the current value is a reference to another type.
     */
    abstract val isReference: Boolean

    /**
     * Whether the current value has a resolved data value.
     */
    abstract val isValueResolved: Boolean

    /**
     * Whether the data value is null or not.
     */
    val isNull: Boolean get() = value == null

    /**
     * Whether the value is an array type.
     */
    val isArray: Boolean get() = element?.isArray ?: false

    /**
     * The ASM [Type] of the value's class
     */
    val type: Type get() = element?.type ?: Type.getObjectType("null")

    /**
     * Gets the element size on the stack.
     *
     * @return Int
     */
    override fun getSize(): Int {
        return type.slotSize
    }
}