package org.spectral.asm.core.reference

import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool

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
    var cls: Class? = null
        internal set

    /**
     * Creates a class name object and attempts to initialize [cls] with the name
     * of this object if it exists in the class pool.
     *
     * @param pool ClassPool
     * @param name String
     * @constructor
     */
    internal constructor(pool: ClassPool, name: String) : this(name) {
        this.cls = pool[name]
    }

    override fun toString(): String {
        return name
    }
}