package com.lightningkite.kotlinx.reflection

data class KxVariable<Owner : Any, Type>(
        override val owner: KxClass<Owner>,
        override val name: String,
        override val type: KxType,
        override val get: (Owner) -> Type,
        override val set: (Owner, Type) -> Unit,
        override val annotations: List<KxAnnotation> = listOf()
) : KxField<Owner, Type>