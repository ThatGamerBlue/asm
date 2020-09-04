package org.spectral.asm.simulator

import org.objectweb.asm.tree.analysis.Analyzer
import org.spectral.asm.core.Method
import org.spectral.asm.simulator.controlflow.BlockHandler
import org.spectral.asm.simulator.util.FlowUtil
import org.spectral.asm.simulator.value.AbstractValue

class MethodSimulator private constructor(
        val method: Method,
        private val interpreter: ExecInterpreter
) : Analyzer<AbstractValue>(interpreter) {

    /**
     * The control flow block handler instance.
     */
    private val blockHandler: BlockHandler = BlockHandler(method)

    /**
     * Public constructor for creating the simulator instance.
     *
     * @param method Method
     * @constructor
     */
    constructor(method: Method) : this(method, ExecInterpreter()) {
        interpreter.simulator = this
        interpreter.blockHandler = blockHandler
    }

    /**
     * Invoked when an exception breaks the control flow graph.
     *
     * @param insnIndex Int
     * @param successorIndex Int
     * @return Boolean
     */
    override fun newControlFlowExceptionEdge(insnIndex: Int, successorIndex: Int): Boolean {
        blockHandler.add(insnIndex, successorIndex)
        return true
    }

    override fun newControlFlowEdge(insnIndex: Int, successorIndex: Int) {
        /*
         * Create a normal control flow block when
         * the instruction is a flow modifier or conditional branch.
         */
        if(FlowUtil.isFlowModifier(method, insnIndex, successorIndex)) {
            blockHandler.add(insnIndex, successorIndex)
        }
    }
}