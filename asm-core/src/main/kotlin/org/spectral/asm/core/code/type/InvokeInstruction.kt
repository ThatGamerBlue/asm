package org.spectral.asm.core.code.type

import org.spectral.asm.core.reference.MethodRef

interface InvokeInstruction {

    val method: MethodRef

    val toInterface: Boolean

}