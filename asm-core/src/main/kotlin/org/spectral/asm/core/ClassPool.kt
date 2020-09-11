package org.spectral.asm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarFile

class ClassPool {

    private val classMap = hashMapOf<String, Class>()

    val classes get() = classMap.values.toList()

    fun addClass(cls: Class) {
        if(!classMap.containsKey(cls.name)) {
            classMap[cls.name] = cls
        }
    }

    fun removeClass(cls: Class) {
        classMap.remove(cls.name)
    }

    operator fun get(name: String): Class? = classMap[name]

    companion object {

        fun loadJar(file: File): ClassPool {
            val pool = ClassPool()

            JarFile(file).use { jar ->
                jar.entries().asSequence()
                        .filter { it.name.endsWith(".class") }
                        .forEach {
                            val node = ClassNode()
                            val reader = ClassReader(jar.getInputStream(it))

                            reader.accept(node, ClassReader.SKIP_FRAMES)

                            val cls = Class(pool, node)
                            pool.addClass(cls)
                        }
            }

            return pool
        }
    }
}