package com.lightningkite.kotlinx.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

actual typealias Runnable = java.lang.Runnable
actual typealias Executor = java.util.concurrent.Executor

actual object Async : ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors(),
        1,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>()
), Executor

actual object UIThread : Executor{
    val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        handler.post(command)
    }
}