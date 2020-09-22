package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.spectral.asm.core.execution.Frame


class FrameNode private constructor(opcode: Int) : Instruction(opcode) {

    var type = 0

    var local: MutableList<Any?>? = null

    var stack: MutableList<Any?>? = null

    constructor(
            type: Int,
            numLocal: Int,
            local: Array<Any?>?,
            numStack: Int,
            stack: Array<Any?>?,
    ) : this(-1) {
        this.type = type
        when (type) {
            Opcodes.F_NEW, Opcodes.F_FULL -> {
                this.local = asArrayList(numLocal, local)
                this.stack = asArrayList(numStack, stack)
            }
            Opcodes.F_APPEND -> this.local = asArrayList(numLocal, local)
            Opcodes.F_CHOP -> this.local = asArrayList(numLocal, null)
            Opcodes.F_SAME -> {
            }
            Opcodes.F_SAME1 -> this.stack = asArrayList(1, stack)
            else -> throw IllegalArgumentException()
        }
    }

    override fun execute(frame: Frame) {
        /*
         * Nothing to do.
         */
    }

    override fun accept(visitor: MethodVisitor) {
        when (type) {
            Opcodes.F_NEW, Opcodes.F_FULL -> visitor.visitFrame(type, local!!.size, asArray(local), stack!!.size, asArray(stack))
            Opcodes.F_APPEND -> visitor.visitFrame(type, local!!.size, asArray(local), 0, null)
            Opcodes.F_CHOP -> visitor.visitFrame(type, local!!.size, null, 0, null)
            Opcodes.F_SAME -> visitor.visitFrame(type, 0, null, 0, null)
            Opcodes.F_SAME1 -> visitor.visitFrame(type, 0, null, 1, asArray(stack))
            else -> throw IllegalArgumentException()
        }
    }

    override fun toString(): String {
        return "FRAME"
    }

    companion object {

        private fun <T> asArrayList(size: Int, array: Array<T?>?): MutableList<T?> {
            val lst = MutableList<T?>(size) { null }
            array?.forEachIndexed { i, it ->
                lst[i] = it
            }

            return lst
        }

        private fun asArray(list: List<Any?>?): Array<Any?> {
            val array = arrayOfNulls<Any>(list!!.size)
            var i = 0
            val n = array.size
            while (i < n) {
                var o = list[i]
                if (o is Label) {
                    o = o.label
                }
                array[i] = o
                ++i
            }
            return array
        }
    }
}
