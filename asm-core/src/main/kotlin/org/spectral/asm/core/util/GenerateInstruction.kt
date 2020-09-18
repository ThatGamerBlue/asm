package org.spectral.asm.core.util

import com.squareup.kotlinpoet.*
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.spectral.asm.core.code.Instruction
import org.spectral.asm.core.common.Opcode
import java.io.File

object GenerateInstruction {

    @JvmStatic
    fun main(args: Array<String>) {
        val opcodeTable = hashMapOf<String, Int>()
        val opcodeFields = Opcodes::class.java.declaredFields
        val startIndex = opcodeFields.indexOf(opcodeFields.first { it.name == "NOP" })
        val endIndex = opcodeFields.indexOf(opcodeFields.first { it.name == "IFNONNULL" })

        for(i in startIndex..endIndex) {
            val field = opcodeFields[i]
            val opcode = field.getInt(null)
            opcodeTable[field.name] = opcode
        }

        /*
         * Generate classes
         */
        opcodeTable.forEach { name, opcode ->
            val file = FileSpec.builder("org.spectral.asm.core.code.instruction", name)
                    .addType(TypeSpec.classBuilder(name)
                            .superclass(Instruction::class)
                            .addSuperclassConstructorParameter("%L", opcode)
                            .addAnnotation(AnnotationSpec.builder(Opcode::class)
                                    .addMember("value=%L", opcode)
                                    .build())
                            .primaryConstructor(FunSpec.constructorBuilder().build())
                            .addFunction(FunSpec.builder("accept")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .addParameter("visitor", MethodVisitor::class)
                                    .addStatement("")
                                    .build()
                            )
                            .addFunction(FunSpec.builder("toString")
                                    .addModifiers(KModifier.OVERRIDE)
                                    .returns(String::class)
                                    .addStatement("return %S", name)
                                    .build()
                            )
                            .build()
                    ).build()

            file.writeTo(File("asm-core/src/main/kotlin/"))
        }
    }
}