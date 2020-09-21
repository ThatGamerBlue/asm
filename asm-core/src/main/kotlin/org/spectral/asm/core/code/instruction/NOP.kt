package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame

@Opcode(value=0)
class NOP : Instruction(0) {

  override fun execute(frame: ExecutionFrame) {
    /*
     * DO NOTHING.
     */
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "NOP"
}
