package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.code.type.InstructionType
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.core.execution.value.TopValue

@Opcode(value=24)
class DLOAD(override val index: Int) : Instruction(24), VarInstruction {

  override val type = InstructionType.DOUBLE

  override fun execute(frame: ExecutionFrame) {
    frame.load(index)
    frame.push(TopValue())
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitVarInsn(opcode, index)
  }

  override fun toString(): String = "DLOAD"
}
