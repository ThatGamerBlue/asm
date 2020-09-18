package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.IncInstruction
import org.spectral.asm.core.code.type.InstructionType
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=132)
class IINC(override val index: Int, override val inc: Int) : Instruction(132), IncInstruction, VarInstruction {

  override val type = InstructionType.INT

  override fun accept(visitor: MethodVisitor) {
    visitor.visitIincInsn(index, inc)
  }

  override fun toString(): String = "IINC"
}
