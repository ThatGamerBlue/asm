package org.spectral.asm.simulator.controlflow

import org.spectral.asm.core.Method
import kotlin.math.max
import kotlin.math.min

/**
 * Responsible for managing and handling control flow block
 * objects.
 *
 * @property method Method
 * @constructor
 */
class BlockHandler(val method: Method) {

    /**
     * The root or top of the chain control flow block.
     */
    val root = createBlock(0, method.instructions.size() - 1)

    /**
     * Adds a block at the given indexes
     *
     * @param insnIndex Int
     * @param successorIndex Int
     */
    fun add(insnIndex: Int, successorIndex: Int) {
       root.addBranch(createBlock(insnIndex, successorIndex))
    }

    /**
     * Gets a block at a given instruction index.
     *
     * @param index Int
     * @return Block
     */
    operator fun get(index: Int): Block {
        return root[index]
    }

    /**
     * Gets a common shared chain of of blocks between two instruction indexes.
     *
     * @param first Int
     * @param second Int
     * @return Block?
     */
    fun getCommonBlock(first: Int, second: Int): Block? {
        var firstBlock: Block = this[first]
        var secondBlock: Block = this[second]

        while(firstBlock != root) {
            while(firstBlock.depth > secondBlock.depth) {
                firstBlock = firstBlock.parent!!
            }

            if(firstBlock == secondBlock) {
                return firstBlock
            }

            secondBlock = secondBlock.parent!!
        }

        return null
    }

    /**
     * Creates a blocks given the current instruction index and the start of the
     * next control flow block index.
     *
     * @param insnIndex Int
     * @param successorIndex Int
     * @return Block
     */
    private fun createBlock(insnIndex: Int, successorIndex: Int): Block {
        val start = min(insnIndex, successorIndex)
        val end = max(insnIndex, successorIndex)
        return Block.create(method, start, end)
    }
}