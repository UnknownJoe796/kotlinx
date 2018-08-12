package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

object KxReflection {
    private val kclassToKxClass = HashMap<KClass<*>, KxClass<*>>()

    operator fun <T : Any> get(kclass: KClass<T>): KxClass<T> {
        @Suppress("UNCHECKED_CAST")
        return kclassToKxClass.getOrPut(kclass) {
            EmptyReflection(kclass, "")
        } as KxClass<T>
    }

    fun register(
            reflection: KxClass<*>
    ) {
        kclassToKxClass[reflection.kclass] = reflection
    }

    init {
        register(AnyReflection)
        register(UnitReflection)
        register(BooleanReflection)
        register(ByteReflection)
        register(ShortReflection)
        register(IntReflection)
        register(LongReflection)
        register(FloatReflection)
        register(DoubleReflection)
        register(CharReflection)
        register(StringReflection)
        register(ListReflection)
        register(MapReflection)
        register(ServerFunctionReflection)
    }
}