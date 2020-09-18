package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=161)
class IF_ICMPLT : Instruction(161) {
  override fun accept(visitor: MethodVisitor) {

  }

  override fun toString(): String = "IF_ICMPLT"
}
