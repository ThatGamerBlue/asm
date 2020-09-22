package org.spectral.asm.core.execution

import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.Method
import org.spectral.asm.core.execution.exception.ExecutionException
import org.spectral.asm.core.execution.value.AbstractValue
import org.spectral.asm.core.execution.value.DoubleValue
import org.spectral.asm.core.execution.value.LongValue

/**
 * Represents the execution of a method as the JVM would.
 * returns a collection of frames.
 *
 * @property pool ClassPool
 * @constructor
 */
class Execution private constructor(val pool: ClassPool) {

}