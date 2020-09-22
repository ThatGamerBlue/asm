package org.spectral.asm.core.common

import org.objectweb.asm.ClassWriter
import org.spectral.asm.core.Class
import org.spectral.asm.core.ClassPool

class NonLoadingClassWriter(val pool: ClassPool, flags: Int) : ClassWriter(flags) {

    override fun getCommonSuperClass(type1: String, type2: String): String {
        if(type1 == "java/lang/Object" || type2 == "java/lang/Object") {
            return "java/lang/Object"
        }

        val c1 = pool[type1]
        val c2 = pool[type2]

        if(c1 == null && c2 == null) {
            return try {
                super.getCommonSuperClass(type1, type2)
            } catch (e: Exception) {
                "java/lang/Object"
            }
        }

        if(c1 != null && c2 != null) {
            if(!(c1.isInterface || c2.isInterface)) {
                var it1 = c1
                while(it1 != null) {
                    var it2 = c2
                    while(it2 != null) {
                        if(it1 == it2) return it1.name
                        it2 = it2.parent.cls
                    }
                    it1 = it1.parent.cls
                }
            }

            return "java/lang/Object"
        }

        val found: Class?
        val other: String

        if(c1 == null) {
            found = c2
            other = type1
        } else {
            found = c1
            other = type2
        }

        var prev: Class? = null
        var c = found
        while(c != null) {
            prev = c
            if(prev.parent.name == other) return other
            c = c.parent.cls
        }

        return super.getCommonSuperClass(prev!!.parent.name, other)
    }
}