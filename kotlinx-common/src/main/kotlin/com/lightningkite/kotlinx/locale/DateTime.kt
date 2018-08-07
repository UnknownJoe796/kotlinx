package com.lightningkite.kotlinx.locale

data class DateTime(val date:Date, val time:Time) {

    companion object {}

    fun toTimeStamp(offset: Long = Locale.defaultLocale.getTimeOffset()) = TimeStamp(date, time, offset)
}