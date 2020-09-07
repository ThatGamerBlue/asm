package org.spectral.asm.core

interface ClassMember<T : Node<T>> : Node<T> {

    val name: String

    val desc: String

    val access: Int

    val isStatic: Boolean

    val isPrivate: Boolean
}