package org.spectral.asm.core.code.instruction

import org.spectral.asm.core.code.Code
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.InstructionType

class ARRAYLENGTH(
  code: Code,
  type: InstructionType
) : Instruction(code, type) {
  constructor(code: Code) : this(code, InstructionType.ARRAYLENGTH)
}
