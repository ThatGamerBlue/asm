package org.spectral.asm.core.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.common.NonLoadingClassWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

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

    fun writeJar(pool: ClassPool, filePath: String) {
        val file = File(filePath)

        if(file.exists()) {
            file.delete()
        }

        val jos = JarOutputStream(FileOutputStream(file))

        pool.forEach { cls ->
            jos.putNextEntry(JarEntry(cls.name + ".class"))

            val writer = NonLoadingClassWriter(pool, ClassWriter.COMPUTE_MAXS)
            cls.accept(writer)

            jos.write(writer.toByteArray())
            jos.closeEntry()
        }

        jos.close()
    }
}