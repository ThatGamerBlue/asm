package org.spectral.asm.core.execution

import org.spectral.asm.core.code.Instruction

/**
 * Represents an execution frame as a snapshot in time after an instruction execution.
 */
interface ExecutionFrame {

    /**
     * The instruction which was execution during this frame.
     */
    val insn: Instruction

    /**
     * The values which were pushed to the stack during this execution frame.
     */
    val pushes: List<ExecutionValue>

    /**
     * The value which were popped from the stack during this execution frame.
     */
    val pops: List<ExecutionValue>

    /**
     * The values which were loaded from the LVT to the stack during this execution frame.
     */
    val loads: List<ExecutionValue>

    /**
     * The values which were saved to the LVT during this execution frame.
     */
    val stores: List<ExecutionValue>
}