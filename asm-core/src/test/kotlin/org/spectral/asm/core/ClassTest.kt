package org.spectral.asm.core

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.tree.ClassNode
import kotlin.test.assertTrue

class ClassTest {

    val pool = ClassPool.create()

    @BeforeEach
    fun before() {
        val classA = Class(pool, "name")
        val classB = Class(pool, "java/lang/Object")

        pool.addClass(classA)
        pool.addClass(classB)
    }

    @Test
    fun `parent class test`() {
        pool.values.forEach { it.accept(it.node) }
        assertTrue { pool["name"]!!.parent == pool["java/lang/Object"]!! }
    }

    @Test
    fun `pool visitor patter test`() {
        pool.values.forEach { it.accept(it.node) }
        val res = pool["name"]!!

        assertTrue { res.name == "name" }
    }

    @Test
    fun `interface tests`() {
        val implNode = ClassNode(ASM8).apply {
            this.name = "implClass"
            this.superName = "java/lang/Object"
            this.interfaces = mutableListOf("interf")
        }
        val impl = Class(pool, implNode)

        val interf = Class(pool, "interf")

        pool.addClass(impl)
        pool.addClass(interf)

        pool.values.forEach { it.accept(it.node) }

        assertTrue { pool["interf"] in pool["implClass"]!!.interfaces }
        assertTrue { pool["implClass"] in pool["interf"]!!.implementers }
    }
}