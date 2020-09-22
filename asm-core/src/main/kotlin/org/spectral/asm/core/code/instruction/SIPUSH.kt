package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.IntInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.ShortValue

@Opcode(value=17)
class SIPUSH(override val operand: Int) : Instruction(17), IntInstruction {

  override fun execute(frame: Frame) {
    frame.push(ShortValue(operand.toShort()))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitIntInsn(opcode, operand)
  }

  override fun toString(): String = "SIPUSH"
}
