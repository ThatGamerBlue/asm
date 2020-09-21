package org.spectral.asm.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.spectral.asm.core.execution.Execution
import org.spectral.asm.core.util.JarUtil
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassTests {

    val pool = ClassPool()
    val testClass = JarUtil.loadClass(pool, ClassTests::class.java.getResourceAsStream("TestClass.class").readAllBytes())
    val messageClass = JarUtil.loadClass(pool, ClassTests::class.java.getResourceAsStream("Message.class").readAllBytes())

    init {
        pool.init()
    }

    @Test
    fun `read test classes`() {
        assertTrue { pool.size == 2 }
    }

    @Test
    fun `execution test`() {
        val method = pool["org/spectral/asm/core/TestClass"]!!.getMethod("run", "()V")!!
        val execution = Execution.executeMethod(method, listOf())
    }
}