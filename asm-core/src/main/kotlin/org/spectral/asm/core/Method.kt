package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type
import org.spectral.asm.core.code.Code
import org.spectral.asm.core.code.Exception
import org.spectral.asm.core.code.Label
import org.spectral.asm.core.code.LineNumber
import org.objectweb.asm.Label as AsmLabel

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
        exceptions?.map { ClassName(it) }?.let { this.exceptionClasses.addAll(it) }
    }

    override var annotations = mutableListOf<Annotation>()

    /**
     * The code or instructions of the method.
     */
    var code = Code(this)

    override fun init() {
        exceptionClasses.forEach { it.cls = pool[it.name] }
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

    /*
     * INSTRUCTION VISITOR METHODS
     */

    override fun visitLabel(label: AsmLabel) {
        code.add(findLabel(label))
    }

    override fun visitLineNumber(line: Int, start: AsmLabel) {
        code.add(LineNumber(line, findLabel(start)))
    }

    override fun visitInsn(opcode: Int) {
        //code.add(InstructionUtil.getInstruction(opcode))
    }

    override fun visitTryCatchBlock(start: AsmLabel, end: AsmLabel, handler: AsmLabel?, type: String?) {
        code.exceptions.add(Exception(findLabel(start), findLabel(end), handler?.let { findLabel(it) }, type))
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        this.code.maxStack = maxStack
        this.code.maxLocals = maxLocals
    }

    override fun visitEnd() {
        /*
         * Nothing to do.
         */
    }

    internal fun findLabel(label: AsmLabel): Label {
        if(label.info !is Label) {
            label.info = Label()
        }

        return label.info as Label
    }

    override fun toString(): String {
        return "$owner.$name${signature.desc}"
    }
}