package org.spectral.asm.core.code

import org.objectweb.asm.MethodVisitor

/**
 * Represents a try-catch exception block within a method's instruction.
 *
 * @property start The label at the start of this block.
 * @property end The label at the end of this block.
 * @property handler The label at the start of the handler block.
 * @property catchType The type of catch class.
 */
class Exception(val start: Label, val end: Label, val handler: Label?, val catchType: String?) {

}