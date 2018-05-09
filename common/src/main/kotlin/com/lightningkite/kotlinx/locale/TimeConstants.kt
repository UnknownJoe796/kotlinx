package com.lightningkite.kotlinx.locale

object TimeConstants {
    const val MS_PER_SECOND: Long = 1000L
    const val MS_PER_MINUTE: Long = 60L * MS_PER_SECOND
    const val MS_PER_HOUR: Long = 60L * MS_PER_MINUTE
    const val MS_PER_DAY: Long = 24L * MS_PER_HOUR

    const val MS_PER_SECOND_INT: Int = 1000
    const val MS_PER_MINUTE_INT: Int = 60 * MS_PER_SECOND_INT
    const val MS_PER_HOUR_INT: Int = 60 * MS_PER_MINUTE_INT
    const val MS_PER_DAY_INT: Int = 24 * MS_PER_HOUR_INT
}