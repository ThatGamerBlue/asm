package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=90)
class DUP_X1 : Instruction(90) {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "DUP_X1"
}
