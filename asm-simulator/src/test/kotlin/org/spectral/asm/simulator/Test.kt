package org.spectral.asm.simulator

import org.spectral.asm.core.ClassPool
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

object Test : Spek({
    describe("fdsklfd") {
        val pool = ClassPool()
        pool.insertFrom(File("C:\\Users\\Kyle\\Projects\\Spectral\\workspaces\\spectral-powered\\asm\\gamepack-deob.jar"))
        pool.init()

        val clientClass = pool["client"]!!
        val initMethod = clientClass.methods.first { it.name == "init" }

        Simulator.simulateMethod(initMethod)
        println()
    }
})