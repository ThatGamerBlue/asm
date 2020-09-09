package org.spectral.asm.executor

import me.coley.analysis.value.AbstractValue

class ExecutionValue(val value: AbstractValue) {

    var pusher: ExecutionFrame? = null

    val poppers = mutableListOf<ExecutionFrame>()

}