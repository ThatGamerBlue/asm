package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.spectral.asm.core.code.*
import org.spectral.asm.core.code.type.FieldInstruction
import org.spectral.asm.core.reference.ClassRef
import org.spectral.asm.core.reference.FieldRef
import org.spectral.asm.core.util.InstructionUtil
import org.objectweb.asm.Label as AsmLabel

/**
 * Represents a Java Method.
 *
 * @property pool ClassPool
 * @property owner Class
 * @constructor
 */
class Method(val pool: ClassPool, val owner: Class) : MethodVisitor(ASM9), Node, ClassMember, Annotatable {

    override var access = 0

    override var name = ""

    lateinit var signature: Signature

    override val type: Type get() = Type.getMethodType(signature.desc)

    var exceptionClasses = mutableListOf<ClassRef>()

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
        exceptions?.map { ClassRef(it) }?.let { this.exceptionClasses.addAll(it) }
    }

    override var annotations = mutableListOf<Annotation>()

    /**
     * The code or instructions of the method.
     */
    var code = Code(this)

    var variables = mutableListOf<LocalVariable>()

    var arguments = mutableListOf<LocalVariable>()

    override fun init() {
        exceptionClasses.forEach { it.cls = pool[it.name] }
        arguments = extractArgs()

        /*
         * Update instruction refs.
         */
        code.instructions.forEach { insn ->
            when(insn) {
                is FieldInstruction -> {
                    val cls = pool[insn.field.owner.name]
                    insn.field.owner.cls = cls

                    val field = cls?.getField(insn.field.name, insn.field.desc)
                    insn.field.field = field
                }
            }
        }
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
        code.add(InstructionUtil.getInstruction(code, opcode))
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        code.add(InstructionUtil.getInstruction(code, opcode, operand))
    }

    override fun visitLdcInsn(value: Any) {
        code.add(InstructionUtil.getInstruction(code, LDC, value))
    }

    override fun visitVarInsn(opcode: Int, index: Int) {
        code.add(InstructionUtil.getInstruction(code, opcode, index))
    }

    override fun visitIincInsn(index: Int, increment: Int) {
        code.add(InstructionUtil.getInstruction(code, IINC, index, increment))
    }

    override fun visitJumpInsn(opcode: Int, label: AsmLabel) {
        code.add(InstructionUtil.getInstruction(code, opcode, findLabel(label)))
    }

    override fun visitLookupSwitchInsn(dflt: AsmLabel, keys: IntArray, labels: Array<out AsmLabel>) {
        code.add(InstructionUtil.getInstruction(code, LOOKUPSWITCH, findLabel(dflt), keys.toList(), labels.map { findLabel(it) }.toList()))
    }

    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: AsmLabel, vararg labels: AsmLabel) {
        code.add(InstructionUtil.getInstruction(code, TABLESWITCH, min, max, findLabel(dflt), labels.map { findLabel(it) }.toList()))
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        val fieldRef = FieldRef(ClassRef(owner), name, descriptor)
        code.add(InstructionUtil.getInstruction(code, opcode, fieldRef))
    }

    override fun visitTryCatchBlock(start: AsmLabel, end: AsmLabel, handler: AsmLabel?, type: String?) {
        code.exceptions.add(Exception(findLabel(start), findLabel(end), handler?.let { findLabel(it) }, type))
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        this.code.maxStack = maxStack
        this.code.maxLocals = maxLocals
    }

    override fun visitLocalVariable(
            name: String,
            descriptor: String,
            signature: String?,
            start: AsmLabel,
            end: AsmLabel,
            index: Int
    ) {
        val variable = LocalVariable(
                this,
                false,
                index,
                index,
                index,
                ClassRef(pool, Type.getType(descriptor).className),
                findLabel(start).offset,
                findLabel(end).offset,
                0,
                "var${index + 1}"
        )

        variables.add(variable)
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

    fun accept(visitor: MethodVisitor) {
        val annotationVisitor = visitor.visitAnnotationDefault()
        annotationVisitor?.visitEnd()

        annotations.forEach { annotation ->
            annotation.accept(visitor.visitAnnotation(annotation.type.descriptor, true))
        }

        if(code.size > 0) {
            visitor.visitCode()

            code.exceptions.forEach { exception ->
                exception.accept(visitor)
            }

            code.accept(visitor)

            if(variables.isNotEmpty()) {
                variables.forEach { variable ->
                    variable.accept(visitor)
                }
            }

            visitor.visitMaxs(code.maxStack, code.maxLocals)
        }

        visitor.visitEnd()
    }

    /**
     * Extracts the arguments from the method's instructions by popping from the local variables.
     */
    private fun extractArgs(): MutableList<LocalVariable> {
        val arguments = mutableListOf<LocalVariable>()

        val argTypes = type.argumentTypes
        if(argTypes.isEmpty()) return arguments
        if(code.instructions.isEmpty()) return arguments

        val locals = variables
        val firstInsn = code.instructions.first()

        var lvIdx = if(this.isStatic) 0 else 1

        for(i in argTypes.indices) {
            val asmType = argTypes[i]
            val ref = ClassRef(pool, asmType.className)

            var asmIndex = -1
            var startIndex = -1
            var endIndex = -1
            var name: String? = null

            if(locals.isEmpty()) {
                for(j in 0 until locals.size) {
                    val n = locals[i]

                    if(n.index == lvIdx && n.startInsn == firstInsn.offset) {
                        asmIndex = j
                        startIndex = n.startInsn
                        endIndex = n.endInsn
                        name = n.name

                        break
                    }
                }
            }

            val arg = LocalVariable(this, true, i, lvIdx, asmIndex, ref, startIndex, endIndex, 0, name ?: "arg${i + 1}")
            arguments.add(arg)

            lvIdx += asmType.size
        }

        return arguments
    }

    override fun toString(): String {
        return "$owner.$name${signature.desc}"
    }
}