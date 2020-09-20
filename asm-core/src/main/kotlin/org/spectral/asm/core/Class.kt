package org.spectral.asm.core

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.ASM9
import org.spectral.asm.core.reference.ClassRef
import java.lang.reflect.Modifier

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

    var source: String = ""

    override var name: String = ""

    override val type get() = Type.getObjectType(name)

    lateinit var parent: ClassRef

    var interfaces = mutableListOf<ClassRef>()

    override var annotations = mutableListOf<Annotation>()

    var methods = mutableListOf<Method>()

    var fields = mutableListOf<Field>()

    val isInterface: Boolean get() = Modifier.isInterface(access)

    override fun init() {
        parent.cls = pool[parent.name]
        interfaces.forEach { it.cls = pool[it.name] }

        methods.forEach { it.init() }
        fields.forEach { it.init() }
    }

    fun getMethod(name: String, desc: String): Method? {
        return methods.firstOrNull { it.name == name && it.signature.desc == desc }
    }

    fun getField(name: String, desc: String): Field? {
        return fields.firstOrNull { it.name == name && it.signature.desc == desc }
    }

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
        this.parent = ClassRef(superName)
        this.interfaces = interfaces.map { ClassRef(it) }.toMutableList()
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

    override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
    ): MethodVisitor {
        val method = Method(pool, this, access, name, descriptor, exceptions)
        methods.add(method)

        return method
    }

    override fun visitField(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            value: Any?
    ): FieldVisitor {
        val field = Field(pool, this, access, name, descriptor, value)
        fields.add(field)
        return field
    }

    override fun visitEnd() {
        /*
         * Nothing to do
         */
    }

    fun accept(visitor: ClassVisitor) {
        val interfs = interfaces.map { it.name }.toTypedArray()

        visitor.visit(version, access, name, null, parent.name, interfs)
        visitor.visitSource(source, null)

        annotations.forEach { annotation ->
            annotation.accept(visitor.visitAnnotation(annotation.type.descriptor, true))
        }

        fields.forEach { field ->
            val fv = visitor.visitField(field.access, field.name, field.signature.desc, null, field.value)
            field.accept(fv)
        }

        methods.forEach { method ->
            val exceptions = method.exceptionClasses.map { it.name }.toTypedArray().let { if(it.isEmpty()) null else it }
            val mv = visitor.visitMethod(method.access, method.name, method.signature.desc, null, exceptions)
            method.accept(mv)
        }

        visitor.visitEnd()
    }

    override fun toString(): String {
        return name
    }
}