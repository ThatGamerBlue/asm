package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.LongValue

@Opcode(value=10)
class LCONST_1 : Instruction(10) {

  override fun execute(frame: Frame) {
    frame.pushWide(LongValue(1L))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "LCONST_1"
}
