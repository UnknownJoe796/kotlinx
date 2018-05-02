package com.lightningkite.kotlinx.locale


data class Date(val daysSinceEpoch: Int)
data class Time(val millisecondsSinceMidnight: Int)
data class TimeStamp(val millisecondsSinceEpoch: Long) {
    constructor(date: Date, time: Time, offset: Long = Locales.defaultLocale.getTimeOffset()) : this(
            leap(
                    date.daysSinceEpoch * 24L * 60 * 60 * 1000 +
                            time.millisecondsSinceMidnight +
                            LeapSeconds.first { date.daysSinceEpoch > it.first }.second +
                            offset
            )
    )

    fun date(offset: Long = Locales.defaultLocale.getTimeOffset()): Date = Date((unleap(millisecondsSinceEpoch - offset) / TimeConstants.MS_PER_DAY).toInt())
    fun time(offset: Long = Locales.defaultLocale.getTimeOffset()): Time = Time((unleap(millisecondsSinceEpoch - offset) % TimeConstants.MS_PER_DAY).toInt())
}

object TimeConstants {
    const val MS_PER_MINUTE: Long = 60L * 1000
    const val MS_PER_HOUR: Long = 60L * MS_PER_MINUTE
    const val MS_PER_DAY: Long = 24L * MS_PER_HOUR
}