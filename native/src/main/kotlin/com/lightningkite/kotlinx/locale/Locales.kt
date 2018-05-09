package com.lightningkite.kotlinx.locale

import kotlin.browser.window
import kotlin.js.Date

//actual object Locales {
//    actual var defaultLocale: Locale = Locale(
//            language = window.navigator.language.substringBefore('-'),
//            languageVariant = window.navigator.language.substringAfter('-', ""),
//            getTimeOffset = { Date().getTimezoneOffset() * TimeConstants.MS_PER_MINUTE },
//            renderNumber = { value, decimalPositions, maxOtherPositions ->
//                value.asDynamic()?.toFixed(decimalPositions) as String
//            },
//            renderDate = {
//                Date(milliseconds = TimeStamp(date = it, time = Time(0)).millisecondsSinceEpoch).toLocaleString()
//            },
//            renderTime = {
//                Date(milliseconds = it.millisecondsSinceMidnight).toLocaleString()
//            },
//            renderTimeStamp = {
//                Date(milliseconds = it.millisecondsSinceEpoch).toLocaleString()
//            }
//    )
//}