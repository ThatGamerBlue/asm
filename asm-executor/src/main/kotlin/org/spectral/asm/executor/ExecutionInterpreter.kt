package org.spectral.asm.executor

import org.objectweb.asm.ConstantDynamic
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.objectweb.asm.tree.analysis.AnalyzerException
import org.objectweb.asm.tree.analysis.Interpreter

class ExecutionInterpreter : Interpreter<StackValue>(ASM8) {

    internal lateinit var analyzer: MethodExecutor

    /**
     * Creates a new value on the stack or LVT.
     */
    private fun newValue(insn: AbstractInsnNode, type: Type?): StackValue? {
        return when {
            type == null -> {
                StackValue.pushUninitialized(insn)
            }
            type == Type.VOID_TYPE -> {
                null
            }
            type.sort <= Type.DOUBLE -> {
                StackValue(insn, type, null)
            }
            else -> StackValue(insn, type, null)
        }
    }

    override fun newValue(type: Type?): StackValue {
        throw UnsupportedOperationException("Interpreter called default implementation of 'newValue()'.")
    }

    override fun newOperation(insn: AbstractInsnNode): StackValue? {
        return when(insn.opcode) {
            ACONST_NULL -> newValue(insn, Type.getObjectType("null"))
            in ICONST_M1..ICONST_5 -> StackValue.pushInt(insn,  insn.opcode - (ICONST_M1 + 1))
            in LCONST_0..LCONST_1 -> StackValue.pushLong(insn, (insn.opcode - LCONST_0).toLong())
            in FCONST_0..FCONST_2 -> StackValue.pushFloat(insn, (insn.opcode - FCONST_0).toFloat())
            in DCONST_0..DCONST_1 -> StackValue.pushDouble(insn, (insn.opcode - DCONST_0).toDouble())
            BIPUSH, SIPUSH -> StackValue.pushInt(insn, cast<IntInsnNode>(insn).operand)
            LDC -> {
                val value = cast<LdcInsnNode>(insn).cst
                when(value) {
                    is Int -> StackValue.pushInt(insn, value)
                    is Float -> StackValue.pushFloat(insn, value)
                    is Long -> StackValue.pushLong(insn, value)
                    is Double -> StackValue.pushDouble(insn, value)
                    is String -> StackValue.pushString(insn, value)
                    is Type -> {
                        val type = value as Type
                        val sort = type.sort

                        if(sort == Type.OBJECT || sort == Type.ARRAY) {
                            StackValue.pushClass(insn, type)
                        }
                        else if(sort == Type.METHOD) {
                            newValue(insn, Type.getObjectType("java/lang/invoke/MethodType"))
                        }
                        else {
                            throw AnalyzerException(insn, "Illegal LDC value $value")
                        }
                    }
                    is Handle -> newValue(insn, Type.getObjectType("java/lang/invoke/MethodHandle"))
                    is ConstantDynamic -> newValue(insn, Type.getType(value.descriptor))
                    else -> throw AnalyzerException(insn, "Illegal LDC value $value")
                }
            }
            JSR -> StackValue.pushReturn(insn)
            GETSTATIC -> newValue(insn, Type.getType(cast<FieldInsnNode>(insn).desc))
            NEW -> newValue(insn, Type.getObjectType(cast<TypeInsnNode>(insn).desc))
            else -> throw AnalyzerException(insn, "Unknown new operation instruction opcode.")
        }
    }

    override fun copyOperation(insn: AbstractInsnNode?, value: StackValue?): StackValue {
        TODO("Not yet implemented")
    }

    override fun naryOperation(insn: AbstractInsnNode?, values: MutableList<out StackValue>?): StackValue {
        TODO("Not yet implemented")
    }

    override fun binaryOperation(insn: AbstractInsnNode?, value1: StackValue?, value2: StackValue?): StackValue {
        TODO("Not yet implemented")
    }

    override fun ternaryOperation(insn: AbstractInsnNode?, value1: StackValue?, value2: StackValue?, value3: StackValue?): StackValue {
        TODO("Not yet implemented")
    }

    override fun unaryOperation(insn: AbstractInsnNode?, value: StackValue?): StackValue {
        TODO("Not yet implemented")
    }

    override fun returnOperation(insn: AbstractInsnNode?, value: StackValue?, expected: StackValue?) {
        TODO("Not yet implemented")
    }

    override fun merge(value1: StackValue?, value2: StackValue?): StackValue {
        TODO("Not yet implemented")
    }

    private inline fun <reified T: AbstractInsnNode> cast(insn: AbstractInsnNode): T {
        return insn as T
    }
}