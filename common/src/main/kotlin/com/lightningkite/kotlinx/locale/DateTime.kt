package com.lightningkite.kotlinx.locale

data class DateTime(val date:Date, val time:Time) {
    fun toTimeStamp(offset: Long = Locales.defaultLocale.getTimeOffset()) = TimeStamp(date, time, offset)
}