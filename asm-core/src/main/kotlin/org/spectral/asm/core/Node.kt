package org.spectral.asm.core

import org.objectweb.asm.Type

interface Node {

    var access: Int

    var name: String

    val type: Type

}