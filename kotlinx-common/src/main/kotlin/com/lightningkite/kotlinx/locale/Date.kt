package com.lightningkite.kotlinx.locale

data class Date(val daysSinceEpoch: Int) {
    companion object {}

    enum class DayOfWeek{
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }
}

val Date.dayOfWeek:Date.DayOfWeek get() = Date.DayOfWeek.values()[(daysSinceEpoch + 4) % 7]