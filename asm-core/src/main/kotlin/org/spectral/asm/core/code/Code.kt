package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.Method
import org.spectral.asm.core.code.type.VarInstruction
import org.objectweb.asm.Label as AsmLabel

/**
 * Represents the code JVM instruction collection of a method.
 *
 * @property method Method
 * @property maxStack Int
 * @property maxLocals Int
 * @constructor
 */
class Code(val method: Method) {

    private val insnList = mutableListOf<Instruction>()

    val instructions: List<Instruction> get() = insnList

    var maxStack = 0

    var maxLocals = 0

    var exceptions = mutableListOf<Exception>()

    val labelMap = hashMapOf<AsmLabel, Label>()

    val size: Int get() = insnList.size

    operator fun get(index: Int): Instruction {
        if(index < 0 || index > size) {
            throw IndexOutOfBoundsException()
        }

        return insnList[index]
    }

    fun contains(insn: Instruction): Boolean {
        return insnList.contains(insn)
    }

    fun indexOf(insn: Instruction): Int {
        return insnList.indexOf(insn)
    }

    fun accept(visitor: MethodVisitor) {
        insnList.forEach { it.accept(visitor) }
    }

    fun add(insn: Instruction) {
        insnList.add(insn)
    }

    fun insert(insn: Instruction) {
        insnList.add(0, insn)
    }

    fun insertAfter(target: Instruction, insn: Instruction) {
        insnList.add(indexOf(target) + 1, insn)
    }

    fun insertBefore(target: Instruction, insn: Instruction) {
        insnList.add(indexOf(target), insn)
    }

    fun remove(insn: Instruction) {
        insnList.remove(insn)
    }

    fun removeAll(insns: Collection<Instruction>) {
        insnList.removeAll(insns)
    }

    fun clear() {
        insnList.clear()
    }

    fun findLabel(label: AsmLabel): Label = method.findLabel(label)

    fun calculateMaxLocals(): Int {
        var max = -1

        instructions.forEach { insn ->
            if(insn is VarInstruction) {
                val slots = insn.index + insn.type.slots
                if(slots > max) {
                    max = slots
                }
            }
        }

        val argSlots = getMethodTypeSlots()
        if(argSlots > max) {
            max = argSlots
        }

        return max
    }

    private fun getMethodTypeSlots(): Int {
        var num = if(method.isStatic) 0 else 1
        val argTypes = method.type.argumentTypes

        argTypes.forEach {
            num += it.size
        }

        return num
    }

    fun resetLabels() {
        labelMap.clear()

        insnList.forEach { insn ->
            if(insn is Label) {
                val label = AsmLabel()
                insn.label = label
                labelMap[label] = insn
            }
        }
    }
}