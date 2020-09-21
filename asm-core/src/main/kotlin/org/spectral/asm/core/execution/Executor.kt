package org.spectral.asm.core.execution

import org.spectral.asm.core.ClassPool
import org.spectral.asm.core.Method

/**
 * Represents a execution simulation of a method.
 * The executor simulates how the JVM would execute the opcodes in a method given some arguments if any exist.
 * At each frame, an execution state execution context is created which allows for backwards tracing of where values from the
 * local variable table or from the stack originate from.
 *
 * @property pool ClassPool
 * @constructor
 */
class Executor(val pool: ClassPool) {


}