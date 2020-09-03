package org.spectral.asm.simulator.controlflow

import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.core.Method
import kotlin.math.max

class Block(val from: Int, val to: Int, val insns: List<AbstractInsnNode>) : Comparable<Block> {

    private val key: Int = max(from, to)
    private val branches = mutableListOf<Block>()

    lateinit var parent: Block
        private set

    var depth: Int = 0
        private set

    val first: AbstractInsnNode get() = insns.first()

    val last: AbstractInsnNode get() = insns.last()

    fun setParent(parent: Block) {
        this.parent = parent
        var depth = 0
        var tmp: Block? = this
        while(tmp?.parent != null) {
            tmp = tmp.parent
            depth++
        }

        this.depth = depth
    }

    operator fun get(insnIndex: Int): Block {
        branches.forEach { subBlock ->
            if(insnIndex > subBlock.from && insnIndex < subBlock.to) {
                return subBlock[insnIndex]
            }
        }

        return this
    }

    fun addBranch(block: Block) {
        var found: Block? = null
        branches.forEach { sub ->
            if(block.from >= sub.from && block.from <= sub.to) {
                found = sub
            }
        }

        if(found != null) {
            found!!.addBranch(block)
            return
        }

        branches.forEach { sub ->
            if(sub.from >= block.from && sub.from <= block.to) {
                found = sub
            }
        }

        if(found != null) {
            branches.remove(found!!)
            block.addBranch(found!!)
        }

        block.parent = this
        branches.add(block)
        branches.sort()
    }

    fun isRoot(): Boolean = depth == 0

    override fun compareTo(other: Block): Int {
        return key.compareTo(other.key)
    }

    override fun toString(): String {
        var prefix = ""
        if(parent == null) prefix = "Root "
        return "$prefix($from,$to)"
    }

    companion object {

        fun create(method: Method, from: Int, to: Int): Block {
            val insns = mutableListOf<AbstractInsnNode>()
            for(i in from + 1 until to - 1) {
                insns.add(method.instructions[i])
            }

            return Block(from, to, insns)
        }
    }
}