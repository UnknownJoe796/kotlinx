package com.lightningkite.kotlinx.locale

import kotlin.js.Date

actual object Timestamps{
    actual fun now(): TimeStamp {
        return TimeStamp(Date.now().toLong())
    }
}