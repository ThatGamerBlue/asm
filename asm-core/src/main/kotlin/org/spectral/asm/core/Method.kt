package org.spectral.asm.core

import org.objectweb.asm.Type
import org.objectweb.asm.tree.LocalVariableNode
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Modifier

/**
 * Represents an ASM MethodNode object
 *
 * @property pool ClassPool
 * @property owner Class
 * @property node MethodNode
 * @constructor
 */
class Method(override val pool: ClassPool, override val owner: Class, override val node: MethodNode) : ClassMember {

    override val name = node.name

    override val desc = node.desc

    override val access = node.access

    override val type = Type.getMethodType(desc)

    /**
     * The return ASM [Type] of this method.
     */
    val returnType = type.returnType

    /**
     * A list of ASM [Type]s of the arguments in this method.
     */
    val argumentTypes = type.argumentTypes.toList()

    /**
     * The method ASM instruction list of this method.
     */
    val instructions = node.instructions

    /**
     * The local variables defined in this method object.
     */
    val locals: List<LocalVariableNode> = node.localVariables

    /**
     * The maximum size of the stack for this methods.
     */
    val maxStack = node.maxStack

    /**
     * The maximum number of local variables in this method.
     */
    val maxLocals = node.maxLocals

    override fun isStatic(): Boolean = Modifier.isStatic(access)

    override fun isPrivate(): Boolean = Modifier.isPrivate(access)

    /**
     * Gets whether this object is a constructor method.
     *
     * @return Boolean
     */
    fun isConstructor(): Boolean = name == "<init>"

    /**
     * Gets whether this object is an initializer method.
     *
     * @return Boolean
     */
    fun isInitializer(): Boolean = name == "<clinit>"

    override fun toString(): String {
        return "$owner.$name$desc"
    }
}