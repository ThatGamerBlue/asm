package org.spectral.asm.core

import org.objectweb.asm.Type
import java.lang.StringBuilder

class Signature(type: Type) {

    val desc: String get() {
        val sb = StringBuilder()
        if(argumentTypes.isNotEmpty()) {
            sb.append('(')
            argumentTypes.forEach { arg ->
                sb.append(arg.toString())
            }
            sb.append(')')
        }
        sb.append(returnType.toString())

        return sb.toString()
    }

    var returnType: Type = if(type.sort != Type.METHOD) type else type.returnType

    var argumentTypes: MutableList<Type> = if(type.sort != Type.METHOD) mutableListOf() else type.argumentTypes.toMutableList()

}