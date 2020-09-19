package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.Label
import org.spectral.asm.core.code.type.LookupSwitchInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=171)
class LOOKUPSWITCH(
        override val defaultHandler: Label,
        override val keys: List<Int>,
        override val labels: List<Label>
) : Instruction(171), LookupSwitchInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitLookupSwitchInsn(defaultHandler.label, keys.toIntArray(), labels.map { it.label }.toTypedArray())
  }

  override fun toString(): String = "LOOKUPSWITCH"
}
