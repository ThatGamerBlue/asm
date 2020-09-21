package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.core.execution.value.IntValue

@Opcode(value=8)
class ICONST_5 : Instruction(8) {

  override fun execute(frame: ExecutionFrame) {
    frame.push(IntValue(5))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "ICONST_5"
}
