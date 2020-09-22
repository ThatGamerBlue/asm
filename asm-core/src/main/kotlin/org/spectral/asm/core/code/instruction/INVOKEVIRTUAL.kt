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
    val type = Type.getMethodType(method.desc)
    val argTypes = type.argumentTypes

    val argValues = mutableListOf<AbstractValue>()

    for(argType in argTypes) {
      /*
       * Double and long types are 64bits. Pop 2x 32bit values.
       */
      if(argType == Type.DOUBLE_TYPE || argType == Type.LONG_TYPE) {
        repeat(2) { argValues.add(frame.pop().copy()) }
      } else {
        argValues.add(frame.pop().copy())
      }
    }

    /*
     * Create a new frame for method invocation.
     */
    if(method.method != null) {
      frame.invokeMethod(method.method!!, argValues, null)
    } else {
      frame.push(ObjectValue(null, type.returnType))
    }
  }

  override fun accept(visitor: MethodVisitor) {
    visitor.visitMethodInsn(opcode, method.owner.name, method.name, method.desc, toInterface)
  }

  override fun toString(): String = "INVOKEVIRTUAL"
}
