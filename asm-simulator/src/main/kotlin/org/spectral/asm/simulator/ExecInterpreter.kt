package org.spectral.asm.simulator

import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.TryCatchBlockNode
import org.objectweb.asm.tree.analysis.Frame
import org.objectweb.asm.tree.analysis.Interpreter
import org.spectral.asm.simulator.value.AbstractValue
import org.spectral.asm.simulator.value.PrimitiveValue
import org.spectral.asm.simulator.value.UninitializedValue
import org.spectral.asm.simulator.value.VirtualValue

/**
 * Responsible for interpreting bytecode of a method and calculating the
 * resulting values pushed to the stack.
 */
class ExecInterpreter : Interpreter<AbstractValue>(ASM8) {

    /**
     * Invoked when a new type value is pushed to the stack.
     *
     * @param insn AbstractInsnNode
     * @param type Type
     * @return AbstractValue
     */
    private fun newValue(insn: AbstractInsnNode, type: Type?): AbstractValue? {
        return when {
            type == null -> {
                UninitializedValue.UNINITIALIZED_VALUE
            }
            type == Type.VOID_TYPE -> {
                null
            }
            type.sort <= Type.DOUBLE -> {
                PrimitiveValue(insn, type, null)
            }
            else -> VirtualValue.ofVirtual(insn, type)
        }
    }

    /**
     * Invoked when a new type value is pushed to the stack.
     *
     * @param insns List<AbstractInsnNode>
     * @param type Type?
     * @return AbstractValue?
     */
    private fun newValue(insns: List<AbstractInsnNode>, type: Type?): AbstractValue? {
        return when {
            type == null -> UninitializedValue.UNINITIALIZED_VALUE
            type == Type.VOID_TYPE -> null
            type.sort <= Type.DOUBLE -> PrimitiveValue(insns, type, null)
            else -> VirtualValue.ofVirtual(insns, type)
        }
    }

    override fun newValue(type: Type): AbstractValue {
        throw UnsupportedOperationException("Interpreter called default implementation of 'newValue'.")
    }

    override fun newReturnTypeValue(type: Type?): AbstractValue? {
        return newValue(listOf(), type)
    }

    override fun newEmptyValue(local: Int): AbstractValue {
        return UninitializedValue.UNINITIALIZED_VALUE
    }

    override fun newParameterValue(isInstanceMethod: Boolean, local: Int, type: Type?): AbstractValue? {
        return newValue(listOf(), type)
    }

    override fun newExceptionValue(tryCatchBlockNode: TryCatchBlockNode,
                                   handlerFrame: Frame<AbstractValue>?,
                                   exceptionType: Type?
    ): AbstractValue? {
        return newValue(tryCatchBlockNode.handler, exceptionType)
    }

    override fun newOperation(insn: AbstractInsnNode?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun copyOperation(insn: AbstractInsnNode?, value: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun unaryOperation(insn: AbstractInsnNode?, value: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun binaryOperation(insn: AbstractInsnNode?, value1: AbstractValue?, value2: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun ternaryOperation(insn: AbstractInsnNode?, value1: AbstractValue?, value2: AbstractValue?, value3: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun naryOperation(insn: AbstractInsnNode?, values: MutableList<out AbstractValue>?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun returnOperation(insn: AbstractInsnNode?, value: AbstractValue?, expected: AbstractValue?) {
        TODO("Not yet implemented")
    }

    override fun merge(value1: AbstractValue, value2: AbstractValue): AbstractValue {
        if(value2 == UninitializedValue.UNINITIALIZED_VALUE) {
            return value1
        }

        if(value1 == value2) {
            return value1
        }

        return UninitializedValue.UNINITIALIZED_VALUE
    }
}