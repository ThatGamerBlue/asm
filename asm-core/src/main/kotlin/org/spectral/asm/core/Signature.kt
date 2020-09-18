package org.spectral.asm.core

import org.objectweb.asm.Type
import java.lang.StringBuilder

/**
 * Represents an field, or method descriptor but allows for modification
 * of the returnType and argument types to build the descriptor.
 *
 * @property desc String
 * @property returnType Type
 * @property argumentTypes MutableList<Type>
 * @constructor
 */
class Signature(var type: Type) {

    val desc: String get() {
        val sb = StringBuilder()
        if(argumentTypes.isNotEmpty()) {
            sb.append('(')
            argumentTypes.forEach { arg ->
                sb.append(arg.toString())
            }
            sb.append(')')
        }

        if(type.sort != Type.METHOD) {
            sb.append(type.toString())
        } else {
            sb.append(returnType.toString())
        }

        return sb.toString()
    }

    var returnType: Type = if(type.sort != Type.METHOD) type else type.returnType
        set(value) {
            if(type.sort != Type.METHOD) {
                throw UnsupportedOperationException("Return type cannot be modified for a non-method signature.")
            }

            field = value
        }

    var argumentTypes: MutableList<Type> = if(type.sort != Type.METHOD) mutableListOf() else type.argumentTypes.toMutableList()

    override fun toString(): String {
        return desc
    }
}