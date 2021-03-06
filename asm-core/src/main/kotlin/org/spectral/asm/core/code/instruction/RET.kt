package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.InstructionType
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=169)
class RET(override val index: Int) : Instruction(169), VarInstruction {

  override val type = InstructionType.INT

  override fun accept(visitor: MethodVisitor) {
    visitor.visitVarInsn(opcode, index)
  }

  override fun toString(): String = "RET"
}
