package org.spectral.asm.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.Method
import org.spectral.asm.core.ext.isPrimitive
import org.spectral.asm.core.ext.slotSize
import java.util.*
import kotlin.math.min

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
    var varSources = arrayOfNulls<VarSource?>(10)

    /**
     * Variable source identifier id map.
     */
    val varIdMap = IntArray(10)

    /**
     * An array of [ExecutionState]'s at each frame of the method execution.
     */
    val states = arrayOfNulls<ExecutionState?>(method.instructions.size())

    /**
     * Initialize the state recorder.
     */
    fun init() {

        /**
         * If the method is NOT static, the first local variable in the LVT is
         * always 'this'.
         */
        if(!method.isStatic()) {
            localVarIds[localsSize] = getNextVarId(VarSource.ARG)
            locals[localsSize++] = method.owner.type
        }

        /**
         * Add the method arguments to the top of the LVT.
         */
        method.arguments.forEach { arg ->
            localVarIds[localsSize] = getNextVarId(VarSource.ARG)
            locals[localsSize++] = arg.type.type

            /*
             * If the argument type has a extended size, account for that by
             * passing it from the TOP variable source.
             */
            if(arg.type.type.slotSize == 2) {
                locals[localsSize++] = CommonClasses.TOP
            }
        }

       updateState()
    }

    /**
     * Updates the current state of the recorder. Returns whether there are
     * any possible execution states proceeding the current state.
     *
     * @return Boolean
     */
    private fun updateState(): Boolean {
        val oldState = states[index]

        if(oldState == null
                || oldState.stack.size != stackSize
                || oldState.locals.size != localsSize
                || !compareVars(oldState.locals, locals, min(oldState.locals.size, localsSize))
                || !compareVars(oldState.stack, stack, stackSize)
        ) {
            if(oldState != null) {
                val newState = mergeStates(oldState)

                if(newState == oldState) {
                    return false
                }

                states[index] = newState
            } else {
                states[index] = ExecutionState(locals, localVarIds, localsSize, stack, stackVarIds, stackSize)
            }

            return true
        } else {
            return false
        }
    }

    private fun mergeStates(oldState: ExecutionState): ExecutionState {
        var lastUsed = -1
        var newLocals: Array<Type?>? = null
        var newLocalVarIds: IntArray? = null

        val max = min(oldState.locals.size, localsSize)
        for(i in 0 until max) {
            val a = oldState.locals[i]
            val b = locals[i]
            val common = CommonClasses.getCommonType(a, b)

            if(common != a) {
                if(newLocals == null) {
                    newLocals = oldState.locals.copyOf(max)
                }

                newLocals[i] = common
            }

            if(common != null) {
                lastUsed = i
            } else if(oldState.localVarIds[i] != 0) {
                if(newLocalVarIds == null) {
                    newLocalVarIds = oldState.localVarIds.copyOf(max)
                }

                newLocalVarIds[i] = 0
            }

            if(localVarIds[i] != oldState.localVarIds[i]) {
                // todo record variable id map
            }
        }

        if(newLocals == null) {
            newLocals = oldState.locals
        }

        if(lastUsed + 1 != newLocals.size) {
            newLocals = newLocals.copyOf(lastUsed + 1)
            newLocalVarIds = (if(newLocalVarIds == null) oldState.localVarIds else newLocalVarIds)
                    .copyOf(lastUsed + 1)
        }
        else if(newLocalVarIds == null) {
            newLocalVarIds = oldState.localVarIds
        }

        if(stackSize != oldState.stack.size) {
            throw IllegalStateException("Mismatching stack sizes")
        }

        var newStack: Array<Type?>? = null

        for(i in 0 until stackSize) {
            val a = oldState.stack[i]
            val b = stack[i]
            val common = CommonClasses.getCommonType(a, b)

            if(common != a) {
                if(newStack == null) {
                    newStack = oldState.stack.copyOf(stackSize)
                }

                newStack[i] = common
            }

            if(stackVarIds[i] != oldState.stackVarIds[i]) {
                // todo record variable identifier map ids
            }
        }

        if(newStack == null) {
            newStack = oldState.stack
        }

        return ExecutionState(newLocals, newLocalVarIds, newStack, oldState.stackVarIds)
    }

    /**
     * Compares two given local variable tables and returns if they are the same or not.
     *
     * @param typesA Array<Type?>
     * @param typesB Array<Type?>
     * @param size Int
     * @return Boolean
     */
    private fun compareVars(typesA: Array<Type?>, typesB: Array<Type?>, size: Int): Boolean {
        for(i in 0 until size) {
            val a = typesA[i]
            val b = typesB[i]

            if(a == null || b == null) {
                return false
            }

            if(a.sort != b.sort) return false
        }

        return true
    }

    /**
     * Gets the next local variable source identifier id.
     * If the source map needs its size increased, the map size is doubled.
     *
     * @param source VarSource
     * @return Int
     */
    fun getNextVarId(source: VarSource): Int {
        if(nextVarId == varSources.size) {
            varSources = varSources.copyOf(varSources.size * 2)
        }

        varSources[nextVarId] = source
        return ++nextVarId
    }

    /**
     * Gets an [ExecutionState] object of the current state recorder's execution state.
     *
     * @return ExecutionState
     */
    fun getState(): ExecutionState {
        return ExecutionState(locals, localVarIds, localsSize, stack, stackVarIds, stackSize)
    }
}