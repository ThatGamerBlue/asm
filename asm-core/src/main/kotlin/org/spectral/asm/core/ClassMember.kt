package org.spectral.asm.core

import java.lang.reflect.Modifier

interface ClassMember : Node {

    val isStatic: Boolean get() = Modifier.isStatic(access)

    val isPrivate: Boolean get() = Modifier.isPrivate(access)

}