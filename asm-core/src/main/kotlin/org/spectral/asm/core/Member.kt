package org.spectral.asm.core

import org.objectweb.asm.Opcodes.*

interface Member : Node {

    val owner: Class

    val desc: Descriptor

    val isStatic: Boolean get() = (access and ACC_STATIC) != 0

    val isPrivate: Boolean get() = (access and ACC_PRIVATE) != 0

    val isFinal: Boolean get() = (access and ACC_FINAL) != 0


}