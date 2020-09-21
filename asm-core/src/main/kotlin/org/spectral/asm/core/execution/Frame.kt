package org.spectral.asm.core.execution

import org.objectweb.asm.Type
import org.spectral.asm.core.Method
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.*

/**
 * Represents a JVM execution frame as per Oracle JVM Spec.
 * @see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6
 *
 * @property execution The [Execution] instance this frame belongs to.
 * @constructor
 */
class Frame private constructor(private val execution: Execution, private val method: Method) {

    /**
     * The current instruction the execution is on.
     */
    var currentInsn: Instruction? = null

    /**
     * The current state of the frame execution.
     */
    var status = FrameStatus.READY

    /**
     * Initializes and creates a frame with given arguments.
     *
     * @param execution Execution
     * @param method The method this frame is for.
     * @param args List<AbstractValue>
     * @constructor
     */
    constructor(execution: Execution, method: Method, args: List<AbstractValue>) : this(execution, method) {
        this.init(args)
    }

    /**
     * Initializes a blank frame from the provided [method]
     */
    private fun init(args: List<AbstractValue>) {
        /*
         * If the method is NOT static, add the 'this' entry to index 0 of
         * the LVT.
         *
         * // TODO Add support for instanced 'this' LV's
         */
        if(!method.isStatic) {
            lvt.add(ObjectValue(null, Type.getObjectType("java/lang/Object")))
        }

        if(args.isNotEmpty()) {
            /*
             * Add each argument given to the LVT.
             */
            args.forEach { arg ->
                lvt.add(arg.copy())

                /*
                 * Account for wide data types
                 */
                if(arg is DoubleValue || arg is LongValue) {
                    lvt.add(TopValue())
                }
            }
        }

        status = FrameStatus.INITIALIZED
    }

    /**
     * The stack for this frame.
     */
    val stack = mutableListOf<AbstractValue>()

    /**
     * The local variable table of this frame.
     */
    val lvt = mutableListOf<AbstractValue>()

    /**
     * The execution frame states after execution of each instruction.
     */
    val states = mutableListOf<ExecutionState>()

    /**
     * Whether this frame terminated with a returning value.
     */
    var returnValue: AbstractValue? = null

    /**
     * Executed the frame until the frame execution finished or predicate matches.
     * In which case the frame's execution is paused.
     */
    fun execute() {
        if(status.priority < FrameStatus.INITIALIZED.priority) {
            throw ExecutionException("Frame has not been initialized.")
        }

        status = FrameStatus.EXECUTING

        /*
         * .... the madness begins
         */
    }

    /**
     * Represents the state of the frame.
     */
    enum class FrameStatus(val priority: Int) {
        READY(0),
        INITIALIZED(1),
        EXECUTING(2),
        INVOKING(3),
        PAUSED(4),
        TERMINATED(5);
    }
}