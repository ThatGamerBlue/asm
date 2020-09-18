package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=18)
class LDC(override val cst: Any?) : Instruction(18), ConstantInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitLdcInsn(cst)
  }

  override fun toString(): String = "LDC"
}
