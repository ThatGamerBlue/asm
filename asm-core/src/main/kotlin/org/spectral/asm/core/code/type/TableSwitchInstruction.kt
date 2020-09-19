package org.spectral.asm.core.code.type

import org.spectral.asm.core.code.Label

interface TableSwitchInstruction {

    val min: Int

    val max: Int

    val defaultHandler: Label

    val labels: List<Label>

}