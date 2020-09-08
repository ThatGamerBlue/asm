package org.spectral.asm.executor

import org.spectral.asm.core.ClassPool
import java.io.File

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = ClassPool.create()
        pool.addArchive(File("gamepack.jar"))

        val initMethod = pool["client"]!!.methods.first { it.name == "init" }

        val executor = MethodExecutor(initMethod)
        executor.run()

        println()
    }
}