package org.spectral.asm.simulator.util

import org.objectweb.asm.tree.AbstractInsnNode

/**
 * Contains instruction utility methods
 */
object InsnUtil {

    /**
     * Reflection instruction index field.
     */
    val INSN_INDEX = AbstractInsnNode::class.java.getDeclaredField("index")
            .apply { this.isAccessible = true }

    /**
     * Gets the index of a given instruction in a method.
     *
     * @param insn AbstractInsnNode
     * @return Int
     */
    fun index(insn: AbstractInsnNode): Int {
        try {
            val v = INSN_INDEX.get(insn) as Int
            if(v >= 0) {
                return v
            }
        } catch(e : Exception) { }

        /*
         * Fall back.
         */
        var index = 0
        var cur: AbstractInsnNode? = insn
        while(cur.also { cur = insn.previous } != null) {
            cur = cur?.previous
            index++
        }

        return index
    }
}