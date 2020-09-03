package org.spectral.asm.simulator

import org.spectral.asm.core.Class

/**
 * Represents an execution state at a given frame.
 *
 * @property locals Array<Class>
 * @property localVarIds IntArray
 * @property stack Array<Class>
 * @property stackVarIds IntArray
 * @constructor
 */
data class ExecState(
        val locals: Array<Class?>,
        val localVarIds: IntArray,
        val stack: Array<Class?>,
        val stackVarIds: IntArray
) {
    /**
     * Secondary constructor.
     *
     * @param locals Array<Class>
     * @param localVarIds IntArray
     * @param localsSize Int
     * @param stack Array<Class>
     * @param stackVarIds IntArray
     * @param stackSize Int
     * @constructor
     */
    constructor(
            locals: Array<Class?>,
            localVarIds: IntArray,
            localsSize: Int,
            stack: Array<Class?>,
            stackVarIds: IntArray,
            stackSize: Int
    ) : this(
            if(localsSize != 0) locals.copyOf(localsSize) else emptyArray<Class?>(),
            if(localsSize != 0) localVarIds.copyOf(localsSize) else IntArray(0),
            if(stackSize != 0) stack.copyOf(stackSize) else emptyArray<Class?>(),
            if(stackSize != 0) stackVarIds.copyOf(stackSize) else IntArray(0)
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExecState

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