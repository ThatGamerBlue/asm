package org.spectral.asm.execution.frame

import org.spectral.asm.core.execution.ExecutionValue
import org.spectral.asm.core.execution.value.*

class FrameValue(override val data: AbstractValue) : ExecutionValue {

    override lateinit var pusher: Frame

    override val poppers = mutableListOf<Frame>()

}