package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.TypeInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=193)
class INSTANCEOF(override val type: Type) : Instruction(193), TypeInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitTypeInsn(opcode, type.internalName)
  }

  override fun toString(): String = "INSTANCEOF"
}
