package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=10)
class LCONST_1 : Instruction(10) {
  override fun accept(visitor: MethodVisitor) {

  }

  override fun toString(): String = "LCONST_1"
}
