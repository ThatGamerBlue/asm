package org.spectral.asm.simulator

import org.objectweb.asm.Type

class Unresolved(val type: Type) {

    val isArray: Boolean get() = type.sort == Type.ARRAY

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Unresolved

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return type.internalName
    }
}