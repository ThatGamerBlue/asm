package org.spectral.asm.core.code

import kotlin.reflect.KClass

enum class Instructions(val opcode: Int, val source: KClass<out Instruction>) {

}