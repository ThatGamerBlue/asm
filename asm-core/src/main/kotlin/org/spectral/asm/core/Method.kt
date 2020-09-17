package org.spectral.asm.core

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

class Method(val pool: ClassPool, val owner: Class) : MethodVisitor(ASM9), Node, Annotatable {

    override var access = 0

    override var name = ""

    lateinit var signature: Signature

    override val type: Type get() = Type.getMethodType(signature.desc)

    var exceptions = mutableListOf<ClassRef>()

    override var annotations = mutableListOf<Annotation>()

    override fun toString(): String {
        return "$owner.$name${signature.desc}"
    }
}