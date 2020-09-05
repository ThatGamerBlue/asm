package org.spectral.asm.core.extractor

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.spectral.asm.core.*
import org.spectral.asm.core.ext.slotSize
import org.spectral.asm.core.ext.targetHandle

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
    fun process(cls: Class) {
        /*
         * Reset the class state
         */
        reset(cls)

        extractSiblingHierarchy(cls)

        cls.methods.forEach { method ->
            extractMethodTypes(method)
            extractMethodArguments(method)
        }

        extractElementClass(cls)

        cls.methods.forEach { method ->
            processMethodInsns(method)
        }
    }

    private fun reset(cls: Class) {
        cls.elementClass = null
        cls.parent = null
        cls.children.clear()
        cls.interfaces.clear()
        cls.implementers.clear()
        cls.methods = cls.node.methods.map { Method(pool, cls, it, true) }
        cls.fields = cls.node.fields.map { Field(pool, cls, it, true) }
        cls.methodTypeRefs.clear()
        cls.fieldTypeRefs.clear()
    }

    /**
     * Extracts the parent and interfaces from the given class.
     *
     * @param cls Class
     */
    private fun extractSiblingHierarchy(cls: Class) {
        if(!cls.real) return

        if(cls.parent == null && cls.node.superName != null) {
            cls.parent = pool.getOrCreate(cls.node.superName)
            cls.parent?.children?.add(cls)
        }

        if(cls.interfaces.isEmpty()) {
            cls.interfaces.addAll(cls.node.interfaces.map { pool.getOrCreate(it) })
            cls.interfaces.forEach { it.implementers.add(cls) }
        }
    }

    /**
     * Extracts the element class from array class types.
     *
     * @param cls Class
     */
    private fun extractElementClass(cls: Class) {
        if(cls.isArray) {
            cls.elementClass = pool.getOrCreate(cls.type.elementType)
        }
    }

    /**
     * Extracts the return type class and argument classes for the given
     * method.
     *
     * @param method Method
     */
    private fun extractMethodTypes(method: Method) {
        method.returnClass = pool.getOrCreate(method.returnType)
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
            val typeClass = method.pool.getOrCreate(type)
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

    /**
     * Processes a method's instructions and builds a reference map
     * between classes, methods, and fields.
     *
     * @param method Method
     */
    private fun processMethodInsns(method: Method) {
        if(!method.real) return

        val it = method.instructions.iterator()

        while(it.hasNext()) {
            val insn = it.next()

            when(insn) {

                /*
                 * When the method instruction is a method invocation.
                 */
                is MethodInsnNode -> processMethodInvoke(
                        method,
                        insn.owner,
                        insn.name,
                        insn.desc,
                        insn.itf,
                        (insn.opcode == INVOKESTATIC)
                )

                /*
                 * When the method instruction is a field invocation.
                 */
                is FieldInsnNode -> {
                    val owner = pool.getOrCreate(insn.owner)
                    val dst = owner.resolveField(insn.name, insn.desc) ?: return

                    /*
                     * Determine if the field instruction is a read or write instruction
                     */
                    if(insn.opcode == GETSTATIC || insn.opcode == GETFIELD) {
                        dst.readRefs.add(method)
                        method.fieldReadRefs.add(dst)
                    } else {
                        dst.writeRefs.add(method)
                        method.fieldWriteRefs.add(dst)
                    }

                    dst.owner.methodTypeRefs.add(method)
                    method.classRefs.add(dst.owner)
                }

                /*
                 * When the method instruction is a type declaration
                 */
                is TypeInsnNode -> {
                    val dst = pool.getOrCreate(insn.desc)

                    dst.methodTypeRefs.add(method)
                    method.classRefs.add(dst)
                }

                /*
                 * When the method instruction is an invoke dynamic type.
                 */
                is InvokeDynamicInsnNode -> {
                    val handle = insn.targetHandle ?: return

                    when(handle.tag) {
                        H_INVOKEVIRTUAL, H_INVOKESTATIC, H_INVOKESPECIAL, H_NEWINVOKESPECIAL, H_INVOKEINTERFACE -> {
                            processMethodInvoke(
                                    method,
                                    handle.owner,
                                    handle.name,
                                    handle.desc,
                                    handle.isInterface,
                                    (handle.tag == H_INVOKESTATIC)
                            )
                            return
                        }
                    }
                }
            }
        }
    }

    /**
     * Processes a method invocation instruction.
     *
     * @param method Method
     * @param owner String
     * @param name String
     * @param desc String
     * @param toInterface Boolean
     * @param isStatic Boolean
     */
    private fun processMethodInvoke(
            method: Method,
            owner: String,
            name: String,
            desc: String,
            toInterface: Boolean,
            isStatic: Boolean
    ) {
        val cls = pool.getOrCreate(owner)
        val dst = cls.resolveMethod(name, desc, toInterface) ?: return

        dst.refsIn.add(method)
        method.refsOut.add(dst)
        dst.owner.methodTypeRefs.add(method)
        method.classRefs.add(dst.owner)
    }
}