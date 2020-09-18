package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=181)
class PUTFIELD : Instruction(181) {
  override fun accept(visitor: MethodVisitor) {

  }

  override fun toString(): String = "PUTFIELD"
}
