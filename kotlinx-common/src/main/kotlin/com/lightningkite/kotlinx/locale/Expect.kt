package com.lightningkite.kotlinx.locale


expect val Locale.Companion.defaultLocale: Locale

expect fun TimeStamp.Companion.now(): TimeStamp

expect fun TimeStamp.iso8601(): String
expect fun TimeStamp.Companion.iso8601(string: String): TimeStamp

expect fun Date.iso8601(): String
expect fun Date.Companion.iso8601(string: String): Date

expect fun Time.iso8601(): String
expect fun Time.Companion.iso8601(string: String): Time

expect fun DateTime.iso8601(): String
expect fun DateTime.Companion.iso8601(string: String): DateTime