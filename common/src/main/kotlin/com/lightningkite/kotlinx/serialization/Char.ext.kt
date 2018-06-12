package com.lightningkite.kotlinx.serialization

inline fun Char.isDigit() = this in '0'..'9'
inline fun Char.isLowercase() = this in 'a' .. 'z'
inline fun Char.isUppercase() = this in 'A' .. 'Z'
inline fun Char.isLetter() = isLowercase() || isUppercase()
inline fun Char.isControl() = this < ' '