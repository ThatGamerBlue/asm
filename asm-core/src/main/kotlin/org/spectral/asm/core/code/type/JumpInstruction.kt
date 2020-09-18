package org.spectral.asm.core.code.type

import org.spectral.asm.core.code.Label

interface JumpInstruction {

    val label: Label

}