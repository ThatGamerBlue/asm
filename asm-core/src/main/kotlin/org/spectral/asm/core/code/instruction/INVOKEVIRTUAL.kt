package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.type.InvokeInstruction
import org.spectral.asm.core.common.Opcode
import org.spectral.asm.core.execution.Frame
import org.spectral.asm.core.execution.value.AbstractValue
import org.spectral.asm.core.execution.value.ObjectValue
import org.spectral.asm.core.reference.MethodRef

@Opcode(value=182)
class INVOKEVIRTUAL(override val method: MethodRef) : Instruction(182), InvokeInstruction {

  override val toInterface: Boolean = false

  override fun execute(frame: Frame) {
    val methodType = Type.getMethodType(method.desc)

    val args = mutableListOf<AbstractValue>()

    for(arg in methodType.argumentTypes) {
      if(arg == Type.DOUBLE_TYPE || arg == Type.LONG_TYPE) {
        args.add(frame.popWide())
      } else {
        args.add(frame.pop())
      }
    }

    if(method.method != null) {
      frame.execution.invokeMethod(method.method!!, args)
    } else {
      frame.push(ObjectValue(null, methodType))
    }
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitMethodInsn(opcode, method.owner.name, method.name, method.desc, toInterface)
  }

  override fun toString(): String = "INVOKEVIRTUAL"
}
