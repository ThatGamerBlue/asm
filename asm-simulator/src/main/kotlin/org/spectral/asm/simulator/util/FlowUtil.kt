package org.spectral.asm.simulator.util

import org.objectweb.asm.Opcodes.IFNULL
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.spectral.asm.core.Method
import org.spectral.asm.simulator.controlflow.BlockHandler
import org.spectral.asm.simulator.value.AbstractValue

/**
 * Holds control flow utility methods.
 */
object FlowUtil {

    /**
     * Gets whether the instruction in the current block is a conditional jump
     * to another instruction index.
     *
     * @param method Method
     * @param insnIndex Int
     * @param successorIndex Int
     * @return Boolean
     */
    fun isFlowModifier(method: Method, insnIndex: Int, successorIndex: Int): Boolean {
        val dest = method.instructions[successorIndex]

        /*
         * False if the jump instruction does not have a defined label
         */
        if(dest.type != LABEL) {
            return false
        }

        val src = method.instructions[insnIndex]
        val srcType = src.type

        return srcType == JUMP_INSN
                || srcType == LOOKUPSWITCH_INSN
                || srcType == TABLESWITCH_INSN
    }

    /**
     * Gets whether a given [AbstractValue] has been null checked or not.
     *
     * @param handler BlockHandler
     * @param value AbstractValue
     * @param usage AbstractInsnNode
     * @return Boolean
     */
    fun isNullChecked(handler: BlockHandler, value: AbstractValue, usage: AbstractInsnNode): Boolean {
        if(!value.isNull) return true
        val nullCheck = value.nullCheck ?: return false
        val safeInsn = if(nullCheck.opcode == IFNULL) {
            nullCheck.next
        } else {
            nullCheck.label
        }

        val safeIndex = InsnUtil.index(safeInsn)
        return handler[safeIndex].insns.contains(usage)
    }
}