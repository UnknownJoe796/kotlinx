package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.*
import com.lightningkite.kotlinx.serialization.ExternalTypeRegistry
import kotlin.test.Test

class GeneralJsonTests {

    val serializer = JsonSerializer()
    init{
        ExternalTypeRegistry
        KxReflection
        TestClass::class.kxReflect = TestClassReflection
        ExternalTypeRegistry.register("TestClass", TestClass::class)
    }

    @Test
    fun basicMapTest() {
        val value = mapOf(
                "test" to 3,
                "value" to 4,
                "another" to 3417
        )
        val typeInfo = KxType(
                base = MapReflection,
                nullable = false,
                typeParameters = listOf(
                        KxTypeProjection(KxType(StringReflection)),
                        KxTypeProjection(KxType(IntReflection))
                )
        )
        val asText = serializer.write(
                type = Map::class,
                additionalTypeInformation = typeInfo,
                value = value
        ).toString()
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = Map::class,
                additionalTypeInformation = typeInfo,
                source = asText
        )
        println(cycled)
    }

    @Test
    fun boxingTest() {
        val value = mapOf(
                "test" to 3,
                "value" to listOf(3, "blah"),
                "another" to "derp"
        )
        val typeInfo = KxType(
                base = MapReflection,
                nullable = false,
                typeParameters = listOf(
                        KxTypeProjection(KxType(StringReflection)),
                        KxTypeProjection(KxType(AnyReflection))
                )
        )
        val asText = serializer.write(
                type = Map::class,
                additionalTypeInformation = typeInfo,
                value = value
        ).toString()
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = Map::class,
                additionalTypeInformation = typeInfo,
                source = asText
        )
        println(cycled)
    }

    @Test
    fun reflectiveTest() {
        val value = TestClass()
        val asText = serializer.write(
                type = TestClass::class,
                value = value
        ).toString()
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = TestClass::class,
                source = asText
        )
        println(cycled)
    }











    data class TestClass(
            var a:Int = 42,
            var b:String = "string"
    )

    object TestClassReflection : KxClass<TestClass> {


        val a = KxVariable<TestClass, Int>(
                name = "a",
                type = KxType(
                        base = Int::class.kxReflect,
                        nullable = false,
                        typeParameters = listOf(),
                        annotations = listOf()
                ),
                get = { owner -> owner.a as Int },
                set = { owner, value -> owner.a = value },
                annotations = listOf()
        )
        val b = KxVariable<TestClass, String>(
                name = "b",
                type = KxType(
                        base = String::class.kxReflect,
                        nullable = false,
                        typeParameters = listOf(),
                        annotations = listOf()
                ),
                get = { owner -> owner.b as String },
                set = { owner, value -> owner.b = value },
                annotations = listOf()
        )

        override val kclass get() = TestClass::class

        override val simpleName: String = "TestClass"
        override val qualifiedName: String = "com.lightningkite.kotlinx.reflection.plugin.test.TestClass"
        override val values: Map<String, KxValue<TestClass, *>> = mapOf()
        override val variables: Map<String, KxVariable<TestClass, *>> = mapOf("a" to a, "b" to b)
        override val functions: List<KxFunction<*>> = listOf()
        override val constructors: List<KxFunction<TestClass>> = listOf(KxFunction<TestClass>(
                name = "",
                type = KxType(
                        base = TestClass::class.kxReflect,
                        nullable = false,
                        typeParameters = listOf(),
                        annotations = listOf()
                ),
                arguments = listOf(KxArgument(
                        name = "a",
                        type = KxType(
                                base = Int::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> 42 }
                ), KxArgument(
                        name = "b",
                        type = KxType(
                                base = String::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(),
                                annotations = listOf()
                        ),
                        annotations = listOf(),
                        default = { previousArguments -> "string" }
                )),
                call = { TestClass(it[0] as Int, it[1] as String) },
                annotations = listOf()
        ))
        override val annotations: List<KxAnnotation> = listOf(KxAnnotation(
                name = "ExternalReflection",
                arguments = listOf()
        ))

        override val isInterface: Boolean get() = false
        override val isOpen: Boolean get() = false
        override val isAbstract: Boolean get() = false
        override val enumValues: List<TestClass>? = null
    }
}