package org.spectral.asm.simulator

import org.spectral.asm.core.Method

/**
 * Responsible for recording the state of a given method execution for each
 * frame of the execution.
 *
 * This is basically a simplified JVM stack machine with frame recording.
 *
 * @property method Method
 * @constructor
 */
class StateRecorder(private val method: Method) {


}