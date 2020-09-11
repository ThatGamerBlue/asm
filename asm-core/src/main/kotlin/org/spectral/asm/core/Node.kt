package org.spectral.asm.core

import org.objectweb.asm.Type

interface Node {

    var name: String

    var access: Int

    val type: Type

}