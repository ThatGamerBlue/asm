package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.Label
import org.spectral.asm.core.code.type.JumpInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=168)
class JSR(override val label: Label) : Instruction(168), JumpInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitJumpInsn(opcode, label.label)
  }

  override fun toString(): String = "JSR"
}
