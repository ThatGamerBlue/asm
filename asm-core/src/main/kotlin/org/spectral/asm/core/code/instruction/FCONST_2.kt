package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=13)
class FCONST_2 : Instruction(13) {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "FCONST_2"
}
