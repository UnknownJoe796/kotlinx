package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.*
import kotlin.test.Test

class GeneralJsonTests {

    @Test
    fun basicMapTest() {
        println(JsonSerializer().write(
                type = Map::class,
                additionalTypeInformation = KxType(
                        base = MapReflection,
                        nullable = false,
                        typeParameters = listOf(
                                KxTypeProjection(KxType(StringReflection)),
                                KxTypeProjection(KxType(IntReflection))
                        )
                ),
                value = mapOf(
                        "test" to 3,
                        "value" to 4,
                        "another" to 3417
                )
        ).toString())
    }

    @Test
    fun boxingTest() {
        println(JsonSerializer().write(
                type = Map::class,
                additionalTypeInformation = KxType(
                        base = MapReflection,
                        nullable = false,
                        typeParameters = listOf(
                                KxTypeProjection(KxType(StringReflection)),
                                KxTypeProjection(KxType(AnyReflection))
                        )
                ),
                value = mapOf(
                        "test" to 3,
                        "value" to listOf(3, "blah"),
                        "another" to "derp"
                )
        ).toString())
    }
}