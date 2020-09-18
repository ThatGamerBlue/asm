package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=141)
class F2D : Instruction(141) {
  override fun accept(visitor: MethodVisitor) {

  }

  override fun toString(): String = "F2D"
}
