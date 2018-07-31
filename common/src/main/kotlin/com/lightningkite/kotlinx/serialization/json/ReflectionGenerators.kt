package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.KxVariable
import com.lightningkite.kotlinx.reflection.kxReflectOrNull
import kotlin.reflect.KClass

object ReflectionGenerators {
    fun writerGenerator(forSerializer: JsonSerializer):(type:KClass<*>)->JsonTypeWriter<Any>? = generator@{

        val kx = it.kxReflectOrNull ?: return@generator null
        val vars = kx.variables

        writer@{ typeInfo, value ->
            with(forSerializer){
                writeObject {
                    for(v in vars){
                        writeEntry(v.key){
                            @Suppress("UNCHECKED_CAST")
                            val subValue = v.value.get.let{ it as (Any)->Any? }.invoke(value)
                            if(subValue == null)
                                writeNull()
                            else
                                writeAny(subValue, v.value.type)
                        }
                    }
                }
            }
        }
    }
    fun readerGeneratorNoArg(forSerializer: JsonSerializer):(type:KClass<*>)->JsonTypeReader<Any>? = generator@{

        val kx = it.kxReflectOrNull ?: return@generator null
        val constructor = kx.constructors.find { it.arguments.isEmpty() } ?: return@generator null
        val vars = kx.variables

        reader@{ _ ->
            with(forSerializer) {
                val instance = constructor.call.invoke(listOf())

                beginObject {
                    while(hasNext()){
                        val name = nextName()
                        val v = vars[name]
                        if(v == null){
                            nextAny()
                            continue
                        }
                        val value = readAny<Any>(v.type)
                        v.set.let{
                            @Suppress("UNCHECKED_CAST")
                            it as (Any, Any?)->Unit
                        }.invoke(instance, value)
                    }
                }

                instance
            }
        }
    }
    fun readerGeneratorAnyConstructor(forSerializer: JsonSerializer):(type:KClass<*>)->JsonTypeReader<Any>? = generator@{

        val kx = it.kxReflectOrNull ?: return@generator null
        val constructor = kx.constructors.firstOrNull()?.takeIf { kx.constructors.size == 1 } ?: return@generator null
        val args = constructor.arguments.associate { it.name to it }
        val vars = kx.variables

        reader@{ _ ->
            with(forSerializer) {
                val arguments = HashMap<String, Any?>()
                val toPlace = ArrayList<Pair<KxVariable<*, *>, Any?>>()

                beginObject {
                    while(hasNext()){
                        val name = nextName()
                        args[name]?.let{ a ->
                            toArg

                        } ?: vars[name]?.let{ v ->
                            readAny<Any>(v.type)
                        } ?: run {
                            nextAny()
                        }
                    }
                }

                val instance = constructor.call.invoke(listOf())
                for((v, value) in toPlace){
                    v.set.let{
                        @Suppress("UNCHECKED_CAST")
                        it as (Any, Any?)->Unit
                    }.invoke(instance, value)
                }
                instance
            }
        }
    }
}