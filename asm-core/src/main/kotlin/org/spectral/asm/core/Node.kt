package org.spectral.asm.core

import org.objectweb.asm.Type

interface Node : Annotatable {

    val name: String

    val access: Int

    val type: Type

    fun initialize()
}