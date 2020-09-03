package org.spectral.asm.core

import org.objectweb.asm.Opcodes.ACC_INTERFACE
import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.spectral.asm.core.ext.isPrimitive
import java.util.*

/**
 * Represents an ASM ClassNode class.
 *
 * @property pool
 * @property node
 * @property real
 * @constructor Create empty Class
 */
class Class(override val pool: ClassPool, override val node: ClassNode, override var type: Type, override val real: Boolean) : Node {

    /**
     * Creates a virtual or fake class
     *
     * @param pool ClassPool
     * @param name String
     * @constructor
     */
    constructor(pool: ClassPool, name: String) : this(pool, createVirtualClassNode(name), Type.getObjectType(name), false)

    /**
     * Creates a virtual or fake class of a primitive type.
     *
     * @param pool ClassPool
     * @param type Type
     * @constructor
     */
    constructor(pool: ClassPool, type: Type) : this(pool, createVirtualClassNode(type.className), type, false)

    /**
     * The name of the class element.
     */
    override val name = node.name

    /**
     * The modifier flags of the class.
     */
    override val access = node.access

    /**
     * Whether the class is an array class or not.
     */
    val isArray: Boolean = (type.sort == 9)

    /**
     * The element class type of the object is an array class.
     */
    var elementClass: Class? = null

    /**
     * The super class of this object.
     */
    var parent: Class? = null

    /**
     * The classes which extend this object.
     */
    val children = hashSetOf<Class>()

    /**
     * The interface classes of this object.
     */
    val interfaces = hashSetOf<Class>()

    /**
     * The classes which implement this object.
     */
    val implementers = hashSetOf<Class>()

    /**
     * The methods in this class object.
     */
    val methods = node.methods.map { Method(pool, this, it, true) }

    /**
     * The fields in this class object.
     */
    val fields = node.fields.map { Field(pool, this, it, true) }

    /**
     * Gets whether the current class is an interface class or not.
     *
     * @return Boolean
     */
    fun isInterface(): Boolean = (access and ACC_INTERFACE) != 0

    /**
     * Checks whether a given class is assignable from another class.
     *
     * @param other Class
     * @return Boolean
     */
    fun isAssignableFrom(other: Class): Boolean {
        if(other == this) return true
        if(type.isPrimitive()) return false

        if (!isInterface()) {
            var sc: Class? = other
            while (sc?.parent.also { sc = it } != null) {
                if (sc == this) return true
            }
        } else {
            if(implementers.isEmpty()) return false
            if(implementers.contains(other)) return true

            var sc: Class? = other
            while(sc?.parent.also { sc = it } != null) {
                if(implementers.contains(sc)) return true
            }

            sc = other
            val toCheck = ArrayDeque<Class>()

            do {
                sc!!.interfaces.forEach { i ->
                    if(i.interfaces.isEmpty()) return@forEach
                    if(implementers.contains(i)) return true
                    toCheck.addAll(i.interfaces)
                }
            } while(sc?.parent.also { sc = it } != null)

            while(sc.also { sc = toCheck.poll() } != null) {
                sc!!.interfaces.forEach { i ->
                    if(implementers.contains(i)) return true
                    toCheck.addAll(i.interfaces)
                }
            }
        }

        return false
    }

    /**
     * Gets common hierarchy parents from another class.
     * Returns null if the given other class is not a relative of the current class.
     *
     * @param other Class
     * @return Class?
     */
    fun getCommonSuperClass(other: Class): Class? {
        if(other == this) return this
        if(type.isPrimitive() || other.type.isPrimitive()) return null
        if(isAssignableFrom(other)) return this
        if(other.isAssignableFrom(this)) return other

        val objCls = pool.getOrCreate("java/lang/Object")

        if(!isInterface() && !other.isInterface()) {
            var sc: Class? = this
            while (sc?.parent.also { sc = it } != null && sc != objCls) {
                if (sc!!.isAssignableFrom(other)) return sc
            }
        }

        if(interfaces.isNotEmpty() || other.interfaces.isNotEmpty()) {
            val ret = mutableListOf<Class>()
            val toCheck = ArrayDeque<Class>()
            val checked = hashSetOf<Class>()
            toCheck.addAll(interfaces)
            toCheck.addAll(other.interfaces)

            var cls: Class
            while(toCheck.poll().also { cls = it } != null) {
                if(!checked.add(cls)) continue
                if(cls.isAssignableFrom(other)) {
                    ret.add(cls)
                } else {
                    toCheck.addAll(cls.interfaces)
                }
            }

            if(ret.isNotEmpty()) {
                if(ret.size >= 1) {
                    val it = ret.iterator()
                    while(it.hasNext()) {
                        cls = it.next()

                        for(cls2 in ret) {
                            if(cls != cls2 && cls.isAssignableFrom(cls2)) {
                                it.remove()
                                break
                            }
                        }
                    }
                }

                return ret[0]
            }
        }

        return objCls
    }

    override fun toString(): String {
        return name
    }

    companion object {
        /**
         * Create a virtual or fake [ClassNode] object.
         *
         * @param name String
         * @return ClassNode
         */
        private fun createVirtualClassNode(name: String): ClassNode {
            return ClassNode(ASM8).apply {
                this.name = name.replace(".", "/")
                this.superName = "java/lang/Object"
            }
        }
    }
}