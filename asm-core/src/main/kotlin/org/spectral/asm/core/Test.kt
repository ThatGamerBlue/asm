package org.spectral.asm.core

import org.spectral.asm.core.util.JarUtil

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = JarUtil.readJar("gamepack.jar")
        println()
    }
}