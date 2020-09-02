package org.spectral.asm.analysis.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.Class
import org.spectral.asm.core.Method

/**
 * Responsible for holding a sequential series of states that
 * a given method's execution changes each frame.
 *
 * @property method Method
 * @constructor
 */
class StateRecorder(val method: Method) {

    /**
     * A mock local variable table for this method.
     */
    val locals = arrayOfNulls<Class?>(method.maxLocals)

    /**
     * A table of identifier id's for the local variables.
     */
    val localIds = IntArray(locals.size)

    /**
     * A mock JVM stack that the method executes on.
     */
    val stack = arrayOfNulls<Class?>(method.maxStack)

    /**
     * A table of identifier id's for the stack elements.
     */
    val stackIds = IntArray(stack.size)


}