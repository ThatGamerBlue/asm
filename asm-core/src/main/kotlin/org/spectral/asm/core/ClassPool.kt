package org.spectral.asm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.spectral.asm.core.extractor.FeatureProcessor
import java.io.File
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
     * Inserts classes from a class path file. (JAR File)
     *
     * @param file File
     */
    fun insertFrom(file: File) {
       if(file.extension != "jar") {
           throw IllegalArgumentException("The specified file is not a JAR file.")
       }

        val nodes = hashSetOf<ClassNode>()

        JarFile(file).use { jar ->
            jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach {
                        val node = ClassNode()
                        val reader = ClassReader(jar.getInputStream(it))
                        reader.accept(node, 0)

                        nodes.add(node)
                    }
        }

        nodes.forEach { this.add(Class(this, it, Type.getObjectType(it.name),true)) }
    }
}