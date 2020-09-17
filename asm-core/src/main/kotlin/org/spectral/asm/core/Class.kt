package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
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

    override var annotations = mutableListOf<Annotation>()

    /*
     * VISITOR METHODS
     */

    override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String,
            interfaces: Array<out String>
    ) {
        this.version = version
        this.access = access
        this.name = name
        this.superName = superName
        this.interfaces = interfaces.toMutableList()
    }

    override fun visitSource(source: String, debug: String?) {
        this.source = source
    }

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        if(visible) {
            val annotation = Annotation(Type.getType(descriptor))
            annotations.add(annotation)

            return annotation
        }

        return null
    }

    override fun visitEnd() {
        /*
         * Nothing to do
         */
    }

    override fun toString(): String {
        return name
    }
}