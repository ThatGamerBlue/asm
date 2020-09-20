package org.spectral.asm.core.code.type

import org.spectral.asm.core.reference.FieldRef

interface FieldInstruction {

    val field: FieldRef

    val static: Boolean

}