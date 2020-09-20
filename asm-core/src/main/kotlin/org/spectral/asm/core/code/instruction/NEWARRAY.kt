package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ArrayInstruction
import org.spectral.asm.core.code.type.IntInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=188)
class NEWARRAY(override val operand: Int) : Instruction(188), IntInstruction, ArrayInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitIntInsn(opcode, operand)
  }

  override fun toString(): String = "NEWARRAY"
}
