package org.spectral.asm.core.util

import org.objectweb.asm.ClassReader
import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool
import java.io.File
import java.io.FileNotFoundException
import java.util.jar.JarFile

object JarUtil {

    fun readJar(filePath: String): ClassPool {
        val pool = ClassPool()
        val file = File(filePath)

        if(!file.exists()) {
            throw FileNotFoundException("Jar file at path '$filePath' not found.")
        }

        JarFile(file).use { jar ->
            jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach {
                        val cls = Class(pool)
                        val reader = ClassReader(jar.getInputStream(it))

                        reader.accept(cls, ClassReader.SKIP_FRAMES)

                        pool.add(cls)
                    }
        }

        /*
         * Initialize the pool after all elements
         * have been added.
         */
        pool.init()

        return pool
    }
}