package com.lightningkite.kotlinx.locale

import kotlin.js.Date

actual object TimeStamps{
    actual fun now(): TimeStamp {
        return TimeStamp(Date.now().toLong())
    }
}