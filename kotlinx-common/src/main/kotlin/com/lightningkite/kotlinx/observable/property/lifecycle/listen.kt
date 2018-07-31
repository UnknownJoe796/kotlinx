package com.lightningkite.kotlinx.observable.property.lifecycle

import com.lightningkite.kotlinx.observable.property.ObservableProperty

fun <T> ObservableProperty<Boolean>.listen(collection: MutableCollection<T>, item: T) = openCloseBinding(
        onOpen = { collection.add(item) },
        onClose = { collection.remove(item) }
)

fun <T> ObservableProperty<Boolean>.listen(
        property: ObservableProperty<T>,
        item: (T)->Unit
) = openCloseBinding(
        onOpen = { property.add(item) },
        onClose = { property.remove(item) }
)