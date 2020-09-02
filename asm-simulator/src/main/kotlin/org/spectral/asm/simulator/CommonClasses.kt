package org.spectral.asm.simulator

import org.objectweb.asm.Type

/**
 * A collection of common primitive types.
 */
object CommonClasses {
    val INT = Type.INT_TYPE
    val LONG = Type.LONG_TYPE
    val BOOLEAN = Type.BOOLEAN_TYPE
    val BYTE = Type.BYTE_TYPE
    val CHAR = Type.CHAR_TYPE
    val SHORT = Type.SHORT_TYPE
    val FLOAT = Type.FLOAT_TYPE
    val DOUBLE = Type.DOUBLE_TYPE
    val NULL = Type.getObjectType("Lnull;")
    val VOID = Type.VOID_TYPE
    val TOP = VOID
    val STRING = Type.getObjectType("Ljava/lang/String;")
}