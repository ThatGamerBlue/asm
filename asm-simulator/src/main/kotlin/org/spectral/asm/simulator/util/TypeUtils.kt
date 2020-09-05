package org.spectral.asm.simulator.util

import org.objectweb.asm.Type
import org.objectweb.asm.Type.*

/**
 * The backing type sort order
 */
private val SORT_ORDER = mutableListOf<Int>(
        VOID, BOOLEAN, BYTE, SHORT, CHAR, INT, FLOAT,
        DOUBLE, LONG, ARRAY, OBJECT
)

/**
 * Whether a type is a primitive data type or not
 */
val Type.isPrimitive: Boolean get() = this.sort < ARRAY

/**
 * Gets the index in the sort order for this type.
 */
val Type.promotionIndex: Int get() = SORT_ORDER.indexOf(this.sort)

fun isPrimitiveDesc(desc: String): Boolean {
    if(desc.length != 1) return false
    return when(desc[0]) {
        'Z', 'C', 'B', 'S',
            'I', 'F', 'J', 'D' -> true
        else -> false
    }
}