package org.spectral.asm.core.execution

import org.objectweb.asm.Type
import org.spectral.asm.core.Method
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.AbstractValue
import org.spectral.asm.core.execution.value.ObjectValue
import java.util.*

/**
 * Represents a method execution frame per JVM specifications.
 *
 * @property execution The execution instance of this frame.
 * @property method The method this frame is of.
 * @constructor
 */
class Frame(val execution: Execution, val method: Method) {

    /**
     * Whether this frame is currently executing.
     */
    var executing = true

    /**
     * The current instruction which is going to be executed.
     */
    var currentInsn: Instruction? = null

    /**
     * The operand stack of this frame.
     */
    val stack = Stack<AbstractValue>()

    /**
     * The local variable table of this frame.
     */
    val lvt = mutableListOf<AbstractValue>()

    /**
     * The max number of slots for the operand stack
     */
    var maxStack: Int = -1
        private set

    /**
     * The max number of local variables in the LVT.
     */
    var maxLocals: Int = -1
        private set

    /**
     * The state recorder instance for this frame.
     */
    private val stateRecorder = StateRecorder(this)

    /**
     * Initializes the frame with given argument values.
     *
     * @param args List<AbstractValue>
     */
    fun init(args: List<AbstractValue>) {
        if(currentInsn != null) {
            throw ExecutionException("Frame has already been initialized.")
        }

        maxStack = method.code.maxStack
        maxLocals = method.code.maxLocals

        /*
         * Set the initial instruction.
         */
        currentInsn = method.code.instructions.first()

        /*
         * If the method is NOT static. Add the 'this' local variable to
         * the LVT at the zero index.
         */
        if(!method.isStatic) {
            /*
             * Add support for instanced initialization later.
             */
            lvt.add(ObjectValue(null, Type.getObjectType("java/lang/Object")))
        }

        /*
         * Add all the args to the LVT.
         */
        if(args.isNotEmpty()) {
            args.forEach { arg ->
                lvt.add(arg)
            }
        }

        executing = true
    }

    /**
     * Executes a single step or instruction in this frame.
     *
     * @return Boolean
     */
    fun execute(): Boolean {
        if(executing) {



            return true
        }

        return false
    }
}