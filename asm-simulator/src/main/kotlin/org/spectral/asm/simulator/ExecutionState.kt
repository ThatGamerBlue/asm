package org.spectral.asm.simulator

import org.objectweb.asm.Type

/**
 * Represents a state of a method at a single execution frame.
 *
 * @property locals Array<Type?>
 * @property localVarIds IntArray
 * @property stack Array<Type?>
 * @property stackVarIds IntArray
 * @constructor
 */
data class ExecutionState(
        val locals: Array<Type?>,
        val localVarIds: IntArray,
        val stack: Array<Type?>,
        val stackVarIds: IntArray
) {

    /**
     * Creates a execution state instance with absolute sizes of both the
     * local variable table and the stack.
     *
     * @param locals Array<Type?>
     * @param localVarIds IntArray
     * @param localsSize Int
     * @param stack Array<Type?>
     * @param stackVarIds IntArray
     * @param stackSize Int
     * @constructor
     */
    constructor(
            locals: Array<Type?>,
            localVarIds: IntArray,
            localsSize: Int,
            stack: Array<Type?>,
            stackVarIds: IntArray,
            stackSize: Int
    ) : this(
            if(localsSize != 0) locals.copyOf(localsSize) else emptyArray(),
            if(localsSize != 0) localVarIds.copyOf(localsSize) else IntArray(0),
            if(stackSize != 0) stack.copyOf(stackSize) else emptyArray(),
            if(stackSize != 0) stackVarIds.copyOf(stackSize) else IntArray(0)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExecutionState

        if (!locals.contentEquals(other.locals)) return false
        if (!localVarIds.contentEquals(other.localVarIds)) return false
        if (!stack.contentEquals(other.stack)) return false
        if (!stackVarIds.contentEquals(other.stackVarIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = locals.contentHashCode()
        result = 31 * result + localVarIds.contentHashCode()
        result = 31 * result + stack.contentHashCode()
        result = 31 * result + stackVarIds.contentHashCode()
        return result
    }
}