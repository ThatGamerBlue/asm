package org.spectral.asm.core

import org.objectweb.asm.Opcodes.ASM8
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
 * @property real Whether this method is of a real type.
 * @constructor
 */
class Method(
        override val pool: ClassPool,
        override val owner: Class,
        override val node: MethodNode,
        override val real: Boolean
) : ClassMember {

    /**
     * Creates a fake or virtual method
     *
     * @param pool ClassPool
     * @param owner Class
     * @param name String
     * @param desc String
     * @constructor
     */
    constructor(pool: ClassPool, owner: Class, name: String, desc: String)
            : this(pool, owner, createVirtualMethodNode(name, desc), false)

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
     * The return type class object.
     */
    var returnClass: Class? = null

    /**
     * The method ASM instruction list of this method.
     */
    val instructions = node.instructions

    /**
     * The local variables defined in this method object.
     */
    val locals: List<LocalVariableNode> = node.localVariables ?: mutableListOf()

    /**
     * The maximum size of the stack for this methods.
     */
    val maxStack = node.maxStack

    /**
     * The maximum number of local variables in this method.
     */
    val maxLocals = node.maxLocals

    /**
     * A list of the argument local variable objects of this method.
     */
    val arguments = mutableListOf<LocalVariable>()

    /**
     * A list of the local variables objects of this method.
     */
    val variables = mutableListOf<LocalVariable>()

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

    companion object {
        /**
         * Creates a blank or virtual method node.
         *
         * @param name String
         * @param desc String
         * @return MethodNode
         */
        private fun createVirtualMethodNode(name: String, desc: String): MethodNode {
            return MethodNode(ASM8).apply {
                this.name = name
                this.desc = desc
            }
        }
    }
}