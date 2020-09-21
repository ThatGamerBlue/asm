package org.spectral.asm.core.execution

/**
 * Represents a value which can be pushed to the stack.
 * @property data Any?
 * @constructor
 */
open class Value(private var data: Any?) {

    open val value: Any? get() = data

    companion object {
        /**
         * Null stack value.
         */
        val NULL = Value(null)

        /**
         * Unresolved or Unknown stack value.
         */
        val UNKNOWN = Value(null)
    }
}