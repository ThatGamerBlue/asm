package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.DoubleValue

@Opcode(value=14)
class DCONST_0 : Instruction(14) {

  override fun execute(frame: Frame) {
    frame.pushWide(DoubleValue(0.0))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "DCONST_0"
}
