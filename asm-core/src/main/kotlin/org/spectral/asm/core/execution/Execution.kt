package org.spectral.asm.core.execution

import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.Method
import org.spectral.asm.core.execution.value.AbstractValue

/**
 * Represents the execution of a method as the JVM would.
 * returns a collection of frames.
 *
 * @property pool ClassPool
 * @constructor
 */
class Execution(val pool: ClassPool) {

    /**
     * Executes a method.
     *
     * @param method Method
     */
    fun executeMethod(method: Method, args: List<AbstractValue>) {

    }
}