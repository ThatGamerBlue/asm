package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

/**
 * Represents a Java field.
 *
 * @property pool ClassPool
 * @property owner Class
 * @constructor
 */
class Field(val pool: ClassPool, val owner: Class) : FieldVisitor(ASM9), Node, Annotatable {

    override var access = 0

    override var name = ""

    lateinit var signature: Signature

    override val type get() = Type.getType(signature.desc)

    var value: Any? = null

    /**
     * Creates an initializes the field values.
     *
     * @param pool ClassPool
     * @param owner Class
     * @param access Int
     * @param name String
     * @param desc String
     * @param value Any?
     * @constructor
     */
    constructor(
            pool: ClassPool,
            owner: Class,
            access: Int,
            name: String,
            desc: String,
            value: Any?
    ) : this(pool, owner) {
        this.access = access
        this.name = name
        this.signature = Signature(Type.getType(desc))
        this.value = value
    }

    override val annotations = mutableListOf<Annotation>()

    override fun init() {

    }

    /*
     * VISITOR METHODS
     */

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
         * Nothing to Do
         */
    }

    override fun toString(): String {
        return "$owner.$name"
    }
}