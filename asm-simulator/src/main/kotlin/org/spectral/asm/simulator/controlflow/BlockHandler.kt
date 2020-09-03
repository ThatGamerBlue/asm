package org.spectral.asm.simulator.controlflow

import org.spectral.asm.core.Method
import kotlin.math.max
import kotlin.math.min

class BlockHandler(private val method: Method) {

    val root = createBlock(0,method.instructions.size() - 1)

    fun add(insnIndex: Int, successorIndex: Int) {
        root.addBranch(createBlock(insnIndex, successorIndex))
    }

    operator fun get(index: Int): Block = root[index]

    fun getCommonBlock(first: Int, second: Int): Block? {
        var firstBlock = this[first]
        var secondBlock = this[second]

        while(firstBlock != root) {
            while(firstBlock.depth > secondBlock.depth) {
                firstBlock = firstBlock.parent
            }

            if(firstBlock == secondBlock) {
                return firstBlock
            }

            secondBlock = secondBlock.parent
        }

        return null
    }

    private fun createBlock(insnIndex: Int, successorIndex: Int): Block {
        val start = min(insnIndex, successorIndex)
        val end = max(insnIndex, successorIndex)
        return Block.create(method, start, end)
    }
}