package org.spectral.asm.analysis.simulator

import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool

object CommonClass {

    val INT = createVirtualClass(Type.INT_TYPE.className)
    val LONG = createVirtualClass(Type.LONG_TYPE.className)
    val BOOLEAN = createVirtualClass(Type.BOOLEAN_TYPE.className)
    val BYTE = createVirtualClass(Type.BYTE_TYPE.className)
    val CHAR = createVirtualClass(Type.CHAR_TYPE.className)
    val SHORT = createVirtualClass(Type.SHORT_TYPE.className)
    val FLOAT = createVirtualClass(Type.FLOAT_TYPE.className)
    val DOUBLE = createVirtualClass(Type.DOUBLE_TYPE.className)
    val NULL = createVirtualClass("NULL")
    val VOID = createVirtualClass(Type.VOID_TYPE.className)
    val TOP = VOID
    val STRING = createVirtualClass("java/lang/String")

    /**
     * Creates a fake or virtual [Class] object.
     *
     * @param name String
     * @return Class
     */
    private fun createVirtualClass(name: String): Class {
        val node = ClassNode(ASM8).apply {
            this.access = 0
            this.name = name
            this.superName = null
        }

        return Class(ClassPool(), node)
    }
}