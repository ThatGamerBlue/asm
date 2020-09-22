package org.spectral.asm.core.execution

import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Responsible for watching changes to the given [frame] and outputting
 * [ExecutionState] instances.
 *
 * @property frame Frame
 * @constructor
 */
class StateRecorder(val frame: Frame) {

    /**
     * The current [ExecutionState] instance of the recorder.
     */
    private lateinit var state: ExecutionState

    /**
     * Creates a new state from the previous state.
     */
    fun record() {
        state = if(::state.isInitialized) ExecutionState(frame.currentInsn!!, state)
                else ExecutionState(frame.currentInsn!!)
    }

    /**
     * Saves the recorded execution state of the [frame] from between when this
     * method was call and when 'record()' was called.
     *
     * Adds the [ExecutionState] to the execution states list.
     */
    fun stop() {
        if(!::state.isInitialized) {
            this.record()
        }

        frame.execution.states.add(state)
    }

    /**
     * Records a push to the stack.
     *
     * @param value AbstractValue
     */
    internal fun recordPush(index: Int, value: StackValue) {
        value.pusher = state
        state.pushes.add(value)
        state.stack.add(index, value)
    }

    /**
     * Records a pop from the stack.
     *
     * @param index Int
     */
    internal fun recordPop(index: Int) {
        val value = state.stack.removeAt(index)
        value.poppers.add(state)
        state.pops.add(value)
    }
}