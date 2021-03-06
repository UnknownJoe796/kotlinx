package com.lightningkite.kotlinx.locale



class Locale(
        val language: String,
        val languageVariant: String,
        val getTimeOffset: () -> Long,
        val renderNumber: (value: Number, decimalPositions: Int, maxOtherPositions: Int) -> String,
        val renderDate: (Date) -> String,
        val renderTime: (Time) -> String,
        val renderDateTime: (DateTime) -> String,
        val renderTimeStamp: (TimeStamp) -> String
) {
    companion object {

    }
}
