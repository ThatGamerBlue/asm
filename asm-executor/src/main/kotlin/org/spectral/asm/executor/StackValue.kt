package org.spectral.asm.executor

import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.analysis.Value

class StackValue(
        val insn: AbstractInsnNode,
        val type: Type?,
        val value: Any?
) : Value {

    override fun getSize(): Int {
        return when(type) {
            null -> 1
            else -> type.size
        }
    }

    val isReference: Boolean get() {
        return type != null && (type.sort == Type.OBJECT || type.sort == Type.ARRAY)
    }

    companion object {
        fun pushUninitialized(insn: AbstractInsnNode) = StackValue(insn, null, null)
        fun pushInt(insn: AbstractInsnNode, value: Int?) = StackValue(insn, Type.INT_TYPE, value)
        fun pushFloat(insn: AbstractInsnNode, value: Float?) = StackValue(insn, Type.FLOAT_TYPE, value)
        fun pushLong(insn: AbstractInsnNode, value: Long?) = StackValue(insn, Type.LONG_TYPE, value)
        fun pushDouble(insn: AbstractInsnNode, value: Double?) = StackValue(insn, Type.DOUBLE_TYPE, value)
        fun pushString(insn: AbstractInsnNode, value: String?) = StackValue(insn, Type.getObjectType("java/lang/String"), value)
        fun pushClass(insn: AbstractInsnNode, value: Type?) = StackValue(insn, Type.getObjectType("java/lang/Class"), value)
        fun pushReturn(insn: AbstractInsnNode) = StackValue(insn, Type.VOID_TYPE, null)
    }
}