package org.spectral.asm.core

import org.objectweb.asm.Type
import java.io.File

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = ClassPool.loadJar(File("gamepack.jar"))
        val method = pool["client"]!!.methods.random()

        println("Initial: $method")

        method.argumentsTypes.add(Type.getType("Ljava/lang/String;"))

        println("After: $method")

        println()
    }
}