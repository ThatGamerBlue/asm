package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.InvokeInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.reference.MethodRef

@Opcode(value=185)
class INVOKEINTERFACE(override val method: MethodRef) : Instruction(185), InvokeInstruction {

  override val toInterface: Boolean = true

  override fun accept(visitor: MethodVisitor) {
    visitor.visitMethodInsn(opcode, method.owner.name, method.name, method.desc, toInterface)
  }

  override fun toString(): String = "INVOKEINTERFACE"
}
