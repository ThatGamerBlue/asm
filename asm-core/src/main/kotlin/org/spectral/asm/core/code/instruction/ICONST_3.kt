package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.IntValue

@Opcode(value=6)
class ICONST_3 : Instruction(6) {

  override fun execute(frame: Frame) {
    frame.push(IntValue(3))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "ICONST_3"
}
