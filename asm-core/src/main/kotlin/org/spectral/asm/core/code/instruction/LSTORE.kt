package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.code.type.VarInstructionType
import org.spectral.asm.core.common.Opcode

@Opcode(value=55)
class LSTORE(override val index: Int) : Instruction(55), VarInstruction {

  override val type = VarInstructionType.LONG

  override fun accept(visitor: MethodVisitor) {
    visitor.visitVarInsn(opcode, index)
  }

  override fun toString(): String = "LSTORE"
}
