package org.spectral.asm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.spectral.asm.core.extractor.FeatureProcessor
import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.jar.JarFile

/**
 * Represents a collection of [Class] objects from
 * a common classpath source.
 */
class ClassPool : AbstractPool<Class>() {

    /**
     * The feature processor instance.
     */
    private val featureProcessor = FeatureProcessor(this)

    /**
     * Initialize the pool for processing.
     */
    fun init() {
        this.forEach { cls ->
            featureProcessor.process(cls)
        }
    }

    /**
     * Gets a [Class] from the pool with a given name,
     * if none exists, a virtual class is created.
     *
     * @param name String
     * @return Class
     */
    fun getOrCreate(name: String): Class {
        if(name.isEmpty()) {
            return getOrCreate("java/lang/Object")
        }

        val found = this[name]

        if(found == null) {
            val virtualClass = Class(this, name)
            this.add(virtualClass)

            featureProcessor.process(virtualClass)

            return virtualClass
        }

        return found
    }

    /**
     * Gets or creates a virtual class of a given primitive type.
     *
     * @param type Type
     * @return Class
     */
    fun getOrCreate(type: Type): Class {
        val found = this[type.className]

        if(found == null) {
            val virtualClass = Class(this, type)
            this.add(virtualClass)

            featureProcessor.process(virtualClass)

            return virtualClass
        }

        return found
    }

    /**
     * Adds class from a file directory.
     *
     * @param dir File
     */
    fun addDirectory(dir: File) {
       if(!dir.exists()) return
        Files.walkFileTree(Paths.get(dir.absolutePath), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if(file.toString().endsWith(".class")) {
                    addClass(file!!.toFile())
                } else if(file.toString().endsWith(".jar") || file.toString().endsWith(".jmod")) {
                    addArchive(file!!.toFile())
                }

                return FileVisitResult.CONTINUE
            }
        })
    }

    /**
     * Adds classes from a given archive JAR file.
     *
     * @param archive File
     */
    fun addArchive(archive: File) {
        JarFile(archive).use { jar ->
            jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach {
                        val bytes = jar.getInputStream(it).readAllBytes()
                        addClass(bytes)
                    }
        }
    }

    /**
     * Adds a class from a given [File]
     *
     * @param classFile File
     */
    fun addClass(classFile: File) {
        addClass(Files.readAllBytes(classFile.toPath()))
    }

    /**
     * Reads and adds a class's bytecode as a [Class] object to the
     * current pool
     *
     * @param bytes ByteArray
     */
    fun addClass(bytes: ByteArray) {
        val node = ClassNode()
        val reader = ClassReader(bytes)
        reader.accept(node, 0)
        this.add(Class(this, node, Type.getObjectType(node.name), true))
    }
}