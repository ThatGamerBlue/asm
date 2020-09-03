package org.spectral.asm.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.Class
import org.spectral.asm.core.Method
import org.spectral.asm.core.ext.isPrimitive
import org.spectral.asm.core.ext.slotSize
import kotlin.math.max
import kotlin.math.min

/**
 * Responsible for recording the state of a given method execution for each
 * frame of the execution.
 *
 * This is basically a simplified JVM stack machine with frame recording.
 *
 * @property method Method
 * @constructor
 */
class StateRecorder(private val method: Method) {

    /**
     * The state history of the execution simulation.
     */
    val states = arrayOfNulls<ExecState?>(method.instructions.size())

    /**
     * Emulated stack tracking fields.
     */
    val locals = arrayOfNulls<Class?>(method.maxLocals)
    val localVarIds = IntArray(locals.size)

    /**
     * Emulated stack tracking fields.
     */
    val stack = arrayOfNulls<Class?>(method.maxStack)
    val stackVarIds = IntArray(locals.size)

    /**
     * The sizes of both the stack and the local variable table.
     */
    var localsSize = 0
    var stackSize = 0

    /**
     * The current stack instruction index.
     */
    var index = 0

    /**
     * The next variable source identifier id.
     */
    private var nextVarId = 0

    /**
     * Variable source identifier table
     */
    var varSources = arrayOfNulls<VarSource>(10)
    var varIdMap = IntArray(10)

    /**
     * Common primitive classes from the method's class pool
     */
    val common = CommonClasses(method.pool)

    /**
     * Initialize the state recorder.
     */
    init {
        /*
         * If the method is NOT static, we need to add the 'this' lv to the
         * local variable table.
         */
        if(!method.isStatic()) {
            localVarIds[localsSize] = getNextVarId(VarSource.ARG)
            locals[localsSize++] = method.owner
        }

        /*
         * Add all of the method arguments to the LVT
         */
        method.arguments.forEach { arg ->
            localVarIds[localsSize] = getNextVarId(VarSource.ARG)
            locals[localsSize++] = arg.type

            /*
             * Account for arguments that are DOUBLE or LONG types since
             * they have extended slot sizes.
             */
            if(arg.type.type.slotSize == 2) {
                locals[localsSize++] = common.TOP
            }
        }

        /*
         * Take a snapshot of the inital / current state.
         */
        this.updateState()
    }

    /**
     * Pushes an element to the stack.
     *
     * @param element Class?
     * @param id Int
     */
    fun push(element: Class?, id: Int) {
        stackVarIds[stackSize] = id
        stack[stackSize++] = element

        /*
         * Account for extend stack slot sizes.
         */
        if(element?.type?.slotSize == 2) {
            stackVarIds[stackSize] = 0
            stack[stackSize++] = common.TOP
        }
    }

    /**
     * Pushes a [Variable] object to the stack.
     *
     * @param variable Variable
     */
    fun push(variable: Variable) {
        push(variable.type, variable.id)
    }

    /**
     * Pops an element from the top of the stack.
     *
     * @return Variable
     */
    fun pop(): Variable {
        val element = stack[--stackSize]
        stack[stackSize] = null

        val id = stackVarIds[stackSize]
        stackVarIds[stackSize] = 0

        return Variable(element, id)
    }

    /**
     * Drops two elements off the top of the stack.
     */
    fun pop2() {
        repeat(2) {
            stack[--stackSize] = null
            stackVarIds[stackSize] = 0
        }
    }

    /**
     * Drops a single element off the top of the stack and returns the
     * next element after.
     *
     * @return Variable
     */
    fun popDouble(): Variable {
        stack[--stackSize] = null
        stackVarIds[stackSize] = 0

        val element = stack[--stackSize]
        stack[stackSize] = null

        val id = stackVarIds[stackSize]
        stackVarIds[stackSize] = 0

        return Variable(element, id)
    }

    /**
     * Gets the element on the top of stack without removing it.
     *
     * @return Variable
     */
    fun peek(): Variable {
        return Variable(stack[stackSize - 1], stackVarIds[stackSize - 1])
    }

    /**
     * Same as popDouble() except the elements are not removed from the stack.
     *
     * @return Variable
     */
    fun peekDouble(): Variable {
        return Variable(stack[stackSize - 2], stackVarIds[stackSize - 2])
    }

    /**
     * Removes all elements from the stack.
     */
    fun clearStack() {
        while(stackSize > 0) {
            stack[--stackSize] = null
            stackVarIds[stackSize] = 0
        }
    }

    /**
     * Gets whether the element at the top of the stack is a
     * double slot sized element.
     */
    val isTopDoubleSlot: Boolean get() {
        val element = stack[stackSize - 2]
        return element != null && element.type.slotSize == 2
    }

    /**
     * Gets an [ExecState] instance for the current state of this recorder object.
     */
    val currentState: ExecState get() {
        return ExecState(locals, localVarIds, localsSize, stack, stackVarIds, stackSize)
    }

    /**
     * Gets a local variable at a given index in the LVT.
     *
     * @param index Int
     * @return Class
     */
    operator fun get(index: Int): Class {
        return locals[index] ?: throw IllegalStateException("Unassigned local variable at index $index")
    }

    /**
     * Gets a local variable identifier id at a given index.
     *
     * @param index Int
     * @return Int
     */
    fun getId(index: Int): Int {
        return localVarIds[index]
    }

    /**
     * Sets a local variable at a specified index.
     *
     * @param index Int
     * @param variable Variable
     */
    operator fun set(index: Int, variable: Variable) {
        set(index, variable.type, variable.id)
    }

    /**
     * Sets a element and identifier id at a specified index as a local
     * variable instance.
     *
     * @param index Int
     * @param element Class?
     * @param id Int
     */
    operator fun set(index: Int, element: Class?, id: Int) {
        locals[index] = element
        localVarIds[index] = id

        if(index >= localsSize) localsSize = index + 1
        if(element != null && element.type.slotSize == 2) {
            locals[index + 1] = common.TOP
            localVarIds[index + 1] = 0
            if(index + 1 >= localsSize) localsSize = index + 2
        }
    }

    /**
     * Continue to the next execution step.
     *
     * @return Boolean
     */
    operator fun next(): Boolean {
        index++
        return updateState()
    }

    /**
     * Jump to an execution state at a given execution index.
     *
     * @param dst Int
     * @return Boolean
     */
    fun jump(dst: Int): Boolean {
        index = dst
        return updateState()
    }

    /**
     * Jump to a specific execution index from a source execution state.
     *
     * @param dst Int
     * @param src ExecState
     * @return Boolean
     */
    fun jump(dst: Int, src: ExecState): Boolean {
        System.arraycopy(src.locals, 0, locals, 0, src.locals.size)
        System.arraycopy(src.localVarIds, 0, localVarIds, 0, src.locals.size)
        System.arraycopy(src.stack, 0, stack, 0, src.stack.size)
        System.arraycopy(src.stackVarIds, 0, stackVarIds, 0, src.stack.size)

        localsSize = src.locals.size
        stackSize = src.stack.size
        index = dst

        return updateState()
    }

    /**
     * Saves a snapshot of the current state of the execution.
     *
     * @return Boolean
     */
    private fun updateState(): Boolean {
        val oldState = states[index]
        return if(oldState == null || oldState.stack.size != stackSize || oldState.locals.size > localsSize
                || !compareVars(oldState.locals, locals, min(oldState.locals.size, localsSize))
                || !compareVars(oldState.stack, stack, stackSize)) {
            if(oldState != null) {
                /*
                 * Merge the old state with the current state properly
                 */
                val newState = mergeStates(states[index]!!)

                if(newState == oldState) {
                    return false
                }

                states[index] = newState
            } else {
                states[index] = ExecState(locals, localVarIds, localsSize, stack, stackVarIds, stackSize)
            }
            true
        } else {
            false
        }
    }

    /**
     * Merges the current execution state with the given [oldState]
     *
     * @param oldState ExecState
     * @return ExecState
     */
    private fun mergeStates(oldState: ExecState): ExecState {
        var lastUsed = -1
        var newLocals: Array<Class?>? = null
        var newLocalVarIds: IntArray? = null

        run {
            var i = 0
            val max = min(oldState.locals.size, localsSize)
            while(i < max) {
                val a = oldState.locals[i]
                val b = locals[i]
                val commonClass = getCommonSuperClass(a, b)

                if(commonClass != a) {
                    if(newLocals == null) newLocals = oldState.locals.copyOf(max)
                    newLocals!![i] = commonClass
                }

                if(commonClass != null) {
                    lastUsed = i
                } else if(oldState.localVarIds[i] != 0) {
                    if(newLocalVarIds == null) newLocalVarIds = oldState.localVarIds.copyOf(max)
                    newLocalVarIds!![i] = 0
                }

                if(localVarIds[i] != oldState.localVarIds[i]) {
                    recordVarIdMap(localVarIds[i], oldState.localVarIds[i])
                }

                i++
            }
        }

        if(newLocals == null) newLocals = oldState.locals
        if(lastUsed + 1 != newLocals!!.size) {
            newLocals = newLocals!!.copyOf(lastUsed + 1)
            newLocalVarIds = (if(newLocalVarIds == null) oldState.localVarIds else newLocalVarIds)!!
                    .copyOf(lastUsed + 1)
        } else if(newLocalVarIds == null) {
            newLocalVarIds = oldState.localVarIds
        }

        var newStack: Array<Class?>? = null
        for(i in 0 until stackSize) {
            val a = oldState.stack[i]
            val b = stack[i]
            val commonClass = getCommonSuperClass(a, b) //?: throw IllegalStateException("Incompatible stack types: $a, $b")

            if(commonClass != a) {
                if(newStack == null) newStack = oldState.stack.copyOf(stackSize)
                newStack[i] = commonClass
            }

            if(stackVarIds[i] != oldState.stackVarIds[i]) {
                recordVarIdMap(stackVarIds[i], oldState.stackVarIds[i])
            }
        }

        if(newStack == null) newStack = oldState.stack

        return ExecState(newLocals!!, newLocalVarIds!!, newStack, oldState.stackVarIds)
    }

    /**
     * Gets the next variable identifier id given the source of
     * the lv's data.
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
     * Gets a [Class] which both [a] and [b] are assignable from and both
     * share hierarchy trees.
     *
     * @param a Class?
     * @param b Class?
     * @return Class?
     */
    private fun getCommonSuperClass(a: Class?, b: Class?): Class? {
        return if(a == b) {
            a
        } else if(a == null || b == null) {
            null
        } else if(b == common.NULL && !a.type.isPrimitive()) {
            a
        } else if(a == common.NULL && !b.type.isPrimitive()) {
            b
        } else if(a.type.isPrimitive() && b.type.isPrimitive()) {
            val typeA = a.type
            val typeB = b.type

            if((typeA == Type.INT_TYPE || typeA == Type.BOOLEAN_TYPE || typeA == Type.BYTE_TYPE || typeA == Type.SHORT_TYPE)
                    && (typeB == Type.INT_TYPE || typeB == Type.BOOLEAN_TYPE || typeB == Type.BYTE_TYPE || typeB == Type.SHORT_TYPE)) {
                common.INT
            } else {
                null
            }
        } else {
            a.getCommonSuperClass(b)
        }
    }

    /**
     * Gets whether the two LVT's are the same or not.
     *
     * @param typesA Array<Class?>
     * @param typesB Array<Class?>
     * @param size Int
     * @return Boolean
     */
    private fun compareVars(typesA: Array<Class?>, typesB: Array<Class?>, size: Int): Boolean {
        for(i in 0 until size) {
            val a = typesA[i]
            val b = typesB[i]
            val commonClass = getCommonSuperClass(a, b)

            if(commonClass != a) return false
        }

        return true
    }

    /**
     * Record the variable identifiers from [srcId] to [dstId] to the the
     * variable identifier id table.
     *
     * @param srcId Int
     * @param dstId Int
     */
    private fun recordVarIdMap(srcId: Int, dstId: Int) {
        var src = srcId
        var dst = dstId

        if(src < dst) {
            val tmp = src
            src = dst
            dst = tmp
        }

        /**
         * If the src is outside the variable identifier id map table size,
         * either increment the table size by one, or double it. Which ever is is larger. (basically
         * only the case when a single identifier is in the table)
         */
        if(src >= varIdMap.size) {
            varIdMap = varIdMap.copyOf(max(varIdMap.size * 2, src + 1))
        }

        /**
         * Update the point map until the loop is broken
         */
        while(true) {
            val prev = varIdMap[src]
            if(prev == dst) {
                break
            }
            else if(prev == 0) {
                varIdMap[src] = dst
                break
            }
            else if(prev < dst) {
                varIdMap[src] = dst
                src = dst
                dst = prev
            }
            else {
                src = prev
            }
        }
    }
}