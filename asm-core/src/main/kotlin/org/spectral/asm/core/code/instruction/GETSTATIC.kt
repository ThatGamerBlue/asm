package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.GetFieldInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.reference.FieldRef

@Opcode(value=178)
class GETSTATIC(override val field: FieldRef) : Instruction(178), GetFieldInstruction {

  override val static: Boolean = true

  override fun accept(visitor: MethodVisitor) {
    visitor.visitFieldInsn(opcode, field.owner.name, field.name, field.desc)
  }

  override fun toString(): String = "GETSTATIC"
}
