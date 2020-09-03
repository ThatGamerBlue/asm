package org.spectral.asm.core.extractor

import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.LocalVariable
import org.spectral.asm.core.Method
import org.spectral.asm.core.ext.slotSize

/**
 * Responsible for extracting features from the classes within the class pool.
 *
 * @property pool ClassPool
 * @constructor
 */
class FeatureProcessor(private val pool: ClassPool) {

    /**
     * Processes the class pool and runs each feature extractor.
     */
    fun process() {
        pool.forEach { cls ->
            extractSiblingHierarchy(cls)
        }

        pool.forEach { cls ->
            cls.methods.forEach { method ->
                extractMethodTypes(method)
                extractMethodArguments(method)
            }
        }

        pool.forEach { cls ->
            extractElementClass(cls)
        }
    }

    /**
     * Extracts the parent and interfaces from the given class.
     *
     * @param cls Class
     */
    private fun extractSiblingHierarchy(cls: Class) {
        if(cls.node.superName != null) {
            cls.parent = pool.getOrCreate(cls.node.superName)
            cls.parent?.children?.add(cls)
        }

        cls.interfaces.addAll(cls.node.interfaces.map { pool.getOrCreate(it) })
        cls.interfaces.forEach { it.implementers.add(cls) }
    }

    /**
     * Extracts the element class from array class types.
     *
     * @param cls Class
     */
    private fun extractElementClass(cls: Class) {
        if(cls.isArray) {
            cls.elementClass = pool.getOrCreate(cls.name.substring(0, cls.name.length - 2))
        }
    }

    /**
     * Extracts the return type class and argument classes for the given
     * method.
     *
     * @param method Method
     */
    private fun extractMethodTypes(method: Method) {
        method.returnClass = pool.getOrCreate(method.returnType.className)
    }

    /**
     * Extracts the arguments from the method and creates
     * [LocalVariable] objects for each of them.
     *
     * @param method Method
     */
    private fun extractMethodArguments(method: Method) {
        val argTypes = method.argumentTypes
        if(argTypes.isEmpty()) return

        val firstInsn = method.instructions.first
        var lvIndex = if(method.isStatic()) 0 else 1

        for(i in argTypes.indices){
            val type = argTypes[i]
            val typeClass = method.pool.getOrCreate(type.className.replace(".", "/"))
            var asmIndex = -1
            var startInsn = -1
            var endInsn = -1
            var name: String? = null

            if(method.locals.isNotEmpty()) {
                for(j in method.locals.indices) {
                    val n = method.locals[j]

                    if(n.index == lvIndex && n.start == firstInsn) {
                        asmIndex = j
                        startInsn = method.instructions.indexOf(n.start)
                        endInsn = method.instructions.indexOf(n.end)
                        name = n.name

                        break
                    }
                }
            }

            val arg = LocalVariable(method, true, i, lvIndex, asmIndex, typeClass, startInsn, endInsn, 0, name ?: "arg${i + 1}")
            method.arguments.add(i, arg)

            lvIndex += type.slotSize
        }
    }
}