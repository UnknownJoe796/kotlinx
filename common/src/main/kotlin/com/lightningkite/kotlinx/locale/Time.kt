package com.lightningkite.kotlinx.locale


data class Time(val millisecondsSinceMidnight: Int)

val Time.hours:Int
    get() = millisecondsSinceMidnight / TimeConstants.MS_PER_HOUR_INT
val Time.minutes:Int
    get() = millisecondsSinceMidnight / TimeConstants.MS_PER_MINUTE_INT % 60
val Time.seconds:Int
    get() = millisecondsSinceMidnight / TimeConstants.MS_PER_SECOND_INT % 60
val Time.milliseconds:Int
    get() = millisecondsSinceMidnight % 1000

fun Time(
        hours:Int,
        minutes:Int,
        seconds:Int = 0,
        milliseconds:Int = 0
): Time = Time(
        hours * TimeConstants.MS_PER_HOUR_INT +
                minutes * TimeConstants.MS_PER_MINUTE_INT +
                seconds * TimeConstants.MS_PER_SECOND_INT +
                milliseconds
)

operator fun Time.plus(milliseconds: Int) = Time(millisecondsSinceMidnight + milliseconds)
operator fun Time.minus(milliseconds: Int) = Time(millisecondsSinceMidnight - milliseconds)