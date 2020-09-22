package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.ConstantInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.*
import org.spectral.asm.core.reference.ClassRef

@Opcode(value=18)
class LDC(override val cst: Any?) : Instruction(18), ConstantInstruction {

  override fun execute(frame: Frame) {
    if(cst == null) {
      throw ExecutionException("Invalid LDC constant.")
    }

    var load = cst
    if(load is Type) {
      load = ClassRef(load.internalName)
    }

    when(load) {
      is Int -> frame.push(IntValue(load))
      is Float -> frame.push(FloatValue(load))
      is Double -> frame.pushWide(DoubleValue(load))
      is Long -> frame.pushWide(LongValue(load))
      is String -> frame.push(ObjectValue(load, Type.getObjectType("java/lang/String")))
      is ClassRef -> frame.push(ObjectValue(load, cst as Type))
      else -> throw ExecutionException("Unexpected LDC type ${cst.javaClass}")
    }
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitLdcInsn(cst)
  }

  override fun toString(): String = "LDC"
}
