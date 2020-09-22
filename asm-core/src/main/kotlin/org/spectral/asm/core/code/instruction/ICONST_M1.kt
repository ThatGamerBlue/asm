package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.IntValue

@Opcode(value=2)
class ICONST_M1 : Instruction(2) {

  override fun execute(frame: Frame) {
    frame.push(IntValue(-1))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "ICONST_M1"
}
