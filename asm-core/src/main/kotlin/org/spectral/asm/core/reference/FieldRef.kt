package org.spectral.asm.core.reference

import org.spectral.asm.core.Field

class FieldRef(val owner: ClassRef, val name: String, val desc: String) {

    var field: Field? = null

    override fun toString(): String {
        return "${owner.name}.$name.$desc"
    }
}