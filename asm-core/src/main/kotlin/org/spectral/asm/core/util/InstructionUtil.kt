package org.spectral.asm.core.util

import com.esotericsoftware.reflectasm.ConstructorAccess
import io.github.classgraph.ClassGraph
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Contains utility methods for building and dealing with
 * instructions within the library.
 */
@Suppress("UNCHECKED_CAST")
object InstructionUtil {

    /**
     * Backing storage of instruction classes mapped as opcode -> Instruction kotlin class.
     */
    val instructionMap = hashMapOf<Int, KClass<out Instruction>>()

    /**
     * Build the instruction map using class graph to find
     * any classes which extend [Instruction]
     */
    init {
        val scan = ClassGraph().enableAllInfo().acceptPackages("org.spectral.asm.core.code.instruction").scan()
        val results = scan.getSubclasses(Instruction::class.java.canonicalName)

        /*
         * Store the results.
         */
        results.forEach { cls ->
            /*
             * Grab the [Opcode] annotation from the cls. If
             * it doesnt exist. skip the registration.
             */
            val annotation = cls.annotationInfo.firstOrNull { it.name == Opcode::class.qualifiedName } ?:
                    throw IllegalStateException("Found instruction class '${cls.simpleName}' without '@Opcode' annotation.")

            val opcode = (annotation.loadClassAndInstantiate() as Opcode).value
            val klass = cls.loadClass().kotlin

            instructionMap[opcode] = klass as KClass<out Instruction>
        }
    }

    /**
     * Gets an instruction instance for a given opcode.
     *
     * @param opcode Int
     * @return Instruction
     */
     fun getInstruction(opcode: Int, vararg args: Any?): Instruction {
        val cls = this.instructionMap[opcode] ?: throw IndexOutOfBoundsException("No instruction found for opcode $opcode.")

        if(args.isEmpty()) {
            return cls.primaryConstructor!!.call()
        } else {
            return cls.primaryConstructor!!.call(*args)
        }
    }
}