package org.spectral.asm.analysis.simulator

import org.spectral.asm.core.Method

/**
 * Simulates the JVM execution of a method.
 *
 * @property subject Method
 * @constructor
 */
class MethodSimulator private constructor(val subject: Method) {

    /**
     * Runs the method simulator.
     */
    private fun run() {

    }

    companion object {

        /**
         * Execute the simulation of a given method and return the created
         * [MethodSimulator] instance holding the results.
         *
         * @param method Method
         * @return MethodSimulator
         */
        fun simulate(method: Method): MethodSimulator {
            val simulator = MethodSimulator(method)
            simulator.run()

            return simulator
        }
    }
}