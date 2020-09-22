package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor
import org.spectral.asm.core.execution.Frame

/**
 * Represents a JVM line number instruction.
 * This instruction is informational only and doesnt have any operation
 * effect on the program.
 *
 * @property line Int
 * @property start Label
 * @constructor
 */
class LineNumber(val line: Int, val start: Label) : Instruction(-1) {

    override fun execute(frame: Frame) {
        /*
         * Nothing to do.
         */
    }

    override fun accept(visitor: MethodVisitor) {
        visitor.visitLineNumber(line, start.label)
    }

    override fun copy(clonedLabels: Map<Label, Label>): Instruction {
        return LineNumber(line, start)
    }

    override fun toString(): String {
        return "LINE"
    }
}