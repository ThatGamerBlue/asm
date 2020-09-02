package org.spectral.asm.simulator

import org.spectral.asm.core.Method

/**
 * A utility for simulating methods as they would
 * execute on the JVM stack.
 */
object Simulator {

    /**
     * Simulates a method as the JVM runtime would by creating virtual methods
     * and recording the state of a ClassPool at each frame.
     *
     * @param method Method
     */
    fun simulateMethod(method: Method) {
        val methodSimulator = MethodSimulator(method)
        methodSimulator.run()
    }
}