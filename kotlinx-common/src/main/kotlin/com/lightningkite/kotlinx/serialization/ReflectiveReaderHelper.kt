package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.*
import kotlin.reflect.KClass

class ReflectiveReaderHelper<IN>(
        val type: KClass<*>,
        val kx: KxClass<*>,
        val constructor: KxFunction<*>,
        val args: Map<String, KxArgument>,
        val vars: Map<String, KxVariable<*, *>>,
        val readers: Map<String, AnySubReader<IN>>
) {

    inner class InstanceBuilder {
        val arguments = HashMap<String, Any?>()
        val toPlace = ArrayList<Pair<KxVariable<*, *>, Any?>>()

        inline fun place(name: String, input: IN, skipField: () -> Unit) {
            args[name]?.let { a ->
                val value = readers[a.name]!!.invoke(input, a.type)
                arguments[name] = value
            } ?: vars[name]?.let { v ->
                val value = readers[v.name]!!.invoke(input, v.type)
                toPlace.add(v to value)
            } ?: run {
                skipField()
            }
        }

        inline fun build(): Any {
            val instance = constructor.callGiven(arguments)!!
            instance.setUntyped(toPlace)
            return instance
        }
    }

    companion object {
        fun <IN> tryInit(type: KClass<*>, forReader: AnyReader<IN>): ReflectiveReaderHelper<IN>? {

            val kx = type.kxReflectOrNull ?: return null
            val constructor = kx.constructors.firstOrNull() ?: return null
            val args = constructor.arguments.associate { it.name to it }
            val vars = kx.variables
            val readers = vars.values.associate {
                it.name to forReader.reader(it.type.base.kclass)
            } + args.values.associate {
                it.name to forReader.reader(it.type.base.kclass)
            }

            return ReflectiveReaderHelper(
                    type = type,
                    constructor = constructor,
                    kx = kx,
                    args = args,
                    vars = vars,
                    readers = readers
            )
        }
    }
}