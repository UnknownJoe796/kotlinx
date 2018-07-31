package com.lightningkite.kotlinx.observable.property.lifecycle

import com.lightningkite.kotlinx.observable.property.ObservableProperty


inline fun ObservableProperty<Boolean>.openCloseBinding(
        crossinline onOpen:()->Unit,
        crossinline onClose:()->Unit
): (Boolean) -> Unit {
    var state:Boolean = false
    val lambda = { newState:Boolean ->
        if(state != newState){
            if(newState){
                onOpen()
            } else {
                onClose()
            }
            state = newState
        }
    }
    lambda(value)
    add(lambda)
    return lambda
}