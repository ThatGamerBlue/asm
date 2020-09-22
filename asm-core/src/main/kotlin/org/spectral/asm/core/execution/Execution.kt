package org.spectral.asm.core.execution

import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.Method
import org.spectral.asm.core.execution.value.AbstractValue
import java.util.*

/**
 * Represents a method execution simulation following the JVM specifications.
 *
 * @property pool ClassPool
 * @constructor
 */
class Execution private constructor(val pool: ClassPool) {

    /**
     * Whether this object is executing or not.
     */
    var running = false

    /**
     * The stack of method frames. The top frame will be the one
     * to be executed.
     */
    private val frameStack = Stack<Frame>()

    /**
     * A list of frames which have been executed.
     */
    val frames = mutableListOf<Frame>()

    /**
     * A timeline of frame states at each instruction execution.
     */
    val states = mutableListOf<ExecutionState>()

    /**
     * Creates a new frame for a given method for execution.
     *
     * @param method Method
     * @param args List<AbstractValue>
     * @return AbstractValue
     */
    fun createFrame(method: Method, args: List<AbstractValue>) {
        /*
         * Create a new frame.
         */
        val frame = Frame(this, method)
        frame.init(args)

        /*
         * Push the new frame to the frame stack.
         */
        frameStack.push(frame)
    }

    /**
     * Run the execution.
     */
    fun run() {
        while(frameStack.isNotEmpty()) {
            /*
             * Peek at the top frame in the stack.
             */
        }
    }

    companion object {

        /**
         * Creates, initializes, and runs an execution for a given [method].
         *
         * @param method Method
         * @return Execution
         */
        fun executeMethod(method: Method, args: List<AbstractValue>): Execution {
            val execution = Execution(method.pool)

            /*
             * Create an initial method frame and push it to the top
             * of the frame stack.
             */
            execution.createFrame(method, args)

            /*
             * Run the execution.
             */
            execution.run()

            return execution
        }
    }
}