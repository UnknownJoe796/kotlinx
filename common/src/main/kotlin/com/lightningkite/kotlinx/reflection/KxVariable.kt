package com.lightningkite.kotlinx.reflection

data class KxVariable<Owner, Type>(
        override val name: String,
        override val type: KxType,
        override val get: (Owner) -> Type,
        override val set: (Owner, Type) -> Unit,
        override val annotations: List<KxAnnotation> = listOf()
) : KxField<Owner, Type>