package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.VarInstruction
import org.spectral.asm.core.code.type.InstructionType
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame

@Opcode(value=57)
class DSTORE(override val index: Int) : Instruction(57), VarInstruction {

  override val type = InstructionType.DOUBLE

  override fun execute(frame: Frame) {
    frame.store(index, frame.popWide().copy())
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitVarInsn(opcode, index)
  }

  override fun toString(): String = "DSTORE"
}
