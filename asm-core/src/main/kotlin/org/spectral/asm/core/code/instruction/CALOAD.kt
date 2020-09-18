package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ArrayInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=52)
class CALOAD : Instruction(52), ArrayInstruction.Load {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "CALOAD"
}
