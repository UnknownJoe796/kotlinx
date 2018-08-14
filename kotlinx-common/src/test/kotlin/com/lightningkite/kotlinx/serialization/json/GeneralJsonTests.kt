package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.*
import com.lightningkite.kotlinx.serialization.CommonSerialization
import com.lightningkite.kotlinx.serialization.ExternalName
import com.lightningkite.kotlinx.server.ConditionOnItem
import com.lightningkite.kotlinx.server.ConditionOnItemReflection
import com.lightningkite.kotlinx.server.ModificationOnItem
import com.lightningkite.kotlinx.server.ModificationOnItemReflection
import com.lightningkite.kotlinx.testing.measurePerformance
import kotlin.test.Test

class GeneralJsonTests {

    val serializer = JsonSerializer()

    init {
        CommonSerialization.ExternalNames.register(TestClassReflection)
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
                type = typeInfo,
                value = value
        )
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = typeInfo,
                from = asText
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
                type = typeInfo,
                value = value
        )
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = typeInfo,
                from = asText
        )
        println(cycled)
    }

    @Test
    fun reflectiveTest() {
        val value = TestClass()
        val asText = serializer.write(
                type = TestClass::class.kxType,
                value = value
        )
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = TestClass::class.kxType,
                from = asText
        )
        println(cycled)
    }

    @Test
    fun reflectiveModifierTest() {
        val value = listOf(ModificationOnItem.Set(TestClassReflection.Fields.a, 23))
        val typeInfo = KxType(
                base = ListReflection,
                nullable = false,
                typeParameters = listOf(
                        KxTypeProjection(
                                KxType(
                                        base = ModificationOnItemReflection,
                                        nullable = false,
                                        typeParameters = listOf(
                                                KxTypeProjection(KxType(TestClassReflection)),
                                                KxTypeProjection.STAR
                                        )
                                )
                        )
                )
        )
        val asText = serializer.write(
                type = typeInfo,
                value = value
        )
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = typeInfo,
                from = asText
        )
        println(cycled)
    }

    @Test
    fun reflectiveConditionTest() {
        val value = listOf(ConditionOnItem.Equal(TestClassReflection.Fields.a, 23))
        val typeInfo = KxType(
                base = ListReflection,
                nullable = false,
                typeParameters = listOf(
                        KxTypeProjection(
                                KxType(
                                        base = ConditionOnItemReflection,
                                        nullable = false,
                                        typeParameters = listOf(
                                                KxTypeProjection(KxType(TestClassReflection)),
                                                KxTypeProjection.STAR
                                        )
                                )
                        )
                )
        )
        val asText = serializer.write(
                type = typeInfo,
                value = value
        )
        println(value)
        println(asText)
        val cycled = serializer.read(
                type = typeInfo,
                from = asText
        )
        println(cycled)
    }

    @Test
    fun reflectivePerformanceTest() {

        val localSerializer = JsonSerializer()

        val value = TestClass()
        val asText = localSerializer.write(
                type = TestClass::class.kxType,
                value = value
        )

        //Test reflective

        val msSerializeReflect = measurePerformance {
            localSerializer.write(
                    type = TestClass::class.kxType,
                    value = value
            )
        }

        println("Serialize reflective performance: $msSerializeReflect milliseconds / item")
        println("Serialize reflective performance: ${1 / (msSerializeReflect / 1000)} items / second")

        val msDeserializeReflect = measurePerformance {
            localSerializer.read(
                    type = TestClass::class.kxType,
                    from = asText
            )
        }

        println("Deserialize reflective performance: $msDeserializeReflect milliseconds / item")
        println("Deserialize reflective performance: ${1 / (msDeserializeReflect / 1000)} items / second")

        //Use fast ser now
        localSerializer.setNullableWriter(TestClass::class) { value, _ ->
            writeObject {
                writeEntry("a") {
                    writeNumber(value.a)
                }
                writeEntry("b") {
                    writeString(value.b)
                }
            }
        }
        localSerializer.setNullableReader(TestClass::class) { _ ->
            var a: Int = 0
            var b: String = ""
            beginObject {
                while (hasNext()) {
                    when (nextName()) {
                        "a" -> a = nextInt()
                        "b" -> b = nextString()
                    }
                }
            }
            TestClass(a, b)
        }

        //Test direct

        val msSerializeDirect = measurePerformance {
            localSerializer.write(
                    type = TestClass::class.kxType,
                    value = value
            )
        }

        println("Serialize direct performance: $msSerializeDirect milliseconds / item")
        println("Serialize direct performance: ${1 / (msSerializeDirect / 1000)} items / second")

        val msDeserializeDirect = measurePerformance {
            localSerializer.read(
                    type = TestClass::class.kxType,
                    from = asText
            )
        }

        println("Deserialize direct performance: $msDeserializeDirect milliseconds / item")
        println("Deserialize direct performance: ${1 / (msDeserializeDirect / 1000)} items / second")


        //Print comparison

        println("Serialization reflection overhead: ${msSerializeReflect / msSerializeDirect}")
        println("Deserialize reflection overhead: ${msDeserializeReflect / msDeserializeDirect}")
    }


    @ExternalReflection
    @ExternalName("TestClass")
    data class TestClass(
            var a: Int = 42,
            var b: String = "string"
    )


    object TestClassReflection : KxClass<TestClass> {
        object Fields {
            val a by lazy {
                KxVariable<TestClass, Int>(
                        owner = TestClassReflection,
                        name = "a",
                        type =
                        KxType(
                                base = Int::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(
                                ),
                                annotations = listOf(
                                )
                        )
                        ,
                        get = { owner -> owner.a as Int },
                        set = { owner, value -> owner.a = value },
                        annotations = listOf(
                        )
                )
            }
            val b by lazy {
                KxVariable<TestClass, String>(
                        owner = TestClassReflection,
                        name = "b",
                        type =
                        KxType(
                                base = String::class.kxReflect,
                                nullable = false,
                                typeParameters = listOf(
                                ),
                                annotations = listOf(
                                )
                        )
                        ,
                        get = { owner -> owner.b as String },
                        set = { owner, value -> owner.b = value },
                        annotations = listOf(
                        )
                )
            }
        }
        override val kclass get() = TestClass::class
        override val implements: List<KxType> by lazy {
            listOf<KxType>(
            )
        }
        override val simpleName: String = "TestClass"
        override val qualifiedName: String = "com.lightningkite.kotlinx.reflection.plugin.test.TestClass"
        override val values: Map<String, KxValue<TestClass, *>> by lazy {
            mapOf<String, KxValue<TestClass, *>>()
        }
        override val variables: Map<String, KxVariable<TestClass, *>> by lazy {
            mapOf<String, KxVariable<TestClass, *>>("a" to Fields.a, "b" to Fields.b)
        }
        override val functions: List<KxFunction<*>> by lazy {
            listOf<KxFunction<*>>(
            )
        }
        override val constructors: List<KxFunction<TestClass>> by lazy {
            listOf<KxFunction<TestClass>>(
                    KxFunction<TestClass>(
                            name = "TestClass",
                            type =
                            KxType(
                                    base = TestClass::class.kxReflect,
                                    nullable = false,
                                    typeParameters = listOf(
                                    ),
                                    annotations = listOf(
                                    )
                            )
                            ,
                            arguments = listOf(
                                    KxArgument(
                                            name = "a",
                                            type =
                                            KxType(
                                                    base = Int::class.kxReflect,
                                                    nullable = false,
                                                    typeParameters = listOf(
                                                    ),
                                                    annotations = listOf(
                                                    )
                                            ),
                                            annotations = listOf(
                                            ),
                                            default = { previousArguments -> 42 }
                                    ),
                                    KxArgument(
                                            name = "b",
                                            type =
                                            KxType(
                                                    base = String::class.kxReflect,
                                                    nullable = false,
                                                    typeParameters = listOf(
                                                    ),
                                                    annotations = listOf(
                                                    )
                                            ),
                                            annotations = listOf(
                                            ),
                                            default = { previousArguments -> "string" }
                                    )
                            ),
                            call = { TestClass(it[0] as Int, it[1] as String) },
                            annotations = listOf(
                            )
                    )
            )
        }
        override val annotations: List<KxAnnotation> = listOf<KxAnnotation>(
        )
        override val modifiers: List<KxClassModifier> = listOf<KxClassModifier>(KxClassModifier.Data)
        override val enumValues: List<TestClass>? = null
    }
}