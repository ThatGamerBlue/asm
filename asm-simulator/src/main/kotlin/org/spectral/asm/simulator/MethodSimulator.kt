package org.spectral.asm.simulator

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.spectral.asm.core.Method
import java.util.*

/**
 * Responsible for executing a method and depending on the
 * method instruction opcode at a given frame, recording the states.
 *
 * @property method Method
 * @constructor
 */
class MethodSimulator(private val method: Method) {

    /**
     * The state recorder for the simulation
     */
    val rec = StateRecorder(method)

    /**
     * The instruction list of the method.
     */
    private val insns = method.instructions

    /**
     * The execution exit points.
     */
    private val exitPoints = IdentityHashMap<AbstractInsnNode, IntArray>()

    /**
     * A queue of execution frames to process.
     */
    private val queue = ArrayDeque<ExecFrame>()

    /**
     * A set of [ExecFrame]s that have already been simulated.
     */
    private val executed = mutableSetOf<ExecFrame>()

    /**
     * Run the method execution simulation.
     */
    fun run() {
        /*
         * Add the default exit point
         */
        exitPoints[null] = intArrayOf(0)

        /*
         * Add the initial execution frames to the queue.
         */
        queue.add(ExecFrame(0, rec.currentState))
        executed.add(queue.peek())

        var first = true
        var frame: ExecFrame

        while(queue.poll().also { frame = it } != null) {
            if(!rec.jump(frame.dstIndex, frame.srcState) && !first) continue
            first = false

            var index = frame.dstIndex

            insnLoop@ while(index < insns.size()) {
                val insn = insns[index]
                val insnType = insn.type

                if(insnType == LABEL || insnType == FRAME || insnType == LINE) {
                    if(!rec.next()) break
                    index++
                    continue
                }

                /**
                 * Check if the current instruction's opcode matches. If so update
                 * the recorder with what the JVM does at that given opcode perm the
                 * Oracle JVM documentation online.
                 */

                val opcode = insn.opcode
                when(opcode) {

                    /**
                     * NOP Opcode, do nothing.
                     */
                    NOP -> {}

                    /**
                     * Constant types
                     */
                    ACONST_NULL -> {
                        rec.push(rec.common.NULL, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    ICONST_0, ICONST_1 -> {
                        rec.push(rec.common.BOOLEAN, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    ICONST_M1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 -> {
                        rec.push(rec.common.BYTE, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    LCONST_0, LCONST_1 -> {
                        rec.push(rec.common.LONG, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    FCONST_0, FCONST_1, FCONST_2 -> {
                        rec.push(rec.common.FLOAT, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    DCONST_0, DCONST_1 -> {
                        rec.push(rec.common.DOUBLE, rec.getNextVarId(VarSource.CONSTANT))
                    }

                    /**
                     * Array elements
                     */
                    IALOAD, BALOAD, CALOAD, SALOAD -> {
                        rec.pop2()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.ARRAY_ELEMENT))
                    }

                    LALOAD -> {
                        rec.pop2()
                        rec.push(rec.common.LONG, rec.getNextVarId(VarSource.ARRAY_ELEMENT))
                    }

                    FALOAD -> {
                        rec.pop2()
                        rec.push(rec.common.FLOAT, rec.getNextVarId(VarSource.ARRAY_ELEMENT))
                    }

                    DALOAD -> {
                        rec.pop2()
                        rec.push(rec.common.DOUBLE, rec.getNextVarId(VarSource.ARRAY_ELEMENT))
                    }

                    AALOAD -> {
                        rec.pop() // Pops the index of the array off
                        val array = rec.pop()
                        rec.push(array.type!!.elementClass, rec.getNextVarId(VarSource.ARRAY_ELEMENT))
                    }


                }
            }
        }
    }
}