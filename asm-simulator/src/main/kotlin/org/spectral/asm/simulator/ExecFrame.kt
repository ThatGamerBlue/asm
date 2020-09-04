package org.spectral.asm.simulator

import org.objectweb.asm.tree.analysis.Frame
import org.spectral.asm.simulator.util.pops
import org.spectral.asm.simulator.util.pushed
import org.spectral.asm.simulator.value.AbstractValue
import org.spectral.asm.simulator.value.UninitializedValue

/**
 * Represents an execution frame at a given instruction simulation
 * state.
 */
class ExecFrame : Frame<AbstractValue> {

    /**
     * Creates an execution frame with a set LVT and stack size.
     *
     * @param numLocals Int
     * @param numStack Int
     * @constructor
     */
    constructor(numLocals: Int, numStack: Int) : super(numLocals, numStack)

    /**
     * Creates an execution frame from the state of different
     * execution frame.
     *
     * @param frame ExecFrame
     * @constructor
     */
    constructor(frame: ExecFrame) : super(frame)

    /**
     * Reserved local variable table slots for JVM specifics such
     * as the 'this' for non static member methods.
     *
     * As well as for elements which have extended slot sizes.
     */
    private val reservedSlots = hashSetOf<Int>()

    /**
     * Pops a value off the stack
     *
     * @return AbstractValue
     */
    override fun pop(): AbstractValue {
        val result = super.pop()
        result.frame = this
        result.insns.first().pops.add(result)

        return result
    }

    /**
     * Pushes a value to the stack.
     *
     * @param value AbstractValue
     */
    override fun push(value: AbstractValue) {
        super.push(value)

        value.frame = this
        value.insns.first().pushed.add(value)
    }

    /**
     * Sets a [value] on the LVT for this frame.
     *
     * @param index Int
     * @param value AbstractValue
     */
    override fun setLocal(index: Int, value: AbstractValue) {
        if(value != UninitializedValue.UNINITIALIZED_VALUE) {
            if(reservedSlots.contains(index)) {
                throw IllegalStateException("Cannot set local[$index] since that index is reserved on the JVM stack.")
            }

            if(value.value is Double || value.value is Long) {
                reservedSlots.add(index + 1)
            }
        }

        /*
         * Update local variable table
         */
        super.setLocal(index, value)
    }
}