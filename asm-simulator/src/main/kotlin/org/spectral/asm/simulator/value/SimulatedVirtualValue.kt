package org.spectral.asm.simulator.value

import org.checkerframework.checker.units.qual.min
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.spectral.asm.simulator.util.combine
import org.spectral.asm.simulator.util.distinct
import org.spectral.asm.simulator.util.isPrimitiveDesc
import java.util.stream.Collectors


/**
 * Value recording the type and value (reflection simulation value)
 */
class SimulatedVirtualValue : VirtualValue {

    private var currentValue: Array<Any?>

    /**
     * Primary constructor
     *
     * @param insns List<AbstractInsnNode>
     * @param type Type
     * @param value Any?
     * @param currentValue Array<Any?>
     * @constructor
     */
    constructor(insns: List<AbstractInsnNode>, type: Type, value: Any?, currentValue: Array<Any?>)
        : super(insns, type, copyValue(value)) {
        this.currentValue = currentValue
        this.currentValue[0] = value
    }

    /**
     * Secondary constructor.
     *
     * @param insns List<AbstractInsnNode>
     * @param type Type
     * @param value Any?
     * @constructor
     */
    constructor(insns: List<AbstractInsnNode>, type: Type, value: Any?)
        : this(insns, type, value, arrayOf(value))

    /**
     * Gets whether the simulated virtual value is resolved.
     */
    override val isValueResolved get() = value != null

    override fun copy(insn: AbstractInsnNode): AbstractValue {
        return SimulatedVirtualValue(combine(insns, insn), type, value, currentValue)
    }

    companion object {
        /**
         * A collection of type procuder elements.
         */
        private val TYPE_PRODUCERS = hashMapOf<String, (List<AbstractInsnNode>) -> SimulatedVirtualValue>()

        /**
         * A collection of methods which are not allowed to be simulated.
         */
        private val BLACKLISTED_METHODS = setOf<Pair<String, String>>(
                "wait" to "()V",
                "wait" to "(J)V",
                "wait" to "(JI)V",
                "notify" to "()V",
                "notifyAll" to "()V",
                "intern" to "()Ljava/lang/String;"
        )

        /**
         * A collection of classes which are allowed to be simulated inside a virtual method ref
         */
        private val WHITELISTED_CLASS = setOf(
                "java/lang/Long",
                "java/lang/Integer",
                "java/lang/Short",
                "java/lang/Character",
                "java/lang/Byte",
                "java/lang/Boolean",
                "java/lang/Float",
                "java/lang/Double",
                "java/lang/Math"
        )

        init {
            /*
             * Insert the default type producers
             */
            TYPE_PRODUCERS["java/lang/StringBuilder"] =
                    { SimulatedVirtualValue(it, Type.getObjectType("java/lang/StringBuilder"), StringBuilder()) }

            TYPE_PRODUCERS["java/lang/StringBuffer"] =
                    { SimulatedVirtualValue(it, Type.getObjectType("java/lang/StringBuffer"), StringBuffer()) }

            TYPE_PRODUCERS["java/lang/String"] = { ofString(it, "") }
        }

        /**
         * Gets whether the given modifiers contains a static flag.
         *
         * @param modifiers Int
         * @return Boolean
         */
        private fun isStatic(modifiers: Int): Boolean {
            return (modifiers and ACC_STATIC) == ACC_STATIC
        }

        private fun isStaticMethodWhitelisted(owner: String, name: String, desc: String): Boolean {
            return WHITELISTED_CLASS.contains(owner)
        }

        /**
         * Gets whether a give type is supported as a producer.
         *
         * @param type Type
         * @return Boolean
         */
        fun supported(type: Type): Boolean {
            return TYPE_PRODUCERS.containsKey(type.internalName)
        }

        /**
         * Initializes a simulated reference.
         *
         * @param insns List<AbstractInsnNode>
         * @param type Type
         * @return SimulatedVirtualValue
         */
        fun initialize(insns: List<AbstractInsnNode>, type: Type): SimulatedVirtualValue {
            return TYPE_PRODUCERS[type.internalName]!!(insns)
        }

        fun ofString(insns: List<AbstractInsnNode>, value: String): SimulatedVirtualValue {
            return SimulatedVirtualValue(insns, Type.getObjectType("java/lang/String"), value)
        }

        fun ofString(insn: AbstractInsnNode, value: String): SimulatedVirtualValue {
            return ofString(listOf(insn), value)
        }

        fun ofStaticInvoke(insn: MethodInsnNode, arguments: List<AbstractValue>): AbstractValue? {
            if(!isStaticMethodWhitelisted(insn.owner, insn.name, insn.desc)) {
                throw IllegalStateException("Static method is not whitelisted.")
            }

            try {
                return invokeStatic(insn, insn.owner, insn.name, Type.getMethodType(insn.desc), arguments)
            } catch(e : Exception) {
                throw IllegalStateException("Failed to invoke method '${insn.owner}.${insn.name}${insn.desc}'")
            }
        }

        /**
         * Copies and returns an object value to prevent
         * concurrent modifications.
         *
         * @param value Any
         * @return Any
         */
        private fun copyValue(value: Any?): Any? {
            return when (value) {
                is String -> {
                    value.toString()
                }
                is StringBuilder -> {
                    StringBuilder(value.toString())
                }
                is StringBuffer -> {
                    StringBuffer(value.toString())
                }
                else -> throw UnsupportedOperationException("$value copying not supported!")
            }

        }

        /**
         * Get the contributing method calls.
         *
         * @param insns List<AbstractInsnNode>
         * @param retVal Any
         * @return AbstractValue?
         */
        private fun unboxed(insns: List<AbstractInsnNode>, retVal: Any): AbstractValue? {
            if (retVal is Int || retVal is Short || retVal is Byte)
                return PrimitiveValue.ofInt(insns, (retVal as Number).toInt())
            else if (retVal is Float) return PrimitiveValue.ofFloat(insns, retVal)
            else if (retVal is Double) return PrimitiveValue.ofDouble(insns, retVal)
            else if (retVal is Boolean) return PrimitiveValue.ofInt(insns, if (retVal) 1 else 0)
            else if (retVal is Char) return PrimitiveValue.ofChar(insns, retVal)
            else if (retVal is Long) return PrimitiveValue.ofLong(insns, retVal)

            throw UnsupportedOperationException("Unsupported boxed type: " + retVal.javaClass.name)
        }

        /**
         * Simulates an invoke static method instructions and returns the
         * stack value as an inlined instruction.
         *
         * @param insn MethodInsnNode
         * @param owner String
         * @param name String
         * @param desc Type
         * @param arguments List<out AbstractValue>
         * @return AbstractValue
         */
        private fun invokeStatic(
                insn: MethodInsnNode,
                owner: String,
                name: String,
                desc: Type,
                arguments: List<AbstractValue>
        ): AbstractValue? {
            val cls = Class.forName(owner.replace("/", "."))
            val retType = desc.returnType
            val argTypes = desc.argumentTypes

            for(m in cls.methods) {
                if(m.name != name) continue
                if(m.parameterCount != argTypes.size) continue
                if(!isStatic(m.modifiers)) continue

                var argsMatch = true
                for(i in argTypes.indices) {
                    argsMatch = argsMatch and (argTypes[i] == Type.getType(m.parameterTypes[i]))
                    if(argsMatch) {
                        val argValues = arguments.map { it.value }
                        m.isAccessible = true
                        val retValue = m.invoke(null, argValues)

                        if(retType.sort == Type.VOID) {
                            return null
                        }

                        if(retValue != null) {
                            val insns: List<AbstractInsnNode> = distinct(combine(arguments.stream()
                                    .flatMap { arg -> arg.insns.stream() }
                                    .collect(Collectors.toList()), insn))

                            return if(isPrimitiveDesc(retType.descriptor)) {
                                unboxed(insns, retValue)
                            } else {
                                SimulatedVirtualValue(insns, Type.getType(retValue::class.java), retValue)
                            }
                        }
                    }
                }
            }

            throw IllegalStateException("Could not find method to simulate '$owner.$name$desc'")
        }
    }
}