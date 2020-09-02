package org.spectral.asm.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.Method

/**
 * Responsible for recording states at each frame of a given [method]
 * to provide a look back into the history of the execution.
 *
 * @property method Method
 * @constructor
 */
class StateRecorder(val method: Method) {

    /**
     * The empty method local variable table.
     */
    val locals = arrayOfNulls<Type?>(method.maxLocals)

    /**
     * The local variable identifier id's
     */
    val localVarIds = IntArray(locals.size)

    /**
     * The empty simulation JVM stack.
     */
    val stack = arrayOfNulls<Type?>(method.maxStack)

    /**
     * The stack variable identifier id's
     */
    val stackVarIds = IntArray(stack.size)

    /**
     * The size or offset of the local variable table
     */
    var localsSize = 0

    /**
     * The size or offset of the stack.
     */
    var stackSize = 0

    /**
     * The current instruction index the execution is at.
     */
    var index = 0

    /**
     * The next variable source identifier id.
     */
    private var nextVarId: Int = -1

    /**
     * Variable source type tables
     */
    val varSources = arrayOfNulls<VarSource?>(10)

    /**
     * Variable source identifier id map.
     */
    val varIdMap = IntArray(10)

    /**
     * An array of [ExecutionState]'s at each frame of the method execution.
     */
    val states = arrayOfNulls<ExecutionState?>(method.instructions.size())
}