package com.lightningkite.kotlinx.locale

import kotlin.test.Test
import kotlin.test.assertEquals

class LocaleTest {
    @Test
    fun compare() {
        val now = KotlinTime.now()
        val nowRendered = KotlinTime.defaultLocale.renderTimeStamp(now)
        println(nowRendered)

        val date = now.date()
        println(date.daysSinceEpoch)
        println(KotlinTime.defaultLocale.renderDate(date))
        val time = now.time()
        println(time.millisecondsSinceMidnight)
        println(KotlinTime.defaultLocale.renderTime(time))

        val redone = TimeStamp(date, time)
        val nowRedoneRerendered = KotlinTime.defaultLocale.renderTimeStamp(redone)
        println(nowRedoneRerendered)

        assertEquals(now, redone)
    }

    @Test
    fun renderDateTest() {
        println("This should render as Jan 1, 1970")
        println(KotlinTime.defaultLocale.renderDate(Date(0)))
    }

    @Test
    fun bringTogether() {
        var stamp = TimeStamp(Date(365), Time(8 * 60 * 60 * 1000))
        println(KotlinTime.defaultLocale.renderTimeStamp(stamp))
    }
}