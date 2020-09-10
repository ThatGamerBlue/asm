package org.spectral.asm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarFile

class ClassPool {

    private val classList = mutableListOf<Class>()
    private val classMap = hashMapOf<String, Class>()

    val classes: List<Class> get() = classList

    fun addClass(element: Class) {
        if(!classList.contains(element)) {
            classList.add(element)
            classMap[element.name] = element
        }
    }

    fun removeClass(element: Class) {
        classList.remove(element)
        classMap.remove(element.name)
    }

    fun findClass(name: String): Class? {
        return classMap[name]
    }

    fun initialize() {
        classes.forEach { it.initialize() }
    }

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

            pool.initialize()

            return pool
        }
    }
}