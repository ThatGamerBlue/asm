package org.spectral.asm.core.execution

import org.objectweb.asm.Type
import org.spectral.asm.core.Method
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.exception.StackUnderflowException
import org.spectral.asm.core.execution.value.*

/**
 * Represents a JVM execution frame as per Oracle JVM Spec.
 * @see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.6
 *
 * @property execution The [Execution] instance this frame belongs to.
 * @constructor
 */
class Frame private constructor(val execution: Execution, private val method: Method) {

    /**
     * The current instruction the execution is on.
     */
    var currentInsn: Instruction? = null

    /**
     * The current instruction index
     */
    var currentInsnIndex = -1

    /**
     * The current [ExecutionState] of the frame.
     */
    var currentState: ExecutionState? = null
        private set

    /**
     * The current state of the frame execution.
     */
    var status = FrameStatus.READY

    /**
     * The instruction to jump to after the current execution step.
     */
    var jumpTo: Instruction? = null

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

        while(status == FrameStatus.EXECUTING) {
            if(!step()) {
                status = FrameStatus.TERMINATED
            }
        }
    }

    /**
     * Steps the execution forward by one.
     * Returns true if the execution can continue, false if the execution was terminal.
     *
     * @return Boolean
     */
    fun step(): Boolean {
        when {
            ++currentInsnIndex >= method.code.instructions.size -> {
                return false
            }
            jumpTo != null -> {
                currentInsn = jumpTo
                jumpTo = null
            }
            else -> {
                currentInsn = method.code.instructions[currentInsnIndex]
            }
        }

        val state = if(currentState != null) {
            ExecutionState(currentInsn!!, currentState!!)
        } else {
            ExecutionState(currentInsn!!).apply {
                this.stack.addAll(stack.map { StackValue(it.value) })
                this.lvt.addAll(lvt.map { StackValue(it.value) })
            }
        }

        currentState = state

        try {
            currentInsn!!.execute(this)
        } catch(e : ExecutionException) {
            //e.printStackTrace()
        }

        states.add(currentState!!)

        return true
    }

    fun push(value: AbstractValue) {
        push(0, value)
    }

    fun pushWide(value: AbstractValue) {
        push(value)
        push(TopValue())
    }

    /**
     * Pushes a value to the stack.
     *
     * @param index Index to push to.
     * @param value AbstractValue
     */
    fun push(index: Int, value: AbstractValue) {
       val stackValue = value.stackValue ?: StackValue(value).apply { value.stackValue = this }

        stackValue.pusher = currentState!!
        currentState!!.pushes.add(stackValue)

        currentState!!.stack.add(index, stackValue)
        stack.add(index, value)
    }

    fun pop(): AbstractValue {
        return pop(0)
    }

    fun popWide(): AbstractValue {
        pop()
        return pop()
    }

    fun pop(index: Int): AbstractValue {
        if(index >= stack.size) {
            throw StackUnderflowException("Popped value off empty stack.")
        }

        val value = stack.removeAt(index)
        val stackValue = currentState!!.stack.removeAt(index)

        stackValue.poppers.add(currentState!!)
        currentState!!.pops.add(stackValue)

        return value
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