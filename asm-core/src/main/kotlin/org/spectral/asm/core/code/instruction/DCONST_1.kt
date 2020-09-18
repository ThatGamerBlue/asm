package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=15)
class DCONST_1 : Instruction(15) {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "DCONST_1"
}
