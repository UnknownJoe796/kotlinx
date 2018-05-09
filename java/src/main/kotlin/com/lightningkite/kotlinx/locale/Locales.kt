package com.lightningkite.kotlinx.locale

import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*
import java.util.Date

actual object Locales {

    private val javaLocale = java.util.Locale.getDefault()
    private val javaTimeZone = java.util.TimeZone.getDefault()
    actual var defaultLocale: Locale = Locale(
            language = javaLocale.language.substringBefore('-'),
            languageVariant = javaLocale.language.substringAfter('-', ""),
            getTimeOffset = {
                -javaTimeZone.getOffset(System.currentTimeMillis()).toLong()
            },
            renderNumber = { value, decimalPositions, maxOtherPositions ->
                DecimalFormat("#".repeat(maxOtherPositions) + "." + "#".repeat(decimalPositions)).format(value)
            },
            renderDate = {
                val date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, 1970)
                    set(Calendar.HOUR_OF_DAY, 6)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.DAY_OF_YEAR, 1)
                    add(Calendar.DAY_OF_YEAR, it.daysSinceEpoch)
                }.time
                DateFormat.getDateInstance().format(date)
            },
            renderTime = {
                DateFormat.getTimeInstance().format(Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    add(Calendar.MILLISECOND, it.millisecondsSinceMidnight)
                }.time)
            },
            renderTimeStamp = {
                DateFormat.getDateTimeInstance().format(Date(it.millisecondsSinceEpoch))
            }
    )
}