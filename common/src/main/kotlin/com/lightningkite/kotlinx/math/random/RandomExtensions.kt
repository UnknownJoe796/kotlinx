package com.lightningkite.kotlinx.math.random

fun IntRange.random(random: Random = MainFastRandom) = random.nextInt(endInclusive - start + 1) + start
fun ClosedFloatingPointRange<Float>.random(random: Random = MainFastRandom) = random.nextFloat().times(endInclusive - start) + start
fun ClosedFloatingPointRange<Double>.random(random: Random = MainFastRandom) = random.nextDouble().times(endInclusive - start) + start
fun CharRange.random(random: Random = MainFastRandom) = start + (random.nextInt(endInclusive - start + 1))

fun <T> List<T>.random(random: Random = MainFastRandom) = this[indices.random(random)]
fun <T> Array<T>.random(random: Random = MainFastRandom) = this[indices.random(random)]