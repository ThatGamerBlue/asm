package org.spectral.asm.core.code.type

interface ArrayInstruction {

    interface Store : ArrayInstruction {}

    interface Load : ArrayInstruction {}

}