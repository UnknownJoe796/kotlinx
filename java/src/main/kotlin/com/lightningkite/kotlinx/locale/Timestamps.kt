package com.lightningkite.kotlinx.locale

actual object TimeStamps{
    actual fun now(): TimeStamp {
        return TimeStamp(System.currentTimeMillis())
    }
}