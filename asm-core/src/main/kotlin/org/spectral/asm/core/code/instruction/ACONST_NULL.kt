package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.core.execution.value.ObjectValue

@Opcode(value=1)
class ACONST_NULL : Instruction(1) {

  override fun execute(frame: ExecutionFrame) {
    frame.push(ObjectValue(null, Type.getObjectType("java/lang/Object")))
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitInsn(opcode)
  }

  override fun toString(): String = "ACONST_NULL"

}
