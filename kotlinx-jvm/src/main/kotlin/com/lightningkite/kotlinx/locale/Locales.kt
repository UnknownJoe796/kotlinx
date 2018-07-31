package com.lightningkite.kotlinx.locale

import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*

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
                DateFormat.getDateInstance().format(it.toJava().time)
            },
            renderTime = {
                DateFormat.getTimeInstance().format(it.toJava().time)
            },
            renderDateTime = {
                DateFormat.getDateTimeInstance().format(it.toJava().time)
            },
            renderTimeStamp = {
                DateFormat.getDateTimeInstance().format(Date(it.millisecondsSinceEpoch))
            }
    )
}