package com.lightningkite.kotlinx.locale

data class TimeStamp(val millisecondsSinceEpoch: Long) {
    constructor(date: Date, time: Time, offset: Long = Locales.defaultLocale.getTimeOffset()) : this(
            date.daysSinceEpoch * 24L * 60 * 60 * 1000 +
                    time.millisecondsSinceMidnight +
                    offset
    )

    fun date(offset: Long = Locales.defaultLocale.getTimeOffset()): Date = Date(((millisecondsSinceEpoch - offset) / TimeConstants.MS_PER_DAY).toInt())
    fun time(offset: Long = Locales.defaultLocale.getTimeOffset()): Time = Time(((millisecondsSinceEpoch - offset) % TimeConstants.MS_PER_DAY).toInt())
}

operator fun TimeStamp.plus(milliseconds: Int) = TimeStamp(millisecondsSinceEpoch + milliseconds)
operator fun TimeStamp.minus(milliseconds: Int) = TimeStamp(millisecondsSinceEpoch - milliseconds)