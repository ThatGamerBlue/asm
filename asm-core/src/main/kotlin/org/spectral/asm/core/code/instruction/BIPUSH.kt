package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.IntInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.ByteValue

@Opcode(value=16)
class BIPUSH(override val operand: Int) : Instruction(16), IntInstruction {

  override fun execute(frame: Frame) {
    frame.push(ByteValue(operand.toByte()))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitIntInsn(opcode, operand)
  }

  override fun toString(): String = "BIPUSH"
}
