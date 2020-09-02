package org.spectral.asm.simulator

/**
 * The sources which a local variable can have its data written from.
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
    METHOD_RETURN;
}