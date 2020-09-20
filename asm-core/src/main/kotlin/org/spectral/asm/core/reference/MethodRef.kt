package org.spectral.asm.core.reference

import org.spectral.asm.core.Method

class MethodRef(val owner: ClassRef, val name: String, val desc: String, val toInterface: Boolean) {

    var method: Method? = null

    override fun toString(): String {
        return "${owner.name}.$name$desc"
    }
}