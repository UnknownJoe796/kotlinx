package com.lightningkite.kotlinx.reflection

import com.lightningkite.kotlinx.server.ServerFunction


object AnyReflection : EmptyReflection<Any>(Any::class, "kotlin.Any")
object UnitReflection : EmptyReflection<Unit>(Unit::class, "kotlin.Unit")
object BooleanReflection : EmptyReflection<Boolean>(Boolean::class, "kotlin.Boolean"){
    override val enumValues: List<Boolean> get() = listOf(false, true)
}
object ByteReflection : EmptyReflection<Byte>(Byte::class, "kotlin.Byte")
object ShortReflection : EmptyReflection<Short>(Short::class, "kotlin.Short")
object IntReflection : EmptyReflection<Int>(Int::class, "kotlin.Int")
object LongReflection : EmptyReflection<Long>(Long::class, "kotlin.Long")
object FloatReflection : EmptyReflection<Float>(Float::class, "kotlin.Float")
object DoubleReflection : EmptyReflection<Double>(Double::class, "kotlin.Double")
object CharReflection : EmptyReflection<Char>(Char::class, "kotlin.Char")
object StringReflection : EmptyReflection<String>(String::class, "kotlin.String")
object ListReflection : EmptyReflection<List<*>>(List::class, "kotlin.List")
object MapReflection : EmptyReflection<Map<*, *>>(Map::class, "kotlin.Map")
object ServerFunctionReflection : EmptyReflection<ServerFunction<*>>(ServerFunction::class, "com.lightningkite.kotlinx.server.ServerFunction") {
    override val modifiers: List<KxClassModifier> = listOf(KxClassModifier.Interface)
}
