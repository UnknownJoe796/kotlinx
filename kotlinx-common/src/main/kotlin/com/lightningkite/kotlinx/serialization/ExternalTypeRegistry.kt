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

    init {
        register("Any", Any::class)
        register("Unit", Unit::class)
        register("Boolean", Boolean::class)
        register("Byte", Byte::class)
        register("Short", Short::class)
        register("Int", Int::class)
        register("Long", Long::class)
        register("Float", Float::class)
        register("Double", Double::class)
        register("String", String::class)
        register("Char", Char::class)
        register("List", List::class)
        register("Map", Map::class)
    }
}

val KClass<*>.externalName get() = ExternalTypeRegistry[this]