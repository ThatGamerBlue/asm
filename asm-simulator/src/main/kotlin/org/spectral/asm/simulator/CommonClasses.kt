package org.spectral.asm.simulator

import org.objectweb.asm.Type
import org.spectral.asm.core.ClassPool

class CommonClasses(private val pool: ClassPool) {

    /**
     * Common primitive classes.
     * If they dont exist as virtual classes in the provided class
     * pool, create them.
     */

    val INT = pool.getOrCreate(Type.INT_TYPE)
    val LONG = pool.getOrCreate(Type.LONG_TYPE)
    val BOOLEAN = pool.getOrCreate(Type.BOOLEAN_TYPE)
    val BYTE = pool.getOrCreate(Type.BYTE_TYPE)
    val CHAR = pool.getOrCreate(Type.CHAR_TYPE)
    val SHORT = pool.getOrCreate(Type.SHORT_TYPE)
    val FLOAT = pool.getOrCreate(Type.FLOAT_TYPE)
    val DOUBLE = pool.getOrCreate(Type.DOUBLE_TYPE)
    val NULL = pool.getOrCreate("null")
    val VOID = pool.getOrCreate(Type.VOID_TYPE)
    val TOP = VOID
    val STRING = pool.getOrCreate("java/lang/String")
}