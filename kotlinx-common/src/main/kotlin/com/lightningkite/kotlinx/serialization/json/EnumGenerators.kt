package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.kxReflectOrNull
import kotlin.reflect.KClass

object EnumGenerators {
    val reader: (KClass<*>) -> JsonTypeReader<Any>? = generator@{ type ->
        val mapped = type.kxReflectOrNull?.enumValues?.associate { (it as Enum<*>).name.toLowerCase() to it } ?: return@generator null

        return@generator { _ ->
            val name = nextString().toLowerCase()
            mapped[name] ?: throw IllegalArgumentException("Enum value $name not recognized.")
        }
    }
    val writer: (KClass<*>)->JsonTypeWriter<Any>? = generator@{ type ->
        if(type.kxReflectOrNull?.enumValues == null) return@generator null
        return@generator { _, value ->
            (value as Enum<*>).name
        }
    }
}