package org.spectral.asm.simulator

import org.objectweb.asm.tree.analysis.Analyzer
import org.spectral.asm.core.Method
import org.spectral.asm.simulator.controlflow.BlockHandler
import org.spectral.asm.simulator.value.AbstractValue

class MethodSimulator

/**
 * Private constructor.
 *
 * @property method Method
 * @property interpreter ExecutionInterpreter
 * @constructor
 */
private constructor(
        val method: Method,
        val interpreter: ExecutionInterpreter
) : Analyzer<AbstractValue>(interpreter) {

    /**
     * The control flow block handler.
     */
    val blockHandler = BlockHandler(method)

    /**
     * Primary constructor.
     *
     * @param method Method
     * @constructor
     */
    constructor(method: Method) : this(method, ExecutionInterpreter()) {
        this.interpreter.simulator = this
        this.interpreter.blockHandler = blockHandler
    }

    companion object {
        /**
         * Common class types.
         */
    }
}