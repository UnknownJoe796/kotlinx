package com.lightningkite.kotlinx.reflection

data class KxClass<Owner>(
        val simpleName: String,
        val qualifiedName: String,
        val variables: Map<String, KxVariable<Owner, *>>,
        val functions: List<KxFunction<*>>,
        val constructors: List<KxFunction<Owner>>,
        val annotations: List<KxAnnotation>
)

data class KxType<Owner>(
        val base: KxClass<Owner>,
        val typeParameters: List<KxType<*>>,
        val annotations: List<KxAnnotation>
)

data class KxVariable<Owner, Type>(
        val name: String,
        val type: KxType<Owner>,
        val get: (Owner) -> Type,
        val set: ((Owner) -> Type)?,
        val annotations: List<KxAnnotation>
)

data class KxArgument(
        val name: String,
        val type: KxType<*>,
        val annotations: List<KxAnnotation>
        //Default?
)

data class KxFunction<Type>(
        val name: String,
        val type: KxType<Type>,
        val arguments: List<KxArgument>,
        val call: (List<Any>) -> Type,
        val annotations: List<KxAnnotation>
)

data class KxAnnotation(
        val name: String,
        val arguments: Map<String, Any>
)