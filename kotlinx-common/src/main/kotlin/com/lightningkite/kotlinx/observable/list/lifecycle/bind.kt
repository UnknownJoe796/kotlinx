package com.lightningkite.kotlinx.observable.list.lifecycle

import com.lightningkite.kotlinx.observable.list.ObservableList
import com.lightningkite.kotlinx.observable.list.ObservableListListenerSet
import com.lightningkite.kotlinx.observable.list.addListenerSet
import com.lightningkite.kotlinx.observable.list.removeListenerSet
import com.lightningkite.kotlinx.observable.property.ObservableProperty
import com.lightningkite.kotlinx.observable.property.lifecycle.bind
import com.lightningkite.kotlinx.observable.property.lifecycle.openCloseBinding


fun <T> ObservableProperty<Boolean>.bind(observable: ObservableList<T>, listener: (ObservableList<T>) -> Unit) {
    bind(observable.onUpdate, listener)
}

fun <T> ObservableProperty<Boolean>.bind(observable: ObservableList<T>, listenerSet: ObservableListListenerSet<T>) {
    this.openCloseBinding(
            onOpen = { observable.addListenerSet(listenerSet) },
            onClose = { observable.removeListenerSet(listenerSet) }
    )
}