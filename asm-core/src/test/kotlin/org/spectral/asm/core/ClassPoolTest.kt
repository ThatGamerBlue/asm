package org.spectral.asm.core

import com.natpryce.hamkrest.assertion.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.objectweb.asm.Type
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassPoolTest {

    val pool = ClassPool.create()

    val element = mockk<Class> {
        every { type } answers { Type.getObjectType("someClass") }
        every { name } answers { "toDelete" }
    }

    @Test
    fun `add class element`() {
        assertTrue { pool.size == 0 }
        pool.addClass(element)
        assertTrue { pool.size == 1 }
    }

    @Test
    fun `get class element`() {
        pool.addClass(Class(pool, "fake"))
        val fakeElement = pool[Type.getObjectType("fake")]

        assertTrue { fakeElement?.type == Type.getObjectType("fake") }
    }

    @Test
    fun `remove class element`() {
        pool.addClass(Class(pool, "toDelete"))
        val toDelete = pool["toDelete"]

        assertTrue { toDelete?.name == "toDelete" }

        pool.remove(toDelete?.type)

        assertFalse { pool.containsKey(toDelete?.type) }
    }
}