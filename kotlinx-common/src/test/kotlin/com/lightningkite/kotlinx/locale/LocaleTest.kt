package com.lightningkite.kotlinx.locale

import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleTest {
    @Test
    fun compare() {
        val now = TimeStamp.now()
        val nowRendered = Locale.defaultLocale.renderTimeStamp(now)
        println(nowRendered)

        val date = now.date()
        println(date.daysSinceEpoch)
        println(Locale.defaultLocale.renderDate(date))
        val time = now.time()
        println(time.millisecondsSinceMidnight)
        println(Locale.defaultLocale.renderTime(time))

        val redone = TimeStamp(date, time)
        val nowRedoneRerendered = Locale.defaultLocale.renderTimeStamp(redone)
        println(nowRedoneRerendered)

        assertEquals(now, redone)
    }

    @Test
    fun renderDateTest() {
        println("This should render as Jan 1, 1970")
        println(Locale.defaultLocale.renderDate(Date(0)))
    }

    @Test
    fun bringTogether() {
        val stamp = TimeStamp(Date(365), Time(8 * 60 * 60 * 1000))
        println(Locale.defaultLocale.renderTimeStamp(stamp))
    }
}