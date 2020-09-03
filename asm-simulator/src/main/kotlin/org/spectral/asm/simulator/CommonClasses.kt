package org.spectral.asm.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.ext.isPrimitive

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

    fun getCommonType(a: Type?, b: Type?): Type? {
        if(a == b) {
            return a
        }
        else if(a == null || b == null) {
            return null
        }
        else if(b == NULL && !a.isPrimitive()) {
            return a
        }
        else if(a == NULL && !b.isPrimitive()) {
            return b
        }
        else if(a.isPrimitive() && b.isPrimitive()) {
            val idA = a.descriptor[0]
            val idB = b.descriptor[0]

            if((idA == 'I' || idA == 'Z' || idA == 'C' || idA == 'B' || idA == 'S') &&
                    (idB == 'I' || idB == 'Z' || idB == 'C' || idB == 'B' || idB == 'S')) {
                return INT
            } else {
                return null
            }
        } else {
            return a
        }
    }
}