package org.spectral.asm.simulator

import org.objectweb.asm.tree.AbstractInsnNode
import org.spectral.asm.core.Method
import java.util.*

class MethodSimulator(private val method: Method) {

    /**
     * The state recorder of the method simulator
     */
    val rec = StateRecorder(method)

    /**
     * The method instruction list
     */
    val insns = method.instructions

    /**
     * The exit point instructions for the execution.
     */
    val exitPoints = IdentityHashMap<AbstractInsnNode, IntArray>()

    /**
     * A queue of states to be processed.
     */
    val queue = ArrayDeque<QueueElement>()

    /**
     * A set of [QueueElement]s which have already been processed.
     */
    val queued = hashSetOf<QueueElement>()

    /**
     * Runs the method simulator.
     */
    fun run() {
        /*
         * Add the normal exit point (end of the method) to the
         * exit points of the execution.
         */
        exitPoints[null] = intArrayOf(0)

        /*
         * Add the first initial state to the queue.
         */
        queue.add(QueueElement(0, rec.getState()))

        /*
         * Add the initial queued state to the processed queue since the first
         * state will always be processed.
         */
        queued.add(queue.peek())

        var first = true
        var element: QueueElement? = queue.poll()

        while(element != null) {

        }
    }

    private fun queueTryCatchBlocks(): Boolean {
        if(method.node.tryCatchBlocks.isEmpty()) return false

        val states = hashSetOf<ExecutionState>()
        var ret = false

        for(n in method.node.tryCatchBlocks) {
            val type = if(n.type != null) method.pool.getOrCreate(n.type) else method.pool.getOrCreate("java/lang/Throwable")
            val stack = arrayOf(type.type)
        }
    }
}