package org.spectral.asm.core

/**
 * A named class reference which may or may not exist. Either
 * way the name of the reference is held in this object.
 *
 * Contains a possibly null [ref] which points to the [Class] if the reference
 * is inside of the class pool.
 */
class ClassRef(val name: String) {

    /**
     * The reference [Class] if its contained in the pool.
     */
    var ref: Class? = null

    override fun toString(): String {
        return name
    }
}