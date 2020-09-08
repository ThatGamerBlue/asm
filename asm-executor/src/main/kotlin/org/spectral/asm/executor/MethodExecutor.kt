package org.spectral.asm.executor

import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue
import org.spectral.asm.core.Method

class MethodExecutor
private constructor(
        val method: Method,
        interpreter: SourceInterpreter
) : Analyzer<SourceValue>(interpreter){

    constructor(method: Method) : this(method, SourceInterpreter())

}