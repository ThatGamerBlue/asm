package org.spectral.asm.core.code.type

enum class InstructionType(val slots: Int) {

    INT(1),
    FLOAT(1),
    OBJECT(1),
    LONG(2),
    DOUBLE(2);

}