package org.spectral.asm.core

import org.objectweb.asm.tree.LocalVariableNode

data class LocalVariable(
        val method: Method,
        val isArg: Boolean,
        val index: Int,
        val lvIndex: Int,
        val asmIndex: Int,
        val type: Class,
        val startInsn: Int,
        val endInsn: Int,
        val startOpIndex: Int,
        val name: String,
        val node: LocalVariableNode? = null
)