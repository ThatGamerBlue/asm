package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.MultiArrayInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=197)
class MULTIANEWARRAY(override val type: Type, override val dims: Int) : Instruction(197), MultiArrayInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitMultiANewArrayInsn(type.internalName, dims)
  }

  override fun toString(): String = "MULTIANEWARRAY"
}
