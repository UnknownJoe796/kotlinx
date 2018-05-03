package com.lightningkite.kotlinx.async

expect interface Runnable{
    fun run():Unit
}
expect interface Executor{
    fun execute(runnable:Runnable)
}
expect object Async : Executor
expect object UIThread : Executor