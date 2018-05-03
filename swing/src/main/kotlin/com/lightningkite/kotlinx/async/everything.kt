package com.lightningkite.kotlinx.async

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

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
    override fun execute(command: Runnable) {
        SwingUtilities.invokeLater(command)
    }
}