package org.spectral.asm.execution.common

import org.spectral.asm.execution.value.AbstractValue

/**
 * Represents a stack collection backed by mutable lists
 * but exposes like a pre-defined JVM stack.
 */
class Stack {

    private val data = mutableListOf<AbstractValue>()

    /**
     * Gets the size or number of 32bit values in the collection.
     */
    val size: Int get() = data.size

    fun pop(index: Int): AbstractValue {
        return data.removeAt(index)
    }

    fun pop(): AbstractValue {
        return pop(0)
    }

    fun push(index: Int, value: AbstractValue) {
        data.add(index, value)
    }

    fun push(value: AbstractValue) {
        push(0, value)
    }
}