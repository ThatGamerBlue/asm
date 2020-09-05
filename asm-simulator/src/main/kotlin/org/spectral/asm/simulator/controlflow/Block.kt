package org.spectral.asm.simulator.controlflow

import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.core.Method
import kotlin.math.max

/**
 * Represents a control flow block of instructions.
 *
 * @property from The starting instruction index
 * @property to The ending instruction index
 * @property insns List<AbstractInsnNode>
 * @constructor
 */
class Block(val from: Int, val to: Int, val insns: List<AbstractInsnNode>) : Comparable<Block> {

    /**
     * The identifier instruction index of this block
     */
    private var key: Int = max(from, to)

    /**
     * The blocks which branch off of the current block due to
     * a control flow instruction conditional
     */
    val branches = mutableListOf<Block>()

    /**
     * The depth of the current block in the chain.
     */
    var depth = 0
        private set

    /**
     * The parent control flow block
     */
    var parent: Block? = null
        set(value) {
            field = value

            var depth = 0
            var tmp: Block? = this
            while(tmp?.parent != null) {
                tmp = tmp.parent
                depth++
            }

            this.depth = depth
        }

    /**
     * The first instruction of the block
     */
    val first: AbstractInsnNode get() = insns.first()

    /**
     * The last instruction of the block
     */
    val last: AbstractInsnNode get() = insns.last()

    /**
     * Whether this block is the top of the chain
     */
    val isRoot: Boolean get() = depth == 0

    /**
     * Recursively gets a block at a given index.
     *
     * @param index Int
     * @return Block
     */
    operator fun get(index: Int): Block {
        branches.forEach { block ->
            if(index > block.from && index < block.to) {
                return block[index]
            }
        }

        return this
    }

    /**
     * Adds a successor or branch block to the current block.
     *
     * @param block Block
     */
    fun addBranch(block: Block) {
        var found: Block? = null
        branches.forEach { sub ->
            if(block.from >= sub.from && block.from <= sub.to) {
                found = sub
            }
        }

        if(found != null) {
            found?.branches?.add(block)
            return
        }

        branches.forEach { sub ->
            if(sub.from >= block.from && sub.from <= block.to) {
                found = sub
            }
        }

        if(found != null) {
            branches.remove(found)
            block.branches.add(found!!)
        }

        block.parent = this
        branches.add(block)
        branches.sort()
    }

    override fun compareTo(other: Block): Int {
        return key.compareTo(other.key)
    }

    override fun toString(): String {
        var prefix = ""
        if(parent == null) {
            prefix = "ROOT"
        }
        return "$prefix($from,$to)"
    }

    companion object {
        /**
         * Creates a control flow block with a given method instructions
         *
         * @param method Method
         * @param from Int
         * @param to Int
         * @return Block
         */
        fun create(method: Method, from: Int, to: Int): Block {
            val insns = mutableListOf<AbstractInsnNode>()
            for(i in from + 1 until to - 1) {
                insns.add(method.instructions[i])
            }

            return Block(from, to, insns)
        }
    }
}