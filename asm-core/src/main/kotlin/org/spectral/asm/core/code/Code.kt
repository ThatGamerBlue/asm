package org.spectral.asm.core.code

import org.spectral.asm.core.Method

class Code(val method: Method) {

    val instructions = mutableListOf<Instruction>()

}