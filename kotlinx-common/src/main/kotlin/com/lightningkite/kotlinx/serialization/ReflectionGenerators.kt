package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.*

object ReflectionGenerators {

    val stringType = KxType(StringReflection)

    inline fun <OUT, RESULT, OBJCONTEXT> writeReflectedGen(
            forWriter: AnyWriter<OUT, RESULT>,
            crossinline writeObject: OUT.(
                    keySubtype: KxType,
                    (
                            writeField: (
                                    Any?,
                                    OUT.() -> RESULT
                            ) -> Unit
                    ) -> Unit
            ) -> RESULT
    ): AnySubWriterGenerator<OUT, RESULT> = generator@{
        val kx = it.kxReflectOrNull ?: return@generator null
        val vars = kx.variables

        writer@{ typeInfo, value ->
            writeObject(stringType) { writeField ->
                for (v in vars) {
                    writeField(v.key) {
                        @Suppress("UNCHECKED_CAST")
                        val subValue = v.value.get.let { it as (Any) -> Any? }.invoke(value)
                        forWriter.write(v.value.type, subValue, this)
                    }
                }
            }
        }
    }

    inline fun <IN> readReflectedGenNoArg(
            forReader: AnyReader<IN>,
            crossinline readObject: IN.(
                    keyType: KxType,
                    onField: IN.(Any?) -> Unit
            ) -> Unit,
            crossinline skipField: IN.() -> Unit
    ): AnySubReaderGenerator<IN> = generator@{
        val kx = it.kxReflectOrNull ?: return@generator null
        val constructor = kx.constructors.find { it.arguments.isEmpty() } ?: return@generator null
        val vars = kx.variables

        reader@{ _ ->
            with(forReader) {
                val instance = constructor.call.invoke(listOf())

                readObject(stringType) { name ->
                    val v = vars[name]
                    if (v == null) {
                        skipField()
                    } else {
                        val value = forReader.read(v.type, this)
                        v.set.untyped.invoke(instance, value)
                    }
                }

                instance
            }
        }
    }

    inline fun <IN> readReflectedGenConstructor(
            forReader: AnyReader<IN>,
            crossinline readObject: IN.(
                    keyType: KxType,
                    onField: IN.(Any?) -> Unit
            ) -> Unit,
            crossinline skipField: IN.() -> Unit
    ): AnySubReaderGenerator<IN> = generator@{
        val kx = it.kxReflectOrNull ?: return@generator null
        val constructor = kx.constructors.firstOrNull()?.takeIf { kx.constructors.size == 1 } ?: return@generator null
        val args = constructor.arguments.associate { it.name to it }
        val vars = kx.variables

        reader@{ _ ->
            with(forReader) {
                val arguments = HashMap<String, Any?>()
                val toPlace = ArrayList<Pair<KxVariable<*, *>, Any?>>()

                //Get all of the data
                readObject(stringType) { name ->
                    args[name]?.let { a ->
                        val value = forReader.read(a.type, this)
                        arguments[name as String] = value
                    } ?: vars[name]?.let { v ->
                        val value = forReader.read(v.type, this)
                        toPlace.add(v to value)
                    } ?: run {
                        skipField()
                    }
                }

                //Compile the list of arguments for the constructor
                val constructorArguments = ArrayList<Any?>()
                for (arg in constructor.arguments) {
                    val value = arguments[arg.name] ?: arg.default!!.invoke(constructorArguments)
                    constructorArguments.add(value)
                }

                val instance = constructor.call.invoke(constructorArguments)
                for ((v, value) in toPlace) {
                    v.set.let {
                        @Suppress("UNCHECKED_CAST")
                        it as (Any, Any?) -> Unit
                    }.invoke(instance, value)
                }
                instance
            }
        }
    }
}