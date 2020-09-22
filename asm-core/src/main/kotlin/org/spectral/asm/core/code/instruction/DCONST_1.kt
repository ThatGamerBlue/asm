package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.DoubleValue

@Opcode(value=15)
class DCONST_1 : Instruction(15) {

  override fun execute(frame: Frame) {
    frame.pushWide(DoubleValue(1.0))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "DCONST_1"
}
