package org.spectral.asm.core.code

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

/**
 * Represents a JVM label instruction which signals the start of
 * a new block of code.
 *
 * @property label The ASM Label.
 * @constructor
 */
class Label(var label: Label?) : Instruction(-1) {

    /**
     * Creates a label without any associated ASM [Label]
     *
     * @constructor
     */
    constructor() : this(Label())

    override fun accept(visitor: MethodVisitor) {
        visitor.visitLabel(label)
    }

    override fun copy(clonedLabels: Map<org.spectral.asm.core.code.Label, org.spectral.asm.core.code.Label>): Instruction {
        return clonedLabels[this] ?: throw IllegalStateException("Failed to clone label. No copy label in labelMap.")
    }

    override fun toString(): String {
        return "LABEL"
    }
}