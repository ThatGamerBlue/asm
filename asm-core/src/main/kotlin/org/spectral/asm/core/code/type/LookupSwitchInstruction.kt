package org.spectral.asm.core.code.type

import org.spectral.asm.core.code.Label

interface LookupSwitchInstruction {

    val defaultHandler: Label

    val keys: List<Int>

    val labels: List<Label>

}