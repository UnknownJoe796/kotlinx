package com.lightningkite.kotlinx.serialization

import kotlin.reflect.KClass

object ExternalTypeRegistry {
    private val forwards = HashMap<String, KClass<*>>()
    private val backwards = HashMap<KClass<*>, String>()
    operator fun get(type: KClass<*>): String? = backwards[type]
    operator fun get(name: String): KClass<*>? = forwards[name]
    fun register(name: String, type: KClass<*>) {
        forwards[name] = type
        backwards[type] = name
    }
}

val KClass<*>.externalName get() = ExternalTypeRegistry[this]