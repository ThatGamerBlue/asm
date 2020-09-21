package org.spectral.asm.core.execution

import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.Method
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.AbstractValue
import org.spectral.asm.core.execution.value.DoubleValue
import org.spectral.asm.core.execution.value.LongValue

/**
 * Represents the execution of a method as the JVM would.
 * returns a collection of frames.
 *
 * @property pool ClassPool
 * @constructor
 */
class Execution private constructor(val pool: ClassPool) {

    /**
     * Whether this object is current executing or not.
     */
    var executing = false

    /**
     * The current method being executed.
     */
    var currentMethod: Method? = null

    /**
     * The current frame being executed.
     */
    var currentFrame: Frame? = null

    /**
     * The frame being executed for a given method.
     */
    val frames = mutableListOf<Frame>()

    /**
     * Executes a method.
     *
     * @param method Method
     */
    private fun executeMethod(method: Method, args: List<AbstractValue>) {
        executing = true
        currentMethod = method

        val initialFrame = Frame(this, method, args)
        initialFrame.currentInsn = method.code.instructions.first()

        frames.add(initialFrame)

        this.run()
    }

    /**
     * Creates a new frame and invokes a method call. Once that method call is complete, the execution
     * jumps back to where it left off.
     *
     * @param method Method
     * @param args List<AbstractValue>
     */
    internal fun invokeMethod(method: Method, args: List<AbstractValue>) {
        if(!executing) throw ExecutionException("Cannot invoke method in a non-running execution.")

        currentFrame?.status = Frame.FrameStatus.INVOKING

        val frame = Frame(this, method, args)
        frames.add(frame)

        currentFrame = frame
    }

    /**
     * Runs the execution until all frames in [frames] are terminated.
     */
    private fun run() {
        executing = true

        /*
         * Loop until all frames have finished executing.
         */
        executionLoop@ while(!frames.all { it.status == Frame.FrameStatus.TERMINATED }) {
            currentFrame = findFrame()

            if(currentFrame == null) {
                executing = false
                break@executionLoop
            }

            currentFrame!!.execute()
        }
    }

    /**
     * Gets the next frame to execute.
     *
     * @return Frame
     */
    private fun findFrame(): Frame? {
        if(currentFrame?.status == Frame.FrameStatus.EXECUTING) {
            return currentFrame
        }

        frames.forEach { frame ->
            if(frame.status == Frame.FrameStatus.INITIALIZED) {
                return frame
            }

            else if(currentFrame?.status == Frame.FrameStatus.TERMINATED && frame.status == Frame.FrameStatus.INVOKING) {
                /*
                 * Push the return value if any exists.
                 */
                if(currentFrame!!.returnValue != null) {
                    if(currentFrame!!.returnValue is LongValue || currentFrame!!.returnValue is DoubleValue) {
                        frame.pushWide(currentFrame!!.popWide())
                    } else {
                        frame.push(currentFrame!!.pop())
                    }
                }

                frame.status = Frame.FrameStatus.EXECUTING

                return frame
            }
        }

        return null
    }

    companion object {

        /**
         * Runs a execution simulation of [method] passed with any [args] argument values.
         *
         * @param method Method
         * @param args List<AbstractValue>
         * @return Execution
         */
        fun executeMethod(method: Method, args: List<AbstractValue>): Execution {
            val execution = Execution(method.pool)
            execution.executeMethod(method, args)

            return execution
        }
    }
}