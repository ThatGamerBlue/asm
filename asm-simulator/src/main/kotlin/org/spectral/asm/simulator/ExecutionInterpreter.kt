package org.spectral.asm.simulator

import org.objectweb.asm.Opcodes.ASM8
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.analysis.Interpreter
import org.spectral.asm.simulator.controlflow.BlockHandler
import org.spectral.asm.simulator.value.AbstractValue

/**
 * Responsible for interpreting a method's bytecode and simulating
 * its execution result.
 *
 * @property simulator MethodSimulator
 * @property blockHandler BlockHandler
 */
class ExecutionInterpreter : Interpreter<AbstractValue>(ASM8) {

    lateinit var simulator: MethodSimulator
    lateinit var blockHandler: BlockHandler

    override fun newValue(type: Type?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun naryOperation(insn: AbstractInsnNode?, values: MutableList<out AbstractValue>?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun ternaryOperation(insn: AbstractInsnNode?, value1: AbstractValue?, value2: AbstractValue?, value3: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun merge(value1: AbstractValue?, value2: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun returnOperation(insn: AbstractInsnNode?, value: AbstractValue?, expected: AbstractValue?) {
        TODO("Not yet implemented")
    }

    override fun unaryOperation(insn: AbstractInsnNode?, value: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun binaryOperation(insn: AbstractInsnNode?, value1: AbstractValue?, value2: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun copyOperation(insn: AbstractInsnNode?, value: AbstractValue?): AbstractValue {
        TODO("Not yet implemented")
    }

    override fun newOperation(insn: AbstractInsnNode?): AbstractValue {
        TODO("Not yet implemented")
    }


}