package com.lightningkite.kotlinx.locale

import com.lightningkite.kotlinx.locale.jackson.JacksonStdDateFormat
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

private val javaLocale = java.util.Locale.getDefault()
private val javaTimeZone = java.util.TimeZone.getDefault()

private val _defaultLocale = Locale(
        language = javaLocale.language.substringBefore('-'),
        languageVariant = javaLocale.language.substringAfter('-', ""),
        getTimeOffset = {
            -javaTimeZone.getOffset(System.currentTimeMillis()).toLong()
        },
        renderNumber = { value, decimalPositions, maxOtherPositions ->
            DecimalFormat("#".repeat(maxOtherPositions) + "." + "#".repeat(decimalPositions)).format(value)
        },
        renderDate = {
            DateFormat.getDateInstance().format(it.toJava().time)
        },
        renderTime = {
            DateFormat.getTimeInstance().format(it.toJava().time)
        },
        renderDateTime = {
            DateFormat.getDateTimeInstance().format(it.toJava().time)
        },
        renderTimeStamp = {
            DateFormat.getDateTimeInstance().format(java.util.Date(it.millisecondsSinceEpoch))
        }
)
actual val Locale.Companion.defaultLocale: Locale get() = _defaultLocale

actual fun TimeStamp.Companion.now(): TimeStamp {
    return TimeStamp(System.currentTimeMillis())
}

private val parseIsoDateFormat = JacksonStdDateFormat()
private fun writeIsoDateFormat() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")

actual fun TimeStamp.iso8601(): String = writeIsoDateFormat().format(java.util.Date(millisecondsSinceEpoch))
actual fun TimeStamp.Companion.iso8601(string: String): TimeStamp = TimeStamp(parseIsoDateFormat.parse(string).time)

actual fun Date.iso8601(): String = writeIsoDateFormat().format(java.util.Date(daysSinceEpoch * TimeConstants.MS_PER_DAY + 1)).dropLast(14)
actual fun Date.Companion.iso8601(string: String): Date = Date(parseIsoDateFormat.parse(string + "T00:01:00.000z").time.div(TimeConstants.MS_PER_DAY).toInt())

actual fun Time.iso8601(): String = writeIsoDateFormat().format(java.util.Date(millisecondsSinceMidnight.toLong())).drop(11).dropLast(1)
actual fun Time.Companion.iso8601(string: String): Time = Time(parseIsoDateFormat.parse("1970-01-01T" + string + "z").time.rem(TimeConstants.MS_PER_DAY).toInt())

actual fun DateTime.iso8601(): String = this.toTimeStamp(0).iso8601()
actual fun DateTime.Companion.iso8601(string: String): DateTime {
    val stamp = TimeStamp.iso8601(string)
    return DateTime(date = stamp.date(0), time = stamp.time(0))
}
