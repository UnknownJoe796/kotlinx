package com.lightningkite.kotlinx.math

import kotlin.math.*

/**
 * Various math functions.
 * Created by jivie on 9/28/15.
 */

object Angles{
    val RADIANS_PER_CIRCLE = PI * 2
    val RADIANS_PER_CIRCLE_F = RADIANS_PER_CIRCLE.toFloat()
    val DEGREES_PER_CIRCLE = 360
    val DEGREES_PER_CIRCLE_F = DEGREES_PER_CIRCLE.toFloat()
}

@Suppress("NOTHING_TO_INLINE")
inline fun Float.circlesToRadians() = this * Angles.RADIANS_PER_CIRCLE_F
@Suppress("NOTHING_TO_INLINE")
inline fun Float.circlesToDegrees() = this * Angles.DEGREES_PER_CIRCLE_F
@Suppress("NOTHING_TO_INLINE")
inline fun Float.radiansToDegrees() = radiansToCircles().circlesToRadians()
@Suppress("NOTHING_TO_INLINE")
inline fun Float.radiansToCircles() = this / Angles.RADIANS_PER_CIRCLE_F
@Suppress("NOTHING_TO_INLINE")
inline fun Float.degreesToCircles() = this / Angles.DEGREES_PER_CIRCLE_F
@Suppress("NOTHING_TO_INLINE")
inline fun Float.degreesToRadians() = degreesToCircles().circlesToRadians()

@Suppress("NOTHING_TO_INLINE")
inline fun Double.circlesToRadians() = this * Angles.RADIANS_PER_CIRCLE
@Suppress("NOTHING_TO_INLINE")
inline fun Double.circlesToDegrees() = this * Angles.DEGREES_PER_CIRCLE
@Suppress("NOTHING_TO_INLINE")
inline fun Double.radiansToDegrees() = radiansToCircles().circlesToRadians()
@Suppress("NOTHING_TO_INLINE")
inline fun Double.radiansToCircles() = this / Angles.RADIANS_PER_CIRCLE
@Suppress("NOTHING_TO_INLINE")
inline fun Double.degreesToCircles() = this / Angles.DEGREES_PER_CIRCLE
@Suppress("NOTHING_TO_INLINE")
inline fun Double.degreesToRadians() = degreesToCircles().circlesToRadians()



/**
 * Degrees from this angle (in degrees) to another angle (in degrees).
 */
infix fun Float.degreesTo(to: Float): Float {
    return ((to - this + 180 + 360).rem(360f)) - 180
}

/**
 * Radians from this angle (in radians) to another angle (in radians).
 */
infix fun Float.radiansTo(to: Float): Float {
    return (((to - this + PI * 3).rem(PI * 2)) - PI).toFloat()
}

/**
 * Radians from this angle (in circles) to another angle (in circles).
 */
infix fun Float.circlesTo(to: Float): Float {
    return ((to - this + 1.5f).rem(1.0f)) - .5f
}

/**
 * Degrees from this angle (in degrees) to another angle (in degrees).
 */
infix fun Double.degreesTo(to: Double): Double {
    return ((to - this + 180 + 360).rem(360.0)) - 180
}

/**
 * Radians from this angle (in radians) to another angle (in radians).
 */
infix fun Double.radiansTo(to: Double): Double {
    return ((to - this + PI * 3).rem(PI * 2)) - PI
}

/**
 * Radians from this angle (in circles) to another angle (in circles).
 */
infix fun Double.circlesTo(to: Double): Double {
    return ((to - this + 1.5).rem(1.0)) - .5
}