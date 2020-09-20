package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.SetFieldInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.reference.FieldRef

@Opcode(value=179)
class PUTSTATIC(override val field: FieldRef) : Instruction(179), SetFieldInstruction {

  override val static = true

  override fun accept(visitor: MethodVisitor) {
    visitor.visitFieldInsn(opcode, field.owner.name, field.name, field.desc)
  }

  override fun toString(): String = "PUTSTATIC"
}
