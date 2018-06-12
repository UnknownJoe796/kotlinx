package com.lightningkite.kotlinx.serialization

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

class JsonSerializer : Serializer<String> {
    var writeType: (KType) -> String = {
        ((it.classifier as? KClass<*>)
                ?.let { ExternalTypeRegistry[it] } ?: "Unit") +
                if (it.isMarkedNullable) "?" else ""
    }
    var readType: (String) -> KType = {
        object : KType {
            override val arguments: List<KTypeProjection>
                get() = listOf()
            override val classifier: KClassifier? = ExternalTypeRegistry[it.trimEnd('?')]
            override val isMarkedNullable: Boolean = it.last() == '?'
        }
    }

    override fun read(value: String): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(value: Any?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun write(value: Any?, to: Appendable) {}
    fun read(from: StringReader):Any? = TODO()

    interface TypeSer<T>{
        fun write(value: T, to: Appendable)
        fun read(from: StringReader):T
    }

    object IntTypeSer: TypeSer<Int>{
        override fun write(value: Int, to: Appendable){
            to.append(value.toString())
        }

        override fun read(from: StringReader): Int {
            return from.readWhile { it in '0' .. '9' }.toInt()
        }
    }
}