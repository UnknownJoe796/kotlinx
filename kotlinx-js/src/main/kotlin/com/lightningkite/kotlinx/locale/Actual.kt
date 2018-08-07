package com.lightningkite.kotlinx.locale

import kotlin.browser.window

private val _defaultLocale = Locale(
        language = window.navigator.language.substringBefore('-'),
        languageVariant = window.navigator.language.substringAfter('-', ""),
        getTimeOffset = { kotlin.js.Date().getTimezoneOffset() * TimeConstants.MS_PER_MINUTE },
        renderNumber = { value, decimalPositions, maxOtherPositions ->
            value.asDynamic()?.toFixed(decimalPositions) as String
        },
        renderDate = {
            kotlin.js.Date(milliseconds = TimeStamp(date = it, time = Time(0)).millisecondsSinceEpoch).toLocaleString()
        },
        renderTime = {
            kotlin.js.Date(milliseconds = it.millisecondsSinceMidnight).toLocaleString()
        },
        renderDateTime = {
            kotlin.js.Date(milliseconds = it.toTimeStamp().millisecondsSinceEpoch).toLocaleString()
        },
        renderTimeStamp = {
            kotlin.js.Date(milliseconds = it.millisecondsSinceEpoch).toLocaleString()
        }
)
actual val Locale.Companion.defaultLocale: Locale get() = _defaultLocale

actual fun TimeStamp.Companion.now(): TimeStamp {
    return TimeStamp(kotlin.js.Date.now().toLong())
}

actual fun TimeStamp.iso8601(): String = kotlin.js.Date(millisecondsSinceEpoch).toISOString()
actual fun TimeStamp.Companion.iso8601(string: String): TimeStamp = TimeStamp(kotlin.js.Date.parse(string).toLong())

actual fun Date.iso8601(): String = kotlin.js.Date(daysSinceEpoch * TimeConstants.MS_PER_DAY + 1).toISOString().dropLast(14)
actual fun Date.Companion.iso8601(string: String): Date = Date(kotlin.js.Date.parse(string + "T00:01:00.000z").toLong().div(TimeConstants.MS_PER_DAY).toInt())

actual fun Time.iso8601(): String = kotlin.js.Date(millisecondsSinceMidnight).toISOString().drop(11).dropLast(1)
actual fun Time.Companion.iso8601(string: String): Time = Time(kotlin.js.Date.parse("1970-01-01T" + string + "z").toLong().rem(TimeConstants.MS_PER_DAY).toInt())

actual fun DateTime.iso8601(): String = this.toTimeStamp(0).iso8601()
actual fun DateTime.Companion.iso8601(string: String): DateTime {
    val stamp = TimeStamp.iso8601(string)
    return DateTime(date = stamp.date(0), time = stamp.time(0))
}
