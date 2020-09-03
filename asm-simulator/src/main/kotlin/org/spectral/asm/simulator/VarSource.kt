package org.spectral.asm.simulator

/**
 * The sources in which a local variable can receive its value or
 * its referenced value.
 */
enum class VarSource {
    CONSTANT,
    ARG,
    MERGE,
    EXT_EXCEPTION,
    INT_EXCEPTION,
    ARRAY_ELEMENT,
    CAST,
    COMPUTED,
    NEW,
    FIELD,
    METHOD_RETURN
}