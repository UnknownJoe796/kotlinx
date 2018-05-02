package com.lightningkite.kotlinx.locale

actual object Timestamps{
    actual fun now(): TimeStamp {
        return TimeStamp(System.currentTimeMillis())
    }
}