package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type

/**
 * Represents a Java Method.
 *
 * @property pool ClassPool
 * @property owner Class
 * @constructor
 */
class Method(val pool: ClassPool, val owner: Class) : MethodVisitor(ASM9), Node, Annotatable {

    override var access = 0

    override var name = ""

    lateinit var signature: Signature

    override val type: Type get() = Type.getMethodType(signature.desc)

    var exceptionClasses = mutableListOf<ClassName>()

    /**
     * Creates a [Method] object and initializes required fields.
     *
     * @param pool ClassPool
     * @param owner Class
     * @param access Int
     * @param name String
     * @param desc String
     * @param exceptions Array<out String>?
     * @constructor
     */
    constructor(
            pool: ClassPool,
            owner: Class,
            access: Int,
            name: String,
            desc: String,
            exceptions: Array<out String>?
    ) : this(pool, owner) {
        this.access = access
        this.name = name
        this.signature = Signature(Type.getMethodType(desc))

        /*
         * Add the exceptions if any are provided. (Not null)
         */
        exceptions?.map { ClassName(pool, it) }?.let { this.exceptionClasses.addAll(it) }
    }

    override var annotations = mutableListOf<Annotation>()

    var maxStack = 0

    var maxLocals = 0

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

    override fun visitCode() {
        // TODO Implement code.
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        this.maxStack = maxStack
        this.maxLocals = maxLocals
    }

    override fun visitEnd() {
        /*
         * Nothing to do.
         */
    }

    override fun toString(): String {
        return "$owner.$name${signature.desc}"
    }
}