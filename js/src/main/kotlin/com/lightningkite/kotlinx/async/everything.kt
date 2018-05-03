package com.lightningkite.kotlinx.async

import kotlin.browser.window

actual interface Runnable {
    actual fun run()
}

actual interface Executor {
    actual fun execute(runnable: Runnable)
}

actual object Async : Executor {
    override fun execute(runnable: Runnable) {
//        val swc = window.navigator.serviceWorker.controller
//        if (swc == null) runnable.invoke()
//        else swc.postMessage()
        runnable.run()
        //TODO
    }
}

actual object UIThread : Executor {
    override fun execute(runnable: Runnable) {
        window.setTimeout({runnable.run()}, 0)
    }
}