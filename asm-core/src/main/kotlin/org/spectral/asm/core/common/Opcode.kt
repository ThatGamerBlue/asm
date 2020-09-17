package org.spectral.asm.core.common

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Opcode(val value: Int)