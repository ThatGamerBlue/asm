package org.spectral.asm.analysis.simulator

/**
 * The types of sources that a variable's data was changed by.
 */
enum class VariableSource {
    CONSTANT,
    ARGUMENT,
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