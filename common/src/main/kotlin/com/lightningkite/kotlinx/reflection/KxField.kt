package com.lightningkite.kotlinx.reflection

interface KxField<Owner, Type> {
    val name: String
    val type: KxType
    val get: (Owner) -> Type
    val set: ((Owner, Type) -> Unit)?
    val annotations: List<KxAnnotation>
}