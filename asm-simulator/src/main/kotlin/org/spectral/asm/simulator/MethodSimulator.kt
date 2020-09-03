package org.spectral.asm.simulator

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.objectweb.asm.tree.AbstractInsnNode.*
import org.spectral.asm.core.Class
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

        /**
         * Loopcode until there are no more frames which have NOT been executed.
         */
        while(queue.poll().also { frame = it } != null
                || queueTryCatchBlocks() && queue.poll().also { frame = it } != null
        ) {

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
                     * Nopcode Opcode, do nothing.
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

                    /**
                     * Retreivals
                     */
                    
                    IASTORE, BASTORE, CASTORE, SASTORE, FASTORE, AASTORE -> {
                        rec.pop()
                        rec.pop2()
                    }
                    LASTORE, DASTORE -> {
                        rec.pop2()
                        rec.pop2()
                    }
                    POP -> rec.pop()
                    POP2 -> rec.pop2()
                    DUP -> rec.push(rec.peek())
                    DUP_X1 -> {
                        val a = rec.pop()
                        val b = rec.pop()
                        rec.push(a)
                        rec.push(b)
                        rec.push(a)
                    }
                    DUP_X2 -> {
                        val a = rec.pop()
                        if (rec.isTopDoubleSlot) {
                            val b = rec.popDouble()
                            rec.push(a)
                            rec.push(b)
                        } else {
                            val b = rec.pop()
                            val c = rec.pop()
                            rec.push(a)
                            rec.push(c)
                            rec.push(b)
                        }
                        rec.push(a)
                    }
                    DUP2 -> if (rec.isTopDoubleSlot) {
                        rec.push(rec.peekDouble())
                    } else {
                        val a = rec.pop()
                        val b = rec.peek()
                        rec.push(a)
                        rec.push(b)
                        rec.push(a)
                    }
                    DUP2_X1 -> if (rec.isTopDoubleSlot) {
                        val a = rec.popDouble()
                        val b = rec.pop()
                        rec.push(a)
                        rec.push(b)
                        rec.push(a)
                    } else {
                        val a = rec.pop()
                        val b = rec.pop()
                        val c = rec.pop()
                        rec.push(b)
                        rec.push(a)
                        rec.push(c)
                        rec.push(b)
                        rec.push(a)
                    }
                    DUP2_X2 -> if (rec.isTopDoubleSlot) {
                        val a = rec.popDouble()
                        if (rec.isTopDoubleSlot) {
                            val b = rec.popDouble()
                            rec.push(a)
                            rec.push(b)
                        } else {
                            val b = rec.pop()
                            val c = rec.pop()
                            rec.push(a)
                            rec.push(c)
                            rec.push(b)
                        }
                        rec.push(a)
                    } else {
                        val a = rec.pop()
                        val b = rec.pop()
                        if (rec.isTopDoubleSlot) {
                            val c = rec.popDouble()
                            rec.push(b)
                            rec.push(a)
                            rec.push(c)
                        } else {
                            val c = rec.pop()
                            val d = rec.pop()
                            rec.push(b)
                            rec.push(a)
                            rec.push(d)
                            rec.push(c)
                        }
                        rec.push(b)
                        rec.push(a)
                    }
                    SWAP -> {
                        val a = rec.pop()
                        val b = rec.pop()
                        rec.push(a)
                        rec.push(b)
                    }

                    /**
                     * Operations
                     */
                    
                    IADD, FADD, ISUB, FSUB, IMUL, FMUL, IDIV, FDIV, IREM, FREM, ISHL, ISHR, IUSHR, IAND, IOR, IXOR -> {
                        rec.pop()
                        val arg1 = rec.pop()
                        rec.push(arg1.type, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    LADD, DADD, LSUB, DSUB, LMUL, DMUL, LDIV, DDIV, LREM, DREM, LAND, LOR, LXOR -> {
                        rec.popDouble()
                        val arg1 = rec.popDouble()
                        rec.push(arg1.type, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    LSHL, LSHR, LUSHR -> {
                        rec.pop()
                        val `var` = rec.popDouble()
                        rec.push(`var`.type, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    INEG, FNEG -> rec.push(
                            rec.pop().type,
                            rec.getNextVarId(VarSource.COMPUTED)
                    )
                    LNEG, DNEG -> rec.push(
                            rec.popDouble().type,
                            rec.getNextVarId(VarSource.COMPUTED)
                    )
                    I2L, F2L -> {
                        rec.pop()
                        rec.push(rec.common.LONG, rec.getNextVarId(VarSource.CAST))
                    }
                    I2F -> {
                        rec.pop()
                        rec.push(rec.common.FLOAT, rec.getNextVarId(VarSource.CAST))
                    }
                    I2D, F2D -> {
                        rec.pop()
                        rec.push(rec.common.DOUBLE, rec.getNextVarId(VarSource.CAST))
                    }
                    L2I, D2I -> {
                        rec.pop2()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.CAST))
                    }
                    L2F, D2F -> {
                        rec.pop2()
                        rec.push(rec.common.FLOAT, rec.getNextVarId(VarSource.CAST))
                    }
                    L2D -> {
                        rec.pop2()
                        rec.push(rec.common.DOUBLE, rec.getNextVarId(VarSource.CAST))
                    }
                    F2I -> {
                        rec.pop()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.CAST))
                    }
                    D2L -> {
                        rec.pop2()
                        rec.push(rec.common.LONG, rec.getNextVarId(VarSource.CAST))
                    }
                    I2B -> {
                        rec.pop()
                        rec.push(rec.common.BYTE, rec.getNextVarId(VarSource.CAST))
                    }
                    I2C -> {
                        rec.pop()
                        rec.push(rec.common.CHAR, rec.getNextVarId(VarSource.CAST))
                    }
                    I2S -> {
                        rec.pop()
                        rec.push(rec.common.SHORT, rec.getNextVarId(VarSource.CAST))
                    }
                    LCMP, DCMPL, DCMPG -> {
                        rec.pop2()
                        rec.pop2()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    FCMPL, FCMPG -> {
                        rec.pop2()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN -> {
                        if (!exitPoints.containsKey(insn)) exitPoints[insn] = null
                        break@insnLoop
                    }
                    ARRAYLENGTH -> {
                        rec.pop()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.COMPUTED))
                    }
                    ATHROW -> {
                        val ex = rec.pop()
                        rec.clearStack()
                        rec.push(ex.type, rec.getNextVarId(VarSource.INT_EXCEPTION)) // same object, but new scope
                        var handler: LabelNode? = null
                        for (n in method.node.tryCatchBlocks) {
                            if (insns.indexOf(n.start) <= index && insns.indexOf(n.end) > index && (n.type == null || method
                                            .pool[n.type]!!.isAssignableFrom(ex.type!!))
                            ) {
                                handler = n.handler
                                break
                            }
                        }
                        if (handler != null) {
                            val dstindex = insns.indexOf(handler)
                            if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                            rec.jump(dstindex)
                            index = dstindex
                        } else {
                            if (!exitPoints.containsKey(insn)) exitPoints[insn] = null
                            break@insnLoop
                        }
                    }
                    MONITORENTER, MONITOREXIT -> {
                        rec.pop()
                    }
                    BIPUSH -> rec.push(rec.common.BYTE, rec.getNextVarId(VarSource.CONSTANT))
                    SIPUSH -> rec.push(rec.common.SHORT, rec.getNextVarId(VarSource.CONSTANT))

                    NEWARRAY -> {
                        rec.pop()
                        var arrayType: String = when ((insn as IntInsnNode).operand) {
                            T_BOOLEAN -> "[Z"
                            T_CHAR -> "[C"
                            T_FLOAT -> "[F"
                            T_DOUBLE -> "[D"
                            T_BYTE -> "[B"
                            T_SHORT -> "[S"
                            T_INT -> "[I"
                            T_LONG -> "[J"
                            else -> throw UnsupportedOperationException("unknown NEWARRAY operand: " + insn.operand)
                        }
                        rec.push(method.pool.getOrCreate(arrayType), rec.getNextVarId(VarSource.NEW))
                    }
                    ILOAD, LLOAD, FLOAD, DLOAD, ALOAD -> {
                        val lvtindex = (insn as VarInsnNode).`var`
                        rec.push(rec[lvtindex], rec.getId(lvtindex))
                    }
                    ISTORE, FSTORE, ASTORE -> rec[(insn as VarInsnNode).`var`] = rec.pop()
                    LSTORE, DSTORE -> rec[(insn as VarInsnNode).`var`] = rec.popDouble()
                    RET -> {
                        throw UnsupportedOperationException("RET is not supported")
                    }
                    NEW -> rec.push(
                            method.pool
                                    .getOrCreate(Type.getType((insn as TypeInsnNode).desc)),
                            rec.getNextVarId(VarSource.NEW)
                    )
                    ANEWARRAY -> {
                        var desc = (insn as TypeInsnNode).desc
                        desc = if (desc.startsWith("[")) {
                            "[$desc"
                        } else {
                            assert(!desc.startsWith("L"))
                            "[L$desc;"
                        }
                        rec.pop()
                        rec.push(method.pool.getOrCreate(desc), rec.getNextVarId(VarSource.NEW))
                    }
                    CHECKCAST -> {
                        rec.pop()
                        rec.push(
                                method.pool
                                        .getOrCreate(Type.getType((insn as TypeInsnNode).desc)),
                                rec.getNextVarId(VarSource.CAST)
                        )
                    }
                    INSTANCEOF -> {
                        rec.pop()
                        rec.push(rec.common.INT, rec.getNextVarId(VarSource.COMPUTED))
                    }

                    IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE -> {
                        rec.pop()
                        rec.pop()
                        run {
                            val `in` = insn as JumpInsnNode
                            val dstindex = insns.indexOf(`in`.label)
                            if (dstindex != index + 1) {
                                if (opcode == GOTO) {
                                    if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                                    if (!rec.jump(dstindex)) return
                                    index = dstindex
                                } else {
                                    if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex, index + 1)
                                    val e = ExecFrame(dstindex, rec.currentState)
                                    if (executed.add(e)) queue.add(e)
                                }
                            } else { // no-opcode jump
                                if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                            }
                        }
                    }

                    IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNULL, IFNONNULL -> {
                        rec.pop()
                        val `in` = insn as JumpInsnNode
                        val dstindex = insns.indexOf(`in`.label)
                        if (dstindex != index + 1) {
                            if (opcode == GOTO) {
                                if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                                if (!rec.jump(dstindex)) break@insnLoop
                                index = dstindex
                            } else {
                                if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex, index + 1)
                                val e = ExecFrame(dstindex, rec.currentState)
                                if (executed.add(e)) queue.add(e)
                            }
                        } else {
                            if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                        }
                    }
                    GOTO -> {
                        val `in` = insn as JumpInsnNode
                        val dstindex = insns.indexOf(`in`.label)
                        if (dstindex != index + 1) {
                            if (opcode == GOTO) {
                                if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                                if (!rec.jump(dstindex)) break@insnLoop
                                index = dstindex
                            } else {
                                if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex, index + 1)
                                val e = ExecFrame(dstindex, rec.currentState)
                                if (executed.add(e)) queue.add(e)
                            }
                        } else {
                            if (!exitPoints.containsKey(insn)) exitPoints[insn] = intArrayOf(dstindex)
                        }
                    }
                    JSR -> {
                        throw UnsupportedOperationException("JSR is not supported")
                    }
                    LDC -> {
                        val `in` = insn as LdcInsnNode
                        val `val` = `in`.cst
                        if (`val` is Int) {
                            rec.push(rec.common.INT, rec.getNextVarId(VarSource.CONSTANT))
                        } else if (`val` is Float) {
                            rec.push(rec.common.FLOAT, rec.getNextVarId(VarSource.CONSTANT))
                        } else if (`val` is Long) {
                            rec.push(rec.common.LONG, rec.getNextVarId(VarSource.CONSTANT))
                        } else if (`val` is Double) {
                            rec.push(rec.common.DOUBLE, rec.getNextVarId(VarSource.CONSTANT))
                        } else if (`val` is String) {
                            rec.push(rec.common.STRING, rec.getNextVarId(VarSource.CONSTANT))
                        } else if (`val` is Type) {
                            val type = `val`
                            when (type.sort) {
                                Type.OBJECT, Type.ARRAY -> rec.push(
                                        method.pool.getOrCreate("java/lang/Class"),
                                        rec.getNextVarId(VarSource.CONSTANT)
                                )
                                Type.METHOD -> rec.push(
                                        method.pool.getOrCreate("java/lang/invoke/MethodType"),
                                        rec.getNextVarId(VarSource.CONSTANT)
                                )
                                else -> throw UnsupportedOperationException("unsupported type sort: " + type.sort)
                            }
                        } else {
                            throw UnsupportedOperationException("unknown ldc constant type: " + `val`.javaClass)
                        }
                    }
                    IINC -> {
                        val `in` = insn as IincInsnNode
                        rec[`in`.`var`, rec[`in`.`var`]] = rec.getId(`in`.`var`)
                    }
                    TABLESWITCH -> {
                        val `in` =
                                insn as TableSwitchInsnNode
                        rec.pop()
                        if (!exitPoints.containsKey(insn)) {
                            val dsts = hashSetOf<LabelNode>()
                            dsts.addAll(`in`.labels)
                            dsts.add(`in`.dflt)
                            exitPoints[insn] = dsts.stream()
                                    .mapToInt { insnNode: LabelNode? ->
                                        insns.indexOf(insnNode)
                                    }.toArray()
                        }
                        val state = rec.currentState
                        for (label in `in`.labels) {
                            val e = ExecFrame(insns.indexOf(label), state)
                            if (executed.add(e)) queue.add(e)
                        }
                        val dstindex = insns.indexOf(`in`.dflt)
                        if (!rec.jump(dstindex)) break@insnLoop
                        index = dstindex
                    }
                    LOOKUPSWITCH -> {
                        val `in` =
                                insn as LookupSwitchInsnNode
                        rec.pop()
                        if (!exitPoints.containsKey(insn)) {
                            val dsts = hashSetOf<LabelNode>()
                            dsts.addAll(`in`.labels)
                            dsts.add(`in`.dflt)
                            exitPoints[insn] = dsts.stream()
                                    .mapToInt { insnNode: LabelNode? ->
                                        insns.indexOf(insnNode)
                                    }.toArray()
                        }
                        val state = rec.currentState
                        for (label in `in`.labels) {
                            val e = ExecFrame(insns.indexOf(label), state)
                            if (executed.add(e)) queue.add(e)
                        }
                        val dstindex = insns.indexOf(`in`.dflt)
                        if (!rec.jump(dstindex)) break@insnLoop
                        index = dstindex
                    }
                    MULTIANEWARRAY -> {
                        val `in` =
                                insn as MultiANewArrayInsnNode
                        val cls = method.pool.getOrCreate(`in`.desc)

                        var i = 0
                        while (i < `in`.dims) {
                            rec.pop()
                            i++
                        }
                        rec.push(cls, rec.getNextVarId(VarSource.NEW))
                    }
                    else -> throw UnsupportedOperationException("unknown opcode: " + insn.opcode + " (type " + insn.type + ")")
                }

                if(!rec.next()) break
                index++
            }
        }
    }

    /**
     * Queue the try-catch blocks as individual control flow frames.
     *
     * @return Boolean
     */
    private fun queueTryCatchBlocks(): Boolean {
        if(method.node.tryCatchBlocks.isEmpty()) return false
        val states = hashSetOf<ExecState>()
        var ret = false

        for(n in method.node.tryCatchBlocks) {
            val type: Class? = if(n.type != null) method.pool
                    .getOrCreate(Type.getObjectType(n.type)) else method.pool
                    .getOrCreate("java/lang/Throwable")

            val stack = arrayOf(type)
            val stackVarIds = intArrayOf(rec.getNextVarId(VarSource.EXT_EXCEPTION))

            var index = insns.indexOf(n.start)
            val max = insns.indexOf(n.end)

            while(index < max) {
                val state = rec.states[index]
                if(state != null) {
                    states.add(ExecState(
                            state.locals.copyOf(state.locals.size),
                            state.localVarIds.copyOf(state.locals.size),
                            stack,
                            stackVarIds
                    ))
                }

                index++
            }

            if(states.isEmpty()) continue

            val dstIndex = insns.indexOf(n.handler)
            for(state in states) {
                val exception = ExecFrame(dstIndex, state)
                if(executed.add(exception)) {
                    queue.add(exception)
                    ret = true
                }
            }

            states.clear()
        }

        return ret
    }
}