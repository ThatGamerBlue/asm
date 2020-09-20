package org.spectral.asm.core

import org.spectral.asm.core.code.Label

class LocalVariable(
        val method: Method,
        val name: String,
        val desc: String,
        val signature: String?,
        val start: Label,
        val end: Label,
        val index: Int
) {

    override fun toString(): String {
        return "VARIABLE"
    }
}