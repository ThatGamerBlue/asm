package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=165)
class IF_ACMPEQ : Instruction(165) {
  override fun accept(visitor: MethodVisitor) {

  }

  override fun toString(): String = "IF_ACMPEQ"
}
