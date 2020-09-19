package org.spectral.asm.core.code.instruction

import kotlin.String
import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.code.Label
import org.spectral.asm.core.code.type.TableSwitchInstruction
import org.spectral.asm.core.common.Opcode

@Opcode(value=170)
class TABLESWITCH(
        override val min: Int,
        override val max: Int,
        override val defaultHandler: Label,
        override val labels: List<Label>
) : Instruction(170), TableSwitchInstruction {

  override fun accept(visitor: MethodVisitor) {
    visitor.visitTableSwitchInsn(min, max, defaultHandler.label, *labels.map { it.label }.toTypedArray())
  }

  override fun toString(): String = "TABLESWITCH"
}
