package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.code.type.InstructionType
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame

@Opcode(value=23)
class FLOAD(override val index: Int) : Instruction(23), VarInstruction {

  override val type = InstructionType.FLOAT

  override fun execute(frame: ExecutionFrame) {
    frame.load(index)
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitVarInsn(opcode, index)
  }

  override fun toString(): String = "FLOAD"
}
