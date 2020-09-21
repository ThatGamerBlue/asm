package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.ExecutionFrame
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.*

@Opcode(value=18)
class LDC(override val cst: Any?) : Instruction(18), ConstantInstruction {

  override fun execute(frame: ExecutionFrame) {
    if(cst == null) {
      throw ExecutionException("LDC constant cannot be null.")
    }

    when(cst) {
      is Type -> frame.push(ObjectValue(cst, cst))
      is Int -> frame.push(IntValue(cst))
      is Float -> frame.push(FloatValue(cst))
      is Double -> frame.pushWide(DoubleValue(cst))
      is Long -> frame.pushWide(LongValue(cst))
      is String -> frame.push(ObjectValue(cst, Type.getObjectType("java/lang/String")))
      is Class<*> -> frame.push(ObjectValue(cst, Type.getObjectType("java/lang/Class")))
      else -> throw ExecutionException("Unexpected LDC value of '$cst'.")
    }
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitLdcInsn(cst)
  }

  override fun toString(): String = "LDC"
}
