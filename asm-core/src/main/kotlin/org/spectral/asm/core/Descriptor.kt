package org.spectral.asm.core

import org.objectweb.asm.Type
import java.lang.StringBuilder

class Descriptor(val argumentTypes: MutableList<Type>, val returnType: Type) {

    constructor(type: Type) : this(type.argumentTypes.toMutableList(), type.returnType)

    constructor(desc: String) : this(Type.getMethodType(desc))

    constructor(other: Descriptor) : this(other.argumentTypes, other.returnType)

    override fun toString(): String {
        val sb = StringBuilder()

        if(argumentTypes.isNotEmpty()) {
            sb.append('(')
            argumentTypes.forEach { sb.append(it.toString()) }
            sb.append(')')
        }

        sb.append(returnType.toString())

        return sb.toString()
    }

    val size: Int get() = argumentTypes.size

    val isVoid: Boolean get() = returnType == Type.VOID_TYPE
}