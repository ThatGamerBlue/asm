package org.spectral.asm.core.code.type

import org.objectweb.asm.Type

interface MultiArrayInstruction : ArrayInstruction {

    val type: Type

    val dims: Int

}