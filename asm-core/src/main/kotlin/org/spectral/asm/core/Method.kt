package org.spectral.asm.core

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.spectral.asm.core.code.*
import org.spectral.asm.core.code.type.FieldInstruction
import org.spectral.asm.core.code.type.InvokeInstruction
import org.spectral.asm.core.reference.ClassRef
import org.spectral.asm.core.reference.FieldRef
import org.spectral.asm.core.reference.MethodRef
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

    var arguments = mutableListOf<Argument>()

    override fun init() {
        exceptionClasses.forEach { it.cls = pool[it.name] }

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

                is InvokeInstruction -> {
                    val cls = pool[insn.method.owner.name]
                    insn.method.owner.cls = cls

                    val method = cls?.getMethod(insn.method.name, insn.method.desc)
                    insn.method.method = method
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

    override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
        val methodRef = MethodRef(ClassRef(owner), name, descriptor, isInterface)
        code.add(InstructionUtil.getInstruction(code, opcode, methodRef))
    }

    override fun visitTypeInsn(opcode: Int, type: String) {
        code.add(InstructionUtil.getInstruction(code, opcode, Type.getObjectType(type)))
    }

    override fun visitMultiANewArrayInsn(descriptor: String, numDimensions: Int) {
        code.add(InstructionUtil.getInstruction(code, MULTIANEWARRAY, Type.getType(descriptor), numDimensions))
    }

    override fun visitTryCatchBlock(start: AsmLabel, end: AsmLabel, handler: AsmLabel?, type: String?) {
        code.exceptions.add(Exception(findLabel(start), findLabel(end), handler?.let { findLabel(it) }, type))
    }

    override fun visitParameter(name: String?, access: Int) {
        val arg = Argument(name, access)
        arguments.add(arg)
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
        val lv = LocalVariable(this, name, descriptor, signature, findLabel(start), findLabel(end), index)

        arguments.forEach { arg ->
            if(arg.name == name) {
                arg.variable = lv
            }
        }
    }

    override fun visitEnd() {
        /*
         * Nothing to do.
         */
    }

    private fun findLabel(label: AsmLabel): Label {
        if(!code.labelMap.containsKey(label)) {
            code.labelMap[label] = Label(label)
        }

        return code.labelMap[label]!!
    }

    fun accept(visitor: MethodVisitor) {
        if(annotations.isNotEmpty()) {
            annotations.forEach { annotation ->
                annotation.accept(visitor.visitAnnotation(annotation.type.descriptor, true))
            }
        }

        if(code.size > 0) {
            code.resetLabels()

            visitor.visitCode()

            if(code.exceptions.isNotEmpty()) {

                code.exceptions.forEach { exception ->
                    /*
                     * Rebuild the exception handler labels.
                     */
                    visitor.visitTryCatchBlock(
                            findLabel(exception.start.label).label,
                            findLabel(exception.end.label).label,
                            if(exception.handler != null) findLabel(exception.handler.label).label else null,
                            exception.catchType
                    )
                }
            }

            code.accept(visitor)

            if(arguments.isNotEmpty()) {
                var start: Label? = null
                var end: Label? = null

                code.instructions.forEach {
                    if(it is Label) {
                        if(start == null) {
                            start = it
                        }

                        end = it
                    }
                }

                arguments.forEach { arg ->
                    if(arg.variable == null) return@forEach
                    val lv = arg.variable!!
                    visitor.visitLocalVariable(lv.name, lv.desc, lv.signature, start!!.label, end!!.label, lv.index)
                }
            }

            visitor.visitMaxs(code.maxStack, code.maxLocals)
        }

        visitor.visitEnd()
    }

    override fun toString(): String {
        return "$owner.$name${signature.desc}"
    }
}