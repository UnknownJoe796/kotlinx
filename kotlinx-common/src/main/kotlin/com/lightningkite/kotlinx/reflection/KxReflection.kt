package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

object KxReflection{
    val map = HashMap<KClass<*>, KxClass<*>>()
    fun register(reflection: KxClass<*>){
        map[reflection.kclass] = reflection
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
