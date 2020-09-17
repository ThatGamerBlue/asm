package org.spectral.asm.core

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

/**
 * Represents a Java class object.
 * Extends the ASM [ClassVisitor]
 *
 * @property pool ClassPool
 * @constructor
 */
class Class(val pool: ClassPool) : ClassVisitor(ASM9), Node, Annotatable {

    var version: Int = 0

    override var access: Int = 0

    lateinit var source: String

    override lateinit var name: String

    override val type get() = Type.getObjectType(name)

    lateinit var superName: String

    var interfaces = mutableListOf<String>()

    var outerClass: String? = null

    var outerMethod: String? = null

    var outerMethodDesc: String? = null

    override var annotations = mutableListOf<Annotation>()
}