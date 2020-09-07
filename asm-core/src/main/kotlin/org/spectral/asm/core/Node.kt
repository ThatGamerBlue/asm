package org.spectral.asm.core

import org.objectweb.asm.Type

interface Node<T> {

    val node: Any

    val type: Type

    val real: Boolean
}