package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.*
import kotlin.reflect.KClass

object CommonSerialization {
    val defaultReaders = HashMap<KClass<*>, (AnyReader<Any?>) -> AnySubReader<Any?>>()
    val defaultWriters = HashMap<KClass<*>, (AnyWriter<Any?, Any?>) -> AnySubWriter<Any?, Any?>>()
    val defaultReaderGenerators = ArrayList<Pair<Float, (AnyReader<Any?>, type: KClass<*>) -> AnySubReader<Any?>?>>()
    val defaultWriterGenerators = ArrayList<Pair<Float, (AnyWriter<Any?, Any?>, type: KClass<*>) -> AnySubWriter<Any?, Any?>?>>()

    @Suppress("UNCHECKED_CAST")
    fun <IN> getDirectSubReader(
            anyReader: AnyReader<IN>,
            type: KClass<*>
    ): AnySubReader<IN>? = defaultReaders[type]?.invoke(anyReader as AnyReader<Any?>)

    @Suppress("UNCHECKED_CAST")
    fun <OUT, RESULT> getDirectSubWriter(
            anyWriter: AnyWriter<OUT, RESULT>,
            type: KClass<*>
    ): AnySubWriter<OUT, RESULT>? = defaultWriters[type]?.invoke(anyWriter as AnyWriter<Any?, Any?>) as? AnySubWriter<OUT, RESULT>

    init {
        defaultReaders[KxVariable::class] = gen@{ givenReader ->
            val stringReader = givenReader.reader(String::class)
            val stringType = String::class.kxType
            return@gen reader@{ type ->
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                if (varOnClass?.kclass?.serializePolymorphic == true) {
                    val propertyName = stringReader.invoke(this, stringType) as String
                    varOnClass.variables[propertyName]!!
                } else {
                    val fullName = stringReader.invoke(this, stringType) as String
                    val className = fullName.substringBeforeLast('.')
                    val propertyName = fullName.substringAfterLast('.')
                    CommonSerialization.ExternalNames[className]!!.variables[propertyName]!!
                }
            }
        }
        defaultWriters[KxVariable::class] = gen@{ givenWriter ->
            val stringWriter = givenWriter.writer(String::class)
            val stringType = String::class.kxType
            return@gen writer@{ value, type ->
                val variable = value as KxVariable<*, *>
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                val reference = if (varOnClass?.kclass?.serializePolymorphic == true) {
                    variable.name
                } else {
                    CommonSerialization.ExternalNames[variable.owner] + "." + variable.name
                }
                stringWriter.invoke(this, reference, stringType)
            }
        }
        defaultReaders[KxValue::class] = gen@{ givenReader ->
            val stringReader = givenReader.reader(String::class)
            val stringType = String::class.kxType
            return@gen reader@{ type ->
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                if (varOnClass?.kclass?.serializePolymorphic == true) {
                    val propertyName = stringReader.invoke(this, stringType) as String
                    varOnClass.values[propertyName]!!
                } else {
                    val fullName = stringReader.invoke(this, stringType) as String
                    val className = fullName.substringBeforeLast('.')
                    val propertyName = fullName.substringAfterLast('.')
                    CommonSerialization.ExternalNames[className]!!.values[propertyName]!!
                }
            }
        }
        defaultWriters[KxValue::class] = gen@{ givenWriter ->
            val stringWriter = givenWriter.writer(String::class)
            val stringType = String::class.kxType
            return@gen writer@{ value, type ->
                val variable = value as KxValue<*, *>
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                val reference = if (varOnClass?.kclass?.serializePolymorphic == true) {
                    variable.name
                } else {
                    CommonSerialization.ExternalNames[variable.owner] + "." + variable.name
                }
                stringWriter.invoke(this, reference, stringType)
            }
        }
        defaultReaders[KxField::class] = gen@{ givenReader ->
            val stringReader = givenReader.reader(String::class)
            val stringType = String::class.kxType
            return@gen reader@{ type ->
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                if (varOnClass?.kclass?.serializePolymorphic == true) {
                    val propertyName = stringReader.invoke(this, stringType) as String
                    (varOnClass.variables[propertyName] ?: varOnClass.values[propertyName])!!
                } else {
                    val fullName = stringReader.invoke(this, stringType) as String
                    val className = fullName.substringBeforeLast('.')
                    val propertyName = fullName.substringAfterLast('.')
                    val varOnClassFound = CommonSerialization.ExternalNames[className]!!
                    (varOnClassFound.variables[propertyName] ?: varOnClassFound.values[propertyName])!!
                }
            }
        }
        defaultWriters[KxField::class] = gen@{ givenWriter ->
            val stringWriter = givenWriter.writer(String::class)
            val stringType = String::class.kxType
            return@gen writer@{ value, type ->
                val variable = value as KxField<*, *>
                val varOnClass = type.typeParameters.getOrNull(0)?.type?.base
                val reference = if (varOnClass?.kclass?.serializePolymorphic == true) {
                    variable.name
                } else {
                    CommonSerialization.ExternalNames[variable.owner] + "." + variable.name
                }
                stringWriter.invoke(this, reference, stringType)
            }
        }
        defaultReaders[KxClass::class] = gen@{ givenReader ->
            val stringReader = givenReader.reader(String::class)
            val stringType = String::class.kxType
            return@gen reader@{ type ->
                val name = stringReader.invoke(this, stringType) as String
                CommonSerialization.ExternalNames[name]!!
            }
        }
        defaultWriters[KxClass::class] = gen@{ givenWriter ->
            val stringWriter = givenWriter.writer(String::class)
            val stringType = String::class.kxType
            return@gen writer@{ value, type ->
                val kclass = value as KxClass<*>
                stringWriter.invoke(this, CommonSerialization.ExternalNames[kclass]!!, stringType)
            }
        }
    }

    object ExternalNames {

        private val forwards = HashMap<String, KxClass<*>>()
        private val backwards = HashMap<KxClass<*>, String>()
        operator fun get(type: KxClass<*>): String? = backwards[type]
        operator fun get(name: String): KxClass<*>? = forwards[name]
        fun register(type: KxClass<*>, name: String = type.simpleName) {
            KxReflection.register(type)
            forwards[name] = type
            backwards[type] = name
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
            register(StringReflection)
            register(CharReflection)
            register(ListReflection)
            register(MapReflection)
            register(ServerFunctionReflection)
            com.lightningkite.kotlinx.server.reflections.forEach { register(it) }
        }
    }
}