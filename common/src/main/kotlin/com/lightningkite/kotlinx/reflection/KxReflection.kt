package com.lightningkite.kotlinx.reflection

interface KxClass<Owner> {
    val simpleName: String
    val qualifiedName: String

    val isInterface: Boolean
    val isOpen: Boolean
    val isAbstract: Boolean
    val enumValues: List<Owner>?

    val values: Map<String, KxValue<Owner, *>>
    val variables: Map<String, KxVariable<Owner, *>>
    val functions: List<KxFunction<*>>
    val constructors: List<KxFunction<Owner>>
    val annotations: List<KxAnnotation>
}

data class KxType(
        val base: KxClass<*>,
        val nullable: Boolean = false,
        val typeParameters: List<KxTypeProjection> = listOf(),
        val annotations: List<KxAnnotation> = listOf()
)

interface KxField<Owner, Type> {
    val name: String
    val type: KxType
    val get: (Owner) -> Type
    val set: ((Owner, Type) -> Unit)?
    val annotations: List<KxAnnotation>
}

data class KxValue<Owner, Type>(
        override val name: String,
        override val type: KxType,
        override val get: (Owner) -> Type,
        override val annotations: List<KxAnnotation> = listOf()
) : KxField<Owner, Type> {
    override val set: ((Owner, Type) -> Unit)?
        get() = null
}

data class KxVariable<Owner, Type>(
        override val name: String,
        override val type: KxType,
        override val get: (Owner) -> Type,
        override val set: (Owner, Type) -> Unit,
        override val annotations: List<KxAnnotation> = listOf()
) : KxField<Owner, Type>

data class KxArgument(
        val name: String,
        val type: KxType,
        val annotations: List<KxAnnotation> = listOf()
        //Default?
)

data class KxFunction<Type>(
        val name: String,
        val type: KxType,
        val arguments: List<KxArgument> = listOf(),
        val call: (List<Any?>) -> Type,
        val annotations: List<KxAnnotation> = listOf()
)

data class KxAnnotation(
        val name: String,
        val arguments: List<Any> = listOf()
)

enum class KxVariance {
    INVARIANT,
    IN,
    OUT,
}

data class KxTypeProjection(
        val type: KxType,
        val variance: KxVariance = KxVariance.INVARIANT,
        val isStar: Boolean = false
) {
    companion object {
        val STAR = KxTypeProjection(KxType(Any::class.kxReflect, true), isStar = true)
    }
}