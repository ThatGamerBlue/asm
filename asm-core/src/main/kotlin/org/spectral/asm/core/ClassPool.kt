package org.spectral.asm.core

import org.objectweb.asm.Type
import java.io.File
import java.util.concurrent.ConcurrentMap

interface ClassPool : ConcurrentMap<Type, Class> {

    fun addClass(element: Class)

    fun addClassAndVisit(element: Class)

    fun addClass(bytes: ByteArray)

    fun addClass(file: File)

    fun addArchive(file: File)

    fun addDirectory(dir: File)

    fun saveArchive(file: File)

    fun saveDirectory(dir: File)

    operator fun get(name: String): Class?

    fun getOrCreate(name: String): Class

    fun getOrCreate(type: Type): Class

    companion object {
        fun create(): ClassPool {
            return ClassContainer()
        }
    }
}