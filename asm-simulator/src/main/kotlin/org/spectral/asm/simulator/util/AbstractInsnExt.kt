package org.spectral.asm.simulator.util

import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.simulator.value.AbstractValue

/**
 * A collection of values poped off the stack by an instruction.
 */
private val poppedValues = hashMapOf<Int, MutableList<AbstractValue>>()

/**
 * A collection of value pushed to the stack by an instruction.
 */
private val pushedValues = hashMapOf<Int, MutableList<AbstractValue>>()

internal val AbstractInsnNode.hash: Int get() = System.identityHashCode(this)

/**
 * The popped values of this instruction.
 */
val AbstractInsnNode.pops get() = poppedValues.getOrPut(this.hash) { mutableListOf() }

/**
 * The pushed values of this instruction.
 */
val AbstractInsnNode.pushed get() = pushedValues.getOrPut(this.hash) { mutableListOf() }