package org.spectral.asm.core

import java.io.File

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = ClassPool.loadJar(File("gamepack.jar"))
        println()
    }
}