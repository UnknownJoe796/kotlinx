package com.lightningkite.kotlinx.math.random

import kotlin.math.abs

private var UniqueSeedNumber:Long = 8682522807148012L
private fun uniqueSeed():Long{
    UniqueSeedNumber *= 181783497276652981L
    return UniqueSeedNumber
}

class Random(var seed: Long = uniqueSeed()) {
    companion object {
        private const val multiplier = 0x5DEECE66DL
        private const val addend = 0xBL
        private const val mask = (1L shl 48) - 1
        private const val DOUBLE_UNIT = 1.0 / (1L shl 53)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun nextBits(bits: Int): Int {
        seed = seed * multiplier + addend and mask
        return seed.ushr(48 - bits).toInt()
    }

    fun nextInt(): Int {
        seed = seed * multiplier + addend and mask
        return seed.ushr(16).toInt()
    }

    fun nextDouble(): Double = ((nextBits(26).toLong() shl 27) + nextBits(27)) * DOUBLE_UNIT
    fun nextLong(): Long = (nextInt().toLong() shl 32) or (nextInt().toLong())
    fun nextFloat(): Float = nextBits(24) / (1 shl 24).toFloat()
    fun nextBoolean(): Boolean = nextBits(1) != 0

    fun nextInt(max:Int):Int = abs(nextInt()) % max
}

val MainFastRandom = Random()