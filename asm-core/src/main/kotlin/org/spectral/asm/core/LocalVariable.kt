package org.spectral.asm.core

import org.objectweb.asm.Type
import org.spectral.asm.core.reference.ClassRef

class LocalVariable(
        val method: Method,
        val isArg: Boolean,
        var index: Int,
        val lvtIndex: Int,
        val asmIndex: Int,
        val typeRef: ClassRef,
        val startInsnIndex: Int,
        val endInsnIndex: Int,
        val startOpIndex: Int,
        override var name: String
) : Node {

    override var access: Int = 0

    override val type get() = Type.getObjectType(typeRef.name)

    override fun init() {
        /*
         * Nothing to Do
         */
    }

}