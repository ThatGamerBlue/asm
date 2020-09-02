package org.spectral.asm.core.ext

import org.objectweb.asm.Type

val Type.slotSize: Int get() {
    val name = this.internalName
    val start = name[0]

    return if(start == 'D' || start == 'J') 2 else 1
}