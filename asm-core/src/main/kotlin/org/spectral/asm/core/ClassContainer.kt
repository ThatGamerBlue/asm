package org.spectral.asm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ClassContainer private constructor(
        elements: ConcurrentHashMap<Type, Class>
) : ConcurrentHashMap<Type, Class>(elements), ClassPool {

    constructor() : this(ConcurrentHashMap())

    private val namedElements get() = this.values.associateBy { it.name }

    override fun addClass(element: Class) {
        this[element.type] = element
    }

    override fun addClassAndVisit(element: Class) {
        addClass(element)
        element.node.accept(element)
    }

    override fun addClass(bytes: ByteArray) {
        val reader = ClassReader(bytes)
        val node = ClassNode()
        reader.accept(node, 0)

        val element = Class(this, node)
        this.addClass(element)
    }

    override fun addClass(file: File) {
        Files.newInputStream(file.toPath()).use { reader ->
            this.addClass(reader.readAllBytes())
        }
    }

    override fun addArchive(file: File) {
        JarFile(file).use { jar ->
            jar.entries().asSequence()
                    .filter { it.name.endsWith(".class") }
                    .forEach {
                        val bytes = jar.getInputStream(it).readAllBytes()
                        this.addClass(bytes)
                    }
        }

        this.values.forEach { it.node.accept(it) }
    }

    override fun addDirectory(dir: File) {
        Files.walkFileTree(dir.toPath(), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if(file.toString().endsWith(".class")) {
                    addClass(file.toFile())
                }

                return FileVisitResult.CONTINUE
            }
        })

        this.values.forEach { it.node.accept(it) }
    }

    override fun saveArchive(file: File) {
        val jos = JarOutputStream(FileOutputStream(file))
        this.values.forEach {
            jos.putNextEntry(JarEntry(it.name + ".class"))

            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            it.accept(writer)

            jos.write(writer.toByteArray())
            jos.closeEntry()
        }

        jos.close()
    }

    override fun saveDirectory(dir: File) {
        TODO("Not yet implemented")
    }

    override fun get(name: String): Class? {
        return this.namedElements[name]
    }

    override fun getOrCreate(name: String): Class {
        return getOrCreate(Type.getObjectType(name))
    }

    override fun getOrCreate(type: Type): Class {
        var ret = this[type]
        if(ret == null) {
            ret = Class(this, type)
            addClassAndVisit(ret)
        }

        return ret
    }
}