package org.spectral.asm.execution

import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.execution.common.Stack

/**
 * Represents an execution frame.
 *
 * @property insn The instruction executed during this frame.
 * @property stack The JVM stack of this frame.
 * @property lvt the JVM local variable table of this frame.
 * @constructor
 */
class Frame internal constructor(override val insn: Instruction, val stack: Stack, val lvt: Stack) : ExecutionFrame {

    /**
     * Creates a frame with a blank stack and lvt.
     *
     * @constructor
     */
    constructor(insn: Instruction) : this(insn, Stack(), Stack())

}