package com.lightningkite.kotlinx.testing

import com.lightningkite.kotlinx.locale.TimeStamp
import com.lightningkite.kotlinx.locale.now

inline fun measurePerformance(
        iterations: Int = 1000000,
        warmUpIterations: Int = 1000,
        action: () -> Unit
): Double {
    repeat(warmUpIterations) { action() }
    val start = TimeStamp.now().millisecondsSinceEpoch
    repeat(iterations) { action() }
    val delta = TimeStamp.now().millisecondsSinceEpoch - start
    return delta.toDouble() / iterations
}